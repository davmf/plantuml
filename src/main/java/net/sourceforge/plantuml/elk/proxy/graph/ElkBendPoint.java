package net.sourceforge.plantuml.elk.proxy.graph;

import java.util.Objects;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkBendPoint {

	public final Object obj;

	public ElkBendPoint(Object obj) {
		this.obj = Objects.requireNonNull(obj);
	}

	@Override
	public int hashCode() {
		return this.obj.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this.obj.equals(((ElkBendPoint) other).obj);
	}

	public double getX() {
		if (obj instanceof ElkJsonGraph.Point)
			return ((ElkJsonGraph.Point) obj).getX();
		return (Double) Reflect.call(obj, "getX");
	}

	public double getY() {
		if (obj instanceof ElkJsonGraph.Point)
			return ((ElkJsonGraph.Point) obj).getY();
		return (Double) Reflect.call(obj, "getY");
	}

}
