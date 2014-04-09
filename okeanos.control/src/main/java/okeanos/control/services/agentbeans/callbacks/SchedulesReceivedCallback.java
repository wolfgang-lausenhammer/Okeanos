package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Schedule;
import de.dailab.jiactng.agentcore.knowledge.IFact;

public interface SchedulesReceivedCallback extends IFact {
	Schedule schedulesReceivedCallback(Schedule allSchedules, List<OptimizedRun> lastOptimizedRuns);
}
