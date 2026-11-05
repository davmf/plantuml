package net.sourceforge.plantuml.elk.proxy.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkEdgeSection {

	public final Object obj;

	public ElkEdgeSection(Object obj) {
		this.obj = Objects.requireNonNull(obj);
	}

	@Override
	public int hashCode() {
		return this.obj.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this.obj.equals(((ElkEdgeSection) other).obj);
	}

	private ElkJsonGraph.Section json() {
		return obj instanceof ElkJsonGraph.Section ? (ElkJsonGraph.Section) obj : null;
	}

	public double getStartX() {
		final ElkJsonGraph.Section json = json();
		if (json != null)
			return json.getStart().getX();
		return (Double) Reflect.call(obj, "getStartX");
	}

	public double getStartY() {
		final ElkJsonGraph.Section json = json();
		if (json != null)
			return json.getStart().getY();
		return (Double) Reflect.call(obj, "getStartY");
	}

	public double getEndX() {
		final ElkJsonGraph.Section json = json();
		if (json != null)
			return json.getEnd().getX();
		return (Double) Reflect.call(obj, "getEndX");
	}

	public double getEndY() {
		final ElkJsonGraph.Section json = json();
		if (json != null)
			return json.getEnd().getY();
		return (Double) Reflect.call(obj, "getEndY");
	}

	public Collection<ElkBendPoint> getBendPoints() {
		final ElkJsonGraph.Section json = json();
		final Collection<?> internal = json != null ? json.getBendPoints()
				: (Collection<?>) Reflect.call(obj, "getBendPoints");
		final List<ElkBendPoint> result = new ArrayList<>();
		for (Object element : internal) {
			result.add(new ElkBendPoint(element));
		}
		return result;
	}

}
