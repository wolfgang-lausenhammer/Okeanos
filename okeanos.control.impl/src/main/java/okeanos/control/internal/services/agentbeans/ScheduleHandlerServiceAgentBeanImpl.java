package okeanos.control.internal.services.agentbeans;

import static okeanos.data.services.agentbeans.CommunicationServiceAgentBean.Header.COMMUNICATION_SENDER;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
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
import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;
import okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback;
import okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback;
import okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback;
import okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback;
import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.entities.MessageScope;
import okeanos.math.regression.LargeSerializableConcurrentSkipListMap;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;

import aQute.bnd.annotation.component.Component;
import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;

@Component
@Scope("prototype")
public class ScheduleHandlerServiceAgentBeanImpl extends
		AbstractMethodExposingBean implements ScheduleHandlerServiceAgentBean {
	private static final Logger LOG = LoggerFactory
			.getLogger(ScheduleHandlerServiceAgentBeanImpl.class);
	private SchedulesReceivedCallback schedulesReceivedCallback;
	private PossibleRunsCallback possibleRunsCallback;
	private OptimizedRunsCallback optimizedRunsCallback;
	private EquilibriumFoundCallback equilibriumFoundCallback;
	private ControlAlgorithm controlAlgorithm;
	private State state;
	private IActionDescription actionBroadcast;

	private ScheduleMessageHandler scheduleMessageHandlerCallback;

	public TaskScheduler taskScheduler;
	public Random random;
	public ScheduleUtil scheduleUtil;
	public Schedule latestAnnouncedSchedule;
	public ControlEntitiesProvider controlEntitiesProvider;
	public List<OptimizedRun> latestOptimizedRuns;

	@Inject
	public ScheduleHandlerServiceAgentBeanImpl(ScheduleUtil scheduleUtil,
			ControlEntitiesProvider controlEntitiesProvider) {
		this.scheduleUtil = scheduleUtil;
		this.controlEntitiesProvider = controlEntitiesProvider;
		state = State.STOPPED;
	}

	@Override
	public void doStart() throws Exception {
		super.doStart();

		IActionDescription template = new Action(
				CommunicationServiceAgentBean.ACTION_BROADCAST);
		actionBroadcast = memory.read(template);
		if (actionBroadcast == null) {
			actionBroadcast = thisAgent.searchAction(template);
		}

		scheduleMessageHandlerCallback = new ScheduleMessageHandler();

		LOG.debug(
				"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
				state, State.WAITING_FOR_SCHEDULES);
		state = State.WAITING_FOR_SCHEDULES;
	}

	@Override
	public void doInit() throws Exception {
		super.doInit();

		setExecutionInterval(0); // make passive
	}

	@Override
	public void registerOtherSchedulesArrivedCallback(
			final SchedulesReceivedCallback schedulesReceivedCallback) {
		this.schedulesReceivedCallback = schedulesReceivedCallback;
	}

	@Override
	public void registerPossibleRunsCallback(
			final PossibleRunsCallback possibleRunsCallback) {
		this.possibleRunsCallback = possibleRunsCallback;
	}

	@Override
	public void registerOptimizedRunsCallback(
			final OptimizedRunsCallback optimizedRunsCallback) {
		this.optimizedRunsCallback = optimizedRunsCallback;
	}

	@Override
	public void registerEquilibriumFoundCallback(
			final EquilibriumFoundCallback equilibriumFoundCallback) {
		this.equilibriumFoundCallback = equilibriumFoundCallback;
	}

	@Override
	public void reset(final boolean cancelRunningOperation) {

	}

	@Override
	public void setControlAlgorithm(final ControlAlgorithm controlAlgorithm) {
		this.controlAlgorithm = controlAlgorithm;
	}

	private class ScheduleMessageHandler implements SpaceObserver<IFact> {
		public ScheduledFuture scheduledBroadcast;

		public ScheduleMessageHandler() {
			// get action to register callback
			IActionDescription template = new Action(
					CommunicationServiceAgentBean.ACTION_RECEIVE_MESSAGE_CALLBACK_IFACT);
			IActionDescription actionReceiveMessageCallbackIfact = memory
					.read(template);
			if (actionReceiveMessageCallbackIfact == null) {
				actionReceiveMessageCallbackIfact = thisAgent
						.searchAction(template);
			}

			Schedule schedule = new ScheduleImpl(null);

			// register this handler as a receiver for ScheduleImpls
			invoke(actionReceiveMessageCallbackIfact, new Serializable[] {
					this, schedule });

			scheduledBroadcast = taskScheduler.schedule(
					new ScheduleMessageBroadcaster(null),
					new DateTime(System.currentTimeMillis()).plusMillis(
							random.nextInt(2500)).toDate());
		}

		@SuppressWarnings("unchecked")
		@Override
		public void notify(final SpaceEvent<? extends IFact> event) {
			LOG.trace("{} - [event={}]", thisAgent.getAgentName(), event);
			if (event instanceof WriteCallEvent<?>) {
				WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;

				// consume message
				IJiacMessage message = memory.remove(wce.getObject());

				// check if message was sent by current agent, if so, ignore it
				if (thisAgent.getAgentDescription().getMessageBoxAddress()
						.toString()
						.equals(message.getHeader(COMMUNICATION_SENDER))) {
					return;
				}

				LOG.debug("{} - Broadcast Message received.",
						thisAgent.getAgentName());
				LOG.debug(
						"{} - Cancelling current announce schedule and schedule new one.",
						thisAgent.getAgentName());

				Schedule latestSchedule = (Schedule) message.getPayload();

				LOG.debug(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						state, State.CALLING_SCHEDULE_CALLBACK);
				state = State.CALLING_SCHEDULE_CALLBACK;
				latestSchedule = schedulesReceivedCallback
						.schedulesReceivedCallback(latestSchedule,
								latestOptimizedRuns);

				LOG.debug("{} - scheduling a task to broadcast new schedule.",
						thisAgent.getAgentName());

				scheduledBroadcast = taskScheduler.schedule(
						new ScheduleMessageBroadcaster(latestSchedule),
						new DateTime(System.currentTimeMillis()).plusMillis(
								random.nextInt(2500)).toDate());

				LOG.debug(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						state, State.WAITING_FOR_RANDOM_TIME_BEFORE_SEND);
				state = State.WAITING_FOR_RANDOM_TIME_BEFORE_SEND;
			}
		}
	}

	private class ScheduleMessageBroadcaster implements Runnable {
		private Schedule latestReceivedSchedule;

		public ScheduleMessageBroadcaster(Schedule latestSchedule) {
			this.latestReceivedSchedule = latestSchedule;
		}

		@Override
		public void run() {
			LOG.debug("{} - ScheduleMessageBroadcaster execute() called",
					thisAgent.getAgentName());

			if (latestOptimizedRuns == null) {
				latestOptimizedRuns = new LinkedList<>();
			}
			if (latestReceivedSchedule == null) {
				latestReceivedSchedule = controlEntitiesProvider
						.getNewSchedule();
				latestReceivedSchedule
						.setSchedule(new LargeSerializableConcurrentSkipListMap<DateTime, Slot>());
			}

			// calculating schedules of other devices (=all schedules - own schedule)
			Schedule lastOptimizedSchedule = scheduleUtil
					.toSchedule(latestOptimizedRuns);
			Schedule latestReceivedScheduleMinusLatestOptimizedSchedule = scheduleUtil
					.minus(latestReceivedSchedule, lastOptimizedSchedule);
			LOG.debug(
					"{} - subtracted lastOptimizedSchedule from latestReceivedSchedule to get demand from others.",
					thisAgent.getAgentName(), latestReceivedSchedule);

			LOG.debug(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					state, State.CALLING_POSSIBLE_RUNS_CALLBACK);
			state = State.CALLING_POSSIBLE_RUNS_CALLBACK;
			List<PossibleRun> possibleRunsToday = possibleRunsCallback
					.getPossibleRuns();

			Configuration configuration = controlEntitiesProvider
					.getNewConfiguration();
			configuration.setPossibleRun(possibleRunsToday);
			configuration
					.setSchedule(latestReceivedScheduleMinusLatestOptimizedSchedule);

			// optimize schedule
			LOG.debug(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					state, State.OPTIMIZING_SCHEDULE);
			state = State.OPTIMIZING_SCHEDULE;
			List<OptimizedRun> optimizedRuns = controlAlgorithm
					.findBestConfiguration(configuration);

			// allow agent bean to correct optimized runs
			LOG.debug(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					state, State.CALLING_OPTIMIZED_RUNS_CALLBACK);
			state = State.CALLING_OPTIMIZED_RUNS_CALLBACK;
			optimizedRuns = optimizedRunsCallback
					.optimizedRunsCallback(optimizedRuns);

			Schedule schedule = scheduleUtil.toSchedule(optimizedRuns);
			if (scheduleUtil.compare(lastOptimizedSchedule, schedule) == 0) {
				// own schedule not changed despite new schedule from others
				// do not announce new schedule!
				LOG.info("{} - Schedule remained unchanged.",
						thisAgent.getAgentName());
				LOG.trace("{}\n{}", thisAgent.getAgentName(), StringUtils.join(
						latestReceivedSchedule.getSchedule().entrySet(), '\n'));
				
				// do something if all devices found their best schedules
				return;
			}

			schedule = scheduleUtil.plus(
					latestReceivedScheduleMinusLatestOptimizedSchedule,
					schedule);

			LOG.debug(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					state, State.SENDING_SCHEDULE);
			state = State.SENDING_SCHEDULE;

			LOG.debug("{} - Announcing new optimized schedule",
					thisAgent.getAgentName());
			LOG.trace("{}\n{}", thisAgent.getAgentName(),
					StringUtils.join(schedule.getSchedule().entrySet(), '\n'));

			invoke(actionBroadcast, new Serializable[] { MessageScope.GROUP,
					schedule });
			LOG.debug("{} - Announced schedule", thisAgent.getAgentName());

			latestAnnouncedSchedule = schedule;
			latestOptimizedRuns = optimizedRuns;

			LOG.debug(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					state, State.WAITING_FOR_SCHEDULES);
			state = State.WAITING_FOR_SCHEDULES;
		}
	}

	private enum State {
		STOPPED, WAITING_FOR_SCHEDULES, CALLING_SCHEDULE_CALLBACK, WAITING_FOR_RANDOM_TIME_BEFORE_SEND, CALLING_POSSIBLE_RUNS_CALLBACK, OPTIMIZING_SCHEDULE, CALLING_OPTIMIZED_RUNS_CALLBACK, SENDING_SCHEDULE, EQUILIBRIUM_REACHED
	}

	@Override
	public boolean isEquilibriumReached() {
		return State.EQUILIBRIUM_REACHED.equals(state);
	}
}
