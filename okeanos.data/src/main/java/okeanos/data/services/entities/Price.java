package okeanos.data.services.entities;

import javax.measure.quantity.Power;

import org.jscience.physics.amount.Amount;

public interface Price {
	double getCostAtConsumption(Amount<Power> consumption);
}
