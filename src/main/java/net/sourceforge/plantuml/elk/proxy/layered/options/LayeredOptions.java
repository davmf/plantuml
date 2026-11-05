package net.sourceforge.plantuml.elk.proxy.layered.options;

import net.sourceforge.plantuml.elk.proxy.ElkProperty;

public class LayeredOptions {

	private static final String LAYERED_OPTIONS = "org.eclipse.elk.alg.layered.options.LayeredOptions";

	public static final ElkProperty LAYERING_LAYER_CONSTRAINT = new ElkProperty(LAYERED_OPTIONS,
			"LAYERING_LAYER_CONSTRAINT", "org.eclipse.elk.layered.layering.layerConstraint");

	public static final ElkProperty NODE_PLACEMENT_STRATEGY = new ElkProperty(LAYERED_OPTIONS, "NODE_PLACEMENT_STRATEGY",
			"org.eclipse.elk.layered.nodePlacement.strategy");

	public static final ElkProperty SPACING_NODE_NODE_BETWEEN_LAYERS = new ElkProperty(LAYERED_OPTIONS,
			"SPACING_NODE_NODE_BETWEEN_LAYERS", "org.eclipse.elk.layered.spacing.nodeNodeBetweenLayers");

	public static final ElkProperty SPACING_EDGE_NODE_BETWEEN_LAYERS = new ElkProperty(LAYERED_OPTIONS,
			"SPACING_EDGE_NODE_BETWEEN_LAYERS", "org.eclipse.elk.layered.spacing.edgeNodeBetweenLayers");

	public static final ElkProperty SPACING_EDGE_EDGE_BETWEEN_LAYERS = new ElkProperty(LAYERED_OPTIONS,
			"SPACING_EDGE_EDGE_BETWEEN_LAYERS", "org.eclipse.elk.layered.spacing.edgeEdgeBetweenLayers");

}
