package okeanos.control.entities;

import javax.measure.quantity.Power;

import org.jscience.physics.amount.Amount;

import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * Represents one time slot within a run. Every time slot can have different
 * consumption/production, e.g., a dishwasher could need power to heat up the
 * water, but then not needs as much power anymore for washing the dishes.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface Slot extends IFact {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the load.
	 * 
	 * @return the load
	 */
	Amount<Power> getLoad();

	/**
	 * Sets the load.
	 * 
	 * @param load
	 *            the new load
	 */
	void setLoad(Amount<Power> load);
}
