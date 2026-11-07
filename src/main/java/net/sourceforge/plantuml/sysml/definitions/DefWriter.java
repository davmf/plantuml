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
package net.sourceforge.plantuml.sysml.definitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlNodes;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlText;
import net.sourceforge.plantuml.sysml.SysmlWriter;

/**
 * Writes the definitions of a file set as class diagram source.
 * <ul>
 * <li>Each definition is a <code>class</code> stereotyped with its kind
 * (<code>&lt;&lt;part def&gt;&gt;</code>...), <code>abstract</code> when it
 * is; an <code>enum def</code> is an <code>enum</code> listing its
 * literals.</li>
 * <li><code>:&gt;</code> is <code>&lt;|--</code>; a definition nested in
 * another is linked to it with <code>+--</code>.</li>
 * <li>Attributes, directed parameters and the usages whose type is not drawn
 * are fields: <code>name : Type [m..n] = value</code>, the type's last
 * segment only.</li>
 * <li>A usage typed by a drawn definition is a composition
 * <code>*--</code> (an association <code>--&gt;</code> for <code>ref</code>,
 * <code>subject</code>, <code>actor</code> and <code>stakeholder</code>) with
 * its multiplicity and its name; the ends of a connection or interface
 * definition that share a type are one dependency <code>..&gt;</code>.</li>
 * <li>Packages are frames, merged by qualified name across files.</li>
 * </ul>
 */
final class DefWriter extends SysmlWriter {

	/** The usage keywords drawn; the others (perform, exhibit, satisfy...) are relationships. */
	private static final Set<String> USAGES = new HashSet<>(Arrays.asList("part", "item", "port", "action",
			"occurrence", "state", "calc", "constraint", "requirement", "connection", "interface", "flow",
			"allocation", "ref", "end", "subject", "actor", "stakeholder", "case", "use case", "analysis",
			"analysis case", "verification", "verification case", "view", "viewpoint", "concern", "rendering",
			"message", "event", "individual", "snapshot", "timeslice"));

	/** The usages drawn as associations rather than compositions. */
	private static final Set<String> REFERENCES = new HashSet<>(Arrays.asList("ref", "subject", "actor",
			"stakeholder"));

	/** Keeps a label with parentheses a field rather than a method. */
	private static final String FIELD = "{field} ";

	private static final List<String> DIRECTIONS = Arrays.asList("in", "out", "inout", "return");

	private static final class Def {
		final SysmlNode node;
		final String alias;

		Def(SysmlNode node, String alias) {
			this.node = node;
			this.alias = alias;
		}
	}

	private static final class Pkg {
		final SysmlNode node;
		String alias;
		final List<Pkg> packages = new ArrayList<>();
		final List<Def> defs = new ArrayList<>();

		Pkg(SysmlNode node) {
			this.node = node;
		}
	}

	private final SysmlModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final Set<String> aliases = new HashSet<>();
	private final Map<SysmlNode, Def> drawn = new IdentityHashMap<>();
	private final List<Def> defs = new ArrayList<>();
	private final Pkg top = new Pkg(null);
	private final Map<String, Pkg> packages = new LinkedHashMap<>();
	private final boolean attributes;
	private final boolean usages;

	DefWriter(SysmlModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
		this.attributes = options.getBoolean("attributes", true);
		this.usages = options.getBoolean("usages", true);
	}

	private boolean isWholeSet() {
		return root.isKeyword("root");
	}

	// ------------------------------------------------------------------
	// Reading
	// ------------------------------------------------------------------

	/** The named definitions under a node, through packages and definitions, in file order. */
	private static void collectDefinitions(SysmlNode owner, List<SysmlNode> result) {
		for (SysmlNode child : owner.getChildren()) {
			if (child.getName() == null)
				continue;
			if (child.isDefinition())
				add(result, child);
			if (child.isDefinition() || child.isKeyword("package"))
				collectDefinitions(child, result);
		}
	}

	private static void add(List<SysmlNode> list, SysmlNode node) {
		if (node != null && node.getName() != null && list.contains(node) == false)
			list.add(node);
	}

