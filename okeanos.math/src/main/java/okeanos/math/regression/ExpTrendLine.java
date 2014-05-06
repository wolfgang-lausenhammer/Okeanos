package okeanos.math.regression;

/**
 * The Class ExpTrendLine.
 * 
 * @author maybeWeCouldStealAVa @ stackoverflow.com
 */
public class ExpTrendLine extends OLSTrendLine {

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.OLSTrendLine#xVector(double)
	 */
	@Override
	protected double[] xVector(final double x) {
		return new double[] { 1, x };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.OLSTrendLine#logY()
	 */
	@Override
	protected boolean logY() {
		return true;
	}
}
