package net.sourceforge.plantuml.elk;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * Component ports, port groups, pin headers and harnesses under
 * {@code !pragma layout elk}. Skipped when ELK is not on the classpath (see
 * {@link ElkPortTest}).
 */
class ElkComponentPortTest {

	private static final String PORTS = String.join("\n", //
			"@startuml", //
			"!pragma layout elk", //
			"skinparam ranksep RANKSEP", //
			"component A {", //
			"  port a1", //
			"  port a2", //
			"}", //
			"component B {", //
			"  port b1", //
			"  port b2", //
			"}", //
			"a1 --> b1", //
			"a2 --> b2", //
			"@enduml");

	private static final String HEADER_AND_GROUPS = String.join("\n", //
			"@startuml", //
			"!pragma layout elk", //
			"component Board {", //
			"  group \"Power\" #red {", //
			"    port \"VCC\" as v1", //
			"    port \"GND\" as g1", //
			"  }", //
			"  group \"Signals\" {", //
			"    port \"TX\" as tx", //
			"    port \"RX\" as rx", //
			"  }", //
			"}", //
			"component Hdr <<header>> {", //
			"  port \"1\" as h1", //
			"  port \"2\" as h2", //
			"  port \"3\" as h3", //
			"  port \"4\" as h4", //
			"}", //
			"h1 --> h2", //
			"v1 --> h3", //
			"tx --> h4", //
			"@enduml");

	private static final String HARNESS = String.join("\n", //
			"@startuml", //
			"!pragma layout elk", //
			"component Ctrl {", //
			"  port c1", //
			"  port c2", //
			"}", //
			"component Dev {", //
			"  port d1", //
			"  port d2", //
			"}", //
			"harness \"Bus\" {", //
			"  c1 --> d1", //
			"  c2 --> d2", //
			"}", //
			"@enduml");

	private static final String UNGROUPED = String.join("\n", //
			"@startuml", //
			"left to right direction", //
			"!pragma layout elk", //
			"skinparam component {", //
			"  style rectangle", //
			"  portLabels inside", //
			"}", //
			"component MCU {", //
			"  portout \"PA5\" as pa5", //
			"  portout \"PA6\" as pa6", //
			"  portout \"PA7\" as pa7", //
			"  portout \"GND\" as gnd", //
			"}", //
			"component Interface {", //
			"  portin \"SCLK\" as sclk", //
			"  portin \"MOSI\" as mosi", //
			"  portin \"MISO\" as miso", //
			"  portin \"GND\" as ignd", //
			"}", //
			"pa5 --> sclk", //
			"pa6 --> mosi", //
			"pa7 <-- miso", //
			"gnd -- ignd", //
			"@enduml");

	private static final String TITLED_BOARD = String.join("\n", //
			"@startuml", //
			"left to right direction", //
			"!pragma layout elk", //
			"skinparam component {", //
			"  style rectangle", //
			"  portLabels inside", //
			"  portLabels<<pcba>> outside", //
			"}", //
			"title A board", //
			"rectangle \"Isolator Board\" as iso <<pcba>> {", //
			"  portout \"CE\" as ce <<pcba>>", //
			"  portout \"RE\" as re <<pcba>>", //
			"  component \"U3\\nTMUX1104\\nCE Mux\" as u3 {", //
			"    portin \"A0\" as a0", //
			"    portout \"S1\" as s1", //
			"    portout \"S2\" as s2", //
			"  }", //
			"}", //
			"s1 --> ce", //
			"s2 --> re", //
			"@enduml");

	private static final Pattern TRUNK = Pattern.compile("<path d=\"([^\"]+)\" style=\"stroke:#[0-9A-F]+;stroke-width:4.5;\"");
	private static final Pattern STUB = Pattern.compile(
			"<line x1=\"([-\\d.]+)\" y1=\"([-\\d.]+)\" x2=\"([-\\d.]+)\" y2=\"([-\\d.]+)\" style=\"stroke:#[0-9A-F]+;stroke-width:1;\"");
	private static final Pattern NUMBER = Pattern.compile("-?\\d+(?:\\.\\d+)?");

	private static final Pattern CLUSTER = Pattern.compile(
			"<rect x=\"([-\\d.]+)\" y=\"([-\\d.]+)\" width=\"([\\d.]+)\" height=\"([\\d.]+)\" fill=\"[^\"]*\" style=\"[^\"]*\" rx=\"2.5\"");
	private static final Pattern EDGE = Pattern.compile("<path d=\"[^\"]+\" style=\"[^\"]*\" fill=\"none\"/>");
	private static final Pattern JUMPER = Pattern.compile("<rect [^>]*fill=\"#A0A0A0\"");
	private static final Pattern HEIGHT = Pattern.compile("height=\"(\\d+)px\"");

