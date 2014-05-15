package okeanos.control.entities.impl;

import java.util.Map;

import javax.measure.quantity.Power;

import okeanos.control.entities.RunConstraint;

import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;

/**
 * Represents the constraints of a run.
 * 
 * @author Wolfgang Lausenhammer
 */
public class RunConstraintImpl implements RunConstraint {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9166858616557806286L;

	/** The charges. */
	private Map<DateTime, Amount<Power>> charges;

	/** The id. */
	private String id;

	/** The maximum charge. */
	private Amount<Power> maximumCharge;

	/** The minimum charge. */
	private Amount<Power> minimumCharge;

	/** The start charge. */
	private Amount<Power> startCharge;

	/**
	 * Instantiates a new run constraint impl.
	 * 
	 * @param id
	 *            the id
	 */
	public RunConstraintImpl(final String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#getCharges()
	 */
	@Override
	public Map<DateTime, Amount<Power>> getCharges() {
		return charges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#getMaximumCharge()
	 */
	@Override
	public Amount<Power> getMaximumCharge() {
		return maximumCharge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#getMinimumCharge()
	 */
	@Override
	public Amount<Power> getMinimumCharge() {
		return minimumCharge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#getStartCharge()
	 */
	@Override
	public Amount<Power> getStartCharge() {
		return startCharge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#setCharges(java.util.Map)
	 */
	@Override
	public void setCharges(final Map<DateTime, Amount<Power>> charges) {
		this.charges = charges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunConstraint#setMaximumCharge(org.jscience.
	 * physics.amount.Amount)
	 */
	@Override
	public void setMaximumCharge(final Amount<Power> maximumCharge) {
		this.maximumCharge = maximumCharge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunConstraint#setMinimumCharge(org.jscience.
	 * physics.amount.Amount)
	 */
	@Override
	public void setMinimumCharge(final Amount<Power> minimumCharge) {
		this.minimumCharge = minimumCharge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunConstraint#setStartCharge(org.jscience.physics
	 * .amount.Amount)
	 */
	@Override
	public void setStartCharge(final Amount<Power> startCharge) {
		this.startCharge = startCharge;
	}

}
