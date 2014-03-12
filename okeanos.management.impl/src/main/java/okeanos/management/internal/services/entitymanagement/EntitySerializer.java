package okeanos.management.internal.services.entitymanagement;

import java.lang.reflect.Type;

import okeanos.core.entities.Entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EntitySerializer implements JsonSerializer<Entity> {

	@Override
	public JsonElement serialize(Entity src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("id", src.getId());
		return jsonObj;
	}

}
