package okeanos.control.entities;

import java.util.List;

/**
 * Represents a configuration that contains the possible runs and the schedule
 * of the other devices, so that the control algorithm is able to figure out the
 * best possible configuration.
 */
public interface Configuration {

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
	 * Gets the schedule of all other devices.
	 * 
	 * @return the schedule of other devices
	 */
	Schedule getScheduleOfOtherDevices();

	/**
	 * Sets the possible run.
	 * 
	 * @param possibleRuns
	 *            the new possible run
	 */
	void setPossibleRun(List<PossibleRun> possibleRuns);

	/**
	 * Sets the schedule.
	 * 
	 * @param scheduleOfOtherDevices
	 *            the new schedule
	 */
	void setSchedule(Schedule scheduleOfOtherDevices);
}
