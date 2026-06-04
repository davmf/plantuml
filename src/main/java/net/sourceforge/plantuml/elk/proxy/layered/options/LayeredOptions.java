package net.sourceforge.plantuml.elk.proxy.layered.options;

import net.sourceforge.plantuml.elk.proxy.Reflect;

public class LayeredOptions {

	public static final Object LAYERING_LAYER_CONSTRAINT = Reflect.field(
			"org.eclipse.elk.alg.layered.options.LayeredOptions", "LAYERING_LAYER_CONSTRAINT");
	public static final Object NODE_PLACEMENT_STRATEGY = Reflect.field(
			"org.eclipse.elk.alg.layered.options.LayeredOptions", "NODE_PLACEMENT_STRATEGY");
	public static final Object SPACING_NODE_NODE_BETWEEN_LAYERS = Reflect.field(
			"org.eclipse.elk.alg.layered.options.LayeredOptions", "SPACING_NODE_NODE_BETWEEN_LAYERS");
	public static final Object SPACING_EDGE_NODE_BETWEEN_LAYERS = Reflect.field(
			"org.eclipse.elk.alg.layered.options.LayeredOptions", "SPACING_EDGE_NODE_BETWEEN_LAYERS");
	public static final Object SPACING_EDGE_EDGE_BETWEEN_LAYERS = Reflect.field(
			"org.eclipse.elk.alg.layered.options.LayeredOptions", "SPACING_EDGE_EDGE_BETWEEN_LAYERS");

}
