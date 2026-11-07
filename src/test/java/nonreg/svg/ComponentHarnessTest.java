package nonreg.svg;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * Harnesses are drawn by SvekHarness, outside of Graphviz: these tests need the
 * Svek (Graphviz) layout.
 */
class ComponentHarnessTest extends SvekSvgTest {

	private static final String NESTED = String.join("\n", //
			"@startuml", //
			"skinparam linetype ortho", //
			"component Ctrl {", //
			"  port c1", //
			"  port c2", //
			"  port c3", //
			"}", //
			"component Dev1 {", //
			"  port d1", //
			"  port d2", //
			"}", //
			"component Dev2 {", //
			"  port e1", //
			"}", //
			"harness \"Loom A\" #blue {", //
			"  c1 --> d1", //
			"  c2 --> d2", //
			"  harness \"sub\" {", //
			"    c3 --> e1", //
			"  }", //
			"}", //
			"@enduml");

	private static final String DIRECTIVES = String.join("\n", //
			"@startuml", //
			"skinparam linetype ortho", //
			"component Ctrl {", //
			"  port c1", //
			"  port c2", //
			"}", //
			"component Dev1 {", //
			"  port d1", //
			"}", //
			"component Dev2 {", //
			"  port e1", //
			"}", //
			"harness \"H1\" [DIRECTIVES] {", //
			"  c1 --> d1", //
			"  c2 --> e1", //
			"}", //
			"@enduml");

	// Two ports facing two ports: Graphviz lines every pair up straight
	private static final String PAIRS = String.join("\n", //
			"@startuml", //
			"left to right direction", //
			"component A {", //
			"  portout \"1\" as a1", //
			"  portout \"2\" as a2", //
			"}", //
			"component B {", //
			"  portin \"1\" as b1", //
			"  portin \"2\" as b2", //
			"}", //
			"harness \"A-B\" #blue [BUNDLE] {", //
			"  LINKS", //
			"}", //
			"@enduml");

	private static String pairs(String bundle, String links) {
		return PAIRS.replace("[BUNDLE]", bundle).replace("LINKS", links);
	}

	private static final Pattern WIRE = Pattern.compile("<line [^>]*style=\"stroke:#00F;stroke-width:1;\"");

	private static final Pattern LINK_GROUP = Pattern.compile("<g class=\"link\"");
	private static final Pattern TRUNK = Pattern.compile("<line [^>]*style=\"stroke:#00F;stroke-width:4.5;\"");

	@Test
	void harness_links_are_drawn_as_one_bundle() throws IOException {
		final String svg = render(NESTED);
		assertEquals(0, count(LINK_GROUP, svg), "harness links must not also be drawn by Graphviz");
		assertEquals(1, count(TRUNK, svg), "thick trunk in the harness colour");
	}

	@Test
	void bundle_stays_beside_its_source_component() throws IOException {
		final String svg = render(NESTED);
		final Matcher cluster = Pattern.compile("<g class=\"cluster\" data-qualified-name=\"Ctrl\"[^>]*><rect x=\"([-\\d.]+)\" y=\"[-\\d.]+\" width=\"([\\d.]+)\"")
				.matcher(svg);
		assertTrue(cluster.find(), "no cluster Ctrl");
		final double left = Double.parseDouble(cluster.group(1));
		final double right = left + Double.parseDouble(cluster.group(2));

		final Matcher trunk = Pattern.compile("<line x1=\"([-\\d.]+)\"[^>]*style=\"stroke:#00F;stroke-width:4.5;\"").matcher(svg);
		assertTrue(trunk.find(), "no trunk");
		final double trunkX = Double.parseDouble(trunk.group(1));
		assertTrue(trunkX > left && trunkX < right, "trunk at " + trunkX + " is not over Ctrl " + left + ".." + right);

		// No stub loops back round the source component: nothing in the harness colour
		// is drawn to the left of it.
		final Matcher path = Pattern.compile("<path d=\"([^\"]+)\" style=\"stroke:#00F;").matcher(svg);
		while (path.find()) {
			final Matcher n = Pattern.compile("-?\\d+(?:\\.\\d+)?").matcher(path.group(1));
			int i = 0;
			while (n.find())
				if (i++ % 2 == 0)
					assertTrue(Double.parseDouble(n.group()) >= left - 1, "stub goes round Ctrl: " + path.group(1));
		}
	}

