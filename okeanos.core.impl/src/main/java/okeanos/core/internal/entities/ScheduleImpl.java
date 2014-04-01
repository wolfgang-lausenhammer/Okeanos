package okeanos.core.internal.entities;

import java.util.Map;

import org.joda.time.DateTime;

import okeanos.core.entities.Schedule;

public class ScheduleImpl implements Schedule {
	private Map<DateTime, Double> schedule;

	public ScheduleImpl(Map<DateTime, Double> schedule) {
		this.schedule = schedule;
	}

	@Override
	public Map<DateTime, Double> getSchedule() {
		return schedule;
	}
}
