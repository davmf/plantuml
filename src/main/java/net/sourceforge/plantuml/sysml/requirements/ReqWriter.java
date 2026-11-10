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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlException;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlText;
import net.sourceforge.plantuml.sysml.SysmlWriter;
import net.sourceforge.plantuml.sysml.requirements.ReqModel.Element;
import net.sourceforge.plantuml.sysml.requirements.ReqModel.Relation;

/**
 * Writes the requirements view as class diagram source.
 * <p>
 * A class diagram, because its boxes have compartments (the keyword, the id
 * and the name in the header, the doc text below, as SysML draws a
 * requirement), its <code>+--</code> link is SysML's containment (a circled
 * plus on the owner) and its stereotypes can be styled. Every element is a
 * <code>class</code> stereotyped with its kind, the focused one with
 * <code>&lt;&lt;focus&gt;&gt;</code> and a reference that names nothing with
 * <code>&lt;&lt;unresolved&gt;&gt;</code> (dashed); relations are links
 * stereotyped with their kind, drawn from the supplier (above) to the client
 * (below).
 */
final class ReqWriter extends SysmlWriter {

	/** Longest doc text shown, in characters. */
	static final int TEXT_LENGTH = 160;

	private final ReqModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final Set<String> aliases = new HashSet<>();

	/**
	 * @param root the package to draw, or <code>null</code> for the whole file
	 *             set
	 */
	ReqWriter(ReqModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
	}

	private boolean inRoot(Element e) {
		if (root == null)
			return true;
		for (SysmlNode n = e.node; n != null; n = n.getParent())
			if (n == root)
				return true;
		return false;
	}

	/** The element <code>focus=</code> names: its short id, qualified name or name. */
	private Element findFocus(String written) throws SysmlException {
		// Names are unquoted in the model: 'Flight time' is Flight time
		final String focus = written.replace("'", "").trim();
		final List<Element> byName = new ArrayList<>();
		for (Element e : model.getElements()) {
			if (inRoot(e) == false)
				continue;
			if (focus.equals(e.id) || focus.equals(e.getQualifiedName()))
				return e;
			if (focus.equals(e.name))
				byName.add(e);
		}
		if (byName.size() == 1)
			return byName.get(0);
		if (byName.isEmpty()) {
			for (Element e : model.getElements())
				if (inRoot(e) && qualifiedById(e).equals(focus))
					return e;
			throw new SysmlException("focus '" + written + "' names no element"
					+ (root == null ? "" : " in " + root.getQualifiedName()) + " (expected a short id, a name or a qualified name)");
		}
		final List<String> names = new ArrayList<>();
		for (Element e : byName)
			names.add(e.getQualifiedName());
		throw new SysmlException("focus '" + written + "' is ambiguous (" + String.join(", ", names)
				+ "): use the short id or the qualified name");
	}

	/** <code>Reqs::REQ-001</code> for <code>requirement &lt;'REQ-001'&gt; speed</code> in Reqs. */
	private static String qualifiedById(Element e) {
		if (e.id == null)
			return e.getQualifiedName();
		final List<String> p = new ArrayList<>(e.path.subList(0, e.path.size() - 1));
		p.add(e.id);
		return String.join("::", p);
	}

	private int depth(boolean focused) throws SysmlException {
		final String value = options.get(ReqView.DEPTH, null);
		if (value == null)
			return focused ? 1 : 0;
		try {
			final int result = Integer.parseInt(value.trim());
			if (result >= 0)
				return result;
		} catch (NumberFormatException e) {
			// reported below
		}
		throw new SysmlException("option depth=" + value + ": expected a whole number, 0 or more");
	}

