package okeanos.data.services.agentbeans;

import java.util.Map;
import java.util.concurrent.Future;

import okeanos.data.services.entities.MessageScope;

import org.sercho.masp.space.event.SpaceObserver;

import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.ICommunicationAddress;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.environment.IEffector;
import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * An interface for all inter-agent communication services. Methods include the
 * synchronous and asynchronous sending and receiving of messages. Moreover,
 * message handlers for receiving of messages including the registration of
 * templates to only listen for specific messages can be registered.
 * 
 * TODO: document what happens, when more than one message handler is registered
 * for a message
 */
public interface CommunicationServiceAgentBean extends IEffector {

	/** The Constant ACTION_BROADCAST. */
	public static final String ACTION_BROADCAST = "okeanos.data.services.CommunicationService#broadcast(MessageScope, IFact)";

	/** The Constant ACTION_RECEIVE_MESSAGE. */
	public static final String ACTION_RECEIVE_MESSAGE = "okeanos.data.services.CommunicationService#receiveMessage()";

	/** The Constant ACTION_RECEIVE_MESSAGE_IFACT. */
	public static final String ACTION_RECEIVE_MESSAGE_IFACT = "okeanos.data.services.CommunicationService#receiveMessage(IFact)";

	/** The Constant ACTION_RECEIVE_MESSAGE_ASYNC. */
	public static final String ACTION_RECEIVE_MESSAGE_ASYNC = "okeanos.data.services.CommunicationService#receiveMessageAsync()";

	/** The Constant ACTION_RECEIVE_MESSAGE_ASYNC_IFACT. */
	public static final String ACTION_RECEIVE_MESSAGE_ASYNC_IFACT = "okeanos.data.services.CommunicationService#receiveMessageAsync(IFact)";

	/** The Constant ACTION_RECEIVE_MESSAGE_CALLBACK. */
	public static final String ACTION_RECEIVE_MESSAGE_CALLBACK = "okeanos.data.services.CommunicationService#receiveMessageCallback(SpaceObserver<IFact>)";

	/** The Constant ACTION_RECEIVE_MESSAGE_CALLBACK_IFACT. */
	public static final String ACTION_RECEIVE_MESSAGE_CALLBACK_IFACT = "okeanos.data.services.CommunicationService#receiveMessageCallback(SpaceObserver<IFact>, IFact)";

	/** The Constant ACTION_RECEIVE_MESSAGE_DETACH_CALLBACK. */
	public static final String ACTION_RECEIVE_MESSAGE_DETACH_CALLBACK = "okeanos.data.services.CommunicationService#receiveMessageDetachCallback(SpaceObserver<IFact>)";

	/** The Constant ACTION_SEND. */
	public static final String ACTION_SEND = "okeanos.data.services.CommunicationService#send(ICommunicationAddress, IFact)";

	/** The Constant ACTION_SEND_OPTIONS. */
	public static final String ACTION_SEND_OPTIONS = "okeanos.data.services.CommunicationService#send(ICommunicationAddress, IFact, Map<String, String>)";

	/** The Constant ACTION_SEND_STRING. */
	public static final String ACTION_SEND_STRING = "okeanos.data.services.CommunicationService#send(String, IFact)";

	/** The Constant ACTION_SEND_STRING_OPTIONS. */
	public static final String ACTION_SEND_STRING_OPTIONS = "okeanos.data.services.CommunicationService#send(String, IFact, Map<String, String>)";

	/** The Constant ACTION_SEND_ASYNC. */
	public static final String ACTION_SEND_ASYNC = "okeanos.data.services.CommunicationService#sendAsync(ICommunicationAddress, IFact)";

	/** The Constant ACTION_SEND_ASYNC_OPTIONS. */
	public static final String ACTION_SEND_ASYNC_OPTIONS = "okeanos.data.services.CommunicationService#sendAsync(ICommunicationAddress, IFact, Map<String, String>)";

	/** The Constant ACTION_SEND_ASYNC_STRING. */
	public static final String ACTION_SEND_ASYNC_STRING = "okeanos.data.services.CommunicationService#sendAsync(String, IFact, Map<String, String>)";

	/** The Constant ACTION_SEND_ASYNC_STRING_OPTIONS. */
	public static final String ACTION_SEND_ASYNC_STRING_OPTIONS = "okeanos.data.services.CommunicationService#sendAsync(String, IFact)";

	/**
	 * Broadcasts a message to the system. Depending on the scope, the message
	 * will be forwarded to a different number of receivers. Further, it is not
	 * guaranteed, that any agent reacts to a broadcast message, because an
	 * appropriate callback function needs to have been set previously.
	 * 
	 * @param scope
	 *            the scope of the message
	 * @param message
	 *            the message itself
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	void broadcast(MessageScope scope, IFact message)
			throws CommunicationException;

	/**
	 * Returns the next received message of any kind.
	 * 
	 * @return the received message
	 */
	IJiacMessage receiveMessage();

	/**
	 * Returns the next received message that complies with the given template.
	 * 
	 * @param factToListenFor
	 *            a template for the fact to listen for
	 * @return the received message
	 */
	IJiacMessage receiveMessage(IFact factToListenFor);

