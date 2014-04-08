package okeanos.control.entities;

import java.util.List;

import org.joda.time.DateTime;

import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * Represents an optimized run, i.e. a run that went through an optimization
 * algorithm that selected the best runs among the proposed runs. Specifies the
 * {@code loadType}, the {@code startTime} and the {@code neededSlots}.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface OptimizedRun extends IFact {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the load type.
	 * 
	 * @return the load type
	 */
	LoadType getLoadType();

	/**
	 * Gets the needed slots.
	 * 
	 * @return the needed slots
	 */
	List<Slot> getNeededSlots();

	/**
	 * Gets the start time.
	 * 
	 * @return the start time
	 */
	DateTime getStartTime();

	/**
	 * Sets the load type.
	 * 
	 * @param loadType
	 *            the new load type
	 */
	void setLoadType(LoadType loadType);

	/**
	 * Sets the needed slots.
	 * 
	 * @param neededSlots
	 *            the new needed slots
	 */
	void setNeededSlots(List<Slot> neededSlots);

	/**
	 * Sets the start time.
	 * 
	 * @param startTime
	 *            the new start time
	 */
	void setStartTime(DateTime startTime);
}
