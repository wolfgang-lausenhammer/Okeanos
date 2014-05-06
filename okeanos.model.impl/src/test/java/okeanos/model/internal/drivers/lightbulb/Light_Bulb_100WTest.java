package okeanos.model.internal.drivers.lightbulb;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.measure.quantity.Power;

import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.SlotImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.Constants;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The Class Light_Bulb_100WTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Light_Bulb_100WTest {

	/** The Constant DEVICE_ID. */
	private static final String DEVICE_ID = "my-device-id";;

	/** The Constant FOUR. */
	private static final int FOUR = 4;

	/** The Constant HUNDRED_WATTS. */
	private static final Amount<Power> HUNDRED_WATTS = Amount.valueOf(100,
			Power.UNIT);

	/** The Constant NINETY_FIVE. */
	private static final int NINETY_FIVE = 95;

	/** The Constant ONE. */
	private static final int ONE = 1;

	/** The Constant SOME_DATE. */
	private static final DateTime SOME_DATE = DateTime
			.parse("2014-04-25T00:00:00Z");

	/** The Constant THREE. */
	private static final int THREE = 3;

	/** The Constant TWO. */
	private static final int TWO = 2;

	/** The Constant ZERO. */
	private static final int ZERO = 0;

	/** The Constant ZERO_WATTS. */
	private static final Amount<Power> ZERO_WATTS = Amount.valueOf(0,
			Power.UNIT);

	/** The control entities provider. */
	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The device. */
	private Light_Bulb_100W device;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Resource loadProfileResource = new ClassPathResource(
				"test-load-profile.json");
		final AtomicInteger slotId = new AtomicInteger();
		Mockito.when(controlEntitiesProvider.getNewSlot()).thenAnswer(
				new Answer<Slot>() {

					@Override
					public Slot answer(final InvocationOnMock invocation)
							throws Throwable {
						return new SlotImpl("my-slot-id-"
								+ slotId.getAndIncrement());
					}
				});
		final AtomicInteger possibleRunId = new AtomicInteger();
		Mockito.when(controlEntitiesProvider.getNewPossibleRun()).thenAnswer(
				new Answer<PossibleRun>() {

					@Override
					public PossibleRun answer(final InvocationOnMock invocation)
							throws Throwable {
						return new PossibleRunImpl("my-possible-run-id-"
								+ possibleRunId.getAndIncrement());
					}
				});

		this.device = new Light_Bulb_100W(loadProfileResource, DEVICE_ID,
				controlEntitiesProvider);
	}

	/**
	 * Test get consumption.
	 */
	@Test
	public void testGetConsumption() {
		DateTimeUtils.setCurrentMillisFixed(SOME_DATE.getMillis());

		Amount<Power> consumptionNow = device.getConsumption();
		Amount<Power> consumptionNow2 = device.getConsumptionIn(Period
				.minutes(Constants.SLOT_INTERVAL));
		Amount<Power> consumptionNow3 = device.getConsumptionIn(Period
				.minutes(TWO * Constants.SLOT_INTERVAL));
		Amount<Power> consumptionNow4 = device.getConsumptionIn(Period
				.minutes(THREE * Constants.SLOT_INTERVAL));
		Amount<Power> consumptionNow5 = device.getConsumptionIn(Period
				.minutes(FOUR * Constants.SLOT_INTERVAL));

		assertTrue(consumptionNow.approximates(ZERO_WATTS));
		assertTrue(consumptionNow2.approximates(ZERO_WATTS));
		assertTrue(consumptionNow3.approximates(HUNDRED_WATTS));
		assertTrue(consumptionNow4.approximates(HUNDRED_WATTS));
		assertTrue(consumptionNow5.approximates(HUNDRED_WATTS));
	}

	/**
	 * Test get consumption.
	 */
	@Test
	public void testGetConsumptionPlusTwoDays() {
		DateTimeUtils.setCurrentMillisFixed(SOME_DATE.plusDays(2).getMillis());

		Amount<Power> consumptionNow = device.getConsumption();
		Amount<Power> consumptionNow2 = device.getConsumptionIn(Period
				.minutes(Constants.SLOT_INTERVAL));
		Amount<Power> consumptionNow3 = device.getConsumptionIn(Period
				.minutes(TWO * Constants.SLOT_INTERVAL));
		Amount<Power> consumptionNow4 = device.getConsumptionIn(Period
				.minutes(THREE * Constants.SLOT_INTERVAL));
		Amount<Power> consumptionNow5 = device.getConsumptionIn(Period
				.minutes(FOUR * Constants.SLOT_INTERVAL));

		assertTrue(consumptionNow.approximates(ZERO_WATTS));
		assertTrue(consumptionNow2.approximates(ZERO_WATTS));
		assertTrue(consumptionNow3.approximates(HUNDRED_WATTS));
		assertTrue(consumptionNow4.approximates(HUNDRED_WATTS));
		assertTrue(consumptionNow5.approximates(HUNDRED_WATTS));
	}

	/**
	 * Test get id.
	 */
	@Test
	public void testGetId() {
		String id = device.getId();

		assertThat(id, is(equalTo(DEVICE_ID)));
	}

	/**
	 * Test get possible runs.
	 */
	@Test
	public void testGetPossibleRuns() {
		DateTimeUtils.setCurrentMillisFixed(SOME_DATE.getMillis());
		List<PossibleRun> possibleRuns = device.getPossibleRuns();

		assertThat(possibleRuns, is(notNullValue()));
		assertThat(possibleRuns, hasSize(1));

		PossibleRun run = possibleRuns.get(0);
		assertThat(run.getNeededSlots(), hasSize(NINETY_FIVE));
		assertTrue(run.getNeededSlots().get(ZERO).getLoad()
				.approximates(Amount.valueOf(0, Power.UNIT)));
		assertTrue(run.getNeededSlots().get(ONE).getLoad()
				.approximates(Amount.valueOf(0, Power.UNIT)));
		assertTrue(run.getNeededSlots().get(TWO).getLoad()
				.approximates(HUNDRED_WATTS));
		assertTrue(run.getNeededSlots().get(THREE).getLoad()
				.approximates(HUNDRED_WATTS));
	}

}
