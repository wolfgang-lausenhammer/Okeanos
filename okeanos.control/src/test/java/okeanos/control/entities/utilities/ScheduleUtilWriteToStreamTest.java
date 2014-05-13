package okeanos.control.entities.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.measure.quantity.Power;

import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.impl.SlotImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.Constants;

import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * The Class ScheduleUtilWriteToStreamTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class ScheduleUtilWriteToStreamTest {

	/** The Constant ZERO_WATT. */
	private static final Amount<Power> ZERO_WATT = Amount
			.valueOf(0, Power.UNIT);

	/** The Constant TEN_WATT. */
	private static final Amount<Power> TEN_WATT = Amount
			.valueOf(10, Power.UNIT);

	/** The Constant TWENTY_WATT. */
	private static final Amount<Power> TWENTY_WATT = Amount.valueOf(20,
			Power.UNIT);

	/** The schedule1. */
	private Schedule schedule1;

	/** The schedule2. */
	private Schedule schedule2;

	/** The control entities provider. */
	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The schedule util. */
	private ScheduleUtil scheduleUtil;

	/** The reference date time. */
	private DateTime referenceDateTime;

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		DateTime date1 = DateTime.parse("2014-03-20T00:00:00Z");
		DateTime date2 = DateTime.parse("2014-04-20T00:00:00Z");
		DateTime date3 = DateTime.parse("2014-05-20T00:00:00Z");
		DateTime date4 = DateTime.parse("2014-06-20T00:00:00Z");
		DateTime date5 = DateTime.parse("2014-07-20T00:00:00Z");

		return Arrays.asList(new Object[][] { { date1 }, { date2 }, { date3 },
				{ date4 }, { date5 } });
	}

	/**
	 * Instantiates a new schedule util write to stream test.
	 * 
	 * @param referenceDateTime
	 *            the reference date time
	 */
	public ScheduleUtilWriteToStreamTest(DateTime referenceDateTime) {
		this.referenceDateTime = referenceDateTime;

		prepareData();
	}

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(controlEntitiesProvider.getNewSchedule()).thenAnswer(
				new Answer<Schedule>() {
					@Override
					public Schedule answer(final InvocationOnMock invocation)
							throws Throwable {
						return new ScheduleImpl("mock-schedule-impl-id");
					}
				});
		Mockito.when(controlEntitiesProvider.getNewSlot()).thenAnswer(
				new Answer<Slot>() {
					@Override
					public Slot answer(final InvocationOnMock invocation)
							throws Throwable {
						return new SlotImpl("mock-slot-impl-id");
					}
				});

		this.scheduleUtil = new ScheduleUtil(controlEntitiesProvider);
	}

	/**
	 * Prepare data.
	 */
	public void prepareData() {
		DateTime startTime = referenceDateTime;
		DateTime endTime = startTime.withTime(23, 45, 0, 0);
		Slot slot1 = new SlotImpl("my-slot-id-1");
		slot1.setLoad(ZERO_WATT);
		Slot slot2 = new SlotImpl("my-slot-id-2");
		slot2.setLoad(TEN_WATT);
		Slot slot3 = new SlotImpl("my-slot-id-3");
		slot3.setLoad(TWENTY_WATT);

		schedule1 = new ScheduleImpl("my-schedule-id-11");
		Map<DateTime, Slot> map1 = new ConcurrentSkipListMap<>();
		DateTime currentDateTime = startTime;
		while (currentDateTime.isBefore(endTime)
				|| currentDateTime.isEqual(endTime)) {
			if (currentDateTime.isBefore(startTime.plusHours(3))) {
				map1.put(currentDateTime, slot1);
			} else if (currentDateTime.isBefore(startTime.plusHours(14))) {
				map1.put(currentDateTime, slot2);
			} else {
				map1.put(currentDateTime, slot3);
			}
			currentDateTime = currentDateTime
					.plusMinutes(Constants.SLOT_INTERVAL);
		}
		schedule1.setSchedule(map1);

		schedule2 = new ScheduleImpl("my-schedule-id-12");
		Map<DateTime, Slot> map2 = new ConcurrentSkipListMap<>();
		currentDateTime = startTime;
		while (currentDateTime.isBefore(endTime)
				|| currentDateTime.isEqual(endTime)) {
			if (currentDateTime.isBefore(startTime.plusHours(3))) {
				map2.put(currentDateTime, slot3);
			} else if (currentDateTime.isBefore(startTime.plusHours(14))) {
				map2.put(currentDateTime, slot2);
			} else {
				map2.put(currentDateTime, slot1);
			}
			currentDateTime = currentDateTime
					.plusMinutes(Constants.SLOT_INTERVAL);
		}
		schedule2.setSchedule(map2);
	}

	/**
	 * Test write schedule to stream schedule output stream.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testWriteScheduleToStreamScheduleOutputStream()
			throws IOException {
		ByteArrayOutputStream out1 = new ByteArrayOutputStream(1024);
		ByteArrayOutputStream out2 = new ByteArrayOutputStream(1024);

		scheduleUtil.writeScheduleToStream(schedule1, out1);
		scheduleUtil.writeScheduleToStream(schedule2, out2);

		Assert.assertThat(
				out1.toString("UTF-8"),
				IsEqual.equalTo("Date\tTotal Consumption\n2014-03-20T00:00\t0.0W\n2014-03-20T00:15\t0.0W\n2014-03-20T00:30\t0.0W\n2014-03-20T00:45\t0.0W\n2014-03-20T01:00\t0.0W\n2014-03-20T01:15\t0.0W\n2014-03-20T01:30\t0.0W\n2014-03-20T01:45\t0.0W\n2014-03-20T02:00\t0.0W\n2014-03-20T02:15\t0.0W\n2014-03-20T02:30\t0.0W\n2014-03-20T02:45\t0.0W\n2014-03-20T03:00\t10.0W\n2014-03-20T03:15\t10.0W\n2014-03-20T03:30\t10.0W\n2014-03-20T03:45\t10.0W\n2014-03-20T04:00\t10.0W\n2014-03-20T04:15\t10.0W\n2014-03-20T04:30\t10.0W\n2014-03-20T04:45\t10.0W\n2014-03-20T05:00\t10.0W\n2014-03-20T05:15\t10.0W\n2014-03-20T05:30\t10.0W\n2014-03-20T05:45\t10.0W\n2014-03-20T06:00\t10.0W\n2014-03-20T06:15\t10.0W\n2014-03-20T06:30\t10.0W\n2014-03-20T06:45\t10.0W\n2014-03-20T07:00\t10.0W\n2014-03-20T07:15\t10.0W\n2014-03-20T07:30\t10.0W\n2014-03-20T07:45\t10.0W\n2014-03-20T08:00\t10.0W\n2014-03-20T08:15\t10.0W\n2014-03-20T08:30\t10.0W\n2014-03-20T08:45\t10.0W\n2014-03-20T09:00\t10.0W\n2014-03-20T09:15\t10.0W\n2014-03-20T09:30\t10.0W\n2014-03-20T09:45\t10.0W\n2014-03-20T10:00\t10.0W\n2014-03-20T10:15\t10.0W\n2014-03-20T10:30\t10.0W\n2014-03-20T10:45\t10.0W\n2014-03-20T11:00\t10.0W\n2014-03-20T11:15\t10.0W\n2014-03-20T11:30\t10.0W\n2014-03-20T11:45\t10.0W\n2014-03-20T12:00\t10.0W\n2014-03-20T12:15\t10.0W\n2014-03-20T12:30\t10.0W\n2014-03-20T12:45\t10.0W\n2014-03-20T13:00\t10.0W\n2014-03-20T13:15\t10.0W\n2014-03-20T13:30\t10.0W\n2014-03-20T13:45\t10.0W\n2014-03-20T14:00\t20.0W\n2014-03-20T14:15\t20.0W\n2014-03-20T14:30\t20.0W\n2014-03-20T14:45\t20.0W\n2014-03-20T15:00\t20.0W\n2014-03-20T15:15\t20.0W\n2014-03-20T15:30\t20.0W\n2014-03-20T15:45\t20.0W\n2014-03-20T16:00\t20.0W\n2014-03-20T16:15\t20.0W\n2014-03-20T16:30\t20.0W\n2014-03-20T16:45\t20.0W\n2014-03-20T17:00\t20.0W\n2014-03-20T17:15\t20.0W\n2014-03-20T17:30\t20.0W\n2014-03-20T17:45\t20.0W\n2014-03-20T18:00\t20.0W\n2014-03-20T18:15\t20.0W\n2014-03-20T18:30\t20.0W\n2014-03-20T18:45\t20.0W\n2014-03-20T19:00\t20.0W\n2014-03-20T19:15\t20.0W\n2014-03-20T19:30\t20.0W\n2014-03-20T19:45\t20.0W\n2014-03-20T20:00\t20.0W\n2014-03-20T20:15\t20.0W\n2014-03-20T20:30\t20.0W\n2014-03-20T20:45\t20.0W\n2014-03-20T21:00\t20.0W\n2014-03-20T21:15\t20.0W\n2014-03-20T21:30\t20.0W\n2014-03-20T21:45\t20.0W\n2014-03-20T22:00\t20.0W\n2014-03-20T22:15\t20.0W\n2014-03-20T22:30\t20.0W\n2014-03-20T22:45\t20.0W\n2014-03-20T23:00\t20.0W\n2014-03-20T23:15\t20.0W\n2014-03-20T23:30\t20.0W\n2014-03-20T23:45\t20.0W\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
		Assert.assertThat(
				out2.toString("UTF-8"),
				IsEqual.equalTo("Date\tTotal Consumption\n2014-03-20T00:00\t20.0W\n2014-03-20T00:15\t20.0W\n2014-03-20T00:30\t20.0W\n2014-03-20T00:45\t20.0W\n2014-03-20T01:00\t20.0W\n2014-03-20T01:15\t20.0W\n2014-03-20T01:30\t20.0W\n2014-03-20T01:45\t20.0W\n2014-03-20T02:00\t20.0W\n2014-03-20T02:15\t20.0W\n2014-03-20T02:30\t20.0W\n2014-03-20T02:45\t20.0W\n2014-03-20T03:00\t10.0W\n2014-03-20T03:15\t10.0W\n2014-03-20T03:30\t10.0W\n2014-03-20T03:45\t10.0W\n2014-03-20T04:00\t10.0W\n2014-03-20T04:15\t10.0W\n2014-03-20T04:30\t10.0W\n2014-03-20T04:45\t10.0W\n2014-03-20T05:00\t10.0W\n2014-03-20T05:15\t10.0W\n2014-03-20T05:30\t10.0W\n2014-03-20T05:45\t10.0W\n2014-03-20T06:00\t10.0W\n2014-03-20T06:15\t10.0W\n2014-03-20T06:30\t10.0W\n2014-03-20T06:45\t10.0W\n2014-03-20T07:00\t10.0W\n2014-03-20T07:15\t10.0W\n2014-03-20T07:30\t10.0W\n2014-03-20T07:45\t10.0W\n2014-03-20T08:00\t10.0W\n2014-03-20T08:15\t10.0W\n2014-03-20T08:30\t10.0W\n2014-03-20T08:45\t10.0W\n2014-03-20T09:00\t10.0W\n2014-03-20T09:15\t10.0W\n2014-03-20T09:30\t10.0W\n2014-03-20T09:45\t10.0W\n2014-03-20T10:00\t10.0W\n2014-03-20T10:15\t10.0W\n2014-03-20T10:30\t10.0W\n2014-03-20T10:45\t10.0W\n2014-03-20T11:00\t10.0W\n2014-03-20T11:15\t10.0W\n2014-03-20T11:30\t10.0W\n2014-03-20T11:45\t10.0W\n2014-03-20T12:00\t10.0W\n2014-03-20T12:15\t10.0W\n2014-03-20T12:30\t10.0W\n2014-03-20T12:45\t10.0W\n2014-03-20T13:00\t10.0W\n2014-03-20T13:15\t10.0W\n2014-03-20T13:30\t10.0W\n2014-03-20T13:45\t10.0W\n2014-03-20T14:00\t0.0W\n2014-03-20T14:15\t0.0W\n2014-03-20T14:30\t0.0W\n2014-03-20T14:45\t0.0W\n2014-03-20T15:00\t0.0W\n2014-03-20T15:15\t0.0W\n2014-03-20T15:30\t0.0W\n2014-03-20T15:45\t0.0W\n2014-03-20T16:00\t0.0W\n2014-03-20T16:15\t0.0W\n2014-03-20T16:30\t0.0W\n2014-03-20T16:45\t0.0W\n2014-03-20T17:00\t0.0W\n2014-03-20T17:15\t0.0W\n2014-03-20T17:30\t0.0W\n2014-03-20T17:45\t0.0W\n2014-03-20T18:00\t0.0W\n2014-03-20T18:15\t0.0W\n2014-03-20T18:30\t0.0W\n2014-03-20T18:45\t0.0W\n2014-03-20T19:00\t0.0W\n2014-03-20T19:15\t0.0W\n2014-03-20T19:30\t0.0W\n2014-03-20T19:45\t0.0W\n2014-03-20T20:00\t0.0W\n2014-03-20T20:15\t0.0W\n2014-03-20T20:30\t0.0W\n2014-03-20T20:45\t0.0W\n2014-03-20T21:00\t0.0W\n2014-03-20T21:15\t0.0W\n2014-03-20T21:30\t0.0W\n2014-03-20T21:45\t0.0W\n2014-03-20T22:00\t0.0W\n2014-03-20T22:15\t0.0W\n2014-03-20T22:30\t0.0W\n2014-03-20T22:45\t0.0W\n2014-03-20T23:00\t0.0W\n2014-03-20T23:15\t0.0W\n2014-03-20T23:30\t0.0W\n2014-03-20T23:45\t0.0W\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
	}

	/**
	 * Test write schedule to stream map of date time schedule output stream.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testWriteScheduleToStreamMapOfDateTimeScheduleOutputStream()
			throws IOException {
		ByteArrayOutputStream out1 = new ByteArrayOutputStream(1024);
		ByteArrayOutputStream out2 = new ByteArrayOutputStream(1024);

		Map<DateTime, Schedule> schedule1Map = new HashMap<>();
		schedule1Map.put(referenceDateTime, schedule1);
		Map<DateTime, Schedule> schedule2Map = new HashMap<>();
		schedule2Map.put(referenceDateTime, schedule2);

		scheduleUtil.writeScheduleToStream(schedule1Map, out1);
		scheduleUtil.writeScheduleToStream(schedule2Map, out2);

		Assert.assertThat(
				out1.toString("UTF-8"),
				IsEqual.equalTo("Date\tTotal Consumption\n2014-03-20T00:00\t0.0W\n2014-03-20T00:15\t0.0W\n2014-03-20T00:30\t0.0W\n2014-03-20T00:45\t0.0W\n2014-03-20T01:00\t0.0W\n2014-03-20T01:15\t0.0W\n2014-03-20T01:30\t0.0W\n2014-03-20T01:45\t0.0W\n2014-03-20T02:00\t0.0W\n2014-03-20T02:15\t0.0W\n2014-03-20T02:30\t0.0W\n2014-03-20T02:45\t0.0W\n2014-03-20T03:00\t10.0W\n2014-03-20T03:15\t10.0W\n2014-03-20T03:30\t10.0W\n2014-03-20T03:45\t10.0W\n2014-03-20T04:00\t10.0W\n2014-03-20T04:15\t10.0W\n2014-03-20T04:30\t10.0W\n2014-03-20T04:45\t10.0W\n2014-03-20T05:00\t10.0W\n2014-03-20T05:15\t10.0W\n2014-03-20T05:30\t10.0W\n2014-03-20T05:45\t10.0W\n2014-03-20T06:00\t10.0W\n2014-03-20T06:15\t10.0W\n2014-03-20T06:30\t10.0W\n2014-03-20T06:45\t10.0W\n2014-03-20T07:00\t10.0W\n2014-03-20T07:15\t10.0W\n2014-03-20T07:30\t10.0W\n2014-03-20T07:45\t10.0W\n2014-03-20T08:00\t10.0W\n2014-03-20T08:15\t10.0W\n2014-03-20T08:30\t10.0W\n2014-03-20T08:45\t10.0W\n2014-03-20T09:00\t10.0W\n2014-03-20T09:15\t10.0W\n2014-03-20T09:30\t10.0W\n2014-03-20T09:45\t10.0W\n2014-03-20T10:00\t10.0W\n2014-03-20T10:15\t10.0W\n2014-03-20T10:30\t10.0W\n2014-03-20T10:45\t10.0W\n2014-03-20T11:00\t10.0W\n2014-03-20T11:15\t10.0W\n2014-03-20T11:30\t10.0W\n2014-03-20T11:45\t10.0W\n2014-03-20T12:00\t10.0W\n2014-03-20T12:15\t10.0W\n2014-03-20T12:30\t10.0W\n2014-03-20T12:45\t10.0W\n2014-03-20T13:00\t10.0W\n2014-03-20T13:15\t10.0W\n2014-03-20T13:30\t10.0W\n2014-03-20T13:45\t10.0W\n2014-03-20T14:00\t20.0W\n2014-03-20T14:15\t20.0W\n2014-03-20T14:30\t20.0W\n2014-03-20T14:45\t20.0W\n2014-03-20T15:00\t20.0W\n2014-03-20T15:15\t20.0W\n2014-03-20T15:30\t20.0W\n2014-03-20T15:45\t20.0W\n2014-03-20T16:00\t20.0W\n2014-03-20T16:15\t20.0W\n2014-03-20T16:30\t20.0W\n2014-03-20T16:45\t20.0W\n2014-03-20T17:00\t20.0W\n2014-03-20T17:15\t20.0W\n2014-03-20T17:30\t20.0W\n2014-03-20T17:45\t20.0W\n2014-03-20T18:00\t20.0W\n2014-03-20T18:15\t20.0W\n2014-03-20T18:30\t20.0W\n2014-03-20T18:45\t20.0W\n2014-03-20T19:00\t20.0W\n2014-03-20T19:15\t20.0W\n2014-03-20T19:30\t20.0W\n2014-03-20T19:45\t20.0W\n2014-03-20T20:00\t20.0W\n2014-03-20T20:15\t20.0W\n2014-03-20T20:30\t20.0W\n2014-03-20T20:45\t20.0W\n2014-03-20T21:00\t20.0W\n2014-03-20T21:15\t20.0W\n2014-03-20T21:30\t20.0W\n2014-03-20T21:45\t20.0W\n2014-03-20T22:00\t20.0W\n2014-03-20T22:15\t20.0W\n2014-03-20T22:30\t20.0W\n2014-03-20T22:45\t20.0W\n2014-03-20T23:00\t20.0W\n2014-03-20T23:15\t20.0W\n2014-03-20T23:30\t20.0W\n2014-03-20T23:45\t20.0W\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
		Assert.assertThat(
				out2.toString("UTF-8"),
				IsEqual.equalTo("Date\tTotal Consumption\n2014-03-20T00:00\t20.0W\n2014-03-20T00:15\t20.0W\n2014-03-20T00:30\t20.0W\n2014-03-20T00:45\t20.0W\n2014-03-20T01:00\t20.0W\n2014-03-20T01:15\t20.0W\n2014-03-20T01:30\t20.0W\n2014-03-20T01:45\t20.0W\n2014-03-20T02:00\t20.0W\n2014-03-20T02:15\t20.0W\n2014-03-20T02:30\t20.0W\n2014-03-20T02:45\t20.0W\n2014-03-20T03:00\t10.0W\n2014-03-20T03:15\t10.0W\n2014-03-20T03:30\t10.0W\n2014-03-20T03:45\t10.0W\n2014-03-20T04:00\t10.0W\n2014-03-20T04:15\t10.0W\n2014-03-20T04:30\t10.0W\n2014-03-20T04:45\t10.0W\n2014-03-20T05:00\t10.0W\n2014-03-20T05:15\t10.0W\n2014-03-20T05:30\t10.0W\n2014-03-20T05:45\t10.0W\n2014-03-20T06:00\t10.0W\n2014-03-20T06:15\t10.0W\n2014-03-20T06:30\t10.0W\n2014-03-20T06:45\t10.0W\n2014-03-20T07:00\t10.0W\n2014-03-20T07:15\t10.0W\n2014-03-20T07:30\t10.0W\n2014-03-20T07:45\t10.0W\n2014-03-20T08:00\t10.0W\n2014-03-20T08:15\t10.0W\n2014-03-20T08:30\t10.0W\n2014-03-20T08:45\t10.0W\n2014-03-20T09:00\t10.0W\n2014-03-20T09:15\t10.0W\n2014-03-20T09:30\t10.0W\n2014-03-20T09:45\t10.0W\n2014-03-20T10:00\t10.0W\n2014-03-20T10:15\t10.0W\n2014-03-20T10:30\t10.0W\n2014-03-20T10:45\t10.0W\n2014-03-20T11:00\t10.0W\n2014-03-20T11:15\t10.0W\n2014-03-20T11:30\t10.0W\n2014-03-20T11:45\t10.0W\n2014-03-20T12:00\t10.0W\n2014-03-20T12:15\t10.0W\n2014-03-20T12:30\t10.0W\n2014-03-20T12:45\t10.0W\n2014-03-20T13:00\t10.0W\n2014-03-20T13:15\t10.0W\n2014-03-20T13:30\t10.0W\n2014-03-20T13:45\t10.0W\n2014-03-20T14:00\t0.0W\n2014-03-20T14:15\t0.0W\n2014-03-20T14:30\t0.0W\n2014-03-20T14:45\t0.0W\n2014-03-20T15:00\t0.0W\n2014-03-20T15:15\t0.0W\n2014-03-20T15:30\t0.0W\n2014-03-20T15:45\t0.0W\n2014-03-20T16:00\t0.0W\n2014-03-20T16:15\t0.0W\n2014-03-20T16:30\t0.0W\n2014-03-20T16:45\t0.0W\n2014-03-20T17:00\t0.0W\n2014-03-20T17:15\t0.0W\n2014-03-20T17:30\t0.0W\n2014-03-20T17:45\t0.0W\n2014-03-20T18:00\t0.0W\n2014-03-20T18:15\t0.0W\n2014-03-20T18:30\t0.0W\n2014-03-20T18:45\t0.0W\n2014-03-20T19:00\t0.0W\n2014-03-20T19:15\t0.0W\n2014-03-20T19:30\t0.0W\n2014-03-20T19:45\t0.0W\n2014-03-20T20:00\t0.0W\n2014-03-20T20:15\t0.0W\n2014-03-20T20:30\t0.0W\n2014-03-20T20:45\t0.0W\n2014-03-20T21:00\t0.0W\n2014-03-20T21:15\t0.0W\n2014-03-20T21:30\t0.0W\n2014-03-20T21:45\t0.0W\n2014-03-20T22:00\t0.0W\n2014-03-20T22:15\t0.0W\n2014-03-20T22:30\t0.0W\n2014-03-20T22:45\t0.0W\n2014-03-20T23:00\t0.0W\n2014-03-20T23:15\t0.0W\n2014-03-20T23:30\t0.0W\n2014-03-20T23:45\t0.0W\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
	}

	/**
	 * Test write schedule to stream map of date time schedule map of date time
	 * map of string schedule output stream.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testWriteScheduleToStreamMapOfDateTimeScheduleMapOfDateTimeMapOfStringScheduleOutputStream()
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

		Map<String, Schedule> deviceSchedules = new HashMap<>();
		deviceSchedules.put("mydevice-name2", schedule2);

		scheduleUtil.writeScheduleToStream(schedule1, deviceSchedules, out);

		Assert.assertThat(
				out.toString("UTF-8"),
				IsEqual.equalTo("Date\tTotal Consumption\tmydevice-name2\n2014-03-20T00:00\t0.0W\t20.0W\n2014-03-20T00:15\t0.0W\t20.0W\n2014-03-20T00:30\t0.0W\t20.0W\n2014-03-20T00:45\t0.0W\t20.0W\n2014-03-20T01:00\t0.0W\t20.0W\n2014-03-20T01:15\t0.0W\t20.0W\n2014-03-20T01:30\t0.0W\t20.0W\n2014-03-20T01:45\t0.0W\t20.0W\n2014-03-20T02:00\t0.0W\t20.0W\n2014-03-20T02:15\t0.0W\t20.0W\n2014-03-20T02:30\t0.0W\t20.0W\n2014-03-20T02:45\t0.0W\t20.0W\n2014-03-20T03:00\t10.0W\t10.0W\n2014-03-20T03:15\t10.0W\t10.0W\n2014-03-20T03:30\t10.0W\t10.0W\n2014-03-20T03:45\t10.0W\t10.0W\n2014-03-20T04:00\t10.0W\t10.0W\n2014-03-20T04:15\t10.0W\t10.0W\n2014-03-20T04:30\t10.0W\t10.0W\n2014-03-20T04:45\t10.0W\t10.0W\n2014-03-20T05:00\t10.0W\t10.0W\n2014-03-20T05:15\t10.0W\t10.0W\n2014-03-20T05:30\t10.0W\t10.0W\n2014-03-20T05:45\t10.0W\t10.0W\n2014-03-20T06:00\t10.0W\t10.0W\n2014-03-20T06:15\t10.0W\t10.0W\n2014-03-20T06:30\t10.0W\t10.0W\n2014-03-20T06:45\t10.0W\t10.0W\n2014-03-20T07:00\t10.0W\t10.0W\n2014-03-20T07:15\t10.0W\t10.0W\n2014-03-20T07:30\t10.0W\t10.0W\n2014-03-20T07:45\t10.0W\t10.0W\n2014-03-20T08:00\t10.0W\t10.0W\n2014-03-20T08:15\t10.0W\t10.0W\n2014-03-20T08:30\t10.0W\t10.0W\n2014-03-20T08:45\t10.0W\t10.0W\n2014-03-20T09:00\t10.0W\t10.0W\n2014-03-20T09:15\t10.0W\t10.0W\n2014-03-20T09:30\t10.0W\t10.0W\n2014-03-20T09:45\t10.0W\t10.0W\n2014-03-20T10:00\t10.0W\t10.0W\n2014-03-20T10:15\t10.0W\t10.0W\n2014-03-20T10:30\t10.0W\t10.0W\n2014-03-20T10:45\t10.0W\t10.0W\n2014-03-20T11:00\t10.0W\t10.0W\n2014-03-20T11:15\t10.0W\t10.0W\n2014-03-20T11:30\t10.0W\t10.0W\n2014-03-20T11:45\t10.0W\t10.0W\n2014-03-20T12:00\t10.0W\t10.0W\n2014-03-20T12:15\t10.0W\t10.0W\n2014-03-20T12:30\t10.0W\t10.0W\n2014-03-20T12:45\t10.0W\t10.0W\n2014-03-20T13:00\t10.0W\t10.0W\n2014-03-20T13:15\t10.0W\t10.0W\n2014-03-20T13:30\t10.0W\t10.0W\n2014-03-20T13:45\t10.0W\t10.0W\n2014-03-20T14:00\t20.0W\t0.0W\n2014-03-20T14:15\t20.0W\t0.0W\n2014-03-20T14:30\t20.0W\t0.0W\n2014-03-20T14:45\t20.0W\t0.0W\n2014-03-20T15:00\t20.0W\t0.0W\n2014-03-20T15:15\t20.0W\t0.0W\n2014-03-20T15:30\t20.0W\t0.0W\n2014-03-20T15:45\t20.0W\t0.0W\n2014-03-20T16:00\t20.0W\t0.0W\n2014-03-20T16:15\t20.0W\t0.0W\n2014-03-20T16:30\t20.0W\t0.0W\n2014-03-20T16:45\t20.0W\t0.0W\n2014-03-20T17:00\t20.0W\t0.0W\n2014-03-20T17:15\t20.0W\t0.0W\n2014-03-20T17:30\t20.0W\t0.0W\n2014-03-20T17:45\t20.0W\t0.0W\n2014-03-20T18:00\t20.0W\t0.0W\n2014-03-20T18:15\t20.0W\t0.0W\n2014-03-20T18:30\t20.0W\t0.0W\n2014-03-20T18:45\t20.0W\t0.0W\n2014-03-20T19:00\t20.0W\t0.0W\n2014-03-20T19:15\t20.0W\t0.0W\n2014-03-20T19:30\t20.0W\t0.0W\n2014-03-20T19:45\t20.0W\t0.0W\n2014-03-20T20:00\t20.0W\t0.0W\n2014-03-20T20:15\t20.0W\t0.0W\n2014-03-20T20:30\t20.0W\t0.0W\n2014-03-20T20:45\t20.0W\t0.0W\n2014-03-20T21:00\t20.0W\t0.0W\n2014-03-20T21:15\t20.0W\t0.0W\n2014-03-20T21:30\t20.0W\t0.0W\n2014-03-20T21:45\t20.0W\t0.0W\n2014-03-20T22:00\t20.0W\t0.0W\n2014-03-20T22:15\t20.0W\t0.0W\n2014-03-20T22:30\t20.0W\t0.0W\n2014-03-20T22:45\t20.0W\t0.0W\n2014-03-20T23:00\t20.0W\t0.0W\n2014-03-20T23:15\t20.0W\t0.0W\n2014-03-20T23:30\t20.0W\t0.0W\n2014-03-20T23:45\t20.0W\t0.0W\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
	}

	/**
	 * Test write schedule to stream schedule map of string schedule output
	 * stream.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testWriteScheduleToStreamScheduleMapOfStringScheduleOutputStream()
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

		Map<DateTime, Schedule> scheduleMap = new HashMap<>();
		scheduleMap.put(referenceDateTime, schedule1);

		Map<String, Schedule> deviceSchedules = new HashMap<>();
		deviceSchedules.put("mydevice-name", schedule2);
		deviceSchedules.put("mydevice-name2", schedule1);
		Map<DateTime, Map<String, Schedule>> devicesSchedulesDays = new HashMap<>();
		devicesSchedulesDays.put(referenceDateTime, deviceSchedules);

		scheduleUtil.writeScheduleToStream(scheduleMap, devicesSchedulesDays,
				out);

		Assert.assertThat(
				out.toString("UTF-8"),
				IsEqual.equalTo("Date\tTotal Consumption\tmydevice-name\tmydevice-name2\n2014-03-20T00:00\t0.0W\t20.0W\t0.0W\n2014-03-20T00:15\t0.0W\t20.0W\t0.0W\n2014-03-20T00:30\t0.0W\t20.0W\t0.0W\n2014-03-20T00:45\t0.0W\t20.0W\t0.0W\n2014-03-20T01:00\t0.0W\t20.0W\t0.0W\n2014-03-20T01:15\t0.0W\t20.0W\t0.0W\n2014-03-20T01:30\t0.0W\t20.0W\t0.0W\n2014-03-20T01:45\t0.0W\t20.0W\t0.0W\n2014-03-20T02:00\t0.0W\t20.0W\t0.0W\n2014-03-20T02:15\t0.0W\t20.0W\t0.0W\n2014-03-20T02:30\t0.0W\t20.0W\t0.0W\n2014-03-20T02:45\t0.0W\t20.0W\t0.0W\n2014-03-20T03:00\t10.0W\t10.0W\t10.0W\n2014-03-20T03:15\t10.0W\t10.0W\t10.0W\n2014-03-20T03:30\t10.0W\t10.0W\t10.0W\n2014-03-20T03:45\t10.0W\t10.0W\t10.0W\n2014-03-20T04:00\t10.0W\t10.0W\t10.0W\n2014-03-20T04:15\t10.0W\t10.0W\t10.0W\n2014-03-20T04:30\t10.0W\t10.0W\t10.0W\n2014-03-20T04:45\t10.0W\t10.0W\t10.0W\n2014-03-20T05:00\t10.0W\t10.0W\t10.0W\n2014-03-20T05:15\t10.0W\t10.0W\t10.0W\n2014-03-20T05:30\t10.0W\t10.0W\t10.0W\n2014-03-20T05:45\t10.0W\t10.0W\t10.0W\n2014-03-20T06:00\t10.0W\t10.0W\t10.0W\n2014-03-20T06:15\t10.0W\t10.0W\t10.0W\n2014-03-20T06:30\t10.0W\t10.0W\t10.0W\n2014-03-20T06:45\t10.0W\t10.0W\t10.0W\n2014-03-20T07:00\t10.0W\t10.0W\t10.0W\n2014-03-20T07:15\t10.0W\t10.0W\t10.0W\n2014-03-20T07:30\t10.0W\t10.0W\t10.0W\n2014-03-20T07:45\t10.0W\t10.0W\t10.0W\n2014-03-20T08:00\t10.0W\t10.0W\t10.0W\n2014-03-20T08:15\t10.0W\t10.0W\t10.0W\n2014-03-20T08:30\t10.0W\t10.0W\t10.0W\n2014-03-20T08:45\t10.0W\t10.0W\t10.0W\n2014-03-20T09:00\t10.0W\t10.0W\t10.0W\n2014-03-20T09:15\t10.0W\t10.0W\t10.0W\n2014-03-20T09:30\t10.0W\t10.0W\t10.0W\n2014-03-20T09:45\t10.0W\t10.0W\t10.0W\n2014-03-20T10:00\t10.0W\t10.0W\t10.0W\n2014-03-20T10:15\t10.0W\t10.0W\t10.0W\n2014-03-20T10:30\t10.0W\t10.0W\t10.0W\n2014-03-20T10:45\t10.0W\t10.0W\t10.0W\n2014-03-20T11:00\t10.0W\t10.0W\t10.0W\n2014-03-20T11:15\t10.0W\t10.0W\t10.0W\n2014-03-20T11:30\t10.0W\t10.0W\t10.0W\n2014-03-20T11:45\t10.0W\t10.0W\t10.0W\n2014-03-20T12:00\t10.0W\t10.0W\t10.0W\n2014-03-20T12:15\t10.0W\t10.0W\t10.0W\n2014-03-20T12:30\t10.0W\t10.0W\t10.0W\n2014-03-20T12:45\t10.0W\t10.0W\t10.0W\n2014-03-20T13:00\t10.0W\t10.0W\t10.0W\n2014-03-20T13:15\t10.0W\t10.0W\t10.0W\n2014-03-20T13:30\t10.0W\t10.0W\t10.0W\n2014-03-20T13:45\t10.0W\t10.0W\t10.0W\n2014-03-20T14:00\t20.0W\t0.0W\t20.0W\n2014-03-20T14:15\t20.0W\t0.0W\t20.0W\n2014-03-20T14:30\t20.0W\t0.0W\t20.0W\n2014-03-20T14:45\t20.0W\t0.0W\t20.0W\n2014-03-20T15:00\t20.0W\t0.0W\t20.0W\n2014-03-20T15:15\t20.0W\t0.0W\t20.0W\n2014-03-20T15:30\t20.0W\t0.0W\t20.0W\n2014-03-20T15:45\t20.0W\t0.0W\t20.0W\n2014-03-20T16:00\t20.0W\t0.0W\t20.0W\n2014-03-20T16:15\t20.0W\t0.0W\t20.0W\n2014-03-20T16:30\t20.0W\t0.0W\t20.0W\n2014-03-20T16:45\t20.0W\t0.0W\t20.0W\n2014-03-20T17:00\t20.0W\t0.0W\t20.0W\n2014-03-20T17:15\t20.0W\t0.0W\t20.0W\n2014-03-20T17:30\t20.0W\t0.0W\t20.0W\n2014-03-20T17:45\t20.0W\t0.0W\t20.0W\n2014-03-20T18:00\t20.0W\t0.0W\t20.0W\n2014-03-20T18:15\t20.0W\t0.0W\t20.0W\n2014-03-20T18:30\t20.0W\t0.0W\t20.0W\n2014-03-20T18:45\t20.0W\t0.0W\t20.0W\n2014-03-20T19:00\t20.0W\t0.0W\t20.0W\n2014-03-20T19:15\t20.0W\t0.0W\t20.0W\n2014-03-20T19:30\t20.0W\t0.0W\t20.0W\n2014-03-20T19:45\t20.0W\t0.0W\t20.0W\n2014-03-20T20:00\t20.0W\t0.0W\t20.0W\n2014-03-20T20:15\t20.0W\t0.0W\t20.0W\n2014-03-20T20:30\t20.0W\t0.0W\t20.0W\n2014-03-20T20:45\t20.0W\t0.0W\t20.0W\n2014-03-20T21:00\t20.0W\t0.0W\t20.0W\n2014-03-20T21:15\t20.0W\t0.0W\t20.0W\n2014-03-20T21:30\t20.0W\t0.0W\t20.0W\n2014-03-20T21:45\t20.0W\t0.0W\t20.0W\n2014-03-20T22:00\t20.0W\t0.0W\t20.0W\n2014-03-20T22:15\t20.0W\t0.0W\t20.0W\n2014-03-20T22:30\t20.0W\t0.0W\t20.0W\n2014-03-20T22:45\t20.0W\t0.0W\t20.0W\n2014-03-20T23:00\t20.0W\t0.0W\t20.0W\n2014-03-20T23:15\t20.0W\t0.0W\t20.0W\n2014-03-20T23:30\t20.0W\t0.0W\t20.0W\n2014-03-20T23:45\t20.0W\t0.0W\t20.0W\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
	}

}
