package net.sourceforge.plantuml.elk.proxy.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkEdge extends ElkWithProperty {

	public ElkEdge(Object obj) {
		super(obj);
	}

	private ElkJsonGraph.Edge json() {
		return obj instanceof ElkJsonGraph.Edge ? (ElkJsonGraph.Edge) obj : null;
	}

	public ElkNode getContainingNode() {
		final ElkJsonGraph.Edge json = json();
		if (json != null)
			return new ElkNode(json.getContainingNode());
		return new ElkNode(Reflect.call(obj, "getContainingNode"));
	}

	public Collection<ElkLabel> getLabels() {
		final ElkJsonGraph.Edge json = json();
		final Collection<?> internal = json != null ? json.getLabels() : (Collection<?>) Reflect.call(obj, "getLabels");
		final List<ElkLabel> result = new ArrayList<>();
		for (Object element : internal) {
			result.add(new ElkLabel(element));
		}
		return result;
	}

	public List<ElkEdgeSection> getSections() {
		final ElkJsonGraph.Edge json = json();
		final Collection<?> internal = json != null ? json.getSections()
				: (Collection<?>) Reflect.call(obj, "getSections");
		final List<ElkEdgeSection> result = new ArrayList<>();
		for (Object element : internal) {
			result.add(new ElkEdgeSection(element));
		}
		return result;
	}

	public boolean isHierarchical() {
		throw new UnsupportedOperationException();
	}

}
