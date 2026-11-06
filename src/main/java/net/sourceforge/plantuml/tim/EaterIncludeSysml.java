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
package net.sourceforge.plantuml.tim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.text.StringLocated;

/**
 * Reads <code>!include_sysml file.sysml [file2.sysml...] [key=value...]</code>.
 * <p>
 * Variables and functions are expanded first, so a path or an option can come
 * from the diagram. A word containing <code>=</code> is an option; any other
 * word, or a double-quoted string, is a file.
 */
public class EaterIncludeSysml extends Eater {

	private final List<String> files = new ArrayList<>();
	private final Map<String, String> options = new LinkedHashMap<>();

	public EaterIncludeSysml(StringLocated s) {
		super(s);
	}

	@Override
	public void analyze(TContext context, TMemory memory) throws EaterException {
		skipSpaces();
		checkAndEatChar("!include_sysml");
		skipSpaces();
		final String args = context.applyFunctionsAndVariables(memory,
				new StringLocated(this.eatAllToEnd(), getLineLocation()));
		for (String word : split(args)) {
			final int eq = word.indexOf('=');
			if (word.startsWith("\"") == false && eq > 0)
				options.put(word.substring(0, eq).trim(), unquote(word.substring(eq + 1).trim()));
			else
				files.add(unquote(word));
		}
	}

	private static List<String> split(String args) {
		final List<String> result = new ArrayList<>();
		final StringBuilder current = new StringBuilder();
		boolean inQuotes = false;
		for (char c : args.toCharArray()) {
			if (c == '"')
				inQuotes = inQuotes == false;
			if (Character.isWhitespace(c) && inQuotes == false) {
				if (current.length() > 0)
					result.add(current.toString());
				current.setLength(0);
			} else {
				current.append(c);
			}
		}
		if (current.length() > 0)
			result.add(current.toString());
		return result;
	}

	private static String unquote(String s) {
		if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\""))
			return s.substring(1, s.length() - 1);
		return s;
	}

	public List<String> getFiles() {
		return Collections.unmodifiableList(files);
	}

	public Map<String, String> getOptions() {
		return Collections.unmodifiableMap(options);
	}

}
