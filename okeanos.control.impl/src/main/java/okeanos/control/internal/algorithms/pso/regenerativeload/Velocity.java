package okeanos.control.internal.algorithms.pso.regenerativeload;

import okeanos.control.entities.PossibleRun;

/**
 * The velocity in PSO.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Velocity {

	/** The delta. */
	private double delta;

	/** The possible run. */
	private PossibleRun possibleRun;

	/**
	 * Instantiates a new velocity.
	 */
	public Velocity() {
	}

	/**
	 * Instantiates a new velocity.
	 * 
	 * @param velocity
	 *            the velocity
	 */
	public Velocity(final Velocity velocity) {
		this.delta = velocity.delta;
		this.possibleRun = velocity.possibleRun;
	}

	/**
	 * Gets the delta.
	 * 
	 * @return the delta
	 */
	public double getDelta() {
		return delta;
	}

	/**
	 * Gets the possible run.
	 * 
	 * @return the possible run
	 */
	public PossibleRun getPossibleRun() {
		return possibleRun;
	}

	/**
	 * Sets the delta.
	 * 
	 * @param delta
	 *            the new delta
	 */
	public void setDelta(final double delta) {
		this.delta = delta;
	}

	/**
	 * Sets the possible run.
	 * 
	 * @param possibleRun
	 *            the new possible run
	 */
	public void setPossibleRun(final PossibleRun possibleRun) {
		this.possibleRun = possibleRun;
	}
}
