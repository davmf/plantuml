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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads what a usage sets directly in its own body, the way the behavioural
 * profiles write their models: <code>:&gt;&gt; name = "Idle";</code>,
 * <code>attribute redefines kind = PseudostateKind::initial;</code>,
 * <code>:&gt;&gt; actions = (sm.actions.a, sm.actions.b);</code>.
 * <p>
 * When the usage does not set a feature, the definition it is typed by (and
 * the definitions that one specializes) is searched, so a user definition such
 * as <code>part def Idle :&gt; State { :&gt;&gt; name = "Idle"; }</code> works
 * too.
 */
public final class SysmlNodes {

	private SysmlNodes() {
	}

	/**
	 * The value bound to a feature in the body of the node, or of its type.
	 *
	 * @param model where to look the type up, or <code>null</code> to read the
	 *              body only
	 */
	public static SysmlValue value(SysmlModel model, SysmlNode node, String name) {
		return value(model, node, name, new HashSet<String>());
	}

	private static SysmlValue value(SysmlModel model, SysmlNode node, String name, Set<String> visited) {
		SysmlValue result = null;
		for (SysmlNode child : node.getChildren())
			if (name.equals(child.getName()) && child.isDefinition() == false && child.getValue() != null)
				result = child.getValue();
		if (result != null || model == null)
			return result;
		final List<String> types = new ArrayList<>(node.getSpecializes());
		if (node.getType() != null)
			types.add(0, node.getType());
		for (String type : types) {
			final SysmlNode def = model.findDefinition(type);
			if (def == null || def == node || visited.add(def.getQualifiedName()) == false)
				continue;
			result = value(model, def, name, visited);
			if (result != null)
				return result;
		}
		return null;
	}

	/** The simplified text of {@link #value}, or <code>null</code>. */
	public static String text(SysmlModel model, SysmlNode node, String name) {
		final SysmlValue v = value(model, node, name);
		return v == null ? null : v.getText();
	}

	/** Like {@link #text} with a default for an unset or empty value. */
	public static String text(SysmlModel model, SysmlNode node, String name, String defaultValue) {
		final String result = text(model, node, name);
		return result == null || result.isEmpty() ? defaultValue : result;
	}

	/**
	 * The items of a value written as a list, <code>(a.b, c.d)</code>, or as one
	 * item: references stay as written, strings lose their quotes.
	 */
	public static List<String> list(SysmlModel model, SysmlNode node, String name) {
		final SysmlValue v = value(model, node, name);
		if (v == null)
			return Collections.emptyList();
		return splitList(v.getRaw());
	}

	/** Splits <code>(a, "b", c.d)</code> at its top-level commas. */
	public static List<String> splitList(String raw) {
		String s = raw.trim();
		if (s.startsWith("(") && s.endsWith(")"))
			s = s.substring(1, s.length() - 1);
		final List<String> result = new ArrayList<>();
		final StringBuilder current = new StringBuilder();
		int depth = 0;
		boolean inString = false;
		for (char c : s.toCharArray()) {
			if (c == '"')
				inString = inString == false;
			if (inString == false) {
				if (c == '(' || c == '[' || c == '{')
					depth++;
				else if (c == ')' || c == ']' || c == '}')
					depth--;
				else if (c == ',' && depth == 0) {
					add(result, current);
					continue;
				}
			}
			current.append(c);
		}
		add(result, current);
		return result;
	}

	private static void add(List<String> result, StringBuilder current) {
		final String item = unquote(current.toString().trim());
		if (item.isEmpty() == false)
			result.add(item);
		current.setLength(0);
	}

	public static String unquote(String s) {
		if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\""))
			return s.substring(1, s.length() - 1);
		return s;
	}

	/** The last segment of <code>A::B</code> or <code>a.b</code>. */
	public static String lastSegment(String qualified) {
		return qualified == null ? null : SysmlParser.lastSegment(qualified);
	}

	/** The named, non-definition members of a body: parts, ports, items... */
	public static List<SysmlNode> members(SysmlNode node) {
		final List<SysmlNode> result = new ArrayList<>();
		for (SysmlNode child : node.getChildren())
			if (child.isDefinition() == false && child.getName() != null && child.getKeyword() != null
					&& child.isKeyword("import") == false && child.isKeyword("doc") == false
					&& child.isKeyword("attribute") == false)
				result.add(child);
		return result;
	}

	/** The direct member with that name, or <code>null</code>. */
	public static SysmlNode member(SysmlNode node, String name) {
		for (SysmlNode child : members(node))
			if (name.equals(child.getName()))
				return child;
		return null;
	}

	/**
	 * The two ends of a connection: written on the declaration
	 * (<code>connect a to b</code>), as <code>connect source to a;</code> and
	 * <code>connect target to b;</code> in its body, or as
	 * <code>end redefines source = a;</code>.
	 *
	 * @return two references, or <code>null</code> when they are not both found
	 */
	public static String[] ends(SysmlNode connection) {
		if (connection.getConnectEnds().size() >= 2)
			return new String[] { connection.getConnectEnds().get(0),
					connection.getConnectEnds().get(connection.getConnectEnds().size() - 1) };
		String source = null;
		String target = null;
		for (SysmlNode child : connection.getChildren()) {
			final List<String> ends = child.getConnectEnds();
			if (ends.size() == 2 && child.getName() == null) {
				if (isSourceEnd(ends.get(0)))
					source = ends.get(1);
				else if (isTargetEnd(ends.get(0)))
					target = ends.get(1);
			} else if (child.isKeyword("end") && child.getValue() != null) {
				if (isSourceEnd(child.getName()))
					source = child.getValue().getRaw();
				else if (isTargetEnd(child.getName()))
					target = child.getValue().getRaw();
			}
		}
		if (source == null || target == null)
			return null;
		return new String[] { source, target };
	}

	private static boolean isSourceEnd(String name) {
		return "source".equals(name);
	}

	private static boolean isTargetEnd(String name) {
		return "target".equals(name) || "sink".equals(name);
	}

}
