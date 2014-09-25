package okeanos.control.internal.algorithms;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.inject.Inject;
import javax.measure.quantity.Power;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.LoadFlexiblity;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.RunConstraint;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.control.internal.algorithms.pso.regenerativeload.Particle;
import okeanos.control.internal.algorithms.pso.regenerativeload.Position;
import okeanos.control.internal.algorithms.pso.regenerativeload.Velocity;
import okeanos.data.services.Constants;
import okeanos.data.services.PricingService;
import okeanos.data.services.entities.CostFunction;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

@Component("PSORegenerativeLoadOptimizer")
public class PSORegenerativeLoadOptimizer implements ControlAlgorithm {

	/** The Constant DEFAULT_COGNITIVE_WEIGHT. */
	private static final double DEFAULT_COGNITIVE_WEIGHT = 1.49445;

	/** The default costs if no costs are assigned at a certain price range. */
	private static final double DEFAULT_COSTS = Double.NaN;

	/** The Constant DEFAULT_INERTIA_WEIGHT. */
	private static final double DEFAULT_INERTIA_WEIGHT = 0.729;

	/** The Constant DEFAULT_SOCIAL_WEIGHT. */
	private static final double DEFAULT_SOCIAL_WEIGHT = 1.49445;

	/** One hour in minutes = 60 minutes. */
	private static final double HOUR_IN_MINUTES = 60.0;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(PSORegenerativeLoadOptimizer.class);

	/** The number of iterations to run the PSO. */
	private static final int NUMBER_OF_ITERATIONS = 100;

	/** The number of particles for the PSO. */
	private static final int NUMBER_OF_PARTICLES = 20;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The pricing service. */
	private PricingService pricingService;

	/** The random. */
	private Random random;

