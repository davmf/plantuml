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
package net.sourceforge.plantuml.sysml.statemachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlException;
import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlNodes;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlScope;
import net.sourceforge.plantuml.sysml.SysmlText;
import net.sourceforge.plantuml.sysml.SysmlWriter;

/**
 * Writes one StateMachine as state diagram source.
 * <ul>
 * <li>States, CompositeStates (one <code>--</code>-separated block per
 * Region) and SubmachineStates (collapsed, with their entry and exit points
 * on the boundary) keep the containment of the model.</li>
 * <li>Pseudostates use PlantUML's stereotypes: <code>&lt;&lt;start&gt;&gt;</code>,
 * <code>&lt;&lt;end&gt;&gt;</code>, <code>&lt;&lt;choice&gt;&gt;</code>,
 * <code>&lt;&lt;junction&gt;&gt;</code>, <code>&lt;&lt;fork&gt;&gt;</code>,
 * <code>&lt;&lt;join&gt;&gt;</code>, <code>&lt;&lt;history&gt;&gt;</code>,
 * <code>&lt;&lt;history*&gt;&gt;</code>, <code>&lt;&lt;entryPoint&gt;&gt;</code>,
 * <code>&lt;&lt;exitPoint&gt;&gt;</code>; a terminate pseudostate is a
 * <code>&lt;&lt;terminate&gt;&gt;</code> state drawn as a cross.</li>
 * <li>Transitions carry their classification as a stereotype
 * (<code>&lt;&lt;primary&gt;&gt;</code>, <code>&lt;&lt;secondary&gt;&gt;</code>,
 * <code>&lt;&lt;exception&gt;&gt;</code>), styled as in the charge-post
 * diagrams; internal transitions are listed inside their state.</li>
 * </ul>
 */
final class SmWriter extends SysmlWriter {

	private static final class Vertex {
		final SysmlNode node;
		final String category;
		final List<String> path;
		String name;
		String kind;
		String alias;
		String submachine;
		final List<String> lines = new ArrayList<>();
		final List<Region> regions = new ArrayList<>();
		final List<Vertex> pins = new ArrayList<>();

		Vertex(SysmlNode node, String category, List<String> path) {
			this.node = node;
			this.category = category;
			this.path = path;
		}

		boolean is(String c) {
			return c.equals(category);
		}

		boolean isPseudo(String k) {
			return is(SmProfile.PSEUDOSTATE) && k.equals(kind);
		}
	}

	private static final class Region {
		final String name;
		final List<Vertex> vertices = new ArrayList<>();
		/** The transitions written in the region's body, drawn inside it */
		final List<Transition> transitions = new ArrayList<>();

		Region(String name) {
			this.name = name;
		}
	}

	private static final class Transition {
		final SysmlNode node;
		final List<String> context;
		Vertex source;
		Vertex target;

		Transition(SysmlNode node, List<String> context) {
			this.node = node;
			this.context = context;
		}
	}

	private final SysmlModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final SysmlScope<Vertex> vertices = new SysmlScope<>();
	private final SysmlScope<SysmlNode> elements = new SysmlScope<>();
	private final List<Region> top = new ArrayList<>();
	private final List<Transition> transitions = new ArrayList<>();
	private final Set<String> aliases = new HashSet<>();
	/** Every vertex by its path inside the machine, <code>top::running</code>. */
	private final Map<String, Vertex> byPath = new LinkedHashMap<>();

	SmWriter(SysmlModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
	}

	// ------------------------------------------------------------------
	// Reading
	// ------------------------------------------------------------------

	private String category(SysmlNode node) {
		return model.classify(node.getType());
	}

	private void registerElements(SysmlNode node, List<String> path) {
		for (SysmlNode child : SysmlNodes.members(node)) {
			final List<String> p = append(path, child.getName());
			elements.register(p, child);
			registerElements(child, p);
		}
	}

	private void read() {
		final List<String> rootPath = Arrays.asList(root.getName());
		registerElements(root, rootPath);
		collectRegions(root, rootPath, top);
		if (top.isEmpty())
			warn(root.getName() + ": no Region found (expected 'part :>> top : Region { ... }')");
		for (Transition t : transitions)
			resolve(t);
	}

	/** Regions of a StateMachine or CompositeState, through grouping parts such as <code>:>> regions { }</code>. */
	private void collectRegions(SysmlNode owner, List<String> path, List<Region> result) {
		for (SysmlNode member : SysmlNodes.members(owner)) {
			final List<String> p = append(path, member.getName());
			final String c = category(member);
			if (SmProfile.REGION.equals(c)) {
				final Region region = new Region(SysmlNodes.text(model, member, "name", member.getName()));
				collectVertices(member, p, region);
				result.add(region);
			} else if (c == null && member.getType() == null && "regions".equals(member.getName())) {
				final int before = result.size();
				collectRegions(member, p, result);
				if (result.size() == before) {
					// ':>> regions { part a : State; ... }': the group is the region
					final Region region = new Region(null);
					collectVertices(member, p, region);
					if (region.vertices.isEmpty() == false)
						result.add(region);
				}
			}
		}
	}

