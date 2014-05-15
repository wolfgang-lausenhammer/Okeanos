package okeanos.control.internal.algorithms;

import java.util.List;

import org.springframework.stereotype.Component;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.control.entities.Configuration;
import okeanos.control.entities.OptimizedRun;

@Component("PSORegenerativeLoadOptimizer")
public class PSORegenerativeLoadOptimizer implements ControlAlgorithm {

	@Override
	public List<OptimizedRun> findBestConfiguration(
			Configuration currentConfiguration) {
		// TODO Auto-generated method stub
		return null;
	}

}
