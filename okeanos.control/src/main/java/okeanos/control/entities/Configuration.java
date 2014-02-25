package okeanos.control.entities;

import java.util.List;

public interface Configuration {
	List<RunProposed> getRunsProposed();

	List<RunOptimized> getRunsOptimized();

	void setRunsProposed(List<RunProposed> runsProposed);

	void setRunsOptimized(List<RunOptimized> runsOptimized);
}
