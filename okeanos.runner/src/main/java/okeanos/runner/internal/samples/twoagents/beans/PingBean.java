package okeanos.runner.internal.samples.twoagents.beans;

import javax.inject.Inject;

import okeanos.data.services.CommunicationService;
import okeanos.runner.internal.samples.twoagents.beans.entities.Ping;

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
	private static final Logger log = LoggerFactory.getLogger(PingBean.class);
	private CommunicationService communicationService;

	@Inject
	public PingBean(CommunicationService communicationService) {
		this.communicationService = communicationService;

		setExecutionInterval(1000);
		log.info("PingBean created");
	}

	@Override
	public void execute() {
		log.info("execute() on PingBean called");
		try {
			log.info("PingAgent - sending ping");
			IJiacMessage answer = communicationService.send(this, "PongAgent",
					new Ping("ping"));
			log.info("got answer [answer={}]", answer);
		} catch (CommunicationException e) {
			log.error("error sending ping message");
		}
	}
}
