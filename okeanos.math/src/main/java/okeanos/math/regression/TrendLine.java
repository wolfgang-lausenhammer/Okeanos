package okeanos.math.regression;

/**
 * Provides a common interface for all trendline functions. That is, regression
 * functions.
 * 
 * @author maybeWeCouldStealAVa @ stackoverflow.com
 */
public interface TrendLine {

	/**
	 * Returns a predicted y for a given x value.
	 * 
	 * @param x
	 *            the x
	 * @return the double
	 */
	double predict(double x);

	/**
	 * Sets the known values for predictions. Used to calculate the parameters.
	 * 
	 * @param y
	 *            the y
	 * @param x
	 *            the x
	 */
	void setValues(double[] y, double[] x);
}