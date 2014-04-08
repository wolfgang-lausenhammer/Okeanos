package okeanos.control.algorithms;

import java.util.List;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;

/**
 * Represents a generic interface for all load optimizing algorithms.
 * 
 * According to whatever best means for an agent, the algorithms behind that can
 * vary. However, it is recommended, that the same algorithm is used.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface ControlAlgorithm {

	/**
	 * Finds the best configuration with the sum of other devices' consumptions
	 * at different times and a list of possible runs.
	 * 
	 * @param currentConfiguration
	 *            the current configuration
	 * @return an optimized list of runs
	 */
	List<OptimizedRun> findBestConfiguration(Configuration currentConfiguration);
}
