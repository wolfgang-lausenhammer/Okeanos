package okeanos.control.entities;

import org.joda.time.DateTime;

public interface RunOptimized {
	DateTime getStartTime();

	void setStartTime(DateTime startTime);

	LoadType getLoadType();

	void setLoadType(LoadType loadType);
}
