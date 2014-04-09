package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Schedule;
import de.dailab.jiactng.agentcore.environment.IEffector;

public interface EquilibriumFoundCallback extends IEffector {
	String ACTION_EQUILIBRIUM = "okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback#equilibrium(Schedule, List<OptimizedRun>)";

	void equilibrium(Schedule schedule, List<OptimizedRun> optimizedRuns);
}
