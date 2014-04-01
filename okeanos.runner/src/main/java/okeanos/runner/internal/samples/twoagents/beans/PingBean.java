package okeanos.runner.internal.samples.twoagents.beans;

import javax.inject.Inject;

import okeanos.data.services.CommunicationService;
import okeanos.runner.internal.samples.twoagents.beans.entities.Ping;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;

/**
 * Sends a {@link Ping} to another agent, where it will be received by the
 * {@link PongBean}, which eventually sends a {@link Ping} with "Pong" as the
 * message.
 */
@Component
@Scope("prototype")
public class PingBean extends AbstractAgentBean {
	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 1000;

	/** The logger. */
	@Logging
	private Logger log;

	/** The communication service to send messages to other agents. */
	private CommunicationService communicationService;

	/**
	 * Instantiates a new ping bean.
	 * 
	 * @param communicationService
	 *            the communication service
	 */
	@Inject
	public PingBean(final CommunicationService communicationService) {
		this.communicationService = communicationService;

		setExecutionInterval(EXECUTION_INTERVAL);
		if (log != null) {
			log.warn("PingBean created");
		}
	}

	/**
	 * The actual work happens here. Sends a ping and waits for the answer
	 * (synchronously).
	 */
	@Override
	public void execute() {
		if (log != null) {
			log.info("execute() on PingBean called");
		}
		try {
			if (log != null) {
				log.info("PingAgent - sending ping");
			}
			IJiacMessage answer = communicationService.send(this, "PongAgent",
					new Ping("ping"));
			if (log != null) {
				log.info("got answer [answer={}]", answer);
			}
		} catch (CommunicationException e) {
			if (log != null) {
				log.error("error sending ping message");
			}
		}
	}
}
