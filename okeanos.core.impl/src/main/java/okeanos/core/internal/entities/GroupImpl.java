package okeanos.core.internal.entities;

import javax.inject.Inject;

import okeanos.core.entities.Group;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GroupImpl extends EntityImpl implements Group {
	@Inject
	public GroupImpl(@Value("#{ uuidGenerator.generateUUID() }") String id) {
		super(id);
	}
}
