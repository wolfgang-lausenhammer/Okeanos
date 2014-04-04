package okeanos.data.internal.services.agentbeans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;
import okeanos.data.services.agentbeans.KnowledgeBaseServiceAgentBean;

/**
 * The Class KnowledgeBaseServiceAgentBeanImpl.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class KnowledgeBaseServiceAgentBeanImpl extends
		AbstractMethodExposingBean implements KnowledgeBaseServiceAgentBean {

}