	private void collectVertices(SysmlNode body, List<String> path, Region region) {
		for (SysmlNode child : body.getChildren()) {
			if (child.isDefinition())
				continue;
			if (child.getConnectEnds().isEmpty() == false || child.isKeyword("connection")) {
				final String c = category(child);
				if (c == null || SmProfile.TRANSITION.equals(c)) {
					final Transition t = new Transition(child, path);
					transitions.add(t);
					region.transitions.add(t);
				}
				continue;
			}
			if (child.getName() == null || child.getKeyword() == null || child.isKeyword("attribute"))
				continue;
			final List<String> p = append(path, child.getName());
			final String c = category(child);
			if (isVertex(c))
				region.vertices.add(readVertex(child, c, p));
			else if (c == null && child.getType() == null)
				// ':>> vertices { ... }', ':>> transitions { ... }'
				collectVertices(child, p, region);
		}
	}

	private static boolean isVertex(String c) {
		return SmProfile.STATE.equals(c) || SmProfile.PSEUDOSTATE.equals(c) || SmProfile.COMPOSITE_STATE.equals(c)
				|| SmProfile.SUBMACHINE_STATE.equals(c);
	}

	private Vertex readVertex(SysmlNode node, String c, List<String> path) {
		final Vertex v = new Vertex(node, c, path);
		v.name = SysmlNodes.text(model, node, "name", node.getName());
		v.alias = SysmlText.unique(SysmlText.sanitize(node.getName()), aliases);
		vertices.register(path, v);
		byPath.put(String.join("::", path.subList(1, path.size())), v);
		if (v.is(SmProfile.PSEUDOSTATE)) {
			v.kind = SysmlNodes.text(model, node, "kind");
			if (v.kind == null) {
				warn(node.getName() + ": Pseudostate without kind, drawn as a choice");
				v.kind = "choice";
			}
		}
		if (options.getBoolean("actions", true)) {
			addActions(v, "entry", "entryActions");
			addActions(v, "do", "doActions");
			addActions(v, "exit", "exitActions");
		}
		if (v.is(SmProfile.COMPOSITE_STATE)) {
			collectRegions(node, path, v.regions);
			if (v.regions.isEmpty()) {
				final Region implicit = new Region(null);
				collectVertices(node, path, implicit);
				v.regions.add(implicit);
			}
		} else if (v.is(SmProfile.SUBMACHINE_STATE)) {
			final SysmlNode ref = submachine(node);
			if (ref != null)
				v.submachine = SysmlNodes.text(model, ref, "name", ref.getName());
			else if (SysmlNodes.value(model, node, "submachine") != null)
				v.submachine = SysmlNodes.lastSegment(SysmlNodes.value(model, node, "submachine").getRaw());
			collectPins(node, path, v);
		}
		return v;
	}

	private SysmlNode submachine(SysmlNode node) {
		final String ref = SysmlNodes.value(model, node, "submachine") == null ? null
				: SysmlNodes.lastSegment(SysmlNodes.value(model, node, "submachine").getRaw());
		if (ref == null)
			return null;
		for (SysmlNode usage : model.getTopLevelUsages())
			if (ref.equals(usage.getName()))
				return usage;
		return null;
	}

	private void collectPins(SysmlNode node, List<String> path, Vertex owner) {
		for (SysmlNode member : SysmlNodes.members(node)) {
			final List<String> p = append(path, member.getName());
			final String c = category(member);
			if (SmProfile.PSEUDOSTATE.equals(c))
				owner.pins.add(readVertex(member, c, p));
			else if (c == null && member.getType() == null)
				collectPins(member, p, owner);
		}
	}

	private void addActions(Vertex v, String label, String feature) {
		final String names = names(SysmlNodes.list(model, v.node, feature), v.path, "; ");
		if (names.isEmpty() == false)
			v.lines.add(label + " / " + names);
	}

	/** The display names of referenced Events, Actions or Guards. */
	private String names(List<String> references, List<String> context, String separator) {
		final List<String> result = new ArrayList<>();
		for (String ref : references)
			result.add(displayName(ref, context, "name"));
		return String.join(separator, result);
	}

	private String displayName(String reference, List<String> context, String attribute) {
		final SysmlScope.Found<SysmlNode> found = elements.resolve(reference, context);
		if (found == null || found.getPort() != null)
			return SysmlNodes.lastSegment(reference);
		return SysmlNodes.text(model, found.getElement(), attribute, found.getElement().getName());
	}

