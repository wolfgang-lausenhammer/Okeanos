package okeanos.data.internal.services;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeUtils.MillisProvider;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AtomicDouble;

import okeanos.data.services.TimeService;

@Component
public class TimeServiceImpl implements TimeService, MillisProvider {
	private AtomicDouble pace = new AtomicDouble();
	private AtomicLong referenceMillis = new AtomicLong();
	private AtomicLong lastSystemNano = new AtomicLong();

	public TimeServiceImpl() {
		setCurrentDateTime(DateTime.now());
		DateTimeUtils.setCurrentMillisProvider(this);
		System.out.println("TimeServiceImpl constructor called");
	}

	@Override
	public long currentTimeMillis() {
		return getMillis();
	}

	@Override
	public long getMillis() {
		long newSystemNano = System.nanoTime();
		long oldSystemNano = lastSystemNano.getAndSet(newSystemNano);
		long difference = newSystemNano - oldSystemNano;

		return referenceMillis.get() + (long) (difference * pace.get() / 1000);
	}

	@Override
	public void setCurrentDateTime(DateTime dateTime) {
		pace.set(1);
		lastSystemNano.set(System.nanoTime());
		referenceMillis.set(dateTime.getMillis());
	}

	@PostConstruct
	private void setMillisProvider() {
		System.out.println("@PostConstruct setMillisProvider called");
		System.out.println("@PostConstruct setMillisProvider called");
		System.out.println("@PostConstruct setMillisProvider called");
		System.out.println("@PostConstruct setMillisProvider called");
		System.out.println("@PostConstruct setMillisProvider called");
		System.out.println("@PostConstruct setMillisProvider called");
		System.out.println("@PostConstruct setMillisProvider called");
		System.out.println("@PostConstruct setMillisProvider called");
		System.out.println("@PostConstruct setMillisProvider called");
		//DateTimeUtils.setCurrentMillisProvider(this);
	}

	@Override
	public void setPace(double factor) {
		setCurrentDateTime(DateTime.now());
		pace.set((factor >= 0) ? factor : -1 / factor);
	}
}
