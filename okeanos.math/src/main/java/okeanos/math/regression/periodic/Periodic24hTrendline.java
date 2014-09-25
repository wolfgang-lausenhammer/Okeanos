package okeanos.math.regression.periodic;

import java.util.Arrays;

import okeanos.math.regression.TrendLine;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Represents a 24h periodic trendline. Values are repeated every 24h.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Periodic24hTrendline implements PeriodicTrendLine {

	/** The Constant TWENTY_FOUR. */
	private static final int TWENTY_FOUR = 24;

	/** The periodic trend line. */
	private PeriodicTrendLine periodicTrendLine;

	private DateTime referenceStartOfDay;

	/** The trend line. */
	private TrendLine trendLine;

	/**
	 * Instantiates a new periodic24h trendline.
	 * 
	 * @param trendLine
	 *            the trend line
	 */
	public Periodic24hTrendline(final TrendLine trendLine) {
		this.trendLine = trendLine;
		this.periodicTrendLine = new PeriodicAllDataTrendLine(trendLine);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.periodic.PeriodicTrendLine#getXBoundaryMax()
	 */
	@Override
	public double getXBoundaryMax() {
		return periodicTrendLine.getXBoundaryMax();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.periodic.PeriodicTrendLine#getXBoundaryMin()
	 */
	@Override
	public double getXBoundaryMin() {
		return periodicTrendLine.getXBoundaryMin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.TrendLine#predict(double)
	 */
	@Override
	public double predict(final double x) {
		DateTime normalizedX = new DateTime((long) x, DateTimeZone.UTC);
		normalizedX = normalizedX.withDate(referenceStartOfDay.getYear(),
				referenceStartOfDay.getMonthOfYear(),
				referenceStartOfDay.getDayOfMonth());

		return periodicTrendLine.predict(x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.math.regression.TrendLine#setValues(double[], double[])
	 */
	@Override
	public void setValues(final double[] y, final double[] x) {
		trendLine.setValues(y, x);
		if (x.length > 0) {
			referenceStartOfDay = new DateTime((long) x[0], DateTimeZone.UTC);
		} else {
			referenceStartOfDay = DateTime.now(DateTimeZone.UTC);
		}
		DateTime nextDay = referenceStartOfDay.plusHours(TWENTY_FOUR)
				.minusMillis(1);
		int numberOfItemsToCopy = 0;
		for (double item : x) {
			if (new DateTime((long) item).isAfter(nextDay)) {
				break;
			}

			numberOfItemsToCopy++;
		}

		double[] xNew = Arrays.copyOf(x, numberOfItemsToCopy + 1);
		double[] yNew = Arrays.copyOf(y, numberOfItemsToCopy + 1);
		xNew[numberOfItemsToCopy] = nextDay.getMillis();
		yNew[numberOfItemsToCopy] = trendLine.predict(nextDay.getMillis());

		periodicTrendLine.setValues(yNew, xNew);
	}

}
