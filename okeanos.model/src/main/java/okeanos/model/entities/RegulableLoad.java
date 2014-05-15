package okeanos.model.entities;

import okeanos.control.entities.Schedule;

/**
 * Regulable loads comprise loads that can be controlled in a basic way. That
 * is, they can be switched on and off at certain points in time. However,
 * compared to {@link RegenerativeLoad} they cannot save energy. Therefore,
 * dishwasher, washing machines, driers, etc. are the perfect candidates for
 * {@link RegulableLoad}s.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface RegulableLoad extends Load {

	/**
	 * Applies the given schedule.
	 * 
	 * @param schedule
	 *            the schedule to apply
	 */
	void applySchedule(Schedule schedule);
}
