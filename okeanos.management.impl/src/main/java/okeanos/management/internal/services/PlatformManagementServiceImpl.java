package okeanos.management.internal.services;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.management.internal.services.platformmanagement.OkeanosBasicAgentNode;
import okeanos.management.services.PlatformManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
public class PlatformManagementServiceImpl implements PlatformManagementService {
	private Map<String, IAgentNode> managedAgentNodes = new ConcurrentHashMap<>();
	private Logger log = LoggerFactory
			.getLogger(PlatformManagementServiceImpl.class);

	private Provider<OkeanosBasicAgentNode> agentNodeProvider;

	@Inject
	public PlatformManagementServiceImpl(
			Provider<OkeanosBasicAgentNode> agentNodeProvider) {
		this.agentNodeProvider = agentNodeProvider;
	}

	@Override
	public IAgentNode startAgentNode() throws LifecycleException {
		IAgentNode node = agentNodeProvider.get();
		managedAgentNodes.put(node.getUUID(), node);
		// node will be started by spring!
		// node.start();

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

		log.debug("Stopped {} agents on agent node [node={}]", agentNode
				.findAgents().size(), agentNode);
		log.debug("Stopped agent node [node={}]", agentNode);
	}

	@Override
	public IAgentNode getAgentNode(String id) {
		return managedAgentNodes.get(id);
	}

	@Override
	public IAgentNode getDefaultAgentNode() {
		try {
			return managedAgentNodes.values().iterator().next();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
}
