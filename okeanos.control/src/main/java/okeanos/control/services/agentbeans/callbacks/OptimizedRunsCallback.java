package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.OptimizedRun;
import de.dailab.jiactng.agentcore.environment.IEffector;

/**
 * Called with the optimized run for the most recent schedule by
 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}.
 * Thus, this callback provides to opportunity to change the optimized runs for
 * the device.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface OptimizedRunsCallback extends IEffector {

	/** The Constant ACTION_OPTIMIZED_RUNS_CALLBACK. */
	String ACTION_OPTIMIZED_RUNS_CALLBACK = "okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback#optimizedRunsCallback(List<OptimizedRun>)";

	/**
	 * Called with the optimized run for the most recent schedule by
	 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
	 * . Thus, this callback provides to opportunity to change the optimized
	 * runs for the device. Return null if the device should not take part in
	 * the game.
	 * 
	 * @param optimizedRuns
	 *            the optimized runs
	 * @return the list
	 */
	List<OptimizedRun> optimizedRunsCallback(List<OptimizedRun> optimizedRuns);
}
