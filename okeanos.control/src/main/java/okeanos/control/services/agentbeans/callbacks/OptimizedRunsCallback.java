package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.OptimizedRun;
import de.dailab.jiactng.agentcore.knowledge.IFact;

public interface OptimizedRunsCallback extends IFact {
	List<OptimizedRun> optimizedRunsCallback(List<OptimizedRun> optimizedRuns);
}
