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
package net.sourceforge.plantuml.sysml.requirements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;

/**
 * The trace graph of a set of SysML v2 files: the elements a requirements
 * diagram can draw and the relations between them, read the way StrictDoc's
 * SysML backend reads them (<code>strictdoc/backend/sysml/reader.py</code>
 * and <code>diagram.py</code>).
 * <p>
 * Every relation points from the dependent element (the client: what
 * satisfies, verifies, derives, specialises or is contained) to the element it
 * depends on (the supplier, usually a requirement). StrictDoc stores the same
 * relations as Parent relations on the client.
 */
final class ReqModel {

	/** Keywords whose named members are drawable elements. */
	static final Set<String> ELEMENT_KINDS = new HashSet<>(Arrays.asList("requirement", "concern", "verification",
			"analysis", "case", "use case", "analysis case", "verification case", "part", "action", "calc", "item", "port", "state", "occurrence", "constraint",
			"interface", "connection", "allocation", "flow", "view", "viewpoint"));

	/** Keywords of the elements drawn without <code>focus=</code>. */
	static final Set<String> REQUIREMENT_KINDS = new HashSet<>(Arrays.asList("requirement", "concern"));

	private static final Set<String> VERIFICATION_KINDS = new HashSet<>(Arrays.asList("verification", "analysis",
			"verification case", "analysis case"));

	/** Members that never hold or type anything drawn. */
	private static final Set<String> SKIPPED = new HashSet<>(Arrays.asList("import", "alias", "doc", "comment"));

	/** One drawable element, or a dashed box for a reference that names nothing. */
	static final class Element {
		final SysmlNode node;
		/** <code>requirement</code>, <code>verification def</code>... or <code>null</code> */
		final String kind;
		final String id;
		final String name;
		final List<String> path;
		/** The enclosing packages, outermost first. */
		final List<SysmlNode> packages;
		final int index;
		String alias;

		Element(SysmlNode node, String kind, String id, String name, List<String> path, List<SysmlNode> packages,
				int index) {
			this.node = node;
			this.kind = kind;
			this.id = id;
			this.name = name;
			this.path = path;
			this.packages = packages;
			this.index = index;
		}

		boolean isUnresolved() {
			return node == null;
		}

		boolean isRequirement() {
			return node != null && REQUIREMENT_KINDS.contains(node.getKeyword());
		}

		String getQualifiedName() {
			return String.join("::", path);
		}

		String getDoc() {
			return node == null ? null : node.getDoc();
		}

		/** The short id, else the name. */
		String getLabel() {
			return id != null ? id : name;
		}

		@Override
		public String toString() {
			return getQualifiedName();
		}
	}

	/** A relation, from the client to the supplier. */
	static final class Relation {
		final Element from;
		final Element to;
		/** {@link ReqModel#SATISFY}, {@link ReqModel#VERIFY}... */
		final String kind;
		/** Where the relation is written, for warnings. */
		final String location;

		Relation(Element from, Element to, String kind, String location) {
			this.from = from;
			this.to = to;
			this.kind = kind;
			this.location = location;
		}
	}

	static final String SATISFY = "satisfy";
	static final String VERIFY = "verify";
	static final String DERIVE = "derive";
	static final String REFINE = "refine";
	static final String DEPENDENCY = "dependency";
	static final String ALLOCATE = "allocate";
	static final String DEFINED_BY = "definedBy";
	static final String SPECIALIZES = "specializes";
	static final String SUBSETS = "subsets";
	static final String SUBJECT = "subject";
	static final String REFERENCES = "references";
	static final String CONTAINMENT = "containment";

	/** A reference read in the files, resolved once every element is known. */
	private static final class Pending {
		final SysmlNode written;
		final List<String> context;
		final Element owner;
		final String kind;
		final List<String> sources;
		final List<String> targets;
		/** Targets name requirements (no path fallback, «requirement» if unresolved). */
		final boolean requirementTargets;
		/** Type references: dropped when they name nothing, as StrictDoc does. */
		final boolean optional;

