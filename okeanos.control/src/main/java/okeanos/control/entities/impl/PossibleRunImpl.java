package okeanos.control.entities.impl;

import java.util.List;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Slot;

import org.joda.time.DateTime;

/**
 * Represents a proposed run, i.e., a run that would be possible for the
 * household device. A dishwasher could, for example, start anytime after its
 * {@code earliestStartTime}, e.g. 1:30, however, needs the given time slots
 * {@code neededSlots} to finish. Also, it needs to finish before its
 * {@code latestEndTime}, e.g. 3:30. For one run, it is only possible to either
 * produce, or consume energy. If both is possible for a device, multiple runs
 * have to be defined.
 * 
 * @author Wolfgang Lausenhammer
 */
public class PossibleRunImpl implements PossibleRun {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4835192147397594886L;

	/** The earliest start time. */
	private DateTime earliestStartTime;

	/** The id. */
	private String id;

	/** The latest end time. */
	private DateTime latestEndTime;

	/** The load type. */
	private LoadType loadType;

	/** The needed slots. */
	private List<Slot> neededSlots;

	/**
	 * Instantiates a new proposed run.
	 * 
	 * @param id
	 *            the id
	 */
	public PossibleRunImpl(final String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunProposed#getEarliestStartTime()
	 */
	@Override
	public DateTime getEarliestStartTime() {
		return earliestStartTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunProposed#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunProposed#getLatestEndTime()
	 */
	@Override
	public DateTime getLatestEndTime() {
		return latestEndTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunProposed#getLoadType()
	 */
	@Override
	public LoadType getLoadType() {
		return loadType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunProposed#getNeededSlots()
	 */
	@Override
	public List<Slot> getNeededSlots() {
		return neededSlots;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunProposed#setEarliestStartTime(org.joda.time
	 * .DateTime)
	 */
	@Override
	public void setEarliestStartTime(final DateTime earliestStartTime) {
		this.earliestStartTime = earliestStartTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunProposed#setLatestEndTime(org.joda.time.DateTime
	 * )
	 */
	@Override
	public void setLatestEndTime(final DateTime latestEndTime) {
		this.latestEndTime = latestEndTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunProposed#setLoadType(okeanos.control.entities
	 * .LoadType)
	 */
	@Override
	public void setLoadType(final LoadType loadType) {
		this.loadType = loadType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunProposed#setNeededSlots(java.util.List)
	 */
	@Override
	public void setNeededSlots(final List<Slot> neededSlots) {
		this.neededSlots = neededSlots;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("PossibleRunImpl [earliestStartTime=%s, latestEndTime=%s, loadType=%s, neededSlots=%s]",
						earliestStartTime, latestEndTime, loadType, neededSlots);
	}

}
