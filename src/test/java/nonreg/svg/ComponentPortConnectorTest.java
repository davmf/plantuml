package nonreg.svg;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.dot.GraphvizRuntimeEnvironment;
import net.sourceforge.plantuml.error.PSystemError;

/**
 * Links between ports of sibling components are drawn by SvekPortConnector,
 * outside of Graphviz: these tests need the Svek (Graphviz) layout.
 */
class ComponentPortConnectorTest extends SvekSvgTest {

	private static final String BOARD = String.join("\n", //
			"@startuml", //
			"skinparam linetype ortho", //
			"rectangle Board {", //
			"  port \"J1\" as bj1", //
			"  component U1 {", //
			"    port \"a\" as u1a", //
			"    port \"b\" as u1b", //
			"  }", //
			"  component U2 {", //
			"    port \"x\" as u2x", //
			"    port \"y\" as u2y", //
			"  }", //
			"  component U3 {", //
			"    port \"z\" as u3z", //
			"  }", //
			"}", //
			"bj1 --> u1a", //
			"u1b --> u2x", //
			"u1a --> u3z", //
			"u2y --> u3z", //
			"@enduml");

	private static final String INSIDE_LABELS = String.join("\n", //
			"@startuml", //
			"skinparam componentPortLabels inside", //
			"component Controller {", //
			"  portin \"clk\" as c_clk", //
			"  portin \"reset\" as c_rst", //
			"  portout \"data out\" as c_out", //
			"}", //
			"component Sink {", //
			"  portin \"data in\" as s_in", //
			"}", //
			"c_out --> s_in", //
			"@enduml");

	private static final String STEREOTYPE_LABELS = String.join("\n", //
			"@startuml", //
			"skinparam componentPortLabels<<in>> inside", //
			"component A {", //
			"  portout \"inside one\" as a1 <<in>>", //
			"  portout \"inside two\" as a2 <<in>>", //
			"}", //
			"component B {", //
			"  portin \"b1\" as b1", //
			"  portin \"b2\" as b2", //
			"}", //
			"a1 --> b1", //
			"a2 --> b2", //
			"@enduml");

	/** Inside and outside labels mixed within each component. */
	private static final String MIXED_LABELS = String.join("\n", //
			"@startuml", //
			"skinparam componentPortLabels<<in>> inside", //
			"component A {", //
			"  portin \"in A\" as ai <<in>>", //
			"  portin \"top A\" as at", //
			"  portout \"inside one\" as a1 <<in>>", //
			"  portout \"outside two\" as a2", //
			"  portout \"outside three\" as a3", //
			"}", //
			"component B {", //
			"  portin \"b1\" as b1 <<in>>", //
			"  portin \"b2\" as b2", //
			"  portin \"b3\" as b3 <<in>>", //
			"}", //
			"a1 --> b1", //
			"a2 --> b2", //
			"a3 --> b3", //
			"@enduml");

	private static final String PINS = String.join("\n", //
			"@startuml", //
			"skinparam linetype ortho", //
			"component MCU {", //
			"  connector J1 <<left>> {", //
			"    pin \"1\" as j1p1", //
			"    pin \"2\" as j1p2", //
			"  }", //
			"}", //
			"component Sensor {", //
			"  connector P1 <<right>> {", //
			"    pin \"1\" as s1", //
			"    pin \"2\" as s2", //
			"  }", //
			"}", //
			"MCU::J1::j1p1 --> Sensor::P1::s1", //
			"MCU::J1::j1p2 --> Sensor::P1::s2", //
			"@enduml");

	/** A connector drawn by SvekPortConnector: a bare path, with no id and no link group. */
	private static final Pattern CONNECTOR = Pattern.compile("<path d=\"([^\"]+)\" style=\"[^\"]*\" fill=\"none\"/>");
	private static final Pattern LINK_GROUP = Pattern.compile("<g class=\"link\"");
	private static final Pattern NUMBER = Pattern.compile("-?\\d+(?:\\.\\d+)?");