	private void read() {
		final List<SysmlNode> all = new ArrayList<>();
		for (SysmlNode file : model.getRoots())
			collectDefinitions(file, all);

		final List<SysmlNode> core = new ArrayList<>();
		if (isWholeSet()) {
			core.addAll(all);
		} else {
			if (root.isDefinition())
				core.add(root);
			collectDefinitions(root, core);
		}
		List<SysmlNode> scope = core;
		if (root.isDefinition()) {
			final List<SysmlNode> neighbours = new ArrayList<>(core);
			addNeighbours(all, neighbours);
			// In file order
			scope = new ArrayList<>();
			for (SysmlNode n : all)
				if (neighbours.contains(n))
					scope.add(n);
		}

		boolean libraries = options.getBoolean("libraries", false);
		if (libraries == false) {
			libraries = true;
			for (SysmlNode n : core)
				if (n.isInLibrary() == false)
					libraries = false;
		}
		for (SysmlNode n : scope)
			if (libraries || n.isInLibrary() == false) {
				final Def def = new Def(n, SysmlText.unique(SysmlText.sanitize(n.getName()), aliases));
				drawn.put(n, def);
				defs.add(def);
			}
		if (defs.isEmpty())
			warn((isWholeSet() ? "the files" : root.getName()) + ": no definition to draw");
		for (Def def : defs)
			place(def);
	}

	/**
	 * The definitions next to the root definition: its general types and
	 * specializations, the types of its usages and the definitions with a usage
	 * of it.
	 */
	private void addNeighbours(List<SysmlNode> all, List<SysmlNode> scope) {
		for (String general : root.getSpecializes())
			add(scope, definition(general, root));
		for (SysmlNode child : root.getChildren())
			if (child.isDefinition() == false && isShown(child))
				add(scope, definition(child.getType(), root));
		for (SysmlNode d : all) {
			for (String general : d.getSpecializes())
				if (definition(general, d) == root)
					add(scope, d);
			for (SysmlNode child : d.getChildren())
				if (child.isDefinition() == false && isShown(child) && definition(child.getType(), d) == root)
					add(scope, d);
		}
	}

	/**
	 * The definition a name designates where it is written: in the
	 * definitions and packages around the context first (so that two
	 * packages can each have their own <code>Status</code>), then anywhere.
	 */
	private SysmlNode definition(String name, SysmlNode context) {
		if (name == null)
			return null;
		if (name.contains("::") == false)
			for (SysmlNode n = context; n != null; n = n.getParent()) {
				final String qualified = n.getQualifiedName();
				if (qualified == null)
					continue;
				final SysmlNode found = model.findDefinition(qualified + "::" + name);
				if (found != null && found.isDefinition() && (qualified + "::" + name).equals(found.getQualifiedName()))
					return found;
			}
		final SysmlNode result = model.findDefinition(name);
		return result != null && result.isDefinition() ? result : null;
	}

	/** The drawn definition a type written in the context names, or <code>null</code>. */
	private Def drawn(String type, SysmlNode context) {
		final SysmlNode node = definition(type, context);
		return node == null ? null : drawn.get(node);
	}

	private void place(Def def) {
		if (options.getBoolean("packages", true) == false) {
			top.defs.add(def);
			return;
		}
		// The packages that own the definition (a nested definition goes with its owner), outermost first
		final List<SysmlNode> chain = new ArrayList<>();
		for (SysmlNode n = def.node.getParent(); n != null; n = n.getParent()) {
			if (n.isKeyword("package") && n.getName() != null)
				chain.add(0, n);
			if (n == root && n.isKeyword("package"))
				break;
		}
		Pkg owner = top;
		for (SysmlNode p : chain) {
			final String key = p.getQualifiedName();
			Pkg pkg = packages.get(key);
			if (pkg == null) {
				pkg = new Pkg(p);
				packages.put(key, pkg);
				owner.packages.add(pkg);
			}
			owner = pkg;
		}
		owner.defs.add(def);
	}

	private static boolean isEnum(SysmlNode def) {
		return def.isKeyword("enum");
	}

	private static boolean isLiteral(SysmlNode child) {
		return (child.isKeyword("enum") || child.getKeyword() == null) && child.isRedefines() == false;
	}

	private static boolean isAttribute(SysmlNode child) {
		return child.isKeyword("attribute") || child.getKeyword() == null;
	}

	private static String direction(SysmlNode child) {
		for (String m : child.getModifiers())
			if (DIRECTIONS.contains(m))
				return m;
		return "";
	}

	/** A member of a definition body this view can show (not a metadata annotation). */
	private static boolean isMember(SysmlNode child) {
		if (child.isDefinition() || child.getName() == null || child.getConnectEnds().isEmpty() == false
				|| child.hasModifier("@"))
			return false;
		return isAttribute(child) || USAGES.contains(child.getKeyword());
	}

	/** Is the member shown with these options (as a field or a link)? */
	private boolean isShown(SysmlNode child) {
		if (isMember(child) == false)
			return false;
		return isAttribute(child) ? attributes : usages;
	}

