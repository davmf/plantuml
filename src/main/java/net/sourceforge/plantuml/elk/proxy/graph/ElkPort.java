package net.sourceforge.plantuml.elk.proxy.graph;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkPort extends ElkWithProperty {

	public ElkPort(Object obj) {
		super(obj);
	}

	private ElkJsonGraph.Port json() {
		return obj instanceof ElkJsonGraph.Port ? (ElkJsonGraph.Port) obj : null;
	}

	public ElkNode getParent() {
		final ElkJsonGraph.Port json = json();
		final Object tmp = json != null ? json.getParent() : Reflect.call(obj, "getParent");
		if (tmp == null) {
			return null;
		}
		return new ElkNode(tmp);
	}

	public double getX() {
		final ElkJsonGraph.Port json = json();
		if (json != null)
			return json.getX();
		return (Double) Reflect.call(obj, "getX");
	}

	public double getY() {
		final ElkJsonGraph.Port json = json();
		if (json != null)
			return json.getY();
		return (Double) Reflect.call(obj, "getY");
	}

	public double getWidth() {
		final ElkJsonGraph.Port json = json();
		if (json != null)
			return json.getWidth();
		return (Double) Reflect.call(obj, "getWidth");
	}

	public double getHeight() {
		final ElkJsonGraph.Port json = json();
		if (json != null)
			return json.getHeight();
		return (Double) Reflect.call(obj, "getHeight");
	}

	public void setDimensions(double width, double height) {
		final ElkJsonGraph.Port json = json();
		if (json != null)
			json.setDimensions(width, height);
		else
			Reflect.call2(obj, "setDimensions", width, height);
	}

}
