package okeanos.core.internal.entities;

import javax.inject.Inject;

import okeanos.core.entities.Grid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The Class GridImpl.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
@Scope("singleton")
public class GridImpl extends GroupImpl implements Grid {

	/**
	 * Instantiates a new grid.
	 * 
	 * @param id
	 *            the id
	 */
	@Inject
	public GridImpl(@Value("#{ uuidGenerator.generateUUID() }") final String id) {
		super(id);
	}
}
