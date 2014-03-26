package okeanos.data.internal.services.pricing.entities;

import java.util.Arrays;

import okeanos.data.internal.services.pricing.regression.PowerTrendLine;
import okeanos.data.internal.services.pricing.regression.TrendLine;
import okeanos.data.services.entities.Price;

public class PriceImpl implements Price {

	private TrendLine trendline;

	public PriceImpl(double[] x, double[] y) {
		trendline = new PowerTrendLine();
		trendline.setValues(y, x);
	}

	@Override
	public double getCostAtConsumption(double consumption) {
		return trendline.predict(consumption);
	}

	@Override
	public String toString() {
		int[] x = new int[] { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
		double[] y = new double[] { trendline.predict(x[0]),
				trendline.predict(x[1]), trendline.predict(x[2]),
				trendline.predict(x[3]), trendline.predict(x[4]),
				trendline.predict(x[5]), trendline.predict(x[6]),
				trendline.predict(x[7]), trendline.predict(x[8]),
				trendline.predict(x[9]) };
		return String.format("PriceImpl\n[x=%s]\n[y=%s]", Arrays.toString(x),
				Arrays.toString(y));
	}
}
