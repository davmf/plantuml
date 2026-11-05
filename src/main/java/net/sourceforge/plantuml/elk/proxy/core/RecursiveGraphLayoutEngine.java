package net.sourceforge.plantuml.elk.proxy.core;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.core.util.NullElkProgressMonitor;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class RecursiveGraphLayoutEngine {

	public void layout(ElkNode root, NullElkProgressMonitor monitor) {
		if (root.obj instanceof ElkJsonGraph.Node) {
			ElkJsonGraph.layout((ElkJsonGraph.Node) root.obj);
			return;
		}
		final Object engine = Reflect.newInstance("org.eclipse.elk.core.RecursiveGraphLayoutEngine");
		Reflect.call2(engine, "layout", root.obj, monitor.getTrueObject());
	}

}
