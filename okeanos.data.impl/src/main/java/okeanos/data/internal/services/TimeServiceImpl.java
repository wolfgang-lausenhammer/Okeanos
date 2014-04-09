package okeanos.data.internal.services;

import java.util.concurrent.atomic.AtomicLong;

import okeanos.data.services.TimeService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeUtils.MillisProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * An implementation of {@code TimeService}.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("timeService")
public class TimeServiceImpl implements TimeService, MillisProvider {
	/** The Logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(TimeServiceImpl.class);

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

	/**
	 * Instantiates a new time service impl.
	 */
	public TimeServiceImpl() {
		LOG.debug("{} - 1.1Current date time [{}]", DateTime.now(),
				DateTime.now());
		LOG.debug("{} - 1.2Current date time [{}]", DateTime.now(),
				DateTime.now());
		LOG.debug("{} - 1.3Current date time [{}]", DateTime.now(),
				DateTime.now());
		LOG.debug("{} - 1.4Current date time [{}]", DateTime.now(),
				DateTime.now());
		setCurrentDateTime(DateTime.now());
		DateTimeUtils.setCurrentMillisProvider(this);
		LOG.debug("{} - 2Current date time [{}]", DateTime.now(),
				DateTime.now());
		LOG.debug("{} - 3Current date time [{}]", DateTime.now(),
				DateTime.now());
		LOG.debug("{} - 4Current date time [{}]", DateTime.now(),
				DateTime.now());
		LOG.debug("{} - 5Current date time [{}]", DateTime.now(),
				DateTime.now());
		LOG.debug("{} - 6Current date time [{}]", DateTime.now(),
				DateTime.now());
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

		return referenceMillis.get() + (long) (difference * pace.get()) / 1000
				/ 1000;
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
		LOG.debug("{} - Current date time [{}]", DateTime.now(), DateTime.now());
		pace.set(1);
		long before = DateTime.now().getMillis();
		long beforeNano = System.nanoTime();
		referenceNano.set(beforeNano);
		referenceMillis.set(dateTime.getMillis());
		long after = DateTime.now().getMillis();
		LOG.debug("{} - Current date time set to [{}]", DateTime.now(),
				dateTime);
		LOG.debug("before {}, after {}", before, after);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.TimeService#setPace(double)
	 */
	@Override
	public void setPace(final double factor) {
		setCurrentDateTime(DateTime.now());
		pace.set((factor >= 0) ? factor : -1 / factor);
		LOG.debug("{} - Pace set to {}", DateTime.now(), pace.get());
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
		LOG.trace(
				"{} - Thread {} - Requested to go to sleep for {}ms and {}ns, actually {}ms and {}ns @ {}",
				DateTime.now(), Thread.currentThread(), millis, nanos,
				(long) (millis / pace.get()), (int) (nanos / pace.get()),
				pace.get());

		Thread.sleep((long) (millis / pace.get()), (int) (nanos / pace.get()));

		LOG.trace("{} - Thread {} - Waked up", DateTime.now(),
				Thread.currentThread());
	}
}