	private static boolean isEnd(SysmlNode child) {
		return child.isKeyword("end") || child.hasModifier("end");
	}

	private static boolean isReference(SysmlNode child) {
		return REFERENCES.contains(child.getKeyword()) || child.hasModifier("ref");
	}

	/** The definition a usage is drawn as a link to, or <code>null</code> for a field. */
	private Def target(SysmlNode child) {
		if (isAttribute(child) || direction(child).isEmpty() == false)
			return null;
		return drawn(child.getType(), child.getParent());
	}

	// ------------------------------------------------------------------
	// Writing
	// ------------------------------------------------------------------

	void run(String source) {
		read();
		final String name = rootName();
		final Map<String, String> variables = new LinkedHashMap<>();
		variables.put("name", name);
		header(source, "definitions view", name, variables);
		writeLabelFunctions();
		out.add("hide empty members");
		if ("left".equals(options.get("direction", "top")))
			out.add("left to right direction");
		if (options.isStyle())
			writeStyle();
		// Definitions take the plain names as aliases, packages come second
		for (Pkg pkg : packages.values())
			pkg.alias = SysmlText.unique(SysmlText.sanitize(pkg.node.getName()), aliases);

		String indent = "";
		if (options.isFrame()) {
			out.add("package \"" + SysmlText.call("$sysml_package_label", name, "frame") + "\" as "
					+ SysmlText.unique(SysmlText.sanitize(name), aliases) + " <<Frame>> {");
			indent = "  ";
		}
		writeContents(top, indent);
		if (options.isFrame())
			out.add("}");
		for (Def def : defs)
			writeLinks(def);
		if ("note".equals(options.get("doc", "none")))
			for (Def def : defs)
				if (SysmlText.isEmpty(def.node.getDoc()) == false)
					out.add("note top of " + def.alias + " : " + SysmlText.call("$sysml_doc_label",
							def.node.getName(), def.node.getDoc().replace("\n", "\\n")));
		footer(source);
	}

	/** The root's name; for the whole set, its only top-level package or <code>model</code>. */
	private String rootName() {
		if (isWholeSet() == false)
			return root.getName();
		if (top.defs.isEmpty() && top.packages.size() == 1)
			return top.packages.get(0).node.getName();
		return "model";
	}

	private void writeLabelFunctions() {
		defaultFunction("$sysml_package_label", "$name, $kind", "!return $name");
		defaultFunction("$sysml_def_label", "$name, $kind, $general", //
				"!if $general == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $name + \" :> \" + $general");
		defaultFunction("$sysml_feature_label", "$keyword, $direction, $name, $type, $multiplicity, $value", //
				"!$sysml_l = $name", //
				"!if $keyword != \"\"", //
				"!$sysml_l = $keyword + \" \" + $sysml_l", //
				"!endif", //
				"!if $direction != \"\"", //
				"!$sysml_l = $direction + \" \" + $sysml_l", //
				"!endif", //
				"!if $type != \"\"", //
				"!$sysml_l = $sysml_l + \" : \" + $type", //
				"!endif", //
				"!if $multiplicity != \"\"", //
				"!$sysml_l = $sysml_l + \" [\" + $multiplicity + \"]\"", //
				"!endif", //
				"!if $value != \"\"", //
				"!$sysml_l = $sysml_l + \" = \" + $value", //
				"!endif", //
				"!return $sysml_l");
		defaultFunction("$sysml_literal_label", "$name", "!return $name");
		defaultFunction("$sysml_usage_label", "$keyword, $name", "!return $name");
		if ("note".equals(options.get("doc", "none")))
			defaultFunction("$sysml_doc_label", "$name, $doc", "!return $doc");
	}

	private void writeStyle() {
		out.add("skinparam classAttributeIconSize 0");
		out.add("skinparam class {");
		style("#EAF1FB", "part");
		style("#EAF5EA", "item", "occurrence");
		style("#FFF5D6", "port");
		style("#F2F2F2", "attribute", "enum");
		style("#FBEFE2", "connection", "interface", "flow", "allocation");
		style("#F3EAFB", "action", "calc", "state", "use case", "analysis", "analysis case", "verification",
				"verification case");
		style("#FBEAE9", "constraint", "requirement", "concern");
		out.add("}");
	}

	/** A colour per kind, for the kinds drawn only. */
	private void style(String color, String... kinds) {
		for (String kind : kinds)
			for (Def def : defs)
				if (kind.equals(def.node.getKeyword())) {
					out.add("  BackgroundColor<<" + kind + " def>> " + color);
					break;
				}
	}

