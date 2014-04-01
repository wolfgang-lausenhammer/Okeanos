package okeanos.core.entities;

import java.util.Map;

import org.joda.time.DateTime;

import de.dailab.jiactng.agentcore.knowledge.IFact;

public interface Schedule extends IFact {
	Map<DateTime, Double> getSchedule();
}
