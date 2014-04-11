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

@Component
@ChildOf(parent = "NonBlockingAgent")
@Scope("prototype")
public class OkeanosBasicAgent extends Agent {
	private boolean active;
	private Future<?> executionFuture;
	private static final Logger LOG = LoggerFactory
			.getLogger(OkeanosBasicAgent.class);
	private TimeService timeService;

	@Inject
	public OkeanosBasicAgent(TimeService timeService,
			ICommunicationBean communication, IMemory memory) {
		super();
		this.timeService = timeService;
		setCommunication(communication);
		setMemory(memory);
		setExecutionInterval(1000);
	}

	@Override
	public void doInit() throws LifecycleException {
		super.doInit();

		setExecutionInterval(1000);
	}

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

	@Override
	public void doStart() throws LifecycleException {
		super.doStart();
		active = true;
	}

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

	@Override
	public String toString() {
		return String.format(
				"OkeanosBasicAgent [getAgentName()=%s, getAgentId()=%s]",
				getAgentName(), getAgentId());
	}

}
