package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum PortLabelPlacement implements ElkObjectProxy {

	OUTSIDE, INSIDE, NEXT_TO_PORT_IF_POSSIBLE, ALWAYS_SAME_SIDE, ALWAYS_OTHER_SAME_SIDE, SPACE_EFFICIENT;

	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.core.options.PortLabelPlacement", name());
	}

}
