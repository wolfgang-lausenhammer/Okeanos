package okeanos.control.internal.algorithms.pso.regenerativeload;

import javax.measure.quantity.Power;

import okeanos.control.entities.PossibleRun;

import org.jscience.physics.amount.Amount;

/**
 * The velocity in PSO.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Velocity {

	/** The chosen value. */
	private Amount<Power> chosenValue;

	/** The possible run. */
	private PossibleRun possibleRun;

	/**
	 * Instantiates a new velocity.
	 * 
	 * @param velocity
	 *            the velocity
	 */
	public Velocity(final PossibleRun possibleRun,
			final Amount<Power> chosenValue) {
		this.possibleRun = possibleRun;
		this.chosenValue = chosenValue;
	}

	/**
	 * Gets the possible run.
	 * 
	 * @return the possible run
	 */
	public PossibleRun getPossibleRun() {
		return possibleRun;
	}

	public Amount<Power> getChosenValue() {
		return chosenValue;
	}

	public void setChosenValue(Amount<Power> chosenValue) {
		this.chosenValue = chosenValue;
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
