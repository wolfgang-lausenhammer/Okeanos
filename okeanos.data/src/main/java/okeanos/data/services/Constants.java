package okeanos.data.services;

/**
 * Contains necessary global constants.
 * 
 * @author Wolfgang Lausenhammer
 */
public final class Constants {

	/**
	 * Defines the granularity of the intervals. 15 minutes is the default (4
	 * times an hour). Reduce the value to get finer grained intervals.
	 */
	public static final int SLOT_INTERVAL = 15;

	/**
	 * Defines the refresh rate after which the pricing resource is checked for
	 * updates.
	 */
	public static final int PRICING_SERVICE_RESOURCE_REFRESH_RATE_MS = 1000;

	/**
	 * Instantiates a new constants.
	 */
	private Constants() {
	}
}
