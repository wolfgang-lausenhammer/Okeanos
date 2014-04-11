package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Schedule;
import de.dailab.jiactng.agentcore.environment.IEffector;

/**
 * Called when the
 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
 * received a schedule from other agents. Can be used to change the received
 * schedule.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface SchedulesReceivedCallback extends IEffector {

	/** The Constant ACTION_SCHEDULE_RECEIVED_CALLBACK. */
	String ACTION_SCHEDULE_RECEIVED_CALLBACK = "okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback#schedulesReceivedCallback(Schedule, List<OptimizedRun>)";

	/**
	 * Called when the
	 * {@link okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean}
	 * received a schedule from other agents. Can be used to change the received
	 * schedule.
	 * 
	 * @param allSchedules
	 *            the all schedules
	 * @param lastOptimizedRuns
	 *            the last optimized runs
	 * @return the schedule
	 */
	Schedule schedulesReceivedCallback(Schedule allSchedules,
			List<OptimizedRun> lastOptimizedRuns);
}
