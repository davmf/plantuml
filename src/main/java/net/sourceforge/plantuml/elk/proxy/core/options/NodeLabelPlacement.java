package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum NodeLabelPlacement implements ElkObjectProxy {

	INSIDE, OUTSIDE, V_TOP, V_CENTER, V_BOTTOM, H_LEFT, H_CENTER, H_RIGHT;

	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.core.options.NodeLabelPlacement", name());
	}

}
