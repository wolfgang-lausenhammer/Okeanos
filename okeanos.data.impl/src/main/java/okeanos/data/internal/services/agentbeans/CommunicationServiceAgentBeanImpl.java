package okeanos.data.internal.services.agentbeans;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.data.internal.services.agentbeans.communication.JiacMessageReplyFuture;
import okeanos.data.services.UUIDGenerator;
import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.agentbeans.entities.GridFact;
import okeanos.data.services.agentbeans.entities.GroupFact;
import okeanos.data.services.entities.MessageScope;
import okeanos.spring.misc.stereotypes.Logging;

import org.sercho.masp.space.event.SpaceObserver;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;
import de.dailab.jiactng.agentcore.comm.CommunicationAddressFactory;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.ICommunicationAddress;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import de.dailab.jiactng.agentcore.ontology.AgentDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;

/**
 * An implementation of {@code CommunicationService} using the communication
 * agent bean provided by JIAC.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class CommunicationServiceAgentBeanImpl extends
		AbstractMethodExposingBean implements CommunicationServiceAgentBean {

	/** The communication bean provider. */
	private Provider<ICommunicationBean> communicationBeanProvider;

	/** The logger. */
	@Logging
	private Logger log;

	/** The uuid generator. */
	private UUIDGenerator uuidGenerator;

	/**
	 * Constructor.
	 * 
	 * @param communicationBeanProvider
	 *            the communication bean provider
	 * @param uuidGenerator
	 *            the uuid generator
	 */
	@Inject
	public CommunicationServiceAgentBeanImpl(
			final Provider<ICommunicationBean> communicationBeanProvider,
			final UUIDGenerator uuidGenerator) {
		this.communicationBeanProvider = communicationBeanProvider;
		this.uuidGenerator = uuidGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#broadcast
	 * (okeanos.data.services.entities.MessageScope,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_BROADCAST)
	@Override
	public void broadcast(final MessageScope scope, final IFact message)
			throws CommunicationException {
		if (log != null) {
			log.trace("Broadcasting [message={}] to [scope={}]", message, scope);
		}

		switch (scope) {
		case GROUP:
			Set<GroupFact> groups = memory.readAll(new GroupFact(null));

			for (GroupFact group : groups) {
				ICommunicationAddress receiver = CommunicationAddressFactory
						.createGroupAddress(group.getGroupId());
				if (log != null) {
					log.trace("Sending to [groupAddress={}]", receiver);
				}
				sendAsync(receiver, message);
			}
			break;
		case GRID:
			GridFact grid = memory.read(new GridFact(null));
			ICommunicationAddress gridAddress = CommunicationAddressFactory
					.createGroupAddress(grid.getGroupId());
			if (log != null) {
				log.trace("Sending to [gridAddress={}]", gridAddress);
			}
			sendAsync(gridAddress, message);
			break;
		default:
			throw new UnsupportedOperationException(String.format(
					"MessageScope [%s] is not supported yet.", scope));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#receiveMessage
	 * ()
	 */
	@Expose(name = ACTION_RECEIVE_MESSAGE)
	@Override
	public IJiacMessage receiveMessage() {
		return receiveMessage(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#receiveMessage
	 * (de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_RECEIVE_MESSAGE_IFACT)
	@Override
	public IJiacMessage receiveMessage(final IFact factToListenFor) {
		if (log != null) {
			log.trace(
					"Receiving synchronously [fact={}] at [receivingAgent={}]",
					factToListenFor, thisAgent);
		}
		try {
			return receiveMessageAsync(factToListenFor).get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.CommunicationServiceAgentBean#
	 * receiveMessageAsync()
	 */
	@Expose(name = ACTION_RECEIVE_MESSAGE_ASYNC)
	@Override
	public Future<IJiacMessage> receiveMessageAsync() {
		return receiveMessageAsync(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.CommunicationServiceAgentBean#
	 * receiveMessageAsync(de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_RECEIVE_MESSAGE_ASYNC_IFACT)
	@Override
	public Future<IJiacMessage> receiveMessageAsync(final IFact factToListenFor) {
		if (log != null) {
			log.trace(
					"Receiving asynchronously [fact={}] at [receivingAgent={}]",
					factToListenFor, thisAgent);
		}
		// build future for reply
		Future<IJiacMessage> replyFuture = new JiacMessageReplyFuture(
				thisAgent, (SpaceObserver<IFact>) null, factToListenFor);

		return replyFuture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.CommunicationServiceAgentBean#
	 * receiveMessageCallback(org.sercho.masp.space.event.SpaceObserver)
	 */
	@Expose(name = ACTION_RECEIVE_MESSAGE_CALLBACK)
	@Override
	public void receiveMessageCallback(final SpaceObserver<IFact> listener) {
		receiveMessageCallback(listener, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.CommunicationServiceAgentBean#
	 * receiveMessageCallback(org.sercho.masp.space.event.SpaceObserver,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_RECEIVE_MESSAGE_CALLBACK_IFACT)
	@Override
	public void receiveMessageCallback(final SpaceObserver<IFact> listener,
			final IFact factToListenFor) {
		if (log != null) {
			log.trace(
					"Registering message callback for [receivingAgent={}] [factToListenFor={}]",
					thisAgent, factToListenFor);
		}

		// build future for reply
		new JiacMessageReplyFuture(thisAgent, listener, factToListenFor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.CommunicationServiceAgentBean#
	 * receiveMessageDetachCallback(org.sercho.masp.space.event.SpaceObserver)
	 */
	@Expose(name = ACTION_RECEIVE_MESSAGE_DETACH_CALLBACK)
	@Override
	public void receiveMessageDetachCallback(final SpaceObserver<IFact> listener) {
		memory.detach(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#send(de
	 * .dailab.jiactng.agentcore.comm.ICommunicationAddress,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_SEND)
	@Override
	public IJiacMessage send(final ICommunicationAddress receiver,
			final IFact message) throws CommunicationException {
		return send(receiver, message, Collections.<String, String> emptyMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#send(de
	 * .dailab.jiactng.agentcore.comm.ICommunicationAddress,
	 * de.dailab.jiactng.agentcore.knowledge.IFact, java.util.Map)
	 */
	@Expose(name = ACTION_SEND_OPTIONS)
	@Override
	public IJiacMessage send(final ICommunicationAddress receiver,
			final IFact message, final Map<String, String> options)
			throws CommunicationException {
		if (log != null) {
			log.trace(
					"Sending [message={}] synchronously from [sender={}] to [receiver={}]",
					message, thisAgent, receiver);
		}
		try {
			return sendAsync(receiver, message, options).get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#send(java
	 * .lang.String, de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_SEND_STRING)
	@Override
	public IJiacMessage send(final String receiver, final IFact message)
			throws CommunicationException {
		return send(receiver, message, Collections.<String, String> emptyMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#send(java
	 * .lang.String, de.dailab.jiactng.agentcore.knowledge.IFact, java.util.Map)
	 */
	@Expose(name = ACTION_SEND_STRING_OPTIONS)
	@Override
	public IJiacMessage send(final String receiver, final IFact message,
			final Map<String, String> options) throws CommunicationException {
		return send(getAddressOfAgentByName(receiver), message, options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#sendAsync
	 * (de.dailab.jiactng.agentcore.comm.ICommunicationAddress,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_SEND_ASYNC)
	@Override
	public Future<IJiacMessage> sendAsync(final ICommunicationAddress receiver,
			final IFact message) throws CommunicationException {
		return sendAsync(receiver, message,
				Collections.<String, String> emptyMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#sendAsync
	 * (de.dailab.jiactng.agentcore.comm.ICommunicationAddress,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_SEND_ASYNC_OPTIONS)
	@Override
	public Future<IJiacMessage> sendAsync(final ICommunicationAddress receiver,
			final IFact message, final Map<String, String> options)
			throws CommunicationException {
		// initialize
		ICommunicationBean communicationBean = getCommunicationBean();
		String messageId = "";
		if (options.containsKey("OkeanosCommunicationCorrelationId")) {
			messageId = options.get("OkeanosCommunicationCorrelationId");
		} else {
			messageId = uuidGenerator.generateUUID();
		}

		// compile message
		JiacMessage jiacMessage = new JiacMessage(message);
		jiacMessage.setHeader("OkeanosCommunicationCorrelationId", messageId);
		jiacMessage.setHeader("OkeanosCommunicationSender", getAddressOfAgent()
				.toString());
		jiacMessage.setHeader("OkeanosCommunicationReceiver",
				receiver.toString());
		jiacMessage.setSender(getAddressOfAgent());
		for (Entry<String, String> entry : options.entrySet()) {
			jiacMessage.setHeader(entry.getKey(), entry.getValue());
		}

		// build future for reply
		Future<IJiacMessage> replyFuture = new JiacMessageReplyFuture(
				thisAgent, messageId, null);

		if (log != null) {
			log.trace(
					"Sending [message={}] asynchronously from [sender={}] to [receiver={}]",
					jiacMessage, getAddressOfAgent(), receiver);
		}

		// send
		communicationBean.send(jiacMessage, receiver);

		// return future for receiving
		return replyFuture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#sendAsync
	 * (java.lang.String, de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Expose(name = ACTION_SEND_ASYNC_STRING)
	@Override
	public Future<IJiacMessage> sendAsync(final String receiver,
			final IFact message) throws CommunicationException {
		return sendAsync(receiver, message,
				Collections.<String, String> emptyMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.agentbeans.CommunicationServiceAgentBean#sendAsync
	 * (java.lang.String, de.dailab.jiactng.agentcore.knowledge.IFact,
	 * java.util.Map)
	 */
	@Expose(name = ACTION_SEND_ASYNC_STRING_OPTIONS)
	@Override
	public Future<IJiacMessage> sendAsync(final String receiver,
			final IFact message, final Map<String, String> options)
			throws CommunicationException {
		return sendAsync(getAddressOfAgentByName(receiver), message, options);
	}

	/**
	 * Gets the address of the agent.
	 * 
	 * @return the address of agent
	 */
	private ICommunicationAddress getAddressOfAgent() {
		return thisAgent.getAgentDescription().getMessageBoxAddress();
	}

	/**
	 * Gets the address of an agent by name.
	 * 
	 * @param agentName
	 *            the agent name
	 * @return the address of agent
	 */
	private ICommunicationAddress getAddressOfAgentByName(final String agentName) {
		if (agentName == null) {
			throw new NullPointerException("agent name must not be null");
		}

		List<IAgentDescription> agentDescriptions = thisAgent
				.searchAllAgents(new AgentDescription(null, agentName, null,
						null, null, null));

		for (IAgentDescription agent : agentDescriptions) {
			if (agentName.equals(agent.getName())) {
				return agent.getMessageBoxAddress();
			}
		}

		log.warn("agent with name \"" + agentName + "\" not found");

		return CommunicationAddressFactory.createMessageBoxAddress("");
	}

	/**
	 * Gets the communication bean of the agent.
	 * 
	 * @return the communication bean
	 */
	private ICommunicationBean getCommunicationBean() {
		ICommunicationBean communicationBean = thisAgent.getCommunication();

		if (communicationBean == null) {
			try {
				communicationBean = setCommunicationBeanIfNotSetOnAgent();
			} catch (LifecycleException e) {
				throw new RuntimeException(
						"Error setting communication bean on agent \""
								+ thisAgent + "\"");
			}
		}

		return communicationBean;
	}

	/**
	 * Sets the communication bean if not set on the agent.
	 * 
	 * @return the communication bean
	 * @throws LifecycleException
	 *             if any error occur during starting of the communication bean
	 */
	private ICommunicationBean setCommunicationBeanIfNotSetOnAgent()
			throws LifecycleException {
		if (thisAgent.getCommunication() != null) {
			return thisAgent.getCommunication();
		}

		ICommunicationBean communicationBean = communicationBeanProvider.get();
		communicationBean.setThisAgent(thisAgent);
		communicationBean.init();
		communicationBean.start();
		communicationBean.setMemory(memory);
		thisAgent.setCommunication(communicationBean);

		return communicationBean;
	}
}
