package okeanos.control.entities;

import java.util.List;

import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * Represents the configuration for the possible runs, therefore, it also
 * includes constraints in addition to the actual possible runs.
 * 
 * @author Wolfgang Lausenhammer
 * 
 */
public interface PossibleRunsConfiguration extends IFact {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the possible runs.
	 * 
	 * @return the possible runs
	 */
	List<PossibleRun> getPossibleRuns();

	/**
	 * Sets the load type.
	 * 
	 * @param loadType
	 *            the new load type
	 */
	void setLoadType(LoadType loadType);

	/**
	 * Gets the load type.
	 * 
	 * @return the load type
	 */
	LoadType getLoadType();

	/**
	 * Returns general constraints of the run, such as the amount of power the
	 * configuration is starting with, a certain amount of power that the device
	 * may not fall below or a certain amount of power that the device may not
	 * exceed.
	 * 
	 * @return the contraints of the run
	 */
	RunConstraint getRunConstraint();

	/**
	 * Sets the possible run.
	 * 
	 * @param possibleRuns
	 *            the new possible run
	 */
	void setPossibleRuns(List<PossibleRun> possibleRuns);

	/**
	 * Sets the run constraint.
	 * 
	 * @param runConstraint
	 *            the new run constraint
	 */
	void setRunConstraint(RunConstraint runConstraint);

}
