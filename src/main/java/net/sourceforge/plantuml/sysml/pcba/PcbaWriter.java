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
package net.sourceforge.plantuml.sysml.pcba;

import java.util.ArrayList;
import java.util.Arrays;
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
 * Writes one PCBA as component diagram source.
 * <ul>
 * <li>Each component is a <code>component</code> stereotyped with its
 * category (<code>&lt;&lt;IC&gt;&gt;</code>, <code>&lt;&lt;Connector&gt;&gt;</code>...),
 * with one port per SinglePinPort or PowerPort: <code>portin</code> for an
 * input or a sink, <code>portout</code> for an output or a source;</li>
 * <li>a Subcircuit is a <code>rectangle</code> around its children;</li>
 * <li>each binary connection is an edge stereotyped with its type
 * (<code>&lt;&lt;Net&gt;&gt;</code>, <code>&lt;&lt;PowerRail&gt;&gt;</code>...)
 * and labelled with its name; a NoConnect marks its pin
 * <code>&lt;&lt;nc&gt;&gt;</code> instead, and a pin with no connection at
 * all is <code>&lt;&lt;unconnected&gt;&gt;</code> (the profile's Rule 1).</li>
 * </ul>
 */
final class PcbaWriter extends SysmlWriter {

	private static final class Part {
		SysmlFeature feature;
		String alias;
		String category;
		final List<Pin> pins = new ArrayList<>();
		final List<Part> children = new ArrayList<>();
	}

	private static final class Pin {
		SysmlFeature feature;
		String alias;
		Part owner;
		boolean connected;
		boolean noConnect;
		boolean in;
		boolean out;
	}

	private final SysmlModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final SysmlScope<Object> scope = new SysmlScope<>();
	private final List<Part> parts = new ArrayList<>();
	private final List<Object[]> connections = new ArrayList<>();
	private final Set<String> aliases = new HashSet<>();

	PcbaWriter(SysmlModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
	}

	private void readParts(SysmlFeature owner, List<String> path, List<Part> result) {
		for (SysmlNode c : owner.getConnections())
			connections.add(new Object[] { c, path });
		for (SysmlFeature f : owner.getFeatures()) {
			if (f.isKeyword("part") == false)
				continue;
			final List<String> p = append(path, f.getName());
			final String category = model.classify(f);
			if (PcbaProfile.COMPONENTS.contains(category)) {
				final Part part = new Part();
				part.feature = f;
				part.category = category;
				part.alias = SysmlText.unique(SysmlText.sanitize(nonEmpty(f.getAttribute("designator"), f.getName())),
						aliases);
				scope.register(p, part);
				readPins(part, p);
				if (PcbaProfile.SUBCIRCUIT.equals(category))
					readParts(f, p, part.children);
				result.add(part);
			} else if (category == null && f.getType() == null) {
				// ':>> components { ... }' and the like
				readParts(f, p, result);
			}
		}
	}

	private void readPins(Part part, List<String> path) {
		for (SysmlFeature f : part.feature.getFeatures()) {
			if (f.isKeyword("port") == false)
				continue;
			final Pin pin = new Pin();
			pin.feature = f;
			pin.owner = part;
			pin.alias = SysmlText.unique(part.alias + "_" + SysmlText.sanitize(f.getName()), aliases);
			final String direction = nonEmpty(f.getAttribute("direction"), "");
			pin.in = direction.equals("input") || direction.equals("analog_in") || direction.equals("sink");
			pin.out = direction.equals("output") || direction.equals("analog_out") || direction.equals("source");
			part.pins.add(pin);
			scope.register(append(path, f.getName()), pin);
		}
	}

	private static String nonEmpty(String s, String defaultValue) {
		return s == null || s.isEmpty() ? defaultValue : s;
	}

	private Object end(String reference, List<String> context, SysmlNode connection) {
		final SysmlScope.Found<Object> found = scope.resolve(reference, context);
		if (found == null) {
			warn(connection.getName() + ": cannot resolve '" + reference + "'");
			return null;
		}
		if (found.getPort() != null)
			warn(connection.getName() + ": '" + reference + "' is not a declared port, drawn on its component");
		return found.getElement();
	}

	private static String alias(Object end) {
		return end instanceof Pin ? ((Pin) end).alias : ((Part) end).alias;
	}

	void run(String source) {
		final SysmlFeature board = model.resolve(root);
		readParts(board, Arrays.asList(root.getName()), parts);

		final List<String[]> edges = new ArrayList<>();
		for (Object[] c : connections) {
			final SysmlNode node = (SysmlNode) c[0];
			final String type = model.classify(node.getType());
			final String[] ends = SysmlNodes.ends(node);
			if (ends == null)
				continue;
			final Object a = end(ends[0], (List<String>) c[1], node);
			final Object b = end(ends[1], (List<String>) c[1], node);
			if (a == null || b == null)
				continue;
			if (PcbaProfile.NO_CONNECT.equals(type)) {
				for (Object e : new Object[] { a, b })
					if (e instanceof Pin)
						((Pin) e).noConnect = true;
				continue;
			}
			for (Object e : new Object[] { a, b })
				if (e instanceof Pin)
					((Pin) e).connected = true;
			final String name = SysmlNodes.text(model, node, "name",
					SysmlNodes.text(model, node, "stubName", node.getName() == null ? "" : node.getName()));
			edges.add(new String[] { alias(a), alias(b), type == null ? "PCBAConnection" : type, name });
		}

		final Map<String, String> attributes = new LinkedHashMap<>();
		for (Map.Entry<String, String> ent : board.getAttributes().entrySet())
			attributes.put(ent.getKey(), ent.getValue());
		header(source, "PCBAProfile", root.getName(), attributes);
		writeLabelFunctions();
		if (options.isStyle())
			writeStyle();
		String indent = "";
		if (options.isFrame()) {
			out.add("rectangle \"" + SysmlText.call("$sysml_board_label", root.getName(),
					nonEmpty(board.getAttribute("partNumber"), ""), nonEmpty(board.getAttribute("revision"), ""))
					+ "\" as " + SysmlText.unique(SysmlText.sanitize(root.getName()), aliases) + " <<PCBA>> {");
			indent = "  ";
		}
		for (Part p : parts)
			writePart(p, indent);
		if (options.isFrame())
			out.add("}");
		final boolean labels = "net".equals(options.get("labels", "net"));
		for (String[] e : edges)
			out.add(e[0] + " -- " + e[1] + " <<" + e[2] + ">>" + (labels && e[3].isEmpty() == false
					? " : " + SysmlText.call("$sysml_net_label", e[3], e[2])
					: ""));
		footer(source);
	}

	private void writeLabelFunctions() {
		defaultFunction("$sysml_board_label", "$name, $partNumber, $revision", //
				"!if $partNumber == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $name + \"\\n<size:10>\" + $partNumber + \" rev \" + $revision + \"</size>\"");
		defaultFunction("$sysml_component_label", "$designator, $type, $category", //
				"!if $type == \"\" || $type == $category", //
				"!return $designator", //
				"!endif", //
				"!return $designator + \"\\n<size:10>\" + $type + \"</size>\"");
		defaultFunction("$sysml_pin_label", "$name, $direction, $role", "!return $name");
		defaultFunction("$sysml_net_label", "$name, $type", "!return $name");
	}

	private void writeStyle() {
		out.add("skinparam component {");
		out.add("  style rectangle");
		out.add("  portLabels inside");
		out.add("}");
		out.add("<style>");
		out.add("component {");
		out.add("  .IC { BackGroundColor #EAF1FB; LineColor #3F6FB2 }");
		out.add("  .Connector { BackGroundColor #FFF5D6; LineColor #B89A2A }");
		out.add("  .PowerComponent { BackGroundColor #FBEAE9; LineColor #C0504D }");
		out.add("  .DiscretePassive { BackGroundColor #F2F2F2; LineColor #666666 }");
		out.add("  .PassiveNetwork { BackGroundColor #F2F2F2; LineColor #666666 }");
		out.add("  .Electromechanical { BackGroundColor #EAF5EA; LineColor #2E7D32 }");
		out.add("}");
		out.add("rectangle {");
		out.add("  .Subcircuit { BackGroundColor #FFFFFF; LineColor #7F7F7F; LineStyle 6-3 }");
		out.add("  .PCBA { BackGroundColor #FFFFFF; LineColor #404040 }");
		out.add("}");
		out.add("port {");
		out.add("  .unconnected { BackGroundColor #FFB3B3; LineColor #C00000 }");
		out.add("  .nc { BackGroundColor #FFFFFF; LineColor #BFBFBF }");
		out.add("  .PowerPort { BackGroundColor #F8CBAD }");
		out.add("  .ground { BackGroundColor #7F7F7F }");
		out.add("}");
		out.add("arrow {");
		out.add("  .PowerRail { LineColor #C0504D; LineThickness 2 }");
		out.add("  .BusHarness { LineColor #7E5BA0; LineThickness 3 }");
		out.add("}");
		out.add("</style>");
	}

	private void writePart(Part p, String indent) {
		final String type = SysmlNodes.lastSegment(p.feature.getType());
		final String label = SysmlText.call("$sysml_component_label",
				nonEmpty(p.feature.getAttribute("designator"), p.feature.getName()), type == null ? "" : type,
				p.category);
		if (PcbaProfile.SUBCIRCUIT.equals(p.category)) {
			out.add(indent + "rectangle \"" + label + "\" as " + p.alias + " <<Subcircuit>> {");
			for (Part child : p.children)
				writePart(child, indent + "  ");
			out.add(indent + "}");
			return;
		}
		if (p.pins.isEmpty()) {
			out.add(indent + "component \"" + label + "\" as " + p.alias + " <<" + p.category + ">>");
			return;
		}
		out.add(indent + "component \"" + label + "\" as " + p.alias + " <<" + p.category + ">> {");
		for (Pin pin : p.pins) {
			final String keyword = pin.in ? "portin" : pin.out ? "portout" : "port";
			final String state = pin.noConnect ? "nc" : pin.connected ? null : "unconnected";
			final String portType = SysmlNodes.lastSegment(model.classify(pin.feature));
			out.add(indent + "  " + keyword + " \"" + SysmlText.call("$sysml_pin_label", pin.feature.getName(),
					nonEmpty(pin.feature.getAttribute("direction"), ""),
					nonEmpty(pin.feature.getAttribute("role"), "")) + "\" as " + pin.alias
					+ SysmlText.stereotypes(portType, pin.feature.getAttribute("role"), state));
		}
		out.add(indent + "}");
	}

}
