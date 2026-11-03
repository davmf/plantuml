package net.sourceforge.plantuml.elk.proxy.layered.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum NodePlacementStrategy implements ElkObjectProxy {

	SIMPLE, INTERACTIVE, LINEAR_SEGMENTS, BRANDES_KOEPF, NETWORK_SIMPLEX;

	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.alg.layered.options.NodePlacementStrategy", name());
	}

}
