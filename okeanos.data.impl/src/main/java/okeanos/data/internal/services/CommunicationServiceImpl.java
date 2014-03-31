package okeanos.data.internal.services;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.data.internal.services.communication.JiacMessageReplyFuture;
import okeanos.data.services.CommunicationService;
import okeanos.data.services.UUIDGenerator;
import okeanos.spring.misc.stereotypes.Logging;

import org.sercho.masp.space.event.SpaceObserver;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.AbstractAgentBeanProtectedMethodPublisher;
import de.dailab.jiactng.agentcore.AbstractAgentProtectedMethodPublisher;
import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.ICommunicationAddress;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.knowledge.IMemory;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import de.dailab.jiactng.agentcore.ontology.AgentDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;

/**
 * An implementation of {@code CommunicationService} using the communication
 * agent bean provided by JIAC.
 */
@Component("communicationServiceImpl")
public class CommunicationServiceImpl implements CommunicationService {

	/** The communication bean provider. */
	private static Provider<ICommunicationBean> communicationBeanProvider;

	/**
	 * Gets the abstract agent bean.
	 * 
	 * @param agentBean
	 *            the agent bean
	 * @return the abstract agent bean
	 */
	private static AbstractAgentBean getAbstractAgentBean(IAgentBean agentBean) {
		if (!(agentBean instanceof AbstractAgentBean)) {
			throw new ClassCastException(
					"agentBean is not of type AbstractAgentBean");
		}

		return (AbstractAgentBean) agentBean;
	}

	/**
	 * Gets the address of the agent.
	 * 
	 * @param agent
	 *            the agent to get the address from
	 * @return the address of agent
	 */
	private static ICommunicationAddress getAddressOfAgent(IAgent agent) {
		return agent.getAgentDescription().getMessageBoxAddress();
	}

	/**
	 * Gets the address of an agent by name.
	 * 
	 * @param anyAgent
	 *            any agent, to be able to get a listing of all agents
	 * @param agentName
	 *            the agent name
	 * @return the address of agent
	 */
	private static ICommunicationAddress getAddressOfAgentByName(
			IAgent anyAgent, String agentName) {
		if (agentName == null)
			throw new NullPointerException("agent name must not be null");

		List<IAgentDescription> agentDescriptions = anyAgent
				.searchAllAgents(new AgentDescription());

		for (IAgentDescription agent : agentDescriptions) {
			if (agentName.equals(agent.getName()))
				return agent.getMessageBoxAddress();
		}

		throw new RuntimeException("agent with name \"" + agentName
				+ "\" not found");
	}

	/**
	 * Gets the communication bean of the agent.
	 * 
	 * @param sender
	 *            the agent from which to get the communication bean
	 * @return the communication bean
	 */
	private static ICommunicationBean getCommunicationBean(IAgentBean sender) {
		ICommunicationBean communicationBean = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(sender)).getCommunication();

		if (communicationBean == null) {
			try {
				communicationBean = setCommunicationBeanIfNotSetOnAgent(sender);
			} catch (LifecycleException e) {
				throw new RuntimeException(
						"Error setting communication bean on agent \"" + sender
								+ "\"");
			}
		}

