package okeanos.model.internal.drivers.readers;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okeanos.data.services.Constants;
import okeanos.model.internal.drivers.readers.StaticLoadLoadProfileReader.XYEntity;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The Class StaticLoadLoadProfileReaderTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class StaticLoadLoadProfileReaderTest {

	/** The Constant FOUR. */
	private static final int FOUR = 4;

	/** The Constant HUNDRED. */
	private static final double HUNDRED = 100.0;

	/**
	 * Test get xy from load profile.
	 */
	@Test
	public void testGetXYFromLoadProfile() {
		Map<DateTime, Double> loadProfile = new ConcurrentSkipListMap<>();
		DateTime dateTime1 = DateTime.now();
		DateTime dateTime2 = dateTime1.plusMinutes(Constants.SLOT_INTERVAL);
		DateTime dateTime3 = dateTime2.plusMinutes(Constants.SLOT_INTERVAL);
		DateTime dateTime4 = dateTime3.plusMinutes(Constants.SLOT_INTERVAL);
		loadProfile.put(dateTime1, 0.0);
		loadProfile.put(dateTime2, 0.0);
		loadProfile.put(dateTime3, HUNDRED);
		loadProfile.put(dateTime4, HUNDRED);

		XYEntity<double[]> xy = StaticLoadLoadProfileReader
				.getXYFromLoadProfile(loadProfile);

		assertThat(xy, is(notNullValue()));
		assertThat(
				xy.getX(),
				is(equalTo(new double[] { dateTime1.getMillis(),
						dateTime2.getMillis(), dateTime3.getMillis(),
						dateTime4.getMillis() })));
		assertThat(xy.getY(),
				is(equalTo(new double[] { 0, 0, HUNDRED, HUNDRED })));
	}

	/**
	 * Test read load profile.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testReadLoadProfile() throws IOException {
		Resource loadProfileResource = new ClassPathResource(
				"test-load-profile.json");

		Map<DateTime, Double> loadProfile = StaticLoadLoadProfileReader
				.readLoadProfile(loadProfileResource);

		assertThat(loadProfile.entrySet(), hasSize(equalTo(FOUR)));
		assertThat(loadProfile.get(DateTime.parse("2014-04-25T00:00:00Z")),
				is(equalTo(0.0)));
		assertThat(loadProfile.get(DateTime.parse("2014-04-25T00:15:00Z")),
				is(equalTo(0.0)));
		assertThat(loadProfile.get(DateTime.parse("2014-04-25T00:30:00Z")),
				is(equalTo(HUNDRED)));
		assertThat(loadProfile.get(DateTime.parse("2014-04-25T00:45:00Z")),
				is(equalTo(HUNDRED)));
	}
}
