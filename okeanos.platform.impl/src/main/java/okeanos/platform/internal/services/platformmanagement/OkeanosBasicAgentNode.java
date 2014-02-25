package okeanos.platform.internal.services.platformmanagement;

import okeanos.platform.internal.spring.stereotype.ChildOf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.SimpleAgentNode;

@Component
@ChildOf(parent = "NodeWithDirectory")
@Scope("prototype")
public class OkeanosBasicAgentNode extends SimpleAgentNode {

}
