package okeanos.control.entities.impl;

import java.util.List;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;

/**
 * Represents a configuration that contains the possible runs and the schedule
 * of the other devices, so that the control algorithm is able to figure out the
 * best possible configuration.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ConfigurationImpl implements Configuration {

	@Override
	public String toString() {
		return String
				.format("ConfigurationImpl [possibleRuns=%s, scheduleOfOtherDevices=%s]",
						possibleRuns, scheduleOfOtherDevices);
	}

	/** The id. */
	private String id;

	/** The possible runs. */
	private List<PossibleRun> possibleRuns;

	/** The schedule of other devices. */
	private Schedule scheduleOfOtherDevices;

	/**
	 * Instantiates a new configuration.
	 * 
	 * @param id
	 *            the id
	 */
	public ConfigurationImpl(final String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Configuration#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Configuration#getPossibleRuns()
	 */
	@Override
	public List<PossibleRun> getPossibleRuns() {
		return possibleRuns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Configuration#getScheduleOfOtherDevices()
	 */
	@Override
	public Schedule getScheduleOfOtherDevices() {
		return scheduleOfOtherDevices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.Configuration#setPossibleRun(java.util.List)
	 */
	@Override
	public void setPossibleRun(final List<PossibleRun> possibleRuns) {
		this.possibleRuns = possibleRuns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.Configuration#setSchedule(okeanos.control.entities
	 * .Schedule)
	 */
	@Override
	public void setSchedule(final Schedule scheduleOfOtherDevices) {
		this.scheduleOfOtherDevices = scheduleOfOtherDevices;
	}

}
