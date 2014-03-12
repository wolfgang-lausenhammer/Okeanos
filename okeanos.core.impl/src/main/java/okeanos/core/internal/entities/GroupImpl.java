package okeanos.core.internal.entities;

import okeanos.core.entities.Group;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GroupImpl extends EntityImpl implements Group {
}
