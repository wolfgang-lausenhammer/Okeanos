package okeanos.math.regression.periodic;

import okeanos.math.regression.TrendLine;

/**
 * Takes all data and repeats the data if a point outside the data is wanted.
 * 
 * @author Wolfgang Lausenhammer
 */
public class PeriodicAllDataTrendLine implements PeriodicTrendLine {

	/** The wrapped trend line. */
	private TrendLine wrappedTrendLine;

	/** The lower x boundary. */
	private double lowerXBoundary = 0;

	/** The upper x boundary. */
	private double upperXBoundary = 0;

	/** The difference. */
	private double difference = 0;

	/**
	 * Instantiates a new periodic all data trend line.
	 * 
	 * @param wrappedTrendLine
	 *            the wrapped trend line
	 */
	public PeriodicAllDataTrendLine(final TrendLine wrappedTrendLine) {
		this.wrappedTrendLine = wrappedTrendLine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.TrendLine#setValues(double[], double[])
	 */
	@Override
	public void setValues(final double[] y, final double[] x) {
		wrappedTrendLine.setValues(y, x);

		lowerXBoundary = x[0];
		upperXBoundary = x[x.length - 1];
		difference = upperXBoundary - lowerXBoundary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.TrendLine#predict(double)
	 */
	@Override
	public double predict(final double x) {
		if (x >= lowerXBoundary && x <= upperXBoundary) {
			return wrappedTrendLine.predict(x);
		} else {
			double val = lowerXBoundary;
			double posX = x;
			while (posX < 0) {
				posX += difference;
			}

			double periodicX = 0;
			if (val < posX) {
				while (val < posX) {
					val += difference;
				}
				periodicX = posX - val + difference;
			} else {
				while (val > posX) {
					val -= difference;
				}
				periodicX = posX - val - difference;
			}

			/*
			 * double periodicX = absX % difference;
			 * 
			 * if (periodicX - lowestIntervalLowerBoundary >= difference) {
			 * periodicX = periodicX - difference; } else { periodicX =
			 * periodicX - lowestIntervalLowerBoundary; }
			 */

			return wrappedTrendLine.predict(periodicX + lowerXBoundary);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.periodic.PeriodicTrendLine#getXBoundaryMin()
	 */
	@Override
	public double getXBoundaryMin() {
		return lowerXBoundary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.periodic.PeriodicTrendLine#getXBoundaryMax()
	 */
	@Override
	public double getXBoundaryMax() {
		return upperXBoundary;
	}

}
