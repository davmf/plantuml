package net.sourceforge.plantuml.elk.proxy.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.elk.proxy.core.RecursiveGraphLayoutEngine;
import net.sourceforge.plantuml.elk.proxy.core.math.ElkPadding;
import net.sourceforge.plantuml.elk.proxy.core.options.CoreOptions;
import net.sourceforge.plantuml.elk.proxy.core.options.Direction;
import net.sourceforge.plantuml.elk.proxy.core.options.EdgeLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.options.HierarchyHandling;
import net.sourceforge.plantuml.elk.proxy.core.options.NodeLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.util.NullElkProgressMonitor;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdgeSection;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.graph.util.ElkGraphUtil;
import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonObject;

/**
 * The ELK graph written for elkjs (the browser build of
 * {@code !pragma layout elk}), and the layout read back. No JavaScript runs
 * here: the answer of elkjs 0.12.1 to {@link #GRAPH} is recorded in
 * {@link #LAYOUT}.
 */
class ElkJsonGraphTest {

	/**
	 * A node, a cluster (padding, label) holding two nodes, an edge from the node
	 * into the cluster with a tail label, an edge inside the cluster, and an edge
	 * from the cluster to one of its children. What {@link #buildGraph()} writes.
	 */
	private static final String GRAPH = "{\"id\":\"root\",\"layoutOptions\":{\"org.eclipse.elk.direction\":\"DOWN\","
			+ "\"org.eclipse.elk.hierarchyHandling\":\"INCLUDE_CHILDREN\"},\"width\":0,\"height\":0,\"children\":["
			+ "{\"id\":\"n0\",\"width\":80,\"height\":40},"
			+ "{\"id\":\"n1\",\"layoutOptions\":{\"org.eclipse.elk.direction\":\"DOWN\","
			+ "\"org.eclipse.elk.padding\":\"[top=40.0,left=15.0,bottom=15.0,right=15.0]\"},"
			+ "\"labels\":[{\"id\":\"l2\",\"text\":\"C\",\"width\":0,\"height\":0}],\"width\":0,\"height\":0,\"children\":["
			+ "{\"id\":\"n3\",\"width\":60,\"height\":30},{\"id\":\"n4\",\"width\":60,\"height\":30}],\"edges\":["
			+ "{\"id\":\"e7\",\"sources\":[\"n3\"],\"targets\":[\"n4\"]},"
			+ "{\"id\":\"e8\",\"sources\":[\"n1\"],\"targets\":[\"n3\"]}]}],\"edges\":["
			+ "{\"id\":\"e5\",\"layoutOptions\":{\"org.eclipse.elk.edgeLabels.inline\":true},\"labels\":["
			+ "{\"id\":\"l6\",\"layoutOptions\":{\"org.eclipse.elk.edgeLabels.placement\":\"TAIL\"},"
			+ "\"text\":\"1\",\"width\":20,\"height\":12}],\"sources\":[\"n0\"],\"targets\":[\"n3\"]}]}";

	/** The answer of elkjs 0.12.1 to {@link #GRAPH}, a bend point added to e7. */
	private static final String LAYOUT = "{\"id\":\"root\",\"width\":119,\"height\":238,\"children\":["
			+ "{\"id\":\"n0\",\"width\":80,\"height\":40,\"$H\":277,\"x\":27,\"y\":12},"
			+ "{\"id\":\"n1\",\"labels\":[{\"id\":\"l2\",\"text\":\"C\",\"width\":0,\"height\":0,\"x\":0,\"y\":0}],"
			+ "\"width\":90,\"height\":135,\"children\":["
			+ "{\"id\":\"n3\",\"width\":60,\"height\":30,\"$H\":283,\"x\":15,\"y\":40},"
			+ "{\"id\":\"n4\",\"width\":60,\"height\":30,\"$H\":285,\"x\":15,\"y\":90}],\"edges\":["
			+ "{\"id\":\"e7\",\"sources\":[\"n3\"],\"targets\":[\"n4\"],\"sections\":[{\"id\":\"e7_s0\","
			+ "\"startPoint\":{\"x\":45,\"y\":70},\"endPoint\":{\"x\":45,\"y\":90},"
			+ "\"bendPoints\":[{\"x\":45,\"y\":80}]}],\"container\":\"n1\"},"
			+ "{\"id\":\"e8\",\"sources\":[\"n1\"],\"targets\":[\"n3\"],\"sections\":[{\"id\":\"e8_s0\","
			+ "\"startPoint\":{\"x\":35,\"y\":0},\"endPoint\":{\"x\":35,\"y\":40}}],\"container\":\"n1\"}],"
			+ "\"x\":12,\"y\":91}],\"edges\":["
			+ "{\"id\":\"e5\",\"labels\":[{\"id\":\"l6\",\"text\":\"1\",\"width\":20,\"height\":12,\"x\":70,\"y\":54}],"
			+ "\"sources\":[\"n0\"],\"targets\":[\"n3\"],\"sections\":[{\"id\":\"e5_s0\","
			+ "\"startPoint\":{\"x\":67,\"y\":52},\"endPoint\":{\"x\":67,\"y\":131}}],\"container\":\"root\"}],"
			+ "\"x\":0,\"y\":0}";

	private String received;

