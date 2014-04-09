package okeanos.runner.internal.samples.twoagents.beans.entities;

import de.dailab.jiactng.agentcore.knowledge.IFact;

// TODO: Auto-generated Javadoc
/**
 * A simple Ping object that can contain a message. Derived from IFact to be
 * sendable by the communication services.
 */
public class Ping implements IFact {

	/** The serial version UID. */
	private static final long serialVersionUID = -2524907185210608864L;

	/** The actual message. */
	private String message;

	/**
	 * Instantiates a new ping.
	 * 
	 * @param message
	 *            the message
	 */
	public Ping(final String message) {
		this.message = message;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the new message
	 */
	public final void setMessage(final String message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Ping [message=%s]", message);
	}

}
