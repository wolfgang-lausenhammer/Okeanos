package okeanos.data.internal.services.pricing.entities.serialization;

import java.lang.reflect.Type;

import okeanos.data.internal.services.pricing.entities.CostFunctionImpl;
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

public class CostFunctionDeserializer implements JsonDeserializer<CostFunction> {
	@Override
	public CostFunction deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		DateTime validAtDateTime = DateTime.parse(jsonObject
				.getAsJsonPrimitive("validFromDateTime").getAsString());
		DateTime validThroughDateTime = DateTime.parse(jsonObject
				.getAsJsonPrimitive("validThroughDateTime").getAsString());
		Price pricesAtDateTime = context.deserialize(
				jsonObject.getAsJsonObject("pricesFromDateTime"), Price.class);

		return new CostFunctionImpl(validAtDateTime, validThroughDateTime,
				pricesAtDateTime);
	}
}
