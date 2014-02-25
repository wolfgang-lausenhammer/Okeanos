package okeanos.platform.internal.services;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Inject;

import okeanos.platform.internal.services.platformmanagement.OkeanosBasicAgentNode;
import okeanos.platform.services.LoggingService;
import okeanos.platform.services.PlatformManagementService;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
public class PlatformManagementServiceImpl implements PlatformManagementService {
	private Set<IAgentNode> managedAgentNodes = new ConcurrentSkipListSet<>();

	@Inject
	private LoggingService log;

	@Inject
	private ApplicationContext context;

	@Override
	public IAgentNode startAgentNode() throws LifecycleException {
		IAgentNode node = context.getBean(OkeanosBasicAgentNode.class);
		managedAgentNodes.add(node);
		// node will be started by spring!
		// node.start();
		return node;
	}

	@Override
	public void stopAgentNode(IAgentNode agentNode) throws LifecycleException {
		if (agentNode instanceof SimpleAgentNode) {
			SimpleAgentNode node = (SimpleAgentNode) agentNode;
			node.shutdown();
		} else {
			agentNode.stop();
			agentNode.cleanup();
		}
	}
}
