package okeanos.data.internal.services.pricing.entities.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okeanos.data.services.entities.CostFunction;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ListCostFunctionDeserializer implements
		JsonDeserializer<List<CostFunction>> {
	private static final Logger log = LoggerFactory
			.getLogger(ListCostFunctionDeserializer.class);

	@Override
	public List<CostFunction> deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonArray array = json.getAsJsonArray();
		List<CostFunction> result = new ArrayList<>(array.size());

		Iterator<JsonElement> iterator = array.iterator();
		while (iterator.hasNext()) {
			CostFunction costFcn = (CostFunction) context.deserialize(
					iterator.next(), CostFunction.class);
			log.trace("Deserialized cost function: {}", costFcn);
			result.add(costFcn);
		}
		log.trace("Deserialized cost functions: {}",
				StringUtils.join(result, "\n"));

		return result;
	}
}
