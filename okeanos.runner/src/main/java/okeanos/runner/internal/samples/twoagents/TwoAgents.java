package okeanos.runner.internal.samples.twoagents;

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

		IAgentNode node = startAgentNode();
		Entity entity1 = startEntity(node);
		Entity entity2 = startEntity(node);

		entity1.getAgent().setAgentName("PingAgent");
		entity2.getAgent().setAgentName("PongAgent");

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

	/**
	 * Start an agent node.
	 * 
	 * @return the agent node
	 * @throws LifecycleException
	 *             if the agent node faces any problems while starting up
	 */
	private IAgentNode startAgentNode() throws LifecycleException {
		if (log != null) {
			log.debug("Starting Agent Node");
		}
		IAgentNode node = platformManagementService.startAgentNode();
		if (log != null) {
			log.debug("Finished starting Agent Node [{}]", node);
		}

		return node;
	}

	/**
	 * Start an entity.
	 * 
	 * @param node
	 *            the node on which the entity should be started on
	 * @return the entity
	 * @throws LifecycleException
	 *             if the entity faces any problems while starting its agent up
	 */
	private Entity startEntity(final IAgentNode node) throws LifecycleException {
		if (log != null) {
			log.debug("Starting Entity on Agent Node [{}]", node);
		}
		Entity entity = entityManagementService.loadEntity();
		entityManagementService.startEntity(entity, node);
		if (log != null) {
			log.debug("Finished starting Entity [{}] on Agent Node[{}]",
					entity, node);
		}

		return entity;
	}
}
