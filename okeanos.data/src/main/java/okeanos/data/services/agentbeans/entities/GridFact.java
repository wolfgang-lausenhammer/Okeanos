package okeanos.data.services.agentbeans.entities;

public class GridFact extends GroupFact {
	public GridFact(String groupId) {
		super(groupId);
	}

	@Override
	public String toString() {
		return String.format("GridFact [toString()=%s]", super.toString());
	}

	private static final long serialVersionUID = -9084166181438917951L;
}
