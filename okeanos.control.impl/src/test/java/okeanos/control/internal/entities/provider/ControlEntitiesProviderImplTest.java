package okeanos.control.internal.entities.provider;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.ConfigurationImpl;
import okeanos.control.entities.impl.OptimizedRunImpl;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.impl.SlotImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * The Class ControlEntitiesProviderImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ControlEntitiesProviderImplTest {

	/** The Constant CONFIGURATION_ID. */
	private static final String CONFIGURATION_ID = "my-configuration-id";

	/** The Constant OPTIMIZED_RUN_ID. */
	private static final String OPTIMIZED_RUN_ID = "my-optimized-run-id";

	/** The Constant POSSIBLE_RUN_ID. */
	private static final String POSSIBLE_RUN_ID = "my-possible-run-id";

	/** The Constant SCHEDULE_ID. */
	private static final String SCHEDULE_ID = "my-schedule-id";

	/** The Constant SLOT_ID. */
	private static final String SLOT_ID = "my-slot-id";

	/** The configuration provider. */
	@Mock
	private Provider<Configuration> configurationProvider;

	/** The control entities provider impl. */
	private ControlEntitiesProviderImpl controlEntitiesProviderImpl;

	/** The optimized run provider. */
	@Mock
	private Provider<OptimizedRun> optimizedRunProvider;

	/** The possible run provider. */
	@Mock
	private Provider<PossibleRun> possibleRunProvider;

	/** The schedule provider. */
	@Mock
	private Provider<Schedule> scheduleProvider;

	/** The slot provider. */
	@Mock
	private Provider<Slot> slotProvider;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(configurationProvider.get()).thenReturn(
				new ConfigurationImpl(CONFIGURATION_ID));
		when(optimizedRunProvider.get()).thenReturn(
				new OptimizedRunImpl(OPTIMIZED_RUN_ID));
		when(possibleRunProvider.get()).thenReturn(
				new PossibleRunImpl(POSSIBLE_RUN_ID));
		when(scheduleProvider.get()).thenReturn(new ScheduleImpl(SCHEDULE_ID));
		when(slotProvider.get()).thenReturn(new SlotImpl(SLOT_ID));

		controlEntitiesProviderImpl = new ControlEntitiesProviderImpl(
				configurationProvider, optimizedRunProvider,
				possibleRunProvider, slotProvider, scheduleProvider);
	}

	/**
	 * Test get new configuration.
	 */
	@Test
	public void testGetNewConfiguration() {
		Configuration result = controlEntitiesProviderImpl
				.getNewConfiguration();

		assertThat(result.getId(), is(equalTo(CONFIGURATION_ID)));
	}

	/**
	 * Test get new optimized run.
	 */
	@Test
	public void testGetNewOptimizedRun() {
		OptimizedRun result = controlEntitiesProviderImpl.getNewOptimizedRun();

		assertThat(result.getId(), is(equalTo(OPTIMIZED_RUN_ID)));
	}

	/**
	 * Test get new possible run.
	 */
	@Test
	public void testGetNewPossibleRun() {
		PossibleRun result = controlEntitiesProviderImpl.getNewPossibleRun();

		assertThat(result.getId(), is(equalTo(POSSIBLE_RUN_ID)));
	}

	/**
	 * Test get new schedule.
	 */
	@Test
	public void testGetNewSchedule() {
		Schedule result = controlEntitiesProviderImpl.getNewSchedule();

		assertThat(result.getId(), is(equalTo(SCHEDULE_ID)));
	}

	/**
	 * Test get new slot.
	 */
	@Test
	public void testGetNewSlot() {
		Slot result = controlEntitiesProviderImpl.getNewSlot();

		assertThat(result.getId(), is(equalTo(SLOT_ID)));
	}

}