	private static final double EPSILON = 0.5;
	private static final double RADIUS = 6;

	@Test
	void port_links_are_drawn_as_axis_aligned_connectors() throws IOException {
		final String svg = render(BOARD);
		final List<String> connectors = connectorPaths(svg);
		assertEquals(4, connectors.size(), "port connectors");
		assertEquals(0, count(LINK_GROUP, svg), "port links must not also be drawn by Graphviz");
		for (String d : connectors) {
			final List<double[]> corners = corners(d);
			for (int i = 0; i + 1 < corners.size(); i++) {
				final double[] a = corners.get(i);
				final double[] b = corners.get(i + 1);
				assertTrue(Math.abs(a[0] - b[0]) <= EPSILON || Math.abs(a[1] - b[1]) <= EPSILON,
						"connector segment " + a[0] + "," + a[1] + " - " + b[0] + "," + b[1] + " is not axis-aligned");
			}
		}
	}

	@Test
	void routing_diagnostics_stay_off_stderr() throws IOException {
		final PrintStream stderr = System.err;
		final ByteArrayOutputStream captured = new ByteArrayOutputStream();
		System.setErr(new PrintStream(captured, true, "UTF-8"));
		try {
			render(BOARD);
		} finally {
			System.setErr(stderr);
		}
		assertEquals("", new String(captured.toByteArray(), UTF_8), "stderr");
	}

	@Test
	void edge_corner_radius_rounds_port_connectors() throws IOException {
		final String svg = render(BOARD.replace("@startuml\n", "@startuml\n!pragma edge_corner_radius " + (int) RADIUS + "\n"));
		int arcs = 0;
		for (String d : connectorPaths(svg))
			for (double[] c : cubics(d)) {
				final double width = spread(c, 0);
				final double height = spread(c, 1);
				if (width > EPSILON && height > EPSILON) {
					arcs++;
					assertTrue(width <= RADIUS + EPSILON && height <= RADIUS + EPSILON,
							"corner arc " + width + " x " + height + " is bigger than the radius " + RADIUS);
				}
			}
		assertTrue(arcs >= 1, "rounded corners: " + arcs);
	}

	@Test
	void skinparam_puts_port_labels_inside_their_component() throws IOException {
		final String svg = render(INSIDE_LABELS);
		final double[] controller = clusterRect(svg, "Controller");
		assertInside(svg, "clk", controller);
		assertInside(svg, "reset", controller);
		assertInside(svg, "data out", controller);
		assertInside(svg, "data in", clusterRect(svg, "Sink"));
	}

	@Test
	void port_labels_stay_outside_by_default() throws IOException {
		final String svg = render(INSIDE_LABELS.replace("skinparam componentPortLabels inside\n", ""));
		final double[] controller = clusterRect(svg, "Controller");
		assertFalse(isInside(textBox(svg, "clk"), controller), "clk is inside Controller");
		assertFalse(isInside(textBox(svg, "data out"), controller), "data out is inside Controller");
	}

	@Test
	void skinparam_can_be_scoped_by_stereotype() throws IOException {
		final String svg = render(STEREOTYPE_LABELS);
		final double[] a = clusterRect(svg, "A");
		assertInside(svg, "inside one", a);
		assertInside(svg, "inside two", a);
		final double[] b = clusterRect(svg, "B");
		assertFalse(isInside(textBox(svg, "b1"), b), "b1 is inside B");
		assertFalse(isInside(textBox(svg, "b2"), b), "b2 is inside B");
	}

	@Test
	void mixed_label_placements_keep_ports_on_their_component() throws IOException {
		final String svg = render(MIXED_LABELS);
		final double[] a = clusterRect(svg, "A");
		for (String port : new String[] { "ai", "at", "a1", "a2", "a3" })
			assertOnBorder(portCenter(svg, "A." + port), a, port);
		final double[] b = clusterRect(svg, "B");
		for (String port : new String[] { "b1", "b2", "b3" })
			assertOnBorder(portCenter(svg, "B." + port), b, port);
		assertInside(svg, "inside one", a);
		assertInside(svg, "b1", b);
		final double[] two = textBox(svg, "outside two");
		final double[] three = textBox(svg, "outside three");
		assertTrue(two[0] + two[2] <= three[0] || three[0] + three[2] <= two[0],
				"outside labels overlap: " + two[0] + "+" + two[2] + " and " + three[0] + "+" + three[2]);
	}

