package okeanos.core.internal.entities;

import okeanos.core.entities.Grid;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class GridImpl extends GroupImpl implements Grid {
}
