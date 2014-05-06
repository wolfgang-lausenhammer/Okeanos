package okeanos.data.internal.services.agentbeans.communication;

import static okeanos.data.services.agentbeans.CommunicationServiceAgentBean.Header.COMMUNICATION_CORRELATION_ID;

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

import de.dailab.jiactng.agentcore.AbstractAgentProtectedMethodPublisher;
import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.knowledge.IMemory;

/**
 * Represents a future that waits for the result to a message. <br>
 * Warning: There is no timeout for waiting for a message. So if a lot of
 * messages are sent that do not get an answer, the listeners will stay there
 * and possibly use up a lot of memory.
 * 
 * @author Wolfgang Lausenhammer
 */
public class JiacMessageReplyFuture implements Future<IJiacMessage>,
		SpaceObserver<IFact> {

	/**
	 * The Enum State.
	 */
	private static enum State {

		/** When the future was cancelled before. */
		CANCELLED,
		/** When the future got its result. */
		DONE,
		/** When the future is waiting for the result. */
		WAITING
	}

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(JiacMessageReplyFuture.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1012633574132382647L;

	/** The callback. */
	private SpaceObserver<IFact> callback;

	/** The memory. */
	private IMemory memory;

	/**
	 * A blocking queue that buffers received messages until they are fetched
	 * from the user.
	 */
	private final BlockingQueue<IJiacMessage> reply = new ArrayBlockingQueue<>(
			10);

	/** The sender. */
	private IAgent sender;

	/** The state. */
	private volatile State state = State.WAITING;

	/**
	 * Instantiates a new jiac message reply future.
	 * 
	 * @param sender
	 *            the sender
	 * @param callback
	 *            the callback
	 * @param factToListenFor
	 *            the fact to listen for
	 */
	public JiacMessageReplyFuture(final IAgent sender,
			final SpaceObserver<IFact> callback, final IFact factToListenFor) {
		this(sender, null, callback, factToListenFor);
	}

	/**
	 * Instantiates a new jiac message reply future.
	 * 
	 * @param sender
	 *            the sender
	 * @param messageId
	 *            the message id
	 * @param factToListenFor
	 *            the fact to listen for
	 */
	public JiacMessageReplyFuture(final IAgent sender, final String messageId,
			final IFact factToListenFor) {
		this(sender, messageId, null, factToListenFor);
	}

	/**
	 * Instantiates a new jiac message reply future.
	 * 
	 * @param sender
	 *            the sender
	 * @param messageId
	 *            the message id
	 * @param callback
	 *            the callback
	 * @param factToListenFor
	 *            the fact to listen for
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	@Override
	public boolean cancel(final boolean mayInterruptIfRunning) {
		state = State.CANCELLED;
		cleanUp();
		return true;
	}

	/**
	 * Clean up.
	 */
	private void cleanUp() {
		LOG.trace("Cleaning up, removing event handler from memory");
		memory.detach(callback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public IJiacMessage get() throws InterruptedException, ExecutionException {
		LOG.trace("Trying to get item from Future");
		IJiacMessage msg = reply.take();

		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return state == State.CANCELLED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		return state == State.DONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sercho.masp.space.event.SpaceObserver#notify(org.sercho.masp.space
	 * .event.SpaceEvent)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("JiacMessageReplyFuture [sender=%s, state=%s]",
				sender, state);
	}
}
