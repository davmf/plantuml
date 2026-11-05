package net.sourceforge.plantuml.elk.proxy.graph.util;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.graph.ElkPort;
import net.sourceforge.plantuml.elk.proxy.graph.ElkWithProperty;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

/**
 * The graph is made of real ELK objects (through reflection) or, when an ELK
 * JSON layouter is registered, of {@link ElkJsonGraph} objects. Only
 * {@link #createGraph()} chooses; everything else is created next to what it
 * is attached to.
 */
public class ElkGraphUtil {

	private static final String ELK_GRAPH_UTIL = "org.eclipse.elk.graph.util.ElkGraphUtil";

	public static ElkLabel createLabel(ElkEdge edge) {
		if (edge.obj instanceof ElkJsonGraph.Edge)
			return new ElkLabel(((ElkJsonGraph.Edge) edge.obj).createLabel());
		return new ElkLabel(Reflect.callStatic2(ELK_GRAPH_UTIL, "createLabel", edge.obj));
	}

	public static ElkLabel createLabel(ElkNode node) {
		if (node.obj instanceof ElkJsonGraph.Node)
			return new ElkLabel(((ElkJsonGraph.Node) node.obj).createLabel());
		return new ElkLabel(Reflect.callStatic2(ELK_GRAPH_UTIL, "createLabel", node.obj));
	}

	public static ElkLabel createLabel(ElkPort port) {
		return new ElkLabel(Reflect.callStatic2(ELK_GRAPH_UTIL, "createLabel", port.obj));
	}

	public static ElkNode createNode(ElkNode root) {
		if (root.obj instanceof ElkJsonGraph.Node)
			return new ElkNode(((ElkJsonGraph.Node) root.obj).createNode());
		return new ElkNode(Reflect.callStatic2(ELK_GRAPH_UTIL, "createNode", root.obj));
	}

	public static ElkPort createPort(ElkNode parent) {
		return new ElkPort(Reflect.callStatic2(ELK_GRAPH_UTIL, "createPort", parent.obj));
	}

	public static ElkEdge createSimpleEdge(ElkNode node1, ElkNode node2) {
		return createSimpleEdgeProxy(node1, node2);
	}

	public static ElkEdge createSimpleEdge(ElkPort port1, ElkPort port2) {
		return createSimpleEdgeProxy(port1, port2);
	}

	public static ElkEdge createSimpleEdge(ElkNode node1, ElkPort port2) {
		return createSimpleEdgeProxy(node1, port2);
	}

	public static ElkEdge createSimpleEdge(ElkPort port1, ElkNode node2) {
		return createSimpleEdgeProxy(port1, node2);
	}

	private static ElkEdge createSimpleEdgeProxy(ElkWithProperty end1, ElkWithProperty end2) {
		if (end1.obj instanceof ElkJsonGraph.Node)
			return new ElkEdge(
					ElkJsonGraph.createSimpleEdge((ElkJsonGraph.Node) end1.obj, (ElkJsonGraph.Node) end2.obj));
		return new ElkEdge(Reflect.callStatic2(ELK_GRAPH_UTIL, "createSimpleEdge", end1.obj, end2.obj));
	}

	public static ElkNode createGraph() {
		if (ElkJsonGraph.getLayouter() != null)
			return new ElkNode(ElkJsonGraph.createGraph());
		return new ElkNode(Reflect.callStatic(ELK_GRAPH_UTIL, "createGraph"));
	}

}
