package okeanos.model.entities;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Power;

public interface Load {
	String getId();

	Measurable<Power> getConsumption();

	Measurable<Power> getConsumptionIn(Measurable<Duration> duration);
}
