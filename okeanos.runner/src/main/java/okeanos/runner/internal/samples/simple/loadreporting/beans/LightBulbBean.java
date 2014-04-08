package okeanos.runner.internal.samples.simple.loadreporting.beans;

import static okeanos.data.services.agentbeans.CommunicationServiceAgentBean.Header.COMMUNICATION_SENDER;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import javax.inject.Inject;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.control.entities.utilities.ScheduleUtil;
import okeanos.data.services.TimeService;
import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.entities.MessageScope;
import okeanos.math.regression.LargeSerializableConcurrentSkipListMap;
import okeanos.model.entities.Load;
import okeanos.runner.internal.samples.twoagents.beans.entities.Ping;
import okeanos.spring.misc.stereotypes.Logging;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;

/**
 * The light bulb bean that provides the main action to the light bulb agent.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class LightBulbBean extends AbstractAgentBean implements
		SpaceObserver<IFact> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5311309436889351318L;

	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 8000;

	private static final Logger LOG = LoggerFactory
			.getLogger(LightBulbBean.class);

	/** The log. */
	@Logging
	private Logger log = LoggerFactory.getLogger(LightBulbBean.class);

	/** The communication action to broadcast messages. */
	private IActionDescription actionBroadcast;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The light bulb model. */
	private Load lightBulb;

	/** The action send async options. */
	private IActionDescription actionSendAsyncOptions;

	/** The my last announced schedule. */
	private Schedule myLastAnnouncedSchedule;

	/** The latest schedule. */
	private Schedule latestSchedule;

	/** The my last optimized runs. */
	private List<OptimizedRun> myLastOptimizedRuns;

	/** The last change. */
	private DateTime lastChange;

	/** The time service. */
	private TimeService timeService;

	/** The task scheduler. */
	private TaskScheduler taskScheduler;

	/** The possible runs today. */
	private List<PossibleRun> possibleRunsToday;

	/** The next scheduled announce schedule. */
	private ScheduledFuture nextScheduledAnnounceSchedule;

	/** The random. */
	private Random random;

	/** The control algorithm. */
	private ControlAlgorithm controlAlgorithm;

	private ScheduleUtil scheduleUtil;

	/**
	 * Instantiates a new light bulb bean.
	 * 
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 * @param lightBulb
	 *            the light bulb
	 * @param timeService
	 *            the time service
	 * @param controlAlgorithm
	 *            the control algorithm
	 * @param taskScheduler
	 *            the task scheduler
	 */
	@Inject
	public LightBulbBean(final ControlEntitiesProvider controlEntitiesProvider,
			@Qualifier("lightBulb100W") final Load lightBulb,
			final TimeService timeService,
			final ControlAlgorithm controlAlgorithm,
			final TaskScheduler taskScheduler) {
		this.controlEntitiesProvider = controlEntitiesProvider;
		this.lightBulb = lightBulb;
		this.timeService = timeService;
		this.controlAlgorithm = controlAlgorithm;
		this.taskScheduler = taskScheduler;
		this.scheduleUtil = new ScheduleUtil(controlEntitiesProvider);
		this.setExecutionInterval(EXECUTION_INTERVAL);
		random = new Random();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.AbstractAgentBean#doStart()
	 */
	@Override
	public void doStart() throws Exception {
		super.doStart();

		IActionDescription template = new Action(
				CommunicationServiceAgentBean.ACTION_BROADCAST);
		actionBroadcast = memory.read(template);
		if (actionBroadcast == null) {
			actionBroadcast = thisAgent.searchAction(template);
		}

		template = new Action(
				CommunicationServiceAgentBean.ACTION_SEND_ASYNC_OPTIONS);
		actionSendAsyncOptions = memory.read(template);
		if (actionSendAsyncOptions == null) {
			actionSendAsyncOptions = thisAgent.searchAction(template);
		}

		template = new Action(
				CommunicationServiceAgentBean.ACTION_RECEIVE_MESSAGE_CALLBACK_IFACT);
		IActionDescription actionReceiveMessageCallbackIfact = memory
				.read(template);
		if (actionReceiveMessageCallbackIfact == null) {
			actionReceiveMessageCallbackIfact = thisAgent
					.searchAction(template);
		}
		Schedule schedule = new ScheduleImpl(null);
		invoke(actionReceiveMessageCallbackIfact, new Serializable[] { this,
				schedule });

		if (lastChange != null) {
			lastChange = new DateTime(timeService.currentTimeMillis());
		}

	}

	/**
	 * The actual work happens here. Called once every
	 * {@link LightBulbBean#EXECUTION_INTERVAL} to get ready for the next
	 * iteration.
	 */
	@Override
	public void execute() {
		log.info("{} - LightBulbBean execute() called",
				thisAgent.getAgentName());
		possibleRunsToday = lightBulb.getPossibleRuns();

		// Next schedule earliest in 1s and then randomly distributed within the
		// next 5s so that not all device announce their schedule at the same
		// time. If another device announced its schedule before this, the task
		// gets cancelled and a new task will be scheduled see #notify.
		nextScheduledAnnounceSchedule = taskScheduler.schedule(
				new AnnounceSchedule(),
				new DateTime(System.currentTimeMillis()).plusSeconds(1)
						.plusMillis(random.nextInt(2500)).toDate());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sercho.masp.space.event.SpaceObserver#notify(org.sercho.masp.space
	 * .event.SpaceEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void notify(final SpaceEvent<? extends IFact> event) {
		log.trace("{} - [event={}]", thisAgent.getAgentName(), event);
		if (event instanceof WriteCallEvent<?>) {
			WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;

			// consume message
			IJiacMessage message = memory.remove(wce.getObject());

			// check if message was sent by current agent, if so, ignore it
			if (thisAgent.getAgentDescription().getMessageBoxAddress()
					.toString().equals(message.getHeader(COMMUNICATION_SENDER))) {
				return;
			}

			if (log != null) {
				log.info("{} - Broadcast Message received.",
						thisAgent.getAgentName());
				log.info(
						"{} - Cancelling current announce schedule and schedule new one.",
						thisAgent.getAgentName());
			}

			nextScheduledAnnounceSchedule.cancel(true);

			latestSchedule = (Schedule) message.getPayload();

			if (log != null) {
				log.trace("{} - doing something with the message",
						thisAgent.getAgentName());
				log.trace("{}\n{}", thisAgent.getAgentName(), StringUtils.join(
						latestSchedule.getSchedule().entrySet(), '\n'));
			}

			nextScheduledAnnounceSchedule = taskScheduler.schedule(
					new AnnounceSchedule(),
					new DateTime(System.currentTimeMillis()).plusSeconds(1)
							.plusMillis(random.nextInt(2500)).toDate());
		}
	}

	/**
	 * The Class AnnounceSchedule.
	 */
	private class AnnounceSchedule implements Runnable {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (log != null) {
				log.info("{} - {} - AnnounceSchedule execute() called",
						thisAgent.getAgentName(), DateTime.now());
			}

			if (latestSchedule == null
					|| scheduleUtil.compare(latestSchedule,
							myLastAnnouncedSchedule) != 0) {
				// first time call or schedule changed
				// optimize my own schedule with the new information
				if (myLastOptimizedRuns == null) {
					myLastOptimizedRuns = new LinkedList<>();
				}
				if (latestSchedule == null) {
					latestSchedule = controlEntitiesProvider.getNewSchedule();
					latestSchedule
							.setSchedule(new LargeSerializableConcurrentSkipListMap<DateTime, Slot>());
				}

				Schedule lastOptimizedSchedule = scheduleUtil
						.toSchedule(myLastOptimizedRuns);
				Schedule latestScheduleMinusMyLastAnnouncedSchedule = scheduleUtil
						.minus(latestSchedule, lastOptimizedSchedule);
				if (log != null) {
					log.info("{} - latestSchedule [schedule={}]",
							thisAgent.getAgentName(), latestSchedule);
				}

				Configuration configuration = controlEntitiesProvider
						.getNewConfiguration();
				configuration.setPossibleRun(possibleRunsToday);
				configuration
						.setSchedule(latestScheduleMinusMyLastAnnouncedSchedule);
				List<OptimizedRun> newLastOptimizedRuns = controlAlgorithm
						.findBestConfiguration(configuration);
				Schedule schedule = scheduleUtil
						.toSchedule(newLastOptimizedRuns);
				schedule = scheduleUtil.plus(
						latestScheduleMinusMyLastAnnouncedSchedule, schedule);

				if (log != null) {
					log.debug("{} - Announcing new optimized schedule",
							thisAgent.getAgentName());
					log.debug("{}\n{}", thisAgent.getAgentName(), StringUtils
							.join(schedule.getSchedule().entrySet(), '\n'));
				}
				invoke(actionBroadcast, new Serializable[] {
						MessageScope.GROUP, schedule });
				if (log != null) {
					log.info("{} - Announced schedule",
							thisAgent.getAgentName());
				}
				myLastAnnouncedSchedule = schedule;
				myLastOptimizedRuns = newLastOptimizedRuns;
			} else {
				// Schedule remained unchanged
				if (log != null) {
					log.info("{} - Schedule remained unchanged.",
							thisAgent.getAgentName());
					log.debug("{}\n{}", thisAgent.getAgentName(), StringUtils
							.join(latestSchedule.getSchedule().entrySet(), '\n'));
				}

				// do something if all devices found their best schedules
			}
		}
	}
}
