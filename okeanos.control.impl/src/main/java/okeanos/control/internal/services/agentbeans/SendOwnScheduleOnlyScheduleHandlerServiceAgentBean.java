package okeanos.control.internal.services.agentbeans;

import static okeanos.data.services.agentbeans.CommunicationServiceAgentBean.Header.COMMUNICATION_SENDER;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import javax.inject.Inject;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.control.entities.utilities.ScheduleUtil;
import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;
import okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback;
import okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback;
import okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback;
import okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback;
import okeanos.data.services.TimeService;
import okeanos.data.services.UUIDGenerator;
import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.entities.MessageScope;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
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
 * sends only one own schedules to other agents. Thus, every agents calculates
 * the sum out of these announced schedules on his own, opposed to
 * {@link LocalSumScheduleHandlerServiceAgentBean}.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("sendOwnScheduleOnlyScheduleHandlerServiceAgentBean")
@Scope("prototype")
public class SendOwnScheduleOnlyScheduleHandlerServiceAgentBean extends
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
				LOG.info(
						"{} - No action called {} available",
						SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_EQUILIBRIUM);
				return;
			}

			LinkedList<OptimizedRun> list;
			if (optimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(optimizedRuns);
			}
			SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this.invoke(
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
				LOG.info(
						"{} - No action called {} available",
						SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_FIND_BEST_CONFIGURATION);
				return null;
			}

			ActionResult result = SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this
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
				LOG.info(
						"{} - No action called {} available",
						SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_GET_POSSIBLE_RUNS);
				return null;
			}

			ActionResult result = SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this
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
				LOG.info(
						"{} - No action called {} available",
						SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_OPTIMIZED_RUNS_CALLBACK);
				return optimizedRuns;
			}

			LinkedList<OptimizedRun> list;
			if (optimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(optimizedRuns);
			}
			ActionResult result = SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this
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
				LOG.info(
						"{} - No action called {} available",
						SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this.thisAgent,
						ACTION_SCHEDULE_RECEIVED_CALLBACK);
				return allSchedules;
			}

			LinkedList<OptimizedRun> list;
			if (lastOptimizedRuns == null) {
				list = new LinkedList<>();
			} else {
				list = new LinkedList<>(lastOptimizedRuns);
			}
			ActionResult result = SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this
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
			IActionDescription action = SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this.memory
					.read(template);
			if (action == null) {
				action = SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.this.thisAgent
						.searchAction(template);
			}
			return action;
		}
	}

	/**
	 * Represents a class handling all of the sending actions. Thus, this class
	 * broadcasts my own schedules to the other agents if the schedule differed
	 * from the last announced one.
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

				Schedule schedule = scheduleUtil.plus(
						scheduleUtil.sum(scheduleOfEntities.values().toArray(
								new Schedule[0])),
						scheduleUtil.toSchedule(latestOptimizedRuns));

				equilibriumFoundCallback.equilibrium(schedule,
						latestOptimizedRuns);
			}
		}

		/** The current uuid of the messages sent. */
		private String currentUUID;

		/** The equilibrium waiter. */
		private EquilibriumWaiter equilibriumWaiter;

		/** The scheduled equilibrium waiter. */
		@SuppressWarnings("rawtypes")
		private ScheduledFuture scheduledEquilibriumWaiter;

		/**
		 * Instantiates a new schedule message broadcaster.
		 * 
		 * @param currentUUID
		 *            the current uuid
		 */
		public ScheduleMessageBroadcaster(final String currentUUID) {
			this.currentUUID = currentUUID;
			this.equilibriumWaiter = new EquilibriumWaiter();
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
			LOG.trace("{} - ScheduleMessageBroadcaster execute() called",
					thisAgent.getAgentName());
			if (scheduledEquilibriumWaiter != null) {
				scheduledEquilibriumWaiter.cancel(true);
			}

			if (latestOptimizedRuns == null) {
				latestOptimizedRuns = new LinkedList<>();
			}

			LOG.trace(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					thisAgent.getAgentName(), state,
					State.CALLING_POSSIBLE_RUNS_CALLBACK);
			state = State.CALLING_POSSIBLE_RUNS_CALLBACK;
			List<PossibleRun> possibleRunsToday = possibleRunsCallback
					.getPossibleRuns();

			Configuration configuration = controlEntitiesProvider
					.getNewConfiguration();
			configuration.setPossibleRun(possibleRunsToday);
			configuration.setSchedule(scheduleUtil.sum(scheduleOfEntities
					.values().toArray(new Schedule[0])));

			// optimize schedule
			LOG.trace(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					thisAgent.getAgentName(), state, State.OPTIMIZING_SCHEDULE);
			state = State.OPTIMIZING_SCHEDULE;
			List<OptimizedRun> optimizedRuns = controlAlgorithm
					.findBestConfiguration(configuration);

			// allow agent bean to correct optimized runs
			LOG.trace(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					thisAgent.getAgentName(), state,
					State.CALLING_OPTIMIZED_RUNS_CALLBACK);
			state = State.CALLING_OPTIMIZED_RUNS_CALLBACK;
			optimizedRuns = optimizedRunsCallback
					.optimizedRunsCallback(optimizedRuns);

			Schedule schedule = scheduleUtil.toSchedule(optimizedRuns);
			if (scheduleUtil.compare(
					scheduleUtil.toSchedule(latestOptimizedRuns), schedule) == 0) {
				// own schedule not changed despite new schedule from others
				// do not announce new schedule!
				LOG.debug("{} - Schedule remained unchanged.",
						thisAgent.getAgentName());
				LOG.trace("{}\n{}", thisAgent.getAgentName(), scheduleUtil
						.plus(configuration.getScheduleOfOtherDevices(),
								schedule));
			} else {
				LOG.trace(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						thisAgent.getAgentName(), state, State.SENDING_SCHEDULE);
				state = State.SENDING_SCHEDULE;

				LOG.debug("{} - Announcing my new optimized schedule",
						thisAgent.getAgentName());
				LOG.trace("{}\n{}", thisAgent.getAgentName(), StringUtils.join(
						schedule.getSchedule().entrySet(), '\n'));
				HashMap<String, String> headers = new HashMap<>();
				headers.put(HEADER_IDENTIFY_SCHEDULE_SENDER, currentUUID);

				invoke(actionBroadcastOptions, new Serializable[] {
						MessageScope.GROUP, schedule, headers });
				LOG.trace("{} - Announced schedule", thisAgent.getAgentName());
			}
			latestOptimizedRuns = optimizedRuns;

			LOG.trace(
					"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
					thisAgent.getAgentName(), state,
					State.WAITING_FOR_SCHEDULES);
			state = State.WAITING_FOR_SCHEDULES;

			scheduledEquilibriumWaiter = timeService
					.schedule(equilibriumWaiter,
							Period.seconds(WAIT_FOR_EQUILIBRIUM_TIMEOUT),
							taskScheduler);
		}
	}

	/**
	 * Handles all received messages. Thus, writes the announced schedules of
	 * other agents to the shared schedule map.
	 * 
	 * @author Wolfgang Lausenhammer
	 */
	private class ScheduleMessageHandler implements SpaceObserver<IFact> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 5191791309923785942L;

		/** The random number generator. */
		private Random random;

		/**
		 * The scheduled future used to keep track of the future broadcasting my
		 * own schedule.
		 */
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

			reset();
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

				LOG.trace("{} - Broadcast Message received.",
						thisAgent.getAgentName());
				LOG.trace(
						"{} - Cancelling current announce schedule and schedule new one.",
						thisAgent.getAgentName());

				Schedule latestSchedule = (Schedule) message.getPayload();
				scheduledBroadcast.cancel(false);

				LOG.trace(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						thisAgent.getAgentName(), state,
						State.CALLING_SCHEDULE_CALLBACK);
				state = State.CALLING_SCHEDULE_CALLBACK;
				latestSchedule = schedulesReceivedCallback
						.schedulesReceivedCallback(latestSchedule,
								latestOptimizedRuns);

				scheduleOfEntities.put(
						message.getHeader(HEADER_IDENTIFY_SCHEDULE_SENDER),
						latestSchedule);

				LOG.trace("{} - scheduling a task to broadcast new schedule.",
						thisAgent.getAgentName());

				scheduledBroadcast = taskScheduler
						.schedule(
								new ScheduleMessageBroadcaster(currentId),
								new DateTime(System.currentTimeMillis())
										.plusMillis(
												random.nextInt(MAXIMUM_TIME_TO_WAIT_FOR_ANNOUNCE_SCHEDULE))
										.toDate());

				LOG.trace(
						"{} - ScheduleHandlerServiceAgentBeanImpl changing state from {} to {}",
						thisAgent.getAgentName(), state,
						State.WAITING_FOR_RANDOM_TIME_BEFORE_SEND);
				state = State.WAITING_FOR_RANDOM_TIME_BEFORE_SEND;
			}
		}

		/**
		 * Reset.
		 */
		public void reset() {
			// Next schedule randomly distributed within the next 500ms so that
			// not all device announce their schedule at the same time. If
			// another device announced its schedule before this, the task gets
			// cancelled and a new task will be scheduled see #notify.
			if (scheduledBroadcast != null) {
				scheduledBroadcast.cancel(true);
			}
			scheduledBroadcast = timeService
					.schedule(
							new ScheduleMessageBroadcaster(currentId),
							Period.millis(random
									.nextInt(MAXIMUM_TIME_TO_WAIT_FOR_ANNOUNCE_SCHEDULE)),
							taskScheduler);
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

	/** The Constant HEADER_IDENTIFY_SCHEDULE_SENDER. */
	public static final String HEADER_IDENTIFY_SCHEDULE_SENDER = "OkeanosScheduleSenderId";

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(SendOwnScheduleOnlyScheduleHandlerServiceAgentBean.class);

	/** The action broadcast options. */
	private IActionDescription actionBroadcastOptions;

	/** The control algorithm. */
	private ControlAlgorithm controlAlgorithm;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The current id. */
	private String currentId;

	/** The equilibrium found callback. */
	private EquilibriumFoundCallback equilibriumFoundCallback;

	/** The latest optimized runs. */
	private List<OptimizedRun> latestOptimizedRuns;

	/** The optimized runs callback. */
	private OptimizedRunsCallback optimizedRunsCallback;

	/** The possible runs callback. */
	private PossibleRunsCallback possibleRunsCallback;

	/** The schedule of entities. */
	private Map<String, Schedule> scheduleOfEntities;

	/** The schedules received callback. */
	private SchedulesReceivedCallback schedulesReceivedCallback;

	/** The schedule util. */
	private ScheduleUtil scheduleUtil;

	/** The state. */
	private State state;

	/** The task scheduler. */
	private TaskScheduler taskScheduler;

	/** The time service. */
	private TimeService timeService;

	/** The uuid generator. */
	private UUIDGenerator uuidGenerator;

	/** The schedule message handler. */
	private ScheduleMessageHandler scheduleMessageHandler;

	/**
	 * Instantiates a new send own schedule only schedule handler service agent
	 * bean.
	 * 
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 * @param timeService
	 *            the time service
	 * @param taskScheduler
	 *            the task scheduler
	 * @param uuidGenerator
	 *            the uuid generator
	 */
	@Inject
	public SendOwnScheduleOnlyScheduleHandlerServiceAgentBean(
			final ControlEntitiesProvider controlEntitiesProvider,
			final TimeService timeService, final TaskScheduler taskScheduler,
			final UUIDGenerator uuidGenerator) {
		super();

		this.controlEntitiesProvider = controlEntitiesProvider;
		this.timeService = timeService;
		this.taskScheduler = taskScheduler;
		this.scheduleUtil = new ScheduleUtil(controlEntitiesProvider);
		this.uuidGenerator = uuidGenerator;
		scheduleOfEntities = new ConcurrentHashMap<>();
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

		currentId = thisAgent.getAgentName();
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
				CommunicationServiceAgentBean.ACTION_BROADCAST_OPTIONS);
		actionBroadcastOptions = memory.read(template);
		if (actionBroadcastOptions == null) {
			actionBroadcastOptions = thisAgent.searchAction(template);
		}

		ProxyCallbacks callbacks = new ProxyCallbacks();
		schedulesReceivedCallback = callbacks;
		possibleRunsCallback = callbacks;
		optimizedRunsCallback = callbacks;
		equilibriumFoundCallback = callbacks;
		controlAlgorithm = callbacks;

		scheduleMessageHandler = new ScheduleMessageHandler();

		LOG.trace(
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
	@Expose(name = ACTION_IS_EQUILIBRIUM_REACHED)
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
	@Expose(name = ACTION_RESET)
	@Override
	public void reset(final boolean cancelRunningOperation) {
		LOG.debug("reset called!");
		scheduleOfEntities.clear();
		latestOptimizedRuns.clear();
		state = State.WAITING_FOR_SCHEDULES;
		currentId = uuidGenerator.generateUUID();

		scheduleMessageHandler.reset();
	}
}
