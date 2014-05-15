package okeanos.data.internal.services;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import okeanos.data.services.TimeService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeUtils.MillisProvider;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * An implementation of {@code TimeService}.
 * 
 * @author Wolfgang Lausenhammer
 */
@SuppressWarnings("rawtypes")
@Component("timeService")
public class TimeServiceImpl implements TimeService, MillisProvider {
	/** The Logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(TimeServiceImpl.class);

	/** The Constant MILLIS_IN_SECONDS. */
	private static final int MILLIS_IN_SECONDS = 1000;

	/** The Constant NANOS_IN_MILLIS. */
	private static final long NANOS_IN_MILLIS = 1000000;

	/** The pace. */
	private AtomicDouble pace = new AtomicDouble();

	/** The reference time in millis. */
	private AtomicLong referenceMillis = new AtomicLong();

	/**
	 * The elapsed time since the last call to
	 * {@link #setCurrentDateTime(DateTime)}. Used to calculate the delta
	 * between the point in time ({@link #referenceMillis}) and the call to
	 * {@link #getMillis()}.
	 */
	private AtomicLong referenceNano = new AtomicLong();

	/** The default task scheduler. */
	private TaskScheduler defaultTaskScheduler;

	/**
	 * Instantiates a new time service impl.
	 * 
	 * @param defaultTaskScheduler
	 *            the default task scheduler
	 */
	@Inject
	public TimeServiceImpl(final TaskScheduler defaultTaskScheduler) {
		DateTimeZone.setDefault(DateTimeZone.UTC);
		setCurrentDateTime(DateTime.now());
		DateTimeUtils.setCurrentMillisProvider(this);
		this.defaultTaskScheduler = defaultTaskScheduler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#currentTimeMillis()
	 */
	@Override
	public long currentTimeMillis() {
		return getMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.joda.time.DateTimeUtils.MillisProvider#getMillis()
	 */
	@Override
	public long getMillis() {
		long difference = System.nanoTime() - referenceNano.get();

		return referenceMillis.get() + (long) (difference * pace.get())
				/ NANOS_IN_MILLIS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.TimeService#setCurrentDateTime(org.joda.time.DateTime
	 * )
	 */
	@Override
	public void setCurrentDateTime(final DateTime dateTime) {
		LOG.debug("Changing date time from [{}] to [{}]", DateTime.now(),
				dateTime);
		pace.set(1);
		referenceNano.set(System.nanoTime());
		referenceMillis.set(dateTime.getMillis());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#setPace(double)
	 */
	@Override
	public void setPace(final double factor) {
		setCurrentDateTime(DateTime.now());
		if (factor >= 0) {
			pace.set(factor);
		} else {
			pace.set(-1 / factor);
		}
		LOG.debug("Pace set to {}", pace.get());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#sleep(long)
	 */
	@Override
	public void sleep(final long millis) throws InterruptedException {
		sleep(millis, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#sleep(long, int)
	 */
	@Override
	public void sleep(final long millis, final int nanos)
			throws InterruptedException {

		long realMillis = (long) (millis / pace.get());
		int realNanos = (int) (nanos / pace.get());
		LOG.trace(
				"{} - Thread {} - Requested to go to sleep for {}ms and {}ns, actually {}ms and {}ns @ {}",
				DateTime.now(), Thread.currentThread(), millis, nanos,
				realMillis, realNanos, pace.get());

		Thread.sleep(realMillis);

		LOG.trace("{} - Thread {} - Waked up", DateTime.now(),
				Thread.currentThread());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#schedule(java.lang.Runnable,
	 * org.joda.time.DateTime)
	 */
	@Override
	public ScheduledFuture schedule(final Runnable task,
			final DateTime startTime) {
		return schedule(task, startTime, defaultTaskScheduler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#schedule(java.lang.Runnable,
	 * org.joda.time.Period)
	 */
	@Override
	public ScheduledFuture schedule(final Runnable task, final Period duration) {
		return schedule(task, duration, defaultTaskScheduler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#schedule(java.lang.Runnable,
	 * org.joda.time.DateTime, org.springframework.scheduling.TaskScheduler)
	 */
	@Override
	public ScheduledFuture schedule(final Runnable task,
			final DateTime startTime, final TaskScheduler taskScheduler) {
		return taskScheduler.schedule(task, startTime.toDate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#schedule(java.lang.Runnable,
	 * org.joda.time.Period, org.springframework.scheduling.TaskScheduler)
	 */
	@Override
	public ScheduledFuture schedule(final Runnable task, final Period duration,
			final TaskScheduler taskScheduler) {
		long millis = (long) ((duration.getMillis() + (duration
				.toStandardSeconds().getSeconds() - duration.getMillis()
				/ MILLIS_IN_SECONDS)
				* MILLIS_IN_SECONDS) / pace.get());
		LOG.trace("{} - Scheduled future to run in {}ms",
				DateTime.now(DateTimeZone.UTC), millis);
		return schedule(task,
				new DateTime(System.currentTimeMillis()).plus(millis),
				taskScheduler);
	}
}
