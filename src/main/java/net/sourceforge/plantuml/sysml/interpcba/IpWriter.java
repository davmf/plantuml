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
package net.sourceforge.plantuml.sysml.interpcba;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlFeature;
import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlNodes;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlScope;
import net.sourceforge.plantuml.sysml.SysmlText;
import net.sourceforge.plantuml.sysml.SysmlWriter;

/**
 * Writes an InterPCBAProfile System, or one COTSDevice, as component diagram
 * source.
 * <ul>
 * <li>Each SystemNode is a <code>component</code> stereotyped
 * <code>&lt;&lt;SystemPCBA&gt;&gt;</code> or <code>&lt;&lt;COTSDevice&gt;&gt;</code>,
 * with one port per InterBoardPort (<code>portin</code> for an input or a
 * sink, <code>portout</code> for an output or a source). A COTSDevice's ports
 * are grouped by the ExternalConnector that carries them, carry the kind of
 * their Channel as a stereotype, and its InternalBlocks are drawn inside.</li>
 * <li>Each Interconnect is drawn per conductor (one edge per ConductorPath,
 * stereotyped with the conductor kind), as one cable edge between the two
 * nodes (<code>detail=cable</code>), or as a <code>harness</code> bundling
 * its conductors (<code>detail=harness</code>).</li>
 * <li>A node an Interconnect reaches outside the System (the profile's Rule
 * 1) is drawn <code>&lt;&lt;external&gt;&gt;</code>, a port no conductor
 * uses <code>&lt;&lt;unconnected&gt;&gt;</code>.</li>
 * </ul>
 */
final class IpWriter extends SysmlWriter {

	private static final class Node {
		SysmlFeature feature;
		String alias;
		String category;
		boolean external;
		final List<Port> ports = new ArrayList<>();
	}

	private static final class Port {
		SysmlFeature feature;
		String alias;
		String name;
		boolean used;
		String channel;
	}

	private final SysmlModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final boolean deviceOnly;
	private final SysmlScope<Object> scope = new SysmlScope<>();
	private final List<Node> nodes = new ArrayList<>();
	private final Set<String> aliases = new HashSet<>();
	private final List<String> edges = new ArrayList<>();

