package okeanos.platform.services;

import okeanos.core.entities.Entity;

public interface EntityManagementService {
	Entity loadEntity(String pathToConfig);

	Entity loadEntity();

	Entity getEntity(int id);

	Entity startEntity(Entity entity);

	Entity stopEntity(Entity entity);

	void unloadEntity(Entity entity);
}
