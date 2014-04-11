package okeanos.runner.internal.samples.twoagents.beans;

import java.io.Serializable;

import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.runner.internal.samples.twoagents.beans.entities.Ping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;

/**
 * Sends a {@link Ping} to another agent, where it will be received by the
 * {@link PongBean}, which eventually sends a {@link Ping} with "Pong" as the
 * message.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class PingBean extends AbstractAgentBean {
	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 5000;

	/** The logger. */
	private static final Logger LOG = LoggerFactory.getLogger(PingBean.class);

	/** The communication action to send messages to other agents. */
	private IActionDescription actionSend;

	/**
	 * Instantiates a new ping bean.
	 */
	public PingBean() {
		setExecutionInterval(EXECUTION_INTERVAL);
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
				CommunicationServiceAgentBean.ACTION_SEND_STRING);
		actionSend = memory.read(template);
		if (actionSend == null) {
			actionSend = thisAgent.searchAction(template);
		}
	}

	/**
	 * The actual work happens here. Sends a ping and waits for the answer
	 * (synchronously).
	 */
	@Override
	public void execute() {
		if (LOG != null) {
			LOG.info("PingAgent - execute() called");
		}

		if (LOG != null) {
			LOG.info("PingAgent - sending ping");
		}

		IJiacMessage answer = (IJiacMessage) invokeAndWaitForResult(actionSend,
				new Serializable[] { "pong-agent", new Ping("ping") })
				.getResults()[0];

		if (LOG != null) {
			LOG.info("PingAgent - received answer [answer={}]", answer);
		}
	}
}
