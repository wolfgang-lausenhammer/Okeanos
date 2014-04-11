package okeanos.math.regression;

import java.util.Arrays;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * The Class OLSTrendLine.
 * 
 * @author maybeWeCouldStealAVa @ stackoverflow.com
 */
public abstract class OLSTrendLine implements TrendLine {

	/** The coef. */
	private RealMatrix coef = null; // will hold prediction coefs once we get
									// values

	/**
	 * X vector.
	 * 
	 * @param x
	 *            the x
	 * @return the double[]
	 */
	protected abstract double[] xVector(double x); // create vector of values
	// from x

	/**
	 * Log y.
	 * 
	 * @return true, if successful
	 */
	protected abstract boolean logY(); // set true to predict log of y (note: y
	// must be positive)

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
		double[] myY = y;

		double[][] xData = new double[x.length][];
		for (int i = 0; i < x.length; i++) {
			// the implementation determines how to produce a vector of
			// predictors from a single x
			xData[i] = xVector(x[i]);
		}
		if (logY()) { // in some models we are predicting ln y, so we replace
						// each y with ln y
			myY = Arrays.copyOf(myY, myY.length); // user might not be finished
													// with
			// the array we were given
			for (int i = 0; i < x.length; i++) {
				myY[i] = Math.log(myY[i]);
			}
		}
		OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
		ols.setNoIntercept(true); // let the implementation include a constant
									// in xVector if desired
		ols.newSampleData(myY, xData); // provide the data to the model
		coef = MatrixUtils.createColumnRealMatrix(ols
				.estimateRegressionParameters()); // get our coefs
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.TrendLine#predict(double)
	 */
	@Override
	public double predict(final double x) {
		double yhat = coef.preMultiply(xVector(x))[0]; // apply coefs to xVector
		if (logY()) {
			yhat = (Math.exp(yhat)); // if we predicted ln y, we still need to
		}
		// get y
		return yhat;
	}
}