package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.PossibleRun;
import de.dailab.jiactng.agentcore.environment.IEffector;

public interface PossibleRunsCallback extends IEffector {
	String ACTION_GET_POSSIBLE_RUNS = "okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback#getPossibleRuns()";

	List<PossibleRun> getPossibleRuns();
}
