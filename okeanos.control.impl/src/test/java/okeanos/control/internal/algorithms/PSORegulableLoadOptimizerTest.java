package okeanos.control.internal.algorithms;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.measure.quantity.Power;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
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
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * The Class ParticleSwarmOptimizationControlAlgorithmTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class PSORegulableLoadOptimizerTest {

	/** The Constant ONE_OCLOCK. */
	private static final DateTime CLOCK_ONE = DateTime
			.parse("2014-04-15T01:00:00Z");

	/** The Constant MORNING. */
	private static final DateTime CLOCK_THREE = DateTime
			.parse("2014-04-15T03:00Z");

	/** The Constant NOON. */
	private static final DateTime CLOCK_TWELVE = DateTime
			.parse("2014-04-15T12:00Z");

	/** The Constant ELEVEN_OCLOCK. */
	private static final DateTime CLOCK_TWENTY_THREE = DateTime
			.parse("2014-04-15T23:00:00Z");

	/** The Constant MIDNIGHT. */
	private static final DateTime CLOCK_ZERO = DateTime
			.parse("2014-04-15T00:00:00Z");

	/** The Constant FOURTY_FIVE. */
	private static final int FOURTY_FIVE = 45;

	/** The Constant PRICE_AVERAGE. */
	private static final Price PRICE_AVERAGE = new PriceImpl(new double[] { 1,
			2, 4, 8 }, new double[] { 11.2, 24.64, 54.21, 119.26 });

	/** The Constant PRICE_CHEAP. */
	private static final Price PRICE_CHEAP = new PriceImpl(new double[] { 1, 2,
			4, 8 }, new double[] { 1.12, 2.464, 5.421, 11.926 });

	/** The Constant PRICE_EXPENSIVE. */
	private static final Price PRICE_EXPENSIVE = new PriceImpl(new double[] {
			1, 2, 4, 8 }, new double[] { 110.2, 240.64, 540.21, 1190.26 });

	/** The Constant TWENTY_THREE. */
	private static final int TWENTY_THREE = 23;

	/** The Constant WATTS_ONE. */
	private static final Amount<Power> WATTS_ONE = Amount
			.valueOf(1, Power.UNIT);

	/** The Constant WATTS_TWO. */
	private static final Amount<Power> WATTS_TWO = Amount
			.valueOf(2, Power.UNIT);

	/** The Constant WATTS_ZERO. */
	private static final Amount<Power> WATTS_ZERO = Amount.valueOf(0,
			Power.UNIT);

	/** The Constant ZERO. */
	private static final int ZERO = 0;

	/** The control entities provider. */
	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The pricing service. */
	@Mock
	private PricingService pricingService;

	/** The control algorithm. */
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

		when(pricingService.getCostFunction(Mockito.any(DateTime.class)))
				.thenAnswer(new Answer<CostFunction>() {

					@Override
					public CostFunction answer(final InvocationOnMock invocation)
							throws Throwable {
						DateTime at = (DateTime) invocation.getArguments()[0];

						if (at.isBefore(CLOCK_THREE)) {
							CostFunction beforeNoonCostFunction = new CostFunctionImpl(
									CLOCK_ZERO, CLOCK_THREE, PRICE_CHEAP);
							return beforeNoonCostFunction;
						} else if (at.isBefore(CLOCK_TWELVE)) {
							CostFunction beforeNoonCostFunction = new CostFunctionImpl(
									CLOCK_THREE, CLOCK_TWELVE, PRICE_AVERAGE);
							return beforeNoonCostFunction;
						} else {
							CostFunction afterNoonCostFunction = new CostFunctionImpl(
									CLOCK_TWELVE, CLOCK_ZERO.plusDays(1),
									PRICE_EXPENSIVE);
							return afterNoonCostFunction;
						}
					}
				});

		when(controlEntitiesProvider.getNewOptimizedRun()).thenAnswer(
				new Answer<OptimizedRun>() {
					@Override
					public OptimizedRun answer(final InvocationOnMock invocation)
							throws Throwable {
						return new OptimizedRunImpl(
								"some OptimizedRunImpl id...");
					}
				});

		this.pso = new PSORegulableLoadOptimizer(pricingService,
				controlEntitiesProvider);
	}

	/**
	 * Test find best configuration before noon.
	 */
	@Test
	public void testFindBestConfigurationBeforeNoon() {
		DateTimeUtils.setCurrentMillisFixed(CLOCK_ZERO.getMillis());

		Configuration currentConfiguration = new ConfigurationImpl("my-conf-id");
		PossibleRun possibleRun = new PossibleRunImpl("my-possible-run-id");
		possibleRun.setEarliestStartTime(CLOCK_ONE);
		possibleRun.setLatestEndTime(CLOCK_TWENTY_THREE);
		Slot slot1 = new SlotImpl("slot-1");
		slot1.setLoad(WATTS_ONE);
		Slot slot2 = new SlotImpl("slot-2");
		slot2.setLoad(WATTS_TWO);
		Slot slot3 = new SlotImpl("slot-3");
		slot3.setLoad(WATTS_ONE);
		List<Slot> neededSlots = Arrays.asList(slot1, slot2, slot3);
		possibleRun.setNeededSlots(neededSlots);
		List<PossibleRun> possibleRuns = Arrays.asList(possibleRun);

		RunConstraint runConstraint = new RunConstraintImpl(
				"my-run-constraint-id");

		PossibleRunsConfiguration possibleRunsConfiguration = new PossibleRunsConfigurationImpl(
				"my-possible-runs-configuration-id");
		possibleRunsConfiguration.setPossibleRuns(possibleRuns);
		possibleRunsConfiguration.setRunConstraint(runConstraint);

		currentConfiguration
				.setPossibleRunsConfiguration(possibleRunsConfiguration);
		Schedule scheduleOfOtherDevices = new ScheduleImpl(
				"schedule-of-other-devices");
		DateTime currentDateTime = CLOCK_ZERO;
		DateTime end = currentDateTime.withTime(TWENTY_THREE, FOURTY_FIVE,
				ZERO, ZERO);
		Map<DateTime, Slot> scheduleOfOtherDevicesSchedule = new ConcurrentSkipListMap<>();
		while (currentDateTime.isBefore(end) || currentDateTime.isEqual(end)) {
			Slot slot = new SlotImpl("slot...");
			slot.setLoad(WATTS_ZERO);
			scheduleOfOtherDevicesSchedule.put(currentDateTime, slot);
			currentDateTime = currentDateTime
					.plusMinutes(Constants.SLOT_INTERVAL);
		}
		scheduleOfOtherDevices.setSchedule(scheduleOfOtherDevicesSchedule);
		currentConfiguration.setScheduleOfOtherDevices(scheduleOfOtherDevices);

		List<OptimizedRun> result = pso
				.findBestConfiguration(currentConfiguration);

		assertThat(result, is(notNullValue()));
		assertThat(result, hasSize(1));
		assertTrue(result.get(0).getStartTime().isBefore(CLOCK_THREE));
	}
}