	private void writeContents(Pkg pkg, String indent) {
		for (Pkg child : pkg.packages) {
			final SysmlNode n = child.node;
			final String kind = n.isLibrary() ? "library package" : "package";
			out.add(indent + "package \"" + SysmlText.call("$sysml_package_label", n.getName(), kind) + "\" as "
					+ child.alias + " <<" + kind + ">> {");
			writeContents(child, indent + "  ");
			out.add(indent + "}");
		}
		for (Def def : pkg.defs)
			writeDef(def, indent);
	}

	private void writeDef(Def def, String indent) {
		final SysmlNode n = def.node;
		final String kind = n.getKeyword() + " def";
		final List<String> generals = new ArrayList<>();
		for (String general : n.getSpecializes())
			if (drawn(general, n) == null)
				generals.add(SysmlNodes.lastSegment(general));
		final String keyword = isEnum(n) ? "enum" : n.hasModifier("abstract") ? "abstract class" : "class";
		final StringBuilder sb = new StringBuilder(indent);
		sb.append(keyword).append(" \"")
				.append(SysmlText.call("$sysml_def_label", n.getName(), kind, String.join(", ", generals)))
				.append("\" as ").append(def.alias).append(SysmlText.stereotypes(kind));
		if ("tooltip".equals(options.get("doc", "none")) && SysmlText.isEmpty(n.getDoc()) == false)
			sb.append(" [[{").append(tooltip(n.getDoc())).append("}]]");
		final List<String> body = body(n);
		if (body.isEmpty()) {
			out.add(sb.toString());
			return;
		}
		out.add(sb.append(" {").toString());
		for (String line : body)
			out.add(indent + "  " + line);
		out.add(indent + "}");
	}

	private static String tooltip(String doc) {
		return doc.replace('\n', ' ').replace('{', '(').replace('}', ')').replace('[', '(').replace(']', ')')
				.replaceAll("\\s+", " ").trim();
	}

	/** The literals and fields of a definition. */
	private List<String> body(SysmlNode n) {
		final List<String> result = new ArrayList<>();
		for (SysmlNode child : n.getChildren()) {
			if (isEnum(n) && child.isDefinition() == false && child.getName() != null && isLiteral(child)) {
				result.add(FIELD + SysmlText.call("$sysml_literal_label", child.getName()));
				continue;
			}
			if (isShown(child) == false || target(child) != null)
				continue;
			String keyword = isAttribute(child) ? "" : child.getKeyword();
			if (child.isRedefines())
				keyword = (keyword + " :>>").trim();
			final String type = child.getType() == null ? "" : SysmlNodes.lastSegment(child.getType());
			result.add(FIELD + SysmlText.call("$sysml_feature_label", keyword, direction(child), child.getName(), type,
					nonNull(child.getMultiplicity()), child.getValue() == null ? "" : child.getValue().getRaw()));
		}
		return result;
	}

	private static String nonNull(String s) {
		return s == null ? "" : s;
	}

	private void writeLinks(Def def) {
		final SysmlNode n = def.node;
		for (String general : n.getSpecializes()) {
			final Def g = drawn(general, n);
			if (g != null && g != def)
				out.add(g.alias + " <|-- " + def.alias);
		}
		if (n.getParent() != null && drawn.get(n.getParent()) != null)
			out.add(drawn.get(n.getParent()).alias + " +-- " + def.alias);
		// The ends that share a type are one dependency
		final Map<Def, List<String>> ends = new LinkedHashMap<>();
		for (SysmlNode child : n.getChildren()) {
			if (isShown(child) == false)
				continue;
			final Def target = target(child);
			if (target == null)
				continue;
			if (isEnd(child)) {
				if (ends.containsKey(target) == false)
					ends.put(target, new ArrayList<String>());
				ends.get(target).add(child.getName());
				continue;
			}
			final String arrow = isReference(child) ? " --> " : " *-- ";
			final String multiplicity = child.getMultiplicity() == null ? ""
					: "\"" + SysmlText.quote(child.getMultiplicity()) + "\" ";
			final String keyword = child.getKeyword();
			final String stereotype = "part".equals(keyword) || isReference(child) ? ""
					: SysmlText.stereotypes(keyword);
			out.add(def.alias + arrow + multiplicity + target.alias + " : "
					+ SysmlText.call("$sysml_usage_label", keyword, child.getName()) + stereotype);
		}
		for (Map.Entry<Def, List<String>> ent : ends.entrySet())
			out.add(def.alias + " ..> " + ent.getKey().alias + " : "
					+ SysmlText.call("$sysml_usage_label", "end", String.join(", ", ent.getValue())));
	}

}
