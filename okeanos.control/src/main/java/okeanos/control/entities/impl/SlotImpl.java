package okeanos.control.entities.impl;

import java.io.Serializable;

import javax.measure.quantity.Power;

import okeanos.control.entities.Slot;

import org.jscience.physics.amount.Amount;

/**
 * Represents one time slot within a run. Every time slot can have different
 * consumption/production, e.g., a dishwasher could need power to heat up the
 * water, but then not needs as much power anymore for washing the dishes.
 * 
 * @author Wolfgang Lausenhammer
 */
public class SlotImpl implements Slot {

	private static final long serialVersionUID = -4079948602773029091L;
	/** The id. */
	private String id;

	/** The load. */
	private Amount<Power> load;

	@Override
	public String toString() {
		return String.format("[load=%s]", load);
	}

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
	public Amount<Power> getLoad() {
		return load;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.Slot#setLoad(javax.measure.Measurable)
	 */
	@Override
	public void setLoad(final Amount<Power> load) {
		this.load = load;
	}

}
