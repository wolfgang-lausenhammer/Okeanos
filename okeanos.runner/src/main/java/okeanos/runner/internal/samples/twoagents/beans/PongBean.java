package okeanos.runner.internal.samples.twoagents.beans;

import javax.inject.Inject;

import okeanos.data.services.CommunicationService;
import okeanos.runner.internal.samples.twoagents.beans.entities.Ping;
import okeanos.spring.misc.stereotypes.Logging;

import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * Provides the receiving of pings functionality to agents. Waits for a
 * {@link Ping} to arrive and answers with a {@link Ping} with message "Pong".
 */
@Component
@Scope("prototype")
public class PongBean extends AbstractAgentBean {

	/**
	 * An asynchronous update interface for receiving notifications about
	 * PingMessage information as the PingMessage is constructed.
	 */
	private class PingMessageObserver implements SpaceObserver<IFact> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -627146953559709337L;

		/**
		 * Is called everytime a {@link Ping} with content "ping" is received.
		 * Answers with a {@link Ping} with content "pong".
		 * 
		 * @param event
		 *            the event
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void notify(final SpaceEvent<? extends IFact> event) {
			if (event instanceof WriteCallEvent<?>) {
				WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;
				// a JiacMessage holding a Ping with message "ping" has been
				// written to this agent's memory
				if (log != null) {
					log.info("PongAgent - ping received");
				}

				// consume message
				IJiacMessage message = memory.remove(wce.getObject());

				// create answer: a JiacMessage holding a Ping with message
				// "pong"
				Ping answer = new Ping("pong");

				// send Pong to PingAgent (the sender of the original message)
				if (log != null) {
					log.info("PongAgent - sending pong message");
				}
				try {
					communicationService.sendAsync(PongBean.this,
							message.getSender(), answer);
				} catch (CommunicationException e) {
					if (log != null) {
						log.error("error sending pong message");
					}
				}
			}
		}
	}

	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 1000;

	/** The logger. */
	@Logging
	private Logger log;

	/** The communication service to send messages to other agents. */
	private CommunicationService communicationService;

	/**
	 * Instantiates a new pong bean.
	 * 
	 * @param communicationService
	 *            the communication service
	 */
	@Inject
	public PongBean(final CommunicationService communicationService) {
		this.communicationService = communicationService;

		setExecutionInterval(EXECUTION_INTERVAL);
		if (log != null) {
			log.info("PongBean created");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.AbstractAgentBean#doStart()
	 */
	@Override
	public void doStart() throws Exception {
		super.doStart();
		communicationService.receiveMessageCallback(this,
				new PingMessageObserver(), new Ping("ping"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.AbstractAgentBean#execute()
	 */
	@Override
	public void execute() {
		// System.out.println("execute() on PongBean called");
		// communicationService.send(new JiacMessage());
	}
}
