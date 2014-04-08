package okeanos.math.regression;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Models a trendline, which "predicts" new values in that, that it keeps the
 * previous value as long as a new value arrives. So a desired value at position
 * 3.5 would result in the value for 3 to be returned, assumed that there is a
 * value defined for 3 and no other value between 3 and 3.5 defined.
 */
public class PreviousValueTrendLine implements TrendLine {

	/** The x-axis values. */
	private double[] x;

	/** The y-axis values. */
	private double[] y;

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.TrendLine#setValues(double[], double[])
	 */
	@Override
	public void setValues(double[] y, double[] x) {
		if (x.length != y.length) {
			throw new IllegalArgumentException(String.format(
					"The numbers of y and x values must be equal (%d != %d)",
					y.length, x.length));
		}

		this.x = ArrayUtils.clone(x);
		this.y = ArrayUtils.clone(y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.TrendLine#predict(double)
	 */
	@Override
	public double predict(double x) {
		for (int i = 1; i < this.x.length; i++) {
			if (this.x[i] > x) {
				return y[i - 1];
			}
		}

		return y[y.length - 1];
	}
}
