package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum SizeConstraint implements ElkObjectProxy {
	NODE_LABELS, PORTS, PORT_LABELS, MINIMUM_SIZE;

	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.core.options.SizeConstraint", name());
	}

}
