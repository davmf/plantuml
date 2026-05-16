package net.sourceforge.plantuml.elk.proxy.graph.util;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.graph.ElkPort;
import net.sourceforge.plantuml.elk.proxy.graph.ElkWithProperty;

public class ElkGraphUtil {
    // ::remove folder when __HAXE__

	public static ElkLabel createLabel(ElkEdge edge) {
		return new ElkLabel(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createLabel", edge.obj));
	}

	public static ElkLabel createLabel(ElkNode node) {
		return new ElkLabel(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createLabel", node.obj));
	}

	public static ElkLabel createLabel(ElkPort port) {
		return new ElkLabel(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createLabel", port.obj));
	}

	public static ElkNode createNode(ElkNode root) {
		return new ElkNode(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createNode", root.obj));
	}

	public static ElkPort createPort(ElkNode parent) {
		return new ElkPort(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createPort", parent.obj));
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
		return new ElkEdge(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createSimpleEdge",
				end1.obj, end2.obj));
	}

	public static ElkNode createGraph() {
		return new ElkNode(Reflect.callStatic("org.eclipse.elk.graph.util.ElkGraphUtil", "createGraph"));
	}

}