	/**
	 * Instantiates a new PSO regenerative load optimizer.
	 * 
	 * @param pricingService
	 *            the pricing service
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 */
	@Inject
	public PSORegenerativeLoadOptimizer(final PricingService pricingService,
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

		// initialize minimum and maximum velocity and position
		Map<DateTime, Position> minX = new ConcurrentSkipListMap<>();
		Map<DateTime, Position> maxX = new ConcurrentSkipListMap<>();
		Map<DateTime, Velocity> minV = new ConcurrentSkipListMap<>();
		Map<DateTime, Velocity> maxV = new ConcurrentSkipListMap<>();
		Map<DateTime, Amount<Power>> minCharges = new ConcurrentSkipListMap<>();
		initMinMax(currentConfiguration, minX, maxX, minV, maxV, minCharges);

		try {
			LOG.debug("getChargesAtPointsInTime = {}", currentConfiguration
					.getPossibleRunsConfiguration().getRunConstraint()
					.getChargesAtPointsInTime());
			LOG.debug("getLossOfEnergyAtPointsInTime = {}",
					currentConfiguration.getPossibleRunsConfiguration()
							.getRunConstraint().getLossOfEnergyAtPointsInTime());
			LOG.debug(
					"minX @ 08:00:00 = {}",
					minX.get(
							DateTime.now(DateTimeZone.UTC).withTime(8, 0, 0, 0))
							.getChosenValue());
			LOG.debug(
					"maxX @ 08:00:00 = {}",
					maxX.get(
							DateTime.now(DateTimeZone.UTC).withTime(8, 0, 0, 0))
							.getChosenValue());
			LOG.debug(
					"minX @ 16:00:00 = {}",
					minX.get(
							DateTime.now(DateTimeZone.UTC)
									.withTime(16, 0, 0, 0)).getChosenValue());
			LOG.debug(
					"maxX @ 16:00:00 = {}",
					maxX.get(
							DateTime.now(DateTimeZone.UTC)
									.withTime(16, 0, 0, 0)).getChosenValue());
			LOG.debug(
					"minCharges @ 07:30 = {}",
					minCharges.get(DateTime.now(DateTimeZone.UTC).withTime(7,
							30, 0, 0)));
			LOG.debug(
					"minCharges @ 07:45 = {}",
					minCharges.get(DateTime.now(DateTimeZone.UTC).withTime(7,
							45, 0, 0)));
			LOG.debug(
					"minCharges @ 08:00 = {}",
					minCharges.get(DateTime.now(DateTimeZone.UTC).withTime(8,
							0, 0, 0)));
		} catch (NullPointerException e) {
		}

		Particle[] swarm = new Particle[numberParticles];
		Map<DateTime, Position> bestGlobalPosition = new ConcurrentSkipListMap<>();
		double bestGlobalFitness = Double.MAX_VALUE;

		// Init best global position to the start of the optimization
		for (PossibleRun possibleRun : currentConfiguration
				.getPossibleRunsConfiguration().getPossibleRuns()) {
			Position position = new Position(possibleRun, minX.get(
					possibleRun.getEarliestStartTime()).getChosenValue());
			bestGlobalPosition
					.put(possibleRun.getEarliestStartTime(), position);
		}
		initParticles(currentConfiguration, swarm, minX, maxX, minCharges);

		for (int i = 0; i < swarm.length; ++i) {
			if (swarm[i].getFitness() < bestGlobalFitness) {
				bestGlobalFitness = swarm[i].getFitness();
				bestGlobalPosition = swarm[i].getCurrentPosition();
			}
		}

		double w = DEFAULT_INERTIA_WEIGHT; // inertia weight
		double c1 = DEFAULT_COGNITIVE_WEIGHT; // cognitive weight
		double c2 = DEFAULT_SOCIAL_WEIGHT; // social weight
		double r1, r2; // randomizations

		// do real work
		while (iteration < numberIterations) {
			++iteration;

			for (int i = 0; i < swarm.length; ++i) { // each Particle
				Map<DateTime, Velocity> newVelocity = new ConcurrentSkipListMap<>();
				Map<DateTime, Position> newPosition = new ConcurrentSkipListMap<>();
				double newFitness;
				Particle currentParticle = swarm[i];

				// each dimension of the velocity
				for (DateTime startTimeOfcurrentPossibleRun : currentParticle
						.getVelocity().keySet()) {
					r1 = random.nextDouble();
					r2 = random.nextDouble();

					Velocity currentVelocity = currentParticle.getVelocity()
							.get(startTimeOfcurrentPossibleRun);

					Amount<Power> thisVelocity = (currentVelocity
							.getChosenValue().times(w)).plus(
							currentParticle
									.getBestPosition()
									.get(startTimeOfcurrentPossibleRun)
									.getChosenValue()
									.minus(currentParticle.getCurrentPosition()
											.get(startTimeOfcurrentPossibleRun)
											.getChosenValue()).times(c1 * r1))
							.plus(bestGlobalPosition
									.get(startTimeOfcurrentPossibleRun)
									.getChosenValue()
									.minus(currentParticle.getCurrentPosition()
											.get(startTimeOfcurrentPossibleRun)
											.getChosenValue()).times(c2 * r2));

					if (thisVelocity.isLessThan(minV.get(
							startTimeOfcurrentPossibleRun).getChosenValue())) {
						thisVelocity = minV.get(startTimeOfcurrentPossibleRun)
								.getChosenValue();
					} else if (thisVelocity.isGreaterThan(maxV.get(
							startTimeOfcurrentPossibleRun).getChosenValue())) {
						thisVelocity = maxV.get(startTimeOfcurrentPossibleRun)
								.getChosenValue();
					}

					if (LoadFlexiblity.LIMITED_CHOICE.equals(currentVelocity
							.getPossibleRun().getLoadFlexibilityOfRun())) {
						thisVelocity = Amount.valueOf(Math.round(thisVelocity
								.doubleValue(Power.UNIT)), Power.UNIT);
					}
					Velocity velocity = new Velocity(
							currentVelocity.getPossibleRun(), thisVelocity);

					newVelocity.put(startTimeOfcurrentPossibleRun, velocity);
				}
				currentParticle.setVelocity(newVelocity);

				Amount<Power> currentCharge = currentConfiguration
						.getPossibleRunsConfiguration().getRunConstraint()
						.getStartCharge();
				Amount<Power> minCapacity = currentConfiguration
						.getPossibleRunsConfiguration().getRunConstraint()
						.getMinimumCapacity();
				Amount<Power> maxCapacity = currentConfiguration
						.getPossibleRunsConfiguration().getRunConstraint()
						.getMaximumCapacity();

				Map<DateTime, Position> positionForEvaluation = new ConcurrentSkipListMap<>();
				for (DateTime startTimeOfCurrentPossibleRun : currentParticle
						.getCurrentPosition().keySet()) {
					Position currentPosition = currentParticle
							.getCurrentPosition().get(
									startTimeOfCurrentPossibleRun);

					Amount<Power> chosenValue = currentPosition
							.getChosenValue().plus(
									newVelocity.get(
											startTimeOfCurrentPossibleRun)
											.getChosenValue());

					if (chosenValue.isLessThan(minX.get(
							startTimeOfCurrentPossibleRun).getChosenValue())) {
						chosenValue = minX.get(startTimeOfCurrentPossibleRun)
								.getChosenValue();
					} else if (chosenValue.isGreaterThan(maxX.get(
							startTimeOfCurrentPossibleRun).getChosenValue())) {
						chosenValue = maxX.get(startTimeOfCurrentPossibleRun)
								.getChosenValue();
					}

					double hours = currentPosition.getPossibleRun()
							.getLengthOfRun().toStandardMinutes().getMinutes()
							/ HOUR_IN_MINUTES;

					// check capacity
					if (LoadFlexiblity.LIMITED_CHOICE.equals(currentPosition
							.getPossibleRun().getLoadFlexibilityOfRun())) {
						int iterationsLeft = currentPosition.getPossibleRun()
								.getPossibleLoads().size();
						// go through the different possible values to find the
						// next value that might be able to to fit within the
						// capacity limits and the current charge
						do {
							Amount<Power> value = null;
							try {
								value = Iterables
										.get(currentPosition.getPossibleRun()
												.getPossibleLoads(),
												(int) chosenValue
														.longValue(Power.UNIT));
							} catch (Exception e) {
								value = Amount.valueOf(0, Power.UNIT);
								LOG.debug("Exception: {}", e);
								LOG.debug("Chosen value: {}", chosenValue);
								LOG.debug("Possible loads: {}", currentPosition
										.getPossibleRun().getPossibleLoads());
								LOG.debug("currentCharge: {}", currentCharge);
								LOG.debug("minCapacity: {}", minCapacity);
								LOG.debug("maxCapacity: {}", maxCapacity);
								LOG.debug("chosenValue.longValue: {}",
										chosenValue.longValue(Power.UNIT) + 1);
								LOG.debug("getPossibleLoads().size: {}",
										currentPosition.getPossibleRun()
												.getPossibleLoads().size());
							}

							if (currentCharge.plus(value.times(hours))
									.isGreaterThan(maxCapacity)) {
								if (chosenValue.longValue(Power.UNIT) > 0) {
									chosenValue = chosenValue.minus(Amount
											.valueOf(1, Power.UNIT));
								}
							} else if (currentCharge.plus(value.times(hours))
									.isLessThan(minCapacity)) {
								if (chosenValue.longValue(Power.UNIT) < currentPosition
										.getPossibleRun().getPossibleLoads()
										.size() - 1) {
									chosenValue = chosenValue.plus(Amount
											.valueOf(1, Power.UNIT));
								}
							} else if (minCharges
									.containsKey(startTimeOfCurrentPossibleRun)
									&& chosenValue.longValue(Power.UNIT) < currentPosition
											.getPossibleRun()
											.getPossibleLoads().size() - 1) {
								value = Iterables
										.get(currentPosition.getPossibleRun()
												.getPossibleLoads(),
												(int) chosenValue
														.longValue(Power.UNIT) + 1);

								// only try to add more charge if it would not
								// exceed the maximum capacity
								if (currentCharge.isLessThan(minCharges
										.get(startTimeOfCurrentPossibleRun))
										&& !currentCharge.plus(
												value.times(hours))
												.isGreaterThan(maxCapacity)) {
									chosenValue = chosenValue.plus(Amount
											.valueOf(1, Power.UNIT));

									// LOG.debug(
									// "currentCharge: {}, minCharge: {}, chosenvalue: {}",
									// currentCharge,
									// minCharges
									// .get(startTimeOfCurrentPossibleRun),
									// chosenValue);
								}
							} else {
								break;
							}
							iterationsLeft--;
						} while (iterationsLeft > 0);

						if (chosenValue
								.isLessThan(minX.get(
										startTimeOfCurrentPossibleRun)
										.getChosenValue())) {
							chosenValue = minX.get(
									startTimeOfCurrentPossibleRun)
									.getChosenValue();
						} else if (chosenValue
								.isGreaterThan(maxX.get(
										startTimeOfCurrentPossibleRun)
										.getChosenValue())) {
							chosenValue = maxX.get(
									startTimeOfCurrentPossibleRun)
									.getChosenValue();
						}

						if (chosenValue.approximates(Amount.valueOf(0,
								Power.UNIT))) {
							// LOG.info("-200W found!");
						}

						Position realValueBehindChosenValue = new Position(
								currentPosition.getPossibleRun(),
								Iterables.get(currentPosition.getPossibleRun()
										.getPossibleLoads(), (int) chosenValue
										.longValue(Power.UNIT)));
						positionForEvaluation.put(
								startTimeOfCurrentPossibleRun,
								realValueBehindChosenValue);

						Position position = new Position(
								currentPosition.getPossibleRun(), chosenValue);
						newPosition
								.put(startTimeOfCurrentPossibleRun, position);

						chosenValue = realValueBehindChosenValue
								.getChosenValue();
					} else {
						Amount<Power> energyWholeRun = chosenValue.times(hours);

						// check if there is enough charge on the regenerative
						// device left for the chosen value and if the capacity
						// limits are abided
						if (currentCharge.plus(energyWholeRun).isGreaterThan(
								maxCapacity)) {
							chosenValue = maxCapacity.minus(currentCharge)
									.divide(hours);
						} else if (currentCharge.plus(energyWholeRun)
								.isLessThan(minCapacity)) {
							chosenValue = currentCharge.minus(minCapacity)
									.divide(hours);
						}

						Position position = new Position(
								currentPosition.getPossibleRun(), chosenValue);

						positionForEvaluation.put(
								startTimeOfCurrentPossibleRun, position);
						newPosition
								.put(startTimeOfCurrentPossibleRun, position);
					}

					currentCharge = currentCharge
							.plus(chosenValue.times(hours));

					Amount<Power> lossOfEnergy = currentConfiguration
							.getPossibleRunsConfiguration().getRunConstraint()
							.getLossOfEnergyAtPointsInTime()
							.get(startTimeOfCurrentPossibleRun);
					if (lossOfEnergy != null) {
						currentCharge = currentCharge.minus(lossOfEnergy
								.times(hours));
					}
				}
				currentParticle.setCurrentPosition(newPosition);

				newFitness = objectiveFunction(positionForEvaluation,
						currentConfiguration.getScheduleOfOtherDevices());
				// if no cost function is available for the current position
				// do not count this run
				if (newFitness == Double.NaN) {
					continue;
				}

				currentParticle.setFitness(newFitness);

				// see if the current position is a new particle's best position
				if (newFitness < currentParticle.getBestFitness()) {
					currentParticle.setBestPosition(newPosition);
					currentParticle.setBestFitness(newFitness);
				}

				// see if the current position is a new global best position
				if (newFitness < bestGlobalFitness) {
					bestGlobalPosition = newPosition;
					bestGlobalFitness = newFitness;
				}

			} // each Particle
		}

		List<OptimizedRun> optimizedRuns = new LinkedList<>();
		compileOptimizedRuns(optimizedRuns, bestGlobalPosition,
				bestGlobalFitness);

		return optimizedRuns;
	}

