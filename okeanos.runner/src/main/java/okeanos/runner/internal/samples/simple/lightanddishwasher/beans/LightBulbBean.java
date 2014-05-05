package okeanos.runner.internal.samples.simple.lightanddishwasher.beans;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;
import okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback;
import okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback;
import okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback;
import okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback;
import okeanos.model.entities.Load;
import okeanos.runner.internal.samples.simple.lightanddishwasher.LightBulbsAndDishwasher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;

/**
 * The light bulb bean that provides the main action to the light bulb agent.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class LightBulbBean extends AbstractMethodExposingBean implements
		EquilibriumFoundCallback, OptimizedRunsCallback, PossibleRunsCallback,
		SchedulesReceivedCallback, ControlAlgorithm {

	/**
	 * The Class ProxyScheduleHandlerServiceAgentBean.
	 */
	private class ProxyScheduleHandlerServiceAgentBean extends
			AbstractMethodExposingBean implements
			ScheduleHandlerServiceAgentBean {

		/** The action is equilibrium reached. */
		private IActionDescription actionIsEquilibriumReached;

		/** The action reset. */
		private IActionDescription actionReset;

		/**
		 * Instantiates a new proxy schedule handler service agent bean.
		 */
		public ProxyScheduleHandlerServiceAgentBean() {
			actionReset = getAction(ACTION_RESET);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean
		 * #isEquilibriumReached()
		 */
		@Override
		public boolean isEquilibriumReached() {

			if (actionIsEquilibriumReached == null) {
				actionIsEquilibriumReached = getAction(ACTION_IS_EQUILIBRIUM_REACHED);
			}
			if (actionIsEquilibriumReached == null) {
				LOG.info("{} - No action called {} available",
						LightBulbBean.this.thisAgent,
						ACTION_IS_EQUILIBRIUM_REACHED);
				return false;
			}

			return (Boolean) LightBulbBean.this.invokeAndWaitForResult(
					actionIsEquilibriumReached, new Serializable[] {})
					.getResults()[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean
		 * #reset(boolean)
		 */
		@Override
		public void reset(final boolean cancelRunningOperation) {

			if (actionReset == null) {
				actionReset = getAction(ACTION_RESET);
			}
			if (actionReset == null) {
				LOG.info("{} - No action called {} available",
						LightBulbBean.this.thisAgent, ACTION_RESET);
				return;
			}

			LightBulbBean.this.invoke(actionReset,
					new Serializable[] { cancelRunningOperation });
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
			IActionDescription action = LightBulbBean.this.memory
					.read(template);
			if (action == null) {
				action = LightBulbBean.this.thisAgent.searchAction(template);
			}
			return action;
		}
	}

	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = LightBulbsAndDishwasher.PLANNING_INTERVAL;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(LightBulbBean.class);

	/** The control algorithm. */
	private ControlAlgorithm controlAlgorithm;

	/** The light bulb model. */
	private Load lightBulb;

	/** The schedule handler service agent bean. */
	private ScheduleHandlerServiceAgentBean scheduleHandlerServiceAgentBean;

	/**
	 * Instantiates a new light bulb bean.
	 * 
	 * @param lightBulb
	 *            the light bulb
	 * @param controlAlgorithm
	 *            the control algorithm
	 */
	@Inject
	public LightBulbBean(
			@Qualifier("lightBulb100W") final Load lightBulb,
			@Qualifier("controlAlgorithmService") final ControlAlgorithm controlAlgorithm) {
		this.lightBulb = lightBulb;
		this.controlAlgorithm = controlAlgorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.AbstractAgentBean#doInit()
	 */
	@Override
	public void doInit() throws Exception {
		super.doInit();

		setExecutionInterval(EXECUTION_INTERVAL);
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

		this.scheduleHandlerServiceAgentBean = new ProxyScheduleHandlerServiceAgentBean();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback
	 * #equilibrium(okeanos.control.entities.Schedule, java.util.List)
	 */
	@Expose(name = ACTION_EQUILIBRIUM)
	@Override
	public void equilibrium(final Schedule schedule,
			final List<OptimizedRun> optimizedRuns) {
		LOG.info("{} - Great! Equilibrium found!", thisAgent.getAgentName());
		LOG.info("{} - Schedule: {}", thisAgent.getAgentName(), schedule);
		LOG.info("{} - Optimized runs: {}", thisAgent.getAgentName(),
				optimizedRuns);
	}

	/**
	 * The actual work happens here. Called once every
	 * {@link LightBulbBean#EXECUTION_INTERVAL} to get ready for the next
	 * iteration.
	 */
	@Override
	public void execute() {
		LOG.info("{} - LightBulbBean execute() called",
				thisAgent.getAgentName());

		// call reset of ScheduleHandlerServiceAgentBean here to make it ready
		// for next iteration
		scheduleHandlerServiceAgentBean.reset(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.algorithms.ControlAlgorithm#findBestConfiguration(okeanos
	 * .control.entities.Configuration)
	 */
	@Expose(name = ACTION_FIND_BEST_CONFIGURATION)
	@Override
	public List<OptimizedRun> findBestConfiguration(
			final Configuration currentConfiguration) {
		LOG.info("{} - findBestConfiguration!", thisAgent.getAgentName());
		return controlAlgorithm.findBestConfiguration(currentConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback#
	 * getPossibleRuns()
	 */
	@Expose(name = ACTION_GET_POSSIBLE_RUNS)
	@Override
	public List<PossibleRun> getPossibleRuns() {
		LOG.info("{} - getPossibleRuns!", thisAgent.getAgentName());
		return lightBulb.getPossibleRuns();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback#
	 * optimizedRunsCallback(java.util.List)
	 */
	@Expose(name = ACTION_OPTIMIZED_RUNS_CALLBACK)
	@Override
	public List<OptimizedRun> optimizedRunsCallback(
			final List<OptimizedRun> optimizedRuns) {
		LOG.info("{} - optimizedRunsCallback!", thisAgent.getAgentName());
		return optimizedRuns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback
	 * #schedulesReceivedCallback(okeanos.control.entities.Schedule,
	 * java.util.List)
	 */
	@Expose(name = ACTION_SCHEDULE_RECEIVED_CALLBACK)
	@Override
	public Schedule schedulesReceivedCallback(final Schedule allSchedules,
			final List<OptimizedRun> lastOptimizedRuns) {
		LOG.info("{} - schedulesReceivedCallback!", thisAgent.getAgentName());
		return allSchedules;
	}
}
