package okeanos.control.entities;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Represents an optimized run, i.e. a run that went through an optimization
 * algorithm that selected the best runs among the proposed runs. Specifies the
 * {@code loadType}, the {@code startTime} and the {@code neededSlots}.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface OptimizedRun {
	String getId();

	LoadType getLoadType();

	List<Slot> getNeededSlots();

	DateTime getStartTime();

	void setLoadType(LoadType loadType);

	void setNeededSlots(List<Slot> neededSlots);

	void setStartTime(DateTime startTime);
}
