package okeanos.runner.internal.samples.helloworld;

import static okeanos.runner.internal.samples.misc.startup.StartUpHelper.startAgentNode;
import static okeanos.runner.internal.samples.misc.startup.StartUpHelper.startEntity;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.PlatformManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(HelloWorld.class);

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

		IAgentNode node = startAgentNode(platformManagementService);
		Entity entity = startEntity(entityManagementService, node,
				"hello-world");

		if (LOG != null) {
			LOG.debug("Adding Hello World functionality to entity [{}]", entity);
		}
		entity.addFunctionality(beanProvider.get());
		if (LOG != null) {
			LOG.debug(
					"Finished adding Hello World functionality to entity [{}]",
					entity);
		}
	}
}
