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
package net.sourceforge.plantuml.sysml.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlNodes;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlScope;
import net.sourceforge.plantuml.sysml.SysmlText;
import net.sourceforge.plantuml.sysml.SysmlWriter;

/**
 * Writes one Activity as a component-style diagram.
 * <p>
 * The profile joins nodes by arbitrary ActivityEdges and has no initial,
 * decision or merge nodes, which PlantUML's activity syntax (structured
 * control flow) cannot express; a graph of rounded boxes can. So:
 * <ul>
 * <li>an Action is a <code>component</code> <code>&lt;&lt;Action&gt;&gt;</code>
 * (drawn rounded), with its Pins as <code>portin</code>/<code>portout</code>;</li>
 * <li>an ObjectNode is a <code>rectangle</code> <code>&lt;&lt;ObjectNode&gt;&gt;</code>;</li>
 * <li>Partitions, ExpansionRegions and InterruptibleRegions are frames
 * around the nodes they reference; an ExpansionRegion's collections are
 * ports on its boundary;</li>
 * <li>edges carry their kind: <code>&lt;&lt;controlFlow&gt;&gt;</code>,
 * <code>&lt;&lt;objectFlow&gt;&gt;</code> (dashed),
 * <code>&lt;&lt;interruptFlow&gt;&gt;</code> (red, with a lightning sign).</li>
 * </ul>
 */
final class ActWriter extends SysmlWriter {

	private static final class Element {
		SysmlNode node;
		String category;
		String alias;
		String name;
		List<String> path;
		/** Pins of an Action; boundary collections of an ExpansionRegion */
		final List<Element> ports = new ArrayList<>();
		/** Per-iteration ObjectNodes of an ExpansionRegion */
		final List<Element> inner = new ArrayList<>();
		final List<Element> partitions = new ArrayList<>();
		Element container;
		boolean input;

		boolean is(String c) {
			return c.equals(category);
		}

		boolean isRegion() {
			return is(ActProfile.EXPANSION_REGION) || is(ActProfile.INTERRUPTIBLE_REGION);
		}
	}

	private final SysmlModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final SysmlScope<Element> scope = new SysmlScope<>();
	private final Map<SysmlNode, Element> byNode = new HashMap<>();
	private final List<Element> nodes = new ArrayList<>();
	private final List<Element> regions = new ArrayList<>();
	private final List<Element> topPartitions = new ArrayList<>();
	private final List<Object[]> edges = new ArrayList<>();
	private final Set<String> aliases = new HashSet<>();

	ActWriter(SysmlModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
	}

	// ------------------------------------------------------------------
	// Reading
	// ------------------------------------------------------------------

	private Element element(SysmlNode node, String category, List<String> path) {
		final Element e = new Element();
		e.node = node;
		e.category = category;
		e.path = path;
		e.name = SysmlNodes.text(model, node, "name", node.getName());
		e.alias = SysmlText.unique(SysmlText.sanitize(node.getName()), aliases);
		scope.register(path, e);
		byNode.put(node, e);
		return e;
	}

	private void read() {
		readBody(root, Arrays.asList(root.getName()), null);
		// Membership is by reference: settle it once every node is known
		for (Element p : allPartitions())
			for (String feature : new String[] { "actionContents", "objectContents" })
				for (Element e : references(p, feature))
					if (e.container == null || e.container.is(ActProfile.PARTITION) == false)
						e.container = p;
		for (Element r : regions) {
			final String feature = r.is(ActProfile.EXPANSION_REGION) ? "contents" : "actionContents";
			final List<Element> contents = new ArrayList<>(references(r, feature));
			contents.addAll(references(r, "objectContents"));
			Element partition = null;
			boolean mixed = false;
			for (Element e : contents) {
				if (e.container != null && e.container.isRegion()) {
					warn(e.node.getName() + ": in two regions, drawn in " + e.container.name);
					continue;
				}
				final Element p = e.container;
				if (partition == null)
					partition = p;
				else if (p != partition)
					mixed = true;
				e.container = r;
			}
			if (mixed)
				warn(r.name + ": contents in several partitions, region drawn outside them");
			else if (r.container == null)
				r.container = partition;
		}
	}

	private List<Element> allPartitions() {
		final List<Element> result = new ArrayList<>();
		addPartitions(topPartitions, result);
		return result;
	}

	private static void addPartitions(List<Element> partitions, List<Element> result) {
		for (Element p : partitions) {
			result.add(p);
			addPartitions(p.partitions, result);
		}
	}

	private List<Element> references(Element owner, String feature) {
		final List<Element> result = new ArrayList<>();
		for (String ref : SysmlNodes.list(model, owner.node, feature)) {
			final SysmlScope.Found<Element> found = scope.resolve(ref, owner.path);
			if (found == null || found.getPort() != null)
				warn(owner.node.getName() + "." + feature + ": cannot resolve '" + ref + "'");
			else
				result.add(found.getElement());
		}
		return result;
	}

