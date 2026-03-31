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
				new RegexLeaf("\\{"), RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.LINE);
	}

	@Override
	protected CommandExecutionResult executeArg(DescriptionDiagram diagram, LineLocation location, RegexResult arg,
			ParserPass currentPass) throws NoSuchColorException {
		final String label = arg.get("LABEL", 0);
		final CommandExecutionResult result = diagram.gotoHarness(label);
		if (result.isOk()) {
			final Harness harness = diagram.getCurrentHarness();
			if (harness != null) {
				final Colors colors = color().getColor(arg,
						diagram.getSkinParam().getIHtmlColorSet());
				if (colors != null)
					harness.setColors(colors);
			}
		}
		return result;
	}
}
