package net.sourceforge.plantuml.descdiagram.command;

import net.sourceforge.plantuml.abel.Harness;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.ParserPass;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.Colors;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandHarness extends SingleLineCommand2<DescriptionDiagram> {

	public CommandHarness() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandHarness.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("harness"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf(1, "LABEL", "[%g]([^%g]+)[%g]"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(1, "PROPS", "(?:\\[([^\\]]*)\\])?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\{"), RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.LINE);
	}

	@Override
	protected CommandExecutionResult executeArg(DescriptionDiagram diagram, LineLocation location, RegexResult arg,
			ParserPass currentPass) throws NoSuchColorException {
		final String label = arg.get("LABEL", 0);

		// Resolve the optional [key=value,...] clause before mutating diagram
		// state, so an invalid directive fails the command cleanly.
		final String props = arg.get("PROPS", 0);
		final Directives directives = parseDirectives(props);
		if (directives == null)
			return CommandExecutionResult.error("Unknown or invalid harness directive: [" + props + "]");

		final CommandExecutionResult result = diagram.gotoHarness(label);
		if (result.isOk()) {
			final Harness harness = diagram.getCurrentHarness();
			if (harness != null) {
				final Colors colors = color().getColor(arg,
						diagram.getSkinParam().getIHtmlColorSet());
				if (colors != null)
					harness.setColors(colors);
				harness.setShape(directives.shape);
				harness.setAlign(directives.align);
				if (directives.lane != null)
					harness.setLane(directives.lane.intValue());
			}
		}
		return result;
	}

	// Resolved directive values from the bracketed clause.
	private static final class Directives {
		private final Harness.Shape shape;
		private final Harness.Align align;
		private final Integer lane;

		Directives(Harness.Shape shape, Harness.Align align, Integer lane) {
			this.shape = shape;
			this.align = align;
			this.lane = lane;
		}
	}

	// Parse the bracketed directive list. Returns the resolved directives
	// (defaults when no clause is present), or null when the clause is
	// malformed or names an unknown key/value.
	private static Directives parseDirectives(String props) {
		Harness.Shape shape = Harness.Shape.AUTO;
		Harness.Align align = Harness.Align.AUTO;
		Integer lane = null;
		if (props == null || props.trim().isEmpty())
			return new Directives(shape, align, lane);

		for (final String token : props.split(",")) {
			final String trimmed = token.trim();
			if (trimmed.isEmpty())
				continue;
			final int eq = trimmed.indexOf('=');
			if (eq < 0)
				return null;
			final String key = trimmed.substring(0, eq).trim();
			final String value = trimmed.substring(eq + 1).trim();
			if (key.equalsIgnoreCase("shape")) {
				final Harness.Shape parsed = shapeValue(value);
				if (parsed == null)
					return null;
				shape = parsed;
			} else if (key.equalsIgnoreCase("align")) {
				final Harness.Align parsed = alignValue(value);
				if (parsed == null)
					return null;
				align = parsed;
			} else if (key.equalsIgnoreCase("lane")) {
				final Integer parsed = laneValue(value);
				if (parsed == null)
					return null;
				lane = parsed;
			} else {
				return null;
			}
		}
		return new Directives(shape, align, lane);
	}

	private static Harness.Shape shapeValue(String value) {
		if (value.equalsIgnoreCase("auto"))
			return Harness.Shape.AUTO;
		if (value.equalsIgnoreCase("single"))
			return Harness.Shape.SINGLE;
		if (value.equalsIgnoreCase("u"))
			return Harness.Shape.U;
		if (value.equalsIgnoreCase("cap") || value.equalsIgnoreCase("invertedu"))
			return Harness.Shape.CAP;
		return null;
	}

	private static Harness.Align alignValue(String value) {
		if (value.equalsIgnoreCase("auto"))
			return Harness.Align.AUTO;
		if (value.equalsIgnoreCase("src") || value.equalsIgnoreCase("source"))
			return Harness.Align.SRC;
		if (value.equalsIgnoreCase("mid") || value.equalsIgnoreCase("middle"))
			return Harness.Align.MID;
		if (value.equalsIgnoreCase("dst") || value.equalsIgnoreCase("dest"))
			return Harness.Align.DST;
		return null;
	}

	private static Integer laneValue(String value) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
