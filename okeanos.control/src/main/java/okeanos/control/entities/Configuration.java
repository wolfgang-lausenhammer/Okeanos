package okeanos.control.entities;

import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * Represents a configuration that contains the possible runs and the schedule
 * of the other devices, so that the control algorithm is able to figure out the
 * best possible configuration.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface Configuration extends IFact {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the possible runs configuration.
	 * 
	 * @return the possible runs configuration
	 */
	PossibleRunsConfiguration getPossibleRunsConfiguration();

	/**
	 * Gets the schedule of all other devices.
	 * 
	 * @return the schedule of other devices
	 */
	Schedule getScheduleOfOtherDevices();

	/**
	 * Sets the possible runs configuration.
	 * 
	 * @param possibleRunsConfiguration
	 *            the new possible runs configuration
	 */
	void setPossibleRunsConfiguration(
			PossibleRunsConfiguration possibleRunsConfiguration);

	/**
	 * Sets the schedule of all other devices.
	 * 
	 * @param scheduleOfOtherDevices
	 *            the new schedule
	 */
	void setScheduleOfOtherDevices(Schedule scheduleOfOtherDevices);
}
