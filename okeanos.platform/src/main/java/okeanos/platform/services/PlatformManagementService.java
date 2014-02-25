package okeanos.platform.services;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

public interface PlatformManagementService {

	IAgentNode startAgentNode() throws LifecycleException;
	//void startPlatform();

	void stopAgentNode(IAgentNode agentNode) throws LifecycleException;
}
