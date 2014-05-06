package okeanos.core.entities.builder;

import okeanos.core.entities.Entity;
import de.dailab.jiactng.agentcore.IAgent;

/**
 * Represents an entity builder. Can be used to create new entities in a fluent
 * API.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface EntityBuilder extends Builder {

	/**
	 * Agent.
	 * 
	 * @param agent
	 *            the agent
	 * @return the entity builder
	 */
	EntityBuilder agent(IAgent agent);

	/**
	 * From json.
	 * 
	 * @param entityAsJson
	 *            the entity as json
	 * @return the entity builder
	 */
	EntityBuilder fromJson(String entityAsJson);

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.core.entities.builder.Builder#build()
	 */
	Entity build();
}
