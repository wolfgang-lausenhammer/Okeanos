package okeanos.model.entities;

import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Power;

import okeanos.control.entities.PossibleRun;

public interface Load {
	String getId();

	Measurable<Power> getConsumption();

	Measurable<Power> getConsumptionIn(Measurable<Duration> duration);

	List<PossibleRun> getPossibleRuns();
}
