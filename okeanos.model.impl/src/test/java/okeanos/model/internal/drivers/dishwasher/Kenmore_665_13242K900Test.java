package okeanos.model.internal.drivers.dishwasher;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.measure.quantity.Power;

import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.PossibleRunsConfigurationImpl;
import okeanos.control.entities.impl.ScheduleImpl;
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
 * The Class Kenmore_665_13242K900Test.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Kenmore_665_13242K900Test {

	/** The Constant DEVICE_ID. */
	private static final String DEVICE_ID = "my-device-id";

	/** The Constant FOUR. */
	private static final int FOUR = 4;

	/** The Constant HUNDRED_WATTS. */
	private static final Amount<Power> HUNDRED_WATTS = Amount.valueOf(100,
			Power.UNIT);

	/** The Constant ONE. */
	private static final int ONE = 1;

	/** The Constant THREE. */
	private static final int THREE = 3;

	/** The Constant TWO. */
	private static final int TWO = 2;

	/** The Constant TWO_HUNDRED_WATTS. */
	private static final Amount<Power> TWO_HUNDRED_WATTS = Amount.valueOf(200,
			Power.UNIT);

	/** The Constant ZERO. */
	private static final int ZERO = 0;

	/** The control entities provider. */
	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The device. */
	private Kenmore_665_13242K900 device;

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
		final AtomicInteger possibleRunConfigurationId = new AtomicInteger();
		Mockito.when(controlEntitiesProvider.getNewPossibleRunsConfiguration())
				.thenAnswer(new Answer<PossibleRunsConfigurationImpl>() {

					@Override
					public PossibleRunsConfigurationImpl answer(
							final InvocationOnMock invocation) throws Throwable {
						return new PossibleRunsConfigurationImpl(
								"my-possible-run-configuration-id-"
										+ possibleRunConfigurationId
												.getAndIncrement());
					}
				});

		this.device = new Kenmore_665_13242K900(loadProfileResource, DEVICE_ID,
				controlEntitiesProvider);
	}

	/**
	 * Test apply schedule.
	 */
	@Test
	public void testApplySchedule() {
		Schedule schedule = new ScheduleImpl("schedule-id");
		DateTime someTime = DateTime.parse("2014-03-20T00:00:00-04:00");
		Map<DateTime, Slot> mapSchedule = new ConcurrentSkipListMap<>();
		Slot slot1 = new SlotImpl("slot-id");
		slot1.setLoad(HUNDRED_WATTS);
		Slot slot2 = new SlotImpl("slot-id");
		slot2.setLoad(TWO_HUNDRED_WATTS);
		mapSchedule.put(someTime, slot1);
		mapSchedule.put(someTime.plusMinutes(Constants.SLOT_INTERVAL), slot2);
		schedule.setSchedule(mapSchedule);

		device.applySchedule(schedule);

		DateTimeUtils.setCurrentMillisFixed(someTime.getMillis());
		assertTrue(device.getConsumption().approximates(HUNDRED_WATTS));
		assertTrue(device.getConsumptionIn(
				Period.minutes(Constants.SLOT_INTERVAL)).approximates(
				TWO_HUNDRED_WATTS));
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
		List<PossibleRun> possibleRuns = device.getPossibleRunsConfiguration()
				.getPossibleRuns();

		assertThat(possibleRuns, is(notNullValue()));
		assertThat(possibleRuns, hasSize(1));

		PossibleRun run = possibleRuns.get(0);
		assertThat(run.getNeededSlots(), hasSize(FOUR));
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