	/**
	 * Compile optimized runs.
	 * 
	 * @param optimizedRuns
	 *            the optimized runs
	 * @param bestGlobalPosition
	 *            the best global position
	 * @param bestGlobalFitness
	 *            the best global fitness
	 */
	private void compileOptimizedRuns(final List<OptimizedRun> optimizedRuns,
			final Map<DateTime, Position> bestGlobalPosition,
			final double bestGlobalFitness) {
		// compile the optimized runs and set the slots with the chosen values
		for (DateTime currentStartTimeOfRun : bestGlobalPosition.keySet()) {
			PossibleRun currentRun = bestGlobalPosition.get(
					currentStartTimeOfRun).getPossibleRun();
			OptimizedRun optimizedRun = controlEntitiesProvider
					.getNewOptimizedRun();

			List<Slot> slots = new LinkedList<>();
			for (DateTime currentTime = currentRun.getEarliestStartTime(); currentTime
					.isBefore(currentRun.getEarliestStartTime().plus(
							currentRun.getLengthOfRun())); currentTime = currentTime
					.plusMinutes(Constants.SLOT_INTERVAL)) {
				Slot slot = controlEntitiesProvider.getNewSlot();
				if (LoadFlexiblity.LIMITED_CHOICE.equals(currentRun
						.getLoadFlexibilityOfRun())) {
					// The index is the value that is optimized for
					// limited_choice. The caller is, however, interested in the
					// real value.
					slot.setLoad(Iterables.get(
							currentRun.getPossibleLoads(),
							(int) bestGlobalPosition
									.get(currentRun.getEarliestStartTime())
									.getChosenValue().longValue(Power.UNIT)));
				} else {
					slot.setLoad(bestGlobalPosition.get(currentRun)
							.getChosenValue());
				}
				slots.add(slot);
			}

			optimizedRun.setStartTime(currentRun.getEarliestStartTime());
			optimizedRun.setNeededSlots(slots);
			optimizedRuns.add(optimizedRun);
		}
	}