		Pending(SysmlNode written, List<String> context, Element owner, String kind, List<String> sources,
				List<String> targets, boolean requirementTargets, boolean optional) {
			this.written = written;
			this.context = context;
			this.owner = owner;
			this.kind = kind;
			this.sources = sources;
			this.targets = targets;
			this.requirementTargets = requirementTargets;
			this.optional = optional;
		}
	}

	private final List<Element> elements = new ArrayList<>();
	private final List<Relation> relations = new ArrayList<>();
	private final List<String> warnings = new ArrayList<>();
	private final List<Pending> pending = new ArrayList<>();
	private final Map<String, Element> byId = new HashMap<>();
	private final Map<String, Element> byQualifiedName = new HashMap<>();
	private final Map<String, List<Element>> byLastSegment = new HashMap<>();
	private final Map<String, Element> unresolved = new LinkedHashMap<>();
	private final Map<SysmlNode, Element> byNode = new HashMap<>();

	ReqModel(SysmlModel model) {
		for (SysmlNode root : model.getRoots())
			walk(root, Collections.<String>emptyList(), Collections.<SysmlNode>emptyList(), null);
		for (Pending p : pending)
			resolve(p);
	}

	List<Element> getElements() {
		return Collections.unmodifiableList(elements);
	}

	List<Relation> getRelations() {
		return Collections.unmodifiableList(relations);
	}

	List<String> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	Element getElement(SysmlNode node) {
		return byNode.get(node);
	}

	private static boolean isElement(SysmlNode node) {
		return node.getKeyword() != null && ELEMENT_KINDS.contains(node.getKeyword())
				&& (node.getName() != null || node.getShortName() != null);
	}

	private static String kindOf(SysmlNode node) {
		return node.isDefinition() ? node.getKeyword() + " def" : node.getKeyword();
	}

	private void walk(SysmlNode node, List<String> path, List<SysmlNode> packages, Element owner) {
		for (SysmlNode child : node.getChildren()) {
			if (child.isLibrary())
				continue;
			final String keyword = child.getKeyword();
			if (keyword != null && SKIPPED.contains(keyword))
				continue;
			if (child.isKeyword("package")) {
				final List<SysmlNode> p = new ArrayList<>(packages);
				p.add(child);
				walk(child, child.getName() == null ? path : append(path, child.getName()), p, owner);
			} else if (child.isKeyword("satisfy") || child.isKeyword("verify")) {
				pending.add(new Pending(child, path, owner, keyword, child.getSources(), child.getTargets(), true,
						false));
				walk(child, path, packages, owner);
			} else if (child.isKeyword("dependency")) {
				final String kind = child.getAnnotations().contains("derivation") ? DERIVE
						: child.getAnnotations().contains("refinement") ? REFINE : DEPENDENCY;
				pending.add(new Pending(child, path, owner, kind, child.getSources(), child.getTargets(), true,
						false));
			} else if (child.isKeyword("allocate")) {
				pending.add(new Pending(child, path, owner, ALLOCATE, child.getSources(), child.getTargets(), false,
						false));
			} else if (child.getAnnotations().contains("derivation") && child.isKeyword("connection")) {
				readDerivation(child, path, owner);
			} else if (isElement(child)) {
				final String name = child.getName() != null ? child.getName() : child.getShortName();
				final List<String> p = append(path, name);
				final Element e = new Element(child, kindOf(child), child.getShortName(), child.getName(), p,
						packages, elements.size());
				register(e);
				if (owner != null && isContained(e, owner))
					relations.add(new Relation(e, owner, CONTAINMENT, child.getLocation()));
				for (String type : child.getTypes())
					pending.add(new Pending(child, path, e, DEFINED_BY, Collections.<String>emptyList(),
							Collections.singletonList(type), true, true));
				for (String general : child.getSpecializes())
					pending.add(new Pending(child, path, e, child.isDefinition() ? SPECIALIZES : SUBSETS,
							Collections.<String>emptyList(), Collections.singletonList(general), true, true));
				walk(child, p, packages, e);
			} else {
				// objective, subject, attribute, ref, end... belong to the owner
				if (owner != null && child.isDefinition() == false)
					for (String type : child.getTypes())
						pending.add(new Pending(child, path, owner, child.isKeyword("subject") ? SUBJECT : REFERENCES,
								Collections.<String>emptyList(), Collections.singletonList(type), true, true));
				walk(child, child.getName() == null ? path : append(path, child.getName()), packages, owner);
			}
		}
	}

