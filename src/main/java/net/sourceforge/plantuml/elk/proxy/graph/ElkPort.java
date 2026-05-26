package net.sourceforge.plantuml.elk.proxy.graph;

import net.sourceforge.plantuml.elk.proxy.Reflect;

public class ElkPort extends ElkWithProperty {

	public ElkPort(Object obj) {
		super(obj);
	}

	public ElkNode getParent() {
		final Object tmp = Reflect.call(obj, "getParent");
		if (tmp == null) {
			return null;
		}
		return new ElkNode(tmp);
	}

	public double getX() {
		return (Double) Reflect.call(obj, "getX");
	}

	public double getY() {
		return (Double) Reflect.call(obj, "getY");
	}

	public double getWidth() {
		return (Double) Reflect.call(obj, "getWidth");
	}

	public double getHeight() {
		return (Double) Reflect.call(obj, "getHeight");
	}

	public void setDimensions(double width, double height) {
		Reflect.call2(obj, "setDimensions", width, height);
	}

	public void setLocation(double x, double y) {
		Reflect.call2(obj, "setLocation", x, y);
	}

}
