package okeanos.core.entities;

import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

public interface Entity {
	String getId();

	IAgent getAgent();

	void setAgent(IAgent agent);

	void addFunctionality(IAgentBean... functionality)
			throws LifecycleException;
}
