package okeanos.data.internal.services;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

public class TimeServiceImplTest {
	private static final long START_MILLIS = 1000L;
	private static final long SLEEP_TIME = 100L;
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

		assertThat(millis, is(greaterThan(START_MILLIS)));
	}

	@Test
	public void testGetMillis() {
		long millis = timeService.getMillis();

		long endNanos = System.nanoTime();
		long durationMillisWithObjectCreation = (endNanos - startNanosBefore) / 1000;
		long durationMillisWithoutObjectCreation = (endNanos - startNanosAfter) / 1000;

		assertThat(millis, is(greaterThan(START_MILLIS)));
		assertThat(
				millis + 1,
				is(anyOf(greaterThan(durationMillisWithoutObjectCreation
						+ START_MILLIS),
						equalTo(durationMillisWithoutObjectCreation
								+ START_MILLIS))));
		assertThat(millis, is(lessThan(durationMillisWithObjectCreation
				+ START_MILLIS)));
	}

	@Test
	public void testSetCurrentDateTime() {
		DateTime dateTime = new DateTime(START_MILLIS * 2);

		timeService.setCurrentDateTime(dateTime);

		long millis = timeService.getMillis();
		assertThat(
				millis,
				is(anyOf(greaterThan(START_MILLIS * 2 - 1),
						equalTo(START_MILLIS * 2))));
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

		assertThat(millisBefore, is(greaterThan(START_MILLIS)));
		assertThat(millisAfter, is(greaterThan(START_MILLIS)));

		assertThat(differenceMillis, is(lessThan(differenceNanosInMillisOuter
				* PACE)));
		assertThat(
				differenceMillis,
				is(anyOf(greaterThan(differenceNanosInMillisInner * PACE),
						equalTo(differenceNanosInMillisInner * PACE))));
	}

	@Test
	public void testSleepMillis() throws InterruptedException {
		long before = System.nanoTime();

		timeService.sleep(SLEEP_TIME);

		long after = System.nanoTime();
		long difference = after - before;
		assertThat(difference,
				is(anyOf(greaterThan(SLEEP_TIME), equalTo(SLEEP_TIME))));
	}

	@Test
	public void testSleepMillisSlowPace() throws InterruptedException {
		double PACE = 0.25;

		timeService.setPace(PACE);
		long before = System.nanoTime();

		timeService.sleep(SLEEP_TIME);

		long after = System.nanoTime();
		long difference = (after - before) / 1000 / 1000;
		assertThat(difference, is(greaterThan(SLEEP_TIME)));
		assertThat(
				difference,
				is(allOf(greaterThan((long) ((SLEEP_TIME - 10) / PACE)),
						lessThan((long) ((SLEEP_TIME + 10) / PACE)))));
	}

	@Test
	public void testSleepMillisHighPace() throws InterruptedException {
		int PACE = 4;

		timeService.setPace(PACE);
		long before = System.nanoTime();

		timeService.sleep(SLEEP_TIME);

		long after = System.nanoTime();
		long difference = (after - before) / 1000 / 1000;
		assertThat(difference, is(lessThan(SLEEP_TIME)));
		assertThat(
				difference,
				is(allOf(greaterThan((long) ((SLEEP_TIME - 10) / PACE)),
						lessThan((long) ((SLEEP_TIME + 10) / PACE)))));
	}
}
