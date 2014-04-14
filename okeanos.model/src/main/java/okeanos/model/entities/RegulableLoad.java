package okeanos.model.entities;

import okeanos.control.entities.Schedule;

public interface RegulableLoad extends Load {
	void applySchedule(Schedule schedule);
}
