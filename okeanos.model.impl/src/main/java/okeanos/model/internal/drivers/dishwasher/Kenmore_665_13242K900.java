package okeanos.model.internal.drivers.dishwasher;

import static javax.measure.unit.SI.WATT;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.measure.quantity.Power;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.Constants;
import okeanos.math.regression.PreviousValueTrendLine;
import okeanos.math.regression.TrendLine;
import okeanos.math.regression.periodic.Periodic24hTrendline;
import okeanos.model.entities.RegulableLoad;
import okeanos.model.internal.drivers.readers.StaticLoadLoadProfileReader;
import okeanos.model.internal.drivers.readers.StaticLoadLoadProfileReader.XYEntity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.jscience.physics.amount.Amount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;

/**
 * Driver for the Kenmore 665.13242K900 dishwasher. Represents a regulable load,
 * i.e. the load can be influenced and scheduled.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("Kenmore_665_13242K900")
public class Kenmore_665_13242K900 implements RegulableLoad {

	/** The Constant FOURTY_FIVE. */
	private static final int FOURTY_FIVE = 45;

	/** The Constant TWENTY_THREE. */
	private static final int TWENTY_THREE = 23;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The id. */
	private String id;

	/** The possible runs. */
	private List<PossibleRun> possibleRuns;

	/** The todays schedule. */
	private TrendLine todaysSchedule;

	/**
	 * Instantiates a new kenmore_665_13242 k900.
	 * 
	 * @param resource
	 *            the resource
	 * @param id
	 *            the id
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Inject
	public Kenmore_665_13242K900(
			@Value("${okeanos.model.internal.drivers.dishwasher.Kenmore_665_13242K900.loadProfilePath}") final Resource resource,
			@Value("#{ uuidGenerator.generateUUID() }") final String id,
			final ControlEntitiesProvider controlEntitiesProvider)
			throws IOException {
		this.id = id;
		this.controlEntitiesProvider = controlEntitiesProvider;

		Map<DateTime, Double> loadProfile = StaticLoadLoadProfileReader
				.readLoadProfile(resource);
		XYEntity<double[]> xyEntries = StaticLoadLoadProfileReader
				.getXYFromLoadProfile(loadProfile);

		PreviousValueTrendLine trendLine = new PreviousValueTrendLine();
		trendLine.setValues(xyEntries.getY(), xyEntries.getX());

		possibleRuns = createPossibleRunsFromLoadProfile(loadProfile, trendLine);
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
	 * @see okeanos.model.entities.Load#getPossibleRuns()
	 */
	@Override
	public List<PossibleRun> getPossibleRuns() {
		return possibleRuns;
	}

	/**
	 * Creates the possible runs from load profile.
	 * 
	 * @param loadProfile
	 *            the load profile
	 * @param trendLine
	 *            the trend line
	 * @return the list
	 */
	private List<PossibleRun> createPossibleRunsFromLoadProfile(
			final Map<DateTime, Double> loadProfile, final TrendLine trendLine) {
		List<Slot> neededSlots = new LinkedList<>();

		for (DateTime instant = Iterables.get(loadProfile.keySet(), 0); instant
				.isBefore(Iterables.getLast(loadProfile.keySet())
						.plusSeconds(1)); instant = instant
				.plusMinutes(Constants.SLOT_INTERVAL)) {
			Slot currentSlot = controlEntitiesProvider.getNewSlot();
			currentSlot.setLoad(Amount.valueOf(
					trendLine.predict(instant.getMillis()), WATT));
			neededSlots.add(currentSlot);
		}

		DateTime startOfToday = DateTime.now(DateTimeZone.UTC)
				.withTimeAtStartOfDay();
		DateTime endOfToday = startOfToday.withTime(TWENTY_THREE, FOURTY_FIVE,
				00, 0);

		PossibleRun run = controlEntitiesProvider.getNewPossibleRun();
		run.setEarliestStartTime(startOfToday);
		run.setLatestEndTime(endOfToday);
		run.setLoadType(LoadType.CONSUMER);
		run.setNeededSlots(neededSlots);
		return Arrays.asList(run);
	}

}
