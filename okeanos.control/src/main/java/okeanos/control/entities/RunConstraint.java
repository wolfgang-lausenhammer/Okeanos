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

	Map<DateTime, Amount<Power>> getChargesAtPointsInTime();

	Amount<Power> getMaximumCapacity();

	Amount<Power> getMinimumCapacity();

	Amount<Power> getStartCharge();

	void setChargesAtPointsInTime(Map<DateTime, Amount<Power>> charges);

	void setMaximumCapacity(Amount<Power> maximumCapacity);

	void setMinimumCapacity(Amount<Power> minimumCapacity);

	void setStartCharge(Amount<Power> startCharge);
}
