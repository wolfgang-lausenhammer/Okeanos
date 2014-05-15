package okeanos.model.entities;

import javax.measure.quantity.Power;

import okeanos.control.entities.PossibleRunsConfiguration;

import org.joda.time.Period;
import org.jscience.physics.amount.Amount;

/**
 * Represents the most basic form of a load. A load cannot be controlled, it can
 * only report.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface Load {

	/**
	 * Gets the current consumption.
	 * 
	 * @return the current consumption
	 */
	Amount<Power> getConsumption();

	/**
	 * Gets the future consumption.
	 * 
	 * @param duration
	 *            the duration
	 * @return the consumption in the future
	 */
	Amount<Power> getConsumptionIn(Period duration);

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
}
