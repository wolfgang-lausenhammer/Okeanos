package okeanos.core.internal.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;
import okeanos.core.entities.Entity;
import okeanos.core.entities.Group;
import okeanos.core.entities.builder.EntityBuilder;
import okeanos.data.services.agentbeans.entities.GroupFact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.environment.IEffector;
import de.dailab.jiactng.agentcore.knowledge.IMemory;
import de.dailab.jiactng.agentcore.lifecycle.ILifecycle.LifecycleStates;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;
import de.dailab.jiactng.agentcore.ontology.ThisAgentDescription;

/**
 * The Class EntityImpl.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("prototype")
public class EntityImpl implements Entity {

	/**
	 * Represents an entity builder. Can be used to create new entities in a
	 * fluent API.
	 */
	@Component("entityBuilder")
	@Scope("prototype")
	public static class EntityBuilderImpl implements EntityBuilder {
		/* optional */
		/** The agent. */
		private IAgent agent;

		/* mandatory */
		/** The id. */
		private final String id;

		/**
		 * Instantiates a new entity builder.
		 * 
		 * @param id
		 *            the id
		 */
		@Inject
		public EntityBuilderImpl(
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
		 * @see
		 * okeanos.core.entities.builder.EntityBuilder#agent(de.dailab.jiactng
		 * .agentcore.IAgent)
		 */
		@Override
		public EntityBuilder agent(final IAgent agent) {
			this.agent = agent;

			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see okeanos.core.entities.builder.EntityBuilder#build()
		 */
		@Override
		public Entity build() {
			return new EntityImpl(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.core.entities.builder.EntityBuilder#fromJson(java.lang.String
		 * )
		 */
		@Override
		public EntityBuilder fromJson(final String entityAsJson) {
			throw new NullPointerException(
					"EntityBuilder from json not yet implemented!");
			// return this;
		}

	}

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(EntityImpl.class);

	/** The agent. */
	private transient IAgent agent;

	/** The id. */
	private String id;

	/** The logger. */
	private Logger log = LoggerFactory.getLogger(EntityImpl.class);

	/** The schedule handler service agent bean. */
	private ScheduleHandlerServiceAgentBean scheduleHandlerServiceAgentBean;

	/**
	 * Instantiates a new entity.
	 * 
	 * @param id
	 *            the id
	 */
	@Inject
	public EntityImpl(
			@Value("#{ uuidGenerator.generateUUID() }") final String id) {
		this.id = id;
	}

	/**
	 * Instantiates a new entity from a builder.
	 * 
	 * @param entityBuilder
	 *            the entity builder
	 */
	private EntityImpl(final EntityBuilderImpl entityBuilder) {
		this(entityBuilder.id);
		setAgent(entityBuilder.agent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.core.entities.Entity#addFunctionality(de.dailab.jiactng.agentcore
	 * .IAgentBean[])
	 */
	@Override
	public void addFunctionality(final IAgentBean... functionality)
			throws LifecycleException {
		if (functionality == null) {
			throw new NullPointerException("functionality must not be null");
		}
		if (functionality.length == 0) {
			throw new NullPointerException(
					"functionality must contain at least one element");
		}

		if (log != null) {
			log.debug("Adding {} functionalities to entity [{}]",
					functionality.length, this);
		}
		List<IAgentBean> beans = agent.getAgentBeans();
		List<IAgentBean> oldBeansPlusNew = new ArrayList<>(beans.size()
				+ functionality.length);
		oldBeansPlusNew.addAll(beans);
		oldBeansPlusNew.addAll(Arrays.asList(functionality));

		for (IAgentBean ab : functionality) {
			ab.setThisAgent(agent);
		}

		agent.setAgentBeans(oldBeansPlusNew);
		doInitOfNewBeans(functionality);
		doStartOfNewBeans(functionality);

		if (log != null) {
			log.debug("Finished adding {} functionalities to entity [{}]",
					functionality.length, this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.core.entities.Entity#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		return agent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.core.entities.Entity#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see okeanos.core.entities.Entity#joinGroup(okeanos.core.entities.Group)
	 */
	@Override
	public void joinGroup(final Group group) {
		log.debug("Entity [entity={}] joining group [group={}]", this, group);
		Agent agent = ((Agent) this.agent);
		IMemory memory = agent.getMemory();
		GroupFact groupFact = new GroupFact(group.getId());
		memory.write(groupFact);

		group.addEntity(this);
	}

	/* (non-Javadoc)
	 * @see okeanos.core.entities.Entity#leaveGroup(okeanos.core.entities.Group)
	 */
	@Override
	public void leaveGroup(final Group group) {
		log.debug("Entity [entity={}] leaving group [group={}]", this, group);
		Agent agent = ((Agent) this.agent);
		IMemory memory = agent.getMemory();
		GroupFact groupFact = new GroupFact(group.getId());
		memory.remove(groupFact);

		group.removeEntity(this);
	}

	/* (non-Javadoc)
	 * @see okeanos.core.entities.Entity#reset()
	 */
	@Override
	public void reset() {
		scheduleHandlerServiceAgentBean.reset(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.core.entities.Entity#setAgent(de.dailab.jiactng.agentcore.IAgent)
	 */
	@Override
	public void setAgent(final IAgent agent) {
		this.agent = agent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("EntityImpl [id=%s]", id);
	}

	/**
	 * Calls the initialization methods of new beans.
	 * 
	 * @param agentBeans
	 *            the agent beans
	 * @throws LifecycleException
	 *             if an agent bean could not be initialized
	 */
	private void doInitOfNewBeans(final IAgentBean... agentBeans)
			throws LifecycleException {
		Agent agent = ((Agent) this.agent);
		IMemory memory = agent.getMemory();

		IAgentDescription myDescription = memory
				.read(new ThisAgentDescription());

		List<LifecycleException> exceptions = new ArrayList<>(1);
		ArrayList<IActionDescription> tempList = new ArrayList<>();

		for (IAgentBean ab : agentBeans) {
			ab.setThisAgent(agent);
			try {
				ab.setMemory(memory);
				ab.addLifecycleListener(agent);

				agent.setBeanState(ab, LifecycleStates.INITIALIZED);
			} catch (LifecycleException e) {
				exceptions.add(e);
			}

			// if bean is effector, add all actions to memory
			if (ab instanceof IEffector) {
				final List<? extends IActionDescription> acts = ((IEffector) ab)
						.getActions();
				if (acts != null) {
					for (IActionDescription item : acts) {
						item.setProviderDescription(myDescription);
						if (item.getProviderBean() == null) {
							item.setProviderBean((IEffector) ab);
						}
						memory.write(item);
						tempList.add(item);
					}
				}
			}
		}

		tempList.addAll(agent.getActionList());
		agent.setActionList(tempList);

		if (exceptions.size() > 0) {
			throw exceptions.get(0);
		}

		this.scheduleHandlerServiceAgentBean = agent.findAgentBean(ScheduleHandlerServiceAgentBean.class);
	}

	/**
	 * Calls the start methods of new beans.
	 * 
	 * @param agentBeans
	 *            the agent beans
	 * @throws LifecycleException
	 *             if an agent bean could not be started
	 */
	private void doStartOfNewBeans(final IAgentBean... agentBeans)
			throws LifecycleException {
		Agent agent = ((Agent) this.agent);

		List<LifecycleException> exceptions = new ArrayList<>(1);
		for (IAgentBean a : agentBeans) {
			try {
				agent.setBeanState(a, LifecycleStates.STARTED);
			} catch (LifecycleException e) {
				exceptions.add(e);
			}
		}

		if (exceptions.size() > 0) {
			throw exceptions.get(0);
		}
	}
}
