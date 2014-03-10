package okeanos.core.internal.entities;

import de.dailab.jiactng.agentcore.IAgent;
import okeanos.core.entities.Entity;

public class EntityImpl implements Entity {

	private String id;
	private IAgent agent;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public IAgent getAgent() {
		return agent;
	}

	@Override
	public void setAgent(IAgent agent) {
		this.agent = agent;
	}

}
