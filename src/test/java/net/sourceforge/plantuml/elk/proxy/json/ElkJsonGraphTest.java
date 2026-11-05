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
import net.sourceforge.plantuml.elk.proxy.core.math.KVector;
import net.sourceforge.plantuml.elk.proxy.core.options.CoreOptions;
import net.sourceforge.plantuml.elk.proxy.core.options.Direction;
import net.sourceforge.plantuml.elk.proxy.core.options.EdgeLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.options.HierarchyHandling;
import net.sourceforge.plantuml.elk.proxy.core.options.NodeLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.options.PortConstraints;
import net.sourceforge.plantuml.elk.proxy.core.options.PortSide;
import net.sourceforge.plantuml.elk.proxy.core.util.NullElkProgressMonitor;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdgeSection;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.graph.ElkPort;
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

	/**
	 * A board with a child chip and an EAST port (index, border offset, anchor,
	 * label), and a sensor with a WEST port: an edge from port to port, and one
	 * from the chip to the port of its board. What {@link #buildPortGraph()}
	 * writes.
	 */
	private static final String PORT_GRAPH = "{\"id\":\"root\",\"layoutOptions\":{\"org.eclipse.elk.direction\":\"RIGHT\",\"org.eclipse.elk.h"
			+ "ierarchyHandling\":\"INCLUDE_CHILDREN\"},\"width\":0,\"height\":0,\"children\":[{\"id\":\"n0\","
			+ "\"layoutOptions\":{\"org.eclipse.elk.portConstraints\":\"FIXED_ORDER\"},\"width\":0,\"height\":"
			+ "0,\"ports\":[{\"id\":\"p1\",\"layoutOptions\":{\"org.eclipse.elk.port.side\":\"EAST\",\"org.ecl"
			+ "ipse.elk.port.index\":0,\"org.eclipse.elk.port.borderOffset\":-5,\"org.eclipse.elk.port.anchor\""
			+ ":\"(5.0,5.0)\"},\"labels\":[{\"id\":\"l2\",\"text\":\"TX\",\"width\":16,\"height\":12}],\"width"
			+ "\":10,\"height\":10}],\"children\":[{\"id\":\"n3\",\"width\":50,\"height\":30}],\"edges\":[{\"i"
			+ "d\":\"e7\",\"sources\":[\"n3\"],\"targets\":[\"p1\"]}]},{\"id\":\"n4\",\"width\":60,\"height\":"
			+ "40,\"ports\":[{\"id\":\"p5\",\"layoutOptions\":{\"org.eclipse.elk.port.side\":\"WEST\"},\"width"
			+ "\":10,\"height\":10}]}],\"edges\":[{\"id\":\"e6\",\"sources\":[\"p1\"],\"targets\":[\"p5\"]}]}";

	/** The answer of elkjs 0.12.1 to {@link #PORT_GRAPH}, layout options left out. */
	private static final String PORT_LAYOUT = "{\"id\":\"root\",\"width\":215,\"height\":81,\"children\":[{\"id\":\"n0\",\"width\":79,\"height"
			+ "\":57,\"ports\":[{\"id\":\"p1\",\"labels\":[{\"id\":\"l2\",\"text\":\"TX\",\"width\":16,\"heigh"
			+ "t\":12,\"x\":11,\"y\":11}],\"width\":10,\"height\":10,\"x\":74,\"y\":22}],\"children\":[{\"id\""
			+ ":\"n3\",\"width\":50,\"height\":30,\"$H\":283,\"x\":12,\"y\":12}],\"edges\":[{\"id\":\"e7\",\"s"
			+ "ources\":[\"n3\"],\"targets\":[\"p1\"],\"sections\":[{\"id\":\"e7_s0\",\"startPoint\":{\"x\":62"
			+ ",\"y\":27},\"endPoint\":{\"x\":79,\"y\":27},\"incomingShape\":\"n3\",\"outgoingShape\":\"p1\"}]"
			+ ",\"container\":\"n0\"}],\"$H\":277,\"x\":12,\"y\":12},{\"id\":\"n4\",\"width\":60,\"height\":40"
			+ ",\"ports\":[{\"id\":\"p5\",\"width\":10,\"height\":10,\"x\":-10,\"y\":15}],\"$H\":285,\"x\":143"
			+ ",\"y\":19}],\"edges\":[{\"id\":\"e6\",\"sources\":[\"p1\"],\"targets\":[\"p5\"],\"sections\":[{"
			+ "\"id\":\"e6_s0\",\"startPoint\":{\"x\":91,\"y\":39},\"endPoint\":{\"x\":133,\"y\":39},\"incomin"
			+ "gShape\":\"p1\",\"outgoingShape\":\"p5\"}],\"container\":\"root\"}],\"$H\":13,\"x\":0,\"y\":0}";

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

	private ElkNode board;
	private ElkNode chip;
	private ElkNode sensor;
	private ElkPort tx;
	private ElkPort rx;
	private ElkLabel txLabel;
	private ElkEdge txToRx;
	private ElkEdge chipToTx;

	/** Built through the proxies, as CucaDiagramFileMakerElk does for component ports. */
	private void buildPortGraph() {
		root = ElkGraphUtil.createGraph();
		root.setProperty(CoreOptions.DIRECTION, Direction.RIGHT);
		root.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.INCLUDE_CHILDREN);

		board = ElkGraphUtil.createNode(root);
		board.setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_ORDER);
		tx = ElkGraphUtil.createPort(board);
		tx.setDimensions(10, 10);
		tx.setProperty(CoreOptions.PORT_SIDE, PortSide.EAST);
		tx.setProperty(CoreOptions.PORT_INDEX, Integer.valueOf(0));
		tx.setProperty(CoreOptions.PORT_BORDER_OFFSET, -5.0);
		tx.setProperty(CoreOptions.PORT_ANCHOR, new KVector(5, 5));
		txLabel = ElkGraphUtil.createLabel(tx);
		txLabel.setText("TX");
		txLabel.setDimensions(16, 12);
		chip = ElkGraphUtil.createNode(board);
		chip.setDimensions(50, 30);

		sensor = ElkGraphUtil.createNode(root);
		sensor.setDimensions(60, 40);
		rx = ElkGraphUtil.createPort(sensor);
		rx.setDimensions(10, 10);
		rx.setProperty(CoreOptions.PORT_SIDE, PortSide.WEST);

		txToRx = ElkGraphUtil.createSimpleEdge(tx, rx);
		chipToTx = ElkGraphUtil.createSimpleEdge(chip, tx);
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
	void writesVectorsAsElkParsesThem() {
		final ElkNode graph = ElkGraphUtil.createGraph();
		ElkGraphUtil.createNode(graph).setProperty(CoreOptions.NODE_SIZE_MINIMUM, new KVector(120, 60.5));
		final JsonObject json = Json.parse(ElkJsonGraph.toJson((ElkJsonGraph.Node) graph.obj)).asObject();
		final JsonObject options = json.get("children").asArray().get(0).asObject().get("layoutOptions").asObject();
		// KVector.toString(), which KVector.parse() reads back
		assertEquals("(120.0,60.5)", options.getString("org.eclipse.elk.nodeSize.minimum", null));
	}

	@Test
	void writesPorts() {
		buildPortGraph();
		assertEquals(Json.parse(PORT_GRAPH), Json.parse(ElkJsonGraph.toJson((ElkJsonGraph.Node) root.obj)));
	}

	@Test
	void putsAPortEdgeWhereItsNodesWouldPutIt() {
		buildPortGraph();
		// A port counts as its node: port to port between siblings, in their parent;
		// from a child to a port of its parent, in that parent
		assertSame(root.obj, txToRx.getContainingNode().obj);
		assertSame(board.obj, chipToTx.getContainingNode().obj);
		// From a port to its own node: a self loop of that node, in its parent
		assertSame(root.obj, ElkGraphUtil.createSimpleEdge(tx, board).getContainingNode().obj);
	}

	@Test
	void readsThePortLayoutBack() {
		buildPortGraph();
		ElkJsonGraph.setLayouter(new ElkJsonLayouter() {
			@Override
			public String layout(String graphJson) {
				received = graphJson;
				return PORT_LAYOUT;
			}
		});
		new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor());
		assertEquals(Json.parse(PORT_GRAPH), Json.parse(received));

		// Ports: relative to their node, which may be outside it
		assertSame(board.obj, tx.getParent().obj);
		assertEquals(74, tx.getX());
		assertEquals(22, tx.getY());
		assertEquals(10, tx.getWidth());
		assertEquals(10, tx.getHeight());
		assertEquals(-10, rx.getX());
		assertEquals(15, rx.getY());
		assertEquals(79, board.getWidth());

		// Port labels: relative to their port
		assertEquals("TX", txLabel.getText());
		assertEquals(11, txLabel.getX());
		assertEquals(11, txLabel.getY());

		// Port edges: relative to their containing node
		assertEquals(91, txToRx.getSections().get(0).getStartX());
		assertEquals(133, txToRx.getSections().get(0).getEndX());
		assertEquals(79, chipToTx.getSections().get(0).getEndX());
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
