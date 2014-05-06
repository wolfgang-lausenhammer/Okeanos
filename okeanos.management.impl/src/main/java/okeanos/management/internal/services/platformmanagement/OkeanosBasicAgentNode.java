package okeanos.management.internal.services.platformmanagement;

import okeanos.spring.misc.stereotypes.ChildOf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.SimpleAgentNode;

/**
 * The Class OkeanosBasicAgentNode.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@ChildOf(parent = "NodeWithDirectory")
@Scope("prototype")
public class OkeanosBasicAgentNode extends SimpleAgentNode {

	/**
	 * Instantiates a new okeanos basic agent node.
	 */
	public OkeanosBasicAgentNode() {
		super();
	}
}
