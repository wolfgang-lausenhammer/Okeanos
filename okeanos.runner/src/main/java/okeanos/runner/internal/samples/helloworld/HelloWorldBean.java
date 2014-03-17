package okeanos.runner.internal.samples.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;

@Component
public class HelloWorldBean extends AbstractAgentBean {
	private static final Logger log = LoggerFactory.getLogger(HelloWorldBean.class);
	
	public HelloWorldBean() {
		this.setExecutionInterval(5000);
	}
	
	public void execute() {
		log.debug("-------------------------------");
		log.debug("Just wanted to say hello world!");
		log.debug("-------------------------------");
		log.debug("agent [{}]", this.thisAgent);
	}
}
