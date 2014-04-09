package okeanos.data.services.agentbeans.provider;

import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.agentbeans.GroupServiceAgentBean;
import okeanos.data.services.agentbeans.KnowledgeBaseServiceAgentBean;

/**
 * Provides methods for creating instances of the data services.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface DataServicesProvider {

	/**
	 * Gets the new communication service agent bean.
	 * 
	 * @return the new communication service agent bean
	 */
	CommunicationServiceAgentBean getNewCommunicationServiceAgentBean();

	/**
	 * Gets the new group service agent bean.
	 * 
	 * @return the new group service agent bean
	 */
	GroupServiceAgentBean getNewGroupServiceAgentBean();

	/**
	 * Gets the new knowledge base service agent bean.
	 * 
	 * @return the new knowledge base service agent bean
	 */
	KnowledgeBaseServiceAgentBean getNewKnowledgeBaseServiceAgentBean();

}
