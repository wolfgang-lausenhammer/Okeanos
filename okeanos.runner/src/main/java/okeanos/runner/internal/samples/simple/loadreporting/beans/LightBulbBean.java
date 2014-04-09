package okeanos.runner.internal.samples.simple.loadreporting.beans;

import java.util.List;

import javax.inject.Inject;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback;
import okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback;
import okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback;
import okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback;
import okeanos.model.entities.Load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;

// TODO: Auto-generated Javadoc
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

	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 8000;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(LightBulbBean.class);

	/** The light bulb model. */
	private Load lightBulb;

	/** The control algorithm. */
	private ControlAlgorithm controlAlgorithm;

	/**
	 * Instantiates a new light bulb bean.
	 * 
	 * @param lightBulb
	 *            the light bulb
	 * @param controlAlgorithm
	 *            the control algorithm
	 */
	@Inject
	public LightBulbBean(@Qualifier("lightBulb100W") final Load lightBulb,
			final ControlAlgorithm controlAlgorithm) {
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
	}

	/* (non-Javadoc)
	 * @see okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback#schedulesReceivedCallback(okeanos.control.entities.Schedule, java.util.List)
	 */
	@Expose(name = ACTION_SCHEDULE_RECEIVED_CALLBACK)
	@Override
	public Schedule schedulesReceivedCallback(Schedule allSchedules,
			List<OptimizedRun> lastOptimizedRuns) {
		LOG.info("{} - schedulesReceivedCallback!", thisAgent.getAgentName());
		return allSchedules;
	}

	/* (non-Javadoc)
	 * @see okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback#getPossibleRuns()
	 */
	@Expose(name = ACTION_GET_POSSIBLE_RUNS)
	@Override
	public List<PossibleRun> getPossibleRuns() {
		LOG.info("{} - getPossibleRuns!", thisAgent.getAgentName());
		return lightBulb.getPossibleRuns();
	}

	/* (non-Javadoc)
	 * @see okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback#optimizedRunsCallback(java.util.List)
	 */
	@Expose(name = ACTION_OPTIMIZED_RUNS_CALLBACK)
	@Override
	public List<OptimizedRun> optimizedRunsCallback(
			List<OptimizedRun> optimizedRuns) {
		LOG.info("{} - optimizedRunsCallback!", thisAgent.getAgentName());
		return optimizedRuns;
	}

	/* (non-Javadoc)
	 * @see okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback#equilibrium(okeanos.control.entities.Schedule, java.util.List)
	 */
	@Expose(name = ACTION_EQUILIBRIUM)
	@Override
	public void equilibrium(Schedule schedule, List<OptimizedRun> optimizedRuns) {
		LOG.info("{} - Great! Equilibrium found!", thisAgent.getAgentName());
		LOG.info("{} - Schedule: {}", thisAgent.getAgentName(), schedule);
		LOG.info("{} - Optimized runs: {}", thisAgent.getAgentName(), optimizedRuns);
	}

	/* (non-Javadoc)
	 * @see okeanos.control.algorithms.ControlAlgorithm#findBestConfiguration(okeanos.control.entities.Configuration)
	 */
	@Expose(name = ACTION_FIND_BEST_CONFIGURATION)
	@Override
	public List<OptimizedRun> findBestConfiguration(
			Configuration currentConfiguration) {
		LOG.info("{} - findBestConfiguration!", thisAgent.getAgentName());
		return controlAlgorithm.findBestConfiguration(currentConfiguration);
	}

}
