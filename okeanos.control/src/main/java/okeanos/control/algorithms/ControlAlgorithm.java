package okeanos.control.algorithms;

import okeanos.control.entities.Configuration;

public interface ControlAlgorithm {

	public Configuration findBestConfiguration(
			Configuration currentConfiguration);
}
