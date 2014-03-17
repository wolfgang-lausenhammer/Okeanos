package okeanos.data.services;

import java.util.concurrent.Future;

import org.sercho.masp.space.event.SpaceObserver;

import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.ICommunicationAddress;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;

public interface CommunicationService {
	IJiacMessage receiveMessage(IAgentBean agentBean);

	IJiacMessage receiveMessage(IAgentBean agentBean, IFact factToListenFor);

	Future<IJiacMessage> receiveMessageAsync(IAgentBean agentBean);

	Future<IJiacMessage> receiveMessageAsync(IAgentBean agentBean,
			IFact factToListenFor);

	void receiveMessageCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener);

	void receiveMessageCallback(IAgentBean agentBean,
			SpaceObserver<IFact> listener, IFact factToListenFor);

	IJiacMessage send(IAgentBean sender, ICommunicationAddress receiver,
			IFact message) throws CommunicationException;

	IJiacMessage send(IAgentBean sender, String receiver, IFact message)
			throws CommunicationException;

	Future<IJiacMessage> sendAsync(IAgentBean sender,
			ICommunicationAddress iCommunicationAddress, IFact message)
			throws CommunicationException;

	Future<IJiacMessage> sendAsync(IAgentBean sender, String receiver,
			IFact message) throws CommunicationException;
}
