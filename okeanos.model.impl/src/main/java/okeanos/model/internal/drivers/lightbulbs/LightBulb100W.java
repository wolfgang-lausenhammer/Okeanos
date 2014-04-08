package okeanos.model.internal.drivers.lightbulbs;

import static javax.measure.unit.SI.SECOND;
import static javax.measure.unit.SI.WATT;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Power;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.math.regression.PreviousValueTrendLine;
import okeanos.math.regression.TrendLine;
import okeanos.math.regression.periodic.Periodic24hTrendline;
import okeanos.model.entities.Load;
import okeanos.model.internal.drivers.lightbulbs.StaticLoadLoadProfileReader.XYEntity;

import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * The Class LightBulb100W.
 */
@Component
public class LightBulb100W implements Load {

	/** The load profile. */
	private TrendLine loadProfile;

	/** The id. */
	private String id;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/**
	 * Instantiates a new light bulb100 w.
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
	public LightBulb100W(
			@Value("${okeanos.model.internal.drivers.lightbulbs.LightBulb100W.loadProfilePath}") final Resource resource,
			@Value("#{ uuidGenerator.generateUUID() }") final String id,
			final ControlEntitiesProvider controlEntitiesProvider)
			throws IOException {
		this.id = id;
		this.controlEntitiesProvider = controlEntitiesProvider;
		XYEntity<double[]> xyEntries = StaticLoadLoadProfileReader
				.getXYFromLoadProfile(StaticLoadLoadProfileReader
						.readLoadProfile(resource));

		loadProfile = new Periodic24hTrendline(new PreviousValueTrendLine());
		loadProfile.setValues(xyEntries.getY(), xyEntries.getX());
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
	 * @see okeanos.model.entities.Load#getConsumption()
	 */
	@Override
	public Measurable<Power> getConsumption() {
		return Measure.valueOf(loadProfile.predict(DateTime.now().getMillis()),
				WATT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.model.entities.Load#getConsumptionIn(javax.measure.Measurable)
	 */
	@Override
	public Measurable<Power> getConsumptionIn(
			final Measurable<Duration> duration) {
		DateTime pointInTime = DateTime.now().plusSeconds(
				(int) duration.longValue(SECOND));
		return Measure.valueOf(loadProfile.predict(pointInTime.getMillis()),
				WATT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.model.entities.Load#getPossibleRuns()
	 */
	@Override
	public List<PossibleRun> getPossibleRuns() {
		DateTime startOfToday = DateTime.now().withTimeAtStartOfDay();
		DateTime endOfToday = startOfToday.withHourOfDay(23)
				.withMinuteOfHour(59).withSecondOfMinute(59);
		List<Slot> neededSlots = new LinkedList<>();

		for (DateTime instant = startOfToday; instant.isBefore(endOfToday); instant = instant
				.plusMinutes(15)) {
			Slot currentSlot = controlEntitiesProvider.getNewSlot();
			currentSlot.setLoad(Amount.valueOf(
					loadProfile.predict(instant.getMillis()), WATT));
			neededSlots.add(currentSlot);
		}
		
		PossibleRun run = controlEntitiesProvider.getNewPossibleRun();
		run.setEarliestStartTime(startOfToday);
		run.setLatestEndTime(endOfToday);
		run.setLoadType(LoadType.CONSUMER);
		run.setNeededSlots(neededSlots);
		return Arrays.asList(run);
	}
}
