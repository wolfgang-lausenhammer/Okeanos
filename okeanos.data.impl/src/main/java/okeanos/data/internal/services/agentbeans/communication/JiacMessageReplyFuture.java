package okeanos.data.internal.services.agentbeans.communication;

import static okeanos.data.services.agentbeans.CommunicationServiceAgentBean.Header.COMMUNICATION_CORRELATION_ID;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okeanos.data.services.agentbeans.CommunicationServiceAgentBean.Header;

import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dailab.jiactng.agentcore.AbstractAgentProtectedMethodPublisher;
import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.knowledge.IMemory;

public class JiacMessageReplyFuture implements Future<IJiacMessage>,
		SpaceObserver<IFact> {
	private static enum State {
		CANCELLED, DONE, WAITING
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(JiacMessageReplyFuture.class);

	private static final long serialVersionUID = 1012633574132382647L;
	private SpaceObserver<IFact> callback;
	private IMemory memory;
	private final BlockingQueue<IJiacMessage> reply = new ArrayBlockingQueue<>(
			10);

	private IAgent sender;

	private volatile State state = State.WAITING;

	public JiacMessageReplyFuture(final IAgent sender,
			final SpaceObserver<IFact> callback, final IFact factToListenFor) {
		this(sender, null, callback, factToListenFor);
	}

	public JiacMessageReplyFuture(final IAgent sender, final String messageId,
			final IFact factToListenFor) {
		this(sender, messageId, null, factToListenFor);
	}

	private JiacMessageReplyFuture(final IAgent sender, final String messageId,
			final SpaceObserver<IFact> callback, final IFact factToListenFor) {
		this.sender = sender;
		if (callback == null) {
			this.callback = this;
		} else {
			this.callback = callback;
		}

		// initialization
		IMemory memory = AbstractAgentProtectedMethodPublisher
				.getMemory((Agent) sender);
		this.memory = memory;

		JiacMessage listenerTemplate = new JiacMessage();
		if (messageId != null) {
			listenerTemplate.setHeader(COMMUNICATION_CORRELATION_ID, messageId);
		}
		if (factToListenFor != null) {
			listenerTemplate.setPayload(factToListenFor);
		}
		LOG.trace("Adding listener [callback={}] for [message={}]",
				this.callback, listenerTemplate);

		// register listener
		memory.attach(this.callback, listenerTemplate);
	}

	@Override
	public boolean cancel(final boolean mayInterruptIfRunning) {
		state = State.CANCELLED;
		cleanUp();
		return true;
	}

	private void cleanUp() {
		LOG.trace("Cleaning up, removing event handler from memory");
		memory.detach(callback);
	}

	@Override
	public IJiacMessage get() throws InterruptedException, ExecutionException {
		LOG.trace("Trying to get item from Future");
		IJiacMessage msg = reply.take();

		return msg;
	}

	@Override
	public IJiacMessage get(final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		LOG.trace("Trying to get item from Future, waiting maximal {}ms",
				unit.toMillis(timeout));
		final IJiacMessage replyOrNull = reply.poll(timeout, unit);
		if (replyOrNull == null) {
			throw new TimeoutException();
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
	public void notify(final SpaceEvent<? extends IFact> event) {
		if (event instanceof WriteCallEvent<?>) {
			WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;
			try {
				LOG.trace("Future received message to provide to receiver.");
				reply.put(wce.getObject());
				state = State.DONE;

				cleanUp();
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
