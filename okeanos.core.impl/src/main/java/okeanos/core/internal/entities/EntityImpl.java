package okeanos.core.internal.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import okeanos.core.entities.Entity;
import okeanos.core.entities.builder.EntityBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentBean;
import de.dailab.jiactng.agentcore.knowledge.IMemory;
import de.dailab.jiactng.agentcore.lifecycle.ILifecycle.LifecycleStates;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@Component
@Scope("prototype")
public class EntityImpl implements Entity {
	@Component("entityBuilderImpl")
	@Scope("prototype")
	public static class EntityBuilderImpl implements EntityBuilder {
		// optional
		private IAgent agent;

		// mandatory
		private final String id;

		@Inject
		public EntityBuilderImpl(
				@Value("#{ uuidGenerator.generateUUID() }") String id) {
			this.id = id;
			if (id == null)
				throw new NullPointerException(
						"ID is null, check if Spring Expression term is right");
		}

		@Override
		public EntityBuilder agent(IAgent agent) {
			this.agent = agent;

			return this;
		}

		@Override
		public Entity build() {
			return new EntityImpl(this);
		}

		@Override
		public EntityBuilder fromJson(String entityAsJson) {
			throw new NullPointerException(
					"EntityBuilder from json not yet implemented!");
			// return this;
		}

	}
	
	private static final Logger log = LoggerFactory.getLogger(EntityImpl.class);

	private transient IAgent agent;

	private String id;

	public EntityImpl() {
	}

	private EntityImpl(EntityBuilderImpl entityBuilder) {
		this.id = entityBuilder.id;
		this.agent = entityBuilder.agent;
	}

	@Override
	public void addFunctionality(IAgentBean... functionality)
			throws LifecycleException {
		if (functionality == null)
			throw new NullPointerException("functionality must not be null");
		if (functionality.length == 0)
			throw new NullPointerException(
					"functionality must contain at least one element");

		log.debug("Adding {} functionalities to entity [{}]", functionality.length, this);
		List<IAgentBean> beans = agent.getAgentBeans();
		List<IAgentBean> oldBeansPlusNew = new ArrayList<>(beans.size()
				+ functionality.length);
		oldBeansPlusNew.addAll(beans);
		oldBeansPlusNew.addAll(Arrays.asList(functionality));

		for (IAgentBean ab : functionality)
			ab.setThisAgent(agent);
		
		agent.setAgentBeans(oldBeansPlusNew);
		doInitOfNewBeans(functionality);
		doStartOfNewBeans(functionality);

		log.debug("Finished adding {} functionalities to entity [{}]", functionality.length, this);
	}

	private void doInitOfNewBeans(IAgentBean... agentBeans)
			throws LifecycleException {
		Agent agent = ((Agent) this.agent);
		IMemory memory = agent.getMemory();

		List<LifecycleException> exceptions = new ArrayList<>(1);
		for (IAgentBean ab : agentBeans) {
			ab.setThisAgent(agent);
			try {
				ab.setMemory(memory);
				ab.addLifecycleListener(agent);

				agent.setBeanState(ab, LifecycleStates.INITIALIZED);
			} catch (LifecycleException e) {
				exceptions.add(e);
			}
		}

		if (exceptions.size() > 0) {
			throw exceptions.get(0);
		}
	}

	private void doStartOfNewBeans(IAgentBean... agentBeans)
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

	@Override
	public IAgent getAgent() {
		return agent;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setAgent(IAgent agent) {
		this.agent = agent;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("EntityImpl [id=%s]", id);
	}
}
