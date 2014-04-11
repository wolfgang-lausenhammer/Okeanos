package okeanos.runner.internal.samples.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;

/**
 * HelloWorldBean provides the functionality to the hello world agent. Prints a
 * simple hello world at every execution interval.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
public class HelloWorldBean extends AbstractAgentBean {
	/** The execution interval. */
	private static final int EXECUTION_INTERVAL = 5000;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(HelloWorldBean.class);

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
		LOG.debug("-------------------------------");
		LOG.debug("Just wanted to say hello world!");
		LOG.debug("-------------------------------");
		LOG.debug("agent [{}]", this.thisAgent);
	}
}
