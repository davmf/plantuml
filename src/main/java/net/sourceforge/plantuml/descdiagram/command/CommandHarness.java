/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  David Fyfe
 *
 *
 */
package net.sourceforge.plantuml.descdiagram.command;

import java.util.Arrays;
import java.util.Collection;

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

	private static final Collection<String> FIRST_TOKENS = Arrays.asList("harness");

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

	static ColorParser color() {
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
				directives.applyTo(harness);
			}
		}
		return result;
	}

	// Directive values from the bracketed clause. A key the clause does not
	// name is null: a harness block then gets the default, and a restyle line
	// (CommandHarnessRestyle) leaves the current value alone.
	static final class Directives {
		private final Harness.Shape shape;
		private final Harness.Align align;
		private final Integer lane;
		private final Harness.Bundle bundle;

		Directives(Harness.Shape shape, Harness.Align align, Integer lane, Harness.Bundle bundle) {
			this.shape = shape;
			this.align = align;
			this.lane = lane;
			this.bundle = bundle;
		}

		boolean isEmpty() {
			return shape == null && align == null && lane == null && bundle == null;
		}

		// A new harness block: unnamed keys take their defaults.
		void applyTo(Harness harness) {
			harness.setShape(shape == null ? Harness.Shape.AUTO : shape);
			harness.setAlign(align == null ? Harness.Align.AUTO : align);
			harness.setBundle(bundle == null ? Harness.Bundle.ALWAYS : bundle);
			if (lane != null)
				harness.setLane(lane.intValue());
		}

		// A restyle: only the named keys change.
		void overrideOn(Harness harness) {
			if (shape != null)
				harness.setShape(shape);
			if (align != null)
				harness.setAlign(align);
			if (bundle != null)
				harness.setBundle(bundle);
			if (lane != null)
				harness.setLane(lane.intValue());
		}
	}

	// Parse the bracketed directive list. Returns the directives (all null
	// when no clause is present), or null when the clause is malformed or
	// names an unknown key/value.
	static Directives parseDirectives(String props) {
		Harness.Shape shape = null;
		Harness.Align align = null;
		Integer lane = null;
		Harness.Bundle bundle = null;
		if (props == null || props.trim().isEmpty())
			return new Directives(shape, align, lane, bundle);

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
			} else if (key.equalsIgnoreCase("bundle")) {
				final Harness.Bundle parsed = bundleValue(value);
				if (parsed == null)
					return null;
				bundle = parsed;
			} else {
				return null;
			}
		}
		return new Directives(shape, align, lane, bundle);
	}

	private static Harness.Bundle bundleValue(String value) {
		if (value.equalsIgnoreCase("always"))
			return Harness.Bundle.ALWAYS;
		if (value.equalsIgnoreCase("auto"))
			return Harness.Bundle.AUTO;
		if (value.equalsIgnoreCase("never"))
			return Harness.Bundle.NEVER;
		return null;
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

	@Override
	public Collection<String> mandatoryFirstTokensFast() {
		return FIRST_TOKENS;
	}

}
