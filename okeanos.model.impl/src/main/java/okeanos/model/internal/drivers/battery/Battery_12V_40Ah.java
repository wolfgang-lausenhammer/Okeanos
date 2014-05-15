package okeanos.model.internal.drivers.battery;

import javax.measure.quantity.Power;

import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.Schedule;
import okeanos.model.entities.RegenerativeLoad;

import org.joda.time.Period;
import org.jscience.physics.amount.Amount;
import org.springframework.stereotype.Component;

/**
 * Driver for a 12V battery with a 40Ah capacity. Represents a regenerative
 * load, i.e. the load can be influenced and scheduled.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("Battery_12V_40Ah")
public class Battery_12V_40Ah implements RegenerativeLoad {

	@Override
	public void applySchedule(Schedule schedule) {
		// TODO Auto-generated method stub

	}

	@Override
	public Amount<Power> getConsumption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Amount<Power> getConsumptionIn(Period duration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PossibleRunsConfiguration getPossibleRunsConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}
