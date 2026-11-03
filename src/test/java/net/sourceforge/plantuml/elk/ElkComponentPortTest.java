package net.sourceforge.plantuml.elk;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
