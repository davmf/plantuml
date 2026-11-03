package net.sourceforge.plantuml.elk.proxy.layered.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum LayerConstraint implements ElkObjectProxy {

	NONE, FIRST, FIRST_SEPARATE, LAST, LAST_SEPARATE;

	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.alg.layered.options.LayerConstraint", name());
	}

}
