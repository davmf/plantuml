package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum Direction implements ElkObjectProxy {
	UNDEFINED, RIGHT, LEFT, DOWN, UP;

	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.core.options.Direction", name());
	}

}
