package okeanos.core.internal.entities;

import javax.inject.Inject;

import okeanos.core.entities.Entity;
import okeanos.core.entities.Group;
import okeanos.core.entities.builder.EntityBuilder;
import okeanos.core.entities.builder.GroupBuilder;
import okeanos.core.internal.entities.EntityImpl.EntityBuilderImpl;
import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.IAgent;

/**
 * The Class GroupImpl.
 */
@Component
@Scope("prototype")
public class GroupImpl extends EntityImpl implements Group {

	/**
	 * Represents an entity builder. Can be used to create new entities in a
	 * fluent API.
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
				@Value("#{ uuidGenerator.generateUUID() }") String id) {
			this.id = id;
			if (id == null)
				throw new NullPointerException(
						"ID is null, check if Spring Expression term is right");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * okeanos.core.entities.builder.EntityBuilder#agent(de.dailab.jiactng
		 * .agentcore.IAgent)
		 */
		@Override
		public GroupBuilder entity(Entity entity) {
			this.entity = entity;

			return this;
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

	}

	/** The logger. */
	@Logging
	private Logger log;

	/**
	 * Instantiates a new group.
	 * 
	 * @param id
	 *            the id
	 */
	@Inject
	public GroupImpl(@Value("#{ uuidGenerator.generateUUID() }") String id) {
		super(id);
	}

	/**
	 * Instantiates a new group from a builder.
	 * 
	 * @param groupBuilder
	 *            the group builder
	 */
	private GroupImpl(GroupBuilderImpl groupBuilder) {
		super(groupBuilder.id);
		setAgent(groupBuilder.entity.getAgent());
	}
}
