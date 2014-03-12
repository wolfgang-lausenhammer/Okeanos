package okeanos.runner.internal.samples.twoagents;

import javax.inject.Inject;

import okeanos.core.entities.Entity;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
public class TwoAgents {
	private static final Logger log = LoggerFactory.getLogger(TwoAgents.class);

	private EntityManagementService entityManagementService;

	private PlatformManagementService platformManagementService;

	@Inject
	public TwoAgents(PlatformManagementService platformManagementService,
			EntityManagementService entityManagementService)
			throws LifecycleException {
		this.platformManagementService = platformManagementService;
		this.entityManagementService = entityManagementService;

		IAgentNode node = startAgentNode();
		Entity entity1 = startEntity(node);
		Entity entity2 = startEntity(node);

		log.debug(
				"Adding Hello World functionality to entities [entity 1: {}, entity 2: {}]",
				entity1, entity2);
	}

	private IAgentNode startAgentNode() throws LifecycleException {
		log.debug("Starting Agent Node");
		IAgentNode node = platformManagementService.startAgentNode();
		log.debug("Finished starting Agent Node [{}]", node);

		return node;
	}

	private Entity startEntity(IAgentNode node) throws LifecycleException {
		log.debug("Starting Entity on Agent Node [{}]", node);
		Entity entity = entityManagementService.loadEntity();
		entityManagementService.startEntity(entity, node);
		log.debug("Finished starting Entity [{}] on Agent Node[{}]", entity,
				node);

		return entity;
	}
}
