package okeanos.control.internal.algorithms;

import java.util.List;

import org.springframework.stereotype.Component;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;

/**
 * Represents a control algorithm that utilizes particle swarm optimization
 * (PSO) to find the best configuration.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("particleSwarmOptimizationControlAlgorithm")
public class ParticleSwarmOptimizationControlAlgorithm implements
		ControlAlgorithm {

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
		// TODO Auto-generated method stub
		return null;
	}
}
