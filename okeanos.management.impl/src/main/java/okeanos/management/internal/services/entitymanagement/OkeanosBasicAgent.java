package okeanos.management.internal.services.entitymanagement;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import okeanos.data.services.TimeService;
import okeanos.management.internal.spring.stereotypes.ChildOf;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
@ChildOf(parent = "SimpleAgent")
@Scope("prototype")
public class OkeanosBasicAgent extends Agent {
	private boolean active;
	private Future<?> executionFuture;
	@Logging
	private Logger log;
	private TimeService timeService;

	@Inject
	public OkeanosBasicAgent(TimeService timeService) {
		super();
		this.timeService = timeService;
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
		active = true;
		super.doStart();
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
							if (log != null)
								log.error("ExecutionCycle did not return: ", to);
							t.cancel(true);
							this.stop();
						}
					} else {
						break;
					}
				}
			} catch (Exception e) {
				if (log != null)
					log.error("Critical error in controlcycle of agent: "
							+ getAgentName() + ". Stopping Agent! Exception: ",
							e);

				try {
					this.stop();
				} catch (LifecycleException lex) {
					if (log != null)
						log.error("Agent " + getAgentName()
								+ " could not be stopped because of: {}", lex);
				}
			}
		}
	}

}
