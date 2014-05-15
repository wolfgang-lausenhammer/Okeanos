package okeanos.control.internal.algorithms;

import static javax.measure.unit.SI.WATT;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.LoadType;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.RunConstraint;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.ConfigurationImpl;
import okeanos.control.entities.impl.OptimizedRunImpl;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.PossibleRunsConfigurationImpl;
import okeanos.control.entities.impl.RunConstraintImpl;
import okeanos.control.entities.impl.SlotImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;

import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * The Class LoadOptimizerTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class LoadOptimizerTest {

	/** The Constant CONFIGURATION_ID. */
	private static final String CONFIGURATION_ID = "my-configuration-id";

	/** The Constant LOAD_100. */
	private static final double LOAD_100 = 100;

	/** The Constant LOAD_200. */
	private static final double LOAD_200 = 200;

	/** The Constant LOAD_300. */
	private static final double LOAD_300 = 300;

	/** The Constant OPTIMIZED_RUN_ID. */
	private static final String OPTIMIZED_RUN_ID = "my-optimized-run-id";

	/** The Constant POSSIBLE_RUN_ID. */
	private static final String POSSIBLE_RUN_ID = "my-possible-run-id";

	/** The Constant POSSIBLE_RUNS_CONFIGURATION_ID. */
	private static final String POSSIBLE_RUNS_CONFIGURATION_ID = "my-possible-runs-configuration-id";

	/** The Constant RUN_CONSTRAINT_ID. */
	private static final String RUN_CONSTRAINT_ID = "my-run-constraint-id";

	/** The Constant SLOT_ID. */
	private static final String SLOT_ID = "my-slot-id";

	/** The Constant TIME_1_OCLOCK. */
	private static final DateTime TIME_1_OCLOCK = DateTime
			.parse("2014-03-20T01:00:00-05:00");

	/** The Constant TIME_MIDNIGHT. */
	private static final DateTime TIME_MIDNIGHT = DateTime
			.parse("2014-03-20T00:00:00-05:00");

	/** The control entities provider. */
	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The no op control algorithm. */
	private LoadOptimizer noOpControlAlgorithm;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		noOpControlAlgorithm = new LoadOptimizer(controlEntitiesProvider);

		when(controlEntitiesProvider.getNewOptimizedRun()).thenReturn(
				new OptimizedRunImpl(OPTIMIZED_RUN_ID));
	}

	/**
	 * Test find best configuration.
	 */
	@Test
	public void testFindBestConfiguration() {
		// Prepare
		Slot slot1 = new SlotImpl(SLOT_ID);
		Slot slot2 = new SlotImpl(SLOT_ID);
		Slot slot3 = new SlotImpl(SLOT_ID);
		slot1.setLoad(Amount.valueOf(LOAD_100, WATT));
		slot2.setLoad(Amount.valueOf(LOAD_200, WATT));
		slot3.setLoad(Amount.valueOf(LOAD_300, WATT));

		List<Slot> neededSlots = new LinkedList<>();
		neededSlots.add(slot1);
		neededSlots.add(slot2);
		neededSlots.add(slot3);

		PossibleRun possibleRun = new PossibleRunImpl(POSSIBLE_RUN_ID);
		possibleRun.setEarliestStartTime(TIME_MIDNIGHT);
		possibleRun.setLatestEndTime(TIME_1_OCLOCK);
		possibleRun.setNeededSlots(neededSlots);

		List<PossibleRun> possibleRuns = Arrays.asList(possibleRun);

		RunConstraint runConstraint = new RunConstraintImpl(RUN_CONSTRAINT_ID);

		PossibleRunsConfiguration possibleRunsConfiguration = new PossibleRunsConfigurationImpl(
				POSSIBLE_RUNS_CONFIGURATION_ID);
		possibleRunsConfiguration.setPossibleRuns(possibleRuns);
		possibleRunsConfiguration.setRunConstraint(runConstraint);

		Configuration currentConfiguration = new ConfigurationImpl(
				CONFIGURATION_ID);
		currentConfiguration
				.setPossibleRunsConfiguration(possibleRunsConfiguration);

		// Do
		List<OptimizedRun> configuration = noOpControlAlgorithm
				.findBestConfiguration(currentConfiguration);

		// Assert
		assertThat(configuration.size(), is(equalTo(1)));
		OptimizedRun run = configuration.get(0);
		assertThat(run.getId(), is(equalTo(OPTIMIZED_RUN_ID)));
		assertThat(run.getStartTime(), is(equalTo(TIME_MIDNIGHT)));
		assertThat(run.getNeededSlots(), is(equalTo(neededSlots)));

	}

}
