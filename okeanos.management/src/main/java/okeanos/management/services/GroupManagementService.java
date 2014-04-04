package okeanos.management.services;

import okeanos.core.entities.Group;
import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * Provides methods for managing groups. That is, similar to the
 * {@link EntityManagementService} provides lifecycle methods like loading,
 * starting, stopping and unloading of groups.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface GroupManagementService {
	Group getGroup(String id);

	Group loadGroup();

	Group startGroup(Group group) throws LifecycleException;

	Group startGroup(Group group, IAgentNode node) throws LifecycleException;

	Group stopGroup(Group group) throws LifecycleException;

	void unloadGroup(Group group) throws LifecycleException;
}
