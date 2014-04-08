package okeanos.control.entities.impl;

import java.util.List;

import okeanos.control.entities.LoadType;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Slot;

import org.joda.time.DateTime;

// TODO: Auto-generated Javadoc
/**
 * Represents an optimized run, i.e. a run that went through an optimization
 * algorithm that selected the best runs among the proposed runs. Specifies the
 * {@code loadType}, the {@code startTime} and the {@code neededSlots}.
 * 
 * @author Wolfgang Lausenhammer
 */
public class OptimizedRunImpl implements OptimizedRun {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2634930143806210883L;

	/** The id. */
	private String id;

	/** The load type. */
	private LoadType loadType;

	/** The needed slots. */
	private List<Slot> neededSlots;

	/** The start time. */
	private DateTime startTime;

	/**
	 * Instantiates a new optimized run.
	 * 
	 * @param id
	 *            the id
	 */
	public OptimizedRunImpl(final String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunOptimized#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunOptimized#getLoadType()
	 */
	@Override
	public LoadType getLoadType() {
		return loadType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunOptimized#getNeededSlots()
	 */
	@Override
	public List<Slot> getNeededSlots() {
		return neededSlots;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunOptimized#getStartTime()
	 */
	@Override
	public DateTime getStartTime() {
		return startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunOptimized#setLoadType(okeanos.control.entities
	 * .LoadType)
	 */
	@Override
	public void setLoadType(final LoadType loadType) {
		this.loadType = loadType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunOptimized#setNeededSlots(java.util.List)
	 */
	@Override
	public void setNeededSlots(final List<Slot> neededSlots) {
		this.neededSlots = neededSlots;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunOptimized#setStartTime(org.joda.time.DateTime
	 * )
	 */
	@Override
	public void setStartTime(final DateTime startTime) {
		this.startTime = startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"OptimizedRunImpl [loadType=%s, neededSlots=%s, startTime=%s]",
				loadType, neededSlots, startTime);
	}

}
