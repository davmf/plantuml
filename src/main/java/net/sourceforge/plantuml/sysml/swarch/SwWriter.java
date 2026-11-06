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
package net.sourceforge.plantuml.sysml.swarch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
 * Writes a layered software architecture as component diagram source: one
 * <code>rectangle &lt;&lt;Layer&gt;&gt;</code> per Layer, stacked by kind
 * (application and service on top, hardware at the bottom), holding its
 * components; SignalFlows are solid arrows (a pair of opposite flows between
 * bidirectional ports is one double-headed arrow), Dependencies dashed ones.
 */
final class SwWriter extends SysmlWriter {

	private static final List<String> KIND_ORDER = Arrays.asList("application", "service", "controller", "driver",
			"hardware");

	private static final class Layer {
		String alias;
		String name;
		String kind;
		int index;
		final List<Component> components = new ArrayList<>();
	}

	private static final class Component {
		String alias;
		String name;
		String type;
	}

	private static final class Flow {
		Component from;
		Component to;
		String fromPort;
		String toPort;
		String category;
		String name;
		boolean bidir;
		boolean merged;
	}

	private final SysmlModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final List<Layer> layers = new ArrayList<>();
	private final SysmlScope<Component> scope = new SysmlScope<>();
	private final List<Flow> flows = new ArrayList<>();
	private final Set<String> aliases = new HashSet<>();

	SwWriter(SysmlModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
	}

	private void read() {
		final SysmlFeature feature = model.resolve(root);
		for (SysmlFeature f : feature.getFeatures()) {
			if (SwProfile.LAYER.equals(model.classify(f)) == false)
				continue;
			final Layer layer = new Layer();
			layer.index = layers.size();
			layer.alias = SysmlText.unique(SysmlText.sanitize(f.getName()), aliases);
			layer.name = nonEmpty(f.getAttribute("name"), f.getName());
			layer.kind = nonEmpty(f.getAttribute("kind"), "");
			for (SysmlFeature c : f.getFeatures()) {
				if (c.isKeyword("part") == false)
					continue;
				final Component component = new Component();
				component.alias = SysmlText.unique(SysmlText.sanitize(c.getName()), aliases);
				component.name = nonEmpty(c.getAttribute("name"), c.getName());
				final String type = SysmlNodes.lastSegment(c.getType());
				component.type = type == null || type.equals(SwProfile.COMPONENT) ? "" : type;
				layer.components.add(component);
				scope.register(Arrays.asList(f.getName(), c.getName()), component);
			}
			layers.add(layer);
		}
		if (layers.isEmpty())
			warn(root.getName() + ": no Layer found");
		for (SysmlNode connection : feature.getConnections())
			readFlow(connection);
	}

	private static String nonEmpty(String s, String defaultValue) {
		return s == null || s.isEmpty() ? defaultValue : s;
	}

	private void readFlow(SysmlNode connection) {
		final String category = model.classify(connection.getType());
		if (SwProfile.SIGNAL_FLOW.equals(category) == false && SwProfile.DEPENDENCY.equals(category) == false)
			return;
		final String[] ends = SysmlNodes.ends(connection);
		if (ends == null)
			return;
		final SysmlScope.Found<Component> a = scope.resolve(ends[0], null);
		final SysmlScope.Found<Component> b = scope.resolve(ends[1], null);
		if (a == null || b == null) {
			warn(connection.getName() + ": cannot resolve '" + (a == null ? ends[0] : ends[1]) + "'");
			return;
		}
		final Flow flow = new Flow();
		flow.from = a.getElement();
		flow.to = b.getElement();
		flow.fromPort = a.getPort();
		flow.toPort = b.getPort();
		flow.category = category;
		flow.name = SysmlNodes.text(model, connection, "name", "");
		flows.add(flow);
	}

