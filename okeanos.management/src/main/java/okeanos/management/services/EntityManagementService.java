package okeanos.management.services;

import okeanos.core.entities.Entity;
import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

public interface EntityManagementService {
	Entity loadEntity(String pathToConfig);

	Entity loadEntity();

	Entity getEntity(String id);

	Entity startEntity(Entity entity);

	Entity startEntity(Entity entity, IAgentNode node);

	Entity stopEntity(Entity entity) throws LifecycleException;

	void unloadEntity(Entity entity) throws LifecycleException;
}
