package okeanos.control.entities.provider;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.RunConstraint;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;

/**
 * Provides a central point to get instances of all the control entities.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface ControlEntitiesProvider {

	/**
	 * Returns a new configuration.
	 * 
	 * @return the new configuration
	 */
	Configuration getNewConfiguration();

	/**
	 * Returns a new optimized run.
	 * 
	 * @return the new optimized run
	 */
	OptimizedRun getNewOptimizedRun();

	/**
	 * Returns a new possible run.
	 * 
	 * @return the new possible run
	 */
	PossibleRun getNewPossibleRun();

	/**
	 * Gets the new possible runs configuration.
	 * 
	 * @return the new possible runs configuration
	 */
	PossibleRunsConfiguration getNewPossibleRunsConfiguration();

	/**
	 * Gets the new run constraint.
	 * 
	 * @return the new run constraint
	 */
	RunConstraint getNewRunConstraint();

	/**
	 * Gets the schedule.
	 * 
	 * @return the schedule
	 */
	Schedule getNewSchedule();

	/**
	 * Returns a new slot.
	 * 
	 * @return the new slot
	 */
	Slot getNewSlot();
}
