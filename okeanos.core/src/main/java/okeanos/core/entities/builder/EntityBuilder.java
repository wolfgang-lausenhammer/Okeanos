package okeanos.core.entities.builder;

import de.dailab.jiactng.agentcore.IAgent;
import okeanos.core.entities.Entity;

public interface EntityBuilder {
	EntityBuilder agent(IAgent agent);

	Entity build();
}