	IpWriter(SysmlModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined,
			boolean deviceOnly) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
		this.deviceOnly = deviceOnly;
	}

	// ------------------------------------------------------------------
	// Reading
	// ------------------------------------------------------------------

	private static String nonEmpty(String s, String defaultValue) {
		return s == null || s.isEmpty() ? defaultValue : s;
	}

	private boolean isNode(SysmlFeature f) {
		final String c = model.classify(f);
		return IpProfile.SYSTEM_PCBA.equals(c) || IpProfile.COTS_DEVICE.equals(c) || IpProfile.SYSTEM_NODE.equals(c);
	}

	private void readNodes(SysmlFeature owner, List<String> path) {
		for (SysmlFeature f : owner.getFeatures()) {
			if (f.isKeyword("part") == false)
				continue;
			if (isNode(f))
				addNode(f, append(path, f.getName()), false);
			else if (model.classify(f) == null && f.getType() == null)
				readNodes(f, append(path, f.getName()));
		}
	}

	private Node addNode(SysmlFeature f, List<String> path, boolean external) {
		final Node node = new Node();
		node.feature = f;
		node.category = model.classify(f);
		node.external = external;
		node.alias = SysmlText.unique(SysmlText.sanitize(nonEmpty(f.getAttribute("refDesignator"), f.getName())),
				aliases);
		scope.register(path, node);
		final Map<String, String> channels = new HashMap<>();
		for (SysmlFeature c : f.getFeatures())
			if (IpProfile.CHANNEL.equals(model.classify(c)))
				for (String name : portNames(c))
					channels.put(name, nonEmpty(c.getAttribute("kind"), ""));
		for (SysmlFeature p : f.getFeatures()) {
			if (p.isKeyword("port") == false)
				continue;
			final Port port = new Port();
			port.feature = p;
			port.name = nonEmpty(p.getAttribute("name"), p.getName());
			port.alias = SysmlText.unique(node.alias + "_" + SysmlText.sanitize(p.getName()), aliases);
			port.channel = channels.containsKey(p.getName()) ? channels.get(p.getName()) : channels.get(port.name);
			node.ports.add(port);
			scope.register(append(path, p.getName()), port);
		}
		nodes.add(node);
		return node;
	}

	private static List<String> portNames(SysmlFeature f) {
		final String raw = f.getAttribute("portNames");
		return raw == null ? new ArrayList<String>() : SysmlNodes.splitList(raw);
	}

	/** A node or port, looked for in the System, then among the top-level parts (outside it). */
	private Object resolve(String reference, List<String> context, String connection) {
		SysmlScope.Found<Object> found = scope.resolve(reference, context);
		if (found == null) {
			final String first = reference.split("\\.")[0];
			for (SysmlNode usage : model.getTopLevelUsages())
				if (first.equals(usage.getName()) && usage != root) {
					final SysmlFeature f = model.resolve(usage);
					if (isNode(f)) {
						warn(connection + ": " + usage.getName() + " is not part of " + root.getName());
						addNode(f, Arrays.asList(usage.getName()), true);
						found = scope.resolve(reference, context);
					}
				}
		}
		if (found == null) {
			warn(connection + ": cannot resolve '" + reference + "'");
			return null;
		}
		return found.getElement();
	}

	private void readInterconnect(SysmlNode c, List<String> context) {
		final String name = c.getName() == null ? "interconnect" : c.getName();
		final String[] ends = SysmlNodes.ends(c);
		final Object a = ends == null ? null : resolve(ends[0], context, name);
		final Object b = ends == null ? null : resolve(ends[1], context, name);

		// The cable, and the kind and gauge of each of its conductors
		SysmlFeature cable = null;
		final Map<String, SysmlFeature> conductors = new LinkedHashMap<>();
		final List<SysmlNode> paths = new ArrayList<>();
		for (SysmlNode member : SysmlNodes.members(c)) {
			final String category = model.classify(member.getType());
			if (IpProfile.CABLE.equals(category) || "cable".equals(member.getName())) {
				cable = model.resolve(member);
				for (SysmlFeature f : cable.getFeatures())
					if (IpProfile.CONDUCTOR.equals(model.classify(f)))
						conductors.put(nonEmpty(f.getAttribute("label"), f.getName()), f);
			} else if (IpProfile.CONDUCTOR_PATH.equals(category) || "paths".equals(member.getName())) {
				paths.add(member);
			}
		}
		final String cableLabel = SysmlText.call("$sysml_cable_label", name,
				cable == null ? "" : nonEmpty(cable.getAttribute("partNumber"), ""),
				cable == null ? "" : nonEmpty(cable.getAttribute("form"), ""), String.valueOf(conductors.size()));
		final String form = cable == null ? null : cable.getAttribute("form");

		final String detail = options.get("detail", "conductor");
		final List<String> conductorEdges = new ArrayList<>();
		for (SysmlNode path : paths) {
			final String[] pe = SysmlNodes.ends(path);
			if (pe == null) {
				warn(name + ": ConductorPath without source and target");
				continue;
			}
			final Object pa = resolve(pe[0], context, name);
			final Object pb = resolve(pe[1], context, name);
			if (pa instanceof Port)
				((Port) pa).used = true;
			if (pb instanceof Port)
				((Port) pb).used = true;
			if (pa == null || pb == null)
				continue;
			final String label = SysmlNodes.text(model, path, "conductorLabel", "");
			final SysmlFeature conductor = conductors.get(label);
			if (conductor == null && cable != null)
				warn(name + ": no conductor labelled '" + label + "' on the cable");
			final String kind = conductor == null ? "" : nonEmpty(conductor.getAttribute("kind"), "");
			final String gauge = conductor == null ? "" : nonEmpty(conductor.getAttribute("gauge"), "");
			conductorEdges.add(alias(pa) + " -- " + alias(pb) + " <<ConductorPath>>" + SysmlText.stereotypes(kind)
					+ " : " + SysmlText.call("$sysml_conductor_label", label, kind, gauge, name));
		}
		if ("cable".equals(detail) || conductorEdges.isEmpty()) {
			if (a != null && b != null)
				edges.add(nodeAlias(a) + " -- " + nodeAlias(b) + " <<Interconnect>>" + SysmlText.stereotypes(form)
						+ " : " + cableLabel);
		} else if ("harness".equals(detail)) {
			edges.add("harness \"" + cableLabel + "\" {");
			for (String e : conductorEdges)
				edges.add("  " + e);
			edges.add("}");
		} else {
			edges.addAll(conductorEdges);
		}
	}

	private static String alias(Object o) {
		return o instanceof Port ? ((Port) o).alias : ((Node) o).alias;
	}

	private String nodeAlias(Object o) {
		if (o instanceof Node)
			return ((Node) o).alias;
		for (Node n : nodes)
			if (n.ports.contains(o))
				return n.alias;
		return alias(o);
	}

	// ------------------------------------------------------------------
	// Writing
	// ------------------------------------------------------------------

	void run(String source) {
		final SysmlFeature rootFeature = model.resolve(root);
		final List<String> rootPath = Arrays.asList(root.getName());
		if (deviceOnly) {
			final Node device = addNode(rootFeature, rootPath, false);
			if (device.category == null)
				// A catalogue 'part def X :> COTSDevice' has no type of its own
				device.category = model.classify(root.getQualifiedName());
		} else {
			readNodes(rootFeature, rootPath);
			for (SysmlNode c : rootFeature.getConnections())
				if (IpProfile.INTERCONNECT.equals(model.classify(c.getType())) || c.getType() == null)
					readInterconnect(c, rootPath);
		}

		final Map<String, String> attributes = new LinkedHashMap<>();
		for (Map.Entry<String, String> ent : rootFeature.getAttributes().entrySet())
			attributes.put(ent.getKey(), ent.getValue());
		header(source, deviceOnly ? "COTSDeviceProfile" : "InterPCBAProfile", root.getName(), attributes);
		if (deviceOnly == false)
			out.add("!$sysml_system = \"" + SysmlText.quote(nonEmpty(rootFeature.getAttribute("name"), root.getName()))
					+ "\"");
		writeLabelFunctions();
		if (options.isStyle())
			writeStyle();
		String indent = "";
		final boolean frame = options.isFrame() && deviceOnly == false;
		if (frame) {
			out.add("rectangle \"" + SysmlText.call("$sysml_system_label",
					nonEmpty(rootFeature.getAttribute("name"), root.getName()),
					nonEmpty(rootFeature.getAttribute("revision"), "")) + "\" as "
					+ SysmlText.unique(SysmlText.sanitize(root.getName()), aliases) + " <<System>> {");
			indent = "  ";
		}
		for (Node n : nodes)
			if (n.external == false)
				writeNode(n, indent);
		if (frame)
			out.add("}");
		for (Node n : nodes)
			if (n.external)
				writeNode(n, "");
		out.addAll(edges);
		footer(source);
	}

	private void writeLabelFunctions() {
		defaultFunction("$sysml_system_label", "$name, $revision", //
				"!if $revision == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $name + \" rev \" + $revision");
		defaultFunction("$sysml_node_label", "$designator, $type, $category, $details", //
				"!$sysml_l = $designator", //
				"!if $type != \"\" && $type != $category", //
				"!$sysml_l = $sysml_l + \"\\n\" + $type", //
				"!endif", //
				"!if $details != \"\"", //
				"!$sysml_l = $sysml_l + \"\\n<size:10>\" + $details + \"</size>\"", //
				"!endif", //
				"!return $sysml_l");
		defaultFunction("$sysml_port_label", "$name, $direction, $role", "!return $name");
		defaultFunction("$sysml_connector_label", "$label, $kind", "!return $label");
		defaultFunction("$sysml_block_label", "$name, $note", "!return $name");
		defaultFunction("$sysml_cable_label", "$name, $partNumber, $form, $conductors", //
				"!if $partNumber == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $name + \"\\n<size:10>\" + $partNumber + \"</size>\"");
		defaultFunction("$sysml_conductor_label", "$label, $kind, $gauge, $cable", "!return $label");
	}

	private void writeStyle() {
		out.add("skinparam component {");
		out.add("  style rectangle");
		out.add("  portLabels inside");
		out.add("}");
		out.add("<style>");
		out.add("component {");
		out.add("  .SystemPCBA { BackGroundColor #EAF1FB; LineColor #3F6FB2 }");
		out.add("  .COTSDevice { BackGroundColor #F2F2F2; LineColor #666666 }");
		out.add("  .external { LineColor #C00000; LineStyle 6-3 }");
		out.add("}");
		out.add("rectangle {");
		out.add("  .System { BackGroundColor #FFFFFF; LineColor #404040 }");
		out.add("  .InternalBlock { BackGroundColor #FFFFFF; LineColor #9E9E9E; LineStyle 4-3 }");
		out.add("}");
		out.add("port {");
		out.add("  .unconnected { BackGroundColor #FFB3B3; LineColor #C00000 }");
		out.add("  .InterBoardPowerPort { BackGroundColor #F8CBAD }");
		out.add("  .ground { BackGroundColor #7F7F7F }");
		out.add("}");
		out.add("arrow {");
		out.add("  .Interconnect { LineColor #8C6D4B; LineThickness 2; FontColor #8C6D4B }");
		out.add("  .power { LineColor #C0504D }");
		out.add("  .ground { LineColor #404040 }");
		out.add("  .shield { LineColor #2E7D32 }");
		out.add("}");
		out.add("</style>");
	}

	private void writeNode(Node n, String indent) {
		final SysmlFeature f = n.feature;
		final String designator = nonEmpty(f.getAttribute("refDesignator"), f.getName());
		String type = deviceOnly && f.getDeclaration().isDefinition() ? f.getName()
				: SysmlNodes.lastSegment(f.getType());
		if (designator.equals(type))
			type = null;
		final List<String> details = new ArrayList<>();
		if (IpProfile.COTS_DEVICE.equals(n.category)) {
			final String maker = nonEmpty(f.getAttribute("manufacturer"), "");
			final String m = nonEmpty(f.getAttribute("model"), "");
			if ((maker + m).isEmpty() == false)
				details.add((maker + " " + m).trim());
		} else {
			final SysmlFeature board = f.getFeature("board");
			if (board != null && board.getAttribute("partNumber") != null)
				details.add(board.getAttribute("partNumber")
						+ (board.getAttribute("revision") == null ? "" : " rev " + board.getAttribute("revision")));
		}
		final String label = SysmlText.call("$sysml_node_label", designator, type == null ? "" : type,
				n.category == null ? "" : n.category, String.join(", ", details));
		final String stereotypes = SysmlText.stereotypes(n.category, n.external ? "external" : null);

		final List<SysmlFeature> blocks = new ArrayList<>();
		final List<SysmlFeature> connectors = new ArrayList<>();
		for (SysmlFeature c : f.getFeatures()) {
			final String category = model.classify(c);
			if (IpProfile.INTERNAL_BLOCK.equals(category))
				blocks.add(c);
			else if (IpProfile.EXTERNAL_CONNECTOR.equals(category))
				connectors.add(c);
		}
		if (n.ports.isEmpty() && blocks.isEmpty()) {
			out.add(indent + "component \"" + label + "\" as " + n.alias + stereotypes);
			return;
		}
		out.add(indent + "component \"" + label + "\" as " + n.alias + stereotypes + " {");
		final Set<Port> written = new HashSet<>();
		for (SysmlFeature connector : connectors) {
			final List<Port> carried = new ArrayList<>();
			for (String name : portNames(connector))
				for (Port p : n.ports)
					if (written.contains(p) == false && (name.equals(p.name) || name.equals(p.feature.getName())))
						carried.add(p);
			if (carried.isEmpty())
				continue;
			out.add(indent + "  group \"" + SysmlText.call("$sysml_connector_label",
					nonEmpty(connector.getAttribute("label"), connector.getName()),
					nonEmpty(connector.getAttribute("kind"), "")) + "\" {");
			for (Port p : carried) {
				writePort(p, indent + "    ");
				written.add(p);
			}
			out.add(indent + "  }");
		}
		for (Port p : n.ports)
			if (written.contains(p) == false)
				writePort(p, indent + "  ");
		for (SysmlFeature b : blocks)
			out.add(indent + "  rectangle \"" + SysmlText.call("$sysml_block_label",
					nonEmpty(b.getAttribute("name"), b.getName()), nonEmpty(b.getAttribute("note"), "")) + "\" as "
					+ SysmlText.unique(n.alias + "_" + SysmlText.sanitize(b.getName()), aliases)
					+ " <<InternalBlock>>");
		out.add(indent + "}");
	}

	private void writePort(Port p, String indent) {
		final String direction = nonEmpty(p.feature.getAttribute("direction"), "");
		final boolean in = direction.equals("input") || direction.equals("analog_in") || direction.equals("sink");
		final boolean outward = direction.equals("output") || direction.equals("analog_out")
				|| direction.equals("source");
		final String keyword = in ? "portin" : outward ? "portout" : "port";
		final String portType = SysmlNodes.lastSegment(p.feature.getType());
		final boolean channels = options.getBoolean("channels", true);
		out.add(indent + keyword + " \"" + SysmlText.call("$sysml_port_label", p.name, direction,
				nonEmpty(p.feature.getAttribute("role"), "")) + "\" as " + p.alias
				+ SysmlText.stereotypes(portType, p.feature.getAttribute("role"), channels ? p.channel : null,
						p.used || deviceOnly ? null : "unconnected"));
	}

}
