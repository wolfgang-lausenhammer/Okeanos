package okeanos.data.services;

import org.joda.time.DateTime;

public interface TimeService {
	void setPace(double factor);

	void setCurrentDateTime(DateTime dateTime);

	long currentTimeMillis();
}
