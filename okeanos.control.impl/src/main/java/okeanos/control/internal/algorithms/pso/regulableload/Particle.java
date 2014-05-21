package okeanos.control.internal.algorithms.pso.regulableload;

import java.util.HashMap;
import java.util.Map;

import okeanos.control.entities.PossibleRun;

import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * A particle in PSO.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Particle {

	/** The best fitness. */
	private double bestFitness;

	/** The best position. */
	private Map<PossibleRun, DateTime> bestPosition;

	/** The fitness. */
	private double fitness;

	/** The position. */
	private Map<PossibleRun, DateTime> position;

	/** The velocity. */
	private Map<PossibleRun, Period> velocity;

	/**
	 * Instantiates a new particle.
	 * 
	 * @param position
	 *            the position
	 * @param fitness
	 *            the fitness
	 * @param velocity
	 *            the velocity
	 * @param bestPosition
	 *            the best position
	 * @param bestFitness
	 *            the best fitness
	 */
	public Particle(final Map<PossibleRun, DateTime> position,
			final double fitness, final Map<PossibleRun, Period> velocity,
			final Map<PossibleRun, DateTime> bestPosition,
			final double bestFitness) {
		this.position = new HashMap<>(position);
		this.fitness = fitness;
		this.velocity = new HashMap<>(velocity);
		this.bestPosition = new HashMap<>(bestPosition);
		this.bestFitness = bestFitness;
	}

	/**
	 * Gets the best fitness.
	 * 
	 * @return the best fitness
	 */
	public double getBestFitness() {
		return bestFitness;
	}

	/**
	 * Gets the best position.
	 * 
	 * @return the best position
	 */
	public Map<PossibleRun, DateTime> getBestPosition() {
		return bestPosition;
	}

	/**
	 * Gets the fitness.
	 * 
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * Gets the position.
	 * 
	 * @return the position
	 */
	public Map<PossibleRun, DateTime> getPosition() {
		return position;
	}

	/**
	 * Gets the velocity.
	 * 
	 * @return the velocity
	 */
	public Map<PossibleRun, Period> getVelocity() {
		return velocity;
	}

	/**
	 * Sets the best fitness.
	 * 
	 * @param bestFitness
	 *            the new best fitness
	 */
	public void setBestFitness(final double bestFitness) {
		this.bestFitness = bestFitness;
	}

	/**
	 * Sets the best position.
	 * 
	 * @param bestPosition
	 *            the best position
	 */
	public void setBestPosition(final Map<PossibleRun, DateTime> bestPosition) {
		this.bestPosition = bestPosition;
	}

	/**
	 * Sets the fitness.
	 * 
	 * @param fitness
	 *            the new fitness
	 */
	public void setFitness(final double fitness) {
		this.fitness = fitness;
	}

	/**
	 * Sets the position.
	 * 
	 * @param position
	 *            the position
	 */
	public void setPosition(final Map<PossibleRun, DateTime> position) {
		this.position = position;
	}

	/**
	 * Sets the velocity.
	 * 
	 * @param velocity
	 *            the velocity
	 */
	public void setVelocity(final Map<PossibleRun, Period> velocity) {
		this.velocity = velocity;
	}
}
