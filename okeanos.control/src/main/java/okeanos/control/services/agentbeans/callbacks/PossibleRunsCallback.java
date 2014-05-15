package okeanos.control.services.agentbeans.callbacks;

import okeanos.control.entities.PossibleRunsConfiguration;
import de.dailab.jiactng.agentcore.environment.IEffector;

/**
 * Called when the
 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
 * wants to get to know the possible runs for the current device.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface PossibleRunsCallback extends IEffector {

	/** The Constant ACTION_GET_POSSIBLE_RUNS_CONFIGURATION. */
	String ACTION_GET_POSSIBLE_RUNS_CONFIGURATION = "okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback#getPossibleRunsConfiguration()";

	/**
	 * Called when the
	 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
	 * wants to get to know the possible runs for the current device.
	 * 
	 * @return the possible runs configuration
	 */
	PossibleRunsConfiguration getPossibleRunsConfiguration();
}
