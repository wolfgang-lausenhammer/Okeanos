package okeanos.runner.internal.samples.helloworld;

import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;

/**
 * HelloWorldBean provides the functionality to the hello world agent. Prints a
 * simple hello world at every execution interval.
 */
@Component
public class HelloWorldBean extends AbstractAgentBean {
	/** The execution interval. */
	private static final int EXECUTION_INTERVAL = 5000;
	/** The logger. */
	@Logging
	private Logger log;

	/**
	 * Instantiates a new hello world bean.
	 */
	public HelloWorldBean() {
		this.setExecutionInterval(EXECUTION_INTERVAL);
	}

	/**
	 * Prints hello world and gives the string representation of the agent when
	 * called.
	 */
	public void execute() {
		if (log != null) {
			log.debug("-------------------------------");
		}
		if (log != null) {
			log.debug("Just wanted to say hello world!");
		}
		if (log != null) {
			log.debug("-------------------------------");
		}
		if (log != null) {
			log.debug("agent [{}]", this.thisAgent);
		}
	}
}
