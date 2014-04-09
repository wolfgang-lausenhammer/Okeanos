package okeanos.control.internal.services.agentbeans;

import static okeanos.data.services.agentbeans.CommunicationServiceAgentBean.Header.COMMUNICATION_SENDER;

import java.io.Serializable;
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
import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;
import okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback;
import okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback;
import okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback;
import okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback;
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
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.action.ActionResult;
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

	private TaskScheduler taskScheduler;
	private ScheduleUtil scheduleUtil;
	private ControlEntitiesProvider controlEntitiesProvider;
	private List<OptimizedRun> latestOptimizedRuns;

	@Inject
	public ScheduleHandlerServiceAgentBeanImpl(
			final ControlEntitiesProvider controlEntitiesProvider,
			final TaskScheduler taskScheduler) {
		this.controlEntitiesProvider = controlEntitiesProvider;
		this.taskScheduler = taskScheduler;
		this.scheduleUtil = new ScheduleUtil(controlEntitiesProvider);
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

		ProxyCallbacks callbacks = new ProxyCallbacks();
		schedulesReceivedCallback = callbacks;
		possibleRunsCallback = callbacks;
		optimizedRunsCallback = callbacks;
		equilibriumFoundCallback = callbacks;
		controlAlgorithm = callbacks;

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
	public void reset(final boolean cancelRunningOperation) {

	}

	private class ScheduleMessageHandler implements SpaceObserver<IFact> {
		private static final long serialVersionUID = 5191791309923785942L;

		@SuppressWarnings("rawtypes")
		private ScheduledFuture scheduledBroadcast;
		private Random random;

		public ScheduleMessageHandler() {
			random = new Random();

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

			// Next schedule randomly distributed within the next 5s so that not
			// all device announce their schedule at the same time. If another
			// device announced its schedule before this, the task gets
			// cancelled and a new task will be scheduled see #notify.
			scheduledBroadcast = taskScheduler.schedule(
					new ScheduleMessageBroadcaster(null),
					new DateTime(System.currentTimeMillis()).plusMillis(
							random.nextInt(2500)).toDate());
		}

		@SuppressWarnings("unchecked")
		@Override
		public void notify(final SpaceEvent<? extends IFact> event) {
			if (isEquilibriumReached()) {
				return;
			}
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
				scheduledBroadcast.cancel(true);

				LOG.debug(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						thisAgent.getAgentName(), state,
						State.CALLING_SCHEDULE_CALLBACK);
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
						thisAgent.getAgentName(), state,
						State.WAITING_FOR_RANDOM_TIME_BEFORE_SEND);
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
			if (isEquilibriumReached()) {
				return;
			}
			LOG.debug("{} - ScheduleMessageBroadcaster execute() called",
					thisAgent.getAgentName());
			if (scheduledEquilibriumWaiter != null) {
				scheduledEquilibriumWaiter.cancel(false);
			}

			if (latestOptimizedRuns == null) {
				latestOptimizedRuns = new LinkedList<>();
			}
			if (latestReceivedSchedule == null) {
				latestReceivedSchedule = controlEntitiesProvider
						.getNewSchedule();
				latestReceivedSchedule
						.setSchedule(new LargeSerializableConcurrentSkipListMap<DateTime, Slot>());
			}

			// calculating schedules of other devices (=all schedules - own
			// schedule)
			Schedule latestOptimizedSchedule = scheduleUtil
					.toSchedule(latestOptimizedRuns);
			Schedule latestReceivedScheduleMinusLatestOptimizedSchedule = scheduleUtil
					.minus(latestReceivedSchedule, latestOptimizedSchedule);
			LOG.debug(
					"{} - subtracted lastOptimizedSchedule from latestReceivedSchedule to get demand from others.",
					thisAgent.getAgentName(), latestReceivedSchedule);

			LOG.debug(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					thisAgent.getAgentName(), state,
					State.CALLING_POSSIBLE_RUNS_CALLBACK);
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
					thisAgent.getAgentName(), state, State.OPTIMIZING_SCHEDULE);
			state = State.OPTIMIZING_SCHEDULE;
			List<OptimizedRun> optimizedRuns = controlAlgorithm
					.findBestConfiguration(configuration);

			// allow agent bean to correct optimized runs
			LOG.debug(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					thisAgent.getAgentName(), state,
					State.CALLING_OPTIMIZED_RUNS_CALLBACK);
			state = State.CALLING_OPTIMIZED_RUNS_CALLBACK;
			optimizedRuns = optimizedRunsCallback
					.optimizedRunsCallback(optimizedRuns);

			Schedule schedule = scheduleUtil.toSchedule(optimizedRuns);
			if (scheduleUtil.compare(latestOptimizedSchedule, schedule) == 0) {
				// own schedule not changed despite new schedule from others
				// do not announce new schedule!
				LOG.info("{} - Schedule remained unchanged.",
						thisAgent.getAgentName());
				LOG.debug("{}\n{}", thisAgent.getAgentName(), StringUtils.join(
						latestReceivedSchedule.getSchedule().entrySet(), '\n'));
			} else {

				schedule = scheduleUtil.plus(
						latestReceivedScheduleMinusLatestOptimizedSchedule,
						schedule);

				LOG.debug(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						thisAgent.getAgentName(), state, State.SENDING_SCHEDULE);
				state = State.SENDING_SCHEDULE;

				LOG.debug("{} - Announcing new optimized schedule",
						thisAgent.getAgentName());
				LOG.trace("{}\n{}", thisAgent.getAgentName(), StringUtils.join(
						schedule.getSchedule().entrySet(), '\n'));

				invoke(actionBroadcast, new Serializable[] {
						MessageScope.GROUP, schedule });
				LOG.debug("{} - Announced schedule", thisAgent.getAgentName());

				latestOptimizedRuns = optimizedRuns;
			}

			LOG.debug(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					thisAgent.getAgentName(), state,
					State.WAITING_FOR_SCHEDULES);
			state = State.WAITING_FOR_SCHEDULES;

			scheduledEquilibriumWaiter = taskScheduler.schedule(
					new EquilibriumWaiter(schedule, optimizedRuns),
					new DateTime(System.currentTimeMillis()).plusSeconds(5)
							.toDate());
		}

		@SuppressWarnings("rawtypes")
		private ScheduledFuture scheduledEquilibriumWaiter;

		private class EquilibriumWaiter implements Runnable {
			private Schedule latestSchedule;
			private List<OptimizedRun> optimizedRuns;

			public EquilibriumWaiter(Schedule latestSchedule,
					List<OptimizedRun> optimizedRuns) {
				this.latestSchedule = latestSchedule;
				this.optimizedRuns = optimizedRuns;
			}

			@Override
			public void run() {
				if (!State.WAITING_FOR_SCHEDULES.equals(state)) {
					return;
				}

				LOG.debug(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						thisAgent.getAgentName(), state,
						State.EQUILIBRIUM_REACHED);
				state = State.EQUILIBRIUM_REACHED;

				// do something if all devices found their best schedules
				// schedule task in a few seconds, that will call
				// equilibriumFoundCallback to notify bean of the completion and
				// the final schedule
				equilibriumFoundCallback.equilibrium(latestSchedule,
						optimizedRuns);
			}
		}
	}

	private enum State {
		STOPPED, WAITING_FOR_SCHEDULES, CALLING_SCHEDULE_CALLBACK, WAITING_FOR_RANDOM_TIME_BEFORE_SEND, CALLING_POSSIBLE_RUNS_CALLBACK, OPTIMIZING_SCHEDULE, CALLING_OPTIMIZED_RUNS_CALLBACK, SENDING_SCHEDULE, EQUILIBRIUM_REACHED
	}

	@Override
	public boolean isEquilibriumReached() {
		return State.EQUILIBRIUM_REACHED.equals(state);
	}

	@SuppressWarnings("unchecked")
	private class ProxyCallbacks extends AbstractMethodExposingBean implements
			EquilibriumFoundCallback, OptimizedRunsCallback,
			PossibleRunsCallback, SchedulesReceivedCallback, ControlAlgorithm {
		private IActionDescription actionEquilibrium;
		private IActionDescription actionOptimizedRunsCallback;
		private IActionDescription actionGetPossibleRuns;
		private IActionDescription actionSchedulesReceivedCallback;
		private IActionDescription actionFindBestConfiguration;

		public ProxyCallbacks() {
			actionEquilibrium = getAction(ACTION_EQUILIBRIUM);
			actionOptimizedRunsCallback = getAction(ACTION_OPTIMIZED_RUNS_CALLBACK);
			actionGetPossibleRuns = getAction(ACTION_GET_POSSIBLE_RUNS);
			actionSchedulesReceivedCallback = getAction(ACTION_SCHEDULE_RECEIVED_CALLBACK);
			actionFindBestConfiguration = getAction(ACTION_FIND_BEST_CONFIGURATION);
		}

		private IActionDescription getAction(String actionString) {
			IActionDescription template = new Action(actionString);
			IActionDescription action = ScheduleHandlerServiceAgentBeanImpl.this.memory
					.read(template);
			if (action == null) {
				action = ScheduleHandlerServiceAgentBeanImpl.this.thisAgent
						.searchAction(template);
			}
			return action;
		}

		@Override
		public Schedule schedulesReceivedCallback(Schedule allSchedules,
				List<OptimizedRun> lastOptimizedRuns) {
			if (actionSchedulesReceivedCallback == null) {
				actionSchedulesReceivedCallback = getAction(ACTION_SCHEDULE_RECEIVED_CALLBACK);
			}
			if (actionSchedulesReceivedCallback == null) {
				LOG.info("{} - No action called {} available",
						ScheduleHandlerServiceAgentBeanImpl.this.thisAgent,
						ACTION_SCHEDULE_RECEIVED_CALLBACK);
				return allSchedules;
			}

			LinkedList<OptimizedRun> list;
			if (lastOptimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(lastOptimizedRuns);
			}
			ActionResult result = ScheduleHandlerServiceAgentBeanImpl.this
					.invokeAndWaitForResult(actionSchedulesReceivedCallback,
							new Serializable[] { allSchedules, list });

			return (Schedule) result.getResults()[0];
		}

		@Override
		public List<PossibleRun> getPossibleRuns() {
			if (actionGetPossibleRuns == null) {
				actionGetPossibleRuns = getAction(ACTION_GET_POSSIBLE_RUNS);
			}
			if (actionGetPossibleRuns == null) {
				LOG.info("{} - No action called {} available",
						ScheduleHandlerServiceAgentBeanImpl.this.thisAgent,
						ACTION_GET_POSSIBLE_RUNS);
				return null;
			}

			ActionResult result = ScheduleHandlerServiceAgentBeanImpl.this
					.invokeAndWaitForResult(actionGetPossibleRuns,
							new Serializable[] {});

			return (List<PossibleRun>) result.getResults()[0];
		}

		@Override
		public List<OptimizedRun> optimizedRunsCallback(
				List<OptimizedRun> optimizedRuns) {
			if (actionOptimizedRunsCallback == null) {
				actionOptimizedRunsCallback = getAction(ACTION_OPTIMIZED_RUNS_CALLBACK);
			}
			if (actionOptimizedRunsCallback == null) {
				LOG.info("{} - No action called {} available",
						ScheduleHandlerServiceAgentBeanImpl.this.thisAgent,
						ACTION_OPTIMIZED_RUNS_CALLBACK);
				return optimizedRuns;
			}

			LinkedList<OptimizedRun> list;
			if (optimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(optimizedRuns);
			}
			ActionResult result = ScheduleHandlerServiceAgentBeanImpl.this
					.invokeAndWaitForResult(actionOptimizedRunsCallback,
							new Serializable[] { list });

			return (List<OptimizedRun>) result.getResults()[0];
		}

		@Override
		public void equilibrium(Schedule schedule,
				List<OptimizedRun> optimizedRuns) {
			if (actionEquilibrium == null) {
				actionEquilibrium = getAction(ACTION_EQUILIBRIUM);
			}
			if (actionEquilibrium == null) {
				LOG.info("{} - No action called {} available",
						ScheduleHandlerServiceAgentBeanImpl.this.thisAgent,
						ACTION_EQUILIBRIUM);
				return;
			}

			LinkedList<OptimizedRun> list;
			if (optimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(optimizedRuns);
			}
			ScheduleHandlerServiceAgentBeanImpl.this.invoke(actionEquilibrium,
					new Serializable[] { schedule, list });
		}

		@Override
		public List<OptimizedRun> findBestConfiguration(
				Configuration currentConfiguration) {
			if (actionFindBestConfiguration == null) {
				actionFindBestConfiguration = getAction(ACTION_FIND_BEST_CONFIGURATION);
			}
			if (actionFindBestConfiguration == null) {
				LOG.info("{} - No action called {} available",
						ScheduleHandlerServiceAgentBeanImpl.this.thisAgent,
						ACTION_FIND_BEST_CONFIGURATION);
				return null;
			}

			ActionResult result = ScheduleHandlerServiceAgentBeanImpl.this
					.invokeAndWaitForResult(actionFindBestConfiguration,
							new Serializable[] { currentConfiguration });

			return (List<OptimizedRun>) result.getResults()[0];
		}
	}
}
