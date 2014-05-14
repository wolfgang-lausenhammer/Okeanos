package okeanos.math.regression;

/**
 * The Class LogTrendLine.
 * 
 * @author maybeWeCouldStealAVa @ stackoverflow.com
 */
public class LogTrendLine extends OLSTrendLine {

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.OLSTrendLine#logY()
	 */
	@Override
	protected boolean logY() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.OLSTrendLine#xVector(double)
	 */
	@Override
	protected double[] xVector(final double x) {
		return new double[] { 1, Math.log(x) };
	}
}
