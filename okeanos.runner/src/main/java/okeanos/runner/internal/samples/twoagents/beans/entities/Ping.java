package okeanos.runner.internal.samples.twoagents.beans.entities;

import de.dailab.jiactng.agentcore.knowledge.IFact;

public class Ping implements IFact {
	private static final long serialVersionUID = -2524907185210608864L;

	private String message;

	public Ping(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return String.format("Ping [message=%s]", message);
	}

}
