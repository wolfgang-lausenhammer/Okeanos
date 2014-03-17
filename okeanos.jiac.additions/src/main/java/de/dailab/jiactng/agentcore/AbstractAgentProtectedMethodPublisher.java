package de.dailab.jiactng.agentcore;

import de.dailab.jiactng.agentcore.knowledge.IMemory;

public class AbstractAgentProtectedMethodPublisher {
	public static IMemory getMemory(Agent agent) {
		return agent.memory;
	}
}
