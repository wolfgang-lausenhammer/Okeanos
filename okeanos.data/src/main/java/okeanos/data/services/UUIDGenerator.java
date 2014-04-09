package okeanos.data.services;

/**
 * The UUIDGenerator interface combines methods for generating unique IDs.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface UUIDGenerator {

	/**
	 * Generate a unique ID.
	 * 
	 * @return the unique ID as a string
	 */
	String generateUUID();
}
