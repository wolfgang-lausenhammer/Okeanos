package okeanos.control.entities.impl;

import java.util.Map;

import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/**
 * Represents a schedule of a household device. Contains the
 * consumption/production of a device for several points in time.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ScheduleImpl implements Schedule {
	private static final long serialVersionUID = -6137167380166709470L;

	/** The id. */
	private String id;

	/** The schedule. */
	private Map<DateTime, Slot> schedule;

	/**
	 * Instantiates a new schedule.
	 * 
	 * @param id
	 *            the id
	 */
	public ScheduleImpl(final String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("ScheduleImpl [schedule=%s]", schedule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Schedule#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.core.entities.Schedule#getSchedule()
	 */
	@Override
	public Map<DateTime, Slot> getSchedule() {
		return schedule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Schedule#setSchedule(java.util.Map)
	 */
	@Override
	public void setSchedule(final Map<DateTime, Slot> schedule) {
		this.schedule = schedule;
	}
}
