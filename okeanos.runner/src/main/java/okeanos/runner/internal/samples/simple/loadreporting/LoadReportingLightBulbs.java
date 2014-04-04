package okeanos.runner.internal.samples.simple.loadreporting;

import static okeanos.runner.internal.samples.misc.startup.StartUpHelper.startAgentNode;
import static okeanos.runner.internal.samples.misc.startup.StartUpHelper.startEntity;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.core.entities.Group;
import okeanos.data.services.TimeService;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.GroupManagementService;
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

	private GroupManagementService groupManagementService;

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
			final GroupManagementService groupManagementService,
			final TimeService timeService,
			final Provider<LightBulbBean> beanProvider)
			throws LifecycleException {
		this.platformManagementService = platformManagementService;
		this.entityManagementService = entityManagementService;
		this.groupManagementService = groupManagementService;
		this.timeService = timeService;

		IAgentNode node = startAgentNode(platformManagementService);
		Entity lightBulbEntity1 = startEntity(entityManagementService, node,
				"light-bulb-1");
		Entity lightBulbEntity2 = startEntity(entityManagementService, node,
				"light-bulb-2");

		Group group = groupManagementService.loadGroup();
		groupManagementService.startGroup(group);
		
		lightBulbEntity1.joinGroup(group);
		lightBulbEntity2.joinGroup(group);

		this.timeService.setPace(PACE);

		lightBulbEntity1.addFunctionality(beanProvider.get());
		lightBulbEntity2.addFunctionality(beanProvider.get());
	}
}
