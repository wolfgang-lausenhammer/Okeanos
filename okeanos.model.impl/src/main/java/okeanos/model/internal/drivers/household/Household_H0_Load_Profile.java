package okeanos.model.internal.drivers.household;

import static javax.measure.unit.SI.WATT;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.measure.quantity.Power;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.Constants;
import okeanos.math.regression.PreviousValueInsideZeroOutsideTrendLine;
import okeanos.math.regression.TrendLine;
import okeanos.math.regression.periodic.Periodic24hTrendline;
import okeanos.model.entities.Load;
import okeanos.model.internal.drivers.readers.StaticLoadLoadProfileReader;
import okeanos.model.internal.drivers.readers.StaticLoadLoadProfileReader.XYEntity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.jscience.physics.amount.Amount;
import org.springframework.core.io.Resource;

/**
 * Driver for a H0 household as standardized by VDEW. Represents a static load,
 * i.e. the load cannot be influenced in any way.
 * 
 * @author Wolfgang Lausenhammer
 */
public class Household_H0_Load_Profile implements Load {

	/** The Constant LOAD_PROFILE_FACTOR. */
	private static final int LOAD_PROFILE_FACTOR = 30;

	/** The Constant FOURTY_FIVE. */
	private static final int FOURTY_FIVE = 45;

	/** The Constant TWENTY_THREE. */
	private static final int TWENTY_THREE = 23;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The id. */
	private String id;

	/** The load profile. */
	private TrendLine loadProfile;

	/** The k wh per day. */
	private Amount<Power> kWhPerDay;

	/**
	 * Instantiates a new Household_H0_Load_Profile.
	 * 
	 * @param resource
	 *            the resource
	 * @param id
	 *            the id
	 * @param kWhPerDay
	 *            the k wh per day
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Household_H0_Load_Profile(final Resource resource, final String id,
			final Amount<Power> kWhPerDay,
			final ControlEntitiesProvider controlEntitiesProvider)
			throws IOException {
		this.id = id;
		this.controlEntitiesProvider = controlEntitiesProvider;
		this.kWhPerDay = kWhPerDay;
		XYEntity<double[]> xyEntries = StaticLoadLoadProfileReader
				.getXYFromLoadProfile(StaticLoadLoadProfileReader
						.readLoadProfile(resource));

		loadProfile = new Periodic24hTrendline(
				new PreviousValueInsideZeroOutsideTrendLine());
		loadProfile.setValues(xyEntries.getY(), xyEntries.getX());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.model.entities.Load#getConsumption()
	 */
	@Override
	public Amount<Power> getConsumption() {
		return Amount
				.valueOf(loadProfile.predict(DateTime.now(DateTimeZone.UTC)
						.getMillis()), WATT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.model.entities.Load#getConsumptionIn(javax.measure.Measurable)
	 */
	@Override
	public Amount<Power> getConsumptionIn(final Period duration) {
		DateTime pointInTime = DateTime.now(DateTimeZone.UTC).plus(duration);
		return Amount.valueOf(loadProfile.predict(pointInTime.getMillis()),
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
	 * @see okeanos.model.entities.Load#getPossibleRuns()
	 */
	@Override
	public PossibleRunsConfiguration getPossibleRunsConfiguration() {
		DateTime startOfToday = DateTime.now(DateTimeZone.UTC)
				.withTimeAtStartOfDay();
		DateTime endOfToday = startOfToday.withTime(TWENTY_THREE, FOURTY_FIVE,
				0, 1);
		List<Slot> neededSlots = new LinkedList<>();

		for (DateTime instant = startOfToday; instant.isBefore(endOfToday); instant = instant
				.plusMinutes(Constants.SLOT_INTERVAL)) {
			Slot currentSlot = controlEntitiesProvider.getNewSlot();
			currentSlot
					.setLoad(Amount.valueOf(
							loadProfile.predict(instant.getMillis())
									* kWhPerDay.doubleValue(WATT)
									/ LOAD_PROFILE_FACTOR, WATT));
			neededSlots.add(currentSlot);
		}

		PossibleRun run = controlEntitiesProvider.getNewPossibleRun();
		run.setEarliestStartTime(startOfToday);
		run.setLatestEndTime(endOfToday);
		run.setLengthOfRun(Period.minutes(neededSlots.size()
				* Constants.SLOT_INTERVAL));
		run.setNeededSlots(neededSlots);

		PossibleRunsConfiguration possibleRunsConfiguration = controlEntitiesProvider
				.getNewPossibleRunsConfiguration();
		possibleRunsConfiguration.setPossibleRuns(Arrays.asList(run));
		possibleRunsConfiguration.setRunConstraint(controlEntitiesProvider
				.getNewRunConstraint());
		possibleRunsConfiguration.setLoadType(LoadType.LOAD);

		return possibleRunsConfiguration;
	}
}
