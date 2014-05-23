package okeanos.core.entities;

import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

/**
 * Specifies the common methods for entities. Entity is a wrapper around agents
 * providing additional convenience features such as joining or leaving of a
 * group and adding of additional functionality to an agent ("AgentBean"s).
 * 
 * @author Wolfgang Lausenhammer
 */
public interface Entity {

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the agent.
	 * 
	 * @return the agent
	 */
	IAgent getAgent();

	/**
	 * Sets the agent.
	 * 
	 * @param agent
	 *            the new agent
	 */
	void setAgent(IAgent agent);

	/**
	 * Adds an agent bean (provides the functionality) to the entity.
	 * 
	 * @param functionality
	 *            the functionality represented by an agent bean
	 * @throws LifecycleException
	 *             the lifecycle exception
	 */
	void addFunctionality(IAgentBean... functionality)
			throws LifecycleException;

	/**
	 * Makes the entity join a group. An entity can belong to several groups.
	 * 
	 * @param group
	 *            the group to join
	 */
	void joinGroup(Group group);

	/**
	 * Makes the entity to leave a group. Does nothing, if the entity does not
	 * belong to the given group.
	 * 
	 * @param group
	 *            the group to leave
	 */
	void leaveGroup(Group group);
	
	void reset();
}
