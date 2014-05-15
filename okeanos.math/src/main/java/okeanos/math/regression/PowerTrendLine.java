package okeanos.math.regression;

/**
 * Models a trendline which approximates the data with a power of model.
 * 
 * @author Wolfgang Lausenhammer
 */
public class PowerTrendLine extends OLSTrendLine {

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.OLSTrendLine#logY()
	 */
	@Override
	protected boolean logY() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.OLSTrendLine#xVector(double)
	 */
	@Override
	protected double[] xVector(final double x) {
		if (x == 0) {
			return new double[] { 1, Math.log(1) };
		}
		return new double[] { 1, Math.log(x) };
	}
}
