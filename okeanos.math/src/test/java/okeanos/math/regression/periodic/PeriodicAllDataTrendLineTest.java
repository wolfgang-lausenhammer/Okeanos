package okeanos.math.regression.periodic;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import okeanos.math.regression.PreviousValueTrendLine;
import okeanos.math.regression.TrendLine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The Class PeriodicAllDataTrendLineTest.
 */
@RunWith(Parameterized.class)
public class PeriodicAllDataTrendLineTest {

	/** The Constant X. */
	private static final double X[] = new double[] { 0, 5, 10 };

	/** The Constant Y. */
	private static final double Y[] = new double[] { 0, 1, 2 };

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { 0, 0 }, { 5, 1 }, { 9, 1 },
				{ 10, 2 }, { 11, 0 }, { 15, 1 }, { 16, 1 }, { 19, 1 },
				{ 20, 2 }, { 21, 0 }, { -1, 1 }, { -5, 1 }, { -2, 1 },
				{ -4, 1 }, { -7, 0 }, { -10, 0 } });
	}

	/** The trend line. */
	private TrendLine trendLine;

	/** The x forecast. */
	private double xForecast;

	/** The y expected. */
	private double yExpected;

	/**
	 * Instantiates a new periodic all data trend line test.
	 * 
	 * @param xForecast
	 *            the x forecast
	 * @param yExpected
	 *            the y expected
	 */
	public PeriodicAllDataTrendLineTest(double xForecast, double yExpected) {
		this.xForecast = xForecast;
		this.yExpected = yExpected;
	}

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		trendLine = new PreviousValueTrendLine();
		trendLine = new PeriodicAllDataTrendLine(trendLine);
		trendLine.setValues(Y, X);
	}

	/**
	 * Test differing array dimensions.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDifferingArrayDimensions() {
		trendLine.setValues(new double[] { 1, 2 }, new double[] { 0 });
	}

	/**
	 * Test predict.
	 */
	@Test
	public void testPredict() {
		double predictedValue = trendLine.predict(xForecast);

		assertThat(predictedValue, is(closeTo(yExpected, 0.0001)));
	}
}
