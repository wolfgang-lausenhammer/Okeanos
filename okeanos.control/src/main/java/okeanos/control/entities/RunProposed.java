package okeanos.control.entities;

import java.util.List;

import org.joda.time.DateTime;

public interface RunProposed {
	DateTime getEarliestStartTime();

	void setEarliestStartTime(DateTime earliestStartTime);

	DateTime getLatestEndTime();

	void setLatestEndTime(DateTime latestEndTime);

	List<Slot> getNeededSlots();

	void setNeededSlots(List<Slot> neededSlots);

	LoadType getLoadType();

	void setLoadType(LoadType loadType);
}
