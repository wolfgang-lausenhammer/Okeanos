package okeanos.data.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

// TODO: Auto-generated Javadoc
/**
 * The TimeService interface provides an abstraction to the real world clock.
 * For simulations it is preferable to not have to wait for an event to happen
 * so long. Therefore, the pace of time can be adjusted accordingly (
 * {@link #setPace(double)}), to run faster. Similarly, it is possible to slow
 * down time ({@link #setPace(double)}).
 * 
 * Moreover, the clock can be set to a specific point in time to either satisfy
 * testing prerequisites or, again, to skip to the interesting parts of the day.
 * 
 * By invoking {@link #currentTimeMillis()} the current time in millis is
 * provided taking into account any calls to
 * {@link #setCurrentDateTime(DateTime)} and {@link #setPace(double)}. The
 * method can be seen as a replacement to {@link System#currentTimeMillis()}.
 * 
 * @see DateTimeUtils.MillisProvider
 * @see DateTimeUtils#currentTimeMillis()
 * @see System#currentTimeMillis()
 */
public interface TimeService {

	/**
	 * Sets the pace.
	 * 
	 * < -1 indicates slower time <br/>
	 * -1-0 indicates faster time (e.g. half as slow = double as fast) <br/>
	 * 0-1 indicates slower time (e.g. half as fast = double as slow) <br/>
	 * > 1 indicates faster time
	 * 
	 * -1,0,1 represent normal time
	 * 
	 * @param factor
	 *            the new pace
	 */
	void setPace(double factor);

	/**
	 * Sets the current date time to the specified point in time. Can be used to
	 * skip an uninteresting part of the day or to go back to a point in time
	 * for simulation or other use cases.
	 * 
	 * @param dateTime
	 *            the new point in time
	 */
	void setCurrentDateTime(DateTime dateTime);

	/**
	 * Returns the current time in millis. Is a replacement for
	 * {@link System#currentTimeMillis()}.
	 * 
	 * @return the difference, measured in milliseconds, between the current
	 *         time and midnight, January 1, 1970 UTC.
	 * 
	 * @see System#currentTimeMillis()
	 * @see DateTimeUtils#currentTimeMillis()
	 */
	long currentTimeMillis();

	/**
	 * Puts the current thread to sleep. If the pace is changed (
	 * {@link #setPace(double)}) the method will adjust the given time in ms
	 * accordingly, so that it will return sooner for a higher pace or later for
	 * a lower pace, respectively.
	 * 
	 * @param millis
	 *            the length of time to sleep in milliseconds
	 * 
	 * @throws IllegalArgumentException
	 *             if the value of millis is negative
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread. The
	 *             interrupted status of the current thread is cleared when this
	 *             exception is thrown.
	 * 
	 * @see Thread#sleep(long)
	 */
	void sleep(long millis) throws InterruptedException;

	/**
	 * Puts the current thread to sleep. If the pace is changed (
	 * {@link #setPace(double)}) the method will adjust the given time in ms and
	 * ns accordingly, so that it will return sooner for a higher pace or later
	 * for a lower pace, respectively.
	 * 
	 * @param millis
	 *            the length of time to sleep in milliseconds
	 * @param nanos
	 *            0-999999 additional nanoseconds to sleep
	 * 
	 * @throws IllegalArgumentException
	 *             if the value of millis is negative, or the value of nanos is
	 *             not in the range 0-999999
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread. The
	 *             interrupted status of the current thread is cleared when this
	 *             exception is thrown.
	 * 
	 * @see Thread#sleep(long,int)
	 */
	void sleep(long millis, int nanos) throws InterruptedException;
}
