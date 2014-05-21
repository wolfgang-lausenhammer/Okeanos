package okeanos.control.internal.algorithms.pso.regenerativeload;

import javax.measure.quantity.Power;

import okeanos.control.entities.PossibleRun;

import org.jscience.physics.amount.Amount;

/**
 * The position of PSO.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Position {

	/** The chosen value. */
	private Amount<Power> chosenValue;

	/** The dimensions. */
	private PossibleRun possibleRun;

	/**
	 * Instantiates a new position.
	 * 
	 * @param possibleRun
	 *            the possible run
	 * @param chosenValue
	 *            the chosen value
	 */
	public Position(final PossibleRun possibleRun,
			final Amount<Power> chosenValue) {
		this.possibleRun = possibleRun;
		this.chosenValue = chosenValue;
	}

	/**
	 * Gets the chosen value.
	 * 
	 * @return the chosen value
	 */
	public Amount<Power> getChosenValue() {
		return chosenValue;
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
	 * Sets the chosen value.
	 * 
	 * @param chosenValue
	 *            the new chosen value
	 */
	public void setChosenValue(final Amount<Power> chosenValue) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Position [possibleRun=%s, chosenValue=%s]",
				possibleRun, chosenValue);
	}
}
