package net.sourceforge.plantuml.elk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * State diagrams laid out by ELK ({@code !pragma layout elk}): they must look
 * like the dot ones. Composite states are rounded frames, their entry and exit
 * points sit on the frame, pseudo-states are met on their cardinal points,
 * arrowheads follow their edge, and concurrent regions are stacked.
 *
 * <p>
 * Skipped when ELK is not on the classpath (see {@link ElkPortTest}).
 */
class ElkStateTest {

	private static final double EPSILON = 1;

	@BeforeEach
	void requireElkOnClasspath() {
		Assumptions.assumeTrue(isElkAvailable(), "ELK is not on the classpath - skipping ELK-dependent test");
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
	void transitionToARemovedStateIsDropped() throws IOException {
		final String svg = render("state A $keep\n" + //
				"state B $gone\n" + //
				"state C $keep\n" + //
				"A --> B\n" + //
				"A --> C\n" + //
				"remove $gone\n");

		assertNoError(svg);
		assertTrue(svg.contains(">A<"));
		assertTrue(svg.contains(">C<"));
		assertFalse(svg.contains(">B<"));
	}

	@Test
	void compositeWithAllContentRemovedIsAPlainState() throws IOException {
		final String svg = render("state Outer {\n" + //
				"  state Inner $gone\n" + //
				"}\n" + //
				"[*] --> Outer\n" + //
				"remove $gone\n");

		assertNoError(svg);
		assertTrue(svg.contains(">Outer<"));
		assertFalse(svg.contains(">Inner<"));
	}

	@Test
	void compositeIsARoundedStateFrame() throws IOException {
		final String svg = render("state Comp {\n" + //
				"  A --> B\n" + //
				"}\n" + //
				"[*] --> Comp\n");

		final double[] frame = clusterFrame(svg, "Comp");
		// A state header (a filled path with arcs), not a folder tab
		final String group = clusterGroup(svg, "Comp");
		assertTrue(group.contains(" A12.5,12.5"), group);
		assertTrue(frame[2] > 0 && frame[3] > 0);
	}

	@Test
	void entryAndExitPointsAreCentredOnTheFrame() throws IOException {
		final String svg = render("state Comp {\n" + //
				"  state in1 <<entryPoint>>\n" + //
				"  state out1 <<exitPoint>>\n" + //
				"  in1 --> A\n" + //
				"  A --> out1\n" + //
				"}\n" + //
				"X --> in1\n" + //
				"out1 --> Y\n");

		final double[] frame = clusterFrame(svg, "Comp");
		final double top = frame[1];
		final double bottom = frame[1] + frame[3];
		final List<double[]> circles = pointCircles(svg);
		assertEquals(2, circles.size());
		final double[] in = circles.get(0)[1] < circles.get(1)[1] ? circles.get(0) : circles.get(1);
		final double[] out = in == circles.get(0) ? circles.get(1) : circles.get(0);

		assertEquals(top, in[1], EPSILON, "entry point centred on the top of the frame");
		assertEquals(bottom, out[1], EPSILON, "exit point centred on the bottom of the frame");
		assertTrue(in[0] > frame[0] && in[0] < frame[0] + frame[2]);
		assertTrue(out[0] > frame[0] && out[0] < frame[0] + frame[2]);
	}

	@Test
	void choiceIsMetOnSeparateVertices() throws IOException {
		final String svg = render("state c <<choice>>\n" + //
				"A --> c\n" + //
				"c --> B : [x]\n" + //
				"c --> C : [y]\n" + //
				"c --> D : [z]\n");

		final double[] diamond = diamond(svg);
		final Set<String> vertices = new HashSet<>();
		int ends = 0;
		for (List<double[]> edge : edges(svg))
			for (int i = 0; i < 2; i++) {
				final double[] p = i == 0 ? edge.get(0) : edge.get(edge.size() - 1);
				final double[] q = i == 0 ? edge.get(1) : edge.get(edge.size() - 2);
				if (Math.abs(p[0] - diamond[0]) > diamond[2] + 2 || Math.abs(p[1] - diamond[1]) > diamond[3] + 2)
					continue;
				ends++;
				final boolean vertical = near(p[0], diamond[0]) && near(q[0], p[0]);
				final boolean horizontal = near(p[1], diamond[1]) && near(q[1], p[1]);
				assertTrue(vertical || horizontal, "edge meets the diamond on a vertex, at right angles");
				vertices.add(Math.round(p[0]) + "," + Math.round(p[1]));
			}
		assertEquals(4, ends);
		assertEquals(4, vertices.size(), "one edge per vertex");
	}

	@Test
	void arrowheadsPointAlongTheirLastSegment() throws IOException {
		final String svg = render("skinparam linetype ortho\n" + //
				"state A\n" + //
				"A --> B\n" + //
				"A --> C\n" + //
				"A --> D\n" + //
				"B --> D\n" + //
				"D --> A\n");

		assertStateDiagram(svg);
		final List<List<double[]>> edges = edges(svg);
		assertEquals(5, edges.size());
		final List<double[][]> heads = arrowheads(svg);
		assertEquals(edges.size(), heads.size());
		for (int i = 0; i < edges.size(); i++) {
			final List<double[]> edge = edges.get(i);
			final double[] end = edge.get(edge.size() - 1);
			final double[] before = edge.get(edge.size() - 2);
			final double[][] head = heads.get(i);
			// The tip is the end of the edge; the back of the head is behind it
			assertEquals(end[0], head[0][0], EPSILON);
			assertEquals(end[1], head[0][1], EPSILON);
			final double[] back = head[2];
			final double dx = end[0] - before[0];
			final double dy = end[1] - before[1];
			assertTrue((back[0] - end[0]) * dx + (back[1] - end[1]) * dy < 0, "arrowhead points along the edge");
		}
	}

	@Test
	void bentTransitionsLeaveStatesStraightForAStem() throws IOException {
		final String svg = render("skinparam linetype ortho\n" + //
				"state A\n" + //
				"A --> B\n" + //
				"A --> C\n" + //
				"A --> D\n" + //
				"B --> E\n" + //
				"C --> E\n" + //
				"D --> E\n");

		assertStateDiagram(svg);
		int bent = 0;
		for (List<double[]> edge : edges(svg)) {
			if (edge.size() < 3)
				continue;
			bent++;
			assertTrue(length(edge.get(0), edge.get(1)) >= 24 - EPSILON, "first stem");
			assertTrue(length(edge.get(edge.size() - 1), edge.get(edge.size() - 2)) >= 24 - EPSILON, "last stem");
		}
		assertTrue(bent > 0, "some edges bend");
	}

	@Test
	void concurrentRegionsAreStacked() throws IOException {
		final String svg = render("state Active {\n" + //
				"  [*] --> X\n" + //
				"  --\n" + //
				"  [*] --> P\n" + //
				"}\n" + //
				"[*] --> Active\n");

		assertNoError(svg);
		assertTrue(svg.contains(">X<"));
		assertTrue(svg.contains(">P<"), "second region drawn");
		assertTrue(svg.contains("stroke-dasharray"), "regions separated by a dashed line");
	}

	@Test
	void transitionToACompositeEndsOnItsFrame() throws IOException {
		final String svg = render("state H {\n" + //
				"  H1 --> H2\n" + //
				"}\n" + //
				"A --> H\n");

		final double[] frame = clusterFrame(svg, "H");
		boolean found = false;
		for (List<double[]> edge : edges(svg)) {
			final double[] end = edge.get(edge.size() - 1);
			if (near(end[1], frame[1]) && end[0] > frame[0] && end[0] < frame[0] + frame[2])
				found = true;
		}
		assertTrue(found, "an edge ends on the top of the frame");
	}

	// ----------------------------------------------------------------------

	private static String render(String body) throws IOException {
		final String source = "@startuml\n!pragma layout elk\n" + body + "@enduml\n";
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		new SourceStringReader(source).outputImage(os, new FileFormatOption(FileFormat.SVG));
		return new String(os.toByteArray(), StandardCharsets.UTF_8);
	}

	private static void assertStateDiagram(String svg) {
		assertTrue(svg.contains("data-diagram-type=\"STATE\""), "state diagram");
	}

	private static void assertNoError(String svg) {
		assertFalse(svg.contains("An error has occurred"), "diagram crashed");
		assertFalse(svg.contains("Syntax Error"), "syntax error");
	}

	private static String clusterGroup(String svg, String name) {
		final Matcher m = Pattern.compile("<g class=\"cluster\" data-qualified-name=\"" + Pattern.quote(name)
				+ "\".*?</g>", Pattern.DOTALL).matcher(svg);
		assertTrue(m.find(), "cluster " + name);
		return m.group();
	}

	// x, y, width, height of the frame of a composite state
	private static double[] clusterFrame(String svg, String name) {
		final Matcher m = Pattern
				.compile("<rect x=\"([-0-9.]+)\" y=\"([-0-9.]+)\" width=\"([0-9.]+)\" height=\"([0-9.]+)\"")
				.matcher(clusterGroup(svg, name));
		assertTrue(m.find(), "frame of " + name);
		return new double[] { d(m.group(1)), d(m.group(2)), d(m.group(3)), d(m.group(4)) };
	}

	// centres of the circles of the entry/exit points
	private static List<double[]> pointCircles(String svg) {
		final List<double[]> result = new ArrayList<>();
		final Matcher m = Pattern.compile("<ellipse cx=\"([-0-9.]+)\" cy=\"([-0-9.]+)\" rx=\"6\" ry=\"6\"")
				.matcher(svg);
		while (m.find())
			result.add(new double[] { d(m.group(1)), d(m.group(2)) });
		return result;
	}

	// centre x, y and half width, half height of the (only) choice diamond
	private static double[] diamond(String svg) {
		final Matcher m = Pattern.compile("<polygon points=\"([-0-9.,]+)\" fill=\"#F1F1F1\"").matcher(svg);
		while (m.find()) {
			final List<double[]> pts = points(m.group(1));
			if (pts.size() != 5)
				continue;
			double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE,
					maxY = -Double.MAX_VALUE;
			for (double[] p : pts) {
				minX = Math.min(minX, p[0]);
				maxX = Math.max(maxX, p[0]);
				minY = Math.min(minY, p[1]);
				maxY = Math.max(maxY, p[1]);
			}
			return new double[] { (minX + maxX) / 2, (minY + maxY) / 2, (maxX - minX) / 2, (maxY - minY) / 2 };
		}
		throw new AssertionError("no diamond");
	}

	// The polylines of the edges (straight lines between M/L points)
	private static List<List<double[]>> edges(String svg) {
		final List<List<double[]>> result = new ArrayList<>();
		final Matcher m = Pattern.compile("<path d=\"(M[-0-9., L]+)\" style=\"stroke:[^\"]*\" fill=\"none\"")
				.matcher(svg);
		while (m.find()) {
			final List<double[]> pts = new ArrayList<>();
			for (String token : m.group(1).replace("M", "").split("L"))
				pts.add(points(token.trim()).get(0));
			result.add(pts);
		}
		return result;
	}

	// The polygons of the arrowheads, in the order of the edges
	private static List<double[][]> arrowheads(String svg) {
		final List<double[][]> result = new ArrayList<>();
		final Matcher m = Pattern.compile("<polygon points=\"([-0-9.,]+)\" fill=\"#181818\"").matcher(svg);
		while (m.find())
			result.add(points(m.group(1)).toArray(new double[0][]));
		return result;
	}

	private static List<double[]> points(String list) {
		final String[] n = list.split("[ ,]+");
		final List<double[]> result = new ArrayList<>();
		for (int i = 0; i + 1 < n.length; i += 2)
			result.add(new double[] { d(n[i]), d(n[i + 1]) });
		return result;
	}

	private static double length(double[] a, double[] b) {
		return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
	}

	private static boolean near(double a, double b) {
		return Math.abs(a - b) < EPSILON;
	}

	private static double d(String s) {
		return Double.parseDouble(s);
	}

}
