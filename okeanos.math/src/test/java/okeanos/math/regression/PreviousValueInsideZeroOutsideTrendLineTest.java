package okeanos.math.regression;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The Class PreviousValueTrendLineTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class PreviousValueInsideZeroOutsideTrendLineTest {

	/** The Constant X. */
	private static final double[] X = new double[] { 0, 2, 5, 9, 14, 20, 27,
			35, 44, 54, 66 };

	/** The Constant Y. */
	private static final double[] Y = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9,
			10, 11 };

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { 0, 1 }, { 1, 1 }, { 6, 3 },
				{ -1, 0 }, { 19, 5 }, { 50, 9 }, { 100, 0 } });
	}

	/** The trend line. */
	private TrendLine trendLine;

	/** The x forecast. */
	private double xForecast;

	/** The y expected. */
	private double yExpected;

	/**
	 * Instantiates a new previous value trend line test.
	 * 
	 * @param xForecast
	 *            the x forecast
	 * @param yExpected
	 *            the y expected
	 */
	public PreviousValueInsideZeroOutsideTrendLineTest(final double xForecast,
			final double yExpected) {
		this.xForecast = xForecast;
		this.yExpected = yExpected;
	}

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		trendLine = new PreviousValueInsideZeroOutsideTrendLine();
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