	@Test
	void harness_and_nested_harness_labels_are_drawn() throws IOException {
		final String svg = render(NESTED);
		assertEquals(1, count(text("Loom A"), svg), "harness label");
		assertTrue(count(text("sub"), svg) >= 1, "nested harness label on the source stub");
	}

	@Test
	void shape_directive_changes_the_topology() throws IOException {
		final String single = DIRECTIVES.replace("DIRECTIVES", "shape=single");
		final String u = DIRECTIVES.replace("DIRECTIVES", "shape=u");
		assertFalse(diagram(single) instanceof PSystemError, "unexpected error");
		assertFalse(diagram(u) instanceof PSystemError, "unexpected error");
		assertNotEquals(strip(render(single)), strip(render(u)));
	}

	@Test
	void directives_are_accepted() {
		final Diagram diagram = diagram(DIRECTIVES.replace("DIRECTIVES", "shape=cap, align=dst, lane=1"));
		assertFalse(diagram instanceof PSystemError, "unexpected error");
	}

	@Test
	void unknown_directive_is_an_error() {
		final Diagram diagram = diagram(DIRECTIVES.replace("DIRECTIVES", "shape=zigzag"));
		assertTrue(diagram instanceof PSystemError, "expected an error");
		final String error = ((PSystemError) diagram).getFirstError().getError();
		assertTrue(error.startsWith("Unknown or invalid harness directive: [shape=zigzag]"), error);
	}

	@Test
	void bundle_auto_draws_straight_pairs_as_plain_wires() throws IOException {
		final String svg = render(pairs("[bundle=auto]", "a1 -- b1\n  a2 -- b2"));
		assertEquals(0, count(LINK_GROUP, svg), "harness links must not also be drawn by Graphviz");
		assertEquals(0, count(TRUNK, svg), "no trunk");
		assertEquals(2, count(WIRE, svg), "one straight wire per link");
		assertEquals(0, count(text("A-B"), svg), "no harness label without a harness");
	}

	@Test
	void bundle_auto_keeps_the_bundle_when_a_port_fans_out() throws IOException {
		final String svg = render(pairs("[bundle=auto]", "a1 -- b1\n  a1 -- b2"));
		assertEquals(1, count(TRUNK, svg), "trunk");
		assertEquals(1, count(text("A-B"), svg), "harness label");
	}

	@Test
	void bundle_always_is_the_default() throws IOException {
		final String links = "a1 -- b1\n  a2 -- b2";
		final String svg = render(pairs("", links));
		assertEquals(1, count(TRUNK, svg), "trunk");
		assertEquals(strip(svg), strip(render(pairs("[bundle=always]", links))));
	}

	@Test
	void bundle_never_leaves_the_links_to_the_layout() throws IOException {
		final String svg = render(pairs("[bundle=never]", "a1 -- b1\n  a2 -- b2"));
		// Port-to-port links are drawn by SvekPortConnector, as bare paths
		assertEquals(2, count(Pattern.compile("<path d=\"M[^\"]*\" style=\"stroke:#181818;stroke-width:1;\""), svg),
				"ordinary edges");
		assertEquals(0, count(TRUNK, svg), "no trunk");
		assertEquals(0, count(text("A-B"), svg), "no harness label");
	}

	@Test
	void unknown_bundle_value_is_an_error() {
		assertTrue(diagram(pairs("[bundle=sometimes]", "a1 -- b1")) instanceof PSystemError, "expected an error");
	}

	private static Pattern text(String text) {
		return Pattern.compile("<text [^>]*>" + Pattern.quote(text) + "</text>");
	}

	/** The SVG without its trailing encoded source, which differs with the source. */
	private static String strip(String svg) {
		return svg.replaceAll("<\\?plantuml-src [^?]*\\?>", "");
	}

	private static int count(Pattern pattern, String text) {
		int n = 0;
		final Matcher m = pattern.matcher(text);
		while (m.find())
			n++;
		return n;
	}

	private static Diagram diagram(String source) {
		return new SourceStringReader(source, UTF_8).getBlocks().get(0).getDiagram();
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