	/**
	 * Inits the min max.
	 * 
	 * @param currentConfiguration
	 *            the current configuration
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minV
	 *            the min v
	 * @param maxV
	 *            the max v
	 */
	private void initMinMax(final Configuration currentConfiguration,
			final Map<DateTime, Position> minX,
			final Map<DateTime, Position> maxX,
			final Map<DateTime, Velocity> minV,
			final Map<DateTime, Velocity> maxV,
			final Map<DateTime, Amount<Power>> minCharges) {

		// set minX, maxX, minV, maxV
		for (PossibleRun possibleRun : currentConfiguration
				.getPossibleRunsConfiguration().getPossibleRuns()) {
			Amount<Power> minXThisDim = Amount.valueOf(Double.NaN, Power.UNIT);
			Amount<Power> maxXThisDim = Amount.valueOf(Double.NaN, Power.UNIT);

			switch (possibleRun.getLoadFlexibilityOfRun()) {
			case FIXED:
				for (Slot slot : possibleRun.getNeededSlots()) {
					if (slot.getLoad().isLessThan(minXThisDim)) {
						minXThisDim = slot.getLoad();
					}
					if (slot.getLoad().isGreaterThan(maxXThisDim)) {
						maxXThisDim = slot.getLoad();
					}
				}

				break;
			case LIMITED_CHOICE:
				if (possibleRun.getPossibleLoads().isEmpty()) {
					throw new UnsupportedOperationException(
							"Possible Loads must contain at least one element!");
				}
				minXThisDim = Amount.valueOf(0, Power.UNIT);
				maxXThisDim = Amount.valueOf(possibleRun.getPossibleLoads()
						.size() - 1, Power.UNIT);
//				LOG.trace(
//						"There are {} possibilities for run with start date {}",
//						possibleRun.getPossibleLoads().size(),
//						possibleRun.getEarliestStartTime());
				break;
			case RANGE:
				minXThisDim = possibleRun.getRangeOfPossibleLoads()
						.lowerEndpoint();
				maxXThisDim = possibleRun.getRangeOfPossibleLoads()
						.upperEndpoint();
				LOG.trace(
						"Run with start date {} has dimension limits {} to {}",
						possibleRun.getEarliestStartTime(), minXThisDim,
						maxXThisDim);
				break;
			default:
				break;
			}

			minX.put(possibleRun.getEarliestStartTime(), new Position(
					possibleRun, minXThisDim));
			maxX.put(possibleRun.getEarliestStartTime(), new Position(
					possibleRun, maxXThisDim));

			minV.put(possibleRun.getEarliestStartTime(), new Velocity(
					possibleRun, minXThisDim.minus(maxXThisDim)));
			maxV.put(possibleRun.getEarliestStartTime(), new Velocity(
					possibleRun, maxXThisDim.minus(minXThisDim)));
		}

		// incorporate no charging times!
		RunConstraint constraint = currentConfiguration
				.getPossibleRunsConfiguration().getRunConstraint();
		for (DateTime noCharge : constraint.getLossOfEnergyAtPointsInTime()
				.keySet()) {
			if (minX.containsKey(noCharge)) {
				PossibleRun run = minX.get(noCharge).getPossibleRun();

				if (LoadFlexiblity.LIMITED_CHOICE.equals(run
						.getLoadFlexibilityOfRun())) {
					minX.get(noCharge).setChosenValue(
							getZeroChargeForLimitedChoice(run));
				} else if (LoadFlexiblity.RANGE.equals(run
						.getLoadFlexibilityOfRun())) {
					minX.get(noCharge).setChosenValue(
							Amount.valueOf(0, Power.UNIT));
				}
			}
			if (maxX.containsKey(noCharge)) {
				PossibleRun run = maxX.get(noCharge).getPossibleRun();

				if (LoadFlexiblity.LIMITED_CHOICE.equals(run
						.getLoadFlexibilityOfRun())) {
					maxX.get(noCharge).setChosenValue(
							getZeroChargeForLimitedChoice(run));
				} else if (LoadFlexiblity.RANGE.equals(run
						.getLoadFlexibilityOfRun())) {
					maxX.get(noCharge).setChosenValue(
							Amount.valueOf(0, Power.UNIT));
				}
			}
			if (minV.containsKey(noCharge)) {
				minV.get(noCharge)
						.setChosenValue(Amount.valueOf(0, Power.UNIT));
			}
			if (maxV.containsKey(noCharge)) {
				maxV.get(noCharge)
						.setChosenValue(Amount.valueOf(0, Power.UNIT));
			}
		}

		// set minCharges
		Amount<Power> powerToCharge = Amount.valueOf(0, Power.UNIT);
		Amount<Power> lastValueOfChargesAtPointsInTime = Amount.valueOf(0,
				Power.UNIT);
		Set<DateTime> reverseKeys = ImmutableSortedSet.copyOf(minX.keySet())
				.descendingSet();
		for (DateTime now : reverseKeys) {
			if (!lastValueOfChargesAtPointsInTime.approximates(Amount.valueOf(
					0, Power.UNIT))) {
				powerToCharge = powerToCharge
						.plus(lastValueOfChargesAtPointsInTime);
				lastValueOfChargesAtPointsInTime = Amount
						.valueOf(0, Power.UNIT);
			}

			if (constraint.getChargesAtPointsInTime().containsKey(now)) {
				lastValueOfChargesAtPointsInTime = constraint
						.getChargesAtPointsInTime().get(now);
			}

			PossibleRun run = minX.get(now).getPossibleRun();

			if (powerToCharge.approximates(Amount.valueOf(0, Power.UNIT))
					|| powerToCharge.isLessThan(Amount.valueOf(0, Power.UNIT))) {
				continue;
			}

			double hours = run.getLengthOfRun().toStandardMinutes()
					.getMinutes()
					/ HOUR_IN_MINUTES;
			Amount<Power> charge = null;
			if (LoadFlexiblity.LIMITED_CHOICE.equals(run
					.getLoadFlexibilityOfRun())) {
				for (Amount<Power> possibleLoad : run.getPossibleLoads()) {
					if (possibleLoad.isLessThan(Amount.valueOf(0, Power.UNIT))) {
						continue;
					}

					charge = possibleLoad;

					if (charge.times(hours).isGreaterThan(powerToCharge)) {
						charge = powerToCharge.divide(hours);
						break;
					}
				}

				if (charge == null) {
					continue;
				}
			} else {
				Amount<Power> maxChargeNow = maxX.get(now).getChosenValue();
				if (powerToCharge.isGreaterThan(maxChargeNow)) {
					charge = maxChargeNow;
				} else if (powerToCharge.isGreaterThan(Amount.valueOf(0,
						Power.UNIT))) {
					charge = powerToCharge;
				}
			}

			minCharges.put(now, powerToCharge);
			powerToCharge = powerToCharge.minus(charge.times(hours));
		}

		if (powerToCharge.isGreaterThan(Amount.valueOf(0, Power.UNIT))) {
			LOG.debug("powerToCharge is greater than 0, which means that the charging constraints cannot be satisfied!");
		}
	}

