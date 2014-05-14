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
				IsEqual.equalTo("Date\tTotal Consumption\n2014-03-20 00:00\t0.0\n2014-03-20 00:15\t0.0\n2014-03-20 00:30\t0.0\n2014-03-20 00:45\t0.0\n2014-03-20 01:00\t0.0\n2014-03-20 01:15\t0.0\n2014-03-20 01:30\t0.0\n2014-03-20 01:45\t0.0\n2014-03-20 02:00\t0.0\n2014-03-20 02:15\t0.0\n2014-03-20 02:30\t0.0\n2014-03-20 02:45\t0.0\n2014-03-20 03:00\t10.0\n2014-03-20 03:15\t10.0\n2014-03-20 03:30\t10.0\n2014-03-20 03:45\t10.0\n2014-03-20 04:00\t10.0\n2014-03-20 04:15\t10.0\n2014-03-20 04:30\t10.0\n2014-03-20 04:45\t10.0\n2014-03-20 05:00\t10.0\n2014-03-20 05:15\t10.0\n2014-03-20 05:30\t10.0\n2014-03-20 05:45\t10.0\n2014-03-20 06:00\t10.0\n2014-03-20 06:15\t10.0\n2014-03-20 06:30\t10.0\n2014-03-20 06:45\t10.0\n2014-03-20 07:00\t10.0\n2014-03-20 07:15\t10.0\n2014-03-20 07:30\t10.0\n2014-03-20 07:45\t10.0\n2014-03-20 08:00\t10.0\n2014-03-20 08:15\t10.0\n2014-03-20 08:30\t10.0\n2014-03-20 08:45\t10.0\n2014-03-20 09:00\t10.0\n2014-03-20 09:15\t10.0\n2014-03-20 09:30\t10.0\n2014-03-20 09:45\t10.0\n2014-03-20 10:00\t10.0\n2014-03-20 10:15\t10.0\n2014-03-20 10:30\t10.0\n2014-03-20 10:45\t10.0\n2014-03-20 11:00\t10.0\n2014-03-20 11:15\t10.0\n2014-03-20 11:30\t10.0\n2014-03-20 11:45\t10.0\n2014-03-20 12:00\t10.0\n2014-03-20 12:15\t10.0\n2014-03-20 12:30\t10.0\n2014-03-20 12:45\t10.0\n2014-03-20 13:00\t10.0\n2014-03-20 13:15\t10.0\n2014-03-20 13:30\t10.0\n2014-03-20 13:45\t10.0\n2014-03-20 14:00\t20.0\n2014-03-20 14:15\t20.0\n2014-03-20 14:30\t20.0\n2014-03-20 14:45\t20.0\n2014-03-20 15:00\t20.0\n2014-03-20 15:15\t20.0\n2014-03-20 15:30\t20.0\n2014-03-20 15:45\t20.0\n2014-03-20 16:00\t20.0\n2014-03-20 16:15\t20.0\n2014-03-20 16:30\t20.0\n2014-03-20 16:45\t20.0\n2014-03-20 17:00\t20.0\n2014-03-20 17:15\t20.0\n2014-03-20 17:30\t20.0\n2014-03-20 17:45\t20.0\n2014-03-20 18:00\t20.0\n2014-03-20 18:15\t20.0\n2014-03-20 18:30\t20.0\n2014-03-20 18:45\t20.0\n2014-03-20 19:00\t20.0\n2014-03-20 19:15\t20.0\n2014-03-20 19:30\t20.0\n2014-03-20 19:45\t20.0\n2014-03-20 20:00\t20.0\n2014-03-20 20:15\t20.0\n2014-03-20 20:30\t20.0\n2014-03-20 20:45\t20.0\n2014-03-20 21:00\t20.0\n2014-03-20 21:15\t20.0\n2014-03-20 21:30\t20.0\n2014-03-20 21:45\t20.0\n2014-03-20 22:00\t20.0\n2014-03-20 22:15\t20.0\n2014-03-20 22:30\t20.0\n2014-03-20 22:45\t20.0\n2014-03-20 23:00\t20.0\n2014-03-20 23:15\t20.0\n2014-03-20 23:30\t20.0\n2014-03-20 23:45\t20.0\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
		Assert.assertThat(
				out2.toString("UTF-8"),
				IsEqual.equalTo("Date\tTotal Consumption\n2014-03-20 00:00\t20.0\n2014-03-20 00:15\t20.0\n2014-03-20 00:30\t20.0\n2014-03-20 00:45\t20.0\n2014-03-20 01:00\t20.0\n2014-03-20 01:15\t20.0\n2014-03-20 01:30\t20.0\n2014-03-20 01:45\t20.0\n2014-03-20 02:00\t20.0\n2014-03-20 02:15\t20.0\n2014-03-20 02:30\t20.0\n2014-03-20 02:45\t20.0\n2014-03-20 03:00\t10.0\n2014-03-20 03:15\t10.0\n2014-03-20 03:30\t10.0\n2014-03-20 03:45\t10.0\n2014-03-20 04:00\t10.0\n2014-03-20 04:15\t10.0\n2014-03-20 04:30\t10.0\n2014-03-20 04:45\t10.0\n2014-03-20 05:00\t10.0\n2014-03-20 05:15\t10.0\n2014-03-20 05:30\t10.0\n2014-03-20 05:45\t10.0\n2014-03-20 06:00\t10.0\n2014-03-20 06:15\t10.0\n2014-03-20 06:30\t10.0\n2014-03-20 06:45\t10.0\n2014-03-20 07:00\t10.0\n2014-03-20 07:15\t10.0\n2014-03-20 07:30\t10.0\n2014-03-20 07:45\t10.0\n2014-03-20 08:00\t10.0\n2014-03-20 08:15\t10.0\n2014-03-20 08:30\t10.0\n2014-03-20 08:45\t10.0\n2014-03-20 09:00\t10.0\n2014-03-20 09:15\t10.0\n2014-03-20 09:30\t10.0\n2014-03-20 09:45\t10.0\n2014-03-20 10:00\t10.0\n2014-03-20 10:15\t10.0\n2014-03-20 10:30\t10.0\n2014-03-20 10:45\t10.0\n2014-03-20 11:00\t10.0\n2014-03-20 11:15\t10.0\n2014-03-20 11:30\t10.0\n2014-03-20 11:45\t10.0\n2014-03-20 12:00\t10.0\n2014-03-20 12:15\t10.0\n2014-03-20 12:30\t10.0\n2014-03-20 12:45\t10.0\n2014-03-20 13:00\t10.0\n2014-03-20 13:15\t10.0\n2014-03-20 13:30\t10.0\n2014-03-20 13:45\t10.0\n2014-03-20 14:00\t0.0\n2014-03-20 14:15\t0.0\n2014-03-20 14:30\t0.0\n2014-03-20 14:45\t0.0\n2014-03-20 15:00\t0.0\n2014-03-20 15:15\t0.0\n2014-03-20 15:30\t0.0\n2014-03-20 15:45\t0.0\n2014-03-20 16:00\t0.0\n2014-03-20 16:15\t0.0\n2014-03-20 16:30\t0.0\n2014-03-20 16:45\t0.0\n2014-03-20 17:00\t0.0\n2014-03-20 17:15\t0.0\n2014-03-20 17:30\t0.0\n2014-03-20 17:45\t0.0\n2014-03-20 18:00\t0.0\n2014-03-20 18:15\t0.0\n2014-03-20 18:30\t0.0\n2014-03-20 18:45\t0.0\n2014-03-20 19:00\t0.0\n2014-03-20 19:15\t0.0\n2014-03-20 19:30\t0.0\n2014-03-20 19:45\t0.0\n2014-03-20 20:00\t0.0\n2014-03-20 20:15\t0.0\n2014-03-20 20:30\t0.0\n2014-03-20 20:45\t0.0\n2014-03-20 21:00\t0.0\n2014-03-20 21:15\t0.0\n2014-03-20 21:30\t0.0\n2014-03-20 21:45\t0.0\n2014-03-20 22:00\t0.0\n2014-03-20 22:15\t0.0\n2014-03-20 22:30\t0.0\n2014-03-20 22:45\t0.0\n2014-03-20 23:00\t0.0\n2014-03-20 23:15\t0.0\n2014-03-20 23:30\t0.0\n2014-03-20 23:45\t0.0\n"
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
				IsEqual.equalTo("Date\tTotal Consumption\n2014-03-20 00:00\t0.0\n2014-03-20 00:15\t0.0\n2014-03-20 00:30\t0.0\n2014-03-20 00:45\t0.0\n2014-03-20 01:00\t0.0\n2014-03-20 01:15\t0.0\n2014-03-20 01:30\t0.0\n2014-03-20 01:45\t0.0\n2014-03-20 02:00\t0.0\n2014-03-20 02:15\t0.0\n2014-03-20 02:30\t0.0\n2014-03-20 02:45\t0.0\n2014-03-20 03:00\t10.0\n2014-03-20 03:15\t10.0\n2014-03-20 03:30\t10.0\n2014-03-20 03:45\t10.0\n2014-03-20 04:00\t10.0\n2014-03-20 04:15\t10.0\n2014-03-20 04:30\t10.0\n2014-03-20 04:45\t10.0\n2014-03-20 05:00\t10.0\n2014-03-20 05:15\t10.0\n2014-03-20 05:30\t10.0\n2014-03-20 05:45\t10.0\n2014-03-20 06:00\t10.0\n2014-03-20 06:15\t10.0\n2014-03-20 06:30\t10.0\n2014-03-20 06:45\t10.0\n2014-03-20 07:00\t10.0\n2014-03-20 07:15\t10.0\n2014-03-20 07:30\t10.0\n2014-03-20 07:45\t10.0\n2014-03-20 08:00\t10.0\n2014-03-20 08:15\t10.0\n2014-03-20 08:30\t10.0\n2014-03-20 08:45\t10.0\n2014-03-20 09:00\t10.0\n2014-03-20 09:15\t10.0\n2014-03-20 09:30\t10.0\n2014-03-20 09:45\t10.0\n2014-03-20 10:00\t10.0\n2014-03-20 10:15\t10.0\n2014-03-20 10:30\t10.0\n2014-03-20 10:45\t10.0\n2014-03-20 11:00\t10.0\n2014-03-20 11:15\t10.0\n2014-03-20 11:30\t10.0\n2014-03-20 11:45\t10.0\n2014-03-20 12:00\t10.0\n2014-03-20 12:15\t10.0\n2014-03-20 12:30\t10.0\n2014-03-20 12:45\t10.0\n2014-03-20 13:00\t10.0\n2014-03-20 13:15\t10.0\n2014-03-20 13:30\t10.0\n2014-03-20 13:45\t10.0\n2014-03-20 14:00\t20.0\n2014-03-20 14:15\t20.0\n2014-03-20 14:30\t20.0\n2014-03-20 14:45\t20.0\n2014-03-20 15:00\t20.0\n2014-03-20 15:15\t20.0\n2014-03-20 15:30\t20.0\n2014-03-20 15:45\t20.0\n2014-03-20 16:00\t20.0\n2014-03-20 16:15\t20.0\n2014-03-20 16:30\t20.0\n2014-03-20 16:45\t20.0\n2014-03-20 17:00\t20.0\n2014-03-20 17:15\t20.0\n2014-03-20 17:30\t20.0\n2014-03-20 17:45\t20.0\n2014-03-20 18:00\t20.0\n2014-03-20 18:15\t20.0\n2014-03-20 18:30\t20.0\n2014-03-20 18:45\t20.0\n2014-03-20 19:00\t20.0\n2014-03-20 19:15\t20.0\n2014-03-20 19:30\t20.0\n2014-03-20 19:45\t20.0\n2014-03-20 20:00\t20.0\n2014-03-20 20:15\t20.0\n2014-03-20 20:30\t20.0\n2014-03-20 20:45\t20.0\n2014-03-20 21:00\t20.0\n2014-03-20 21:15\t20.0\n2014-03-20 21:30\t20.0\n2014-03-20 21:45\t20.0\n2014-03-20 22:00\t20.0\n2014-03-20 22:15\t20.0\n2014-03-20 22:30\t20.0\n2014-03-20 22:45\t20.0\n2014-03-20 23:00\t20.0\n2014-03-20 23:15\t20.0\n2014-03-20 23:30\t20.0\n2014-03-20 23:45\t20.0\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
		Assert.assertThat(
				out2.toString("UTF-8"),
				IsEqual.equalTo("Date\tTotal Consumption\n2014-03-20 00:00\t20.0\n2014-03-20 00:15\t20.0\n2014-03-20 00:30\t20.0\n2014-03-20 00:45\t20.0\n2014-03-20 01:00\t20.0\n2014-03-20 01:15\t20.0\n2014-03-20 01:30\t20.0\n2014-03-20 01:45\t20.0\n2014-03-20 02:00\t20.0\n2014-03-20 02:15\t20.0\n2014-03-20 02:30\t20.0\n2014-03-20 02:45\t20.0\n2014-03-20 03:00\t10.0\n2014-03-20 03:15\t10.0\n2014-03-20 03:30\t10.0\n2014-03-20 03:45\t10.0\n2014-03-20 04:00\t10.0\n2014-03-20 04:15\t10.0\n2014-03-20 04:30\t10.0\n2014-03-20 04:45\t10.0\n2014-03-20 05:00\t10.0\n2014-03-20 05:15\t10.0\n2014-03-20 05:30\t10.0\n2014-03-20 05:45\t10.0\n2014-03-20 06:00\t10.0\n2014-03-20 06:15\t10.0\n2014-03-20 06:30\t10.0\n2014-03-20 06:45\t10.0\n2014-03-20 07:00\t10.0\n2014-03-20 07:15\t10.0\n2014-03-20 07:30\t10.0\n2014-03-20 07:45\t10.0\n2014-03-20 08:00\t10.0\n2014-03-20 08:15\t10.0\n2014-03-20 08:30\t10.0\n2014-03-20 08:45\t10.0\n2014-03-20 09:00\t10.0\n2014-03-20 09:15\t10.0\n2014-03-20 09:30\t10.0\n2014-03-20 09:45\t10.0\n2014-03-20 10:00\t10.0\n2014-03-20 10:15\t10.0\n2014-03-20 10:30\t10.0\n2014-03-20 10:45\t10.0\n2014-03-20 11:00\t10.0\n2014-03-20 11:15\t10.0\n2014-03-20 11:30\t10.0\n2014-03-20 11:45\t10.0\n2014-03-20 12:00\t10.0\n2014-03-20 12:15\t10.0\n2014-03-20 12:30\t10.0\n2014-03-20 12:45\t10.0\n2014-03-20 13:00\t10.0\n2014-03-20 13:15\t10.0\n2014-03-20 13:30\t10.0\n2014-03-20 13:45\t10.0\n2014-03-20 14:00\t0.0\n2014-03-20 14:15\t0.0\n2014-03-20 14:30\t0.0\n2014-03-20 14:45\t0.0\n2014-03-20 15:00\t0.0\n2014-03-20 15:15\t0.0\n2014-03-20 15:30\t0.0\n2014-03-20 15:45\t0.0\n2014-03-20 16:00\t0.0\n2014-03-20 16:15\t0.0\n2014-03-20 16:30\t0.0\n2014-03-20 16:45\t0.0\n2014-03-20 17:00\t0.0\n2014-03-20 17:15\t0.0\n2014-03-20 17:30\t0.0\n2014-03-20 17:45\t0.0\n2014-03-20 18:00\t0.0\n2014-03-20 18:15\t0.0\n2014-03-20 18:30\t0.0\n2014-03-20 18:45\t0.0\n2014-03-20 19:00\t0.0\n2014-03-20 19:15\t0.0\n2014-03-20 19:30\t0.0\n2014-03-20 19:45\t0.0\n2014-03-20 20:00\t0.0\n2014-03-20 20:15\t0.0\n2014-03-20 20:30\t0.0\n2014-03-20 20:45\t0.0\n2014-03-20 21:00\t0.0\n2014-03-20 21:15\t0.0\n2014-03-20 21:30\t0.0\n2014-03-20 21:45\t0.0\n2014-03-20 22:00\t0.0\n2014-03-20 22:15\t0.0\n2014-03-20 22:30\t0.0\n2014-03-20 22:45\t0.0\n2014-03-20 23:00\t0.0\n2014-03-20 23:15\t0.0\n2014-03-20 23:30\t0.0\n2014-03-20 23:45\t0.0\n"
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
				IsEqual.equalTo("Date\tTotal Consumption\tmydevice-name2\n2014-03-20 00:00\t0.0\t20.0\n2014-03-20 00:15\t0.0\t20.0\n2014-03-20 00:30\t0.0\t20.0\n2014-03-20 00:45\t0.0\t20.0\n2014-03-20 01:00\t0.0\t20.0\n2014-03-20 01:15\t0.0\t20.0\n2014-03-20 01:30\t0.0\t20.0\n2014-03-20 01:45\t0.0\t20.0\n2014-03-20 02:00\t0.0\t20.0\n2014-03-20 02:15\t0.0\t20.0\n2014-03-20 02:30\t0.0\t20.0\n2014-03-20 02:45\t0.0\t20.0\n2014-03-20 03:00\t10.0\t10.0\n2014-03-20 03:15\t10.0\t10.0\n2014-03-20 03:30\t10.0\t10.0\n2014-03-20 03:45\t10.0\t10.0\n2014-03-20 04:00\t10.0\t10.0\n2014-03-20 04:15\t10.0\t10.0\n2014-03-20 04:30\t10.0\t10.0\n2014-03-20 04:45\t10.0\t10.0\n2014-03-20 05:00\t10.0\t10.0\n2014-03-20 05:15\t10.0\t10.0\n2014-03-20 05:30\t10.0\t10.0\n2014-03-20 05:45\t10.0\t10.0\n2014-03-20 06:00\t10.0\t10.0\n2014-03-20 06:15\t10.0\t10.0\n2014-03-20 06:30\t10.0\t10.0\n2014-03-20 06:45\t10.0\t10.0\n2014-03-20 07:00\t10.0\t10.0\n2014-03-20 07:15\t10.0\t10.0\n2014-03-20 07:30\t10.0\t10.0\n2014-03-20 07:45\t10.0\t10.0\n2014-03-20 08:00\t10.0\t10.0\n2014-03-20 08:15\t10.0\t10.0\n2014-03-20 08:30\t10.0\t10.0\n2014-03-20 08:45\t10.0\t10.0\n2014-03-20 09:00\t10.0\t10.0\n2014-03-20 09:15\t10.0\t10.0\n2014-03-20 09:30\t10.0\t10.0\n2014-03-20 09:45\t10.0\t10.0\n2014-03-20 10:00\t10.0\t10.0\n2014-03-20 10:15\t10.0\t10.0\n2014-03-20 10:30\t10.0\t10.0\n2014-03-20 10:45\t10.0\t10.0\n2014-03-20 11:00\t10.0\t10.0\n2014-03-20 11:15\t10.0\t10.0\n2014-03-20 11:30\t10.0\t10.0\n2014-03-20 11:45\t10.0\t10.0\n2014-03-20 12:00\t10.0\t10.0\n2014-03-20 12:15\t10.0\t10.0\n2014-03-20 12:30\t10.0\t10.0\n2014-03-20 12:45\t10.0\t10.0\n2014-03-20 13:00\t10.0\t10.0\n2014-03-20 13:15\t10.0\t10.0\n2014-03-20 13:30\t10.0\t10.0\n2014-03-20 13:45\t10.0\t10.0\n2014-03-20 14:00\t20.0\t0.0\n2014-03-20 14:15\t20.0\t0.0\n2014-03-20 14:30\t20.0\t0.0\n2014-03-20 14:45\t20.0\t0.0\n2014-03-20 15:00\t20.0\t0.0\n2014-03-20 15:15\t20.0\t0.0\n2014-03-20 15:30\t20.0\t0.0\n2014-03-20 15:45\t20.0\t0.0\n2014-03-20 16:00\t20.0\t0.0\n2014-03-20 16:15\t20.0\t0.0\n2014-03-20 16:30\t20.0\t0.0\n2014-03-20 16:45\t20.0\t0.0\n2014-03-20 17:00\t20.0\t0.0\n2014-03-20 17:15\t20.0\t0.0\n2014-03-20 17:30\t20.0\t0.0\n2014-03-20 17:45\t20.0\t0.0\n2014-03-20 18:00\t20.0\t0.0\n2014-03-20 18:15\t20.0\t0.0\n2014-03-20 18:30\t20.0\t0.0\n2014-03-20 18:45\t20.0\t0.0\n2014-03-20 19:00\t20.0\t0.0\n2014-03-20 19:15\t20.0\t0.0\n2014-03-20 19:30\t20.0\t0.0\n2014-03-20 19:45\t20.0\t0.0\n2014-03-20 20:00\t20.0\t0.0\n2014-03-20 20:15\t20.0\t0.0\n2014-03-20 20:30\t20.0\t0.0\n2014-03-20 20:45\t20.0\t0.0\n2014-03-20 21:00\t20.0\t0.0\n2014-03-20 21:15\t20.0\t0.0\n2014-03-20 21:30\t20.0\t0.0\n2014-03-20 21:45\t20.0\t0.0\n2014-03-20 22:00\t20.0\t0.0\n2014-03-20 22:15\t20.0\t0.0\n2014-03-20 22:30\t20.0\t0.0\n2014-03-20 22:45\t20.0\t0.0\n2014-03-20 23:00\t20.0\t0.0\n2014-03-20 23:15\t20.0\t0.0\n2014-03-20 23:30\t20.0\t0.0\n2014-03-20 23:45\t20.0\t0.0\n"
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
				IsEqual.equalTo("Date\tTotal Consumption\tmydevice-name\tmydevice-name2\n2014-03-20 00:00\t0.0\t20.0\t0.0\n2014-03-20 00:15\t0.0\t20.0\t0.0\n2014-03-20 00:30\t0.0\t20.0\t0.0\n2014-03-20 00:45\t0.0\t20.0\t0.0\n2014-03-20 01:00\t0.0\t20.0\t0.0\n2014-03-20 01:15\t0.0\t20.0\t0.0\n2014-03-20 01:30\t0.0\t20.0\t0.0\n2014-03-20 01:45\t0.0\t20.0\t0.0\n2014-03-20 02:00\t0.0\t20.0\t0.0\n2014-03-20 02:15\t0.0\t20.0\t0.0\n2014-03-20 02:30\t0.0\t20.0\t0.0\n2014-03-20 02:45\t0.0\t20.0\t0.0\n2014-03-20 03:00\t10.0\t10.0\t10.0\n2014-03-20 03:15\t10.0\t10.0\t10.0\n2014-03-20 03:30\t10.0\t10.0\t10.0\n2014-03-20 03:45\t10.0\t10.0\t10.0\n2014-03-20 04:00\t10.0\t10.0\t10.0\n2014-03-20 04:15\t10.0\t10.0\t10.0\n2014-03-20 04:30\t10.0\t10.0\t10.0\n2014-03-20 04:45\t10.0\t10.0\t10.0\n2014-03-20 05:00\t10.0\t10.0\t10.0\n2014-03-20 05:15\t10.0\t10.0\t10.0\n2014-03-20 05:30\t10.0\t10.0\t10.0\n2014-03-20 05:45\t10.0\t10.0\t10.0\n2014-03-20 06:00\t10.0\t10.0\t10.0\n2014-03-20 06:15\t10.0\t10.0\t10.0\n2014-03-20 06:30\t10.0\t10.0\t10.0\n2014-03-20 06:45\t10.0\t10.0\t10.0\n2014-03-20 07:00\t10.0\t10.0\t10.0\n2014-03-20 07:15\t10.0\t10.0\t10.0\n2014-03-20 07:30\t10.0\t10.0\t10.0\n2014-03-20 07:45\t10.0\t10.0\t10.0\n2014-03-20 08:00\t10.0\t10.0\t10.0\n2014-03-20 08:15\t10.0\t10.0\t10.0\n2014-03-20 08:30\t10.0\t10.0\t10.0\n2014-03-20 08:45\t10.0\t10.0\t10.0\n2014-03-20 09:00\t10.0\t10.0\t10.0\n2014-03-20 09:15\t10.0\t10.0\t10.0\n2014-03-20 09:30\t10.0\t10.0\t10.0\n2014-03-20 09:45\t10.0\t10.0\t10.0\n2014-03-20 10:00\t10.0\t10.0\t10.0\n2014-03-20 10:15\t10.0\t10.0\t10.0\n2014-03-20 10:30\t10.0\t10.0\t10.0\n2014-03-20 10:45\t10.0\t10.0\t10.0\n2014-03-20 11:00\t10.0\t10.0\t10.0\n2014-03-20 11:15\t10.0\t10.0\t10.0\n2014-03-20 11:30\t10.0\t10.0\t10.0\n2014-03-20 11:45\t10.0\t10.0\t10.0\n2014-03-20 12:00\t10.0\t10.0\t10.0\n2014-03-20 12:15\t10.0\t10.0\t10.0\n2014-03-20 12:30\t10.0\t10.0\t10.0\n2014-03-20 12:45\t10.0\t10.0\t10.0\n2014-03-20 13:00\t10.0\t10.0\t10.0\n2014-03-20 13:15\t10.0\t10.0\t10.0\n2014-03-20 13:30\t10.0\t10.0\t10.0\n2014-03-20 13:45\t10.0\t10.0\t10.0\n2014-03-20 14:00\t20.0\t0.0\t20.0\n2014-03-20 14:15\t20.0\t0.0\t20.0\n2014-03-20 14:30\t20.0\t0.0\t20.0\n2014-03-20 14:45\t20.0\t0.0\t20.0\n2014-03-20 15:00\t20.0\t0.0\t20.0\n2014-03-20 15:15\t20.0\t0.0\t20.0\n2014-03-20 15:30\t20.0\t0.0\t20.0\n2014-03-20 15:45\t20.0\t0.0\t20.0\n2014-03-20 16:00\t20.0\t0.0\t20.0\n2014-03-20 16:15\t20.0\t0.0\t20.0\n2014-03-20 16:30\t20.0\t0.0\t20.0\n2014-03-20 16:45\t20.0\t0.0\t20.0\n2014-03-20 17:00\t20.0\t0.0\t20.0\n2014-03-20 17:15\t20.0\t0.0\t20.0\n2014-03-20 17:30\t20.0\t0.0\t20.0\n2014-03-20 17:45\t20.0\t0.0\t20.0\n2014-03-20 18:00\t20.0\t0.0\t20.0\n2014-03-20 18:15\t20.0\t0.0\t20.0\n2014-03-20 18:30\t20.0\t0.0\t20.0\n2014-03-20 18:45\t20.0\t0.0\t20.0\n2014-03-20 19:00\t20.0\t0.0\t20.0\n2014-03-20 19:15\t20.0\t0.0\t20.0\n2014-03-20 19:30\t20.0\t0.0\t20.0\n2014-03-20 19:45\t20.0\t0.0\t20.0\n2014-03-20 20:00\t20.0\t0.0\t20.0\n2014-03-20 20:15\t20.0\t0.0\t20.0\n2014-03-20 20:30\t20.0\t0.0\t20.0\n2014-03-20 20:45\t20.0\t0.0\t20.0\n2014-03-20 21:00\t20.0\t0.0\t20.0\n2014-03-20 21:15\t20.0\t0.0\t20.0\n2014-03-20 21:30\t20.0\t0.0\t20.0\n2014-03-20 21:45\t20.0\t0.0\t20.0\n2014-03-20 22:00\t20.0\t0.0\t20.0\n2014-03-20 22:15\t20.0\t0.0\t20.0\n2014-03-20 22:30\t20.0\t0.0\t20.0\n2014-03-20 22:45\t20.0\t0.0\t20.0\n2014-03-20 23:00\t20.0\t0.0\t20.0\n2014-03-20 23:15\t20.0\t0.0\t20.0\n2014-03-20 23:30\t20.0\t0.0\t20.0\n2014-03-20 23:45\t20.0\t0.0\t20.0\n"
						.replaceAll("2014-03-20",
								referenceDateTime.toString("yyyy-MM-dd"))));
	}

}
