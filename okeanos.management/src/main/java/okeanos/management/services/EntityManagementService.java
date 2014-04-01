package okeanos.management.services;

import okeanos.core.entities.Entity;
import okeanos.core.entities.builder.EntityBuilder;
import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

public interface EntityManagementService {
	Entity getEntity(String id);

	EntityBuilder loadConfigurableEntity();

	Entity loadEntity();

	Entity loadEntityFromJson(String entityAsJson);

	String saveEntityToJson(Entity entity);

	Entity startEntity(Entity entity) throws LifecycleException;

	Entity startEntity(Entity entity, IAgentNode node)
			throws LifecycleException;

	Entity stopEntity(Entity entity) throws LifecycleException;

	void unloadEntity(Entity entity) throws LifecycleException;
}
