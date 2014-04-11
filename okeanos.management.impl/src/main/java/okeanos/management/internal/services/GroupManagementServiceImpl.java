package okeanos.management.internal.services;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.core.entities.Group;
import okeanos.core.entities.builder.GroupBuilder;
import okeanos.management.services.EntityManagementService;
import okeanos.management.services.GroupManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * Provides methods for managing groups. That is, similar to the
 * {@link EntityManagementService} provides lifecycle methods like loading,
 * starting, stopping and unloading of groups.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
public class GroupManagementServiceImpl implements GroupManagementService {

	/** The entity management service. */
	private EntityManagementService entityManagementService;

	/** The group builder provider. */
	private Provider<GroupBuilder> groupBuilderProvider;

	/** The logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(GroupManagementServiceImpl.class);

	/**
	 * Instantiates a new group management service.
	 * 
	 * @param entityManagementService
	 *            the entity management service
	 * @param entityBuilderProvider
	 *            the entity builder provider
	 * @param groupBuilderProvider
	 *            the group builder provider
	 */
	@Inject
	public GroupManagementServiceImpl(
			final EntityManagementService entityManagementService,
			final Provider<GroupBuilder> groupBuilderProvider) {
		this.entityManagementService = entityManagementService;
		this.groupBuilderProvider = groupBuilderProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.GroupManagementService#getGroup(java.lang
	 * .String)
	 */
	@Override
	public Group getGroup(String group) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.management.services.GroupManagementService#loadGroup()
	 */
	@Override
	public Group loadGroup() {
		LOG.debug("Loading group with default configuration");
		Entity entity = entityManagementService.loadEntity();
		GroupBuilder builder = loadConfigurableGroup();
		builder.entity(entity);
		LOG.debug("Successfully loaded group with default configuration");

		return builder.build();
	}

	/**
	 * Load configurable group.
	 * 
	 * @return the group builder
	 */
	public GroupBuilder loadConfigurableGroup() {
		return groupBuilderProvider.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.GroupManagementService#startGroup(okeanos
	 * .core.entities.Group)
	 */
	@Override
	public Group startGroup(Group group) throws LifecycleException {
		return (Group) entityManagementService.startEntity(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.GroupManagementService#startGroup(okeanos
	 * .core.entities.Group, de.dailab.jiactng.agentcore.IAgentNode)
	 */
	@Override
	public Group startGroup(Group group, IAgentNode node)
			throws LifecycleException {
		return (Group) entityManagementService.startEntity(group, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.GroupManagementService#stopGroup(okeanos.
	 * core.entities.Group)
	 */
	@Override
	public Group stopGroup(Group group) throws LifecycleException {
		return (Group) entityManagementService.stopEntity(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.management.services.GroupManagementService#unloadGroup(okeanos
	 * .core.entities.Group)
	 */
	@Override
	public void unloadGroup(Group group) throws LifecycleException {
		entityManagementService.unloadEntity(group);
	}

}
