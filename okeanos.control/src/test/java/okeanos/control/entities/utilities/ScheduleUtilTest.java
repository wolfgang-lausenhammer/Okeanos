package okeanos.control.entities.utilities;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.measure.quantity.Power;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.OptimizedRunImpl;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.impl.SlotImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;

import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;
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
 * The Class ScheduleUtilTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class ScheduleUtilTest {

	/** The Constant ZERO_WATT. */
	private static final Amount<Power> ZERO_WATT = Amount
			.valueOf(0, Power.UNIT);

	/** The Constant TEN_WATT. */
	private static final Amount<Power> TEN_WATT = Amount
			.valueOf(10, Power.UNIT);

	/** The Constant TWENTY_WATT. */
	private static final Amount<Power> TWENTY_WATT = Amount.valueOf(20,
			Power.UNIT);

	/** The control entities provider. */
	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The schedule util. */
	private ScheduleUtil scheduleUtil;

	/** The schedule1. */
	private Schedule schedule1;

	/** The schedule2. */
	private Schedule schedule2;

	/** The result plus. */
	private Schedule resultPlus;

	/** The result minus. */
	private Schedule resultMinus;

	/** The result compare. */
	private int resultCompare;

	/** The optimized runs. */
	private List<OptimizedRun> optimizedRuns;

	/** The result to schedule. */
	private Schedule resultToSchedule;

	/**
	 * Instantiates a new schedule util test.
	 * 
	 * @param schedule1
	 *            the schedule1
	 * @param schedule2
	 *            the schedule2
	 * @param resultPlus
	 *            the result plus
	 * @param resultMinus
	 *            the result minus
	 * @param resultCompare
	 *            the result compare
	 * @param optimizedRuns
	 *            the optimized runs
	 * @param resultToSchedule
	 *            the result to schedule
	 */
	public ScheduleUtilTest(final Schedule schedule1, final Schedule schedule2,
			final Schedule resultPlus, final Schedule resultMinus,
			final int resultCompare, final List<OptimizedRun> optimizedRuns,
			final Schedule resultToSchedule) {
		this.schedule1 = schedule1;
		this.schedule2 = schedule2;
		this.resultPlus = resultPlus;
		this.resultMinus = resultMinus;
		this.resultCompare = resultCompare;
		this.optimizedRuns = optimizedRuns;
		this.resultToSchedule = resultToSchedule;
	}

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		DateTime time1 = DateTime.parse("2014-03-20T00:00:00-04:00");
		DateTime time2 = DateTime.parse("2014-03-20T01:00:00-04:00");
		DateTime time3 = DateTime.parse("2014-03-20T02:00:00-04:00");
		Slot slot1 = new SlotImpl("my-slot-id-1");
		slot1.setLoad(ZERO_WATT);
		Slot slot2 = new SlotImpl("my-slot-id-2");
		slot2.setLoad(TEN_WATT);
		Slot slot3 = new SlotImpl("my-slot-id-3");
		slot3.setLoad(TWENTY_WATT);

		// /////////////// Run 1
		Schedule schedule11 = new ScheduleImpl("my-schedule-id-11");
		Map<DateTime, Slot> map1 = new ConcurrentSkipListMap<>();
		map1.put(time1, slot1);
		map1.put(time2, slot2);
		map1.put(time3, slot3);
		schedule11.setSchedule(map1);

		Schedule schedule12 = new ScheduleImpl("my-schedule-id-12");
		Map<DateTime, Slot> map2 = new ConcurrentSkipListMap<>(map1);
		schedule12.setSchedule(map2);

		Schedule resultPlus1 = new ScheduleImpl("my-schedule-result-plus-id-1");
		Slot resultPlus1Slot = new SlotImpl("resultPlus1Slot");
		resultPlus1Slot.setLoad(ZERO_WATT);
		Slot resultPlus2Slot = new SlotImpl("resultPlus2Slot");
		resultPlus2Slot.setLoad(TEN_WATT.times(2));
		Slot resultPlus3Slot = new SlotImpl("resultPlus3Slot");
		resultPlus3Slot.setLoad(TWENTY_WATT.times(2));
		Map<DateTime, Slot> mapResultPlus1 = new ConcurrentSkipListMap<>();
		mapResultPlus1.put(time1, resultPlus1Slot);
		mapResultPlus1.put(time2, resultPlus2Slot);
		mapResultPlus1.put(time3, resultPlus3Slot);
		resultPlus1.setSchedule(mapResultPlus1);

		Schedule resultMinus1 = new ScheduleImpl(
				"my-schedule-result-minus-id-1");
		Slot resultMinus1Slot = new SlotImpl("resultPlus1Slot");
		resultMinus1Slot.setLoad(ZERO_WATT);
		Slot resultMinus2Slot = new SlotImpl("resultPlus2Slot");
		resultMinus2Slot.setLoad(ZERO_WATT);
		Slot resultMinus3Slot = new SlotImpl("resultPlus3Slot");
		resultMinus3Slot.setLoad(ZERO_WATT);
		Map<DateTime, Slot> mapResultMinus1 = new ConcurrentSkipListMap<>();
		mapResultMinus1.put(time1, resultMinus1Slot);
		mapResultMinus1.put(time2, resultMinus2Slot);
		mapResultMinus1.put(time3, resultMinus3Slot);
		resultMinus1.setSchedule(mapResultMinus1);

		int resultCompare1 = 0;

		Schedule resultToSchedule1 = new ScheduleImpl(
				"my-schedule-result-to-schedule-compare-id-1");
		Map<DateTime, Slot> mapResultToSchedule1 = new ConcurrentSkipListMap<>();
		mapResultToSchedule1.put(time1, slot1);
		mapResultToSchedule1.put(time1.plusMinutes(15), slot2);
		mapResultToSchedule1.put(time1.plusMinutes(2 * 15), slot3);
		resultToSchedule1.setSchedule(mapResultToSchedule1);

		OptimizedRun optimizedRun1 = new OptimizedRunImpl(
				"my-optimized-run-id-1");
		optimizedRun1.setLoadType(LoadType.PRODUCER);
		optimizedRun1.setStartTime(time1);
		optimizedRun1.setNeededSlots(Arrays.asList(slot1, slot2, slot3));
		List<OptimizedRun> optimizedRuns1 = Arrays.asList(optimizedRun1);

		// /////////////// Run 2
		Schedule schedule21 = new ScheduleImpl("my-schedule-id-21");
		Map<DateTime, Slot> map21 = new ConcurrentSkipListMap<>();
		map21.put(time1, slot1);
		map21.put(time2, slot2);
		map21.put(time3, slot3);
		schedule21.setSchedule(map21);

		Schedule schedule22 = new ScheduleImpl("my-schedule-id-22");
		Map<DateTime, Slot> map22 = new ConcurrentSkipListMap<>();
		map22.put(time1, slot3);
		map22.put(time2, slot1);
		map22.put(time3, slot2);
		schedule22.setSchedule(map22);

		Schedule resultPlus2 = new ScheduleImpl("my-schedule-result-plus-id-2");
		Slot resultPlus21Slot = new SlotImpl("resultPlus21Slot");
		resultPlus21Slot.setLoad(TWENTY_WATT);
		Slot resultPlus22Slot = new SlotImpl("resultPlus22Slot");
		resultPlus22Slot.setLoad(TEN_WATT);
		Slot resultPlus23Slot = new SlotImpl("resultPlus23Slot");
		resultPlus23Slot.setLoad(TWENTY_WATT.plus(TEN_WATT));
		Map<DateTime, Slot> mapResultPlus2 = new ConcurrentSkipListMap<>();
		mapResultPlus2.put(time1, resultPlus21Slot);
		mapResultPlus2.put(time2, resultPlus22Slot);
		mapResultPlus2.put(time3, resultPlus23Slot);
		resultPlus2.setSchedule(mapResultPlus2);

		Schedule resultMinus2 = new ScheduleImpl(
				"my-schedule-result-minus-id-2");
		Slot resultMinus21Slot = new SlotImpl("resultPlus21Slot");
		resultMinus21Slot.setLoad(TWENTY_WATT.times(-1));
		Slot resultMinus22Slot = new SlotImpl("resultPlus22Slot");
		resultMinus22Slot.setLoad(TEN_WATT);
		Slot resultMinus23Slot = new SlotImpl("resultPlus23Slot");
		resultMinus23Slot.setLoad(TEN_WATT);
		Map<DateTime, Slot> mapResultMinus2 = new ConcurrentSkipListMap<>();
		mapResultMinus2.put(time1, resultMinus21Slot);
		mapResultMinus2.put(time2, resultMinus22Slot);
		mapResultMinus2.put(time3, resultMinus23Slot);
		resultMinus2.setSchedule(mapResultMinus2);

		int resultCompare2 = -1;

		Schedule resultToSchedule2 = new ScheduleImpl(
				"my-schedule-result-to-schedule-compare-id-1");
		Map<DateTime, Slot> mapResultToSchedule2 = new ConcurrentSkipListMap<>();
		mapResultToSchedule2.put(time1, slot3);
		mapResultToSchedule2.put(time1.plusMinutes(15), slot2);
		mapResultToSchedule2.put(time1.plusMinutes(2 * 15), slot1);
		resultToSchedule2.setSchedule(mapResultToSchedule2);

		OptimizedRun optimizedRun2 = new OptimizedRunImpl(
				"my-optimized-run-id-1");
		optimizedRun2.setLoadType(LoadType.PRODUCER);
		optimizedRun2.setStartTime(time1);
		optimizedRun2.setNeededSlots(Arrays.asList(slot3, slot2, slot1));
		List<OptimizedRun> optimizedRuns2 = Arrays.asList(optimizedRun2);

		return Arrays.asList(new Object[][] {
				{ schedule11, schedule12, resultPlus1, resultMinus1,
						resultCompare1, optimizedRuns1, resultToSchedule1 },
				{ schedule21, schedule22, resultPlus2, resultMinus2,
						resultCompare2, optimizedRuns2, resultToSchedule2 } });
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
	 * Test compare.
	 */
	@Test
	public void testCompare() {
		int result = scheduleUtil.compare(schedule1, schedule2);

		assertThat(result, is(equalTo(this.resultCompare)));
	}

	/**
	 * Test minus.
	 */
	@Test
	public void testMinus() {
		Schedule resultMinus = scheduleUtil.minus(schedule1, schedule2);

		assertThat(scheduleUtil.compare(resultMinus, this.resultMinus),
				is(equalTo(0)));
	}

	/**
	 * Test plus.
	 */
	@Test
	public void testPlus() {
		Schedule resultPlus = scheduleUtil.plus(schedule1, schedule2);

		assertThat(scheduleUtil.compare(resultPlus, this.resultPlus),
				is(equalTo(0)));
	}

	/**
	 * Test to schedule.
	 */
	@Test
	public void testToSchedule() {
		Schedule resultToSchedule = scheduleUtil.toSchedule(optimizedRuns);

		assertThat(
				scheduleUtil.compare(resultToSchedule, this.resultToSchedule),
				is(equalTo(0)));
	}

}
