package okeanos.core.internal.entities;

import javax.inject.Inject;

import okeanos.core.entities.Grid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class GridImpl extends GroupImpl implements Grid {
	@Inject
	public GridImpl(@Value("#{ uuidGenerator.generateUUID() }") String id) {
		super(id);
	}
}
