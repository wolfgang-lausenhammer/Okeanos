package okeanos.control.entities.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import okeanos.control.entities.PossibleRunsConfiguration;
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
		PossibleRunsConfiguration run1 = new PossibleRunsConfigurationImpl(
				"my-possible-run-configuration-id-1");
		PossibleRunsConfiguration run2 = new PossibleRunsConfigurationImpl(
				"my-possible-run-configuration-id-2");
		PossibleRunsConfiguration run3 = new PossibleRunsConfigurationImpl(
				"my-possible-run-configuration-id-3");

		Schedule schedule1 = new ScheduleImpl("my-schedule-id-1");
		Schedule schedule2 = new ScheduleImpl("my-schedule-id-2");
		Schedule schedule3 = new ScheduleImpl("my-schedule-id-3");

		return Arrays.asList(new Object[][] {
				{ "my-configuration-id-1", run1, schedule1 },
				{ "my-configuration-id-2", run2, schedule2 },
				{ "my-configuration-id-3", run3, schedule3 },
				{ "my-configuration-id-4", null, null },
				{ "my-configuration-id-5", null, schedule1 },
				{ "my-configuration-id-6", null, schedule3 },
				{ null, null, schedule3 }, { null, null, null } });
	}

	/** The configuration. */
	private ConfigurationImpl configuration;

	/** The id. */
	private String id;

	/** The possible runs configuration. */
	private PossibleRunsConfiguration possibleRunsConfiguration;

	/** The schedule of other devices. */
	private Schedule scheduleOfOtherDevices;

	/**
	 * Instantiates a new configuration impl test.
	 * 
	 * @param id
	 *            the id
	 * @param possibleRunsConfiguration
	 *            the possible runs configuration
	 * @param scheduleOfOtherDevices
	 *            the schedule of other devices
	 */
	public ConfigurationImplTest(final String id,
			final PossibleRunsConfiguration possibleRunsConfiguration,
			final Schedule scheduleOfOtherDevices) {
		this.id = id;
		this.possibleRunsConfiguration = possibleRunsConfiguration;
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
		configuration.setPossibleRunsConfiguration(possibleRunsConfiguration);

		PossibleRunsConfiguration possibleRunsConfiguration = configuration
				.getPossibleRunsConfiguration();

		assertThat(possibleRunsConfiguration,
				is(equalTo(this.possibleRunsConfiguration)));
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
