package okeanos.control.entities;

import java.util.List;

import org.joda.time.DateTime;

public interface RunProposed {
	DateTime getEarliestStartTime();

	DateTime getLatestEndTime();

	LoadType getLoadType();

	List<Slot> getNeededSlots();
}
