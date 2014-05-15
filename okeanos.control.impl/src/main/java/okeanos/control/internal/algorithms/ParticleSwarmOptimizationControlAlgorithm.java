package okeanos.control.internal.algorithms;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.control.internal.algorithms.pso.Particle;
import okeanos.data.services.Constants;
import okeanos.data.services.PricingService;
import okeanos.data.services.entities.CostFunction;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Represents a control algorithm that utilizes particle swarm optimization
 * (PSO) to find the best configuration.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("particleSwarmOptimizationControlAlgorithm")
public class ParticleSwarmOptimizationControlAlgorithm implements
		ControlAlgorithm {

	private static final double DEFAULT_SOCIAL_WEIGHT = 1.49445;

	private static final double DEFAULT_COGNITIVE_WEIGHT = 1.49445;

	private static final double DEFAULT_INERTIA_WEIGHT = 0.729;

	/** The number of particles for the PSO. */
	private static final int NUMBER_OF_PARTICLES = 10;

	/** The number of iterations to run the PSO. */
	private static final int NUMBER_OF_ITERATIONS = 100;

	/** The default costs if no costs are assigned at a certain price range. */
	private static final double DEFAULT_COSTS = Double.NaN;

	/** The random. */
	private Random random;

	/** The pricing service. */
	private PricingService pricingService;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(ParticleSwarmOptimizationControlAlgorithm.class);

	/**
	 * Instantiates a new particle swarm optimization control algorithm.
	 * 
	 * @param pricingService
	 *            the pricing service
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 */
	@Inject
	public ParticleSwarmOptimizationControlAlgorithm(
			final PricingService pricingService,
			final ControlEntitiesProvider controlEntitiesProvider) {
		random = new Random();

		this.pricingService = pricingService;
		this.controlEntitiesProvider = controlEntitiesProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.algorithms.ControlAlgorithm#findBestConfiguration(okeanos
	 * .control.entities.Configuration)
	 */
	@Override
	public List<OptimizedRun> findBestConfiguration(
			final Configuration currentConfiguration) {

		int numberParticles = NUMBER_OF_PARTICLES;
		int numberIterations = NUMBER_OF_ITERATIONS;
		int iteration = 0;
		DateTime minX = DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay();
		DateTime maxX = minX.withTime(23, 45, 0, 0);

		Particle[] swarm = new Particle[numberParticles];
		Map<PossibleRun, DateTime> bestGlobalPosition = new HashMap<>();
		double bestGlobalFitness = Double.MAX_VALUE;

		Period maxV = new Period(minX, maxX);
		Period minV = maxV.negated();

		// Init best global position to the start of the optimization
		for (PossibleRun possibleRun : currentConfiguration
				.getPossibleRunsConfiguration().getPossibleRuns()) {
			bestGlobalPosition.put(possibleRun, minX);
		}

		// Initialize all Particle objects
		for (int i = 0; i < swarm.length; ++i) {
			Map<PossibleRun, DateTime> randomPosition = new HashMap<>();
			for (PossibleRun possibleRun : currentConfiguration
					.getPossibleRunsConfiguration().getPossibleRuns()) {
				DateTime lo = minX;
				DateTime hi = maxX;

				DateTime position = minX
						.plus(Period.minutes((int) (Math.round(new Period(lo,
								hi).toStandardMinutes().getMinutes()
								* random.nextDouble()
								/ ((double) Constants.SLOT_INTERVAL)) * Constants.SLOT_INTERVAL)));
				position = checkPosition(possibleRun, position, minX, maxX);

				randomPosition.put(possibleRun, position);
			}

			double fitness = objectiveFunction(randomPosition,
					currentConfiguration.getScheduleOfOtherDevices());
			Map<PossibleRun, Period> randomVelocity = new HashMap<>();
			for (PossibleRun possibleRun : currentConfiguration
					.getPossibleRunsConfiguration().getPossibleRuns()) {
				Period lo = new Period(minX, maxX).negated();
				Period hi = new Period(minX, maxX);
				randomVelocity
						.put(possibleRun,
								Period.minutes(
										(int) ((hi.toStandardMinutes()
												.getMinutes() - lo
												.toStandardMinutes()
												.getMinutes()) * random
												.nextDouble())).plus(lo));
			}

			swarm[i] = new Particle(randomPosition, fitness, randomVelocity,
					randomPosition, fitness);

			if (swarm[i].getFitness() < bestGlobalFitness) {
				bestGlobalFitness = swarm[i].getFitness();
				bestGlobalPosition = swarm[i].getPosition();
			}
		} // End initialization loop

		double w = DEFAULT_INERTIA_WEIGHT; // inertia weight
		double c1 = DEFAULT_COGNITIVE_WEIGHT; // cognitive weight
		double c2 = DEFAULT_SOCIAL_WEIGHT; // social weight
		double r1, r2; // randomizations

		// do real work
		while (iteration < numberIterations) {
			++iteration;

			for (int i = 0; i < swarm.length; ++i) { // each Particle
				Map<PossibleRun, Period> newVelocity = new HashMap<>();
				Map<PossibleRun, DateTime> newPosition = new HashMap<>();
				double newFitness;
				Particle currP = swarm[i];

				// each dimension of the velocity
				for (PossibleRun currentPossibleRun : currP.getVelocity()
						.keySet()) {
					r1 = random.nextDouble();
					r2 = random.nextDouble();

					Period thisVelocity = Period.minutes((int) ((w * currP
							.getVelocity().get(currentPossibleRun)
							.toStandardMinutes().getMinutes())
							+ (c1 * r1 * new Period(currP.getPosition().get(
									currentPossibleRun), currP
									.getBestPosition().get(currentPossibleRun))
									.toStandardMinutes().getMinutes()) + (c2
							* r2 * new Period(bestGlobalPosition
							.get(currentPossibleRun), currP.getBestPosition()
							.get(currentPossibleRun)).toStandardMinutes()
							.getMinutes())));

					if (thisVelocity.toStandardMinutes().getMinutes() < minV
							.toStandardMinutes().getMinutes()) {
						thisVelocity = minV;
					} else if (thisVelocity.toStandardMinutes().getMinutes() > maxV
							.toStandardMinutes().getMinutes()) {
						thisVelocity = maxV;
					}
					thisVelocity = Period
							.minutes((int) (Math.round(thisVelocity
									.toStandardMinutes().getMinutes()
									/ ((double) Constants.SLOT_INTERVAL)) * Constants.SLOT_INTERVAL));

					newVelocity.put(currentPossibleRun, thisVelocity);
				}
				currP.setVelocity(newVelocity);

				for (PossibleRun currentPossibleRun : currP.getPosition()
						.keySet()) {
					DateTime thisPosition = currP.getPosition()
							.get(currentPossibleRun)
							.plus(newVelocity.get(currentPossibleRun));

					thisPosition = checkPosition(currentPossibleRun,
							thisPosition, minX, maxX);

					newPosition.put(currentPossibleRun, thisPosition);
				}
				currP.setPosition(newPosition);

				newFitness = objectiveFunction(newPosition,
						currentConfiguration.getScheduleOfOtherDevices());
				// if no cost function is available for the current position
				// or if the schedules of one device overlap (not possible to
				// more
				// than once at a time), do not count this run
				if (newFitness == Double.NaN || doPositionsOverlap(newPosition)) {
					continue;
				}

				currP.setFitness(newFitness);

				if (newFitness < currP.getBestFitness()) {
					currP.setBestPosition(newPosition);
					currP.setBestFitness(newFitness);
				}

				if (newFitness < bestGlobalFitness) {
					bestGlobalPosition = newPosition;
					bestGlobalFitness = newFitness;
				}

			} // each Particle
		} // while

		List<OptimizedRun> optimizedRuns = new LinkedList<>();

		for (PossibleRun currentRun : bestGlobalPosition.keySet()) {
			OptimizedRun optimizedRun = controlEntitiesProvider
					.getNewOptimizedRun();
			optimizedRun.setLoadType(currentRun.getLoadType());
			optimizedRun.setNeededSlots(currentRun.getNeededSlots());
			optimizedRun.setStartTime(bestGlobalPosition.get(currentRun));
			optimizedRuns.add(optimizedRun);
		}

		return optimizedRuns;
	}

	/**
	 * Check position.
	 * 
	 * @param currentPossibleRun
	 *            the current possible run
	 * @param thisPosition
	 *            the this position
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @return the date time
	 */
	private DateTime checkPosition(final PossibleRun currentPossibleRun,
			final DateTime thisPosition, final DateTime minX,
			final DateTime maxX) {

		DateTime returnPosition = thisPosition;
		if (returnPosition.plusMinutes(
				Constants.SLOT_INTERVAL
						* currentPossibleRun.getNeededSlots().size()).isAfter(
				currentPossibleRun.getLatestEndTime())) {
			returnPosition = currentPossibleRun.getLatestEndTime()
					.minusMinutes(
							Constants.SLOT_INTERVAL
									* currentPossibleRun.getNeededSlots()
											.size());
		}

		if (returnPosition.isBefore(currentPossibleRun.getEarliestStartTime())) {
			returnPosition = currentPossibleRun.getEarliestStartTime();
		}

		if (returnPosition.isBefore(minX)) {
			LOG.debug("returnPosition.isBefore(minX), before {}, now {}",
					returnPosition, minX);
			returnPosition = minX;
		} else if (returnPosition.isAfter(maxX)) {
			LOG.debug(
					"returnPosition.isAfter(minX), before {}, now {}",
					returnPosition,
					maxX.minusMinutes(Constants.SLOT_INTERVAL
							* currentPossibleRun.getNeededSlots().size()));
			returnPosition = maxX.minusMinutes(Constants.SLOT_INTERVAL
					* currentPossibleRun.getNeededSlots().size());
		}

		return returnPosition;
	}

	/**
	 * Checks if two runs overlap, which is prohibited.
	 * 
	 * @param newPosition
	 *            the new position in the solution space that should be checked
	 * @return true, if two or more runs at the new position overlap, false
	 *         otherwise
	 */
	private boolean doPositionsOverlap(
			final Map<PossibleRun, DateTime> newPosition) {
		for (PossibleRun run : newPosition.keySet()) {
			DateTime start = newPosition.get(run);
			DateTime end = start.plusMinutes(Constants.SLOT_INTERVAL
					* run.getNeededSlots().size());

			for (PossibleRun otherRun : newPosition.keySet()) {
				if (run == otherRun) {
					continue;
				}
				DateTime startOtherRun = newPosition.get(otherRun);
				DateTime endOtherRun = startOtherRun
						.plusMinutes(Constants.SLOT_INTERVAL
								* run.getNeededSlots().size());

				// if the current run starts at the same time or after another
				// run has started but has not yet finished
				if (((start.isAfter(startOtherRun) || start
						.isEqual(startOtherRun)) && start.isBefore(endOtherRun))
						|| end.isAfter(startOtherRun)
						&& (end.isBefore(endOtherRun) || end
								.isEqual(endOtherRun))) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * The objective function that assigns costs to the current position.
	 * 
	 * @param position
	 *            the position in the solution space
	 * @param scheduleOfOtherDevices
	 *            The schedule of the other devices. Needs to be taken into
	 *            consideration when calculating the costs. The price rises
	 *            exponentially if more energy is used.
	 * @return The fitness value, which is represented by the calculated costs.
	 *         If NaN, the current position in the solution space is invalid.
	 */
	private double objectiveFunction(final Map<PossibleRun, DateTime> position,
			final Schedule scheduleOfOtherDevices) {
		double fitness = 0;

		for (PossibleRun currentRun : position.keySet()) {
			DateTime currentTime = position.get(currentRun);
			for (Slot slot : currentRun.getNeededSlots()) {
				CostFunction costFunction = pricingService
						.getCostFunction(currentTime);

				if (costFunction == null) {
					/*
					 * LOG.error(
					 * "No cost function for time {} found. Substituting with {}\navailable cost functions:\n{}"
					 * , currentTime, DEFAULT_COSTS, StringUtils.join(
					 * pricingService.getCostFunctions(), '\n'));
					 */
					/*
					 * throw new IllegalArgumentException( String.format(
					 * "No cost function for time %s found\navailable cost functions:\n%s"
					 * , currentTime, StringUtils.join(
					 * pricingService.getCostFunctions(), '\n')));
					 */
					fitness += DEFAULT_COSTS;
				} else {
					Map<DateTime, Slot> schedule = scheduleOfOtherDevices
							.getSchedule();
					if (schedule == null || schedule.get(currentTime) == null) {
						fitness += costFunction.getPrice()
								.getCostAtConsumption(slot.getLoad());
					} else {
						fitness += costFunction.getPrice()
								.getCostAtConsumption(
										slot.getLoad().plus(
												schedule.get(currentTime)
														.getLoad()));
					}
				}
				currentTime = currentTime.plusMinutes(Constants.SLOT_INTERVAL);
			}
		}
		return fitness;
	}
}
