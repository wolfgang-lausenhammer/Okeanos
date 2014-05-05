package okeanos.control.internal.algorithms;

import java.util.Arrays;
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
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.control.internal.algorithms.pso.Particle;
import okeanos.data.services.Constants;
import okeanos.data.services.PricingService;
import okeanos.data.services.entities.CostFunction;

import org.apache.commons.lang3.StringUtils;
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

	private static final int NUMBER_OF_PARTICLES = 10;
	private static final int NUMBER_OF_ITERATIONS = 100;
	private static final double DEFAULT_COSTS = Double.NaN;
	private Random random;
	private PricingService pricingService;
	private ControlEntitiesProvider controlEntitiesProvider;

	private Logger LOG = LoggerFactory
			.getLogger(ParticleSwarmOptimizationControlAlgorithm.class);

	@Inject
	public ParticleSwarmOptimizationControlAlgorithm(
			PricingService pricingService,
			ControlEntitiesProvider controlEntitiesProvider) {
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
		DateTime maxX = minX.withTime(23, 45, 00, 0);

		Particle[] swarm = new Particle[numberParticles];
		Map<PossibleRun, DateTime> bestGlobalPosition = new HashMap<>();
		double bestGlobalFitness = Double.MAX_VALUE;

		Period maxV = new Period(minX, maxX);
		Period minV = maxV.negated();

		// Init best global position to the start of the optimization
		for (PossibleRun possibleRun : currentConfiguration.getPossibleRuns()) {
			bestGlobalPosition.put(possibleRun, minX);
		}

		// Initialize all Particle objects
		for (int i = 0; i < swarm.length; ++i) {
			Map<PossibleRun, DateTime> randomPosition = new HashMap<>();
			for (PossibleRun possibleRun : currentConfiguration
					.getPossibleRuns()) {
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

			double fitness = objectiveFunction(randomPosition);
			Map<PossibleRun, Period> randomVelocity = new HashMap<>();
			for (PossibleRun possibleRun : currentConfiguration
					.getPossibleRuns()) {
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

		double w = 0.729; // inertia weight
		double c1 = 1.49445; // cognitive weight
		double c2 = 1.49445; // social weight
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

				newFitness = objectiveFunction(newPosition);
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

	private DateTime checkPosition(PossibleRun currentPossibleRun,
			DateTime thisPosition, DateTime minX, DateTime maxX) {

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
			returnPosition = minX;
		} else if (returnPosition.isAfter(maxX)) {
			returnPosition = maxX;
		}

		return returnPosition;
	}

	private boolean doPositionsOverlap(Map<PossibleRun, DateTime> newPosition) {
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

	private double objectiveFunction(Map<PossibleRun, DateTime> randomPosition) {
		double fitness = 0;

		for (PossibleRun currentRun : randomPosition.keySet()) {
			DateTime currentTime = randomPosition.get(currentRun);
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
					fitness += costFunction.getPrice().getCostAtConsumption(
							slot.getLoad());
				}
				currentTime = currentTime.plusMinutes(Constants.SLOT_INTERVAL);
			}
		}
		return fitness;
	}
}
