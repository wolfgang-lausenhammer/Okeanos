package okeanos.control.internal.algorithms;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.measure.quantity.Power;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.LoadType;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.ConfigurationImpl;
import okeanos.control.entities.impl.OptimizedRunImpl;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.SlotImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.control.internal.algorithms.pso.CostFunctionImpl;
import okeanos.control.internal.algorithms.pso.PriceImpl;
import okeanos.data.services.PricingService;
import okeanos.data.services.entities.CostFunction;
import okeanos.data.services.entities.Price;

import org.hamcrest.collection.IsCollectionWithSize;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.jscience.physics.amount.Amount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Contains;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ParticleSwarmOptimizationControlAlgorithmTest {
	private static final DateTime ONE_OCLOCK = DateTime
			.parse("2014-04-15T01:00:00Z");
	private static final DateTime ELEVEN_OCLOCK = DateTime
			.parse("2014-04-15T23:00:00Z");
	private static final DateTime NOON = DateTime.parse("2014-04-15T12:00Z");
	private static final DateTime MORNING = DateTime.parse("2014-04-15T03:00Z");
	private static final DateTime MIDNIGHT = DateTime
			.parse("2014-04-15T00:00:00Z");

	@Mock
	private PricingService pricingService;

	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	private ParticleSwarmOptimizationControlAlgorithm pso;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(pricingService.getCostFunction(Mockito.any(DateTime.class)))
				.thenAnswer(new Answer<CostFunction>() {

					@Override
					public CostFunction answer(final InvocationOnMock invocation)
							throws Throwable {
						DateTime at = (DateTime) invocation.getArguments()[0];

						if (at.isBefore(MORNING)) {
							Price price = new PriceImpl(new double[] { 1, 2, 4,
									8 }, new double[] { 1.12, 2.464, 5.421,
									11.926 });
							CostFunction beforeNoonCostFunction = new CostFunctionImpl(
									MIDNIGHT, MORNING, price);
							return beforeNoonCostFunction;
						} else if (at.isBefore(NOON)) {
							Price price = new PriceImpl(new double[] { 1, 2, 4,
									8 }, new double[] { 11.2, 24.64, 54.21,
									119.26 });
							CostFunction beforeNoonCostFunction = new CostFunctionImpl(
									MORNING, NOON, price);
							return beforeNoonCostFunction;
						} else {
							Price price = new PriceImpl(new double[] { 1, 2, 4,
									8 }, new double[] { 110.2, 240.64, 540.21,
									1190.26 });
							CostFunction afterNoonCostFunction = new CostFunctionImpl(
									NOON, MIDNIGHT.plusDays(1), price);
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

		this.pso = new ParticleSwarmOptimizationControlAlgorithm(
				pricingService, controlEntitiesProvider);
	}

	@Test
	public void testFindBestConfigurationBeforeNoon() {
		DateTimeUtils.setCurrentMillisFixed(DateTime.parse(
				"2014-04-15T10:00:00Z").getMillis());

		Configuration currentConfiguration = new ConfigurationImpl("my-conf-id");
		PossibleRun possibleRun = new PossibleRunImpl("my-possible-run-id");
		possibleRun.setEarliestStartTime(ONE_OCLOCK);
		possibleRun.setLatestEndTime(ELEVEN_OCLOCK);
		possibleRun.setLoadType(LoadType.CONSUMER);
		Slot slot1 = new SlotImpl("slot-1");
		slot1.setLoad(Amount.valueOf(1, Power.UNIT));
		Slot slot2 = new SlotImpl("slot-2");
		slot2.setLoad(Amount.valueOf(2, Power.UNIT));
		Slot slot3 = new SlotImpl("slot-3");
		slot3.setLoad(Amount.valueOf(1, Power.UNIT));
		List<Slot> neededSlots = Arrays.asList(slot1, slot2, slot3);
		possibleRun.setNeededSlots(neededSlots);
		List<PossibleRun> possibleRuns = Arrays.asList(possibleRun);
		currentConfiguration.setPossibleRun(possibleRuns);

		List<OptimizedRun> result = pso
				.findBestConfiguration(currentConfiguration);

		assertThat(result, is(notNullValue()));
		assertThat(result, hasSize(1));
		assertTrue(result.get(0).getStartTime().isBefore(MORNING));
	}
}
