package net.sourceforge.plantuml.svek.orthoroute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public final class VisGraph {

	private final Map<XPoint2D, VisNode> nodesByPosition =
			new LinkedHashMap<XPoint2D, VisNode>();
	private final Map<VisNode, List<VisEdge>> adjacency =
			new LinkedHashMap<VisNode, List<VisEdge>>();

	public VisNode addNode(double x, double y, VisNodeKind kind) {
		return addNode(new XPoint2D(x, y), kind);
	}

	public VisNode addNode(XPoint2D point, VisNodeKind kind) {
		final VisNode existing = nodesByPosition.get(point);
		if (existing != null)
			return existing;
		final VisNode node = new VisNode(point, kind);
		nodesByPosition.put(point, node);
		adjacency.put(node, new ArrayList<VisEdge>());
		return node;
	}

	public VisNode getNode(XPoint2D point) {
		return nodesByPosition.get(point);
	}

	public VisNode getNode(double x, double y) {
		return nodesByPosition.get(new XPoint2D(x, y));
	}

	public void addEdge(VisNode from, VisNode to) {
		if (from.equals(to))
			return;
		final VisEdge edge = new VisEdge(from, to);
		adjacency.get(from).add(edge);
		adjacency.get(to).add(edge);
	}

	public List<VisEdge> getNeighbors(VisNode node) {
		final List<VisEdge> edges = adjacency.get(node);
		if (edges == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(edges);
	}

	public Collection<VisNode> allNodes() {
		return Collections.unmodifiableCollection(
				nodesByPosition.values());
	}

	public int nodeCount() {
		return nodesByPosition.size();
	}

	public int edgeCount() {
		int total = 0;
		for (List<VisEdge> edges : adjacency.values())
			total += edges.size();
		return total / 2;
	}
}
