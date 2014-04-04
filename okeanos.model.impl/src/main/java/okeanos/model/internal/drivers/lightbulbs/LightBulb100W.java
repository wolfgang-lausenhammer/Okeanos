package okeanos.model.internal.drivers.lightbulbs;

import static javax.measure.unit.SI.SECOND;
import static javax.measure.unit.SI.WATT;

import java.io.IOException;

import javax.inject.Inject;
import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Power;

import okeanos.math.regression.PolyTrendLine;
import okeanos.math.regression.TrendLine;
import okeanos.math.regression.periodic.Periodic24hTrendline;
import okeanos.model.entities.Load;
import okeanos.model.internal.drivers.lightbulbs.StaticLoadLoadProfileReader.XYEntity;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * The Class LightBulb100W.
 */
@Component
public class LightBulb100W implements Load {

	/**
	 * The degree of the polynomial that will be used to interpolate the load
	 * profile.
	 */
	private static final int TRENDLINE_DEGREE = 10;

	/** The load profile. */
	private TrendLine loadProfile;

	/** The id. */
	private String id;

	/**
	 * Instantiates a new light bulb100 w.
	 * 
	 * @param resource
	 *            the resource
	 * @param id
	 *            the id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Inject
	public LightBulb100W(
			@Value("${okeanos.model.internal.drivers.lightbulbs.LightBulb100W.loadProfilePath}") final Resource resource,
			@Value("#{ uuidGenerator.generateUUID() }") final String id)
			throws IOException {
		this.id = id;
		XYEntity<double[]> xyEntries = StaticLoadLoadProfileReader
				.getXYFromLoadProfile(StaticLoadLoadProfileReader
						.readLoadProfile(resource));

		loadProfile = new Periodic24hTrendline(new PolyTrendLine(
				TRENDLINE_DEGREE));
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

}
