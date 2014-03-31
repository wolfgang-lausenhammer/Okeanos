package okeanos.runner.internal.samples.helloworld;

import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;

@Component
public class HelloWorldBean extends AbstractAgentBean {
	@Logging
	private Logger log;

	public HelloWorldBean() {
		this.setExecutionInterval(5000);
	}

	public void execute() {
		if (log != null)
			log.debug("-------------------------------");
		if (log != null)
			log.debug("Just wanted to say hello world!");
		if (log != null)
			log.debug("-------------------------------");
		if (log != null)
			log.debug("agent [{}]", this.thisAgent);
	}
}