	/**
	 * The elements drawn: the focus (or every requirement of the root) and
	 * what lies within <code>depth</code> relations of it, following
	 * relations towards their supplier (<code>up</code>), their client
	 * (<code>down</code>) or both.
	 */
	private Set<Element> select(Element focus, int depth) {
		final Map<Element, List<Relation>> outgoing = new HashMap<>();
		final Map<Element, List<Relation>> incoming = new HashMap<>();
		for (Relation r : model.getRelations()) {
			add(outgoing, r.from, r);
			add(incoming, r.to, r);
		}
		final String direction = options.get(ReqView.DIRECTION, "both");
		final boolean up = direction.equals("down") == false;
		final boolean down = direction.equals("up") == false;
		final Set<Element> result = new LinkedHashSet<>();
		List<Element> frontier = new ArrayList<>();
		if (focus != null)
			frontier.add(focus);
		else
			for (Element e : model.getElements())
				if (e.isRequirement() && inRoot(e))
					frontier.add(e);
		result.addAll(frontier);
		for (int hop = 0; hop < depth && frontier.isEmpty() == false; hop++) {
			final List<Element> next = new ArrayList<>();
			for (Element e : frontier) {
				if (up && outgoing.containsKey(e))
					for (Relation r : outgoing.get(e))
						if (result.add(r.to) && isTerminal(r) == false)
							next.add(r.to);
				if (down && incoming.containsKey(e))
					for (Relation r : incoming.get(e))
						if (result.add(r.from) && isTerminal(r) == false)
							next.add(r.from);
			}
			frontier = next;
		}
		return result;
	}

	/**
	 * A subject or a type reference of a member is shown but not followed:
	 * a configuration item that hundreds of verifications name as their
	 * subject would otherwise pull them all in.
	 */
	private static boolean isTerminal(Relation r) {
		return ReqModel.SUBJECT.equals(r.kind) || ReqModel.REFERENCES.equals(r.kind);
	}

	private static void add(Map<Element, List<Relation>> map, Element key, Relation r) {
		List<Relation> list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.add(r);
	}

	/** A package frame and what it holds. */
	private static final class Frame {
		final SysmlNode node;
		final String qualifiedName;
		final Map<String, Frame> children = new LinkedHashMap<>();
		final List<Element> elements = new ArrayList<>();

		Frame(SysmlNode node, String qualifiedName) {
			this.node = node;
			this.qualifiedName = qualifiedName;
		}
	}

	void run(String source) throws SysmlException {
		final String focusName = options.get(ReqView.FOCUS, null);
		final Element focus = focusName == null ? null : findFocus(focusName);
		final Set<Element> selected = select(focus, depth(focus != null));

		final Map<String, String> attributes = new LinkedHashMap<>();
		if (focus != null) {
			attributes.put("focus", focus.getLabel());
			attributes.put("focusName", focus.name == null ? focus.getLabel() : focus.name);
			attributes.put("focusKind", focus.kind);
		}
		header(source, "requirements", root == null ? "" : root.getName(), attributes);
		defaultFunction("$sysml_element_label", "$id, $name, $kind", //
				"!$sysml_l = \"\"", //
				"!if $kind != \"\"", //
				"!$sysml_l = \"«\" + $kind + \"»\\n\"", //
				"!endif", //
				"!if $id != \"\"", //
				"!$sysml_l = $sysml_l + \"<b>\" + $id + \"</b>\"", //
				"!if $name != \"\" && $name != $id", //
				"!$sysml_l = $sysml_l + \"\\n\"", //
				"!endif", //
				"!endif", //
				"!if $name != $id", //
				"!$sysml_l = $sysml_l + $name", //
				"!endif", //
				"!return $sysml_l");
		defaultFunction("$sysml_text_label", "$text, $id", "!return $text");
		defaultFunction("$sysml_relation_label", "$kind", "!return \"«\" + $kind + \"»\"");
		if (hasConstraints(selected))
			defaultFunction("$sysml_constraint_label", "$text, $kind", "!return $kind + \" { \" + $text + \" }\"");
		out.add("hide circle");
		out.add("hide empty members");
		out.add("hide stereotype");
		if (options.isStyle())
			writeStyle();

		final List<Element> ordered = new ArrayList<>();
		final List<Element> unresolved = new ArrayList<>();
		for (Element e : selected)
			if (e.isUnresolved())
				unresolved.add(e);
		for (Element e : model.getElements())
			if (selected.contains(e))
				ordered.add(e);
		ordered.addAll(unresolved);
		for (Element e : ordered)
			e.alias = SysmlText.unique(SysmlText.sanitize(e.getLabel()), aliases);

		final boolean text = options.getBoolean(ReqView.TEXT, true);
		if (options.getBoolean(ReqView.PACKAGES, false)) {
			final Frame top = new Frame(null, "");
			for (Element e : ordered)
				frame(top, e).elements.add(e);
			writeFrame(top, "", focus, text);
		} else {
			for (Element e : ordered)
				writeElement(e, "", focus, text);
		}

		final Set<String> written = new HashSet<>();
		for (Relation r : model.getRelations())
			if (selected.contains(r.from) && selected.contains(r.to)
					&& written.add(r.from.alias + " " + r.kind + " " + r.to.alias)) {
				writeRelation(r);
				if (r.to.isUnresolved())
					warn(r.location + ": " + label(r.kind) + " '" + r.to.name + "' names no element");
				if (r.from.isUnresolved())
					warn(r.location + ": " + label(r.kind) + " '" + r.from.name + "' names no element");
			}
		for (String w : model.getWarnings())
			warn(w);
		if (selected.isEmpty())
			warn(root == null ? "no requirement found" : "no requirement in " + root.getQualifiedName());
		footer(source);
	}

