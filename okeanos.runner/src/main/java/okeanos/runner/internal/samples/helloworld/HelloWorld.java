package okeanos.runner.internal.samples.helloworld;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * A simple hello world sample which demonstrates the basic interaction and
 * configuration of Okeanos.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
public class HelloWorld {

	/** The logger. */
	@Logging
	private Logger log;

	/** The entity management service. */
	private EntityManagementService entityManagementService;

	/** The platform management service. */
	private PlatformManagementService platformManagementService;

	/**
	 * Constructor. Creates an agent node and starts an entity on that node.
	 * Finally, adding the hello world functionality to the entity, which in
	 * turn then prints hello world.
	 * 
	 * @param platformManagementService
	 *            the platform management service
	 * @param entityManagementService
	 *            the entity management service
	 * @param beanProvider
	 *            the bean provider
	 * @throws LifecycleException
	 *             if the entity faces any troubles while starting its agent up
	 */
	@Inject
	public HelloWorld(
			final PlatformManagementService platformManagementService,
			final EntityManagementService entityManagementService,
			final Provider<HelloWorldBean> beanProvider)
			throws LifecycleException {
		this.platformManagementService = platformManagementService;
		this.entityManagementService = entityManagementService;

		IAgentNode node = startAgentNode();
		Entity entity = startEntity(node);

		if (log != null) {
			log.debug("Adding Hello World functionality to entity [{}]", entity);
		}
		entity.addFunctionality(beanProvider.get());
		if (log != null) {
			log.debug(
					"Finished adding Hello World functionality to entity [{}]",
					entity);
		}
	}

	/**
	 * Start an agent node.
	 * 
	 * @return the agent node
	 * @throws LifecycleException
	 *             if the agent node faces any problems while starting up
	 */
	private IAgentNode startAgentNode() throws LifecycleException {
		if (log != null) {
			log.debug("Starting Agent Node");
		}
		IAgentNode node = platformManagementService.startAgentNode();
		if (log != null) {
			log.debug("Finished starting Agent Node [{}]", node);
		}

		return node;
	}

	/**
	 * Start an entity.
	 * 
	 * @param node
	 *            the node on which the entity should be started on
	 * @return the entity
	 * @throws LifecycleException
	 *             if the entity faces any problems while starting its agent up
	 */
	private Entity startEntity(final IAgentNode node) throws LifecycleException {
		if (log != null) {
			log.debug("Starting Entity on Agent Node [{}]", node);
		}
		Entity entity = entityManagementService.loadEntity();
		entityManagementService.startEntity(entity, node);
		if (log != null) {
			log.debug("Finished starting Entity [{}] on Agent Node[{}]",
					entity, node);
		}

		return entity;
	}
}
