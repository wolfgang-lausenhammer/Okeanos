package okeanos.data.internal.services.pricing.entities.serialization;

import java.lang.reflect.Type;

import okeanos.data.internal.services.pricing.entities.CostFunctionImpl;
import okeanos.data.internal.services.pricing.entities.PriceImpl;
import okeanos.data.services.entities.CostFunction;
import okeanos.data.services.entities.Price;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Json deserializer for the cost function.
 * 
 * @author Wolfgang Lausenhammer
 */
public class CostFunctionDeserializer implements JsonDeserializer<CostFunction> {

	/**
	 * The default slope of the exponential function if none is given. Slope is
	 * the speed of the exponential increase of the function.
	 */
	private static final double DEFAULT_SLOPE = 0.1;

	/** The default number of points to approximate the exponential function. */
	private static final int POINTS_TO_APPROXIMATE = 10;

	/** The distance between each of the approximated points. */
	private static final int DISTANCE_BETWEEN_X = 10;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(PriceDeserializer.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
	 * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public CostFunction deserialize(final JsonElement json, final Type typeOfT,
			final JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		DateTime validAtDateTime = DateTime.parse(jsonObject
				.getAsJsonPrimitive("validFromDateTime").getAsString());
		DateTime validThroughDateTime = DateTime.parse(jsonObject
				.getAsJsonPrimitive("validThroughDateTime").getAsString());

		double priceFor1kWh = jsonObject.get("priceFor1kWh").getAsDouble();
		double slope = DEFAULT_SLOPE;

		if (jsonObject.get("slope") != null) {
			slope = jsonObject.get("slope").getAsDouble();
		}

		LOG.trace("Deserialized priceFor1kWh: {}", priceFor1kWh);
		LOG.trace("Deserialized slope: {}", slope);

		double[] x = new double[POINTS_TO_APPROXIMATE];
		double[] y = new double[POINTS_TO_APPROXIMATE];

		for (int i = 0; i < POINTS_TO_APPROXIMATE; i++) {
			x[i] = i * DISTANCE_BETWEEN_X + 1;
			y[i] = priceFor1kWh * (x[i] + Math.exp(x[i] * slope));
		}

		Price pricesAtDateTime = new PriceImpl(new double[] { 1000 },
				new double[] { priceFor1kWh });

		return new CostFunctionImpl(validAtDateTime, validThroughDateTime,
				pricesAtDateTime);
	}
}
