package okeanos.core.entities;

/**
 * Represents the interface for a group. A group is a collection of entities,
 * that is, also other groups can be contained in a group.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface Group extends Entity {
	boolean addEntity(Entity entity);

	boolean removeEntity(Entity entity);
}
