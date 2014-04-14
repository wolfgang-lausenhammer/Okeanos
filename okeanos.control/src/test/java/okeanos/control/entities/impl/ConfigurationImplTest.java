package okeanos.control.entities.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The Class ConfigurationImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class ConfigurationImplTest {

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		PossibleRun run1 = new PossibleRunImpl("my-possible-run-id-1");
		PossibleRun run2 = new PossibleRunImpl("my-possible-run-id-1");
		PossibleRun run3 = new PossibleRunImpl("my-possible-run-id-1");

		Schedule schedule1 = new ScheduleImpl("my-schedule-id-1");
		Schedule schedule2 = new ScheduleImpl("my-schedule-id-2");
		Schedule schedule3 = new ScheduleImpl("my-schedule-id-3");

		return Arrays
				.asList(new Object[][] {
						{ "my-configuration-id-1", Arrays.asList(run1),
								schedule1 },
						{ "my-configuration-id-2", Arrays.asList(run1, run2),
								schedule2 },
						{ "my-configuration-id-3",
								Arrays.asList(run1, run2, run3), schedule3 },
						{ "my-configuration-id-4", null, null },
						{ "my-configuration-id-5",
								Arrays.asList((PossibleRun) null), null },
						{ "my-configuration-id-6",
								Arrays.asList((PossibleRun) null), schedule1 },
						{ "my-configuration-id-7", null, schedule3 },
						{ null, null, schedule3 }, { null, null, null } });
	}

	/** The configuration. */
	private ConfigurationImpl configuration;

	/** The id. */
	private String id;

	/** The possible runs. */
	private List<PossibleRun> possibleRuns;

	/** The schedule of other devices. */
	private Schedule scheduleOfOtherDevices;

	/**
	 * Instantiates a new configuration impl test.
	 * 
	 * @param id
	 *            the id
	 * @param possibleRuns
	 *            the possible runs
	 * @param scheduleOfOtherDevices
	 *            the schedule of other devices
	 */
	public ConfigurationImplTest(final String id,
			final List<PossibleRun> possibleRuns,
			final Schedule scheduleOfOtherDevices) {
		this.id = id;
		this.possibleRuns = possibleRuns;
		this.scheduleOfOtherDevices = scheduleOfOtherDevices;
	}

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		configuration = new ConfigurationImpl(id);
	}

	/**
	 * Test get id.
	 */
	@Test
	public void testGetId() {
		String id = configuration.getId();

		assertThat(id, is(equalTo(this.id)));
	}

	/**
	 * Test possible runs.
	 */
	@Test
	public void testPossibleRuns() {
		configuration.setPossibleRun(possibleRuns);

		List<PossibleRun> possibleRuns = configuration.getPossibleRuns();

		assertThat(possibleRuns, is(equalTo(this.possibleRuns)));
	}

	/**
	 * Test schedule of other devices.
	 */
	@Test
	public void testScheduleOfOtherDevices() {
		configuration.setScheduleOfOtherDevices(scheduleOfOtherDevices);

		Schedule scheduleOfOtherDevices = configuration
				.getScheduleOfOtherDevices();

		assertThat(scheduleOfOtherDevices,
				is(equalTo(this.scheduleOfOtherDevices)));
	}

}