	/** Nested requirements, and verifications nested in verifications. */
	private static boolean isContained(Element child, Element owner) {
		final String c = child.node.getKeyword();
		return REQUIREMENT_KINDS.contains(c)
				|| (VERIFICATION_KINDS.contains(c) && VERIFICATION_KINDS.contains(owner.node.getKeyword()));
	}

	/**
	 * <code>#derivation connection { end #original ::&gt; a; end #derive ::&gt; b; }</code>:
	 * each derived requirement depends on the original one.
	 */
	private void readDerivation(SysmlNode connection, List<String> path, Element owner) {
		final List<String> originals = new ArrayList<>();
		final List<String> derived = new ArrayList<>();
		for (SysmlNode end : connection.getChildren()) {
			final List<String> refs = new ArrayList<>(end.getSpecializes());
			if (refs.isEmpty() && end.getValue() != null)
				refs.add(end.getValue().getRaw());
			if (refs.isEmpty() && end.getName() != null && end.isRedefines() == false)
				refs.add(end.getName());
			if (end.getAnnotations().contains("original"))
				originals.addAll(refs);
			else if (end.getAnnotations().contains("derive"))
				derived.addAll(refs);
		}
		if (originals.isEmpty() || derived.isEmpty()) {
			warnings.add(connection.getLocation() + ": #derivation connection without #original and #derive ends");
			return;
		}
		pending.add(new Pending(connection, path, owner, DERIVE, derived, originals, true, false));
	}

	private void register(Element e) {
		elements.add(e);
		byNode.put(e.node, e);
		if (e.id != null) {
			if (byId.containsKey(e.id))
				warnings.add(e.node.getLocation() + ": duplicate short id '" + e.id + "' (also "
						+ byId.get(e.id).node.getLocation() + ")");
			else
				byId.put(e.id, e);
		}
		final String qualified = e.getQualifiedName();
		if (byQualifiedName.containsKey(qualified) == false)
			byQualifiedName.put(qualified, e);
		index(e.path.get(e.path.size() - 1), e);
		if (e.id != null && e.name != null) {
			final List<String> idPath = new ArrayList<>(e.path.subList(0, e.path.size() - 1));
			idPath.add(e.id);
			final String q = String.join("::", idPath);
			if (byQualifiedName.containsKey(q) == false)
				byQualifiedName.put(q, e);
			index(e.id, e);
		}
	}

	private void index(String segment, Element e) {
		List<Element> list = byLastSegment.get(segment);
		if (list == null) {
			list = new ArrayList<>();
			byLastSegment.put(segment, list);
		}
		list.add(e);
	}

	private void resolve(Pending p) {
		final List<Element> from = new ArrayList<>();
		if (p.sources.isEmpty()) {
			if (p.owner == null) {
				warnings.add(p.written.getLocation() + ": '" + p.kind + "' must be inside an element"
						+ (SATISFY.equals(p.kind) ? " or name the element with 'by'" : ""));
				return;
			}
			from.add(p.owner);
		} else {
			for (String s : p.sources)
				from.add(resolveOrUnresolved(s, p.context, p.requirementTargets && SATISFY.equals(p.kind) == false));
		}
		for (String t : p.targets) {
			Element to = resolve(t, p.context, p.requirementTargets);
			if (to == null) {
				if (p.optional)
					continue;
				to = unresolved(t, p.requirementTargets);
			}
			for (Element f : from)
				if (f != to)
					relations.add(new Relation(f, to, p.kind, p.written.getLocation()));
		}
	}

