package okeanos.management.internal.services.entitymanagement;

import okeanos.management.internal.spring.stereotype.ChildOf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.Agent;

@Component
@ChildOf(parent = "SimpleAgent")
@Scope("prototype")
public class OkeanosBasicAgent extends Agent {

}
