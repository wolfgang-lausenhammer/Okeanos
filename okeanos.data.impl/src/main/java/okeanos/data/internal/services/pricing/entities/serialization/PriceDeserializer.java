package okeanos.data.internal.services.pricing.entities.serialization;

import java.lang.reflect.Type;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okeanos.data.internal.services.pricing.entities.PriceImpl;
import okeanos.data.services.entities.Price;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PriceDeserializer implements JsonDeserializer<Price> {
	private static final Logger log = LoggerFactory
			.getLogger(PriceDeserializer.class);

	@Override
	public Price deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		JsonArray xArray = jsonObject.getAsJsonArray("kwh");
		JsonArray yArray = jsonObject.getAsJsonArray("cost");
		double[] x = context.deserialize(xArray, double[].class);
		double[] y = context.deserialize(yArray, double[].class);

		log.trace("Deserialized kwH: {}", Arrays.toString(x));
		log.trace("Deserialized cost: {}", Arrays.toString(y));

		return new PriceImpl(x, y);
	}
}