	/**
	 * Inits the particles.
	 * 
	 * @param currentConfiguration
	 *            the current configuration
	 * @param swarm
	 *            the swarm
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minCharges
	 */
	private void initParticles(final Configuration currentConfiguration,
			final Particle[] swarm, final Map<DateTime, Position> minX,
			final Map<DateTime, Position> maxX,
			Map<DateTime, Amount<Power>> minCharges) {
		// Initialize all Particle objects
		for (int i = 0; i < swarm.length; ++i) {
			Map<DateTime, Position> randomPosition = new ConcurrentSkipListMap<>();
			Map<DateTime, Position> positionForEvaluation = new ConcurrentSkipListMap<>();
			for (PossibleRun possibleRun : currentConfiguration
					.getPossibleRunsConfiguration().getPossibleRuns()) {
				DateTime startTimeOfPossibleRun = possibleRun
						.getEarliestStartTime();
				Amount<Power> lo = minX.get(startTimeOfPossibleRun)
						.getChosenValue();
				Amount<Power> hi = maxX.get(startTimeOfPossibleRun)
						.getChosenValue();

				Amount<Power> chosenValue = Amount.valueOf(0, Power.UNIT);
				/* lo.plus(hi.minus(lo).times(random.nextDouble())); */

				if (chosenValue.isLessThan(lo)) {
					chosenValue = lo;
				} else if (chosenValue.isGreaterThan(hi)) {
					chosenValue = hi;
				}
				/*
				 * Amount<Power> currentCharge = currentConfiguration
				 * .getPossibleRunsConfiguration().getRunConstraint()
				 * .getStartCharge(); Amount<Power> minCapacity =
				 * currentConfiguration
				 * .getPossibleRunsConfiguration().getRunConstraint()
				 * .getMinimumCapacity(); Amount<Power> maxCapacity =
				 * currentConfiguration
				 * .getPossibleRunsConfiguration().getRunConstraint()
				 * .getMaximumCapacity();
				 * 
				 * double hours =
				 * possibleRun.getLengthOfRun().toStandardMinutes()
				 * .getMinutes() / HOUR_IN_MINUTES;
				 */

				if (LoadFlexiblity.LIMITED_CHOICE.equals(possibleRun
						.getLoadFlexibilityOfRun())) {

					chosenValue = getZeroChargeForLimitedChoice(possibleRun);
					/*
					 * int iterationsLeft =
					 * possibleRun.getPossibleLoads().size(); // go through the
					 * different possible values to find the // next value that
					 * might be able to to fit within the // capacity limits and
					 * the current charge do { Amount<Power> value = null; try {
					 * value = Iterables.get( possibleRun.getPossibleLoads(),
					 * (int) chosenValue.longValue(Power.UNIT)); } catch
					 * (Exception e) { value = Amount.valueOf(0, Power.UNIT);
					 * LOG.debug("Exception: {}", e);
					 * LOG.debug("Chosen value: {}", chosenValue);
					 * LOG.debug("Possible loads: {}",
					 * possibleRun.getPossibleLoads());
					 * LOG.debug("currentCharge: {}", currentCharge);
					 * LOG.debug("minCapacity: {}", minCapacity);
					 * LOG.debug("maxCapacity: {}", maxCapacity);
					 * LOG.debug("chosenValue.longValue: {}",
					 * chosenValue.longValue(Power.UNIT) + 1);
					 * LOG.debug("getPossibleLoads().size: {}",
					 * possibleRun.getPossibleLoads().size()); }
					 * 
					 * if (currentCharge.plus(value.times(hours))
					 * .isGreaterThan(maxCapacity)) { if
					 * (chosenValue.longValue(Power.UNIT) > 0) { chosenValue =
					 * chosenValue.minus(Amount.valueOf( 1, Power.UNIT)); } }
					 * else if (currentCharge.plus(value.times(hours))
					 * .isLessThan(minCapacity)) { if
					 * (chosenValue.longValue(Power.UNIT) < possibleRun
					 * .getPossibleLoads().size() - 1) { chosenValue =
					 * chosenValue.plus(Amount.valueOf( 1, Power.UNIT)); } }
					 * else if (minCharges .containsKey(startTimeOfPossibleRun)
					 * && chosenValue.longValue(Power.UNIT) < possibleRun
					 * .getPossibleLoads().size() - 1) { value = Iterables
					 * .get(possibleRun.getPossibleLoads(), (int) chosenValue
					 * .longValue(Power.UNIT) + 1);
					 * 
					 * // only try to add more charge if it would not // exceed
					 * the maximum capacity if
					 * (currentCharge.isLessThan(minCharges
					 * .get(startTimeOfPossibleRun)) &&
					 * !currentCharge.plus(value.times(hours))
					 * .isGreaterThan(maxCapacity)) { chosenValue =
					 * chosenValue.plus(Amount.valueOf( 1, Power.UNIT));
					 * 
					 * LOG.debug(
					 * "currentCharge: {}, minCharge: {}, chosenvalue: {}",
					 * currentCharge, minCharges .get(startTimeOfPossibleRun),
					 * chosenValue); } } else { break; } iterationsLeft--; }
					 * while (iterationsLeft > 0);
					 * 
					 * if (chosenValue.isLessThan(minX.get(
					 * startTimeOfPossibleRun).getChosenValue())) { chosenValue
					 * = minX.get(startTimeOfPossibleRun) .getChosenValue(); }
					 * else if (chosenValue.isGreaterThan(maxX.get(
					 * startTimeOfPossibleRun).getChosenValue())) { chosenValue
					 * = maxX.get(startTimeOfPossibleRun) .getChosenValue(); }
					 */
				}

				Position position = new Position(possibleRun, chosenValue);

				randomPosition.put(startTimeOfPossibleRun, position);

				if (LoadFlexiblity.LIMITED_CHOICE.equals(possibleRun
						.getLoadFlexibilityOfRun())) {
					position = new Position(possibleRun, Iterables.get(
							possibleRun.getPossibleLoads(),
							(int) chosenValue.longValue(Power.UNIT)));
					positionForEvaluation.put(startTimeOfPossibleRun, position);
				} else {
					positionForEvaluation.put(startTimeOfPossibleRun, position);
				}
				/*
				 * currentCharge = currentCharge.plus(chosenValue.times(hours));
				 * 
				 * Amount<Power> lossOfEnergy = currentConfiguration
				 * .getPossibleRunsConfiguration().getRunConstraint()
				 * .getLossOfEnergyAtPointsInTime()
				 * .get(startTimeOfPossibleRun); if (lossOfEnergy != null) {
				 * currentCharge = currentCharge.minus(lossOfEnergy
				 * .times(hours)); }
				 */
			}

			double fitness = Double.MAX_VALUE;
			if (minCharges.isEmpty()) {
				fitness = objectiveFunction(positionForEvaluation,
						currentConfiguration.getScheduleOfOtherDevices());
			}

			Map<DateTime, Velocity> randomVelocity = new ConcurrentSkipListMap<>();
			for (PossibleRun possibleRun : currentConfiguration
					.getPossibleRunsConfiguration().getPossibleRuns()) {
				DateTime startTimeOfPossibleRun = possibleRun
						.getEarliestStartTime();
				Amount<Power> aMinX = minX.get(startTimeOfPossibleRun)
						.getChosenValue();
				Amount<Power> aMaxX = maxX.get(startTimeOfPossibleRun)
						.getChosenValue();

				Amount<Power> lo = aMinX.minus(maxX.get(startTimeOfPossibleRun)
						.getChosenValue());
				Amount<Power> hi = aMaxX.minus(aMinX);

				Amount<Power> velocity = hi.minus(lo)
						.times(random.nextDouble()).plus(lo);

				if (LoadFlexiblity.LIMITED_CHOICE.equals(possibleRun
						.getLoadFlexibilityOfRun())) {
					velocity = Amount.valueOf(
							Math.round(velocity.doubleValue(Power.UNIT)),
							Power.UNIT);
				}
				Velocity position = new Velocity(possibleRun, velocity);

				randomVelocity.put(startTimeOfPossibleRun, position);
			}

			swarm[i] = new Particle(randomPosition, fitness, randomVelocity,
					randomPosition, fitness);
		}
	}

