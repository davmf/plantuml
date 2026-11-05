package net.sourceforge.plantuml.elk.proxy.core.util;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public class NullElkProgressMonitor implements ElkObjectProxy {

	@Override
	public Object getTrueObject() {
		return Reflect.newInstance("org.eclipse.elk.core.util.NullElkProgressMonitor");
	}

}
