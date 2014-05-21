package okeanos.control.internal.algorithms.pso.regenerativeload;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.joda.time.DateTime;

/**
 * A particle in PSO.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Particle {

	/** The best fitness. */
	private double bestFitness;

	/** The best position. */
	private Map<DateTime, Position> bestPosition;

	/** The current position. */
	private Map<DateTime, Position> currentPosition;

	/** The fitness. */
	private double fitness;

	/** The velocity. */
	private Map<DateTime, Position> velocity;

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
	public Particle(final Map<DateTime, Position> position,
			final double fitness, final Map<DateTime, Position> velocity,
			final Map<DateTime, Position> bestPosition, final double bestFitness) {
		this.currentPosition = new ConcurrentSkipListMap<>(position);
		this.fitness = fitness;
		this.velocity = new ConcurrentSkipListMap<>(velocity);
		this.bestPosition = new ConcurrentSkipListMap<>(bestPosition);
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
	public Map<DateTime, Position> getBestPosition() {
		return bestPosition;
	}

	/**
	 * Gets the current position.
	 * 
	 * @return the current position
	 */
	public Map<DateTime, Position> getCurrentPosition() {
		return currentPosition;
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
	 * Gets the velocity.
	 * 
	 * @return the velocity
	 */
	public Map<DateTime, Position> getVelocity() {
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
	 *            the new best position
	 */
	public void setBestPosition(final Map<DateTime, Position> bestPosition) {
		this.bestPosition = bestPosition;
	}

	/**
	 * Sets the current position.
	 * 
	 * @param currentPosition
	 *            the new current position
	 */
	public void setCurrentPosition(final Map<DateTime, Position> currentPosition) {
		this.currentPosition = new ConcurrentSkipListMap<>(currentPosition);
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
	 * Sets the velocity.
	 * 
	 * @param velocity
	 *            the new velocity
	 */
	public void setVelocity(final Map<DateTime, Position> velocity) {
		this.velocity = velocity;
	}
}
