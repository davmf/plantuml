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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A set of parsed SysML v2 files, with the name resolution an interconnect
 * diagram needs.
 * <p>
 * Definitions are found by qualified name or, failing that, by simple name.
 * This ignores import visibility on purpose: a diagram should still render
 * when a library is not loaded, or is loaded under another package name.
 */
public final class SysmlModel {

	private final List<SysmlNode> roots = new ArrayList<>();
	private final Map<String, SysmlNode> definitionsByQualifiedName = new HashMap<>();
	private final Map<String, SysmlNode> definitionsBySimpleName = new HashMap<>();
	private final Set<String> stopNames;

	/**
	 * @param stopNames simple names of the library types the caller classifies
	 *                  by (e.g. <code>PCBAUnit</code>): the specialization chain
	 *                  stops at the first of them, whether or not the library
	 *                  file is loaded
	 */
	public SysmlModel(Collection<String> stopNames) {
		this.stopNames = new HashSet<>(stopNames);
	}

	public void add(SysmlNode root) {
		roots.add(root);
		index(root);
	}

	public List<SysmlNode> getRoots() {
		return Collections.unmodifiableList(roots);
	}

	private void index(SysmlNode node) {
		for (SysmlNode child : node.getChildren()) {
			if (child.getName() == null)
				continue;
			if (child.isDefinition()) {
				definitionsByQualifiedName.put(child.getQualifiedName(), child);
				// The first definition of a simple name wins: a user model loaded
				// before a library keeps its own meaning for a shared name
				if (definitionsBySimpleName.containsKey(child.getName()) == false)
					definitionsBySimpleName.put(child.getName(), child);
				index(child);
			} else if (child.isKeyword("package")) {
				index(child);
			}
		}
	}

	public SysmlNode findDefinition(String name) {
		if (name == null)
			return null;
		final SysmlNode result = definitionsByQualifiedName.get(name);
		if (result != null)
			return result;
		return definitionsBySimpleName.get(SysmlParser.lastSegment(name));
	}

	/**
	 * Follows the specialization chain of a type up to the first stop name.
	 *
	 * @return that stop name, or <code>null</code> when the chain never reaches
	 *         one
	 */
	public String classify(String typeName) {
		return classify(typeName, new HashSet<String>());
	}

	private String classify(String typeName, Set<String> visited) {
		if (typeName == null)
			return null;
		final String simple = SysmlParser.lastSegment(typeName);
		if (stopNames.contains(simple))
			return simple;
		final SysmlNode def = findDefinition(typeName);
		if (def == null || visited.add(def.getQualifiedName()) == false)
			return null;
		for (String general : def.getSpecializes()) {
			final String result = classify(general, visited);
			if (result != null)
				return result;
		}
		return classify(def.getType(), visited);
	}

	/** Classifies a resolved feature by its type. */
	public String classify(SysmlFeature feature) {
		return classify(feature.getType());
	}

	/**
	 * Top-level usages (not definitions), searched through packages, in file
	 * order.
	 */
	public List<SysmlNode> getTopLevelUsages() {
		final List<SysmlNode> result = new ArrayList<>();
		for (SysmlNode root : roots)
			collectUsages(root, result);
		return result;
	}

	/** Top-level definitions outside library packages, searched through packages, in file order. */
	public List<SysmlNode> getTopLevelDefinitions() {
		final List<SysmlNode> result = new ArrayList<>();
		for (SysmlNode root : roots)
			collectDefinitions(root, result);
		return result;
	}

	private void collectDefinitions(SysmlNode node, List<SysmlNode> result) {
		for (SysmlNode child : node.getChildren())
			if (child.isKeyword("package") && child.isLibrary() == false)
				collectDefinitions(child, result);
			else if (child.isDefinition() && child.getName() != null)
				result.add(child);
	}

	private void collectUsages(SysmlNode node, List<SysmlNode> result) {
		for (SysmlNode child : node.getChildren())
			if (child.isKeyword("package"))
				collectUsages(child, result);
			else if (child.isDefinition() == false && child.getName() != null && child.getKeyword() != null
					&& child.isKeyword("import") == false && child.isKeyword("doc") == false)
				result.add(child);
	}

	/**
	 * Resolves a usage: its type's chain, then the type body, then its own body.
	 */
	public SysmlFeature resolve(SysmlNode usage) {
		final SysmlFeature result = new SysmlFeature(usage.getName(), usage.getKeyword(), usage, false);
		applyUsage(result, usage, false);
		return result;
	}

	private void applyUsage(SysmlFeature feature, SysmlNode usage, boolean library) {
		if (usage.getType() != null) {
			feature.addType(usage.getType());
			applyType(feature, usage.getType(), new HashSet<String>());
		}
		for (String general : usage.getSpecializes())
			applyType(feature, general, new HashSet<String>());
		if (usage.getValue() != null)
			feature.setValue(usage.getValue());
		applyBody(feature, usage, library);
	}

	private void applyType(SysmlFeature feature, String typeName, Set<String> visited) {
		final SysmlNode def = findDefinition(typeName);
		if (def == null || def.isDefinition() == false || visited.add(def.getQualifiedName()) == false)
			return;
		for (String general : def.getSpecializes())
			applyType(feature, general, visited);
		applyBody(feature, def, def.isInLibrary());
	}

	private void applyBody(SysmlFeature feature, SysmlNode owner, boolean library) {
		if (owner.getDoc() != null)
			feature.setDoc(owner.getDoc());
		for (SysmlNode child : owner.getChildren()) {
			if (child.isDefinition())
				continue;
			if (child.getConnectEnds().isEmpty() == false) {
				if (library == false)
					feature.addConnection(child);
				continue;
			}
			final String name = child.getName();
			if (name == null)
				continue;
			final SysmlFeature existing = feature.getFeature(name);
			if (isAttribute(child, existing)) {
				if (child.getValue() != null)
					feature.putAttribute(name, child.getValue().getText());
				else if (feature.getAttributes().containsKey(name) == false)
					feature.putAttribute(name, null);
			} else if (isStructural(child)) {
				if (child.isRedefines() && existing != null) {
					applyUsage(existing, child, library);
				} else {
					final SysmlFeature nested = new SysmlFeature(name, keywordOf(child, existing), child, library);
					applyUsage(nested, child, library);
					feature.putFeature(nested);
				}
			}
		}
	}

	private static boolean isAttribute(SysmlNode child, SysmlFeature existing) {
		if (child.isKeyword("attribute"))
			return true;
		// Shorthand ":>> designator = ..." on something that is not a nested feature
		return child.getKeyword() == null && existing == null;
	}

	private static boolean isStructural(SysmlNode child) {
		final String k = child.getKeyword();
		return k == null || "part".equals(k) || "port".equals(k) || "item".equals(k) || "ref".equals(k)
				|| "end".equals(k);
	}

	private static String keywordOf(SysmlNode child, SysmlFeature existing) {
		if (child.getKeyword() != null)
			return child.getKeyword();
		return existing == null ? null : existing.getKeyword();
	}

}
