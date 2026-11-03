package nonreg.svg;

import static java.nio.charset.StandardCharsets.UTF_8;
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
import net.sourceforge.plantuml.dot.GraphvizRuntimeEnvironment;

/**
 * A node-style edge label takes the rank after the start of its edge. When
 * Graphviz puts the end of the edge above its start - an edge going back to an
 * earlier state - the label must still sit between the two, not past the start
 * with the edge coming back over it.
 * <p>
 * Ranks are Graphviz's, so this runs through Svek (not Smetana) and is skipped
 * when dot is not installed.
 */
class EdgeLabelRankTest extends SvekSvgTest {

	private static final String STATES = String.join("\n", //
			"@startuml", //
			"skinparam stateDiagramEdgeLabelStyle node", //
			"hide empty description", //
			"[*] --> Idle", //
			"Idle --> Busy : start", //
			"Busy --> Done : finish", //
			"Done --> Idle : reset", //
			"@enduml");

	private static final String STATES_LEFT_TO_RIGHT = STATES.replace("@startuml",
			"@startuml\nleft to right direction");

	private static final String STATES_ORTHO = STATES.replace("@startuml", "@startuml\nskinparam linetype ortho");

	private static final String COMPONENTS = String.join("\n", //
			"@startuml", //
			"skinparam componentDiagramEdgeLabelStyle node", //
			"[A] --> [B] : call", //
			"[B] --> [C] : forward", //
			"[C] --> [A] : reply", //
			"@enduml");

	@Test
	void label_of_an_edge_going_back_up_sits_between_its_states() throws IOException {
		final String svg = render(STATES);
		assertBetween(svg, "reset", "Done", "Idle", false);
		assertBetween(svg, "start", "Idle", "Busy", false);
		assertBetween(svg, "finish", "Busy", "Done", false);
	}

	@Test
	void label_of_an_edge_going_back_left_sits_between_its_states() throws IOException {
		final String svg = render(STATES_LEFT_TO_RIGHT);
		assertBetween(svg, "reset", "Done", "Idle", true);
	}

	@Test
	void label_of_an_edge_going_back_up_sits_between_its_states_with_ortho() throws IOException {
		final String svg = render(STATES_ORTHO);
		assertBetween(svg, "reset", "Done", "Idle", false);
	}

	@Test
	void label_of_a_component_edge_going_back_up_sits_between_its_components() throws IOException {
		final String svg = render(COMPONENTS);
		assertBetween(svg, "reply", "C", "A", false);
	}

	/**
	 * The text {@code label} is, on the rank axis (y, or x when
	 * {@code leftToRight}), between the names of {@code from} and {@code to}.
	 */
	private static void assertBetween(String svg, String label, String from, String to, boolean leftToRight) {
		final double at = textCenter(svg, label, leftToRight);
		final double a = textCenter(svg, from, leftToRight);
		final double b = textCenter(svg, to, leftToRight);
		assertTrue(at > Math.min(a, b) && at < Math.max(a, b),
				label + " at " + at + " is not between " + from + " at " + a + " and " + to + " at " + b);
	}

	/** Where the one text {@code text} of {@code svg} is, on the rank axis. */
	private static double textCenter(String svg, String text, boolean leftToRight) {
		// A one-letter text has no textLength: its x is close enough to its centre.
		final Matcher m = Pattern.compile("<text[^>]* x=\"([-\\d.]+)\" y=\"([-\\d.]+)\"(?:[^>]*textLength=\"([-\\d.]+)\")?[^>]*>"
				+ Pattern.quote(text) + "</text>").matcher(svg);
		assertTrue(m.find(), text + " found");
		if (leftToRight)
			return Double.parseDouble(m.group(1)) + (m.group(3) == null ? 0 : Double.parseDouble(m.group(3)) / 2);
		return Double.parseDouble(m.group(2));
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
