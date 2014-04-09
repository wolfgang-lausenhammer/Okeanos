package okeanos.control.services.agentbeans.callbacks;

import java.util.List;

import okeanos.control.entities.PossibleRun;
import de.dailab.jiactng.agentcore.knowledge.IFact;

public interface PossibleRunsCallback extends IFact {
	List<PossibleRun> getPossibleRuns();
}
