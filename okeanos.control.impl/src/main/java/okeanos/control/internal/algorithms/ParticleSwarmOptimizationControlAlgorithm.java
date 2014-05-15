package okeanos.control.internal.algorithms;

import java.util.List;

import javax.inject.Inject;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Represents a control algorithm that utilizes particle swarm optimization
 * (PSO) to find the best configuration.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("particleSwarmOptimizationControlAlgorithm")
public class ParticleSwarmOptimizationControlAlgorithm implements
		ControlAlgorithm {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(ParticleSwarmOptimizationControlAlgorithm.class);

	/** The regulable load optimizer. */
	private ControlAlgorithm regulableLoadOptimizer;

	/** The load optimizer. */
	private ControlAlgorithm loadOptimizer;

	/** The regenerative load optimizer. */
	private ControlAlgorithm regenerativeLoadOptimizer;

	/**
	 * Instantiates a new particle swarm optimization control algorithm.
	 * 
	 * @param loadOptimizer
	 *            the load optimizer
	 * @param regulableLoadOptimizer
	 *            the regulable load optimizer
	 * @param regenerativeLoadOptimizer
	 *            the regenerative load optimizer
	 */
	@Inject
	public ParticleSwarmOptimizationControlAlgorithm(
			@Qualifier("LoadOptimizer") final ControlAlgorithm loadOptimizer,
			@Qualifier("PSORegulableLoadOptimizer") final ControlAlgorithm regulableLoadOptimizer,
			@Qualifier("PSORegenerativeLoadOptimizer") final ControlAlgorithm regenerativeLoadOptimizer) {
		this.loadOptimizer = loadOptimizer;
		this.regulableLoadOptimizer = regulableLoadOptimizer;
		this.regenerativeLoadOptimizer = regenerativeLoadOptimizer;
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
		List<OptimizedRun> optimizedRuns = null;
		switch (currentConfiguration.getPossibleRunsConfiguration()
				.getLoadType()) {
		case LOAD:
			optimizedRuns = loadOptimizer
					.findBestConfiguration(currentConfiguration);
			break;

		case REGULABLE_LOAD:
			optimizedRuns = regulableLoadOptimizer
					.findBestConfiguration(currentConfiguration);
			break;

		case REGENERATIVE_LOAD:
			optimizedRuns = regenerativeLoadOptimizer
					.findBestConfiguration(currentConfiguration);
			break;
		default:
			break;
		}

		return optimizedRuns;
	}
}
