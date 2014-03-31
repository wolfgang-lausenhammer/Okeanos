package okeanos.management.internal.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.core.entities.builder.EntityBuilder;
import okeanos.management.internal.services.entitymanagement.EntitySerializer;
import okeanos.management.internal.services.entitymanagement.OkeanosBasicAgent;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
public class EntityManagementServiceImpl implements EntityManagementService {
	private Provider<OkeanosBasicAgent> agentProvider;

	private Provider<EntityBuilder> entityBuilderProvider;
	private Gson gson = new GsonBuilder()
			.registerTypeAdapter(Entity.class, new EntitySerializer())
			.serializeNulls().create();

	@Logging
	private Logger log;

	private Map<String, IAgent> managedAgents = new ConcurrentHashMap<>();
	private Map<String, Entity> managedEntitiesByAgentId = new ConcurrentHashMap<>();
	private Map<String, Entity> managedEntitiesByEntityId = new ConcurrentHashMap<>();

	private PlatformManagementService platformManagementService;

	@Inject
	public EntityManagementServiceImpl(
			PlatformManagementService platformManagementService,
			Provider<EntityBuilder> entityBuilderProvider,
			Provider<OkeanosBasicAgent> agentProvider) {
		this.platformManagementService = platformManagementService;
		this.entityBuilderProvider = entityBuilderProvider;
		this.agentProvider = agentProvider;
	}

	private OkeanosBasicAgent getDefaultAgent() {
		OkeanosBasicAgent agent = agentProvider.get();
		managedAgents.put(agent.getAgentId(), agent);
		return agent;
	}

	@Override
	public Entity getEntity(String id) {
		return managedEntitiesByEntityId.get(id);
	}

	/**
	 * Creates and returns an entity builder instance, which can be used to
	 * configure and eventually create an entity.
	 */
	@Override
	public EntityBuilder loadConfigurableEntity() {
		return entityBuilderProvider.get();
	}

	/**
	 * Creates a new entity with the default settings.
	 */
	@Override
	public Entity loadEntity() {
		if (log != null)
			log.debug("Loading entity with default configuration");
		EntityBuilder builder = loadConfigurableEntity();
		builder.agent(getDefaultAgent());
		if (log != null)
			log.debug("Successfully loaded entity with default configuration");

		return builder.build();
	}

	/**
	 * Loads an entity from
	 */
	@Override
	public Entity loadEntityFromJson(String entityAsJson) {
		if (entityAsJson == null)
			throw new NullPointerException("Json string must not be null");

		if (log != null)
			log.debug("Loading entity from json [{}]", entityAsJson);
		EntityBuilder builder = loadConfigurableEntity();
		builder.fromJson(entityAsJson);
		if (log != null)
			log.debug("Successfully loaded entity from json [{}]", entityAsJson);

		return builder.build();
	}

	@Override
	public String saveEntityToJson(Entity entity) {
		return gson.toJson(entity, Entity.class);
	}

	@Override
	public Entity startEntity(Entity entity) throws LifecycleException {
		return startEntity(entity,
				platformManagementService.getDefaultAgentNode());
	}

	@Override
	public Entity startEntity(Entity entity, IAgentNode node)
			throws LifecycleException {
		if (entity == null)
			throw new NullPointerException("Entity entity must not be null");
		if (node == null)
			throw new NullPointerException("IAgentNode node must not be null");

		if (log != null)
			log.debug("Starting entity [{}] by adding it to node [{}]", entity,
					node);
		IAgent agent = entity.getAgent();
		node.addAgent(agent);
		agent.init();
		agent.start();
		managedEntitiesByEntityId.put(entity.getId(), entity);
		managedEntitiesByAgentId.put(agent.getAgentId(), entity);
		managedAgents.put(agent.getAgentId(), agent);
		if (log != null)
			log.debug("Successfully started entity [{}] on node [{}]", entity,
					node);

		return entity;
	}

	@Override
	public Entity stopEntity(Entity entity) throws LifecycleException {
		if (log != null)
			log.debug("Sopping entity [{}]", entity);
		entity.getAgent().stop();
		if (log != null)
			log.debug("Successfully stopped entity [{}]", entity);

		return entity;
	}

	@Override
	public void unloadEntity(Entity entity) throws LifecycleException {
		if (log != null)
			log.debug("Unloading entity [{}]", entity);
		IAgent agent = entity.getAgent();

		managedEntitiesByEntityId.remove(entity.getId());
		managedEntitiesByAgentId.remove(agent.getAgentId());
		managedAgents.remove(agent.getAgentId());

		if (agent instanceof Agent) {
			Agent myAgent = (Agent) agent;
			myAgent.remove();
		} else {
			agent.stop();
			agent.cleanup();

			// remove agent from the agent list of the agent node
			if (agent.getAgentNode() != null) {
				agent.getAgentNode().removeAgent(agent);
			}
		}
		if (log != null)
			log.debug("Successfully unloaded entity [{}]", entity);
	}

}
