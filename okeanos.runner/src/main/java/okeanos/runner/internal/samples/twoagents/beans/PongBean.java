package okeanos.runner.internal.samples.twoagents.beans;

import java.io.Serializable;
import java.util.HashMap;

import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.runner.internal.samples.twoagents.beans.entities.Ping;

import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;

/**
 * Provides the receiving of pings functionality to agents. Waits for a
 * {@link Ping} to arrive and answers with a {@link Ping} with message "Pong".
 * 
 * @author Wolfgang Lausenhammer
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

		/** The action send async. */
		private IActionDescription actionSendAsyncOptions;

		/**
		 * This method is called when information about an PingMessage which was
		 * previously requested using an asynchronous interface becomes
		 * available.
		 */
		public PingMessageObserver() {
			IActionDescription template = new Action(
					CommunicationServiceAgentBean.ACTION_SEND_ASYNC_OPTIONS);
			actionSendAsyncOptions = memory.read(template);
			if (actionSendAsyncOptions == null) {
				actionSendAsyncOptions = thisAgent.searchAction(template);
			}
		}

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
				if (LOG != null) {
					LOG.info("PongAgent - ping received");
				}

				// consume message
				IJiacMessage message = memory.remove(wce.getObject());

				// create answer: a JiacMessage holding a Ping with message
				// "pong"
				Ping answer = new Ping("pong");

				// send Pong to PingAgent (the sender of the original message)
				if (LOG != null) {
					LOG.info("PongAgent - sending pong message");
				}

				HashMap<String, String> options = new HashMap<>();
				options.put("OkeanosCommunicationCorrelationId",
						message.getHeader("OkeanosCommunicationCorrelationId"));

				invoke(actionSendAsyncOptions,
						new Serializable[] { message.getSender(), answer,
								options });
			}
		}
	}

	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 0;

	/** The logger. */
	private static final Logger LOG = LoggerFactory.getLogger(PongBean.class);

	/** The communication action to register a callback handler. */
	private IActionDescription actionReceiveMessageCallbackIfact;

	/**
	 * Instantiates a new pong bean.
	 * 
	 */
	public PongBean() {
		setExecutionInterval(EXECUTION_INTERVAL); // disable execution
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.AbstractAgentBean#doStart()
	 */
	@Override
	public void doStart() throws Exception {
		super.doStart();

		IActionDescription template = new Action(
				CommunicationServiceAgentBean.ACTION_RECEIVE_MESSAGE_CALLBACK_IFACT);
		actionReceiveMessageCallbackIfact = memory.read(template);
		if (actionReceiveMessageCallbackIfact == null) {
			actionReceiveMessageCallbackIfact = thisAgent
					.searchAction(template);
		}
		invoke(actionReceiveMessageCallbackIfact, new Serializable[] {
				new PingMessageObserver(), new Ping("ping") });
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
