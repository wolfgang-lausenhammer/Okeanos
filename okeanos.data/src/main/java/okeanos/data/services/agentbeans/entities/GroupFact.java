package okeanos.data.services.agentbeans.entities;

import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * The Class GroupFact.
 */
public class GroupFact implements IFact {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3909442614695738721L;

	/** The group id. */
	private String groupId;

	/**
	 * Instantiates a new group fact.
	 * 
	 * @param groupId
	 *            the group id
	 */
	public GroupFact(final String groupId) {
		this.groupId = groupId;
	}

	/**
	 * Gets the group id.
	 * 
	 * @return the group id
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Sets the group id.
	 * 
	 * @param groupId
	 *            the new group id
	 */
	public void setGroupId(final String groupId) {
		this.groupId = groupId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("GroupFact [groupId=%s]", groupId);
	}
}
