package okeanos.math.regression;

/**
 * Models a trendline which approximates the data with a power of model.
 */
public class PowerTrendLine extends OLSTrendLine {

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.OLSTrendLine#xVector(double)
	 */
	@Override
	protected double[] xVector(double x) {
		return new double[] { 1, Math.log(x) };
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
