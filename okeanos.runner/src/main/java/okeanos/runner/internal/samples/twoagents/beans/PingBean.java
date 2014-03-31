package okeanos.runner.internal.samples.twoagents.beans;

import javax.inject.Inject;

import okeanos.data.services.CommunicationService;
import okeanos.runner.internal.samples.twoagents.beans.entities.Ping;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;

@Component
@Scope("prototype")
public class PingBean extends AbstractAgentBean {
	@Logging
	private Logger log;
	private CommunicationService communicationService;

	@Inject
	public PingBean(CommunicationService communicationService) {
		this.communicationService = communicationService;

		setExecutionInterval(1000);
		if (log != null)
			log.warn("PingBean created");
	}

	@Override
	public void execute() {
		if (log != null)
			log.info("execute() on PingBean called");
		try {
			if (log != null)
				log.info("PingAgent - sending ping");
			IJiacMessage answer = communicationService.send(this, "PongAgent",
					new Ping("ping"));
			if (log != null)
				log.info("got answer [answer={}]", answer);
		} catch (CommunicationException e) {
			if (log != null)
				log.error("error sending ping message");
		}
	}
}
