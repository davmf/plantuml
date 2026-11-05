package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkProperty;

public class CoreOptions {

	private static final String CORE_OPTIONS = "org.eclipse.elk.core.options.CoreOptions";

	public static final ElkProperty DIRECTION = new ElkProperty(CORE_OPTIONS, "DIRECTION", "org.eclipse.elk.direction");

	public static final ElkProperty EDGE_LABELS_INLINE = new ElkProperty(CORE_OPTIONS, "EDGE_LABELS_INLINE",
			"org.eclipse.elk.edgeLabels.inline");

	public static final ElkProperty NODE_SIZE_CONSTRAINTS = new ElkProperty(CORE_OPTIONS, "NODE_SIZE_CONSTRAINTS",
			"org.eclipse.elk.nodeSize.constraints");

	public static final ElkProperty HIERARCHY_HANDLING = new ElkProperty(CORE_OPTIONS, "HIERARCHY_HANDLING",
			"org.eclipse.elk.hierarchyHandling");

	public static final ElkProperty EDGE_LABELS_PLACEMENT = new ElkProperty(CORE_OPTIONS, "EDGE_LABELS_PLACEMENT",
			"org.eclipse.elk.edgeLabels.placement");

	public static final ElkProperty EDGE_TYPE = new ElkProperty(CORE_OPTIONS, "EDGE_TYPE", "org.eclipse.elk.edge.type");

	public static final ElkProperty NODE_LABELS_PLACEMENT = new ElkProperty(CORE_OPTIONS, "NODE_LABELS_PLACEMENT",
			"org.eclipse.elk.nodeLabels.placement");

	public static final ElkProperty NODE_SIZE_OPTIONS = new ElkProperty(CORE_OPTIONS, "NODE_SIZE_OPTIONS",
			"org.eclipse.elk.nodeSize.options");

	public static final ElkProperty PADDING = new ElkProperty(CORE_OPTIONS, "PADDING", "org.eclipse.elk.padding");

	public static final ElkProperty PORT_CONSTRAINTS = new ElkProperty(CORE_OPTIONS, "PORT_CONSTRAINTS",
			"org.eclipse.elk.portConstraints");

	public static final ElkProperty PORT_SIDE = new ElkProperty(CORE_OPTIONS, "PORT_SIDE", "org.eclipse.elk.port.side");

	public static final ElkProperty PORT_LABELS_PLACEMENT = new ElkProperty(CORE_OPTIONS, "PORT_LABELS_PLACEMENT",
			"org.eclipse.elk.portLabels.placement");

	public static final ElkProperty NODE_SIZE_MINIMUM = new ElkProperty(CORE_OPTIONS, "NODE_SIZE_MINIMUM",
			"org.eclipse.elk.nodeSize.minimum");

	public static final ElkProperty SPACING_PORT_PORT = new ElkProperty(CORE_OPTIONS, "SPACING_PORT_PORT",
			"org.eclipse.elk.spacing.portPort");

	public static final ElkProperty SPACING_NODE_NODE = new ElkProperty(CORE_OPTIONS, "SPACING_NODE_NODE",
			"org.eclipse.elk.spacing.nodeNode");

	public static final ElkProperty PORT_BORDER_OFFSET = new ElkProperty(CORE_OPTIONS, "PORT_BORDER_OFFSET",
			"org.eclipse.elk.port.borderOffset");

	public static final ElkProperty PORT_ANCHOR = new ElkProperty(CORE_OPTIONS, "PORT_ANCHOR",
			"org.eclipse.elk.port.anchor");

	public static final ElkProperty PORT_INDEX = new ElkProperty(CORE_OPTIONS, "PORT_INDEX", "org.eclipse.elk.port.index");

}
