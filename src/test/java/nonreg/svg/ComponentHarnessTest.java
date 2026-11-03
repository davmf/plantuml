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

	private static final Pattern LINK_GROUP = Pattern.compile("<g class=\"link\"");
	private static final Pattern TRUNK = Pattern.compile("<line [^>]*style=\"stroke:#00F;stroke-width:4.5;\"");

	@Test
	void harness_links_are_drawn_as_one_bundle() throws IOException {
		final String svg = render(NESTED);
		assertEquals(0, count(LINK_GROUP, svg), "harness links must not also be drawn by Graphviz");
		assertEquals(1, count(TRUNK, svg), "thick trunk in the harness colour");
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
