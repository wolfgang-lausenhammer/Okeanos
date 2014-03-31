package okeanos.runner.internal.samples.twoagents;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.data.services.PricingService;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;
import okeanos.runner.internal.samples.twoagents.beans.PingBean;
import okeanos.runner.internal.samples.twoagents.beans.PongBean;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
public class TwoAgents {
	@Logging
	private Logger log;

	private EntityManagementService entityManagementService;

	private PlatformManagementService platformManagementService;

	@Inject
	private PricingService pricingService;

	@Inject
	public TwoAgents(PlatformManagementService platformManagementService,
			EntityManagementService entityManagementService,
			Provider<PingBean> pingBeanProvider,
			Provider<PongBean> pongBeanProvider) throws LifecycleException {
		this.platformManagementService = platformManagementService;
		this.entityManagementService = entityManagementService;

		IAgentNode node = startAgentNode();
		Entity entity1 = startEntity(node);
		Entity entity2 = startEntity(node);

		entity1.getAgent().setAgentName("PingAgent");
		entity2.getAgent().setAgentName("PongAgent");

		if (log != null)
			log.debug(
					"Adding Ping&Pong functionality to entities [entity 1: {}, entity 2: {}]",
					entity1, entity2);

		if (log != null)
			log.debug("Adding Ping functionality to entity [{}]", entity1);
		entity1.addFunctionality(pingBeanProvider.get());
		if (log != null)
			log.debug("Finished adding Ping functionality to entity [{}]",
					entity1);

		if (log != null)
			log.debug("Adding Pong functionality to entity [{}]", entity2);
		entity2.addFunctionality(pongBeanProvider.get());
		if (log != null)
			log.debug("Finished adding Pong functionality to entity [{}]",
					entity2);
	}

	private IAgentNode startAgentNode() throws LifecycleException {
		if (log != null)
			log.debug("Starting Agent Node");
		IAgentNode node = platformManagementService.startAgentNode();
		if (log != null)
			log.debug("Finished starting Agent Node [{}]", node);

		return node;
	}

	private Entity startEntity(IAgentNode node) throws LifecycleException {
		if (log != null)
			log.debug("Starting Entity on Agent Node [{}]", node);
		Entity entity = entityManagementService.loadEntity();
		entityManagementService.startEntity(entity, node);
		if (log != null)
			log.debug("Finished starting Entity [{}] on Agent Node[{}]",
					entity, node);

		return entity;
	}
}