		return communicationBean;
	}

	/**
	 * Sets the communication bean if not set on the agent.
	 * 
	 * @param agentBean
	 *            the agent bean
	 * @return the communication bean
	 * 
	 * @throws LifecycleException
	 *             if any error occur during starting of the communication bean
	 */
	private static ICommunicationBean setCommunicationBeanIfNotSetOnAgent(
			IAgentBean agentBean) throws LifecycleException {
		IAgent agent = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(agentBean));
		if (agent.getCommunication() != null) {
			return agent.getCommunication();
		}

		ICommunicationBean communicationBean = communicationBeanProvider.get();
		communicationBean.setThisAgent(agent);
		communicationBean.init();
		communicationBean.start();
		communicationBean.setMemory(AbstractAgentProtectedMethodPublisher
				.getMemory((Agent) agent));
		agent.setCommunication(communicationBean);

		return communicationBean;
	}

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
	public CommunicationServiceImpl(
			Provider<ICommunicationBean> communicationBeanProvider,
			UUIDGenerator uuidGenerator) {
		CommunicationServiceImpl.communicationBeanProvider = communicationBeanProvider;
		this.uuidGenerator = uuidGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#receiveMessage(de.dailab.jiactng
	 * .agentcore.IAgentBean)
	 */
	@Override
	public IJiacMessage receiveMessage(IAgentBean agentBean) {
		return receiveMessage(agentBean, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#receiveMessage(de.dailab.jiactng
	 * .agentcore.IAgentBean, de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Override
	public IJiacMessage receiveMessage(IAgentBean agentBean,
			IFact factToListenFor) {
		if (log != null)
			log.debug("Receiving synchronously [fact={}] at [agentBean={}]",
					factToListenFor, agentBean);
		try {
			return receiveMessageAsync(agentBean, factToListenFor).get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#receiveMessageAsync(de.dailab
	 * .jiactng.agentcore.IAgentBean)
	 */
	@Override
	public Future<IJiacMessage> receiveMessageAsync(IAgentBean agentBean) {
		return receiveMessageAsync(agentBean, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#receiveMessageAsync(de.dailab
	 * .jiactng.agentcore.IAgentBean,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Override
	public Future<IJiacMessage> receiveMessageAsync(IAgentBean agentBean,
			IFact factToListenFor) {
		if (log != null)
			log.debug("Receiving asynchronously [fact={}] at [agentBean={}]",
					factToListenFor, agentBean);
		// build future for reply
		Future<IJiacMessage> replyFuture = new JiacMessageReplyFuture(
				agentBean, (SpaceObserver<IFact>) null, factToListenFor);

		return replyFuture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#receiveMessageCallback(de.
	 * dailab.jiactng.agentcore.IAgentBean,
	 * org.sercho.masp.space.event.SpaceObserver)
	 */
	@Override
	public void receiveMessageCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener) {
		receiveMessageCallback(agentBean, listener, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#receiveMessageCallback(de.
	 * dailab.jiactng.agentcore.IAgentBean,
	 * org.sercho.masp.space.event.SpaceObserver,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Override
	public void receiveMessageCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener, IFact factToListenFor) {
		if (log != null)
			log.debug(
					"Registering message callback for [agentBean={}] [factToListenFor={}]",
					agentBean, factToListenFor);

		// build future for reply
		new JiacMessageReplyFuture(agentBean, listener, factToListenFor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#receiveMessageDetachCallback
	 * (de.dailab.jiactng.agentcore.IAgentBean,
	 * org.sercho.masp.space.event.SpaceObserver)
	 */
	@Override
	public void receiveMessageDetachCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener) {
		IMemory memory = AbstractAgentBeanProtectedMethodPublisher
				.getMemory(((AbstractAgentBean) agentBean));
		memory.detach(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#send(de.dailab.jiactng.agentcore
	 * .IAgentBean, de.dailab.jiactng.agentcore.comm.ICommunicationAddress,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Override
	public IJiacMessage send(IAgentBean sender, ICommunicationAddress receiver,
			IFact message) throws CommunicationException {
		if (log != null)
			log.debug(
					"Sending [message={}] synchronously from [sender={}] to [receiver={}]",
					message, sender, receiver);
		try {
			return sendAsync(sender, receiver, message).get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#send(de.dailab.jiactng.agentcore
	 * .IAgentBean, java.lang.String,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Override
	public IJiacMessage send(IAgentBean sender, String receiver, IFact message)
			throws CommunicationException {
		IAgent agent = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(sender));
		return send(sender, getAddressOfAgentByName(agent, receiver), message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#sendAsync(de.dailab.jiactng
	 * .agentcore.IAgentBean,
	 * de.dailab.jiactng.agentcore.comm.ICommunicationAddress,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Override
	public Future<IJiacMessage> sendAsync(IAgentBean sender,
			ICommunicationAddress receiver, IFact message)
			throws CommunicationException {
		// initialize
		ICommunicationBean communicationBean = getCommunicationBean(sender);
		IAgent agent = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(sender));
		String messageId = uuidGenerator.generateUUID();

		// compile message
		JiacMessage jiacMessage = new JiacMessage(message);
		jiacMessage.setHeader("OkeanosCommunicationCorrelationId", messageId);
		jiacMessage.setHeader("OkeanosCommunicationSender",
				getAddressOfAgent(agent).toString());
		jiacMessage.setHeader("OkeanosCommunicationReceiver",
				receiver.toString());
		jiacMessage.setSender(getAddressOfAgent(agent));

		// build future for reply
		Future<IJiacMessage> replyFuture = new JiacMessageReplyFuture(sender,
				messageId, null);

		if (log != null)
			log.debug(
					"Sending [message={}] asynchronously from [sender={}] to [receiver={}]",
					jiacMessage, getAddressOfAgent(agent), receiver);

		// send
		communicationBean.send(jiacMessage, receiver);

		// return future for receiving
		return replyFuture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.CommunicationService#sendAsync(de.dailab.jiactng
	 * .agentcore.IAgentBean, java.lang.String,
	 * de.dailab.jiactng.agentcore.knowledge.IFact)
	 */
	@Override
	public Future<IJiacMessage> sendAsync(IAgentBean sender, String receiver,
			IFact message) throws CommunicationException {
		IAgent agent = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(sender));
		return sendAsync(sender, getAddressOfAgentByName(agent, receiver),
				message);
	}
}
