package okeanos.data.services.agentbeans.entities;

/**
 * The Class GridFact.
 */
public class GridFact extends GroupFact {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9084166181438917951L;

	/**
	 * Instantiates a new grid fact.
	 * 
	 * @param groupId
	 *            the group id
	 */
	public GridFact(final String groupId) {
		super(groupId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.agentbeans.entities.GroupFact#toString()
	 */
	@Override
	public String toString() {
		return String.format("GridFact [toString()=%s]", super.toString());
	}
}
