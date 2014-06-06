package okeanos.control.entities.impl;

import java.util.Map;
import java.util.Set;

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
	private Map<DateTime, Amount<Power>> chargesAtPointsInTime;

	/** The id. */
	private String id;

	/** The losses. */
	private Map<DateTime, Amount<Power>> losses;

	/** The maximum capacity. */
	private Amount<Power> maximumCapacity;

	/** The minimum capacity. */
	private Amount<Power> minimumCapacity;

	/** The no action at points in time. */
	private Set<DateTime> noActionAtPointsInTime;

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
	 * @see okeanos.control.entities.RunConstraint#getChargesAtPointsInTime()
	 */
	@Override
	public Map<DateTime, Amount<Power>> getChargesAtPointsInTime() {
		return chargesAtPointsInTime;
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
	 * @see
	 * okeanos.control.entities.RunConstraint#getLossOfEnergyAtPointsInTime()
	 */
	@Override
	public Map<DateTime, Amount<Power>> getLossOfEnergyAtPointsInTime() {
		return losses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#getMaximumCapacity()
	 */
	@Override
	public Amount<Power> getMaximumCapacity() {
		return maximumCapacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#getMinimumCapacity()
	 */
	@Override
	public Amount<Power> getMinimumCapacity() {
		return minimumCapacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.RunConstraint#getNoActionAtPointsInTime()
	 */
	@Override
	public Set<DateTime> getNoActionAtPointsInTime() {
		return noActionAtPointsInTime;
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
	 * @see
	 * okeanos.control.entities.RunConstraint#setChargesAtPointsInTime(java.
	 * util.Map)
	 */
	@Override
	public void setChargesAtPointsInTime(
			final Map<DateTime, Amount<Power>> chargesAtPointsInTime) {
		this.chargesAtPointsInTime = chargesAtPointsInTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunConstraint#setLossOfEnergyAtPointsInTime(
	 * java.util.Map)
	 */
	@Override
	public void setLossOfEnergyAtPointsInTime(
			final Map<DateTime, Amount<Power>> losses) {
		this.losses = losses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunConstraint#setMaximumCapacity(org.jscience
	 * .physics.amount.Amount)
	 */
	@Override
	public void setMaximumCapacity(final Amount<Power> maximumCapacity) {
		this.maximumCapacity = maximumCapacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunConstraint#setMinimumCapacity(org.jscience
	 * .physics.amount.Amount)
	 */
	@Override
	public void setMinimumCapacity(final Amount<Power> minimumCapacity) {
		this.minimumCapacity = minimumCapacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.RunConstraint#setNoActionsInPointInTime(java
	 * .util.Set)
	 */
	@Override
	public void setNoActionsInPointInTime(final Set<DateTime> noActions) {
		noActionAtPointsInTime = noActions;
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
