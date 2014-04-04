package okeanos.control.entities;

/**
 * A device can be either a producer or a consumer at one time, both at the same
 * time is not possible.
 * 
 * @author Wolfgang Lausenhammer
 * 
 */
public enum LoadType {

	/** The consumer. */
	CONSUMER,

	/** The producer. */
	PRODUCER
}