	/**
	 * Returns a future, which returns the next received message of any kind.
	 * 
	 * @return a future for the received message
	 * @see CommunicationServiceAgentBean#receiveMessage(IAgentBean)
	 */
	Future<IJiacMessage> receiveMessageAsync();

	/**
	 * Returns a future, which returns the next received message that complies
	 * with the given template.
	 * 
	 * @param factToListenFor
	 *            a template for the fact to listen for
	 * @return a future for the received message
	 * @see CommunicationServiceAgentBean#receiveMessage(IAgentBean, IFact)
	 */
	Future<IJiacMessage> receiveMessageAsync(IFact factToListenFor);

	/**
	 * Registers a listener, which is called for all upcoming messages. Call
	 * {@link #receiveMessageDetachCallback(IAgentBean, SpaceObserver)} to stop
	 * listening for messages.
	 * 
	 * @param listener
	 *            a listener, which will be called for the next received
	 *            messages
	 */
	void receiveMessageCallback(SpaceObserver<IFact> listener);

	/**
	 * Registers a listener, which is called for all upcoming messages that
	 * comply with the given template. Call
	 * {@link #receiveMessageDetachCallback(IAgentBean, SpaceObserver)} to stop
	 * listening for messages.
	 * 
	 * @param listener
	 *            a listener, which will be called for the next received
	 *            messages
	 * @param factToListenFor
	 *            a template for the fact to listen for
	 */
	void receiveMessageCallback(SpaceObserver<IFact> listener,
			IFact factToListenFor);

	/**
	 * Detaches a callback from the agent. That is, the callback will not be
	 * called anymore for future messages.
	 * 
	 * @param listener
	 *            the callback to detach from the agent
	 */
	void receiveMessageDetachCallback(SpaceObserver<IFact> listener);

	/**
	 * Sends a message to a communication address and wait for an answer to that
	 * message by listening out for the message id in the header. Use
	 * {@link #sendAsync(IAgentBean, ICommunicationAddress, IFact)} if no answer
	 * is expected, otherwise the method will never return.
	 * 
	 * @param receiver
	 *            the receiver of the message
	 * @param message
	 *            the message itself
	 * @return the answer of the receiver of the message
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	IJiacMessage send(ICommunicationAddress receiver, IFact message)
			throws CommunicationException;

	/**
	 * Sends a message to a communication address and wait for an answer to that
	 * message by listening out for the message id in the header. Use
	 * {@link #sendAsync(IAgentBean, String, IFact)} if no answer is expected,
	 * otherwise the method will never return.
	 * 
	 * @param receiver
	 *            the receiver of the message
	 * @param message
	 *            the message itself
	 * @return the answer of the receiver of the message
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	IJiacMessage send(String receiver, IFact message)
			throws CommunicationException;

	/**
	 * Sends a message to a communication address and returns a future, which
	 * can be used to fetch the answer of the receiver. A unique message id is
	 * used to correlate the sent with the received message. Simply do not call
	 * {@link Future#get()} if you do not expect an answer.
	 * 
	 * @param receiver
	 *            the receiver of the message
	 * @param message
	 *            the message itself
	 * @return a future for the received message
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	Future<IJiacMessage> sendAsync(ICommunicationAddress receiver, IFact message)
			throws CommunicationException;

	/**
	 * Send async.
	 * 
	 * @param receiver
	 *            the receiver
	 * @param message
	 *            the message
	 * @param options
	 *            the options
	 * @return the future
	 * @throws CommunicationException
	 *             the communication exception
	 */
	Future<IJiacMessage> sendAsync(ICommunicationAddress receiver,
			IFact message, Map<String, String> options)
			throws CommunicationException;

	/**
	 * Sends a message to a communication address and returns a future, which
	 * can be used to fetch the answer of the receiver. A unique message id is
	 * used to correlate the sent with the received message. Simply do not call
	 * {@link Future#get()} if you do not expect an answer.
	 * 
	 * @param receiver
	 *            the receiver of the message
	 * @param message
	 *            the message itself
	 * @return a future for the received message
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	Future<IJiacMessage> sendAsync(String receiver, IFact message)
			throws CommunicationException;

	/**
	 * Send async.
	 * 
	 * @param receiver
	 *            the receiver
	 * @param message
	 *            the message
	 * @param options
	 *            the options
	 * @return the future
	 * @throws CommunicationException
	 *             the communication exception
	 */
	Future<IJiacMessage> sendAsync(String receiver, IFact message,
			Map<String, String> options) throws CommunicationException;

	/**
	 * Send.
	 * 
	 * @param receiver
	 *            the receiver
	 * @param message
	 *            the message
	 * @param options
	 *            the options
	 * @return the i jiac message
	 * @throws CommunicationException
	 *             the communication exception
	 */
	IJiacMessage send(ICommunicationAddress receiver, IFact message,
			Map<String, String> options) throws CommunicationException;

	/**
	 * Send.
	 * 
	 * @param receiver
	 *            the receiver
	 * @param message
	 *            the message
	 * @param options
	 *            the options
	 * @return the i jiac message
	 * @throws CommunicationException
	 *             the communication exception
	 */
	IJiacMessage send(String receiver, IFact message,
			Map<String, String> options) throws CommunicationException;
}
