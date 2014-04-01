package okeanos.runner.internal.samples.simple.loadreporting.beans;

import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;

/**
 * The Class LightBulbBean.
 */
@Component
public class LightBulbBean extends AbstractAgentBean {
	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 5000;

	/** The log. */
	@Logging
	private Logger log;

	/**
	 * Instantiates a new light bulb bean.
	 */
	public LightBulbBean() {
		this.setExecutionInterval(EXECUTION_INTERVAL);
	}

}
