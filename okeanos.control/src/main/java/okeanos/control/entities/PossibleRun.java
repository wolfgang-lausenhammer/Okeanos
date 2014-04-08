package okeanos.control.entities;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Represents a proposed run, i.e., a run that would be possible for the
 * household device. A dishwasher could, for example, start anytime after its
 * {@code earliestStartTime}, e.g. 1:30, however, needs the given time slots
 * {@code neededSlots} to finish. Also, it needs to finish before its
 * {@code latestEndTime}, e.g. 3:30. For one run, it is only possible to either
 * produce, or consume energy. If both is possible for a device, multiple runs
 * have to be defined.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface PossibleRun {

	/**
	 * Gets the earliest start time.
	 * 
	 * @return the earliest start time
	 */
	DateTime getEarliestStartTime();

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the latest end time.
	 * 
	 * @return the latest end time
	 */
	DateTime getLatestEndTime();

	/**
	 * Gets the load type.
	 * 
	 * @return the load type
	 */
	LoadType getLoadType();

	/**
	 * Gets the needed slots.
	 * 
	 * @return the needed slots
	 */
	List<Slot> getNeededSlots();

	/**
	 * Sets the earliest start time.
	 * 
	 * @param earliestStartTime
	 *            the new earliest start time
	 */
	void setEarliestStartTime(DateTime earliestStartTime);

	/**
	 * Sets the latest end time.
	 * 
	 * @param latestEndTime
	 *            the new latest end time
	 */
	void setLatestEndTime(DateTime latestEndTime);

	/**
	 * Sets the load type.
	 * 
	 * @param loadType
	 *            the new load type
	 */
	void setLoadType(LoadType loadType);

	/**
	 * Sets the needed slots.
	 * 
	 * @param neededSlots
	 *            the new needed slots
	 */
	void setNeededSlots(List<Slot> neededSlots);

}
