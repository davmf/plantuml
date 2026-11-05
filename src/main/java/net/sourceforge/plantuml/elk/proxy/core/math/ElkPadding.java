package net.sourceforge.plantuml.elk.proxy.core.math;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public class ElkPadding implements ElkObjectProxy {

	private final double top;
	private final double right;
	private final double bottom;
	private final double left;
	private Object obj;

	public ElkPadding(double top, double right, double bottom, double left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	@Override
	public synchronized Object getTrueObject() {
		if (obj == null)
			obj = Reflect.newInstance("org.eclipse.elk.core.math.ElkPadding", top, right, bottom, left);
		return obj;
	}

	/** The same text as <code>org.eclipse.elk.core.math.ElkPadding.toString()</code>, which ELK parses back. */
	public String toJsonValue() {
		return "[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
	}

}
