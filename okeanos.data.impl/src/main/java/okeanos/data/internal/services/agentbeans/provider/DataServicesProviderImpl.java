package okeanos.data.internal.services.agentbeans.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.stereotype.Component;

import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.agentbeans.GroupServiceAgentBean;
import okeanos.data.services.agentbeans.KnowledgeBaseServiceAgentBean;
import okeanos.data.services.agentbeans.provider.DataServicesProvider;

@Component("dataServicesProvider")
public class DataServicesProviderImpl implements DataServicesProvider {

	private Provider<CommunicationServiceAgentBean> communicationServiceAgentBeanProvider;
	private Provider<KnowledgeBaseServiceAgentBean> knowledgeBaseServiceAgentBean;
	private Provider<GroupServiceAgentBean> groupServiceAgentBeanProvider;

	@Inject
	public DataServicesProviderImpl(
			final Provider<CommunicationServiceAgentBean> communicationServiceAgentBeanProvider,
			final Provider<KnowledgeBaseServiceAgentBean> knowledgeBaseServiceAgentBean,
			final Provider<GroupServiceAgentBean> groupServiceAgentBeanProvider) {
		this.communicationServiceAgentBeanProvider = communicationServiceAgentBeanProvider;
		this.knowledgeBaseServiceAgentBean = knowledgeBaseServiceAgentBean;
		this.groupServiceAgentBeanProvider = groupServiceAgentBeanProvider;
	}

	@Override
	public CommunicationServiceAgentBean getNewCommunicationServiceAgentBean() {
		return communicationServiceAgentBeanProvider.get();
	}

	@Override
	public GroupServiceAgentBean getNewGroupServiceAgentBean() {
		return groupServiceAgentBeanProvider.get();
	}

	@Override
	public KnowledgeBaseServiceAgentBean getNewKnowledgeBaseServiceAgentBean() {
		return knowledgeBaseServiceAgentBean.get();
	}
}
