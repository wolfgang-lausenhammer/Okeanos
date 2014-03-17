package okeanos.data.internal.services;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.data.internal.services.communication.JiacMessageReplyFuture;
import okeanos.data.services.CommunicationService;
import okeanos.data.services.UUIDGenerator;

import org.sercho.masp.space.event.SpaceObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import de.dailab.jiactng.agentcore.ontology.AgentDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;

@Component("communicationServiceImpl")
public class CommunicationServiceImpl implements CommunicationService {
	private static Provider<ICommunicationBean> communicationBeanProvider;

	private static final Logger log = LoggerFactory
			.getLogger(CommunicationServiceImpl.class);

	private static AbstractAgentBean getAbstractAgentBean(IAgentBean agentBean) {
		if (!(agentBean instanceof AbstractAgentBean)) {
			throw new ClassCastException(
					"agentBean is not of type AbstractAgentBean");
		}

		return (AbstractAgentBean) agentBean;
	}

	private static ICommunicationAddress getAddressOfAgent(IAgent agent) {
		return agent.getAgentDescription().getMessageBoxAddress();
	}

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

	private static ICommunicationBean getCommunicationBean(IAgentBean sender) {
		ICommunicationBean communicationBean = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(sender)).getCommunication();

		if (communicationBean == null) {
			try {
				setCommunicationBeanIfNotSetOnAgent(sender);
			} catch (LifecycleException e) {
				throw new RuntimeException(
						"Error setting communication bean on agent \"" + sender
								+ "\"");
			}
		}

		return communicationBean;
	}

	private static void setCommunicationBeanIfNotSetOnAgent(IAgentBean agentBean)
			throws LifecycleException {
		IAgent agent = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(agentBean));
		if (agent.getCommunication() != null) {
			return;
		}

		ICommunicationBean communicationBean = communicationBeanProvider.get();
		communicationBean.setThisAgent(agent);
		communicationBean.init();
		communicationBean.start();
		communicationBean.setMemory(AbstractAgentProtectedMethodPublisher
				.getMemory((Agent) agent));
		agent.setCommunication(communicationBean);
	}

	private UUIDGenerator uuidGenerator;

	@Inject
	public CommunicationServiceImpl(
			Provider<ICommunicationBean> communicationBeanProvider,
			UUIDGenerator uuidGenerator) {
		CommunicationServiceImpl.communicationBeanProvider = communicationBeanProvider;
		this.uuidGenerator = uuidGenerator;
	}

	@Override
	public IJiacMessage receiveMessage(IAgentBean agentBean) {
		return receiveMessage(agentBean, null);
	}

	@Override
	public IJiacMessage receiveMessage(IAgentBean agentBean,
			IFact factToListenFor) {
		log.debug("Receiving synchronously [fact={}] at [agentBean={}]",
				factToListenFor, agentBean);
		try {
			return receiveMessageAsync(agentBean, factToListenFor).get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	@Override
	public Future<IJiacMessage> receiveMessageAsync(IAgentBean agentBean) {
		return receiveMessageAsync(agentBean, null);
	}

	@Override
	public Future<IJiacMessage> receiveMessageAsync(IAgentBean agentBean,
			IFact factToListenFor) {
		log.debug("Receiving asynchronously [fact={}] at [agentBean={}]",
				factToListenFor, agentBean);
		// build future for reply
		Future<IJiacMessage> replyFuture = new JiacMessageReplyFuture(
				agentBean, (SpaceObserver<IFact>) null, factToListenFor);

		return replyFuture;
	}

	@Override
	public void receiveMessageCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener) {
		receiveMessageCallback(agentBean, listener, null);
	}

	@Override
	public void receiveMessageCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener, IFact factToListenFor) {
		log.debug(
				"Registering message callback for [agentBean={}] [factToListenFor={}]",
				agentBean, factToListenFor);

		// build future for reply
		new JiacMessageReplyFuture(agentBean, listener, factToListenFor);
	}

	@Override
	public IJiacMessage send(IAgentBean sender, ICommunicationAddress receiver,
			IFact message) throws CommunicationException {
		log.debug(
				"Sending [message={}] synchronously from [sender={}] to [receiver={}]",
				message, sender, receiver);
		try {
			return sendAsync(sender, receiver, message).get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	@Override
	public IJiacMessage send(IAgentBean sender, String receiver, IFact message)
			throws CommunicationException {
		IAgent agent = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(sender));
		return send(sender, getAddressOfAgentByName(agent, receiver), message);
	}

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

		log.debug(
				"Sending [message={}] asynchronously from [sender={}] to [receiver={}]",
				jiacMessage, getAddressOfAgent(agent), receiver);

		// send
		communicationBean.send(jiacMessage, receiver);

		// return future for receiving
		return replyFuture;
	}

	@Override
	public Future<IJiacMessage> sendAsync(IAgentBean sender, String receiver,
			IFact message) throws CommunicationException {
		IAgent agent = AbstractAgentBeanProtectedMethodPublisher
				.getAgent(getAbstractAgentBean(sender));
		return sendAsync(sender, getAddressOfAgentByName(agent, receiver),
				message);
	}
}
