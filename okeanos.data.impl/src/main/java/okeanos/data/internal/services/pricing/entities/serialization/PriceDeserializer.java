package okeanos.data.internal.services.pricing.entities.serialization;

import java.lang.reflect.Type;

import okeanos.data.internal.services.pricing.entities.PriceImpl;
import okeanos.data.services.entities.Price;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PriceDeserializer implements JsonDeserializer<Price> {
	private static final Logger log = LoggerFactory
			.getLogger(PriceDeserializer.class);
	private static final float DEFAULT_SLOPE = 1;
	private static final int NUMBERS_TO_APPROXIMATE = 10;
	private static final int DISTANCE_BETWEEN_X = 10;

	@Override
	public Price deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		float priceFor1kWh = jsonObject.get("priceFor1kWh").getAsFloat();
		float slope = DEFAULT_SLOPE;

		if (jsonObject.get("slope") != null) {
			slope = jsonObject.get("slope").getAsFloat();
		}

		log.trace("Deserialized priceFor1kWh: {}", priceFor1kWh);
		log.trace("Deserialized slope: {}", slope);

		double[] x = new double[NUMBERS_TO_APPROXIMATE];
		double[] y = new double[NUMBERS_TO_APPROXIMATE];

		for (int i = 0; i < NUMBERS_TO_APPROXIMATE; i++) {
			x[i] = (i + 1) * DISTANCE_BETWEEN_X;
			y[i] = Math.exp(x[i] * slope);
		}

		return new PriceImpl(x, y);
	}
}