	/** Whether a box shows a constraint, so that its label function is needed. */
	static boolean hasConstraints(Collection<Element> elements) {
		for (Element e : elements)
			if (constraints(e.node).isEmpty() == false)
				return true;
		return false;
	}

	private Frame frame(Frame top, Element e) {
		Frame f = top;
		for (SysmlNode p : e.packages) {
			if (root != null && isAncestorOrSelf(p, root) && p != root)
				continue;
			final String q = p.getQualifiedName();
			Frame child = f.children.get(q);
			if (child == null) {
				child = new Frame(p, q);
				f.children.put(q, child);
			}
			f = child;
		}
		return f;
	}

	private static boolean isAncestorOrSelf(SysmlNode candidate, SysmlNode node) {
		for (SysmlNode n = node; n != null; n = n.getParent())
			if (n == candidate)
				return true;
		return false;
	}

	private void writeFrame(Frame f, String indent, Element focus, boolean text) {
		for (Element e : f.elements)
			writeElement(e, indent, focus, text);
		for (Frame child : f.children.values()) {
			out.add(indent + "package \"" + SysmlText.quote(child.node.getName()) + "\" as "
					+ SysmlText.unique(SysmlText.sanitize("pkg_" + child.qualifiedName), aliases)
					+ " <<package>> {");
			writeFrame(child, indent + "  ", focus, text);
			out.add(indent + "}");
		}
	}

	private void writeElement(Element e, String indent, Element focus, boolean text) {
		final String kind = e.kind == null ? "" : e.kind;
		final StringBuilder sb = new StringBuilder(indent);
		sb.append("class \"")
				.append(SysmlText.call("$sysml_element_label", e.id == null ? "" : e.id, e.name == null ? "" : e.name,
						kind))
				.append("\" as ").append(e.alias)
				.append(SysmlText.stereotypes(stereotype(kind), e == focus ? "focus" : null,
						e.isUnresolved() ? "unresolved" : null));
		final List<List<String>> compartments = new ArrayList<>();
		if (text) {
			final String doc = clean(e.getDoc());
			if (doc != null)
				compartments.add(Collections.singletonList(
						"{field} " + SysmlText.call("$sysml_text_label", doc, e.id == null ? "" : e.id)));
			compartments.add(constraints(e.node));
		}
		body(out, indent, sb.toString(), compartments);
	}

	/**
	 * Writes a class and its body: one compartment per group of lines that is
	 * not empty, and no body at all when they all are.
	 */
	static void body(List<String> out, String indent, String declaration, List<List<String>> compartments) {
		boolean open = false;
		for (List<String> lines : compartments) {
			if (lines.isEmpty())
				continue;
			out.add(open ? indent + "  --" : declaration + " {");
			open = true;
			for (String line : lines)
				out.add(indent + "  " + line);
		}
		out.add(open ? indent + "}" : declaration);
	}

