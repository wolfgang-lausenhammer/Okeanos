package okeanos.management.internal.services.entitymanagement;

import java.lang.reflect.Type;

import okeanos.core.entities.Entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes the Entity class to json.
 * 
 * @author Wolfgang Lausenhammer
 */
public class EntitySerializer implements JsonSerializer<Entity> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(final Entity src, final Type typeOfSrc,
			final JsonSerializationContext context) {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("id", src.getId());
		return jsonObj;
	}

}
