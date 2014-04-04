package okeanos.core.entities.builder;

import okeanos.core.entities.Entity;
import de.dailab.jiactng.agentcore.IAgent;

public interface EntityBuilder extends Builder {
	EntityBuilder agent(IAgent agent);

	EntityBuilder fromJson(String entityAsJson);

	Entity build();
}
