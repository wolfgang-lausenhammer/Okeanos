package okeanos.math.regression.periodic;

import java.util.Arrays;

import okeanos.math.regression.TrendLine;

import org.joda.time.DateTime;

public class Periodic24hTrendline implements PeriodicTrendLine {
	private PeriodicTrendLine periodicTrendLine;
	private TrendLine trendLine;

	public Periodic24hTrendline(TrendLine trendLine) {
		this.trendLine = trendLine;
		this.periodicTrendLine = new PeriodicAllDataTrendLine(trendLine);
	}

	@Override
	public void setValues(double[] y, double[] x) {
		trendLine.setValues(y, x);
		DateTime beginning = new DateTime((long) x[0]);
		DateTime nextDay = beginning.plusHours(24);
		int numberOfItemsToCopy = 0;
		for (double item : x) {
			if (new DateTime((long) item).isAfter(nextDay)) {
				break;
			}

			numberOfItemsToCopy++;
		}

		double[] xNew = Arrays.copyOf(x, numberOfItemsToCopy + 1);
		double[] yNew = Arrays.copyOf(y, numberOfItemsToCopy + 1);
		xNew[numberOfItemsToCopy] = nextDay.getMillis();
		yNew[numberOfItemsToCopy] = trendLine.predict(nextDay.getMillis());

		periodicTrendLine.setValues(yNew, xNew);
	}

	@Override
	public double predict(double x) {
		return periodicTrendLine.predict(x);
	}

	@Override
	public double getXBoundaryMin() {
		return periodicTrendLine.getXBoundaryMin();
	}

	@Override
	public double getXBoundaryMax() {
		return periodicTrendLine.getXBoundaryMax();
	}

}
