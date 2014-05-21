package okeanos.control.entities.impl;

import java.util.List;
import java.util.Set;

import javax.measure.quantity.Power;

import okeanos.control.entities.LoadFlexiblity;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Slot;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.jscience.physics.amount.Amount;

import com.google.common.collect.Range;

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

	/** The length of run. */
	private Period lengthOfRun;

	/** The load flexibility. */
	private LoadFlexiblity loadFlexibility;

	/** The needed slots. */
	private List<Slot> neededSlots;

	/** The possible loads. */
	private Set<Amount<Power>> possibleLoads;

	/** The range of possible loads. */
	private Range<Amount<Power>> rangeOfPossibleLoads;

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
	 * @see okeanos.control.entities.PossibleRun#getLengthOfRun()
	 */
	@Override
	public Period getLengthOfRun() {
		return lengthOfRun;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.PossibleRun#getLoadFlexibilityOfRun()
	 */
	@Override
	public LoadFlexiblity getLoadFlexibilityOfRun() {
		return loadFlexibility;
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
	 * @see okeanos.control.entities.PossibleRun#getPossibleLoads()
	 */
	@Override
	public Set<Amount<Power>> getPossibleLoads() {
		return possibleLoads;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.PossibleRun#getRangeOfPossibleLoads()
	 */
	@Override
	public Range<Amount<Power>> getRangeOfPossibleLoads() {
		return rangeOfPossibleLoads;
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
	 * okeanos.control.entities.PossibleRun#setLengthOfRun(org.joda.time.Period)
	 */
	@Override
	public void setLengthOfRun(final Period lengthOfRun) {
		this.lengthOfRun = lengthOfRun;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.PossibleRun#setLoadFlexibilityOfRun(okeanos.
	 * control.entities.LoadFlexiblity)
	 */
	@Override
	public void setLoadFlexibilityOfRun(final LoadFlexiblity loadFlexibility) {
		this.loadFlexibility = loadFlexibility;
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
	 * @see okeanos.control.entities.PossibleRun#setPossibleLoads(java.util.Set)
	 */
	@Override
	public void setPossibleLoads(final Set<Amount<Power>> possibleLoads) {
		this.possibleLoads = possibleLoads;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.PossibleRun#setRangeOfPossibleLoads(com.google
	 * .common.collect.Range)
	 */
	@Override
	public void setRangeOfPossibleLoads(final Range<Amount<Power>> rangeOfPossibleLoads) {
		this.rangeOfPossibleLoads = rangeOfPossibleLoads;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("PossibleRunImpl [earliestStartTime=%s, latestEndTime=%s, length=%s]",
						earliestStartTime, latestEndTime, lengthOfRun);
	}

}
