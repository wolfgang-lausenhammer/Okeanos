package okeanos.control.entities.impl;

import java.util.List;

import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.RunConstraint;

/**
 * Represents the configuration for the possible runs, therefore, it also
 * includes constraints in addition to the actual possible runs.
 * 
 * @author Wolfgang Lausenhammer
 */
public class PossibleRunsConfigurationImpl implements PossibleRunsConfiguration {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7676296238318770224L;

	/** The id. */
	private String id;

	/** The possible runs. */
	private List<PossibleRun> possibleRuns;

	/** The run constraint. */
	private RunConstraint runConstraint;

	/**
	 * Instantiates a new possible runs configuration impl.
	 * 
	 * @param id
	 *            the id
	 */
	public PossibleRunsConfigurationImpl(final String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.PossibleRunsConfiguration#getId()
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
	 * @see okeanos.control.entities.Configuration#getRunConstraint()
	 */
	@Override
	public RunConstraint getRunConstraint() {
		return runConstraint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.Configuration#setPossibleRun(java.util.List)
	 */
	@Override
	public void setPossibleRuns(final List<PossibleRun> possibleRuns) {
		this.possibleRuns = possibleRuns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.Configuration#setRunConstraint(okeanos.control
	 * .entities.RunConstraint)
	 */
	@Override
	public void setRunConstraint(final RunConstraint runConstraint) {
		this.runConstraint = runConstraint;
	}
}
