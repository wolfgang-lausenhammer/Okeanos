package okeanos.management.internal.services;

import java.util.Map;
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

/**
 * Provides functions for managing the platform.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
public class PlatformManagementServiceImpl implements PlatformManagementService {

	/** The agent node provider. */
	private Provider<OkeanosBasicAgentNode> agentNodeProvider;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(PlatformManagementServiceImpl.class);

	/** The managed agent nodes. */
	private Map<String, IAgentNode> managedAgentNodes = new ConcurrentHashMap<>();

	/**
	 * Instantiates a new platform management service.
	 * 
	 * @param agentNodeProvider
	 *            the agent node provider
	 */
	@Inject
	public PlatformManagementServiceImpl(
			Provider<OkeanosBasicAgentNode> agentNodeProvider) {
		this.agentNodeProvider = agentNodeProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.PlatformManagementService#getAgentNode(java
	 * .lang.String)
	 */
	@Override
	public IAgentNode getAgentNode(String id) {
		return managedAgentNodes.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.PlatformManagementService#getDefaultAgentNode
	 * ()
	 */
	@Override
	public IAgentNode getDefaultAgentNode() {
		try {
			if (managedAgentNodes.size() == 0) {
				return startAgentNode();
			} else {
				return managedAgentNodes.values().iterator().next();
			}
		} catch (LifecycleException e) {
			LOG.error(
					"Encountered a LivecycleException when starting an agent node [{}]",
					e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.PlatformManagementService#startAgentNode()
	 */
	@Override
	public IAgentNode startAgentNode() throws LifecycleException {
		IAgentNode node = agentNodeProvider.get();
		managedAgentNodes.put(node.getUUID(), node);
		// node will be started by spring!
		// node.start();

		LOG.debug("Started new agent node [node={}]", node);
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.PlatformManagementService#stopAgentNode(de
	 * .dailab.jiactng.agentcore.IAgentNode)
	 */
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

		LOG.debug("Stopped {} agents on agent node [node={}]", agentNode
				.findAgents().size(), agentNode);
		LOG.debug("Stopped agent node [node={}]", agentNode);
	}
}