	private void resolve(Transition t) {
		final String[] ends = SysmlNodes.ends(t.node);
		if (ends == null) {
			warn(name(t) + ": transition without two ends");
			return;
		}
		t.source = vertex(ends[0], t);
		t.target = vertex(ends[1], t);
	}

	private Vertex vertex(String reference, Transition t) {
		final SysmlScope.Found<Vertex> found = vertices.resolve(reference, t.context);
		if (found == null) {
			warn(name(t) + ": cannot resolve '" + reference + "'");
			return null;
		}
		return found.getElement();
	}

	private static String name(Transition t) {
		return t.node.getName() == null ? "transition at " + t.node.getLocation() : t.node.getName();
	}

	// ------------------------------------------------------------------
	// Writing
	// ------------------------------------------------------------------

	/**
	 * The state of <code>within=</code>, or <code>null</code> for the whole
	 * machine.
	 */
	private Vertex within() throws SysmlException {
		final String written = options.get("within", null);
		if (written == null)
			return null;
		final String path = written.replace("'", "").replaceAll("\\s*(::|\\.)\\s*", "::").trim();
		final Vertex v = byPath.get(path);
		if (v == null)
			throw new SysmlException("within=" + written + ": no state at this path in " + root.getName()
					+ " (expected one of " + byPath.keySet() + ")");
		return v;
	}

	void run(String source) throws SysmlException {
		read();
		final Vertex within = within();
		final Map<String, String> attributes = new LinkedHashMap<>();
		attributes.put("name", SysmlNodes.text(model, root, "name", root.getName()));
		header(source, "StateMachineProfile", root.getName(), attributes);
		writeLabelFunctions();
		out.add("hide empty description");
		if (options.isStyle())
			writeStyle();

		boolean frame = options.isFrame();
		for (Region r : top)
			for (Vertex v : r.vertices)
				if (v.isPseudo("entryPoint") || v.isPseudo("exitPoint"))
					frame = true;
		String indent = "";
		if (within != null) {
			writeVertex(within, indent);
			footer(source);
			return;
		}
		if (frame) {
			out.add("state \"" + SysmlText.call("$sysml_state_label", attributes.get("name"), SmProfile.STATE_MACHINE,
					"") + "\" as " + SysmlText.unique(SysmlText.sanitize(root.getName()), aliases) + " <<"
					+ SmProfile.STATE_MACHINE + ">> {");
			indent = "  ";
		}
		writeRegions(top, indent);
		if (frame)
			out.add("}");
		footer(source);
	}

	private void writeLabelFunctions() {
		defaultFunction("$sysml_state_label", "$name, $category, $submachine", //
				"!if $submachine != \"\"", //
				"!return $name + \" : \" + $submachine", //
				"!endif", //
				"!return $name");
		defaultFunction("$sysml_transition_label", "$trigger, $guard, $actions, $name", //
				"!$sysml_l = $trigger", //
				"!if $guard != \"\"", //
				"!if $sysml_l != \"\"", //
				"!$sysml_l = $sysml_l + \" \"", //
				"!endif", //
				"!$sysml_l = $sysml_l + \"[\" + $guard + \"]\"", //
				"!endif", //
				"!if $actions != \"\"", //
				"!if $sysml_l != \"\"", //
				"!$sysml_l = $sysml_l + \" \"", //
				"!endif", //
				"!$sysml_l = $sysml_l + \"/ \" + $actions", //
				"!endif", //
				"!return $sysml_l");
	}

	private void writeStyle() {
		// Transition labels as nodes of their own (placed by the layout, not
		// squeezed beside the arrow), read when each transition is parsed:
		// before the regions
		out.add("skinparam stateDiagramEdgeLabelStyle node");
		out.add("!pragma edgeCornerRadius 10");
		out.add("<style>");
		out.add("state {");
		out.add("  .terminate { LineColor transparent; BackgroundColor transparent; FontSize 34; FontColor #B00020; shadowing 0 }");
		out.add("  .submachine { BackgroundColor #EEF3FF }");
		out.add("  .StateMachine { BackgroundColor transparent }");
		out.add("}");
		out.add("arrow {");
		out.add("  .primary { LineColor #2E7D32; LineThickness 4 }");
		out.add("  .secondary { LineColor #1565C0; LineThickness 2 }");
		out.add("  .exception { LineColor #C62828; LineThickness 1 }");
		out.add("}");
		out.add("</style>");
	}

	private void writeRegions(List<Region> regions, String indent) {
		final String separator = "vertical".equals(options.get("regions", "horizontal")) ? "||" : "--";
		for (int i = 0; i < regions.size(); i++) {
			if (i > 0)
				out.add(indent + separator);
			for (Vertex v : regions.get(i).vertices)
				writeVertex(v, indent);
			// Links into a concurrent region only work from inside it
			for (Transition t : regions.get(i).transitions)
				writeTransition(t, indent);
		}
	}

