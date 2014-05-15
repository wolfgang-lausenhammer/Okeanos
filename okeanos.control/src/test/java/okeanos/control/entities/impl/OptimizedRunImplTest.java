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
 * The Class OptimizedRunImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class OptimizedRunImplTest {

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		Slot slot1 = new SlotImpl("my-slot-id-1");
		Slot slot2 = new SlotImpl("my-slot-id-2");
		Slot slot3 = new SlotImpl("my-slot-id-3");

		DateTime startTime1 = DateTime.parse("2014-03-20T00:00:00-04:00");
		DateTime startTime2 = DateTime.parse("2014-03-21T12:00:00-04:00");
		DateTime startTime3 = DateTime.parse("2014-03-22T20:30:00-04:00");

		return Arrays.asList(new Object[][] {
				{ "my-optimized-run-id-1", LoadType.LOAD, Arrays.asList(slot1),
						startTime1 },
				{ "my-optimized-run-id-2", LoadType.LOAD,
						Arrays.asList(slot1, slot2), startTime2 },
				{ "my-optimized-run-id-3", LoadType.LOAD,
						Arrays.asList(slot1, slot2, slot3), startTime3 },
				{ "my-optimized-run-id-4", LoadType.LOAD, Arrays.asList(slot1),
						startTime1 },
				{ "my-optimized-run-id-5", LoadType.LOAD,
						Arrays.asList(slot1, slot2), startTime2 },
				{ "my-optimized-run-id-6", LoadType.LOAD,
						Arrays.asList(slot1, slot2, slot3), startTime3 },
				{ "my-optimized-run-id-7", LoadType.LOAD, null, startTime1 },
				{ "my-optimized-run-id-8", LoadType.LOAD, null, startTime2 },
				{ "my-optimized-run-id-9", LoadType.LOAD, null, startTime3 },
				{ "my-optimized-run-id-10", null, null, startTime1 },
				{ "my-optimized-run-id-11", LoadType.LOAD, null, null },
				{ "my-optimized-run-id-12", null, null, null },
				{ null, null, null, null }, });
	}

	/** The id. */
	private String id;

	/** The load type. */
	private LoadType loadType;

	/** The needed slots. */
	private List<Slot> neededSlots;

	/** The optimized run. */
	private OptimizedRunImpl optimizedRun;

	/** The start time. */
	private DateTime startTime;

	/**
	 * Instantiates a new optimized run impl test.
	 * 
	 * @param id
	 *            the id
	 * @param loadType
	 *            the load type
	 * @param neededSlots
	 *            the needed slots
	 * @param startTime
	 *            the start time
	 */
	public OptimizedRunImplTest(final String id, final LoadType loadType,
			final List<Slot> neededSlots, final DateTime startTime) {
		this.id = id;
		this.loadType = loadType;
		this.neededSlots = neededSlots;
		this.startTime = startTime;
	}

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.optimizedRun = new OptimizedRunImpl(id);
	}

	/**
	 * Test get id.
	 */
	@Test
	public void testGetId() {
		String id = optimizedRun.getId();

		assertThat(id, is(equalTo(this.id)));
	}

	/**
	 * Test get start time.
	 */
	@Test
	public void testGetStartTime() {
		optimizedRun.setStartTime(startTime);

		DateTime startTime = optimizedRun.getStartTime();

		assertThat(startTime, is(equalTo(this.startTime)));
	}

	/**
	 * Test needed slots.
	 */
	@Test
	public void testNeededSlots() {
		optimizedRun.setNeededSlots(neededSlots);

		List<Slot> neededSlots = optimizedRun.getNeededSlots();

		assertThat(neededSlots, is(equalTo(this.neededSlots)));
	}

}