	private Amount<Power> getZeroChargeForLimitedChoice(PossibleRun possibleRun) {
		Amount<Power> chosenValue = Amount.valueOf(0, Power.UNIT);
		for (int j = 0; j < possibleRun.getPossibleLoads().size(); j++) {
			if (Iterables.get(possibleRun.getPossibleLoads(), j).approximates(
					Amount.valueOf(0, Power.UNIT))) {
				chosenValue = Amount.valueOf(j, Power.UNIT);
				break;
			}
		}

		return chosenValue;
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
	private double objectiveFunction(final Map<DateTime, Position> position,
			final Schedule scheduleOfOtherDevices) {
		double fitness = 0;

		for (DateTime currentRun : position.keySet()) {
			Position currentPosition = position.get(currentRun);
			Amount<Power> loadNeededForEverySlotOfRun = currentPosition
					.getChosenValue();

			DateTime currentTime = currentPosition.getPossibleRun()
					.getEarliestStartTime();
			while (currentTime.isBefore(currentRun.plus(currentPosition
					.getPossibleRun().getLengthOfRun()))) {
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
								.getCostAtConsumption(
										loadNeededForEverySlotOfRun
												.divide(1000));
					} else {
						fitness += costFunction.getPrice()
								.getCostAtConsumption(
										loadNeededForEverySlotOfRun.plus(
												schedule.get(currentTime)
														.getLoad())
												.divide(1000));
					}
				}
				currentTime = currentTime.plusMinutes(Constants.SLOT_INTERVAL);
			}
		}
		return fitness;
	}
}
