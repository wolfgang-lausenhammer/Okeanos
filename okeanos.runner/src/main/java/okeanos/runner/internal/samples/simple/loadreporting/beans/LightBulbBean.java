package okeanos.runner.internal.samples.simple.loadreporting.beans;

import java.io.Serializable;
import java.util.HashMap;

import javax.inject.Inject;

import okeanos.control.entities.Schedule;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import okeanos.data.services.entities.MessageScope;
import okeanos.model.entities.Load;
import okeanos.spring.misc.stereotypes.Logging;

import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;

/**
 * The light bulb bean that provides the main action to the light bulb agent.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class LightBulbBean extends AbstractAgentBean implements
		SpaceObserver<IFact> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5311309436889351318L;

	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 4000;

	/** The log. */
	@Logging
	private Logger log = LoggerFactory.getLogger(LightBulbBean.class);

	/** The communication action to broadcast messages. */
	private IActionDescription actionBroadcast;

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The light bulb model. */
	private Load lightBulb;

	/** The action send async options. */
	private IActionDescription actionSendAsyncOptions;

	/**
	 * Instantiates a new light bulb bean.
	 * 
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 * @param lightBulb
	 *            the light bulb
	 */
	@Inject
	public LightBulbBean(final ControlEntitiesProvider controlEntitiesProvider,
			@Qualifier("lightBulb100W") final Load lightBulb) {
		this.controlEntitiesProvider = controlEntitiesProvider;
		this.lightBulb = lightBulb;
		this.setExecutionInterval(EXECUTION_INTERVAL);
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
				CommunicationServiceAgentBean.ACTION_BROADCAST);
		actionBroadcast = memory.read(template);
		if (actionBroadcast == null) {
			actionBroadcast = thisAgent.searchAction(template);
		}

		template = new Action(
				CommunicationServiceAgentBean.ACTION_SEND_ASYNC_OPTIONS);
		actionSendAsyncOptions = memory.read(template);
		if (actionSendAsyncOptions == null) {
			actionSendAsyncOptions = thisAgent.searchAction(template);
		}

		template = new Action(
				CommunicationServiceAgentBean.ACTION_RECEIVE_MESSAGE_CALLBACK_IFACT);
		IActionDescription actionReceiveMessageCallbackIfact = memory
				.read(template);
		if (actionReceiveMessageCallbackIfact == null) {
			actionReceiveMessageCallbackIfact = thisAgent
					.searchAction(template);
		}
		log.error("LightBulbBean [{}] - error1??", thisAgent.getAgentName());
		Schedule schedule = new ScheduleImpl(null);
		invokeAndWaitForResult(actionReceiveMessageCallbackIfact,
				new Serializable[] { this, schedule });
		log.error("LightBulbBean [{}] - error2??", thisAgent.getAgentName());
	}

	/**
	 * The actual work happens here. Called once every
	 * {@link LightBulbBean#EXECUTION_INTERVAL} to get ready for the next
	 * iteration.
	 */
	@Override
	public void execute() {
		if (log != null) {
			log.info("LightBulbBean [{}] - execute() called",
					thisAgent.getAgentName());
		}

		Schedule schedule = new ScheduleImpl("abcccc");

		if (log != null) {
			log.info("LightBulbBean [{}] - announcing schedule",
					thisAgent.getAgentName());
		}
		invoke(actionBroadcast, new Serializable[] { MessageScope.GROUP,
				schedule });
		if (log != null) {
			log.info("LightBulbBean [{}] - announced schedule",
					thisAgent.getAgentName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sercho.masp.space.event.SpaceObserver#notify(org.sercho.masp.space
	 * .event.SpaceEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void notify(final SpaceEvent<? extends IFact> event) {
		log.info("LightBulbAgent [{}] - [event={}]", thisAgent.getAgentName(),
				event);
		if (event instanceof WriteCallEvent<?>) {
			WriteCallEvent<IJiacMessage> wce = (WriteCallEvent<IJiacMessage>) event;
			if (log != null) {
				log.info("LightBulbAgent [{}] - Broadcast Message received",
						thisAgent.getAgentName());
			}

			// consume message
			IJiacMessage message = memory.remove(wce.getObject());

			if (log != null) {
				log.info(
						"LightBulbAgent [{}] - doing something with [message={}]",
						thisAgent.getAgentName(), message);
			}

			HashMap<String, String> options = new HashMap<>();
			options.put("OkeanosCommunicationCorrelationId",
					message.getHeader("OkeanosCommunicationCorrelationId"));
			// invoke(actionSendAsyncOptions,
			// new Serializable[] { message.getSender(), answer, options });
		}
	}

}
