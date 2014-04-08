package okeanos.control.entities.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import okeanos.control.entities.Slot;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The Class ScheduleImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class ScheduleImplTest {

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		Map<DateTime, Slot> scheduleContent1 = new HashMap<>();
		scheduleContent1.put(DateTime.parse("2014-03-20T00:00:00-04:00"),
				new SlotImpl("my-slot-id-1"));

		Map<DateTime, Slot> scheduleContent2 = new HashMap<>();
		scheduleContent1.put(DateTime.parse("2014-03-20T00:00:00-04:00"),
				new SlotImpl("my-slot-id-1"));
		scheduleContent1.put(DateTime.parse("2014-03-21T12:00:00-04:00"),
				new SlotImpl("my-slot-id-2"));

		Map<DateTime, Slot> scheduleContent3 = new HashMap<>();
		scheduleContent1.put(DateTime.parse("2014-03-20T00:00:00-04:00"),
				new SlotImpl("my-slot-id-1"));
		scheduleContent1.put(DateTime.parse("2014-03-21T12:00:00-04:00"),
				new SlotImpl("my-slot-id-2"));
		scheduleContent1.put(DateTime.parse("2014-03-22T20:20:00-04:00"),
				new SlotImpl("my-slot-id-3"));

		return Arrays.asList(new Object[][] {
				{ "my-schedule-id-1", scheduleContent1 },
				{ "my-schedule-id-2", scheduleContent2 },
				{ "my-schedule-id-3", scheduleContent3 },
				{ "my-schedule-id-4", new HashMap<>() },
				{ "my-schedule-id-5", null }, { null, null }, });
	}

	/** The id. */
	private String id;

	/** The schedule. */
	private ScheduleImpl schedule;

	/** The schedule content. */
	private Map<DateTime, Slot> scheduleContent;

	/**
	 * Instantiates a new schedule impl test.
	 * 
	 * @param id
	 *            the id
	 * @param scheduleContent
	 *            the schedule content
	 */
	public ScheduleImplTest(final String id,
			final Map<DateTime, Slot> scheduleContent) {
		this.id = id;
		this.scheduleContent = scheduleContent;
	}

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.schedule = new ScheduleImpl(id);
	}

	/**
	 * Test get id.
	 */
	@Test
	public void testGetId() {
		String id = schedule.getId();

		assertThat(id, is(equalTo(this.id)));
	}

	/**
	 * Test get schedule.
	 */
	@Test
	public void testSchedule() {
		schedule.setSchedule(scheduleContent);

		Map<DateTime, Slot> scheduleContent = schedule.getSchedule();

		assertThat(scheduleContent, is(equalTo(this.scheduleContent)));
	}

}
