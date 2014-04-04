package okeanos.control.entities;

import java.util.Map;

import org.joda.time.DateTime;

import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * Represents a schedule of a household device. Contains the
 * consumption/production of a device for several points in time.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface Schedule extends IFact {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the schedule.
	 * 
	 * @return the schedule
	 */
	Map<DateTime, Double> getSchedule();

	/**
	 * Sets the schedule.
	 * 
	 * @param schedule
	 *            the schedule
	 */
	void setSchedule(Map<DateTime, Double> schedule);
}
