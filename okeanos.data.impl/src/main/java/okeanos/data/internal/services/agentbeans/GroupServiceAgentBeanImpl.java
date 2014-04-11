package okeanos.data.internal.services.agentbeans;

import java.io.Serializable;
import java.util.Set;

import okeanos.data.services.agentbeans.GroupServiceAgentBean;
import okeanos.data.services.agentbeans.entities.GroupFact;

import org.sercho.masp.space.SimpleObjectSpace;
import org.sercho.masp.space.event.RemoveCallEvent;
import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;
import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.comm.CommunicationAddressFactory;
import de.dailab.jiactng.agentcore.comm.CommunicationBean;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;

/**
 * Provides an interface whose implementations are responsible for handling
 * group management events. That is group join and leave events will be
 * processed and listeners (de)registered as needed.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class GroupServiceAgentBeanImpl extends AbstractMethodExposingBean
		implements GroupServiceAgentBean, SpaceObserver<IFact> {

	/** The Constant log. */
	private static final Logger LOG = LoggerFactory
			.getLogger(GroupServiceAgentBeanImpl.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9170386178940753951L;

	/** The join group action. */
	private IActionDescription actionJoinGroup;

	/** The leave group action. */
	private IActionDescription actionLeaveGroup;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.dailab.jiactng.agentcore.action.AbstractActionAuthorizationBean#doStart
	 * ()
	 */
	@Override
	public void doStart() throws Exception {
		super.doStart();

		IActionDescription template = new Action(
				CommunicationBean.ACTION_JOIN_GROUP);
		actionJoinGroup = memory.read(template);
		if (actionJoinGroup == null) {
			actionJoinGroup = thisAgent.searchAction(template);
		}

		template = new Action(CommunicationBean.ACTION_LEAVE_GROUP);
		actionLeaveGroup = memory.read(template);
		if (actionLeaveGroup == null) {
			actionLeaveGroup = thisAgent.searchAction(template);
		}

		Set<GroupFact> groups = memory.readAll(new GroupFact(null));
		for (GroupFact group : groups) {
			notify(new WriteCallEvent<IFact>(new SimpleObjectSpace<>("id"),
					group));
		}

		memory.attach(this, new GroupFact(null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sercho.masp.space.event.SpaceObserver#notify(org.sercho.masp.space
	 * .event.SpaceEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void notify(final SpaceEvent<? extends IFact> event) {
		if (event instanceof WriteCallEvent<?>) {
			WriteCallEvent<GroupFact> wce = (WriteCallEvent<GroupFact>) event;
			LOG.info(
					"GroupServiceAgentBeanImpl [{}] - Group join event raised [{}]",
					thisAgent.getAgentName(), wce);

			// consume message
			GroupFact groupFact = wce.getObject();
			invoke(actionJoinGroup,
					new Serializable[] { CommunicationAddressFactory
							.createGroupAddress(groupFact.getGroupId()) });

		} else if (event instanceof RemoveCallEvent<?>) {
			RemoveCallEvent<GroupFact> rce = (RemoveCallEvent<GroupFact>) event;
			LOG.info(
					"GroupServiceAgentBeanImpl [{}] - Group leave event raised [{}]",
					thisAgent.getAgentName(), rce);

			// consume message
			GroupFact groupFact = rce.getRemoved();
			invoke(actionLeaveGroup,
					new Serializable[] { CommunicationAddressFactory
							.createGroupAddress(groupFact.getGroupId()) });
		}
	}

}
