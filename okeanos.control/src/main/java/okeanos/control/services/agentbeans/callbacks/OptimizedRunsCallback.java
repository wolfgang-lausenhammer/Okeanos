package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.OptimizedRun;
import de.dailab.jiactng.agentcore.environment.IEffector;

public interface OptimizedRunsCallback extends IEffector {
	String ACTION_OPTIMIZED_RUNS_CALLBACK = "okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback#optimizedRunsCallback(List<OptimizedRun>)";

	List<OptimizedRun> optimizedRunsCallback(List<OptimizedRun> optimizedRuns);
}
