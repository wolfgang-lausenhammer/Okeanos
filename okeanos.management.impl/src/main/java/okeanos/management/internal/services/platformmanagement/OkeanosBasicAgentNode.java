package okeanos.management.internal.services.platformmanagement;

import java.util.Arrays;

import javax.inject.Inject;

import okeanos.spring.misc.stereotypes.ChildOf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNodeBean;
import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.comm.broker.ActiveMQBroker;
import de.dailab.jiactng.agentcore.directory.DirectoryAgentNodeBean;

/**
 * The Class OkeanosBasicAgentNode.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@ChildOf(parent = "MyNodeWithDirectory")
@Scope("prototype")
public class OkeanosBasicAgentNode extends SimpleAgentNode {

	/**
	 * Instantiates a new okeanos basic agent node.
	 */
	public OkeanosBasicAgentNode() {
		super();
	}
}
