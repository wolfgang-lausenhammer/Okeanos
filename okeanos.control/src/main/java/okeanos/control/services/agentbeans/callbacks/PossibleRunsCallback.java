package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.PossibleRun;
import de.dailab.jiactng.agentcore.environment.IEffector;

/**
 * Called when the
 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
 * wants to get to know the possible runs for the current device.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface PossibleRunsCallback extends IEffector {

	/** The Constant ACTION_GET_POSSIBLE_RUNS. */
	String ACTION_GET_POSSIBLE_RUNS = "okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback#getPossibleRuns()";

	/**
	 * Called when the
	 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
	 * wants to get to know the possible runs for the current device.
	 * 
	 * @return the possible runs
	 */
	List<PossibleRun> getPossibleRuns();
}