	private void readBody(SysmlNode body, List<String> path, final Element partition) {
		walk(model, body, path, new Visitor() {
			public boolean visit(SysmlNode node, List<String> p, String category) {
				if (node.isKeyword("connection") || node.getConnectEnds().isEmpty() == false) {
					if (category == null || ActProfile.EDGE.equals(category))
						edges.add(new Object[] { node, p });
					return false;
				}
				if (ActProfile.ACTION.equals(category) || ActProfile.OBJECT_NODE.equals(category)) {
					final Element e = element(node, category, p);
					e.container = partition;
					nodes.add(e);
					if (e.is(ActProfile.ACTION))
						readPins(e);
					return false;
				}
				if (ActProfile.PARTITION.equals(category)) {
					final Element e = element(node, category, p);
					e.name = SysmlNodes.text(model, node, "name", node.getName());
					if (partition == null)
						topPartitions.add(e);
					else
						partition.partitions.add(e);
					readBody(node, p, e);
					return false;
				}
				if (ActProfile.EXPANSION_REGION.equals(category) || ActProfile.INTERRUPTIBLE_REGION.equals(category)) {
					final Element e = element(node, category, p);
					e.container = partition;
					regions.add(e);
					readRegionNodes(e);
					return false;
				}
				return category == null;
			}
		});
	}

	private void readPins(final Element action) {
		walk(model, action.node, action.path, new Visitor() {
			public boolean visit(SysmlNode node, List<String> p, String category) {
				if (ActProfile.PIN.equals(category)) {
					final Element pin = element(node, category, p);
					final String direction = SysmlNodes.text(model, node, "direction", "input");
					pin.input = "output".equals(direction) == false;
					pin.alias = SysmlText.unique(action.alias + "_" + SysmlText.sanitize(node.getName()), aliases);
					action.ports.add(pin);
				}
				return false;
			}
		});
	}

	private void readRegionNodes(final Element region) {
		walk(model, region.node, region.path, new Visitor() {
			public boolean visit(SysmlNode node, List<String> p, String category) {
				if (ActProfile.OBJECT_NODE.equals(category) == false)
					return category == null;
				final Element e = element(node, category, p);
				final String group = p.size() >= 2 ? p.get(p.size() - 2) : "";
				if ("inputCollections".equals(group) || "outputCollections".equals(group)) {
					e.input = "inputCollections".equals(group);
					e.alias = SysmlText.unique(region.alias + "_" + SysmlText.sanitize(node.getName()), aliases);
					region.ports.add(e);
				} else {
					region.inner.add(e);
				}
				return false;
			}
		});
	}

	// ------------------------------------------------------------------
	// Writing
	// ------------------------------------------------------------------

	void run(String source) {
		read();
		final Map<String, String> attributes = new LinkedHashMap<>();
		attributes.put("name", SysmlNodes.text(model, root, "name", root.getName()));
		header(source, "ActivityProfile", root.getName(), attributes);
		writeLabelFunctions();
		if ("left".equals(options.get("direction", "top")))
			out.add("left to right direction");
		if (options.isStyle())
			writeStyle();

		String indent = "";
		if (options.isFrame()) {
			out.add("rectangle \"" + SysmlText.call("$sysml_activity_label", attributes.get("name")) + "\" as "
					+ SysmlText.unique(SysmlText.sanitize(root.getName()), aliases) + " <<Activity>> {");
			indent = "  ";
		}
		for (Element p : topPartitions)
			writePartition(p, indent);
		writeContents(null, indent);
		if (options.isFrame())
			out.add("}");
		for (Object[] edge : edges)
			writeEdge((SysmlNode) edge[0], (List<String>) edge[1]);
		footer(source);
	}

	private void writeLabelFunctions() {
		defaultFunction("$sysml_activity_label", "$name", "!return $name");
		defaultFunction("$sysml_action_label", "$name", "!return $name");
		defaultFunction("$sysml_pin_label", "$name, $direction", "!return $name");
		defaultFunction("$sysml_object_label", "$name, $typeName, $ordering, $upper", //
				"!if $typeName == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $name + \" : \" + $typeName");
		defaultFunction("$sysml_partition_label", "$name, $represents", "!return $name");
		defaultFunction("$sysml_region_label", "$name, $category, $mode", //
				"!if $mode == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $name + \"\\n<size:10>\" + $mode + \"</size>\"");
		defaultFunction("$sysml_edge_label", "$kind, $guard, $weight, $name", //
				"!$sysml_l = \"\"", //
				"!if $kind == \"interruptFlow\"", //
				"!$sysml_l = \"\u21af\"", //
				"!endif", //
				"!if $guard != \"\"", //
				"!$sysml_l = $sysml_l + \"[\" + $guard + \"]\"", //
				"!endif", //
				"!if $weight != \"\"", //
				"!$sysml_l = $sysml_l + \"{weight=\" + $weight + \"}\"", //
				"!endif", //
				"!return $sysml_l");
	}

