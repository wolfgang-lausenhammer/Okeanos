package okeanos.management.internal.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.core.entities.builder.EntityBuilder;
import okeanos.data.services.agentbeans.provider.DataServicesProvider;
import okeanos.management.internal.services.entitymanagement.EntitySerializer;
import okeanos.management.internal.services.entitymanagement.OkeanosBasicAgent;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentNode;
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
@Component
public class EntityManagementServiceImpl implements EntityManagementService {

	/** The agent provider. */
	private Provider<OkeanosBasicAgent> agentProvider;

	/** The data services provider. */
	private DataServicesProvider dataServicesProvider;

	/** The entity builder provider. */
	private Provider<EntityBuilder> entityBuilderProvider;

	/** The gson (de)serializer. */
	private Gson gson = new GsonBuilder()
			.registerTypeAdapter(Entity.class, new EntitySerializer())
			.serializeNulls().create();

	/** The logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(EntityManagementServiceImpl.class);

	/** The managed agents. */
	private Map<String, IAgent> managedAgents = new ConcurrentHashMap<>();

	/** The managed entities by agent id. */
	private Map<String, Entity> managedEntitiesByAgentId = new ConcurrentHashMap<>();

	/** The managed entities by entity id. */
	private Map<String, Entity> managedEntitiesByEntityId = new ConcurrentHashMap<>();

	/** The platform management service. */
	private PlatformManagementService platformManagementService;

	/**
	 * Instantiates a new entity management service.
	 * 
	 * @param platformManagementService
	 *            the platform management service
	 * @param dataServicesProvider
	 *            the data services provider
	 * @param entityBuilderProvider
	 *            the entity builder provider
	 * @param agentProvider
	 *            the agent provider
	 */
	@Inject
	public EntityManagementServiceImpl(
			final PlatformManagementService platformManagementService,
			final DataServicesProvider dataServicesProvider,
			final Provider<EntityBuilder> entityBuilderProvider,
			final Provider<OkeanosBasicAgent> agentProvider) {
		this.platformManagementService = platformManagementService;
		this.dataServicesProvider = dataServicesProvider;
		this.entityBuilderProvider = entityBuilderProvider;
		this.agentProvider = agentProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.EntityManagementService#getEntity(java.lang
	 * .String)
	 */
	@Override
	public Entity getEntity(String id) {
		return managedEntitiesByEntityId.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.EntityManagementService#loadConfigurableEntity
	 * ()
	 */
	@Override
	public EntityBuilder loadConfigurableEntity() {
		return entityBuilderProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.management.services.EntityManagementService#loadEntity()
	 */
	@Override
	public Entity loadEntity() {
		LOG.debug("Loading entity with default configuration");
		EntityBuilder builder = loadConfigurableEntity();
		builder.agent(getDefaultAgent());
		LOG.debug("Successfully loaded entity with default configuration");

		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.EntityManagementService#loadEntityFromJson
	 * (java.lang.String)
	 */
	@Override
	public Entity loadEntityFromJson(String entityAsJson) {
		if (entityAsJson == null) {
			throw new NullPointerException("Json string must not be null");
		}

		LOG.debug("Loading entity from json [{}]", entityAsJson);
		EntityBuilder builder = loadConfigurableEntity();
		builder.fromJson(entityAsJson);
		LOG.debug("Successfully loaded entity from json [{}]", entityAsJson);

		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.EntityManagementService#saveEntityToJson(
	 * okeanos.core.entities.Entity)
	 */
	@Override
	public String saveEntityToJson(Entity entity) {
		return gson.toJson(entity, Entity.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.EntityManagementService#startEntity(okeanos
	 * .core.entities.Entity)
	 */
	@Override
	public Entity startEntity(Entity entity) throws LifecycleException {
		return startEntity(entity,
				platformManagementService.getDefaultAgentNode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.EntityManagementService#startEntity(okeanos
	 * .core.entities.Entity, de.dailab.jiactng.agentcore.IAgentNode)
	 */
	@Override
	public Entity startEntity(Entity entity, IAgentNode node)
			throws LifecycleException {
		if (entity == null) {
			throw new NullPointerException("Entity entity must not be null");
		}
		if (node == null) {
			throw new NullPointerException("IAgentNode node must not be null");
		}

		LOG.debug("Starting entity [{}] by adding it to node [{}]", entity,
				node);
		IAgent agent = entity.getAgent();
		node.addAgent(agent);
		agent.init();
		agent.start();
		entity.addFunctionality(dataServicesProvider
				.getNewCommunicationServiceAgentBean());
		entity.addFunctionality(dataServicesProvider
				.getNewGroupServiceAgentBean());
		managedEntitiesByEntityId.put(entity.getId(), entity);
		managedEntitiesByAgentId.put(agent.getAgentId(), entity);
		managedAgents.put(agent.getAgentId(), agent);
		LOG.debug("Successfully started entity [{}] on node [{}]", entity, node);

		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.EntityManagementService#stopEntity(okeanos
	 * .core.entities.Entity)
	 */
	@Override
	public Entity stopEntity(Entity entity) throws LifecycleException {
		LOG.debug("Sopping entity [{}]", entity);
		entity.getAgent().stop();
		LOG.debug("Successfully stopped entity [{}]", entity);

		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.EntityManagementService#unloadEntity(okeanos
	 * .core.entities.Entity)
	 */
	@Override
	public void unloadEntity(Entity entity) throws LifecycleException {
		LOG.debug("Unloading entity [{}]", entity);
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
		LOG.debug("Successfully unloaded entity [{}]", entity);
	}

	/**
	 * Creates a default agent. A unique id will be generated.
	 * 
	 * @return the default agent
	 */
	private OkeanosBasicAgent getDefaultAgent() {
		OkeanosBasicAgent agent = agentProvider.get();
		managedAgents.put(agent.getAgentId(), agent);
		return agent;
	}

}