	private ElkNode root;
	private ElkNode a;
	private ElkNode cluster;
	private ElkNode c;
	private ElkNode d;
	private ElkEdge ac;
	private ElkEdge cd;
	private ElkEdge clusterToC;

	@BeforeEach
	void registerLayouter() {
		ElkJsonGraph.setLayouter(new ElkJsonLayouter() {
			@Override
			public String layout(String graphJson) {
				received = graphJson;
				return LAYOUT;
			}
		});
	}

	@AfterEach
	void unregisterLayouter() {
		ElkJsonGraph.setLayouter(null);
	}

	/** Built through the proxies, as CucaDiagramFileMakerElk does. */
	private void buildGraph() {
		root = ElkGraphUtil.createGraph();
		root.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
		root.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.INCLUDE_CHILDREN);

		a = ElkGraphUtil.createNode(root);
		a.setDimensions(80, 40);

		cluster = ElkGraphUtil.createNode(root);
		cluster.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
		cluster.setProperty(CoreOptions.PADDING, new ElkPadding(40, 15, 15, 15));
		ElkGraphUtil.createLabel(cluster).setText("C");

		c = ElkGraphUtil.createNode(cluster);
		c.setDimensions(60, 30);
		d = ElkGraphUtil.createNode(cluster);
		d.setDimensions(60, 30);

		ac = ElkGraphUtil.createSimpleEdge(a, c);
		final ElkLabel tail = ElkGraphUtil.createLabel(ac);
		tail.setText("1");
		tail.setDimensions(20, 12);
		tail.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.TAIL);
		ac.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);

		cd = ElkGraphUtil.createSimpleEdge(c, d);
		clusterToC = ElkGraphUtil.createSimpleEdge(cluster, c);
	}

	@Test
	void writesTheElkJsonFormat() {
		buildGraph();
		assertEquals(Json.parse(GRAPH), Json.parse(ElkJsonGraph.toJson((ElkJsonGraph.Node) root.obj)));
	}

	@Test
	void writesOptionValuesAsElkParsesThem() {
		final ElkNode graph = ElkGraphUtil.createGraph();
		final ElkLabel label = ElkGraphUtil.createLabel(ElkGraphUtil.createNode(graph));
		label.setProperty(CoreOptions.NODE_LABELS_PLACEMENT, java.util.EnumSet.of(NodeLabelPlacement.INSIDE,
				NodeLabelPlacement.H_CENTER, NodeLabelPlacement.V_CENTER));
		final JsonObject json = Json.parse(ElkJsonGraph.toJson((ElkJsonGraph.Node) graph.obj)).asObject();
		final JsonObject options = json.get("children").asArray().get(0).asObject().get("labels").asArray().get(0)
				.asObject().get("layoutOptions").asObject();
		// EnumSet order: the declaration order of the proxy enum
		assertEquals("[INSIDE, V_CENTER, H_CENTER]", options.getString("org.eclipse.elk.nodeLabels.placement", null));
	}

	@Test
	void putsEachEdgeInTheNodeElkJavaWould() {
		buildGraph();
		// Lowest common ancestor, a node counting as its own ancestor
		assertSame(root.obj, ac.getContainingNode().obj);
		assertSame(cluster.obj, cd.getContainingNode().obj);
		assertSame(cluster.obj, clusterToC.getContainingNode().obj);
		// A self loop belongs to the parent of its node
		assertSame(cluster.obj, ElkGraphUtil.createSimpleEdge(d, d).getContainingNode().obj);
		assertSame(root.obj, ElkGraphUtil.createSimpleEdge(a, a).getContainingNode().obj);
	}

	@Test
	void readsTheLayoutBack() {
		buildGraph();
		new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor());
		assertEquals(Json.parse(GRAPH), Json.parse(received));

		// Nodes: relative to their parent
		assertEquals(27, a.getX());
		assertEquals(12, a.getY());
		assertEquals(12, cluster.getX());
		assertEquals(91, cluster.getY());
		assertEquals(90, cluster.getWidth());
		assertEquals(135, cluster.getHeight());
		assertEquals(15, c.getX());
		assertEquals(40, c.getY());
		assertSame(cluster.obj, c.getParent().obj);
		assertNull(root.getParent());

		// Edges: relative to their containing node
		final List<ElkEdgeSection> sections = cd.getSections();
		assertEquals(1, sections.size());
		assertEquals(45, sections.get(0).getStartX());
		assertEquals(70, sections.get(0).getStartY());
		assertEquals(45, sections.get(0).getEndX());
		assertEquals(90, sections.get(0).getEndY());
		assertEquals(1, sections.get(0).getBendPoints().size());
		assertEquals(80, sections.get(0).getBendPoints().iterator().next().getY());
		assertEquals(131, ac.getSections().get(0).getEndY());

		final ElkLabel tail = ac.getLabels().iterator().next();
		assertEquals("1", tail.getText());
		assertEquals(70, tail.getX());
		assertEquals(54, tail.getY());
	}

	@Test
	void reportsAnAnswerForAnotherGraph() {
		buildGraph();
		ElkJsonGraph.setLayouter(new ElkJsonLayouter() {
			@Override
			public String layout(String graphJson) {
				return "{\"id\":\"root\",\"children\":[{\"id\":\"n99\"}]}";
			}
		});
		assertThrows(IllegalArgumentException.class,
				() -> new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor()));
	}

}
