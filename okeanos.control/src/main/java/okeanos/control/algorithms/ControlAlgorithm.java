package okeanos.control.algorithms;

import java.util.List;

import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;

public interface ControlAlgorithm {

	List<OptimizedRun> findBestConfiguration(Configuration currentConfiguration);
}