	private void writeVertex(Vertex v, String indent) {
		if (v.is(SmProfile.PSEUDOSTATE)) {
			writePseudostate(v, indent);
			return;
		}
		final String stereotype = v.is(SmProfile.SUBMACHINE_STATE) ? " <<submachine>>" : "";
		final String declaration = indent + "state \"" + SysmlText.call("$sysml_state_label", v.name, v.category,
				v.submachine == null ? "" : v.submachine) + "\" as " + v.alias + stereotype;
		if (v.regions.isEmpty() == false || v.pins.isEmpty() == false) {
			out.add(declaration + " {");
			for (Vertex pin : v.pins)
				writePseudostate(pin, indent + "  ");
			writeRegions(v.regions, indent + "  ");
			out.add(indent + "}");
		} else {
			out.add(declaration);
		}
		writeLines(v, indent);
	}

	private void writeLines(Vertex v, String indent) {
		for (String line : v.lines)
			out.add(indent + v.alias + " : " + line);
	}

	private void writePseudostate(Vertex v, String indent) {
		final String stereotype = pseudostateStereotype(v.kind);
		if ("terminate".equals(stereotype)) {
			out.add(indent + "state \"✕\" as " + v.alias + " <<terminate>>");
			return;
		}
		if (stereotype == null) {
			warn(v.node.getName() + ": unknown Pseudostate kind '" + v.kind + "', drawn as a choice");
			out.add(indent + "state " + v.alias + " <<choice>>");
			return;
		}
		if (v.isPseudo("entryPoint") || v.isPseudo("exitPoint"))
			out.add(indent + "state \"" + SysmlText.quote(v.name) + "\" as " + v.alias + " <<" + stereotype + ">>");
		else
			out.add(indent + "state " + v.alias + " <<" + stereotype + ">>");
	}

	private static String pseudostateStereotype(String kind) {
		switch (kind) {
		case "initial":
			return "start";
		case "final":
			return "end";
		case "shallowHistory":
			return "history";
		case "deepHistory":
			return "history*";
		case "entryPoint":
			return "entryPoint";
		case "exitPoint":
			return "exitPoint";
		case "joinNode":
		case "join":
			return "join";
		case "forkNode":
		case "fork":
			return "fork";
		case "choice":
			return "choice";
		case "junction":
			return "junction";
		case "terminateNode":
		case "terminate":
			return "terminate";
		default:
			return null;
		}
	}

	private void writeTransition(Transition t, String indent) {
		if (t.source == null || t.target == null)
			return;
		final SysmlNode n = t.node;
		final List<String> context = t.context;
		String trigger = "";
		if (SysmlNodes.value(model, n, "trigger") != null) {
			final String ref = SysmlNodes.value(model, n, "trigger").getRaw();
			trigger = displayName(ref, context, "name");
			final SysmlScope.Found<SysmlNode> event = elements.resolve(ref, context);
			if (event != null && event.getPort() == null) {
				final String payload = SysmlNodes.text(model, event.getElement(), "payload");
				if (SysmlText.isEmpty(payload) == false)
					trigger = trigger + "(" + payload + ")";
			}
		}
		final List<String> guards = new ArrayList<>();
		for (String ref : SysmlNodes.list(model, n, "guards")) {
			final SysmlScope.Found<SysmlNode> guard = elements.resolve(ref, context);
			if (guard == null || guard.getPort() != null)
				guards.add(SysmlNodes.lastSegment(ref));
			else
				guards.add(SysmlNodes.text(model, guard.getElement(), "expression",
						SysmlNodes.text(model, guard.getElement(), "name", guard.getElement().getName())));
		}
		final String guard = String.join(" && ", guards);
		final String actions = names(SysmlNodes.list(model, n, "actions"), context, "; ");
		final String name = SysmlNodes.text(model, n, "name", n.getName() == null ? "" : n.getName());
		final String kind = SysmlNodes.text(model, n, "kind", "external");
		final String classification = SysmlNodes.text(model, n, "classification", "unclassified");
		final String shown = options.get("transitions", "all");
		if (("primary".equals(shown) && "primary".equals(classification) == false)
				|| ("noexceptions".equals(shown) && "exception".equals(classification)))
			return;

		final boolean hasLabel = trigger.isEmpty() == false || guard.isEmpty() == false
				|| actions.isEmpty() == false || isUserDefined("$sysml_transition_label");
		final String label = SysmlText.call("$sysml_transition_label", trigger, guard, actions, name);
		if ("internal".equals(kind) && t.source == t.target) {
			if (hasLabel)
				out.add(indent + t.source.alias + " : " + label);
			return;
		}
		out.add(indent + t.source.alias + " --> " + t.target.alias + SysmlText.stereotypes(classification)
				+ (hasLabel ? " : " + label : ""));
	}

}
