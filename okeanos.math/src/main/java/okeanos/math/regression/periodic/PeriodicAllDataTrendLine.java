package okeanos.math.regression.periodic;

import okeanos.math.regression.TrendLine;

public class PeriodicAllDataTrendLine implements PeriodicTrendLine {
	private TrendLine wrappedTrendLine;
	private double lowerXBoundary = 0;
	private double upperXBoundary = 0;
	private double lowestIntervalLowerBoundary = 0;
	private double difference = 0;

	public PeriodicAllDataTrendLine(TrendLine wrappedTrendLine) {
		this.wrappedTrendLine = wrappedTrendLine;
	}

	@Override
	public void setValues(double[] y, double[] x) {
		wrappedTrendLine.setValues(y, x);

		lowerXBoundary = x[0];
		upperXBoundary = x[x.length];
		lowestIntervalLowerBoundary = lowerXBoundary % upperXBoundary
				- lowerXBoundary;
		difference = upperXBoundary - lowerXBoundary;
	}

	@Override
	public double predict(double x) {
		if (x >= lowerXBoundary && x <= upperXBoundary) {
			return wrappedTrendLine.predict(x);
		} else {
			double periodicX = x % difference;

			if (periodicX - lowestIntervalLowerBoundary >= difference) {
				periodicX = periodicX - difference;
			} else {
				periodicX = periodicX - lowestIntervalLowerBoundary;
			}

			return wrappedTrendLine.predict(periodicX + lowerXBoundary);
		}
	}

	@Override
	public double getXBoundaryMin() {
		return lowerXBoundary;
	}

	@Override
	public double getXBoundaryMax() {
		return upperXBoundary;
	}

}
