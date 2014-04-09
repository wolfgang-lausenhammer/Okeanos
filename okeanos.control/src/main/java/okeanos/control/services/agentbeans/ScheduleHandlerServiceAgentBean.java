package okeanos.control.services.agentbeans;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback;
import okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback;
import okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback;
import okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback;
import de.dailab.jiactng.agentcore.environment.IEffector;

public interface ScheduleHandlerServiceAgentBean extends IEffector {
	public static String ACTION_REGISTER_OTHER_SCHEDULES_ARRIVED_CALLBACK = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#registerOtherSchedulesArrivedCallback(OtherSchedulesArrivedCallback)";
	public static String ACTION_REGISTER_POSSIBLE_RUNS_CALLBACK = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#registerPossibleRunsCallback(PossibleRunsCallback)";
	public static String ACTION_REGISTER_OPTIMIZED_RUNS_CALLBACK = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#registerOptimizedRunsCallback(OptimizedRunsCallback)";
	public static String ACTION_REGISTER_EQUILIBRIUM_FOUND_CALLBACK = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#registerEquilibriumFoundCallback(EquilibriumFoundCallback)";
	public static String ACTION_SET_CONTROL_ALGORITHM = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#setControlAlgorithm(ControlAlgorithm)";
	public static String ACTION_RESET = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#reset(boolean)";
	public static String ACTION_IS_EQUILIBRIUM_REACHED = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#isEquilibriumReached()";

	void registerOtherSchedulesArrivedCallback(
			SchedulesReceivedCallback otherSchedulesArrivedCallback);

	void registerPossibleRunsCallback(PossibleRunsCallback possibleRunsCallback);

	void registerOptimizedRunsCallback(
			OptimizedRunsCallback optimizedRunsCallback);

	void registerEquilibriumFoundCallback(
			EquilibriumFoundCallback equilibriumFoundCallback);

	void setControlAlgorithm(ControlAlgorithm controlAlgorithm);

	void reset(boolean cancelRunningOperation);

	boolean isEquilibriumReached();
}
