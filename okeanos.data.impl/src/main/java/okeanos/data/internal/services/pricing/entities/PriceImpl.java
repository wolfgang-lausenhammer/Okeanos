package okeanos.data.internal.services.pricing.entities;

import java.util.Arrays;

import javax.measure.quantity.Power;

import org.jscience.physics.amount.Amount;

import okeanos.data.services.Constants;
import okeanos.data.services.entities.Price;
import okeanos.math.regression.ExpTrendLine;
import okeanos.math.regression.PowerTrendLine;
import okeanos.math.regression.TrendLine;

public class PriceImpl implements Price {

	private TrendLine trendline;

	public PriceImpl(double[] x, double[] y) {
		trendline = new ExpTrendLine();
		trendline.setValues(y, x);
	}

	@Override
	public double getCostAtConsumption(Amount<Power> consumption) {
		double costs = trendline.predict(consumption.abs().doubleValue(
				Power.UNIT));
		if (consumption.isLessThan(Amount.valueOf(0, Power.UNIT))) {
			costs = -costs * Constants.FEEDBACK_TARIFF_PERCENTAGE;
		}
		return costs;
	}

	@Override
	public String toString() {
		int[] x = new int[] { 0, 10, 20, 30, 40 };
		double[] y = new double[] { trendline.predict(x[0]),
				trendline.predict(x[1]), trendline.predict(x[2]),
				trendline.predict(x[3]), trendline.predict(x[4]) };
		return String.format("PriceImpl\n[x=%s]\n[y=%s]", Arrays.toString(x),
				Arrays.toString(y));
	}
}