	/**
	 * One line per constraint expression of a requirement:
	 * <code>require constraint { x &lt;= 3 }</code> and
	 * <code>assume constraint { ... }</code>.
	 */
	static List<String> constraints(SysmlNode node) {
		final List<String> result = new ArrayList<>();
		if (node == null)
			return result;
		for (SysmlNode child : node.getChildren()) {
			if (child.isKeyword("require") == false && child.isKeyword("assume") == false)
				continue;
			for (SysmlNode expression : child.getChildren())
				if (expression.getExpression() != null)
					result.add("{field} "
							+ SysmlText.call("$sysml_constraint_label", expression.getExpression(), child.getKeyword()));
		}
		return result;
	}

	/** <code>requirement def</code> gives <code>requirementDef</code>. */
	static String stereotype(String kind) {
		final StringBuilder sb = new StringBuilder();
		boolean upper = false;
		for (char c : kind.toCharArray()) {
			if (c == ' ') {
				upper = true;
				continue;
			}
			sb.append(upper ? Character.toUpperCase(c) : c);
			upper = false;
		}
		return sb.toString();
	}

	/** The doc text on one line, cut after {@link #TEXT_LENGTH} characters. */
	static String clean(String doc) {
		if (doc == null)
			return null;
		final String s = doc.replaceAll("\\s+", " ").trim();
		if (s.isEmpty())
			return null;
		if (s.length() <= TEXT_LENGTH)
			return s;
		int cut = s.lastIndexOf(' ', TEXT_LENGTH);
		if (cut < TEXT_LENGTH / 2)
			cut = TEXT_LENGTH;
		return s.substring(0, cut) + "…";
	}

	private static String label(String kind) {
		if (ReqModel.DEFINED_BY.equals(kind))
			return "defined by";
		return kind;
	}

	private void writeRelation(Relation r) {
		final String stereotype = SysmlText.stereotypes(r.kind);
		if (ReqModel.CONTAINMENT.equals(r.kind)) {
			out.add(r.to.alias + " +-- " + r.from.alias + stereotype);
			return;
		}
		final String arrow;
		if (ReqModel.SPECIALIZES.equals(r.kind) || ReqModel.SUBSETS.equals(r.kind))
			arrow = " <|-- ";
		else if (ReqModel.DEFINED_BY.equals(r.kind))
			arrow = " <|.. ";
		else
			arrow = " <.. ";
		out.add(r.to.alias + arrow + r.from.alias + stereotype + " : "
				+ SysmlText.call("$sysml_relation_label", label(r.kind)));
	}

	private void writeStyle() {
		out.add("skinparam classAttributeIconSize 0");
		out.add("<style>");
		out.add("classDiagram {");
		out.add("  class { BackGroundColor #FFFFFF; LineColor #404040; MaximumWidth 280 }");
		out.add("  package { LineColor #9E9E9E; FontColor #616161 }");
		out.add("  .requirement { BackGroundColor #EAF1FB }");
		out.add("  .requirementDef { BackGroundColor #DCE7F7 }");
		out.add("  .concern { BackGroundColor #F0EAF7 }");
		out.add("  .verification { BackGroundColor #EAF5EA }");
		out.add("  .verificationDef { BackGroundColor #DDEFDD }");
		out.add("  .unresolved { BackGroundColor #FFFFFF; LineColor #9E9E9E; LineStyle 5; FontColor #757575 }");
		out.add("  .focus { BackGroundColor #FFF3B0; LineColor #D9822B; LineThickness 3 }");
		out.add("  arrow {");
		out.add("    .satisfy { LineColor #2E7D32; FontColor #2E7D32 }");
		out.add("    .verify { LineColor #1565C0; FontColor #1565C0 }");
		out.add("    .derive { LineColor #6A1B9A; FontColor #6A1B9A }");
		out.add("    .refine { LineColor #6A1B9A; FontColor #6A1B9A }");
		out.add("    .allocate { LineColor #EF6C00; FontColor #EF6C00 }");
		out.add("    .definedBy { LineColor #757575; FontColor #757575 }");
		out.add("    .specializes { LineColor #757575; FontColor #757575 }");
		out.add("    .subsets { LineColor #757575; FontColor #757575 }");
		out.add("    .subject { LineColor #757575; FontColor #757575 }");
		out.add("    .references { LineColor #757575; FontColor #757575 }");
		out.add("  }");
		out.add("}");
		out.add("</style>");
	}

}
