package okeanos.management.internal.services.entitymanagement;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import okeanos.data.services.TimeService;
import okeanos.spring.misc.stereotypes.ChildOf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.knowledge.IMemory;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * Base class for all agents. Overwrites the run method of the default agent
 * defined by JIAC to account for the time service and to be simulation ready.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@ChildOf(parent = "NonBlockingAgent")
@Scope("prototype")
public class OkeanosBasicAgent extends Agent {

	/** The Constant EXECUTION_INTERVAL. */
	private static final int EXECUTION_INTERVAL = 10;

	/** The active. */
	private boolean active;

	/** The execution future. */
	private Future<?> executionFuture;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(OkeanosBasicAgent.class);

	/** The time service. */
	private TimeService timeService;

	/**
	 * Instantiates a new okeanos basic agent.
	 * 
	 * @param timeService
	 *            the time service
	 * @param communication
	 *            the communication
	 * @param memory
	 *            the memory
	 */
	@Inject
	public OkeanosBasicAgent(final TimeService timeService,
			final ICommunicationBean communication, final IMemory memory) {
		super();
		this.timeService = timeService;
		setCommunication(communication);
		setMemory(memory);
		setExecutionInterval(EXECUTION_INTERVAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.Agent#doInit()
	 */
	@Override
	public void doInit() throws LifecycleException {
		super.doInit();

		setExecutionInterval(EXECUTION_INTERVAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.Agent#doCleanup()
	 */
	@Override
	public void doCleanup() throws LifecycleException {
		synchronized (this) {
			if (executionFuture != null) {
				executionFuture.cancel(true);
				executionFuture = null;
			}
		}
		super.doCleanup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.Agent#doStart()
	 */
	@Override
	public void doStart() throws LifecycleException {
		super.doStart();
		active = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.Agent#doStop()
	 */
	@Override
	public void doStop() throws LifecycleException {
		synchronized (this) {
			active = false;
			if (executionFuture != null) {
				executionFuture.cancel(false);
			}
		}
		super.doStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dailab.jiactng.agentcore.Agent#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				timeService.sleep(getExecutionInterval());
				synchronized (this) {
					if (active) {
						executionFuture = getAgentNode().getThreadPool()
								.submit(getExecution());
						final FutureTask<?> t = ((FutureTask<?>) executionFuture);
						try {
							t.get(getBeanExecutionTimeout(),
									TimeUnit.MILLISECONDS);
						} catch (TimeoutException to) {
							LOG.error("ExecutionCycle did not return: ", to);
							t.cancel(true);
							this.stop();
						}
					} else {
						break;
					}
				}
			} catch (Exception e) {
				LOG.error("Critical error in controlcycle of agent: "
						+ getAgentName() + ". Stopping Agent! Exception: ", e);

				try {
					this.stop();
				} catch (LifecycleException lex) {
					LOG.error("Agent " + getAgentName()
							+ " could not be stopped because of: {}", lex);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"OkeanosBasicAgent [getAgentName()=%s, getAgentId()=%s]",
				getAgentName(), getAgentId());
	}

}