	private void writeStyle() {
		out.add("skinparam component {");
		out.add("  style rectangle");
		out.add("}");
		out.add("hide stereotype");
		out.add("<style>");
		out.add("component { .Action { RoundCorner 20; BackGroundColor #FFFFFF; LineColor #404040 } }");
		out.add("rectangle {");
		out.add("  .ObjectNode { BackGroundColor #F7F7F7; LineColor #404040 }");
		out.add("  .Partition { BackGroundColor transparent; LineColor #7F7F7F; FontStyle bold }");
		out.add("  .ExpansionRegion { BackGroundColor transparent; LineColor #404040; LineStyle 6-3; RoundCorner 20 }");
		out.add("  .InterruptibleRegion { BackGroundColor transparent; LineColor #C62828; LineStyle 6-3; RoundCorner 20 }");
		out.add("  .Activity { BackGroundColor transparent; RoundCorner 30 }");
		out.add("}");
		out.add("arrow {");
		out.add("  .objectFlow { LineStyle 6-3 }");
		out.add("  .interruptFlow { LineColor #C62828; FontColor #C62828 }");
		out.add("}");
		out.add("</style>");
	}

	private void writePartition(Element p, String indent) {
		out.add(indent + "rectangle \"" + SysmlText.call("$sysml_partition_label", p.name,
				SysmlNodes.text(model, p.node, "represents", "")) + "\" as " + p.alias + " <<Partition>> {");
		for (Element sub : p.partitions)
			writePartition(sub, indent + "  ");
		writeContents(p, indent + "  ");
		out.add(indent + "}");
	}

	/** The nodes and regions whose container is the given one. */
	private void writeContents(Element container, String indent) {
		for (Element r : regions)
			if (r.container == container)
				writeRegion(r, indent);
		for (Element e : nodes)
			if (e.container == container)
				writeNode(e, indent);
	}

	private void writeRegion(Element r, String indent) {
		final String mode = r.is(ActProfile.EXPANSION_REGION) ? SysmlNodes.text(model, r.node, "mode", "") : "";
		out.add(indent + "rectangle \"" + SysmlText.call("$sysml_region_label", r.name, r.category, mode) + "\" as "
				+ r.alias + " <<" + r.category + ">> {");
		for (Element port : r.ports)
			out.add(indent + "  " + (port.input ? "portin" : "portout") + " \"" + objectLabel(port) + "\" as "
					+ port.alias);
		for (Element e : r.inner)
			writeNode(e, indent + "  ");
		writeContents(r, indent + "  ");
		out.add(indent + "}");
	}

	private void writeNode(Element e, String indent) {
		if (e.is(ActProfile.OBJECT_NODE)) {
			out.add(indent + "rectangle \"" + objectLabel(e) + "\" as " + e.alias + " <<ObjectNode>>");
			return;
		}
		final String declaration = indent + "component \"" + SysmlText.call("$sysml_action_label", e.name) + "\" as "
				+ e.alias + " <<Action>>";
		if (e.ports.isEmpty()) {
			out.add(declaration);
			return;
		}
		out.add(declaration + " {");
		for (Element pin : e.ports)
			out.add(indent + "  " + (pin.input ? "portin" : "portout") + " \""
					+ SysmlText.call("$sysml_pin_label", pin.name, SysmlNodes.text(model, pin.node, "direction", ""))
					+ "\" as " + pin.alias);
		out.add(indent + "}");
	}

	private String objectLabel(Element e) {
		return SysmlText.call("$sysml_object_label", e.name, SysmlNodes.text(model, e.node, "typeName", ""),
				SysmlNodes.text(model, e.node, "ordering", ""), SysmlNodes.text(model, e.node, "upper", ""));
	}

	private void writeEdge(SysmlNode edge, List<String> path) {
		final String[] ends = SysmlNodes.ends(edge);
		if (ends == null) {
			warn(edge.getName() + ": edge without source and target");
			return;
		}
		final String from = anchor(ends[0], path, true, edge);
		final String to = anchor(ends[1], path, false, edge);
		if (from == null || to == null)
			return;
		final String kind = SysmlNodes.text(model, edge, "kind", "controlFlow");
		final String guard = SysmlNodes.text(model, edge, "guard", "");
		final String weight = SysmlNodes.text(model, edge, "weight", "");
		final boolean hasLabel = guard.isEmpty() == false || weight.isEmpty() == false
				|| "interruptFlow".equals(kind) || isUserDefined("$sysml_edge_label");
		out.add(from + " --> " + to + SysmlText.stereotypes(kind) + (hasLabel ? " : " + SysmlText.call(
				"$sysml_edge_label", kind, guard, weight, SysmlNodes.text(model, edge, "name", "")) : ""));
	}

	/** The alias an edge end attaches to: a pin, a collection port, or the node itself. */
	private String anchor(String reference, List<String> path, boolean source, SysmlNode edge) {
		final SysmlScope.Found<Element> found = scope.resolve(reference, path);
		if (found == null) {
			warn(edge.getName() + ": cannot resolve '" + reference + "'");
			return null;
		}
		final Element e = found.getElement();
		if (e.is(ActProfile.EXPANSION_REGION))
			for (Element port : e.ports)
				if (port.input != source)
					return port.alias;
		return e.alias;
	}

}
