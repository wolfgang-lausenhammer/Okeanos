package okeanos.control.entities;

import java.util.List;

public interface Configuration {
	List<RunOptimized> getRunsOptimized();

	List<RunProposed> getRunsProposed();
}
