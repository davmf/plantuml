package net.sourceforge.plantuml.elk.proxy.core.math;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public class KVector implements ElkObjectProxy {
    // ::remove folder when __HAXE__

	private final double x;
	private final double y;
	private Object obj;

	public KVector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public synchronized Object getTrueObject() {
		if (obj == null)
			obj = Reflect.newInstance("org.eclipse.elk.core.math.KVector", x, y);
		return obj;
	}

	/** The same text as <code>org.eclipse.elk.core.math.KVector.toString()</code>, which ELK parses back. */
	public String toJsonValue() {
		return "(" + x + "," + y + ")";
	}

}
