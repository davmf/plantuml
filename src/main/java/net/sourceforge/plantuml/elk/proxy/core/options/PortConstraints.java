package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum PortConstraints implements ElkObjectProxy {

	UNDEFINED, FREE, FIXED_SIDE, FIXED_ORDER, FIXED_RATIO, FIXED_POS;

	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.core.options.PortConstraints", name());
	}

}
