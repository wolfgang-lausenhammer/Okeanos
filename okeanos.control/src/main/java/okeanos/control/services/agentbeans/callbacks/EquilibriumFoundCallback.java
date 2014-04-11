package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Schedule;
import de.dailab.jiactng.agentcore.environment.IEffector;

/**
 * Called when the
 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
 * has detected an equilibrium. Thus, no changes in schedules were announced by
 * agents recently.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface EquilibriumFoundCallback extends IEffector {

	/** The Constant ACTION_EQUILIBRIUM. */
	String ACTION_EQUILIBRIUM = "okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback#equilibrium(Schedule, List<OptimizedRun>)";

	/**
	 * Called when the
	 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
	 * has detected an equilibrium. Thus, no changes in schedules were announced
	 * by agents recently.
	 * 
	 * @param schedule
	 *            the schedule
	 * @param optimizedRuns
	 *            the optimized runs
	 */
	void equilibrium(Schedule schedule, List<OptimizedRun> optimizedRuns);
}
