package okeanos.management.services;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

public interface PlatformManagementService {

	IAgentNode getAgentNode(String id);

	IAgentNode getDefaultAgentNode();

	IAgentNode startAgentNode() throws LifecycleException;

	void stopAgentNode(IAgentNode agentNode) throws LifecycleException;
}
