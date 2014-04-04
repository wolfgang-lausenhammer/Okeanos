package okeanos.control.entities;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Represents a proposed run, i.e., a run that would be possible for the
 * household device. A dishwasher could, for example, start anytime after its
 * {@code earliestStartTime}, e.g. 1:30, however, needs the given time slots
 * {@code neededSlots} to finish. Also, it needs to finish before its
 * {@code latestEndTime}, e.g. 3:30. For one run, it is only possible to either
 * produce, or consume energy. If both is possible for a device, multiple runs
 * have to be defined.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface PossibleRun {
	DateTime getEarliestStartTime();

	String getId();

	DateTime getLatestEndTime();

	LoadType getLoadType();

	List<Slot> getNeededSlots();

	void setEarliestStartTime(DateTime earliestStartTime);

	void setLatestEndTime(DateTime latestEndTime);

	void setLoadType(LoadType loadType);

	void setNeededSlots(List<Slot> neededSlots);

}
