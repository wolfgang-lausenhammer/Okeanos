package okeanos.model.internal.drivers.pev;

import static javax.measure.unit.SI.WATT;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.measure.quantity.Power;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.RunConstraint;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.Constants;
import okeanos.math.regression.PreviousValueTrendLine;
import okeanos.math.regression.TrendLine;
import okeanos.math.regression.periodic.Periodic24hTrendline;
import okeanos.model.entities.RegenerativeLoad;
import okeanos.model.internal.drivers.readers.StaticLoadLoadProfileReader;
import okeanos.model.internal.drivers.readers.StaticLoadLoadProfileReader.XYEntity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.jscience.physics.amount.Amount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Driver for a Tesla Model S with 85kWh. Represents a regenerative load, i.e.
 * the load can be influenced and scheduled.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("Tesla_Model_S_85kWh")
public class Tesla_Model_S_85kWh implements RegenerativeLoad {

	/** The Constant MAXIMUM_CAPACITY. */
	private static final Amount<Power> MAXIMUM_CAPACITY = Amount.valueOf(85000,
			Power.UNIT);

	/** The Constant MINIMUM_CAPACITY. */
	private static final Amount<Power> MINIMUM_CAPACITY = Amount.valueOf(0,
			Power.UNIT);

	/** The Constant NUM_RUNS. */
	private static final int NUM_RUNS = 2;

	/** The Constant START_CAPACITY. */
	private static final Amount<Power> START_CAPACITY = Amount.valueOf(10000,
			Power.UNIT);

	/** The Constant TWENTY_KWH_CHARGE. */
	private static final Amount<Power> TWENTY_KWH_CHARGE = Amount.valueOf(
			20000, Power.UNIT);

	/** The Constant ZERO_CHARGE. */
	private static final Amount<Power> ZERO_CHARGE = Amount.valueOf(0,
			Power.UNIT);

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The id. */
	private String id;

	/** The todays schedule. */
	private TrendLine todaysSchedule;

	/**
	 * Instantiates a new tesla_ model_ s_85k wh.
	 * 
	 * @param id
	 *            the id
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Inject
	public Tesla_Model_S_85kWh(
			@Value("#{ uuidGenerator.generateUUID() }") final String id,
			final ControlEntitiesProvider controlEntitiesProvider)
			throws IOException {
		this.id = id;
		this.controlEntitiesProvider = controlEntitiesProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.model.entities.RegulableLoad#applySchedule(okeanos.control.entities
	 * .Schedule)
	 */
	@Override
	public void applySchedule(final Schedule schedule) {
		XYEntity<double[]> xyEntries = StaticLoadLoadProfileReader
				.getXYFromLoadProfileSlot(schedule.getSchedule());

		todaysSchedule = new Periodic24hTrendline(new PreviousValueTrendLine());
		todaysSchedule.setValues(xyEntries.getY(), xyEntries.getX());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.model.entities.Load#getConsumption()
	 */
	@Override
	public Amount<Power> getConsumption() {
		return Amount.valueOf(
				todaysSchedule.predict(DateTime.now().getMillis()), WATT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.model.entities.Load#getConsumptionIn(org.joda.time.Period)
	 */
	@Override
	public Amount<Power> getConsumptionIn(final Period duration) {
		DateTime pointInTime = DateTime.now().plus(duration);
		return Amount.valueOf(todaysSchedule.predict(pointInTime.getMillis()),
				WATT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.model.entities.Load#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.model.entities.Load#getPossibleRunsConfiguration()
	 */
	@Override
	public PossibleRunsConfiguration getPossibleRunsConfiguration() {
		Set<Amount<Power>> possibleLoads = new HashSet<>();
		possibleLoads.add(ZERO_CHARGE);
		possibleLoads.add(TWENTY_KWH_CHARGE);
		possibleLoads.add(TWENTY_KWH_CHARGE.opposite());

		DateTime currentTime = DateTime.now(DateTimeZone.UTC)
				.withTimeAtStartOfDay();

		List<PossibleRun> runs = new LinkedList<>();
		Period lengthOfRuns = Period
				.minutes(((24 * 60 / Constants.SLOT_INTERVAL) / NUM_RUNS)
						* Constants.SLOT_INTERVAL);
		for (int i = 0; i < NUM_RUNS; i++) {
			PossibleRun run = controlEntitiesProvider.getNewPossibleRun();
			run.setEarliestStartTime(currentTime);
			run.setLatestEndTime(currentTime.plus(lengthOfRuns).minusMinutes(
					Constants.SLOT_INTERVAL));
			run.setLengthOfRun(lengthOfRuns);
			run.setPossibleLoads(possibleLoads);
			runs.add(run);
			currentTime = currentTime.plus(lengthOfRuns);
		}

		RunConstraint runConstraint = controlEntitiesProvider
				.getNewRunConstraint();
		runConstraint.setStartCharge(START_CAPACITY);
		runConstraint.setMinimumCharge(MINIMUM_CAPACITY);
		runConstraint.setMaximumCharge(MAXIMUM_CAPACITY);

		PossibleRunsConfiguration possibleRunsConfiguration = controlEntitiesProvider
				.getNewPossibleRunsConfiguration();
		possibleRunsConfiguration.setPossibleRuns(runs);
		possibleRunsConfiguration.setRunConstraint(runConstraint);
		possibleRunsConfiguration.setLoadType(LoadType.REGENERATIVE_LOAD);

		return possibleRunsConfiguration;
	}
}
