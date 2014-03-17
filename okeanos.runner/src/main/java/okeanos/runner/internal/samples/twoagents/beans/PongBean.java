package okeanos.runner.internal.samples.twoagents.beans;

import javax.inject.Inject;

import okeanos.data.services.CommunicationService;
import okeanos.runner.internal.samples.twoagents.beans.entities.Ping;

import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;

@Component
@Scope("prototype")
public class PongBean extends AbstractAgentBean {
	private class PingMessageObserver implements SpaceObserver<IFact> {
		private static final long serialVersionUID = -627146953559709337L;

		@SuppressWarnings("unchecked")
		@Override
		public void notify(SpaceEvent<? extends IFact> event) {
			if (event instanceof WriteCallEvent<?>) {
				WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;
				// a JiacMessage holding a Ping with message "ping" has been
				// written to this agent's memory
				log.info("PongAgent - ping received");

				// consume message
				IJiacMessage message = memory.remove(wce.getObject());

				// create answer: a JiacMessage holding a Ping with message
				// "pong"
				Ping answer = new Ping("pong");

				// send Pong to PingAgent (the sender of the original message)
				log.info("PongAgent - sending pong message");
				try {
					communicationService.sendAsync(PongBean.this,
							message.getSender(), answer);
				} catch (CommunicationException e) {
					log.error("error sending pong message");
				}
			}
		}
	}

	private static final Logger log = LoggerFactory.getLogger(PongBean.class);

	private CommunicationService communicationService;

	@Inject
	public PongBean(CommunicationService communicationService) {
		this.communicationService = communicationService;

		setExecutionInterval(1000);
		System.out.println("PongBean created");
	}

	@Override
	public void doStart() throws Exception {
		super.doStart();
		communicationService.receiveMessageCallback(this,
				new PingMessageObserver(), new Ping("ping"));
	}

	@Override
	public void execute() {
		// System.out.println("execute() on PongBean called");
		// communicationService.send(new JiacMessage());
	}
}
