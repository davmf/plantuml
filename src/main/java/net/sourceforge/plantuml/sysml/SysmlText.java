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

	/**
	 * Removes the reStructuredText directive blocks from a doc comment: a line
	 * <code>.. name::</code> and the indented or blank lines after it (its
	 * options and content). StrictDoc writes images into doc comments this way
	 * (<code>.. image:: _assets/chart.svg</code> followed by
	 * <code>:alt:</code>); a diagram box would show them as text.
	 *
	 * @return the text without them, trimmed; <code>null</code> stays
	 *         <code>null</code>
	 */
	public static String withoutDirectives(String text) {
		if (text == null || text.contains(".. ") == false)
			return text;
		final StringBuilder sb = new StringBuilder();
		boolean inDirective = false;
		for (String line : text.split("\n", -1)) {
			if (line.trim().matches("\\.\\. [A-Za-z][A-Za-z0-9_-]*::.*")
					&& (line.isEmpty() || Character.isWhitespace(line.charAt(0)) == false)) {
				inDirective = true;
				continue;
			}
			if (inDirective && (line.trim().isEmpty() || Character.isWhitespace(line.charAt(0))))
				continue;
			inDirective = false;
			sb.append(line).append('\n');
		}
		return sb.toString().trim();
	}

	/**
	 * The link of an element, <code> [[url]]</code>, from the pattern of
	 * <code>links=</code>: <code>{id}</code> becomes the ID, percent-encoded.
	 * Empty without <code>links=</code> or without an ID: only items (and
	 * elements with a short name) are pages of their own.
	 */
	public static String link(SysmlOptions options, String id) {
		final String pattern = options.get(SysmlOptions.LINKS, null);
		if (pattern == null || pattern.isEmpty() || id == null)
			return "";
		return " [[" + pattern.replace("{id}", encode(id)) + "]]";
	}

	/** Percent-encodes what is not an unreserved URL character (UTF-8). */
	static String encode(String s) {
		final StringBuilder sb = new StringBuilder();
		final byte[] bytes = s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
		for (byte b : bytes) {
			final int c = b & 0xFF;
			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_'
					|| c == '.' || c == '~')
				sb.append((char) c);
			else
				sb.append('%').append("0123456789ABCDEF".charAt(c >> 4)).append("0123456789ABCDEF".charAt(c & 15));
		}
		return sb.toString();
	}

	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

}
