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
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


/**
 * Base of the generators: collects the output lines and warnings, and writes
 * what every generated diagram starts and ends with.
 * <p>
 * The conventions are those of the interconnect generator: labels are calls
 * to preprocessor functions that are only defined when the diagram has not
 * defined them first, every element carries stereotypes the diagram can style,
 * and the root's attributes become <code>$sysml_...</code> variables.
 */
public abstract class SysmlWriter {

	protected final List<String> out = new ArrayList<>();
	private final List<String> warnings = new ArrayList<>();
	private final Predicate<String> isFunctionDefined;

	protected SysmlWriter(Predicate<String> isFunctionDefined) {
		this.isFunctionDefined = isFunctionDefined;
	}

	public final List<String> getLines() {
		return Collections.unmodifiableList(out);
	}

	protected final void warn(String warning) {
		if (warnings.contains(warning) == false)
			warnings.add(warning);
	}

	/**
	 * The leading comment and the variables: <code>$sysml_root</code> (the root's
	 * usage name), <code>$sysml_name</code> when the root has one, and one
	 * <code>$sysml_&lt;attribute&gt;</code> per attribute.
	 */
	protected final void header(String source, String profile, String rootName, Map<String, String> attributes) {
		out.add("' ---- !include_sysml " + source + " : " + rootName + " (" + profile + ") ----");
		out.add("!$sysml_root = \"" + SysmlText.quote(rootName) + "\"");
		for (Map.Entry<String, String> ent : attributes.entrySet())
			if (SysmlText.isIdentifier(ent.getKey()) && ent.getValue() != null)
				out.add("!$sysml_" + ent.getKey() + " = \"" + SysmlText.quote(ent.getValue()) + "\"");
	}

	protected final void footer(String source) {
		for (String warning : warnings)
			out.add("' sysml warning: " + warning);
		out.add("' ---- end of !include_sysml " + source + " ----");
	}

	/**
	 * Defines a label function unless the diagram did first. (A default cannot
	 * be guarded by an <code>!if</code>: the preprocessor declares functions
	 * before it evaluates conditions.)
	 */
	/** Has the diagram defined this label function itself, before the directive? */
	protected final boolean isUserDefined(String function) {
		return isFunctionDefined.test(function);
	}

	/** Called by {@link SysmlWriter#walk} for each named member. */
	protected interface Visitor {
		/** @return whether to walk into the member's body */
		boolean visit(SysmlNode node, List<String> path, String category);
	}

	/**
	 * Visits the named members (parts, ports, items, connections...) under a
	 * node, depth first, with their path and their profile type.
	 */
	protected static void walk(SysmlModel model, SysmlNode node, List<String> path, Visitor visitor) {
		for (SysmlNode child : node.getChildren()) {
			if (child.isDefinition() || child.getName() == null || child.getKeyword() == null
					|| child.isKeyword("import") || child.isKeyword("doc") || child.isKeyword("attribute"))
				continue;
			final List<String> p = append(path, child.getName());
			if (visitor.visit(child, p, model.classify(child.getType())))
				walk(model, child, p, visitor);
		}
	}

	protected static List<String> append(List<String> path, String name) {
		final List<String> result = new ArrayList<>(path);
		result.add(name);
		return result;
	}

	protected final void defaultFunction(String name, String parameters, String... body) {
		if (isFunctionDefined.test(name))
			return;
		out.add("!function " + name + "(" + parameters + ")");
		Collections.addAll(out, body);
		out.add("!endfunction");
	}

}
