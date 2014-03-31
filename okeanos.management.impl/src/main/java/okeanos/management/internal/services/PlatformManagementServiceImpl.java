package okeanos.management.internal.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.management.internal.services.platformmanagement.OkeanosBasicAgentNode;
import okeanos.management.services.PlatformManagementService;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
public class PlatformManagementServiceImpl implements PlatformManagementService {
	private Provider<OkeanosBasicAgentNode> agentNodeProvider;

	@Logging
	private Logger log;

	private Map<String, IAgentNode> managedAgentNodes = new ConcurrentHashMap<>();

	@Inject
	public PlatformManagementServiceImpl(
			Provider<OkeanosBasicAgentNode> agentNodeProvider) {
		this.agentNodeProvider = agentNodeProvider;
	}

	@Override
	public IAgentNode getAgentNode(String id) {
		return managedAgentNodes.get(id);
	}

	@Override
	public IAgentNode getDefaultAgentNode() {
		try {
			if (managedAgentNodes.size() == 0) {
				return startAgentNode();
			} else {
				return managedAgentNodes.values().iterator().next();
			}
		} catch (LifecycleException e) {
			if (log != null)
				log.error(
						"Encountered a LivecycleException when starting an agent node [{}]",
						e);
			return null;
		}
	}

	@Override
	public IAgentNode startAgentNode() throws LifecycleException {
		IAgentNode node = agentNodeProvider.get();
		managedAgentNodes.put(node.getUUID(), node);
		// node will be started by spring!
		// node.start();

		if (log != null)
			log.debug("Started new agent node [node={}]", node);
		return node;
	}

	@Override
	public void stopAgentNode(IAgentNode agentNode) throws LifecycleException {
		managedAgentNodes.remove(agentNode.getUUID());

		if (agentNode instanceof SimpleAgentNode) {
			SimpleAgentNode node = (SimpleAgentNode) agentNode;
			node.shutdown();
		} else {
			agentNode.stop();
			agentNode.cleanup();
		}

		if (log != null)
			log.debug("Stopped {} agents on agent node [node={}]", agentNode
					.findAgents().size(), agentNode);
		if (log != null)
			log.debug("Stopped agent node [node={}]", agentNode);
	}
}
