package okeanos.control.entities.impl;

import java.util.Map;

import okeanos.control.entities.Schedule;

import org.joda.time.DateTime;

// TODO: Auto-generated Javadoc
/**
 * Represents a schedule of a household device. Contains the
 * consumption/production of a device for several points in time.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ScheduleImpl implements Schedule {

	/** The serialVersionUID. */
	private static final long serialVersionUID = -8286110109867048650L;

	/** The id. */
	private String id;

	/** The schedule. */
	private Map<DateTime, Double> schedule;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ScheduleImpl [id=%s]", id);
	}

	/**
	 * Instantiates a new schedule.
	 * 
	 * @param id
	 *            the id
	 */
	public ScheduleImpl(final String id) {
		this.id = id;
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
	public Map<DateTime, Double> getSchedule() {
		return schedule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Schedule#setSchedule(java.util.Map)
	 */
	@Override
	public void setSchedule(final Map<DateTime, Double> schedule) {
		this.schedule = schedule;
	}
}
