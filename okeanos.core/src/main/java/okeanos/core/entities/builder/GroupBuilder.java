package okeanos.core.entities.builder;

import okeanos.core.entities.Entity;
import okeanos.core.entities.Group;

public interface GroupBuilder extends Builder {
	GroupBuilder entity(Entity entity);

	Group build();
}
