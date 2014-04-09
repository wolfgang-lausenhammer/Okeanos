package okeanos.control.services.agentbeans;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback;
import okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback;
import okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback;
import okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback;
import de.dailab.jiactng.agentcore.environment.IEffector;

public interface ScheduleHandlerServiceAgentBean extends IEffector {
	String ACTION_RESET = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#reset(boolean)";
	String ACTION_IS_EQUILIBRIUM_REACHED = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#isEquilibriumReached()";

	void reset(boolean cancelRunningOperation);

	boolean isEquilibriumReached();
}
