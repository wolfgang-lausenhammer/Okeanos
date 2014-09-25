package okeanos.data.internal.services.pricing.entities;

import java.util.Arrays;

import javax.measure.quantity.Power;

import okeanos.data.services.Constants;
import okeanos.data.services.entities.Price;
import okeanos.math.regression.TrendLine;

import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceImpl implements Price {

	private static final Logger LOG = LoggerFactory.getLogger(PriceImpl.class);

	private TrendLine trendline;

	private double priceFor1kWh;

	public PriceImpl(double[] x, double[] y) {
		priceFor1kWh = y[0];
		//
		// trendline = new ExpTrendLine();
		// trendline.setValues(y, x);
		//
		// LOG.error("1W cost: {}",
		// getCostAtConsumption(Amount.valueOf(1, Power.UNIT)));
		// LOG.error("10W cost: {}",
		// getCostAtConsumption(Amount.valueOf(10, Power.UNIT)));
		// LOG.error("100W cost: {}",
		// getCostAtConsumption(Amount.valueOf(100, Power.UNIT)));
		// LOG.error("1000W cost: {}",
		// getCostAtConsumption(Amount.valueOf(1000, Power.UNIT)));
		// LOG.error("10000W cost: {}",
		// getCostAtConsumption(Amount.valueOf(10000, Power.UNIT)));
	}

	@Override
	public double getCostAtConsumption(Amount<Power> consumption) {
		// double costs = trendline.predict(consumption.abs().doubleValue(
		// Power.UNIT));
		double consump = consumption.abs().doubleValue(Power.UNIT);

		double costs = 0;
		if (consump < 1000) {
			costs = priceFor1kWh
					* (0.00000004 * consump * consump + 0.001 * consump + 0.0045);
		} else {
			costs = priceFor1kWh
					* (0.000000001 * consump * consump + 0.0011 * consump - 0.101);
		}

		if (consumption.isLessThan(Amount.valueOf(0, Power.UNIT))) {
			costs = -costs * Constants.FEEDBACK_TARIFF_PERCENTAGE;
		}
		return costs;
	}

	@Override
	public String toString() {
		int[] x = new int[] { 0, 1, 10, 100, 1000, 10000 };
		double[] y = new double[] {
				getCostAtConsumption(Amount.valueOf(x[0], Power.UNIT)),
				getCostAtConsumption(Amount.valueOf(x[1], Power.UNIT)),
				getCostAtConsumption(Amount.valueOf(x[2], Power.UNIT)),
				getCostAtConsumption(Amount.valueOf(x[3], Power.UNIT)),
				getCostAtConsumption(Amount.valueOf(x[4], Power.UNIT)),
				getCostAtConsumption(Amount.valueOf(x[5], Power.UNIT)) };
		return String.format("PriceImpl\n[x=%s]\n[y=%s]", Arrays.toString(x),
				Arrays.toString(y));
	}
}
