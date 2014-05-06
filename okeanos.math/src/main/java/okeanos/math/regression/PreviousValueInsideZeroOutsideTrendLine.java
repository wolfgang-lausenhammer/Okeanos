package okeanos.math.regression;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Models a trendline, which "predicts" new values in that, that it keeps the
 * previous value as long as a new value arrives inside the available values. So
 * a desired value at position 3.5 would result in the value for 3 to be
 * returned, assumed that there is a value defined for 3 and no other value
 * between 3 and 3.5 defined. <br>
 * Assuming that there are values defined between 0 and 2, prediction of values
 * outside that interval would result in 0 being returned.
 * 
 * @author Wolfgang Lausenhammer
 */
public class PreviousValueInsideZeroOutsideTrendLine implements TrendLine {

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
	public void setValues(final double[] y, final double[] x) {
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
	public double predict(final double x) {
		// value smaller than first value
		if (x < this.x[0]) {
			return 0;
		}

		// value in the middle of other values, get the previous value
		for (int i = 1; i < this.x.length; i++) {
			if (this.x[i] > x) {
				return y[i - 1];
			}
		}

		// value bigger than last value
		return 0;
	}
}
