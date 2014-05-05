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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.TaskScheduler;

/**
 * The Class TimeServiceImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class TimeServiceImplTest {

	/** The Constant HIGH_PACE. */
	private static final int HIGH_PACE = 4;

	/** The Constant PACE_EIGHT. */
	private static final int PACE_EIGHT = 8;

	/** The Constant SLEEP_TIME. */
	private static final long SLEEP_TIME = 100L;

	/** The Constant SLOW_PACE. */
	private static final double SLOW_PACE = 0.25;

	/** The Constant START_MILLIS. */
	private static final long START_MILLIS = 1000L;

	/** The Constant THOUSAND. */
	private static final long THOUSAND = 1000;

	/** The Constant TOLERANCE. */
	private static final long TOLERANCE = 15;

	/** The start nanos after. */
	private long startNanosAfter;

	/** The start nanos before. */
	private long startNanosBefore;

	/** The time service. */
	private TimeServiceImpl timeService;

	/** The task scheduler. */
	@Mock
	private TaskScheduler taskScheduler;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		DateTimeUtils.setCurrentMillisFixed(START_MILLIS);
		startNanosBefore = System.nanoTime();
		timeService = new TimeServiceImpl(taskScheduler);
		startNanosAfter = System.nanoTime();
	}

	/**
	 * Test get millis.
	 */
	@Test
	public void testGetMillis() {
		long millis = timeService.getMillis();

		long endNanos = System.nanoTime();
		long durationMillisWithObjectCreation = (endNanos - startNanosBefore)
				/ THOUSAND;
		long durationMillisWithoutObjectCreation = (endNanos - startNanosAfter)
				/ THOUSAND;

		assertThat(millis,
				is(anyOf(greaterThan(START_MILLIS), equalTo(START_MILLIS))));
		assertThat(millis, is(lessThan(durationMillisWithoutObjectCreation
				+ START_MILLIS)));
		assertThat(millis, is(lessThan(durationMillisWithObjectCreation
				+ START_MILLIS)));
	}

	/**
	 * Test set current date time.
	 */
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

	/**
	 * Test set pace.
	 */
	@Test
	public void testSetPace() {
		int pace = PACE_EIGHT;

		timeService.setPace(pace);

		long millisBefore = timeService.getMillis();
		long nanosBefore = System.nanoTime();
		for (int i = (int) (THOUSAND * THOUSAND); i > 0; i--) {
		}
		long nanosAfter = System.nanoTime();
		long millisAfter = timeService.getMillis();

		long differenceMillis = millisAfter - millisBefore;
		long differenceNanosInMillisInner = (nanosAfter - nanosBefore)
				/ THOUSAND / THOUSAND;

		assertThat(millisBefore,
				is(anyOf(greaterThan(START_MILLIS), equalTo(START_MILLIS))));
		assertThat(millisAfter, is(greaterThan(START_MILLIS)));

		assertThat(
				differenceMillis,
				is(anyOf(greaterThan(differenceNanosInMillisInner * pace),
						equalTo(differenceNanosInMillisInner * pace))));
	}

	/**
	 * Test sleep millis.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testSleepMillis() throws InterruptedException {
		long before = System.nanoTime();

		timeService.sleep(SLEEP_TIME);

		long after = System.nanoTime();
		long difference = after - before;
		assertThat(difference,
				is(anyOf(greaterThan(SLEEP_TIME), equalTo(SLEEP_TIME))));
	}

	/**
	 * Test sleep millis high pace.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Ignore
	@Test
	public void testSleepMillisHighPace() throws InterruptedException {
		int pace = HIGH_PACE;

		timeService.setPace(pace);
		long before = System.nanoTime();

		timeService.sleep(SLEEP_TIME);

		long after = System.nanoTime();
		long difference = (after - before) / THOUSAND / THOUSAND;
		assertThat(difference, is(lessThan(SLEEP_TIME)));
		assertThat(
				difference,
				is(allOf(
						greaterThan((long) ((SLEEP_TIME - 2 * TOLERANCE) / pace)),
						lessThan((long) ((SLEEP_TIME + 2 * TOLERANCE) / pace)))));
	}

	/**
	 * Test sleep millis slow pace.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testSleepMillisSlowPace() throws InterruptedException {
		double pace = SLOW_PACE;

		timeService.setPace(pace);
		long before = System.nanoTime();

		timeService.sleep(SLEEP_TIME);

		long after = System.nanoTime();
		long difference = (after - before) / THOUSAND / THOUSAND;
		assertThat(difference, is(greaterThan(SLEEP_TIME)));
		assertThat(
				difference,
				is(allOf(greaterThan((long) ((SLEEP_TIME - TOLERANCE) / pace)),
						lessThan((long) ((SLEEP_TIME + TOLERANCE) / pace)))));
	}

	/**
	 * Test time service impl.
	 */
	@Test
	public void testTimeServiceImpl() {
		long millis = timeService.currentTimeMillis();

		assertThat(millis,
				is(anyOf(greaterThan(START_MILLIS), equalTo(START_MILLIS))));
	}
}
