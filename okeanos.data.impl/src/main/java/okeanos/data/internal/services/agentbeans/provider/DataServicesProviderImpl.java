package okeanos.data.internal.services.agentbeans.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.stereotype.Component;

import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.agentbeans.GroupServiceAgentBean;
import okeanos.data.services.agentbeans.KnowledgeBaseServiceAgentBean;
import okeanos.data.services.agentbeans.provider.DataServicesProvider;

/**
 * Provides methods for creating instances of the data services.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("dataServicesProvider")
public class DataServicesProviderImpl implements DataServicesProvider {

	/** The communication service agent bean provider. */
	private Provider<CommunicationServiceAgentBean> communicationServiceAgentBeanProvider;

	/** The knowledge base service agent bean. */
	private Provider<KnowledgeBaseServiceAgentBean> knowledgeBaseServiceAgentBean;

	/** The group service agent bean provider. */
	private Provider<GroupServiceAgentBean> groupServiceAgentBeanProvider;

	/**
	 * Instantiates a new data services provider impl.
	 * 
	 * @param communicationServiceAgentBeanProvider
	 *            the communication service agent bean provider
	 * @param knowledgeBaseServiceAgentBean
	 *            the knowledge base service agent bean
	 * @param groupServiceAgentBeanProvider
	 *            the group service agent bean provider
	 */
	@Inject
	public DataServicesProviderImpl(
			final Provider<CommunicationServiceAgentBean> communicationServiceAgentBeanProvider,
			final Provider<KnowledgeBaseServiceAgentBean> knowledgeBaseServiceAgentBean,
			final Provider<GroupServiceAgentBean> groupServiceAgentBeanProvider) {
		this.communicationServiceAgentBeanProvider = communicationServiceAgentBeanProvider;
		this.knowledgeBaseServiceAgentBean = knowledgeBaseServiceAgentBean;
		this.groupServiceAgentBeanProvider = groupServiceAgentBeanProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.provider.DataServicesProvider#
	 * getNewCommunicationServiceAgentBean()
	 */
	@Override
	public CommunicationServiceAgentBean getNewCommunicationServiceAgentBean() {
		return communicationServiceAgentBeanProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.provider.DataServicesProvider#
	 * getNewGroupServiceAgentBean()
	 */
	@Override
	public GroupServiceAgentBean getNewGroupServiceAgentBean() {
		return groupServiceAgentBeanProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.provider.DataServicesProvider#
	 * getNewKnowledgeBaseServiceAgentBean()
	 */
	@Override
	public KnowledgeBaseServiceAgentBean getNewKnowledgeBaseServiceAgentBean() {
		return knowledgeBaseServiceAgentBean.get();
	}
}
