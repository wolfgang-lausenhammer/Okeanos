package okeanos.control.entities.impl;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.Schedule;

/**
 * Represents a configuration that contains the possible runs and the schedule
 * of the other devices, so that the control algorithm is able to figure out the
 * best possible configuration.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ConfigurationImpl implements Configuration {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5682690715158469780L;

	/** The id. */
	private String id;

	/** The possible runs configuration. */
	private PossibleRunsConfiguration possibleRunsConfiguration;

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
	 * @see
	 * okeanos.control.entities.Configuration#getPossibleRunsConfiguration()
	 */
	@Override
	public PossibleRunsConfiguration getPossibleRunsConfiguration() {
		return possibleRunsConfiguration;
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
	 * okeanos.control.entities.Configuration#setPossibleRunsConfiguration(okeanos
	 * .control.entities.PossibleRunsConfiguration)
	 */
	@Override
	public void setPossibleRunsConfiguration(
			final PossibleRunsConfiguration possibleRunsConfiguration) {
		this.possibleRunsConfiguration = possibleRunsConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.Configuration#setSchedule(okeanos.control.entities
	 * .Schedule)
	 */
	@Override
	public void setScheduleOfOtherDevices(final Schedule scheduleOfOtherDevices) {
		this.scheduleOfOtherDevices = scheduleOfOtherDevices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("ConfigurationImpl [possibleRunsConfiguration=%s, scheduleOfOtherDevices=%s]",
						possibleRunsConfiguration, scheduleOfOtherDevices);
	}

}
