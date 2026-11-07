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
package net.sourceforge.plantuml.sysml;

import java.util.Set;

/**
 * Text helpers shared by the generators of <code>!include_sysml</code>: they
 * all write PlantUML source that goes through the preprocessor, with labels
 * passed as string arguments to label functions.
 */
public final class SysmlText {

	private SysmlText() {
	}

	/** Makes a model string safe inside a double-quoted preprocessor string. */
	public static String quote(String s) {
		if (s == null)
			return "";
		return s.replace('"', '\'').replace("\r", "").replace("\n", " ");
	}

	/** The values as quoted, comma-separated function arguments. */
	public static String args(String... values) {
		final StringBuilder sb = new StringBuilder();
		for (String v : values) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append('"').append(quote(v)).append('"');
		}
		return sb.toString();
	}

	/** A call to a label function, e.g. <code>$sysml_state_label("Idle")</code>. */
	public static String call(String function, String... values) {
		return function + "(" + args(values) + ")";
	}

	/** One <code>&lt;&lt;name&gt;&gt;</code> per non-empty name, each led by a space. */
	public static String stereotypes(String... names) {
		final StringBuilder sb = new StringBuilder();
		for (String n : names)
			if (n != null && n.isEmpty() == false)
				sb.append(" <<").append(n.replace('>', '_').replace('<', '_')).append(">>");
		return sb.toString();
	}

	/** A PlantUML identifier made from a model name. */
	public static String sanitize(String name) {
		final StringBuilder sb = new StringBuilder();
		if (name != null)
			for (char c : name.toCharArray())
				sb.append(Character.isLetterOrDigit(c) || c == '_' ? c : '_');
		if (sb.length() == 0 || Character.isDigit(sb.charAt(0)))
			sb.insert(0, '_');
		return sb.toString();
	}

	/** The base itself, or the base with the first free <code>_2</code>, <code>_3</code>... */
	public static String unique(String base, Set<String> used) {
		String result = base;
		for (int i = 2; used.add(result) == false; i++)
			result = base + "_" + i;
		return result;
	}

	/** Is the string a preprocessor-safe variable suffix? */
	public static boolean isIdentifier(String s) {
		return s != null && s.matches("[A-Za-z_][A-Za-z0-9_]*");
	}

	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

}
