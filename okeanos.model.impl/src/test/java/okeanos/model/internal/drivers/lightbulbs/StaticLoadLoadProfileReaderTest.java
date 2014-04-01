package okeanos.model.internal.drivers.lightbulbs;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okeanos.model.internal.drivers.lightbulbs.StaticLoadLoadProfileReader.XYEntity;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class StaticLoadLoadProfileReaderTest {

	@Test
	public void testReadLoadProfile() throws IOException {
		Resource loadProfileResource = new ClassPathResource(
				"test-load-profile.json");

		Map<DateTime, Double> loadProfile = StaticLoadLoadProfileReader
				.readLoadProfile(loadProfileResource);

		assertThat(loadProfile.size(), is(equalTo(4)));
		assertThat(
				loadProfile.get(DateTime.parse("2014-03-20T00:00:00-05:00")),
				is(equalTo(0.0)));
		assertThat(
				loadProfile.get(DateTime.parse("2014-03-20T00:15:00-05:00")),
				is(equalTo(0.0)));
		assertThat(
				loadProfile.get(DateTime.parse("2014-03-20T00:30:00-05:00")),
				is(equalTo(100.0)));
		assertThat(
				loadProfile.get(DateTime.parse("2014-03-20T00:45:00-05:00")),
				is(equalTo(100.0)));
	}

	@Test
	public void testGetXYFromLoadProfile() {
		Map<DateTime, Double> loadProfile = new ConcurrentSkipListMap<>();
		DateTime dateTime1 = DateTime.now();
		DateTime dateTime2 = dateTime1.plusMinutes(15);
		DateTime dateTime3 = dateTime2.plusMinutes(15);
		DateTime dateTime4 = dateTime3.plusMinutes(15);
		loadProfile.put(dateTime1, 0.0);
		loadProfile.put(dateTime2, 0.0);
		loadProfile.put(dateTime3, 100.0);
		loadProfile.put(dateTime4, 100.0);

		XYEntity<double[]> xy = StaticLoadLoadProfileReader
				.getXYFromLoadProfile(loadProfile);

		assertThat(xy, is(notNullValue()));
		assertThat(
				xy.getX(),
				is(equalTo(new double[] { dateTime1.getMillis(),
						dateTime2.getMillis(), dateTime3.getMillis(),
						dateTime4.getMillis() })));
		assertThat(xy.getY(), is(equalTo(new double[] { 0, 0, 100, 100 })));
	}
}
