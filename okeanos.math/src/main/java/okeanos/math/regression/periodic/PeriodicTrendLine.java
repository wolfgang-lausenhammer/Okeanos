package okeanos.math.regression.periodic;

import okeanos.math.regression.TrendLine;

/**
 * Provides a common interface for all periodic trendline functions. That is,
 * regression functions.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface PeriodicTrendLine extends TrendLine {

	/**
	 * Gets the lower boundary of the x axis.
	 * 
	 * @return the x boundary min
	 */
	double getXBoundaryMin();

	/**
	 * Gets the upper boundary of the y axis.
	 * 
	 * @return the x boundary max
	 */
	double getXBoundaryMax();
}
