package okeanos.control.internal.algorithms;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.isOneOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.measure.quantity.Power;

import junit.framework.Assert;
import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.LoadFlexiblity;
import okeanos.control.entities.LoadType;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.RunConstraint;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.ConfigurationImpl;
import okeanos.control.entities.impl.OptimizedRunImpl;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.PossibleRunsConfigurationImpl;
import okeanos.control.entities.impl.RunConstraintImpl;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.impl.SlotImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.control.internal.algorithms.pso.CostFunctionImpl;
import okeanos.control.internal.algorithms.pso.PriceImpl;
import okeanos.data.services.Constants;
import okeanos.data.services.PricingService;
import okeanos.data.services.entities.CostFunction;
import okeanos.data.services.entities.Price;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * The Class PSORegenerativeLoadOptimizerTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class PSORegenerativeLoadOptimizerTest {

	/** The Constant CLOCK_0. */
	private static final DateTime CLOCK_0 = DateTime
			.parse("2014-04-15T00:00:00Z");

	/** The Constant CLOCK_1. */
	private static final DateTime CLOCK_1 = DateTime
			.parse("2014-04-15T01:00:00Z");

	/** The Constant CLOCK_2. */
	private static final DateTime CLOCK_2 = DateTime
			.parse("2014-04-15T02:00:00Z");

	/** The Constant CLOCK_3. */
	private static final DateTime CLOCK_3 = DateTime
			.parse("2014-04-15T03:00:00Z");

	/** The Constant CLOCK_4. */
	private static final DateTime CLOCK_4 = DateTime
			.parse("2014-04-15T04:00:00Z");

	/** The Constant CLOCK_5. */
	private static final DateTime CLOCK_5 = DateTime
			.parse("2014-04-15T05:00:00Z");

	/** The Constant CLOCK_6. */
	private static final DateTime CLOCK_6 = DateTime
			.parse("2014-04-15T06:00:00Z");

	/** The Constant CLOCK_7. */
	private static final DateTime CLOCK_7 = DateTime
			.parse("2014-04-15T07:00:00Z");

	/** The Constant CLOCK_8. */
	private static final DateTime CLOCK_8 = DateTime
			.parse("2014-04-15T08:00:00Z");

	/** The Constant CLOCK_9. */
	private static final DateTime CLOCK_9 = DateTime
			.parse("2014-04-15T09:00:00Z");

	/** The Constant CLOCK_12. */
	private static final DateTime CLOCK_12 = DateTime
			.parse("2014-04-15T12:00:00Z");

	/** The Constant CLOCK_15. */
	private static final DateTime CLOCK_15 = DateTime
			.parse("2014-04-15T15:00:00Z");

	/** The Constant CLOCK_16. */
	private static final DateTime CLOCK_16 = DateTime
			.parse("2014-04-15T16:00:00Z");

	/** The Constant CLOCK_18. */
	private static final DateTime CLOCK_18 = DateTime
			.parse("2014-04-15T18:00:00Z");

	/** The Constant CLOCK_21. */
	private static final DateTime CLOCK_21 = DateTime
			.parse("2014-04-15T21:00:00Z");

	/** The Constant CLOCK_24. */
	private static final DateTime CLOCK_24 = DateTime
			.parse("2014-04-16T00:00:00Z");

	/** The Constant FIVE. */
	private static final int FIVE = 5;

	/** The Constant FOUR. */
	private static final int FOUR = 4;

	/** The Constant FOURTY_FIVE. */
	private static final int FOURTY_FIVE = 45;

	/** The Constant ONE. */
	private static final int ONE = 1;

	/** The Constant PRICE_CHEAP. */
	private static final Price PRICE_CHEAP = new PriceImpl(new double[] { 1, 2,
			4, 8 }, new double[] { 1, 2, 4, 8 });

	/** The Constant PRICE_EXPENSIVE. */
	private static final Price PRICE_EXPENSIVE = new PriceImpl(new double[] {
			1, 2, 4, 8 }, new double[] { 1000, 2000, 4000, 8000 });

	/** The Constant SEVEN. */
	private static final int SEVEN = 7;

	/** The Constant SIX. */
	private static final int SIX = 6;

	/** The Constant THREE. */
	private static final int THREE = 3;

	/** The Constant TWENTY_THREE. */
	private static final int TWENTY_THREE = 23;

	/** The Constant TWO. */
	private static final int TWO = 2;

	/** The Constant WATTS_0. */
	private static final Amount<Power> WATTS_0 = Amount.valueOf(0, Power.UNIT);

	/** The Constant WATTS_200. */
	private static final Amount<Power> WATTS_200 = Amount.valueOf(200,
			Power.UNIT);

	/** The Constant WATTS_1201. */
	private static final Amount<Power> WATTS_1201 = Amount.valueOf(1201,
			Power.UNIT);

	/** The Constant WATTS_MINUS_1. */
	private static final Amount<Power> WATTS_MINUS_1 = Amount.valueOf(-1,
			Power.UNIT);

	/** The Constant WATTS_MINUS_200. */
	private static final Amount<Power> WATTS_MINUS_200 = Amount.valueOf(-200,
			Power.UNIT);

	/** The Constant ZERO. */
	private static final int ZERO = 0;

	/** The Constant EIGHT. */
	private static final int EIGHT = 8;

	/** The control entities provider. */
	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The pricing service. */
	@Mock
	private PricingService pricingService;

	/** The pso. */
	private ControlAlgorithm pso;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(controlEntitiesProvider.getNewOptimizedRun()).thenAnswer(
				new Answer<OptimizedRun>() {
					@Override
					public OptimizedRun answer(final InvocationOnMock invocation)
							throws Throwable {
						return new OptimizedRunImpl(
								"some OptimizedRunImpl id...");
					}
				});
		when(controlEntitiesProvider.getNewSlot()).thenAnswer(
				new Answer<Slot>() {
					@Override
					public Slot answer(final InvocationOnMock invocation)
							throws Throwable {
						return new SlotImpl("some slot id...");
					}
				});

		this.pso = new PSORegenerativeLoadOptimizer(pricingService,
				controlEntitiesProvider);
	}

	/**
	 * Test find best configuration two slots.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindBestConfigurationEightSlots24h() {
		DateTimeUtils.setCurrentMillisFixed(CLOCK_0.getMillis());
		final int numRuns = EIGHT;

		when(pricingService.getCostFunction(Mockito.any(DateTime.class)))
				.thenAnswer(new Answer<CostFunction>() {
					@Override
					public CostFunction answer(final InvocationOnMock invocation)
							throws Throwable {
						DateTime at = (DateTime) invocation.getArguments()[0];

						CostFunctionImpl costFunction;
						if (at.isBefore(CLOCK_3)) {
							costFunction = new CostFunctionImpl(CLOCK_0,
									CLOCK_3, PRICE_CHEAP);
						} else if ((at.isAfter(CLOCK_3) || at.isEqual(CLOCK_3))
								&& at.isBefore(CLOCK_6)) {
							costFunction = new CostFunctionImpl(CLOCK_3,
									CLOCK_6, PRICE_EXPENSIVE);
						} else if ((at.isAfter(CLOCK_6) || at.isEqual(CLOCK_6))
								&& at.isBefore(CLOCK_9)) {
							costFunction = new CostFunctionImpl(CLOCK_6,
									CLOCK_9, PRICE_CHEAP);
						} else if ((at.isAfter(CLOCK_9) || at.isEqual(CLOCK_9))
								&& at.isBefore(CLOCK_12)) {
							costFunction = new CostFunctionImpl(CLOCK_9,
									CLOCK_12, PRICE_EXPENSIVE);
						} else if ((at.isAfter(CLOCK_12) || at
								.isEqual(CLOCK_12)) && at.isBefore(CLOCK_15)) {
							costFunction = new CostFunctionImpl(CLOCK_12,
									CLOCK_15, PRICE_CHEAP);
						} else if ((at.isAfter(CLOCK_15) || at
								.isEqual(CLOCK_15)) && at.isBefore(CLOCK_18)) {
							costFunction = new CostFunctionImpl(CLOCK_15,
									CLOCK_18, PRICE_EXPENSIVE);
						} else if ((at.isAfter(CLOCK_18) || at
								.isEqual(CLOCK_18)) && at.isBefore(CLOCK_21)) {
							costFunction = new CostFunctionImpl(CLOCK_18,
									CLOCK_21, PRICE_CHEAP);
						} else {
							costFunction = new CostFunctionImpl(CLOCK_21,
									CLOCK_24, PRICE_EXPENSIVE);
						}

						return costFunction;
					}
				});

		Configuration currentConfiguration = createConfiguration(numRuns,
				Period.hours(THREE));

		Map<DateTime, Amount<Power>> charges = new ConcurrentSkipListMap<>();
		charges.put(CLOCK_9, WATTS_1201);
		charges.put(CLOCK_18, WATTS_1201);

		Map<DateTime, Amount<Power>> losses = new ConcurrentSkipListMap<>();
		losses.put(DateTime.parse("2014-04-15T09:00:00Z"), WATTS_200);
		losses.put(DateTime.parse("2014-04-15T09:15:00Z"), WATTS_200);
		losses.put(DateTime.parse("2014-04-15T09:30:00Z"), WATTS_200);
		losses.put(DateTime.parse("2014-04-15T09:45:00Z"), WATTS_200);
		losses.put(DateTime.parse("2014-04-15T18:00:00Z"), WATTS_200);
		losses.put(DateTime.parse("2014-04-15T18:15:00Z"), WATTS_200);
		losses.put(DateTime.parse("2014-04-15T18:30:00Z"), WATTS_200);
		losses.put(DateTime.parse("2014-04-15T18:45:00Z"), WATTS_200);
		currentConfiguration.getPossibleRunsConfiguration().getRunConstraint()
				.setChargesAtPointsInTime(charges);
		currentConfiguration.getPossibleRunsConfiguration().getRunConstraint()
				.setLossOfEnergyAtPointsInTime(losses);

		List<OptimizedRun> result = pso
				.findBestConfiguration(currentConfiguration);

		assertThat(result, is(notNullValue()));
		assertThat(result, hasSize(numRuns));
		assertTrue(result.get(0).getStartTime().equals(CLOCK_0));
		assertTrue(result.get(1).getStartTime().equals(CLOCK_3));

		assertThat(result.get(ZERO).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200));
		assertThat(result.get(ONE).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));
		assertThat(result.get(TWO).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200));
		assertThat(result.get(THREE).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_MINUS_200));
		assertThat(result.get(FOUR).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200));
		assertThat(result.get(FIVE).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_MINUS_200));
		assertThat(result.get(SIX).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200));
		assertThat(result.get(SEVEN).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_MINUS_200));

		for (OptimizedRun run : result) {
			if (!run.getNeededSlots().get(0).getLoad().equals(WATTS_0)) {
				return;
			}
		}
		Assert.fail("not all slots should be 0 W");
	}

	/**
	 * Test find best configuration two slots.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindBestConfigurationEightSlots() {
		DateTimeUtils.setCurrentMillisFixed(CLOCK_0.getMillis());

		when(pricingService.getCostFunction(Mockito.any(DateTime.class)))
				.thenAnswer(new Answer<CostFunction>() {
					@Override
					public CostFunction answer(final InvocationOnMock invocation)
							throws Throwable {
						DateTime at = (DateTime) invocation.getArguments()[0];

						CostFunctionImpl costFunction;
						if (at.isBefore(CLOCK_1)) {
							costFunction = new CostFunctionImpl(CLOCK_0,
									CLOCK_1, PRICE_CHEAP);
						} else if (at.isAfter(CLOCK_1) && at.isBefore(CLOCK_2)) {
							costFunction = new CostFunctionImpl(CLOCK_1,
									CLOCK_2, PRICE_EXPENSIVE);
						} else if (at.isAfter(CLOCK_2) && at.isBefore(CLOCK_3)) {
							costFunction = new CostFunctionImpl(CLOCK_2,
									CLOCK_3, PRICE_CHEAP);
						} else if (at.isAfter(CLOCK_3) && at.isBefore(CLOCK_4)) {
							costFunction = new CostFunctionImpl(CLOCK_3,
									CLOCK_4, PRICE_EXPENSIVE);
						} else if (at.isAfter(CLOCK_4) && at.isBefore(CLOCK_5)) {
							costFunction = new CostFunctionImpl(CLOCK_4,
									CLOCK_5, PRICE_CHEAP);
						} else if (at.isAfter(CLOCK_5) && at.isBefore(CLOCK_6)) {
							costFunction = new CostFunctionImpl(CLOCK_5,
									CLOCK_6, PRICE_EXPENSIVE);
						} else if (at.isAfter(CLOCK_6) && at.isBefore(CLOCK_7)) {
							costFunction = new CostFunctionImpl(CLOCK_6,
									CLOCK_7, PRICE_CHEAP);
						} else {
							costFunction = new CostFunctionImpl(CLOCK_7,
									CLOCK_8, PRICE_EXPENSIVE);
						}
						return costFunction;
					}
				});

		int numRuns = EIGHT;
		Configuration currentConfiguration = createConfiguration(numRuns,
				Period.hours(1));

		Map<DateTime, Amount<Power>> charges = new ConcurrentSkipListMap<>();
		Map<DateTime, Amount<Power>> losses = new ConcurrentSkipListMap<>();
		currentConfiguration.getPossibleRunsConfiguration().getRunConstraint()
				.setChargesAtPointsInTime(charges);
		currentConfiguration.getPossibleRunsConfiguration().getRunConstraint()
				.setLossOfEnergyAtPointsInTime(losses);

		List<OptimizedRun> result = pso
				.findBestConfiguration(currentConfiguration);

		assertThat(result, is(notNullValue()));
		assertThat(result, hasSize(numRuns));
		assertTrue(result.get(0).getStartTime().equals(CLOCK_0));
		assertTrue(result.get(1).getStartTime().equals(CLOCK_1));

		assertThat(result.get(ZERO).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200));
		assertThat(result.get(ONE).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));
		assertThat(result.get(TWO).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));
		assertThat(result.get(THREE).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));
		assertThat(result.get(FOUR).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));
		assertThat(result.get(FIVE).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));
		assertThat(result.get(SIX).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));
		assertThat(result.get(SEVEN).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));

		for (OptimizedRun run : result) {
			if (!run.getNeededSlots().get(0).getLoad().equals(WATTS_0)) {
				return;
			}
		}
		Assert.fail("not all slots should be 0 W");
	}

	/**
	 * Test find best configuration four slots.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindBestConfigurationFourSlots() {
		DateTimeUtils.setCurrentMillisFixed(CLOCK_0.getMillis());

		when(pricingService.getCostFunction(Mockito.any(DateTime.class)))
				.thenAnswer(new Answer<CostFunction>() {

					@Override
					public CostFunction answer(final InvocationOnMock invocation)
							throws Throwable {
						DateTime at = (DateTime) invocation.getArguments()[0];

						CostFunctionImpl costFunction;
						if (at.isBefore(CLOCK_1)) {
							costFunction = new CostFunctionImpl(CLOCK_0,
									CLOCK_1, PRICE_CHEAP);
						} else if (at.isAfter(CLOCK_1) && at.isBefore(CLOCK_2)) {
							costFunction = new CostFunctionImpl(CLOCK_1,
									CLOCK_2, PRICE_CHEAP);
						} else if (at.isAfter(CLOCK_2) && at.isBefore(CLOCK_3)) {
							costFunction = new CostFunctionImpl(CLOCK_2,
									CLOCK_3, PRICE_EXPENSIVE);
						} else {
							costFunction = new CostFunctionImpl(CLOCK_3,
									CLOCK_4, PRICE_EXPENSIVE);
						}
						return costFunction;
					}
				});

		int numRuns = FOUR;
		Configuration currentConfiguration = createConfiguration(numRuns,
				Period.hours(1));

		Map<DateTime, Amount<Power>> charges = new ConcurrentSkipListMap<>();
		Map<DateTime, Amount<Power>> losses = new ConcurrentSkipListMap<>();
		currentConfiguration.getPossibleRunsConfiguration().getRunConstraint()
				.setChargesAtPointsInTime(charges);
		currentConfiguration.getPossibleRunsConfiguration().getRunConstraint()
				.setLossOfEnergyAtPointsInTime(losses);

		List<OptimizedRun> result = pso
				.findBestConfiguration(currentConfiguration);

		assertThat(result, is(notNullValue()));
		assertThat(result, hasSize(numRuns));
		assertTrue(result.get(ZERO).getStartTime().equals(CLOCK_0));
		assertTrue(result.get(ONE).getStartTime().equals(CLOCK_1));
		assertTrue(result.get(TWO).getStartTime().equals(CLOCK_2));
		assertTrue(result.get(THREE).getStartTime().equals(CLOCK_3));

		assertThat(result.get(ZERO).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200));
		assertThat(result.get(ONE).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_MINUS_200, WATTS_200));
		assertThat(result.get(TWO).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_MINUS_200, WATTS_200));
		assertThat(result.get(THREE).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_MINUS_200, WATTS_200));

		for (OptimizedRun run : result) {
			if (!run.getNeededSlots().get(0).getLoad().equals(WATTS_0)) {
				return;
			}
		}
		Assert.fail("not all slots should be 0 W");
	}

	/**
	 * Test find best configuration two slots.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindBestConfigurationTwoSlots() {
		DateTimeUtils.setCurrentMillisFixed(CLOCK_0.getMillis());

		when(pricingService.getCostFunction(Mockito.any(DateTime.class)))
				.thenAnswer(new Answer<CostFunction>() {
					@Override
					public CostFunction answer(final InvocationOnMock invocation)
							throws Throwable {
						DateTime at = (DateTime) invocation.getArguments()[0];

						CostFunction costFunction;
						if (at.isBefore(CLOCK_1)) {
							costFunction = new CostFunctionImpl(CLOCK_0,
									CLOCK_1, PRICE_CHEAP);
						} else {
							costFunction = new CostFunctionImpl(CLOCK_3,
									CLOCK_4, PRICE_EXPENSIVE);
						}
						return costFunction;
					}
				});

		int numRuns = TWO;
		Configuration currentConfiguration = createConfiguration(numRuns,
				Period.hours(1));

		Map<DateTime, Amount<Power>> charges = new ConcurrentSkipListMap<>();
		Map<DateTime, Amount<Power>> losses = new ConcurrentSkipListMap<>();
		currentConfiguration.getPossibleRunsConfiguration().getRunConstraint()
				.setChargesAtPointsInTime(charges);
		currentConfiguration.getPossibleRunsConfiguration().getRunConstraint()
				.setLossOfEnergyAtPointsInTime(losses);

		List<OptimizedRun> result = pso
				.findBestConfiguration(currentConfiguration);

		assertThat(result, is(notNullValue()));
		assertThat(result, hasSize(numRuns));
		assertTrue(result.get(0).getStartTime().equals(CLOCK_0));
		assertTrue(result.get(1).getStartTime().equals(CLOCK_1));

		assertThat(result.get(0).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200));
		assertThat(result.get(1).getNeededSlots().get(0).getLoad(),
				isOneOf(WATTS_0, WATTS_200, WATTS_MINUS_200));

		for (OptimizedRun run : result) {
			if (!run.getNeededSlots().get(0).getLoad().equals(WATTS_0)) {
				return;
			}
		}
		Assert.fail("not all slots should be 0 W");
	}

	/**
	 * Creates the configuration.
	 * 
	 * @param numRuns
	 *            the num runs
	 * @param length
	 *            the length
	 * @return the configuration
	 */
	private Configuration createConfiguration(final int numRuns,
			final Period length) {
		Set<Amount<Power>> set = new ConcurrentSkipListSet<>();
		set.add(WATTS_0);
		set.add(WATTS_200);
		set.add(WATTS_MINUS_200);

		List<PossibleRun> possibleRuns = new LinkedList<>();
		DateTime startTime = CLOCK_0;
		for (int i = 0; i < numRuns; i++) {
			PossibleRun run = new PossibleRunImpl("my-possible-run-id-" + i);
			run.setEarliestStartTime(startTime);
			run.setLatestEndTime(startTime.plus(length));
			run.setLengthOfRun(length);
			run.setLoadFlexibilityOfRun(LoadFlexiblity.LIMITED_CHOICE);
			run.setPossibleLoads(set);
			possibleRuns.add(run);

			startTime = startTime.plus(length);
		}

		RunConstraint runConstraint = new RunConstraintImpl(
				"my-run-constraint-id");
		runConstraint.setStartCharge(WATTS_0);
		runConstraint.setMaximumCapacity(WATTS_1201);
		runConstraint.setMinimumCapacity(WATTS_MINUS_1);

		PossibleRunsConfiguration possibleRunsConfiguration = new PossibleRunsConfigurationImpl(
				"my-possible-runs-configuration-id");
		possibleRunsConfiguration.setPossibleRuns(possibleRuns);
		possibleRunsConfiguration.setRunConstraint(runConstraint);
		possibleRunsConfiguration.setLoadType(LoadType.REGENERATIVE_LOAD);

		Configuration currentConfiguration = new ConfigurationImpl("my-conf-id");
		currentConfiguration
				.setPossibleRunsConfiguration(possibleRunsConfiguration);
		Schedule scheduleOfOtherDevices = new ScheduleImpl(
				"schedule-of-other-devices");
		DateTime currentDateTime = CLOCK_0;
		DateTime end = currentDateTime.withTime(TWENTY_THREE, FOURTY_FIVE,
				ZERO, ZERO);
		Map<DateTime, Slot> scheduleOfOtherDevicesSchedule = new ConcurrentSkipListMap<>();
		while (currentDateTime.isBefore(end) || currentDateTime.isEqual(end)) {
			Slot slot = new SlotImpl("slot...");
			slot.setLoad(Amount.valueOf(0, Power.UNIT));
			scheduleOfOtherDevicesSchedule.put(currentDateTime, slot);
			currentDateTime = currentDateTime
					.plusMinutes(Constants.SLOT_INTERVAL);
		}
		scheduleOfOtherDevices.setSchedule(scheduleOfOtherDevicesSchedule);
		currentConfiguration.setScheduleOfOtherDevices(scheduleOfOtherDevices);

		return currentConfiguration;
	}
}
