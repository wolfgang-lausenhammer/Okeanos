package okeanos.core.entities;

import de.dailab.jiactng.agentcore.IAgent;

public interface Entity {
	String getId();

	void setId(String id);

	IAgent getAgent();

	void setAgent(IAgent agent);
}
