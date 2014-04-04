package okeanos.data.services.agentbeans.provider;

import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.agentbeans.GroupServiceAgentBean;
import okeanos.data.services.agentbeans.KnowledgeBaseServiceAgentBean;

public interface DataServicesProvider {

	CommunicationServiceAgentBean getNewCommunicationServiceAgentBean();

	KnowledgeBaseServiceAgentBean getNewKnowledgeBaseServiceAgentBean();

	GroupServiceAgentBean getNewGroupServiceAgentBean();

}
