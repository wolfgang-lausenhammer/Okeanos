package okeanos.model.internal.drivers.pev;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import javax.measure.quantity.Power;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.RunConstraint;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.PossibleRunsConfigurationImpl;
import okeanos.control.entities.impl.RunConstraintImpl;
import okeanos.control.entities.impl.SlotImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.Constants;

import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * The Class Tesla_Model_S_85kWhTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Tesla_Model_S_85kWhTest {

	/** The Constant DEVICE_ID. */
	private static final String DEVICE_ID = "my-tesla";

	/** The Constant NUM_SLOTS. */
	private static final int NUM_SLOTS = Tesla_Model_S_85kWh.NUM_RUNS;

	/** The control entities provider. */
	@Mock
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The device. */
	private Tesla_Model_S_85kWh device;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		final AtomicInteger slotId = new AtomicInteger();
		Mockito.when(controlEntitiesProvider.getNewSlot()).thenAnswer(
				new Answer<Slot>() {

					@Override
					public Slot answer(final InvocationOnMock invocation)
							throws Throwable {
						return new SlotImpl("my-slot-id-"
								+ slotId.getAndIncrement());
					}
				});
		final AtomicInteger possibleRunId = new AtomicInteger();
		Mockito.when(controlEntitiesProvider.getNewPossibleRun()).thenAnswer(
				new Answer<PossibleRun>() {

					@Override
					public PossibleRun answer(final InvocationOnMock invocation)
							throws Throwable {
						return new PossibleRunImpl("my-possible-run-id-"
								+ possibleRunId.getAndIncrement());
					}
				});
		final AtomicInteger runConstraintId = new AtomicInteger();
		Mockito.when(controlEntitiesProvider.getNewRunConstraint()).thenAnswer(
				new Answer<RunConstraint>() {

					@Override
					public RunConstraint answer(
							final InvocationOnMock invocation) throws Throwable {
						return new RunConstraintImpl("my-possible-run-id-"
								+ runConstraintId.getAndIncrement());
					}
				});
		final AtomicInteger possibleRunConfigurationId = new AtomicInteger();
		Mockito.when(controlEntitiesProvider.getNewPossibleRunsConfiguration())
				.thenAnswer(new Answer<PossibleRunsConfigurationImpl>() {

					@Override
					public PossibleRunsConfigurationImpl answer(
							final InvocationOnMock invocation) throws Throwable {
						return new PossibleRunsConfigurationImpl(
								"my-possible-run-configuration-id-"
										+ possibleRunConfigurationId
												.getAndIncrement());
					}
				});

		this.device = new Tesla_Model_S_85kWh(DEVICE_ID,
				controlEntitiesProvider);
	}

	/**
	 * Test get id.
	 */
	@Test
	public void testGetId() {
		String id = device.getId();

		assertThat(id, equalTo(DEVICE_ID));
	}

	/**
	 * Test get possible runs configuration.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetPossibleRunsConfiguration() {
		DateTimeUtils.setCurrentMillisFixed(DateTime.parse("2014-05-05T13:00Z")
				.getMillis());

		PossibleRunsConfiguration possibleRunsConfiguration = device
				.getPossibleRunsConfiguration();

		assertThat(possibleRunsConfiguration.getLoadType(),
				equalTo(LoadType.REGENERATIVE_LOAD));
		assertThat(possibleRunsConfiguration.getPossibleRuns(),
				IsCollectionWithSize.hasSize(NUM_SLOTS));

		PossibleRun run1 = possibleRunsConfiguration.getPossibleRuns().get(0);
		assertThat(run1.getEarliestStartTime(),
				equalTo(DateTime.parse("2014-05-05T00:00Z")));
		assertThat(run1.getLatestEndTime(),
				equalTo(DateTime.parse("2014-05-05T00:00Z")));
		assertThat(run1.getLengthOfRun(),
				equalTo(Period.minutes(Constants.SLOT_INTERVAL)));
		assertThat(run1.getNeededSlots(), nullValue());
		assertThat(run1.getRangeOfPossibleLoads(), nullValue());
		assertThat(
				run1.getPossibleLoads(),
				IsIterableContainingInAnyOrder.containsInAnyOrder(
						Amount.valueOf(0, Power.UNIT),
						Amount.valueOf(10000, Power.UNIT),
						Amount.valueOf(-10000, Power.UNIT),
						Amount.valueOf(5000, Power.UNIT),
						Amount.valueOf(-5000, Power.UNIT),
						Amount.valueOf(2000, Power.UNIT),
						Amount.valueOf(-2000, Power.UNIT)));

		PossibleRun run2 = possibleRunsConfiguration.getPossibleRuns().get(1);
		assertThat(run2.getEarliestStartTime(),
				equalTo(DateTime.parse("2014-05-05T00:15Z")));
		assertThat(run2.getLatestEndTime(),
				equalTo(DateTime.parse("2014-05-05T00:15Z")));
		assertThat(run2.getLengthOfRun(),
				equalTo(Period.minutes(Constants.SLOT_INTERVAL)));
		assertThat(run2.getNeededSlots(), nullValue());
		assertThat(run2.getRangeOfPossibleLoads(), nullValue());
		assertThat(
				run2.getPossibleLoads(),
				IsIterableContainingInAnyOrder.containsInAnyOrder(
						Amount.valueOf(0, Power.UNIT),
						Amount.valueOf(10000, Power.UNIT),
						Amount.valueOf(-10000, Power.UNIT),
						Amount.valueOf(5000, Power.UNIT),
						Amount.valueOf(-5000, Power.UNIT),
						Amount.valueOf(2000, Power.UNIT),
						Amount.valueOf(-2000, Power.UNIT)));
	}
}
