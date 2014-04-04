package okeanos.control.entities.impl;

import okeanos.control.entities.Slot;

/**
 * Represents one time slot within a run. Every time slot can have different
 * consumption/production, e.g., a dishwasher could need power to heat up the
 * water, but then not needs as much power anymore for washing the dishes.
 * 
 * @author Wolfgang Lausenhammer
 */
public class SlotImpl implements Slot {

	/** The id. */
	private String id;

	/** The load. */
	private double load;

	/**
	 * Instantiates a new slot.
	 * 
	 * @param id
	 *            the id
	 */
	public SlotImpl(final String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Slot#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Slot#getLoad()
	 */
	@Override
	public double getLoad() {
		return load;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Slot#setLoad(double)
	 */
	@Override
	public void setLoad(final double load) {
		this.load = load;
	}

}
