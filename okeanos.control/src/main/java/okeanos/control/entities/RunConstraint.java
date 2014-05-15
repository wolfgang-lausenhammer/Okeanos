package okeanos.control.entities;

import java.util.Map;

import javax.measure.quantity.Power;

import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;

import de.dailab.jiactng.agentcore.knowledge.IFact;

public interface RunConstraint extends IFact {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	Map<DateTime, Amount<Power>> getCharges();

	Amount<Power> getMaximumCharge();

	Amount<Power> getMinimumCharge();

	Amount<Power> getStartCharge();

	void setCharges(Map<DateTime, Amount<Power>> charges);

	void setMaximumCharge(Amount<Power> maximumCharge);

	void setMinimumCharge(Amount<Power> minimumCharge);

	void setStartCharge(Amount<Power> startCharge);
}
