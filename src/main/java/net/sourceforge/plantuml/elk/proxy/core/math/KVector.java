package net.sourceforge.plantuml.elk.proxy.core.math;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public class KVector implements ElkObjectProxy {
    // ::remove folder when __HAXE__

	public final Object obj;

	public KVector(double x, double y) {
		this.obj = Reflect.newInstance("org.eclipse.elk.core.math.KVector", x, y);
	}

	@Override
	public Object getTrueObject() {
		return obj;
	}

}
