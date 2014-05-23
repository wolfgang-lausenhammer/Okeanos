package okeanos.core.internal.entities;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;
import okeanos.core.entities.Entity;
import okeanos.core.entities.Group;
import okeanos.core.entities.builder.GroupBuilder;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.action.AbstractMethodExposingBean;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;

/**
 * The Class GroupImpl.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class GroupImpl extends EntityImpl implements Group {

	/**
	 * Represents a group builder. Can be used to create new groups in a fluent
	 * API.
	 */
	@Component("groupBuilder")
	@Scope("prototype")
	public static class GroupBuilderImpl implements GroupBuilder {
		/* optional */
		/** The entity. */
		private Entity entity;

		/* mandatory */
		/** The id. */
		private final String id;

		/**
		 * Instantiates a new group builder.
		 * 
		 * @param id
		 *            the id
		 */
		@Inject
		public GroupBuilderImpl(
				@Value("#{ uuidGenerator.generateUUID() }") final String id) {
			this.id = id;
			if (id == null) {
				throw new NullPointerException(
						"ID is null, check if Spring Expression term is right");
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see okeanos.core.entities.builder.EntityBuilder#build()
		 */
		@Override
		public Group build() {
			return new GroupImpl(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.core.entities.builder.EntityBuilder#agent(de.dailab.jiactng
		 * .agentcore.IAgent)
		 */
		@Override
		public GroupBuilder entity(final Entity entity) {
			this.entity = entity;

			return this;
		}

	}
	/**
	 * The Class ProxyScheduleHandlerServiceAgentBean.
	 */
	private class ProxyScheduleHandlerServiceAgentBean extends
			AbstractMethodExposingBean implements
			ScheduleHandlerServiceAgentBean {

		/** The action is equilibrium reached. */
		private IActionDescription actionIsEquilibriumReached;

		/** The action reset. */
		private IActionDescription actionReset;

		/** The action send. */
		private IActionDescription actionSend;

		/**
		 * Instantiates a new proxy schedule handler service agent bean.
		 */
		public ProxyScheduleHandlerServiceAgentBean() {
			// actionReset = getAction(ACTION_RESET);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean
		 * #isEquilibriumReached()
		 */
		@Override
		public boolean isEquilibriumReached() {
			return false;
			// if (actionIsEquilibriumReached == null) {
			// actionIsEquilibriumReached =
			// getAction(ACTION_IS_EQUILIBRIUM_REACHED);
			// }
			// if (actionIsEquilibriumReached == null) {
			// LOG.trace("{} - No action called {} available", getAgent(),
			// ACTION_IS_EQUILIBRIUM_REACHED);
			// return false;
			// }
			//
			// return (Boolean) AbstractAgentBeanProtectedMethodPublisher
			// .invokeAndWaitForResult((AbstractAgentBean) getAgent(),
			// actionIsEquilibriumReached, new Serializable[] {})
			// .getResults()[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean
		 * #reset(boolean)
		 */
		@Override
		public void reset(final boolean cancelRunningOperation) {
			Thread[] threads = new Thread[entities.size()];
			int i = 0;
			for (final Entity entity : entities) {
				threads[i] = new Thread(new Runnable() {

					@Override
					public void run() {
						entity.reset();
					}
				});
				threads[i].start();
				i++;
			}

			for (i = 0; i < threads.length; i++) {
				try {
					threads[i].join();
				} catch (InterruptedException e) {
				}
			}

			// if (actionSend == null) {
			// actionReset = getAction(ACTION_SEND);
			// }
			// if (actionSend == null) {
			// LOG.trace("{} - No action called {} available", getAgent(),
			// ACTION_SEND);
			// return;
			// }
			//
			// AbstractAgentBeanProtectedMethodPublisher.invoke(
			// (AbstractAgentBean) getAgent(),
			// actionSend,
			// new Serializable[] {
			// actionReset,
			// CommunicationAddressFactory
			// .createGroupAddress(getId()) });
		}
	}

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(GroupImpl.class);

	/** The entities. */
	private List<Entity> entities;

	/** The logger. */
	@Logging
	private Logger log;
	
	/** The schedule handler service agent bean. */
	private ScheduleHandlerServiceAgentBean scheduleHandlerServiceAgentBean;

	/**
	 * Instantiates a new group.
	 * 
	 * @param id
	 *            the id
	 */
	@Inject
	public GroupImpl(@Value("#{ uuidGenerator.generateUUID() }") final String id) {
		super(id);
		scheduleHandlerServiceAgentBean = new ProxyScheduleHandlerServiceAgentBean();
		entities = new LinkedList<>();
	}

	/**
	 * Instantiates a new group from a builder.
	 * 
	 * @param groupBuilder
	 *            the group builder
	 */
	private GroupImpl(final GroupBuilderImpl groupBuilder) {
		this(groupBuilder.id);
		setAgent(groupBuilder.entity.getAgent());
	}

	/* (non-Javadoc)
	 * @see okeanos.core.entities.Group#addEntity(okeanos.core.entities.Entity)
	 */
	@Override
	public boolean addEntity(Entity entity) {
		return entities.add(entity);
	}

	/* (non-Javadoc)
	 * @see okeanos.core.entities.Group#removeEntity(okeanos.core.entities.Entity)
	 */
	@Override
	public boolean removeEntity(Entity entity) {
		return entities.remove(entity);
	}

	/* (non-Javadoc)
	 * @see okeanos.core.internal.entities.EntityImpl#reset()
	 */
	@Override
	public void reset() {
		LOG.info("RESET ON GROUP {} called", getId());
		scheduleHandlerServiceAgentBean.reset(true);
	}
}
