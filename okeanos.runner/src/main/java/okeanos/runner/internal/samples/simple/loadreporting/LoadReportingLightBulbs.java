package okeanos.runner.internal.samples.simple.loadreporting;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.data.services.TimeService;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;
import okeanos.runner.internal.samples.simple.loadreporting.beans.LightBulbBean;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * The Class LoadReportingLightBulbs.
 */
@Component
public class LoadReportingLightBulbs {

	/** The Constant PACE. */
	private static final int PACE = 10;

	/** The logger. */
	@Logging
	private Logger log;

	/** The entity management service. */
	private EntityManagementService entityManagementService;

	/** The platform management service. */
	private PlatformManagementService platformManagementService;

	/** The time service. */
	private TimeService timeService;

	/**
	 * Instantiates a new load reporting light bulbs.
	 * 
	 * @param platformManagementService
	 *            the platform management service
	 * @param entityManagementService
	 *            the entity management service
	 * @param timeService
	 *            the time service
	 * @param beanProvider
	 *            the bean provider
	 * @throws LifecycleException
	 *             if the entity faces any troubles while starting its agent up
	 */
	@Inject
	public LoadReportingLightBulbs(
			final PlatformManagementService platformManagementService,
			final EntityManagementService entityManagementService,
			final TimeService timeService,
			final Provider<LightBulbBean> beanProvider)
			throws LifecycleException {
		this.platformManagementService = platformManagementService;
		this.entityManagementService = entityManagementService;
		this.timeService = timeService;

		IAgentNode node = startAgentNode();
		Entity lightBulbEntity1 = startEntity(node);
		Entity lightBulbEntity2 = startEntity(node);

		lightBulbEntity1.addFunctionality(beanProvider.get());
		lightBulbEntity2.addFunctionality(beanProvider.get());

		this.timeService.setPace(PACE);
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
