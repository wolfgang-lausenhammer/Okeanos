package okeanos.control.services.agentbeans;

import de.dailab.jiactng.agentcore.environment.IEffector;

/**
 * Provides a reuseable agent bean that is responsible for dealing with the
 * boilerplate code of broadcasting, optimizing and receiving of schedules. The
 * agent calls callback methods when it reaches certain states in its lifecycle.
 * 
 * @author Wolfgang Lausenhammer
 * 
 * @see okeanos.control.services.agentbeans.callbacks.EquilibriumFoundCallback
 * @see okeanos.control.services.agentbeans.callbacks.OptimizedRunsCallback
 * @see okeanos.control.services.agentbeans.callbacks.PossibleRunsCallback
 * @see okeanos.control.services.agentbeans.callbacks.SchedulesReceivedCallback
 */
public interface ScheduleHandlerServiceAgentBean extends IEffector {

	/** The Constant ACTION_RESET. */
	String ACTION_RESET = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#reset(boolean)";

	/** The Constant ACTION_IS_EQUILIBRIUM_REACHED. */
	String ACTION_IS_EQUILIBRIUM_REACHED = "okeanos.data.services.agentbeans.ScheduleHandlerServiceAgentBean#isEquilibriumReached()";

	/** The Constant WAIT_FOR_EQUILIBRIUM_TIMEOUT. */
	int WAIT_FOR_EQUILIBRIUM_TIMEOUT = 5000;

	/** The Constant MAXIMUM_TIME_TO_WAIT_FOR_ANNOUNCE_SCHEDULE. */
	int MAXIMUM_TIME_TO_WAIT_FOR_ANNOUNCE_SCHEDULE = 20;

	/**
	 * Resets the current schedule handler to make it ready for the next round.
	 * Thus, might be called every 24h.
	 * 
	 * @param cancelRunningOperation
	 *            the cancel running operation
	 */
	void reset(boolean cancelRunningOperation);

	/**
	 * Checks if the equilibrium is already reached.
	 * 
	 * @return true, if the equilibrium is reached
	 */
	boolean isEquilibriumReached();
}
