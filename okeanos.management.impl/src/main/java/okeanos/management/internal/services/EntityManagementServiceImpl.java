package okeanos.management.internal.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import okeanos.core.entities.Entity;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;

import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
public class EntityManagementServiceImpl implements EntityManagementService {

	private PlatformManagementService managementManagementService;

	private Map<String, Entity> managedEntities = new ConcurrentHashMap<>();

	@Inject
	public EntityManagementServiceImpl(
			PlatformManagementService managementManagementService) {
		this.managementManagementService = managementManagementService;
	}

	@Override
	public Entity loadEntity(String pathToConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity loadEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getEntity(String id) {
		return managedEntities.get(id);
	}

	@Override
	public Entity startEntity(Entity entity) {
		return startEntity(entity,
				managementManagementService.getDefaultAgentNode());
	}

	@Override
	public Entity startEntity(Entity entity, IAgentNode node) {
		if (entity == null)
			throw new NullPointerException("Entity entity must not be null");
		if (node == null)
			throw new NullPointerException("IAgentNode node must not be null");

		node.addAgent(entity.getAgent());

		return null;
	}

	@Override
	public Entity stopEntity(Entity entity) throws LifecycleException {
		entity.getAgent().stop();

		return entity;
	}

	@Override
	public void unloadEntity(Entity entity) throws LifecycleException {
		managedEntities.remove(entity.getId());
		IAgent agent = entity.getAgent();

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
	}

}
