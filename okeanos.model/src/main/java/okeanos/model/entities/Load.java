package okeanos.model.entities;

import java.util.List;

import javax.measure.quantity.Power;

import okeanos.control.entities.PossibleRun;

import org.joda.time.Period;
import org.jscience.physics.amount.Amount;

public interface Load {
	String getId();

	Amount<Power> getConsumption();

	Amount<Power> getConsumptionIn(Period duration);

	List<PossibleRun> getPossibleRuns();
}
