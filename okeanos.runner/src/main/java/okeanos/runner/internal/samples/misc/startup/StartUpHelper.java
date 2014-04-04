package okeanos.runner.internal.samples.misc.startup;

import okeanos.core.entities.Entity;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * Provides utility functions to start up a platform and start entities.
 */
public final class StartUpHelper {

	/** The Logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(StartUpHelper.class);

	/**
	 * Start an agent node.
	 * 
	 * @param platformManagementService
	 *            the platform management service
	 * @return the agent node
	 * @throws LifecycleException
	 *             if the agent node faces any problems while starting up
	 */
	public static IAgentNode startAgentNode(
			final PlatformManagementService platformManagementService)
			throws LifecycleException {
		if (LOG != null) {
			LOG.debug("Starting Agent Node");
		}
		IAgentNode node = platformManagementService.startAgentNode();
		if (LOG != null) {
			LOG.debug("Finished starting Agent Node [{}]", node);
		}

		return node;
	}

	/**
	 * Start an entity.
	 * 
	 * @param entityManagementService
	 *            the entity management service
	 * @param node
	 *            the node on which the entity should be started on
	 * @param agentName
	 *            the agent name
	 * @return the entity
	 * @throws LifecycleException
	 *             if the entity faces any problems while starting its agent up
	 */
	public static Entity startEntity(
			final EntityManagementService entityManagementService,
			final IAgentNode node, final String agentName)
			throws LifecycleException {
		if (LOG != null) {
			LOG.debug("Starting Entity on Agent Node [{}]", node);
		}
		Entity entity = entityManagementService.loadEntity();
		entity.getAgent().setAgentName(agentName);
		entityManagementService.startEntity(entity, node);
		if (LOG != null) {
			LOG.debug("Finished starting Entity [{}] on Agent Node[{}]",
					entity, node);
		}

		return entity;
	}

	/**
	 * Private constructor to prevent instances.
	 */
	private StartUpHelper() {
	}
}
