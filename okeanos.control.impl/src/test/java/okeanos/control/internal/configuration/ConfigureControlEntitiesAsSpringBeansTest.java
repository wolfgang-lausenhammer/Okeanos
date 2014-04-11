package okeanos.control.internal.configuration;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.data.services.UUIDGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * The Class ConfigureControlEntitiesAsSpringBeansTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class ConfigureControlEntitiesAsSpringBeansTest {

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "my-uuid-1" }, { "my-uuid-2" },
				{ null } });
	}

	/** The uuid. */
	private String uuid;

	/**
	 * Instantiates a new configure control entities as spring beans test.
	 * 
	 * @param uuid
	 *            the uuid
	 */
	public ConfigureControlEntitiesAsSpringBeansTest(final String uuid) {
		this.uuid = uuid;
	}

	/** The uuid generator. */
	@Mock
	private UUIDGenerator uuidGenerator;

	/** The configure control entities as spring beans. */
	private ConfigureControlEntitiesAsSpringBeans configureControlEntitiesAsSpringBeans;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Mockito.when(uuidGenerator.generateUUID()).thenReturn(uuid);

		configureControlEntitiesAsSpringBeans = new ConfigureControlEntitiesAsSpringBeans();
		configureControlEntitiesAsSpringBeans.setUuidGenerator(uuidGenerator);
	}

	/**
	 * Test configuration.
	 */
	@Test
	public void testConfiguration() {
		Configuration configuration = configureControlEntitiesAsSpringBeans
				.configuration();

		assertThat(configuration, is(notNullValue()));
		assertThat(configuration.getId(), is(equalTo(uuid)));
	}

	/**
	 * Test optimized run.
	 */
	@Test
	public void testOptimizedRun() {
		OptimizedRun optimizedRun = configureControlEntitiesAsSpringBeans
				.optimizedRun();

		assertThat(optimizedRun, is(notNullValue()));
		assertThat(optimizedRun.getId(), is(equalTo(uuid)));
	}

	/**
	 * Test possible run.
	 */
	@Test
	public void testPossibleRun() {
		PossibleRun possibleRun = configureControlEntitiesAsSpringBeans
				.possibleRun();

		assertThat(possibleRun, is(notNullValue()));
		assertThat(possibleRun.getId(), is(equalTo(uuid)));
	}

	/**
	 * Test schedule.
	 */
	@Test
	public void testSchedule() {
		Schedule schedule = configureControlEntitiesAsSpringBeans.schedule();

		assertThat(schedule, is(notNullValue()));
		assertThat(schedule.getId(), is(equalTo(uuid)));
	}

	/**
	 * Test slot.
	 */
	@Test
	public void testSlot() {
		Slot slot = configureControlEntitiesAsSpringBeans.slot();

		assertThat(slot, is(notNullValue()));
		assertThat(slot.getId(), is(equalTo(uuid)));
	}

}