	private Element resolveOrUnresolved(String reference, List<String> context, boolean requirement) {
		final Element result = resolve(reference, context, requirement);
		return result != null ? result : unresolved(reference, requirement);
	}

	private Element unresolved(String reference, boolean requirement) {
		final String text = requirement ? lastSegment(reference) : reference;
		final String key = (requirement ? "requirement:" : ":") + text;
		Element result = unresolved.get(key);
		if (result == null) {
			result = new Element(null, requirement ? "requirement" : null, null, text,
					Collections.singletonList(text), Collections.<SysmlNode>emptyList(), -1 - unresolved.size());
			unresolved.put(key, result);
		}
		return result;
	}

	private static String lastSegment(String reference) {
		final int colons = reference.lastIndexOf("::");
		if (colons >= 0)
			return reference.substring(colons + 2).trim();
		if (reference.matches(".*[^A-Za-z0-9_.].*"))
			// 'SYS-001.1' rather than a feature chain
			return reference.trim();
		final List<String> segments = split(reference);
		return segments.isEmpty() ? reference : segments.get(segments.size() - 1);
	}

	/**
	 * Finds what a reference names: a short id, a name in an enclosing scope, or
	 * the element whose path ends with the reference (the one closest to where
	 * it is written wins). A path that names nothing resolves to the deepest
	 * element its leading segments name (<code>vehicle.engine.port</code> to
	 * <code>engine</code>), as StrictDoc resolves <code>satisfy ... by</code>,
	 * unless it names a requirement.
	 *
	 * @return the element, or <code>null</code>
	 */
	Element resolve(String reference, List<String> context, boolean requirement) {
		// A short id may hold dots: 'SYS-001.1'
		final String trimmed = reference.trim();
		if (byId.containsKey(trimmed))
			return byId.get(trimmed);
		final int colons = trimmed.lastIndexOf("::");
		if (colons >= 0 && byId.containsKey(trimmed.substring(colons + 2)))
			return byId.get(trimmed.substring(colons + 2));
		final List<String> segments = split(reference);
		if (segments.isEmpty())
			return null;
		for (int length = segments.size(); length >= 1; length--) {
			final Element found = find(segments.subList(0, length), context);
			if (found != null || requirement)
				return found;
		}
		return null;
	}

	private Element find(List<String> segments, List<String> context) {
		for (int length = context.size(); length >= 0; length--) {
			final List<String> candidate = new ArrayList<>(context.subList(0, length));
			candidate.addAll(segments);
			final Element e = byQualifiedName.get(String.join("::", candidate));
			if (e != null)
				return e;
		}
		final List<Element> candidates = byLastSegment.get(segments.get(segments.size() - 1));
		if (candidates == null)
			return null;
		Element best = null;
		int bestScore = -1;
		for (Element e : candidates) {
			if (endsWith(e, segments) == false)
				continue;
			final int score = commonPrefix(e.path, context);
			if (score > bestScore) {
				best = e;
				bestScore = score;
			}
		}
		return best;
	}

	private static boolean endsWith(Element e, List<String> segments) {
		if (segments.size() > e.path.size())
			return false;
		final int offset = e.path.size() - segments.size();
		for (int i = 0; i < segments.size(); i++) {
			final String s = segments.get(i);
			final String p = e.path.get(offset + i);
			final boolean last = i == segments.size() - 1;
			if (s.equals(p) == false && (last == false || s.equals(e.id) == false))
				return false;
		}
		return true;
	}

	private static int commonPrefix(List<String> a, List<String> b) {
		int n = 0;
		while (n < a.size() && n < b.size() && a.get(n).equals(b.get(n)))
			n++;
		return n;
	}

	static List<String> split(String reference) {
		final List<String> result = new ArrayList<>();
		for (String s : reference.trim().split("\\s*(\\.|::)\\s*"))
			if (s.isEmpty() == false)
				result.add(s);
		return result;
	}

	private static List<String> append(List<String> path, String name) {
		final List<String> result = new ArrayList<>(path);
		result.add(name);
		return result;
	}

}