	/** Two opposite SignalFlows between bidirectional ports are one link. */
	private void mergeBidirectional() {
		for (Flow f : flows)
			if (f.merged == false && SwProfile.SIGNAL_FLOW.equals(f.category) && "signalsBidir".equals(f.fromPort))
				for (Flow g : flows)
					if (g != f && g.merged == false && g.bidir == false && SwProfile.SIGNAL_FLOW.equals(g.category)
							&& g.from == f.to && g.to == f.from && "signalsBidir".equals(g.fromPort)) {
						f.bidir = true;
						g.merged = true;
						break;
					}
	}

	void run(String source) {
		read();
		mergeBidirectional();
		final Map<String, String> attributes = new LinkedHashMap<>();
		header(source, "SoftwareArchitectureProfile", root.getName(), attributes);
		defaultFunction("$sysml_layer_label", "$name, $kind", "!return $name");
		defaultFunction("$sysml_component_label", "$name, $type", "!return $name");
		defaultFunction("$sysml_flow_label", "$name, $category", "!return $name");
		if (options.isStyle())
			writeStyle();

		final List<Layer> ordered = new ArrayList<>(layers);
		if ("kind".equals(options.get("order", "kind")))
			Collections.sort(ordered, new Comparator<Layer>() {
				public int compare(Layer a, Layer b) {
					final int c = Integer.compare(rank(a), rank(b));
					return c != 0 ? c : Integer.compare(a.index, b.index);
				}
			});
		String indent = "";
		if (options.isFrame()) {
			out.add("rectangle \"" + SysmlText.quote(root.getName()) + "\" as "
					+ SysmlText.unique(SysmlText.sanitize(root.getName()), aliases) + " <<Architecture>> {");
			indent = "  ";
		}
		for (Layer layer : ordered) {
			out.add(indent + "rectangle \"" + SysmlText.call("$sysml_layer_label", layer.name, layer.kind) + "\" as "
					+ layer.alias + " <<Layer>>" + SysmlText.stereotypes(layer.kind) + " {");
			for (Component c : layer.components)
				out.add(indent + "  component \"" + SysmlText.call("$sysml_component_label", c.name, c.type)
						+ "\" as " + c.alias + " <<LayerComponent>>");
			out.add(indent + "}");
		}
		if (options.isFrame())
			out.add("}");
		// Keep the layers stacked in order, whatever the flows say
		for (int i = 1; i < ordered.size(); i++)
			out.add(ordered.get(i - 1).alias + " -[hidden]down- " + ordered.get(i).alias);
		for (Flow f : flows) {
			if (f.merged)
				continue;
			final String arrow = SwProfile.DEPENDENCY.equals(f.category) ? " ..> " : f.bidir ? " <--> " : " --> ";
			out.add(f.from.alias + arrow + f.to.alias + SysmlText.stereotypes(f.category, f.bidir ? "bidir" : null)
					+ (f.name.isEmpty() ? "" : " : " + SysmlText.call("$sysml_flow_label", f.name, f.category)));
		}
		footer(source);
	}

	private static int rank(Layer layer) {
		final int i = KIND_ORDER.indexOf(layer.kind);
		return i == -1 ? KIND_ORDER.size() : i;
	}

	private void writeStyle() {
		out.add("skinparam component {");
		out.add("  style rectangle");
		out.add("}");
		out.add("hide stereotype");
		out.add("<style>");
		out.add("rectangle {");
		out.add("  .Layer { BackGroundColor #FAFAFA; LineColor #7F7F7F; FontStyle bold }");
		out.add("  .hardware { BackGroundColor #F2F2F2 }");
		out.add("  .driver { BackGroundColor #EAF1FB }");
		out.add("  .controller { BackGroundColor #EAF5EA }");
		out.add("  .application { BackGroundColor #FFF5D6 }");
		out.add("  .service { BackGroundColor #F0EAF7 }");
		out.add("}");
		out.add("component { .LayerComponent { BackGroundColor #FFFFFF; LineColor #404040 } }");
		out.add("arrow {");
		out.add("  .SignalFlow { LineColor #404040 }");
		out.add("  .Dependency { LineColor #7F7F7F; LineStyle 6-4 }");
		out.add("}");
		out.add("</style>");
	}

}
