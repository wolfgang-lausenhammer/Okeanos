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
import okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback;
import okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback;
import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.entities.MessageScope;

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

/**
 * Represents an implementation of {@link ScheduleHandlerServiceAgentBean} that
 * adds one own schedule to the sum of the other agents' schedules. Thus, every
 * agents receives the sum for all agents and needs to subtract its own
 * previously announced schedule, calculate the new optimized schedule with this
 * data, add the new schedule to the sum and then broadcast this sum to all
 * other agents. This implementation is in contrast to
 * {@link SendOwnScheduleOnlyScheduleHandlerServiceAgentBean}
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("localSumScheduleHandlerServiceAgentBean")
@Scope("prototype")
public class LocalSumScheduleHandlerServiceAgentBean extends
		AbstractMethodExposingBean implements ScheduleHandlerServiceAgentBean {

	/**
	 * Provides a transparent proxy for calling the callback methods.
	 * 
	 * @author Wolfgang Lausenhammer
	 */
	@SuppressWarnings("unchecked")
	private class ProxyCallbacks extends AbstractMethodExposingBean implements
			EquilibriumFoundCallback, OptimizedRunsCallback,
			PossibleRunsCallback, SchedulesReceivedCallback, ControlAlgorithm {

		/** The action equilibrium. */
		private IActionDescription actionEquilibrium;

		/** The action find best configuration. */
		private IActionDescription actionFindBestConfiguration;

		/** The action get possible runs. */
		private IActionDescription actionGetPossibleRuns;

		/** The action optimized runs callback. */
		private IActionDescription actionOptimizedRunsCallback;

		/** The action schedules received callback. */
		private IActionDescription actionSchedulesReceivedCallback;

		/**
		 * Instantiates a new proxy callbacks.
		 */
		public ProxyCallbacks() {
			actionEquilibrium = getAction(ACTION_EQUILIBRIUM);
			actionOptimizedRunsCallback = getAction(ACTION_OPTIMIZED_RUNS_CALLBACK);
			actionGetPossibleRuns = getAction(ACTION_GET_POSSIBLE_RUNS);
			actionSchedulesReceivedCallback = getAction(ACTION_SCHEDULE_RECEIVED_CALLBACK);
			actionFindBestConfiguration = getAction(ACTION_FIND_BEST_CONFIGURATION);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback
		 * #equilibrium(okeanos.control.entities.Schedule, java.util.List)
		 */
		@Override
		public void equilibrium(final Schedule schedule,
				final List<OptimizedRun> optimizedRuns) {
			if (actionEquilibrium == null) {
				actionEquilibrium = getAction(ACTION_EQUILIBRIUM);
			}
			if (actionEquilibrium == null) {
				LOG.info("{} - No action called {} available",
						LocalSumScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_EQUILIBRIUM);
				return;
			}

			LinkedList<OptimizedRun> list;
			if (optimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(optimizedRuns);
			}
			LocalSumScheduleHandlerServiceAgentBean.this.invoke(
					actionEquilibrium, new Serializable[] { schedule, list });
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.algorithms.ControlAlgorithm#findBestConfiguration
		 * (okeanos.control.entities.Configuration)
		 */
		@Override
		public List<OptimizedRun> findBestConfiguration(
				final Configuration currentConfiguration) {
			if (actionFindBestConfiguration == null) {
				actionFindBestConfiguration = getAction(ACTION_FIND_BEST_CONFIGURATION);
			}
			if (actionFindBestConfiguration == null) {
				LOG.info("{} - No action called {} available",
						LocalSumScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_FIND_BEST_CONFIGURATION);
				return null;
			}

			ActionResult result = LocalSumScheduleHandlerServiceAgentBean.this
					.invokeAndWaitForResult(actionFindBestConfiguration,
							new Serializable[] { currentConfiguration });

			return (List<OptimizedRun>) result.getResults()[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback
		 * #getPossibleRuns()
		 */
		@Override
		public List<PossibleRun> getPossibleRuns() {
			if (actionGetPossibleRuns == null) {
				actionGetPossibleRuns = getAction(ACTION_GET_POSSIBLE_RUNS);
			}
			if (actionGetPossibleRuns == null) {
				LOG.info("{} - No action called {} available",
						LocalSumScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_GET_POSSIBLE_RUNS);
				return null;
			}

			ActionResult result = LocalSumScheduleHandlerServiceAgentBean.this
					.invokeAndWaitForResult(actionGetPossibleRuns,
							new Serializable[] {});

			return (List<PossibleRun>) result.getResults()[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback
		 * #optimizedRunsCallback(java.util.List)
		 */
		@Override
		public List<OptimizedRun> optimizedRunsCallback(
				final List<OptimizedRun> optimizedRuns) {
			if (actionOptimizedRunsCallback == null) {
				actionOptimizedRunsCallback = getAction(ACTION_OPTIMIZED_RUNS_CALLBACK);
			}
			if (actionOptimizedRunsCallback == null) {
				LOG.info("{} - No action called {} available",
						LocalSumScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_OPTIMIZED_RUNS_CALLBACK);
				return optimizedRuns;
			}

			LinkedList<OptimizedRun> list;
			if (optimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(optimizedRuns);
			}
			ActionResult result = LocalSumScheduleHandlerServiceAgentBean.this
					.invokeAndWaitForResult(actionOptimizedRunsCallback,
							new Serializable[] { list });

			return (List<OptimizedRun>) result.getResults()[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback
		 * #schedulesReceivedCallback(okeanos.control.entities.Schedule,
		 * java.util.List)
		 */
		@Override
		public Schedule schedulesReceivedCallback(final Schedule allSchedules,
				final List<OptimizedRun> lastOptimizedRuns) {
			if (actionSchedulesReceivedCallback == null) {
				actionSchedulesReceivedCallback = getAction(ACTION_SCHEDULE_RECEIVED_CALLBACK);
			}
			if (actionSchedulesReceivedCallback == null) {
				LOG.info("{} - No action called {} available",
						LocalSumScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_SCHEDULE_RECEIVED_CALLBACK);
				return allSchedules;
			}

			LinkedList<OptimizedRun> list;
			if (lastOptimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(lastOptimizedRuns);
			}
			ActionResult result = LocalSumScheduleHandlerServiceAgentBean.this
					.invokeAndWaitForResult(actionSchedulesReceivedCallback,
							new Serializable[] { allSchedules, list });

			return (Schedule) result.getResults()[0];
		}

		/**
		 * Gets the action.
		 * 
		 * @param actionString
		 *            the action string
		 * @return the action
		 */
		private IActionDescription getAction(final String actionString) {
			IActionDescription template = new Action(actionString);
			IActionDescription action = LocalSumScheduleHandlerServiceAgentBean.this.memory
					.read(template);
			if (action == null) {
				action = LocalSumScheduleHandlerServiceAgentBean.this.thisAgent
						.searchAction(template);
			}
			return action;
		}
	}

	/**
	 * Represents a class handling all of the sending actions. Thus, this class
	 * broadcasts the sum of all schedules to the other agents if the schedule
	 * differed from the last announced one.
	 * 
	 * @author Wolfgang Lausenhammer
	 */
	private class ScheduleMessageBroadcaster implements Runnable {

		/**
		 * Represents a task that waits for an equilibrium to occur. Usually
		 * scheduled with a timeout, when the run method runs, sets the state of
		 * the ScheduleHandlerServiceAgentBean to
		 * {@link State#EQUILIBRIUM_REACHED} and calls a callback.
		 * 
		 * @author Wolfgang Lausenhammer
		 */
		private class EquilibriumWaiter implements Runnable {

			/** The latest schedule. */
			private Schedule latestSchedule;

			/** The optimized runs. */
			private List<OptimizedRun> optimizedRuns;

			/**
			 * Instantiates a new equilibrium waiter.
			 * 
			 * @param latestSchedule
			 *            the latest schedule
			 * @param optimizedRuns
			 *            the optimized runs
			 */
			public EquilibriumWaiter(final Schedule latestSchedule,
					final List<OptimizedRun> optimizedRuns) {
				this.latestSchedule = latestSchedule;
				this.optimizedRuns = optimizedRuns;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
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

		/** The latest received schedule. */
		private Schedule latestReceivedSchedule;

		/** The scheduled equilibrium waiter. */
		@SuppressWarnings("rawtypes")
		private ScheduledFuture scheduledEquilibriumWaiter;

		/**
		 * Instantiates a new schedule message broadcaster.
		 * 
		 * @param latestSchedule
		 *            the latest schedule
		 */
		public ScheduleMessageBroadcaster(final Schedule latestSchedule) {
			this.latestReceivedSchedule = latestSchedule;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
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
						.setSchedule(new ConcurrentSkipListMap<DateTime, Slot>());
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
					.setScheduleOfOtherDevices(latestReceivedScheduleMinusLatestOptimizedSchedule);

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
					new DateTime(System.currentTimeMillis()).plusSeconds(
							WAIT_FOR_EQUILIBRIUM_TIMEOUT).toDate());
		}
	}

	/**
	 * Handles all received messages. Thus, receiving the announced schedules of
	 * other agents.
	 * 
	 * @author Wolfgang Lausenhammer
	 */
	private class ScheduleMessageHandler implements SpaceObserver<IFact> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 5191791309923785942L;

		/** The random. */
		private Random random;

		/** The scheduled broadcast. */
		@SuppressWarnings("rawtypes")
		private ScheduledFuture scheduledBroadcast;

		/**
		 * Instantiates a new schedule message handler.
		 */
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
			scheduledBroadcast = taskScheduler
					.schedule(
							new ScheduleMessageBroadcaster(null),
							new DateTime(System.currentTimeMillis())
									.plusMillis(
											random.nextInt(MAXIMUM_TIME_TO_WAIT_FOR_ANNOUNCE_SCHEDULE))
									.toDate());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.sercho.masp.space.event.SpaceObserver#notify(org.sercho.masp.
		 * space.event.SpaceEvent)
		 */
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

				scheduledBroadcast = taskScheduler
						.schedule(
								new ScheduleMessageBroadcaster(latestSchedule),
								new DateTime(System.currentTimeMillis())
										.plusMillis(
												random.nextInt(MAXIMUM_TIME_TO_WAIT_FOR_ANNOUNCE_SCHEDULE))
										.toDate());

				LOG.debug(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						thisAgent.getAgentName(), state,
						State.WAITING_FOR_RANDOM_TIME_BEFORE_SEND);
				state = State.WAITING_FOR_RANDOM_TIME_BEFORE_SEND;
			}
		}
	}

	/**
	 * The different possible state the ScheduleHandlerServiceAgentBean can be
	 * in.
	 * 
	 * @author Wolfgang Lausenhammer
	 */
	private enum State {

		/** Calls the {@link OptimizedRunsCallback} callback. */
		CALLING_OPTIMIZED_RUNS_CALLBACK,

		/** Calls the {@link PossibleRunsCallback} callback. */
		CALLING_POSSIBLE_RUNS_CALLBACK,

		/** Calls the {@link SchedulesReceivedCallback} callback. */
		CALLING_SCHEDULE_CALLBACK,

		/**
		 * Indicates that the equilibrium was reached. Thus, no further
		 * schedules will be announced anymore by this agent and at changing to
		 * this state the {@link EquilibriumFoundCallback} will be called.
		 */
		EQUILIBRIUM_REACHED,

		/**
		 * Currently optimizing the schedule, thus finding the optimal schedule
		 * for the day.
		 */
		OPTIMIZING_SCHEDULE,

		/** Currently sending the schedule. */
		SENDING_SCHEDULE,

		/**
		 * {@link ScheduleHandlerServiceAgentBean} is currently stopped and does
		 * not wait for anything.
		 */
		STOPPED,

		/**
		 * Represents a state where the agent waits to broadcast its schedule
		 * for other agents to do the same. If other agents broadcast their
		 * schedule in this time, sending of the schedule will be cancelled and
		 * a new optimal schedule calculated.
		 */
		WAITING_FOR_RANDOM_TIME_BEFORE_SEND,

		/**
		 * Currently waiting for other agents to announce their schedule. If
		 * agents fail to announce their schedules within
		 * {@link ScheduleHandlerServiceAgentBean#WAIT_FOR_EQUILIBRIUM_TIMEOUT}
		 * it is assumed that there are no updates planned and the
		 * {@link ScheduleHandlerServiceAgentBean} will change to the
		 * {@link State#EQUILIBRIUM_REACHED} state.
		 */
		WAITING_FOR_SCHEDULES
	}

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(LocalSumScheduleHandlerServiceAgentBean.class);

	/** The action broadcast. */
	private IActionDescription actionBroadcast;

	/** The control algorithm. */
	private ControlAlgorithm controlAlgorithm;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The equilibrium found callback. */
	private EquilibriumFoundCallback equilibriumFoundCallback;

	/** The latest optimized runs. */
	private List<OptimizedRun> latestOptimizedRuns;

	/** The optimized runs callback. */
	private OptimizedRunsCallback optimizedRunsCallback;

	/** The possible runs callback. */
	private PossibleRunsCallback possibleRunsCallback;

	/** The schedules received callback. */
	private SchedulesReceivedCallback schedulesReceivedCallback;

	/** The schedule util. */
	private ScheduleUtil scheduleUtil;

	/** The state. */
	private State state;

	/** The task scheduler. */
	private TaskScheduler taskScheduler;

	/**
	 * Instantiates a new local sum schedule handler service agent bean.
	 * 
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 * @param taskScheduler
	 *            the task scheduler
	 */
	@Inject
	public LocalSumScheduleHandlerServiceAgentBean(
			final ControlEntitiesProvider controlEntitiesProvider,
			final TaskScheduler taskScheduler) {
		this.controlEntitiesProvider = controlEntitiesProvider;
		this.taskScheduler = taskScheduler;
		this.scheduleUtil = new ScheduleUtil(controlEntitiesProvider);
		state = State.STOPPED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.AbstractAgentBean#doInit()
	 */
	@Override
	public void doInit() throws Exception {
		super.doInit();

		setExecutionInterval(0); // make passive
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.dailab.jiactng.agentcore.action.AbstractActionAuthorizationBean#doStart
	 * ()
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

		ProxyCallbacks callbacks = new ProxyCallbacks();
		schedulesReceivedCallback = callbacks;
		possibleRunsCallback = callbacks;
		optimizedRunsCallback = callbacks;
		equilibriumFoundCallback = callbacks;
		controlAlgorithm = callbacks;

		new ScheduleMessageHandler();

		LOG.debug(
				"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
				state, State.WAITING_FOR_SCHEDULES);
		state = State.WAITING_FOR_SCHEDULES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean#
	 * isEquilibriumReached()
	 */
	@Override
	public boolean isEquilibriumReached() {
		return State.EQUILIBRIUM_REACHED.equals(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean#reset
	 * (boolean)
	 */
	@Override
	public void reset(final boolean cancelRunningOperation) {

	}
}
