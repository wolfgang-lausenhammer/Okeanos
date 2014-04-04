package okeanos.data.services.agentbeans.entities;

import de.dailab.jiactng.agentcore.knowledge.IFact;

public class GroupFact implements IFact {
	@Override
	public String toString() {
		return String.format("GroupFact [groupId=%s]", groupId);
	}

	private static final long serialVersionUID = 3909442614695738721L;

	private String groupId;

	public GroupFact(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
