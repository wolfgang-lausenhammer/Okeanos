package okeanos.runner.internal.samples.twoagents;

import static okeanos.runner.internal.samples.misc.startup.StartUpHelper.startAgentNode;
import static okeanos.runner.internal.samples.misc.startup.StartUpHelper.startEntity;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;
import okeanos.runner.internal.samples.twoagents.beans.PingBean;
import okeanos.runner.internal.samples.twoagents.beans.PongBean;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * Provides a simple example of two agents interacting with each other via
 * ping-pongs. Uses synchronous sending and a callback for the receiver.
 */
@Component
public class TwoAgents {

	/** The logger. */
	@Logging
	private Logger log;

	/** The entity management service. */
	private EntityManagementService entityManagementService;

	/** The platform management service. */
	private PlatformManagementService platformManagementService;

	/**
	 * Instantiates a new two agents.
	 * 
	 * @param platformManagementService
	 *            the platform management service
	 * @param entityManagementService
	 *            the entity management service
	 * @param pingBeanProvider
	 *            the ping bean provider
	 * @param pongBeanProvider
	 *            the pong bean provider
	 * @throws LifecycleException
	 *             if the entity faces any troubles while starting its agent up
	 */
	@Inject
	public TwoAgents(final PlatformManagementService platformManagementService,
			final EntityManagementService entityManagementService,
			final Provider<PingBean> pingBeanProvider,
			final Provider<PongBean> pongBeanProvider)
			throws LifecycleException {
		this.platformManagementService = platformManagementService;
		this.entityManagementService = entityManagementService;

		IAgentNode node = startAgentNode(platformManagementService);
		Entity entity1 = startEntity(entityManagementService, node, "ping-agent");
		Entity entity2 = startEntity(entityManagementService, node, "pong-agent");

		if (log != null) {
			log.debug(
					"Adding Ping&Pong functionality to entities [entity 1: {}, entity 2: {}]",
					entity1, entity2);
		}

		if (log != null) {
			log.debug("Adding Ping functionality to entity [{}]", entity1);
		}
		entity1.addFunctionality(pingBeanProvider.get());
		if (log != null) {
			log.debug("Finished adding Ping functionality to entity [{}]",
					entity1);
		}

		if (log != null) {
			log.debug("Adding Pong functionality to entity [{}]", entity2);
		}
		entity2.addFunctionality(pongBeanProvider.get());
		if (log != null) {
			log.debug("Finished adding Pong functionality to entity [{}]",
					entity2);
		}
	}
}
