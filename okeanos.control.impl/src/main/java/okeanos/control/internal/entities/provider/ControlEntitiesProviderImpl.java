package okeanos.control.internal.entities.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.RunConstraint;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;

import org.springframework.stereotype.Component;

/**
 * Provides a central point to get instances of all the control entities.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("controlEntitiesProvider")
public class ControlEntitiesProviderImpl implements ControlEntitiesProvider {

	/** The configuration provider. */
	private Provider<Configuration> configurationProvider;

	/** The optimized runs provider. */
	private Provider<OptimizedRun> optimizedRunProvider;

	/** The possible runs provider. */
	private Provider<PossibleRun> possibleRunProvider;

	/** The possible runs configuration provider. */
	private Provider<PossibleRunsConfiguration> possibleRunsConfigurationProvider;

	/** The run constraint provider. */
	private Provider<RunConstraint> runConstraintProvider;

	/** The schedule provider. */
	private Provider<Schedule> scheduleProvider;

	/** The slot provider. */
	private Provider<Slot> slotProvider;

	/**
	 * Instantiates a new control entities provider.
	 * 
	 * @param configurationProvider
	 *            the configuration provider
	 * @param optimizedRunProvider
	 *            the optimized runs provider
	 * @param possibleRunProvider
	 *            the possible runs provider
	 * @param slotProvider
	 *            the slot provider
	 * @param scheduleProvider
	 *            the schedule provider
	 * @param possibleRunsConfigurationProvider
	 *            the possible runs configuration provider
	 * @param runConstraintProvider
	 *            the run constraint provider
	 */
	@Inject
	public ControlEntitiesProviderImpl(
			final Provider<Configuration> configurationProvider,
			final Provider<OptimizedRun> optimizedRunProvider,
			final Provider<PossibleRun> possibleRunProvider,
			final Provider<Slot> slotProvider,
			final Provider<Schedule> scheduleProvider,
			final Provider<PossibleRunsConfiguration> possibleRunsConfigurationProvider,
			final Provider<RunConstraint> runConstraintProvider) {
		this.configurationProvider = configurationProvider;
		this.optimizedRunProvider = optimizedRunProvider;
		this.possibleRunProvider = possibleRunProvider;
		this.slotProvider = slotProvider;
		this.scheduleProvider = scheduleProvider;
		this.possibleRunsConfigurationProvider = possibleRunsConfigurationProvider;
		this.runConstraintProvider = runConstraintProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.provider.ControlEntitiesProvider#getNewConfiguration
	 * ()
	 */
	@Override
	public Configuration getNewConfiguration() {
		return configurationProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.provider.ControlEntitiesProvider#getNewRunOptimized
	 * ()
	 */
	@Override
	public OptimizedRun getNewOptimizedRun() {
		return optimizedRunProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.provider.ControlEntitiesProvider#getNewRunProposed
	 * ()
	 */
	@Override
	public PossibleRun getNewPossibleRun() {
		return possibleRunProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.control.entities.provider.ControlEntitiesProvider#
	 * getNewPossibleRunsConfiguration()
	 */
	@Override
	public PossibleRunsConfiguration getNewPossibleRunsConfiguration() {
		return possibleRunsConfigurationProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.provider.ControlEntitiesProvider#getNewRunConstraint
	 * ()
	 */
	@Override
	public RunConstraint getNewRunConstraint() {
		return runConstraintProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.provider.ControlEntitiesProvider#getSchedule()
	 */
	@Override
	public Schedule getNewSchedule() {
		return scheduleProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.entities.provider.ControlEntitiesProvider#getNewSlot()
	 */
	@Override
	public Slot getNewSlot() {
		return slotProvider.get();
	}

}
