package okeanos.model.internal.drivers.readers;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.measure.quantity.Power;

import okeanos.control.entities.Slot;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Static Load Load Profile Reader reads the load profile from a resource and
 * provides a method to convert it to a x/y form, so that it can be used for
 * interpolating.
 * 
 * @author Wolfgang Lausenhammer
 */
public final class StaticLoadLoadProfileReader {

	/**
	 * Represents the x/y values of a load profile.
	 * 
	 * @param <T>
	 *            the generic type
	 */
	public static class XYEntity<T> {

		/** The x. */
		private T x;

		/** The y. */
		private T y;

		/**
		 * Gets x.
		 * 
		 * @return the x
		 */
		public T getX() {
			return x;
		}

		/**
		 * Gets y.
		 * 
		 * @return the y
		 */
		public T getY() {
			return y;
		}

		/**
		 * Sets x.
		 * 
		 * @param x
		 *            the new x
		 */
		public void setX(final T x) {
			this.x = x;
		}

		/**
		 * Sets y.
		 * 
		 * @param y
		 *            the new y
		 */
		public void setY(final T y) {
			this.y = y;
		}
	}

	/** Gson (de)serializer. */
	private static Gson gson = new Gson();

	/** The Logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(StaticLoadLoadProfileReader.class);

	/**
	 * Gets the X/Y values from the load profile.
	 * 
	 * @param loadProfile
	 *            the load profile
	 * @return the X/Y values from load profile
	 */
	public static XYEntity<double[]> getXYFromLoadProfile(
			final Map<DateTime, Double> loadProfile) {
		double[] x = new double[loadProfile.size()];
		double[] y = new double[loadProfile.size()];
		int i = 0;

		for (Entry<DateTime, Double> entry : loadProfile.entrySet()) {
			x[i] = entry.getKey().getMillis();
			y[i] = entry.getValue();
			i++;
		}

		XYEntity<double[]> xy = new XYEntity<>();
		xy.setX(x);
		xy.setY(y);

		return xy;
	}

	/**
	 * Gets the X/Y values from the load profile.
	 * 
	 * @param loadProfile
	 *            the load profile
	 * @return the X/Y values from load profile
	 */
	public static XYEntity<double[]> getXYFromLoadProfileSlot(
			final Map<DateTime, Slot> loadProfile) {
		double[] x = new double[loadProfile.size()];
		double[] y = new double[loadProfile.size()];
		int i = 0;

		for (Entry<DateTime, Slot> entry : loadProfile.entrySet()) {
			x[i] = entry.getKey().getMillis();
			y[i] = entry.getValue().getLoad().doubleValue(Power.UNIT);
			i++;
		}

		XYEntity<double[]> xy = new XYEntity<>();
		xy.setX(x);
		xy.setY(y);

		return xy;
	}

	/**
	 * Reads a load profile from a resource.
	 * 
	 * @param loadProfile
	 *            the load profile
	 * @return the map with the load profile
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Map<DateTime, Double> readLoadProfile(
			final Resource loadProfile) throws IOException {
		Map<DateTime, Double> loadProfileMap = new ConcurrentSkipListMap<>();
		String jsonString = IOUtils.toString(loadProfile.getInputStream());

		JsonArray arrayOfInstances = gson.fromJson(jsonString, JsonArray.class);
		for (JsonElement item : arrayOfInstances) {
			try {
				JsonObject obj = item.getAsJsonObject();

				DateTime dateTime = DateTime.parse(obj.get("dateTime")
						.getAsString());
				double consumption = obj.get("consumption").getAsDouble();

				loadProfileMap.put(dateTime, consumption);
			} catch (IllegalStateException e) {
				continue;
			}
		}

		if (LOG != null) {
			LOG.trace("Read Load Profile:\n[{}]",
					StringUtils.join(loadProfileMap, StringUtils.LF));
		}

		return loadProfileMap;
	}

	/**
	 * Private constructor.
	 */
	private StaticLoadLoadProfileReader() {
	}
}
