package okeanos.data.services;

import java.util.concurrent.Future;

import org.sercho.masp.space.event.SpaceObserver;

import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.ICommunicationAddress;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
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
public interface CommunicationService {

	/**
	 * Returns the next received message of any kind.
	 * 
	 * @param agentBean
	 *            the agent bean to listen for the message
	 * @return the received message
	 */
	IJiacMessage receiveMessage(IAgentBean agentBean);

	/**
	 * Returns the next received message that complies with the given template.
	 * 
	 * @param agentBean
	 *            the agent bean to listen for the message
	 * @param factToListenFor
	 *            a template for the fact to listen for
	 * @return the received message
	 */
	IJiacMessage receiveMessage(IAgentBean agentBean, IFact factToListenFor);

	/**
	 * Returns a future, which returns the next received message of any kind.
	 * 
	 * @param agentBean
	 *            the agent bean to listen for the message
	 * @return a future for the received message
	 * @see CommunicationService#receiveMessage(IAgentBean)
	 */
	Future<IJiacMessage> receiveMessageAsync(IAgentBean agentBean);

	/**
	 * Returns a future, which returns the next received message that complies
	 * with the given template.
	 * 
	 * @param agentBean
	 *            the agent bean to listen for the message
	 * @param factToListenFor
	 *            a template for the fact to listen for
	 * @return a future for the received message
	 * @see CommunicationService#receiveMessage(IAgentBean, IFact)
	 */
	Future<IJiacMessage> receiveMessageAsync(IAgentBean agentBean,
			IFact factToListenFor);

	/**
	 * Registers a listener, which is called for all upcoming messages. Call
	 * {@link #receiveMessageDetachCallback(IAgentBean, SpaceObserver)} to stop
	 * listening for messages.
	 * 
	 * @param agentBean
	 *            the agent bean to listen for the message
	 * @param listener
	 *            a listener, which will be called for the next received
	 *            messages
	 */
	void receiveMessageCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener);

	/**
	 * Registers a listener, which is called for all upcoming messages that
	 * comply with the given template. Call
	 * {@link #receiveMessageDetachCallback(IAgentBean, SpaceObserver)} to stop
	 * listening for messages.
	 * 
	 * @param agentBean
	 *            the agent bean to listen for the message
	 * @param listener
	 *            a listener, which will be called for the next received
	 *            messages
	 * @param factToListenFor
	 *            a template for the fact to listen for
	 */
	void receiveMessageCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener, IFact factToListenFor);

	/**
	 * Detaches a callback from the agent. That is, the callback will not be
	 * called anymore for future messages.
	 * 
	 * @param agentBean
	 *            the agent bean to listen for the message
	 * @param listener
	 *            the callback to detach from the agent
	 */
	void receiveMessageDetachCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener);

	/**
	 * Sends a message to a communication address and wait for an answer to that
	 * message by listening out for the message id in the header. Use
	 * {@link #sendAsync(IAgentBean, ICommunicationAddress, IFact)} if no answer
	 * is expected, otherwise the method will never return.
	 * 
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 * @param message
	 *            the message itself
	 * @return the answer of the receiver of the message
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	IJiacMessage send(IAgentBean sender, ICommunicationAddress receiver,
			IFact message) throws CommunicationException;

	/**
	 * Sends a message to a communication address and wait for an answer to that
	 * message by listening out for the message id in the header. Use
	 * {@link #sendAsync(IAgentBean, String, IFact)} if no answer is expected,
	 * otherwise the method will never return.
	 * 
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 * @param message
	 *            the message itself
	 * @return the answer of the receiver of the message
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	IJiacMessage send(IAgentBean sender, String receiver, IFact message)
			throws CommunicationException;

	/**
	 * Sends a message to a communication address and returns a future, which
	 * can be used to fetch the answer of the receiver. A unique message id is
	 * used to correlate the sent with the received message. Simply do not call
	 * {@link Future#get()} if you do not expect an answer.
	 * 
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 * @param message
	 *            the message itself
	 * @return a future for the received message
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	Future<IJiacMessage> sendAsync(IAgentBean sender,
			ICommunicationAddress receiver, IFact message)
			throws CommunicationException;

	/**
	 * Sends a message to a communication address and returns a future, which
	 * can be used to fetch the answer of the receiver. A unique message id is
	 * used to correlate the sent with the received message. Simply do not call
	 * {@link Future#get()} if you do not expect an answer.
	 * 
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 * @param message
	 *            the message itself
	 * @return a future for the received message
	 * @throws CommunicationException
	 *             if any error occurs during sending or waiting for an answer
	 */
	Future<IJiacMessage> sendAsync(IAgentBean sender, String receiver,
			IFact message) throws CommunicationException;
}