	private static final double EPSILON = 0.5;

	@BeforeEach
	void requireElkOnClasspath() {
		Assumptions.assumeTrue(isElkAvailable(),
				"ELK (org.eclipse.elk.graph.util.ElkGraphUtil) is not on the classpath - skipping ELK-dependent test");
	}

	private static boolean isElkAvailable() {
		try {
			Class.forName("org.eclipse.elk.graph.util.ElkGraphUtil");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Test
	void ports_are_centred_on_their_cluster_boundary() throws IOException {
		final String svg = render(PORTS.replace("RANKSEP", "40"));
		final List<double[]> clusters = rects(CLUSTER, svg);
		for (String port : new String[] { "A.a1", "A.a2", "B.b1", "B.b2" }) {
			final double[] p = port(svg, port);
			final double cx = p[0] + p[2] / 2;
			final double cy = p[1] + p[3] / 2;
			boolean onBoundary = false;
			for (double[] c : clusters)
				onBoundary |= isOnBoundary(cx, cy, c);
			assertTrue(onBoundary, port + " is not centred on a cluster boundary");
		}
	}

	@Test
	void ranksep_is_honoured() throws IOException {
		final int tight = height(render(PORTS.replace("RANKSEP", "20")));
		final int loose = height(render(PORTS.replace("RANKSEP", "200")));
		assertTrue(loose > tight + 100, "ranksep 20 gives " + tight + "px, ranksep 200 gives " + loose + "px");
	}

	@Test
	void header_pairs_are_joined_by_a_jumper_bar() throws IOException {
		final String svg = render(HEADER_AND_GROUPS);
		assertEquals(1, count(JUMPER, svg), "jumper bars");
		// h1 --> h2 is the jumper: only the two other links are routed.
		assertEquals(2, count(EDGE, svg), "routed edges");
	}

	@Test
	void port_groups_keep_declaration_order_and_colour() throws IOException {
		final String svg = render(HEADER_AND_GROUPS);
		final double[] v1 = port(svg, "Board.v1");
		final double[] g1 = port(svg, "Board.g1");
		final double[] tx = port(svg, "Board.tx");
		final double[] rx = port(svg, "Board.rx");
		assertTrue(v1[1] < g1[1] && g1[1] < tx[1] && tx[1] < rx[1], "ports are not in declaration order");
		assertTrue(portElement(svg, "Board.v1").contains("fill=\"#F00\""), "VCC does not take the group colour");
		assertTrue(portElement(svg, "Board.g1").contains("fill=\"#F00\""), "GND does not take the group colour");
		assertTrue(portElement(svg, "Board.tx").contains("fill=\"#F00\"") == false, "TX takes the Power colour");
	}

	@Test
	void harnesses_are_drawn() throws IOException {
		final String svg = render(HARNESS);
		assertTrue(svg.contains(">Bus</text>"), "harness label");
	}

	@Test
	void ungrouped_ports_keep_declaration_order() throws IOException {
		final String svg = render(UNGROUPED);
		assertInOrder(svg, "MCU.pa5", "MCU.pa6", "MCU.pa7", "MCU.gnd");
		assertInOrder(svg, "Interface.sclk", "Interface.mosi", "Interface.miso", "Interface.ignd");
	}

	@Test
	void titled_board_fits_in_the_drawing() throws IOException {
		final String svg = render(TITLED_BOARD);
		final int height = height(svg);
		for (double[] c : rects(CLUSTER, svg))
			assertTrue(c[1] + c[3] < height - 5, "cluster bottom " + (c[1] + c[3]) + " is at the edge of the "
					+ height + "px drawing");
	}

	@Test
	void harnesses_do_not_cross_each_other() throws IOException {
		// Eight harnesses fanning out from one board to four stacked components,
		// whose rows sit above or below the board ports they connect to.
		final String svg = render(new String(Files.readAllBytes(Paths.get("src/test/resources/elk/ad5941-harnesses.puml")), UTF_8));
		final List<double[]> stubs = new ArrayList<>();
		final Matcher stub = STUB.matcher(svg);
		while (stub.find())
			stubs.add(new double[] { Double.parseDouble(stub.group(1)), Double.parseDouble(stub.group(2)),
					Double.parseDouble(stub.group(3)), Double.parseDouble(stub.group(4)) });
		assertTrue(stubs.size() >= 50, "harness stubs: " + stubs.size());
		final Matcher trunk = TRUNK.matcher(svg);
		int trunks = 0;
		while (trunk.find()) {
			trunks++;
			final List<double[]> corners = corners(trunk.group(1));
			for (int i = 0; i + 1 < corners.size(); i++)
				for (double[] s : stubs)
					assertTrue(crosses(corners.get(i), corners.get(i + 1), s) == false,
							"harness trunk " + corners.get(i)[0] + "," + corners.get(i)[1] + " - "
									+ corners.get(i + 1)[0] + "," + corners.get(i + 1)[1] + " crosses stub "
									+ s[0] + "," + s[1] + " - " + s[2] + "," + s[3]);
		}
		assertEquals(8, trunks, "harness trunks");
	}

	/**
	 * True when the axis-aligned trunk segment a-b and the stub (x1, y1, x2, y2)
	 * cross inside both. A stub ending on the trunk, as a harness's own stubs do,
	 * does not count.
	 */
	private static boolean crosses(double[] a, double[] b, double[] s) {
		final boolean trunkVertical = Math.abs(a[0] - b[0]) < EPSILON;
		final boolean stubHorizontal = Math.abs(s[1] - s[3]) < EPSILON;
		if (trunkVertical && stubHorizontal)
			return strictlyInside(a[0], s[0], s[2]) && strictlyInside(s[1], a[1], b[1]);
		if (trunkVertical == false && stubHorizontal == false)
			return strictlyInside(s[0], a[0], b[0]) && strictlyInside(a[1], s[1], s[3]);
		return false;
	}

	private static boolean strictlyInside(double v, double end1, double end2) {
		return v > Math.min(end1, end2) + EPSILON && v < Math.max(end1, end2) - EPSILON;
	}

	/** The end points of each segment of an SVG path made of "M" then "C"s. */
	private static List<double[]> corners(String d) {
		final List<Double> values = new ArrayList<>();
		final Matcher m = NUMBER.matcher(d);
		while (m.find())
			values.add(Double.parseDouble(m.group()));
		final List<double[]> result = new ArrayList<>();
		result.add(new double[] { values.get(0), values.get(1) });
		for (int i = 6; i + 1 < values.size(); i += 6)
			result.add(new double[] { values.get(i), values.get(i + 1) });
		return result;
	}

	private static void assertInOrder(String svg, String... qualifiedNames) {
		for (int i = 0; i + 1 < qualifiedNames.length; i++)
			assertTrue(port(svg, qualifiedNames[i])[1] < port(svg, qualifiedNames[i + 1])[1],
					qualifiedNames[i] + " is not above " + qualifiedNames[i + 1]);
	}

	private static boolean isOnBoundary(double x, double y, double[] r) {
		final boolean withinY = y >= r[1] - EPSILON && y <= r[1] + r[3] + EPSILON;
		final boolean withinX = x >= r[0] - EPSILON && x <= r[0] + r[2] + EPSILON;
		final boolean onSide = withinY && (Math.abs(x - r[0]) <= EPSILON || Math.abs(x - r[0] - r[2]) <= EPSILON);
		final boolean onTopOrBottom = withinX
				&& (Math.abs(y - r[1]) <= EPSILON || Math.abs(y - r[1] - r[3]) <= EPSILON);
		return onSide || onTopOrBottom;
	}

	private static String portElement(String svg, String qualifiedName) {
		final Matcher m = Pattern.compile("<g class=\"entity\" data-qualified-name=\"" + Pattern.quote(qualifiedName)
				+ "\"[^>]*>(?:<text [^>]*>[^<]*</text>)?<rect [^>]*>").matcher(svg);
		assertTrue(m.find(), "no port " + qualifiedName);
		return m.group();
	}

	/** The glyph (x, y, width, height) of a port, by qualified name. */
	private static double[] port(String svg, String qualifiedName) {
		final Matcher m = Pattern.compile("<rect x=\"([-\\d.]+)\" y=\"([-\\d.]+)\" width=\"([\\d.]+)\" height=\"([\\d.]+)\"")
				.matcher(portElement(svg, qualifiedName));
		assertTrue(m.find(), "no glyph for " + qualifiedName);
		return new double[] { Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)),
				Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4)) };
	}

	private static List<double[]> rects(Pattern pattern, String svg) {
		final List<double[]> result = new ArrayList<>();
		final Matcher m = pattern.matcher(svg);
		while (m.find())
			result.add(new double[] { Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)),
					Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4)) });
		return result;
	}

	private static int height(String svg) {
		final Matcher m = HEIGHT.matcher(svg);
		assertTrue(m.find(), "no height");
		return Integer.parseInt(m.group(1));
	}

	private static int count(Pattern pattern, String text) {
		int n = 0;
		final Matcher m = pattern.matcher(text);
		while (m.find())
			n++;
		return n;
	}

	private static String render(String source) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new SourceStringReader(source, UTF_8).outputImage(baos, 0, new FileFormatOption(FileFormat.SVG, false));
		return new String(baos.toByteArray(), UTF_8);
	}

}
