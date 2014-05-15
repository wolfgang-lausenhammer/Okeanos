package okeanos.control.internal.algorithms;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.provider.ControlEntitiesProvider;

import org.springframework.stereotype.Component;

/**
 * Represents a no operation control algorithm. That is, all proposed runs are
 * taken as optimized runs.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("noOpControlAlgorithm")
public class NoOpControlAlgorithm implements ControlAlgorithm {

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/**
	 * Instantiates a new no operation control algorithm.
	 * 
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 */
	@Inject
	public NoOpControlAlgorithm(
			final ControlEntitiesProvider controlEntitiesProvider) {
		this.controlEntitiesProvider = controlEntitiesProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.algorithms.ControlAlgorithm#findBestConfiguration(okeanos
	 * .control.entities.Configuration)
	 */
	@Override
	public List<OptimizedRun> findBestConfiguration(
			final Configuration currentConfiguration) {
		List<PossibleRun> possibleRuns = currentConfiguration
				.getPossibleRunsConfiguration().getPossibleRuns();
		List<OptimizedRun> runsOptimized = new LinkedList<>();

		for (PossibleRun currentPossibleRun : possibleRuns) {
			OptimizedRun currentOptimizedRun = controlEntitiesProvider
					.getNewOptimizedRun();
			currentOptimizedRun.setLoadType(currentPossibleRun.getLoadType());
			currentOptimizedRun.setStartTime(currentPossibleRun
					.getEarliestStartTime());
			currentOptimizedRun.setNeededSlots(currentPossibleRun
					.getNeededSlots());

			runsOptimized.add(currentOptimizedRun);
		}

		return runsOptimized;
	}
}
