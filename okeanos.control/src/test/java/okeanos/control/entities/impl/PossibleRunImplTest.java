package okeanos.control.entities.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.Slot;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The Class PossibleRunImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class PossibleRunImplTest {

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		DateTime time1 = DateTime.parse("2014-03-20T00:00:00-04:00");
		DateTime time2 = DateTime.parse("2014-03-21T12:00:00-04:00");
		DateTime time3 = DateTime.parse("2014-03-22T20:15:00-04:00");

		Slot slot1 = new SlotImpl("my-slot-id-1");
		Slot slot2 = new SlotImpl("my-slot-id-2");
		Slot slot3 = new SlotImpl("my-slot-id-3");

		return Arrays.asList(new Object[][] {
				{ "my-possible-run-id-1", time1, time3, LoadType.LOAD,
						Arrays.asList(slot1) },
				{ "my-possible-run-id-2", time2, time1, LoadType.LOAD,
						Arrays.asList(slot1, slot2) },
				{ "my-possible-run-id-3", time3, time2, LoadType.LOAD,
						Arrays.asList(slot1, slot2, slot3) },
				{ "my-possible-run-id-4", time1, time3, LoadType.LOAD,
						Arrays.asList(slot1) },
				{ "my-possible-run-id-5", time2, time1, LoadType.LOAD,
						Arrays.asList(slot1, slot2) },
				{ "my-possible-run-id-6", time3, time2, LoadType.LOAD,
						Arrays.asList(slot1, slot2, slot3) },
				{ "my-possible-run-id-7", time1, time1, LoadType.LOAD,
						Arrays.asList((Slot) null) },
				{ "my-possible-run-id-7", time1, time1, LoadType.LOAD, null },
				{ "my-possible-run-id-8", time1, time1, null, null },
				{ "my-possible-run-id-9", time1, null, null, null },
				{ "my-possible-run-id-10", null, null, null, null }, });
	}

	/** The earliest start time. */
	private DateTime earliestStartTime;

	/** The id. */
	private String id;

	/** The latest end time. */
	private DateTime latestEndTime;

	/** The needed slots. */
	private List<Slot> neededSlots;

	/** The possible run. */
	private PossibleRunImpl possibleRun;

	/**
	 * Instantiates a new possible run impl test.
	 * 
	 * @param id
	 *            the id
	 * @param earliestStartTime
	 *            the earliest start time
	 * @param latestEndTime
	 *            the latest end time
	 * @param loadType
	 *            the load type
	 * @param neededSlots
	 *            the needed slots
	 */
	public PossibleRunImplTest(final String id,
			final DateTime earliestStartTime, final DateTime latestEndTime,
			final LoadType loadType, final List<Slot> neededSlots) {
		this.id = id;
		this.earliestStartTime = earliestStartTime;
		this.latestEndTime = latestEndTime;
		this.neededSlots = neededSlots;
	}

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.possibleRun = new PossibleRunImpl(id);
	}

	/**
	 * Test earliest start time.
	 */
	@Test
	public void testEarliestStartTime() {
		possibleRun.setEarliestStartTime(earliestStartTime);

		DateTime earliestStartTime = possibleRun.getEarliestStartTime();

		assertThat(earliestStartTime, is(equalTo(this.earliestStartTime)));
	}

	/**
	 * Test get id.
	 */
	@Test
	public void testGetId() {
		String id = possibleRun.getId();

		assertThat(id, is(equalTo(this.id)));
	}

	/**
	 * Test latest end time.
	 */
	@Test
	public void testLatestEndTime() {
		possibleRun.setLatestEndTime(latestEndTime);

		DateTime latestEndTime = possibleRun.getLatestEndTime();

		assertThat(latestEndTime, is(equalTo(this.latestEndTime)));
	}

	/**
	 * Test needed slots.
	 */
	@Test
	public void testNeededSlots() {
		possibleRun.setNeededSlots(neededSlots);

		List<Slot> neededSlots = possibleRun.getNeededSlots();

		assertThat(neededSlots, is(equalTo(this.neededSlots)));
	}

}
