package okeanos.data.internal.services.communication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.AbstractAgentBeanProtectedMethodPublisher;
import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.knowledge.IMemory;

public class JiacMessageReplyFuture implements Future<IJiacMessage>,
		SpaceObserver<IFact> {
	private static enum State {
		CANCELLED, DONE, WAITING
	}

	private static final Logger log = LoggerFactory
			.getLogger(JiacMessageReplyFuture.class);

	private static final long serialVersionUID = 1012633574132382647L;
	private SpaceObserver<IFact> callback;
	private IMemory memory;
	private final BlockingQueue<IJiacMessage> reply = new ArrayBlockingQueue<>(
			10);

	private IAgentBean sender;

	private volatile State state = State.WAITING;

	public JiacMessageReplyFuture(IAgentBean sender,
			SpaceObserver<IFact> callback, IFact factToListenFor) {
		this(sender, null, callback, factToListenFor);
	}

	public JiacMessageReplyFuture(IAgentBean sender, String messageId,
			IFact factToListenFor) {
		this(sender, messageId, null, factToListenFor);
	}

	private JiacMessageReplyFuture(IAgentBean sender, String messageId,
			SpaceObserver<IFact> callback, IFact factToListenFor) {
		this.sender = sender;
		if (callback == null) {
			this.callback = this;
		} else {
			this.callback = callback;
		}

		// initialization
		IMemory memory = AbstractAgentBeanProtectedMethodPublisher
				.getMemory(((AbstractAgentBean) sender));
		this.memory = memory;

		JiacMessage listenerTemplate = new JiacMessage();
		if (messageId != null) {
			listenerTemplate.setHeader("OkeanosCommunicationCorrelationId",
					messageId);
		}
		if (factToListenFor != null) {
			listenerTemplate.setPayload(factToListenFor);
		}
		log.trace("Adding listener [callback={}] for [message={}]",
				this.callback, listenerTemplate);

		// register listener
		memory.attach(this.callback, listenerTemplate);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		state = State.CANCELLED;
		cleanUp();
		return true;
	}

	private void cleanUp() {
		log.trace("Cleaning up, removing event handler from memory");
		memory.detach(callback);
	}

	@Override
	public IJiacMessage get() throws InterruptedException, ExecutionException {
		log.trace("Trying to get item from Future");
		IJiacMessage msg = reply.take();

		if (callback != this) {
			state = State.WAITING;
		}

		return msg;
	}

	@Override
	public IJiacMessage get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		log.trace("Trying to get item from Future, waiting maximal {}ms",
				unit.toMillis(timeout));
		final IJiacMessage replyOrNull = reply.poll(timeout, unit);
		if (replyOrNull == null) {
			throw new TimeoutException();
		}

		if (callback != this) {
			state = State.WAITING;
		}

		return replyOrNull;
	}

	@Override
	public boolean isCancelled() {
		return state == State.CANCELLED;
	}

	@Override
	public boolean isDone() {
		return state == State.DONE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notify(SpaceEvent<? extends IFact> event) {
		if (event instanceof WriteCallEvent<?>) {
			WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;
			try {
				log.trace("Future received message to provide to receiver.");
				reply.put(wce.getObject());
				state = State.DONE;

				if (callback == this) {
					cleanUp();
				}
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public String toString() {
		return String.format("JiacMessageReplyFuture [sender=%s, state=%s]",
				sender, state);
	}
}
