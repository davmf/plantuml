package net.sourceforge.plantuml.elk.proxy.graph;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkNode extends ElkWithProperty {

	public ElkNode(Object obj) {
		super(obj);
	}

	private ElkJsonGraph.Node json() {
		return obj instanceof ElkJsonGraph.Node ? (ElkJsonGraph.Node) obj : null;
	}

	public ElkNode getParent() {
		final ElkJsonGraph.Node json = json();
		final Object tmp = json != null ? json.getParent() : Reflect.call(obj, "getParent");
		if (tmp == null) {
			return null;
		}
		return new ElkNode(tmp);
	}

	public double getX() {
		final ElkJsonGraph.Node json = json();
		if (json != null)
			return json.getX();
		return (Double) Reflect.call(obj, "getX");
	}

	public double getY() {
		final ElkJsonGraph.Node json = json();
		if (json != null)
			return json.getY();
		return (Double) Reflect.call(obj, "getY");
	}

	public Collection<ElkLabel> getLabels() {
		final ElkJsonGraph.Node json = json();
		final Collection<?> internal = json != null ? json.getLabels() : (Collection<?>) Reflect.call(obj, "getLabels");
		final Collection<ElkLabel> result = new ArrayList<>();
		for (Object element : internal) {
			result.add(new ElkLabel(element));
		}
		return result;
	}

	public double getWidth() {
		final ElkJsonGraph.Node json = json();
		if (json != null)
			return json.getWidth();
		return (Double) Reflect.call(obj, "getWidth");
	}

	public double getHeight() {
		final ElkJsonGraph.Node json = json();
		if (json != null)
			return json.getHeight();
		return (Double) Reflect.call(obj, "getHeight");
	}

	public void setDimensions(double width, double height) {
		final ElkJsonGraph.Node json = json();
		if (json != null)
			json.setDimensions(width, height);
		else
			Reflect.call2(obj, "setDimensions", width, height);
	}

}
