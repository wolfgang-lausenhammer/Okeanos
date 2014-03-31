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

@Component
public class HelloWorld {
	@Logging
	private Logger log;

	private EntityManagementService entityManagementService;

	private PlatformManagementService platformManagementService;

	@Inject
	public HelloWorld(PlatformManagementService platformManagementService,
			EntityManagementService entityManagementService,
			Provider<HelloWorldBean> beanProvider) throws LifecycleException {
		this.platformManagementService = platformManagementService;
		this.entityManagementService = entityManagementService;

		IAgentNode node = startAgentNode();
		Entity entity = startEntity(node);

		if (log != null)
			log.debug("Adding Hello World functionality to entity [{}]", entity);
		entity.addFunctionality(beanProvider.get());
		if (log != null)
			log.debug(
					"Finished adding Hello World functionality to entity [{}]",
					entity);
	}

	private IAgentNode startAgentNode() throws LifecycleException {
		if (log != null)
			log.debug("Starting Agent Node");
		IAgentNode node = platformManagementService.startAgentNode();
		if (log != null)
			log.debug("Finished starting Agent Node [{}]", node);

		return node;
	}

	private Entity startEntity(IAgentNode node) throws LifecycleException {
		if (log != null)
			log.debug("Starting Entity on Agent Node [{}]", node);
		Entity entity = entityManagementService.loadEntity();
		entityManagementService.startEntity(entity, node);
		if (log != null)
			log.debug("Finished starting Entity [{}] on Agent Node[{}]",
					entity, node);

		return entity;
	}
}
