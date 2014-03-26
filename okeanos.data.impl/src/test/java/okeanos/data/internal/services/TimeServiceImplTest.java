package okeanos.data.internal.services;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TimeServiceImplTest {
	private static final long START_MILLIS = 1000L;
	private TimeServiceImpl timeService;
	private long startNanosBefore;
	private long startNanosAfter;

	@Before
	public void setUp() throws Exception {
		DateTimeUtils.setCurrentMillisFixed(START_MILLIS);
		startNanosBefore = System.nanoTime();
		timeService = new TimeServiceImpl();
		startNanosAfter = System.nanoTime();
	}

	@Test
	public void testTimeServiceImpl() {
		long millis = timeService.currentTimeMillis();

		Assert.assertThat(millis, is(greaterThan(START_MILLIS)));
	}

	@Test
	public void testGetMillis() {
		long millis = timeService.getMillis();

		long endNanos = System.nanoTime();
		long durationMillisWithObjectCreation = (endNanos - startNanosBefore) / 1000;
		long durationMillisWithoutObjectCreation = (endNanos - startNanosAfter) / 1000;

		Assert.assertThat(millis, is(greaterThan(START_MILLIS)));
		Assert.assertThat(millis,
				is(greaterThan(durationMillisWithoutObjectCreation
						+ START_MILLIS)));
		Assert.assertThat(millis, is(lessThan(durationMillisWithObjectCreation
				+ START_MILLIS)));
	}

	@Test
	public void testSetCurrentDateTime() {
		DateTime dateTime = new DateTime(START_MILLIS * 2);

		timeService.setCurrentDateTime(dateTime);

		long millis = timeService.getMillis();
		Assert.assertThat(millis, is(greaterThan(START_MILLIS * 2)));
	}

	@Test
	public void testSetPace() {
		int PACE = 8;

		timeService.setPace(PACE);

		long nanosBeforeBefore = System.nanoTime();
		long millisBefore = timeService.getMillis();
		long nanosBefore = System.nanoTime();
		for (int i = 1000000; i > 0; i--)
			;
		long nanosAfter = System.nanoTime();
		long millisAfter = timeService.getMillis();
		long nanosAfterAfter = System.nanoTime();

		long differenceMillis = millisAfter - millisBefore;
		long differenceNanosInMillisInner = (nanosAfter - nanosBefore) / 1000;
		long differenceNanosInMillisOuter = (nanosAfterAfter - nanosBeforeBefore) / 1000;

		Assert.assertThat(millisBefore, is(greaterThan(START_MILLIS)));
		Assert.assertThat(millisAfter, is(greaterThan(START_MILLIS)));

		Assert.assertThat(differenceMillis,
				is(lessThan(differenceNanosInMillisOuter * PACE)));
		Assert.assertThat(differenceMillis,
				is(greaterThan(differenceNanosInMillisInner * PACE)));
	}

}