	@Test
	void pins_of_a_connector_are_linked_with_qualified_names() throws IOException {
		final Diagram diagram = diagram(PINS);
		assertFalse(diagram instanceof PSystemError, "unexpected error: " + errorOf(diagram));
		final String svg = render(PINS);
		// The pins belong to unrelated components, so Graphviz draws these links.
		assertEquals(2, count(LINK_GROUP, svg), "pin links");
		assertTrue(isInside(clusterRect(svg, "MCU.J1"), clusterRect(svg, "MCU")), "J1 is not inside MCU");
		assertTrue(isInside(clusterRect(svg, "Sensor.P1"), clusterRect(svg, "Sensor")), "P1 is not inside Sensor");
	}

	@Test
	void unknown_qualified_endpoint_is_an_error() {
		final Diagram diagram = diagram(PINS.replace("MCU::J1::j1p2 -->", "MCU::J1::nope -->"));
		assertTrue(diagram instanceof PSystemError, "expected an error");
		assertTrue(errorOf(diagram).startsWith("'nope' not found in 'J1'"), errorOf(diagram));
	}

	@Test
	void pin_outside_a_connector_is_an_error() {
		final Diagram diagram = diagram(String.join("\n", //
				"@startuml", //
				"[Other] --> [Another]", //
				"component MCU {", //
				"  pin \"1\" as p1", //
				"}", //
				"@enduml"));
		assertTrue(diagram instanceof PSystemError, "expected an error");
		assertTrue(errorOf(diagram).startsWith("Pin can only be used inside a connector"), errorOf(diagram));
	}

	private static void assertInside(String svg, String label, double[] rect) {
		assertTrue(isInside(textBox(svg, label), rect), label + " is not inside its component");
	}

	/** The centre of the 12x12 square drawn for a port, by qualified name. */
	private static double[] portCenter(String svg, String qualifiedName) {
		final Matcher m = Pattern.compile("<g class=\"entity\" data-qualified-name=\"" + Pattern.quote(qualifiedName)
				+ "\"[^>]*>(?:(?!<g ).)*?<rect x=\"([-\\d.]+)\" y=\"([-\\d.]+)\" width=\"12\" height=\"12\"",
				Pattern.DOTALL).matcher(svg);
		assertTrue(m.find(), "no port " + qualifiedName);
		return new double[] { Double.parseDouble(m.group(1)) + 6, Double.parseDouble(m.group(2)) + 6 };
	}

	private static void assertOnBorder(double[] p, double[] rect, String port) {
		final boolean withinX = p[0] >= rect[0] - EPSILON && p[0] <= rect[0] + rect[2] + EPSILON;
		final boolean withinY = p[1] >= rect[1] - EPSILON && p[1] <= rect[1] + rect[3] + EPSILON;
		final boolean onVertical = Math.abs(p[0] - rect[0]) <= EPSILON || Math.abs(p[0] - rect[0] - rect[2]) <= EPSILON;
		final boolean onHorizontal = Math.abs(p[1] - rect[1]) <= EPSILON || Math.abs(p[1] - rect[1] - rect[3]) <= EPSILON;
		assertTrue((onVertical && withinY) || (onHorizontal && withinX),
				port + " at " + p[0] + "," + p[1] + " is off its component " + rect[0] + "," + rect[1] + " " + rect[2]
						+ "x" + rect[3]);
	}

	/** True when box (x, y, width, height) lies within rect (x, y, width, height). */
	private static boolean isInside(double[] box, double[] rect) {
		return box[0] >= rect[0] - EPSILON && box[1] >= rect[1] - EPSILON
				&& box[0] + box[2] <= rect[0] + rect[2] + EPSILON && box[1] + box[3] <= rect[1] + rect[3] + EPSILON;
	}

