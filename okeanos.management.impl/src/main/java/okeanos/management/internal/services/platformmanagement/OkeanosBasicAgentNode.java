package okeanos.management.internal.services.platformmanagement;

import okeanos.management.internal.spring.stereotype.ChildOf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.SimpleAgentNode;

@Component
@ChildOf(parent = "NodeWithDirectory")
@Scope("prototype")
public class OkeanosBasicAgentNode extends SimpleAgentNode {
	public OkeanosBasicAgentNode() {
		super();
	}
}
