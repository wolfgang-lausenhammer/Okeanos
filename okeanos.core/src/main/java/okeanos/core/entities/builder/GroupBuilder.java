package okeanos.core.entities.builder;

import okeanos.core.entities.Entity;
import okeanos.core.entities.Group;

/**
 * Represents a group builder. Can be used to create new groups in a fluent API.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface GroupBuilder extends Builder {

	/**
	 * Entity.
	 * 
	 * @param entity
	 *            the entity
	 * @return the group builder
	 */
	GroupBuilder entity(Entity entity);

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.core.entities.builder.Builder#build()
	 */
	Group build();
}
