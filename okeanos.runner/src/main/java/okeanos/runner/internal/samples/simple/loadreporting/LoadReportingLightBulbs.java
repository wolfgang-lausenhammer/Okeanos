package okeanos.runner.internal.samples.simple.loadreporting;

import static okeanos.runner.internal.samples.misc.startup.StartUpHelper.startAgentNode;
import static okeanos.runner.internal.samples.misc.startup.StartUpHelper.startEntity;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.control.services.agentbeans.provider.ControlServicesProvider;
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
 * Sample that demonstartes a set of light bulbs that report their schedule and
 * try to find an equilibrium.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
public class LoadReportingLightBulbs {

	/** The Constant PACE. */
	private static final int PACE = 10;

	/** The logger. */
	@Logging
	private Logger log;

	/** The time service. */
	private TimeService timeService;

	/**
	 * Instantiates a new load reporting light bulbs.
	 * 
	 * @param platformManagementService
	 *            the platform management service
	 * @param entityManagementService
	 *            the entity management service
	 * @param groupManagementService
	 *            the group management service
	 * @param timeService
	 *            the time service
	 * @param lightBulbBeanProvider
	 *            the bean provider
	 * @param controlServicesProvider
	 *            the control services provider
	 * @throws LifecycleException
	 *             if the entity faces any troubles while starting its agent up
	 */
	@Inject
	public LoadReportingLightBulbs(
			final PlatformManagementService platformManagementService,
			final EntityManagementService entityManagementService,
			final GroupManagementService groupManagementService,
			final TimeService timeService,
			final Provider<LightBulbBean> lightBulbBeanProvider,
			final ControlServicesProvider controlServicesProvider)
			throws LifecycleException {
		this.timeService = timeService;

		IAgentNode node = startAgentNode(platformManagementService);
		Entity lightBulbEntity1 = startEntity(entityManagementService, node,
				"light-bulb-1");
		Entity lightBulbEntity2 = startEntity(entityManagementService, node,
				"light-bulb-2");
		Entity lightBulbEntity3 = startEntity(entityManagementService, node,
				"light-bulb-3");

		Group group = groupManagementService.loadGroup();
		groupManagementService.startGroup(group);

		lightBulbEntity1.joinGroup(group);
		lightBulbEntity2.joinGroup(group);
		lightBulbEntity3.joinGroup(group);

		this.timeService.setPace(PACE);

		lightBulbEntity1.addFunctionality(lightBulbBeanProvider.get());
		lightBulbEntity2.addFunctionality(lightBulbBeanProvider.get());
		lightBulbEntity3.addFunctionality(lightBulbBeanProvider.get());

		lightBulbEntity1.addFunctionality(controlServicesProvider
				.getNewScheduleHandlerServiceAgentBean());
		lightBulbEntity2.addFunctionality(controlServicesProvider
				.getNewScheduleHandlerServiceAgentBean());
		lightBulbEntity3.addFunctionality(controlServicesProvider
				.getNewScheduleHandlerServiceAgentBean());
	}
}
