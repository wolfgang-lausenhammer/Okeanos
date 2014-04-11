package okeanos.math.regression;

/**
 * The Class PolyTrendLine.
 * 
 * @author maybeWeCouldStealAVa @ stackoverflow.com
 */
public class PolyTrendLine extends OLSTrendLine {

	/** The degree. */
	private final int degree;

	/**
	 * Instantiates a new poly trend line.
	 * 
	 * @param degree
	 *            the degree
	 */
	public PolyTrendLine(final int degree) {
		if (degree < 0) {
			throw new IllegalArgumentException(
					"The degree of the polynomial must not be negative");
		}
		this.degree = degree;
	}

	/**
	 * The x vector.
	 * 
	 * @param x
	 *            x values
	 * 
	 * @return the x vector
	 */
	protected double[] xVector(final double x) { // {1, x, x*x, x*x*x, ...}
		double[] poly = new double[degree + 1];
		double xi = 1;
		for (int i = 0; i <= degree; i++) {
			poly[i] = xi;
			xi *= x;
		}
		return poly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.OLSTrendLine#logY()
	 */
	@Override
	protected boolean logY() {
		return false;
	}
}
