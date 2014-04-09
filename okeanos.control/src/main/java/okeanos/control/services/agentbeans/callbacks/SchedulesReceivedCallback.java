package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Schedule;
import de.dailab.jiactng.agentcore.environment.IEffector;

public interface SchedulesReceivedCallback extends IEffector {
	String ACTION_SCHEDULE_RECEIVED_CALLBACK = "okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback#schedulesReceivedCallback(Schedule, List<OptimizedRun>)";

	Schedule schedulesReceivedCallback(Schedule allSchedules,
			List<OptimizedRun> lastOptimizedRuns);
}
