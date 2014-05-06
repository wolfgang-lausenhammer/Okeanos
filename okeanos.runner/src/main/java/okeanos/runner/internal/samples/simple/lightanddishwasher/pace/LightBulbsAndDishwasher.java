package okeanos.runner.internal.samples.simple.lightanddishwasher.pace;

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
import okeanos.runner.internal.samples.simple.lightanddishwasher.pace.beans.DishwasherBean;
import okeanos.runner.internal.samples.simple.lightanddishwasher.pace.beans.LightBulbBean;
import okeanos.spring.misc.stereotypes.Logging;

import org.joda.time.Days;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * Sample that demonstrates a set of light bulbs and dishwashers that report
 * their schedule and try to find an equilibrium.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
public class LightBulbsAndDishwasher {

	/** The Constant PACE. */
	public static final int PACE = 4000;

	/** The Constant PLANNING_INTERVAL. */
	public static final int PLANNING_INTERVAL = Days.ONE.toStandardSeconds()
			.getSeconds();

	/** The logger. */
	@Logging
	private Logger log;

	/** The time service. */
	private TimeService timeService;

	/**
	 * Instantiates a new light bulbs and dishwasher.
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
	 * @param dishwasherBeanProvider
	 *            the dishwasher bean provider
	 * @param controlServicesProvider
	 *            the control services provider
	 * @throws LifecycleException
	 *             if the entity faces any troubles while starting its agent up
	 */
	@Inject
	public LightBulbsAndDishwasher(
			final PlatformManagementService platformManagementService,
			final EntityManagementService entityManagementService,
			final GroupManagementService groupManagementService,
			final TimeService timeService,
			final Provider<LightBulbBean> lightBulbBeanProvider,
			final Provider<DishwasherBean> dishwasherBeanProvider,
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
		Entity dishwasherEntity1 = startEntity(entityManagementService, node,
				"dishwasher-1");
		Entity dishwasherEntity2 = startEntity(entityManagementService, node,
				"dishwasher-2");

		Group group = groupManagementService.loadGroup();
		groupManagementService.startGroup(group);

		lightBulbEntity1.joinGroup(group);
		lightBulbEntity2.joinGroup(group);
		lightBulbEntity3.joinGroup(group);
		dishwasherEntity1.joinGroup(group);
		dishwasherEntity2.joinGroup(group);

		this.timeService.setPace(PACE);

		lightBulbEntity1.addFunctionality(lightBulbBeanProvider.get());
		lightBulbEntity2.addFunctionality(lightBulbBeanProvider.get());
		lightBulbEntity3.addFunctionality(lightBulbBeanProvider.get());
		dishwasherEntity1.addFunctionality(dishwasherBeanProvider.get());
		dishwasherEntity2.addFunctionality(dishwasherBeanProvider.get());

		lightBulbEntity1.addFunctionality(controlServicesProvider
				.getNewScheduleHandlerServiceAgentBean());
		lightBulbEntity2.addFunctionality(controlServicesProvider
				.getNewScheduleHandlerServiceAgentBean());
		lightBulbEntity3.addFunctionality(controlServicesProvider
				.getNewScheduleHandlerServiceAgentBean());
		dishwasherEntity1.addFunctionality(controlServicesProvider
				.getNewScheduleHandlerServiceAgentBean());
		dishwasherEntity2.addFunctionality(controlServicesProvider
				.getNewScheduleHandlerServiceAgentBean());
	}
}