	/** Bounding box of a text element: baseline y minus the font size, up to the baseline. */
	private static double[] textBox(String svg, String text) {
		final Matcher m = Pattern.compile("<text x=\"([-\\d.]+)\" y=\"([-\\d.]+)\"[^>]* font-size=\"(\\d+)\"[^>]* textLength=\"([\\d.]+)\"[^>]*>"
				+ Pattern.quote(text) + "</text>").matcher(svg);
		assertTrue(m.find(), "no text " + text);
		final double size = Double.parseDouble(m.group(3));
		return new double[] { Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)) - size,
				Double.parseDouble(m.group(4)), size };
	}

	/** The rectangle (x, y, width, height) of a cluster, by qualified name. */
	private static double[] clusterRect(String svg, String qualifiedName) {
		final Matcher m = Pattern.compile("<g class=\"cluster\" data-qualified-name=\"" + Pattern.quote(qualifiedName)
				+ "\"[^>]*><rect x=\"([-\\d.]+)\" y=\"([-\\d.]+)\" width=\"([\\d.]+)\" height=\"([\\d.]+)\"").matcher(svg);
		assertTrue(m.find(), "no cluster " + qualifiedName);
		return new double[] { Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)),
				Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4)) };
	}

	private static List<String> connectorPaths(String svg) {
		final List<String> result = new ArrayList<>();
		final Matcher m = CONNECTOR.matcher(svg);
		while (m.find())
			result.add(m.group(1));
		return result;
	}

	private static int count(Pattern pattern, String text) {
		int n = 0;
		final Matcher m = pattern.matcher(text);
		while (m.find())
			n++;
		return n;
	}

	private static List<Double> numbers(String d) {
		final List<Double> values = new ArrayList<>();
		final Matcher m = NUMBER.matcher(d);
		while (m.find())
			values.add(Double.parseDouble(m.group()));
		return values;
	}

	/** Each cubic of an SVG path made of "M" then "C"s, as x,y of its start and then its three points. */
	private static List<double[]> cubics(String d) {
		final List<Double> values = numbers(d);
		final List<double[]> result = new ArrayList<>();
		for (int i = 0; i + 7 < values.size(); i += 6) {
			final double[] c = new double[8];
			for (int k = 0; k < 8; k++)
				c[k] = values.get(i + k);
			result.add(c);
		}
		return result;
	}

	/** Extent of the four points of a cubic along x (axis 0) or y (axis 1). */
	private static double spread(double[] cubic, int axis) {
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		for (int k = axis; k < 8; k += 2) {
			min = Math.min(min, cubic[k]);
			max = Math.max(max, cubic[k]);
		}
		return max - min;
	}

	/** The end points of each cubic in an SVG path made of "M" then "C"s. */
	private static List<double[]> corners(String d) {
		final List<Double> values = numbers(d);
		final List<double[]> result = new ArrayList<>();
		result.add(new double[] { values.get(0), values.get(1) });
		for (int i = 6; i + 1 < values.size(); i += 6)
			result.add(new double[] { values.get(i), values.get(i + 1) });
		return result;
	}

	private static Diagram diagram(String source) {
		return new SourceStringReader(source, UTF_8).getBlocks().get(0).getDiagram();
	}

	private static String errorOf(Diagram diagram) {
		if (diagram instanceof PSystemError)
			return ((PSystemError) diagram).getFirstError().getError();
		return null;
	}

	private static String render(String source) throws IOException {
		// Other tests can leave these behind; either one would take the layout off
		// Graphviz and make this test vacuous.
		TitledDiagram.FORCE_SMETANA = false;
		GraphvizRuntimeEnvironment.getInstance().setDotExecutable(null);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new SourceStringReader(source, UTF_8).outputImage(baos, 0, new FileFormatOption(FileFormat.SVG, false));
		return new String(baos.toByteArray(), UTF_8);
	}

}
