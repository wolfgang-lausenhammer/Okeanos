package okeanos.math.regression.periodic;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import okeanos.math.regression.PreviousValueInsideZeroOutsideTrendLine;
import okeanos.math.regression.PreviousValueTrendLine;
import okeanos.math.regression.TrendLine;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The Class Periodic24hTrendlineTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class Periodic24hTrendlineTest {

	/** The Constant START_OF_DAY. */
	private static final DateTime START_OF_DAY = DateTime.now(DateTimeZone.UTC)
			.withTimeAtStartOfDay();
	/** The Constant X. */
	private static final double[] X = new double[] { START_OF_DAY.getMillis(),
			START_OF_DAY.plusHours(1).getMillis(),
			START_OF_DAY.plusHours(2).getMillis(),
			START_OF_DAY.withTime(23, 45, 0, 0).getMillis() };

	/** The Constant Y. */
	private static final double[] Y = new double[] { 1, 2, 3, 4 };

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ START_OF_DAY.getMillis(), 1 },
				{ START_OF_DAY.plusMinutes(10).getMillis(), 1 },
				{ START_OF_DAY.plusHours(1).plusMinutes(10).getMillis(), 2 },
				{ START_OF_DAY.plusHours(2).plusMinutes(10).getMillis(), 3 },
				{ START_OF_DAY.plusHours(3).plusMinutes(10).getMillis(), 3 },
				{ START_OF_DAY.plusHours(4).plusMinutes(10).getMillis(), 3 },
				{ START_OF_DAY.plusHours(5).plusMinutes(10).getMillis(), 3 },
				{ START_OF_DAY.withTime(23, 45, 0, 0).getMillis(), 4 },
				{ START_OF_DAY.withTime(23, 50, 0, 0).getMillis(), 4 },
				{ START_OF_DAY.plusDays(1).plusMinutes(10).getMillis(), 1 },
				{
						START_OF_DAY.plusDays(1).plusHours(1).plusMinutes(10)
								.getMillis(), 2 },
				{
						START_OF_DAY.plusDays(1).plusHours(2).plusMinutes(10)
								.getMillis(), 3 } });
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
	public Periodic24hTrendlineTest(final double xForecast,
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
		trendLine = new Periodic24hTrendline(trendLine);
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