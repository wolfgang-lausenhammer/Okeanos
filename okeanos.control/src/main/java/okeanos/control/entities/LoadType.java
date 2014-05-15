package okeanos.control.entities;

/**
 * A device can be one of the three types of load.
 * 
 * @author Wolfgang Lausenhammer
 * 
 * @see okeanos.model.entities.Load
 * @see okeanos.model.entities.RegulableLoad
 * @see okeanos.model.entities.RegenerativeLoad
 * 
 */
public enum LoadType {

	/** Regular Load. */
	LOAD,

	/** Regulable load. */
	REGULABLE_LOAD,

	/** Regenerative load. */
	REGENERATIVE_LOAD
}
