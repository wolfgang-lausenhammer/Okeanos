package okeanos.management.services;

import okeanos.core.entities.Entity;
import okeanos.core.entities.builder.EntityBuilder;
import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.ILifecycle.LifecycleStates;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * The {@link EntityManagementService} provides services around {@link Entity}.
 * That is, it enables the creation of entities, both with a default
 * configuration or through a {@link EntityBuilder}, which allows for complete
 * configuration of the entity.
 * 
 * Moreover, the so created entity can then be started, stopped and unloaded.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface EntityManagementService {

	/**
	 * Gets a previously created entity. If no entity with the given id was
	 * found, returns null.
	 * 
	 * @param id
	 *            the id of the previously created entity
	 * @return the entity
	 */
	Entity getEntity(String id);

	/**
	 * Returns an {@link EntityBuilder} instance to be able to fully configure
	 * the entity.
	 * 
	 * @return the entity builder
	 */
	EntityBuilder loadConfigurableEntity();

	/**
	 * Returns an {@link Entity} with the default configuration applied.
	 * 
	 * @return the loaded entity
	 */
	Entity loadEntity();

	/**
	 * Loads an {@link Entity} from a json string.
	 * 
	 * @param entityAsJson
	 *            the entity as json
	 * @return the loaded entity
	 */
	Entity loadEntityFromJson(String entityAsJson);

	/**
	 * Saves an {@link Entity} to json.
	 * 
	 * @param entity
	 *            the entity to save
	 * @return the loaded string
	 */
	String saveEntityToJson(Entity entity);

	/**
	 * Starts an entity and runs through the whole {@link LifecycleStates} until
	 * the underlying agent is in state {@code RUN}. The agent will be started
	 * on the default {@link IAgentNode}. If no agent node has been created
	 * before, creates a new agent node.
	 * 
	 * @param entity
	 *            the entity to start
	 * @return the entity
	 * @throws LifecycleException
	 *             if the underlying agent faces any problems while initializing
	 *             and starting up
	 */
	Entity startEntity(Entity entity) throws LifecycleException;

	/**
	 * Starts an entity and runs through the whole {@link LifecycleStates} until
	 * the underlying agent is in state {@code RUN}. The agent will be started
	 * on the given {@link IAgentNode}.
	 * 
	 * @param entity
	 *            the entity to start
	 * @param node
	 *            the node to run the underlying agent on
	 * @return the entity
	 * @throws LifecycleException
	 *             if the underlying agent faces any problems while initializing
	 *             and starting up
	 */
	Entity startEntity(Entity entity, IAgentNode node)
			throws LifecycleException;

	/**
	 * Stops an entity, thereby stopping the underlying agent. To remove the
	 * agent from the agent node, see {@link #unloadEntity(Entity)}.
	 * 
	 * @param entity
	 *            the entity to sop
	 * @return the stopped entity
	 * @throws LifecycleException
	 *             if the underlying agent faces any problems while shutting
	 *             down
	 */
	Entity stopEntity(Entity entity) throws LifecycleException;

	/**
	 * Unloads an entity. The underlying agent is thereby removed from the agent
	 * node.
	 * 
	 * Note: stop the entity before considering to unload it, see
	 * {@link #stopEntity(Entity)}.
	 * 
	 * @param entity
	 *            the entity to unload
	 * @throws LifecycleException
	 *             if the underlying agent faces any problems while unloading
	 */
	void unloadEntity(Entity entity) throws LifecycleException;
}
