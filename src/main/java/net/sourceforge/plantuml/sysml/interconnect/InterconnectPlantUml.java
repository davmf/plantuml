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
package net.sourceforge.plantuml.sysml.interconnect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Writes an {@link InterconnectModel} as component diagram source.
 * <p>
 * The output is plain PlantUML, run through the preprocessor like an included
 * file, so that the importing diagram can change how it looks:
 * <ul>
 * <li>every element carries stereotypes taken from the model: the profile
 * category on components (<code>&lt;&lt;PCBAUnit&gt;&gt;</code>,
 * <code>&lt;&lt;Cable&gt;&gt;</code>...), gender, connector kind and mating
 * state on ports (<code>&lt;&lt;female&gt;&gt;</code>,
 * <code>&lt;&lt;unmated&gt;&gt;</code>...), contact kind on contact ports
 * (<code>&lt;&lt;power&gt;&gt;</code>) and connection type and protocol on
 * links (<code>&lt;&lt;Mate&gt;&gt;</code>,
 * <code>&lt;&lt;InterfaceLink&gt;&gt; &lt;&lt;can&gt;&gt;</code>);</li>
 * <li>every label is the result of a preprocessor function
 * (<code>$sysml_unit_label</code>, <code>$sysml_connector_label</code>...)
 * that is only defined here when the diagram has not defined it first;</li>
 * <li>aliases are stable (<code>A1</code>, <code>A1_J2</code>,
 * <code>A1_J2_c1</code>) so the diagram can add links, notes or layout hints
 * after the import;</li>
 * <li>system attributes are exposed as variables (<code>$sysml_system</code>,
 * <code>$sysml_partNumber</code>, <code>$sysml_revision</code>...).</li>
 * </ul>
 */
public final class InterconnectPlantUml {

	private enum Direction {
		IN, OUT
	}

	private final InterconnectModel model;
	private final IcOptions options;
	private final Predicate<String> isFunctionDefined;
	private final List<String> out = new ArrayList<>();
	private final Map<IcConnector, Direction> directions = new HashMap<>();
	private final Map<IcConnector, String> aliases = new HashMap<>();
	private final Set<IcConnector> mated = new HashSet<>();
	private final Set<IcConnector> noMated = new HashSet<>();
	/** Mates drawn target-to-source, so that a Cable's ends face both ways */
	private final Set<IcConnection> reversed = new HashSet<>();
	/** Cables drawn as one edge, with the Mates they replace */
	private final Map<IcInstance, IcConnection[]> cableEdges = new LinkedHashMap<>();

	private InterconnectPlantUml(InterconnectModel model, IcOptions options, Predicate<String> isFunctionDefined) {
		this.model = model;
		this.options = options;
		this.isFunctionDefined = isFunctionDefined;
	}

	/**
	 * @param source            what was read, for the leading comment
	 * @param isFunctionDefined tells which label functions the diagram already
	 *                          defines: only the others get a default. (A
	 *                          default cannot be guarded by an
	 *                          <code>!if</code>: the preprocessor declares
	 *                          functions before it evaluates conditions.)
	 */
	public static List<String> generate(InterconnectModel model, IcOptions options, String source,
			Predicate<String> isFunctionDefined) {
		final InterconnectPlantUml writer = new InterconnectPlantUml(model, options, isFunctionDefined);
		writer.run(source);
		return Collections.unmodifiableList(writer.out);
	}

	private boolean physical() {
		return options.getView() != IcOptions.View.LOGICAL;
	}

	private boolean logical() {
		return options.getView() != IcOptions.View.PHYSICAL;
	}

	private boolean contacts() {
		// A logical view links connectors, not contacts
		return options.getDetail() == IcOptions.Detail.CONTACT && options.getView() == IcOptions.View.PHYSICAL;
	}

	private void run(String source) {
		assignAliases();
		analyse();

		final IcInstance system = model.getSystem();
		out.add("' ---- !include_sysml " + source + " : " + system.getName() + " ----");
		out.add("!$sysml_system = \"" + quote(system.getName()) + "\"");
		for (Map.Entry<String, String> ent : model.getSystemAttributes().entrySet())
			if (ent.getKey().matches("[A-Za-z_][A-Za-z0-9_]*"))
				out.add("!$sysml_" + ent.getKey() + " = \"" + quote(ent.getValue()) + "\"");
		writeLabelFunctions();
		if (options.isStyle())
			writeStyle();

		String indent = "";
		if (options.isFrame()) {
			final String revision = model.getSystemAttributes().get("revision");
			out.add("rectangle \"" + unitLabel(system.getName(), system.getTypeName(), system.getPartNumber(),
					IcCategory.SYSTEM.getProfileName(), revision == null ? "" : "rev " + revision) + "\" as "
					+ system.getAlias() + " <<" + IcCategory.SYSTEM.getProfileName() + ">> {");
			indent = "  ";
		}
		for (IcInstance child : system.getChildren())
			writeInstance(child, indent);
		if (options.isFrame())
			out.add("}");

		writeConnections();
		for (String warning : model.getWarnings())
			out.add("' sysml warning: " + warning);
		out.add("' ---- end of !include_sysml " + source + " ----");
	}

	// ------------------------------------------------------------------
	// Analysis
	// ------------------------------------------------------------------

	private void assignAliases() {
		final Map<String, Integer> leafCount = new HashMap<>();
		for (IcInstance inst : model.getInstances())
			leafCount.merge(sanitize(inst.getName()), 1, Integer::sum);

		final Set<String> used = new HashSet<>();
		final IcInstance system = model.getSystem();
		system.setAlias(unique(sanitize(system.getName()), used));
		for (IcInstance inst : model.getInstances()) {
			final String leaf = sanitize(inst.getName());
			final String base = leafCount.get(leaf) == 1 ? leaf : sanitize(inst.getPath().replace('.', '_'));
			inst.setAlias(unique(base, used));
			for (IcConnector c : inst.getConnectors())
				aliases.put(c, inst.getAlias() + "_" + sanitize(c.getName()));
		}
	}

	private static String unique(String base, Set<String> used) {
		String result = base;
		for (int i = 2; used.add(result) == false; i++)
			result = base + "_" + i;
		return result;
	}

	static String sanitize(String name) {
		final StringBuilder sb = new StringBuilder();
		for (char c : name.toCharArray())
			sb.append(Character.isLetterOrDigit(c) || c == '_' ? c : '_');
		if (sb.length() == 0 || Character.isDigit(sb.charAt(0)))
			sb.insert(0, '_');
		return sb.toString();
	}

	private void analyse() {
		for (IcConnection c : model.getConnections()) {
			final IcConnector a = c.getSource().getConnector();
			final IcConnector b = c.getTarget().getConnector();
			if (c.getCategory() == IcCategory.MATE && a != null && b != null) {
				mated.add(a);
				mated.add(b);
			} else if (c.getCategory() == IcCategory.NO_MATE) {
				if (a != null)
					noMated.add(a);
				if (b != null)
					noMated.add(b);
			}
		}
		if (physical() && options.getCables() == IcOptions.Cables.EDGE)
			findCableEdges();

		// Port sides follow the direction connections are written in: the source
		// of "connect a to b" is an output, the target an input
		if (physical()) {
			orientCables();
			setDirections(IcCategory.MATE);
		}
		if (logical())
			setDirections(IcCategory.INTERFACE_LINK);
	}

	/**
	 * A Cable whose mates all point into it (or all out of it) would have every
	 * end on one side: keep its first end as written and turn the others round.
	 */
	private void orientCables() {
		for (IcInstance inst : model.getInstances()) {
			if (inst.getCategory() != IcCategory.CABLE || inst.getConnectors().size() < 2)
				continue;
			final List<IcConnection> mates = new ArrayList<>();
			boolean anyIn = false;
			boolean anyOut = false;
			for (IcConnector c : inst.getConnectors()) {
				final IcConnection mate = firstMate(c);
				mates.add(mate);
				if (mate == null)
					continue;
				if (target(mate) == c)
					anyIn = true;
				else
					anyOut = true;
			}
			if (anyIn && anyOut)
				continue;
			for (int i = 0; i < mates.size(); i++) {
				final IcConnection mate = mates.get(i);
				final IcConnector c = inst.getConnectors().get(i);
				if (mate == null)
					continue;
				final boolean wantIn = i == 0;
				if ((target(mate) == c) != wantIn)
					reversed.add(mate);
			}
		}
	}

	private IcConnection firstMate(IcConnector connector) {
		for (IcConnection c : model.getConnections())
			if (c.getCategory() == IcCategory.MATE
					&& (c.getSource().getConnector() == connector || c.getTarget().getConnector() == connector))
				return c;
		return null;
	}

	/** The source end as drawn. */
	private IcConnector source(IcConnection c) {
		return reversed.contains(c) ? c.getTarget().getConnector() : c.getSource().getConnector();
	}

	/** The target end as drawn. */
	private IcConnector target(IcConnection c) {
		return reversed.contains(c) ? c.getSource().getConnector() : c.getTarget().getConnector();
	}

	private void setDirections(IcCategory category) {
		for (IcConnection c : model.getConnections()) {
			if (c.getCategory() != category)
				continue;
			final IcConnector a = source(c);
			final IcConnector b = target(c);
			if (a == null || b == null)
				continue;
			if (directions.containsKey(a) == false)
				directions.put(a, Direction.OUT);
			if (directions.containsKey(b) == false)
				directions.put(b, Direction.IN);
		}
	}

	/** A Cable with two connectors, each mated once to a Unit. */
	private void findCableEdges() {
		for (IcInstance inst : model.getInstances()) {
			if (inst.getCategory() != IcCategory.CABLE || inst.getConnectors().size() != 2)
				continue;
			final IcConnection first = singleMate(inst.getConnectors().get(0));
			final IcConnection second = singleMate(inst.getConnectors().get(1));
			if (first != null && second != null)
				cableEdges.put(inst, new IcConnection[] { first, second });
		}
	}

	private IcConnection singleMate(IcConnector connector) {
		IcConnection result = null;
		for (IcConnection c : model.getConnections()) {
			if (c.getCategory() != IcCategory.MATE)
				continue;
			final IcConnector other;
			if (c.getSource().getConnector() == connector)
				other = c.getTarget().getConnector();
			else if (c.getTarget().getConnector() == connector)
				other = c.getSource().getConnector();
			else
				continue;
			if (result != null || other == null || other.getOwner().getCategory().isUnit() == false)
				return null;
			result = c;
		}
		return result;
	}

	private static IcConnector otherEnd(IcConnection mate, IcInstance cable) {
		return mate.getSource().getInstance() == cable ? mate.getTarget().getConnector()
				: mate.getSource().getConnector();
	}

	private boolean isDrawn(IcInstance inst) {
		if (inst.getCategory() == IcCategory.CABLE)
			return physical() && cableEdges.containsKey(inst) == false;
		return true;
	}

	// ------------------------------------------------------------------
	// Preamble
	// ------------------------------------------------------------------

	private void writeLabelFunctions() {
		defaultFunction("$sysml_unit_label", "$designator, $type, $partNumber, $category, $details", //
				"!$sysml_l = $designator", //
				"!if $type != \"\" && $type != $category", //
				"!$sysml_l = $sysml_l + \"\\n\" + $type", //
				"!endif", //
				"!if $partNumber != \"\"", //
				"!$sysml_l = $sysml_l + \"\\n<size:10>\" + $partNumber + \"</size>\"", //
				"!endif", //
				"!return $sysml_l");
		defaultFunction("$sysml_connector_label", "$designator, $series, $gender, $kind, $partNumber", //
				"!return $designator");
		defaultFunction("$sysml_contact_label", "$contactId, $signalName, $kind", //
				"!if $signalName == \"\"", //
				"!return $contactId", //
				"!endif", //
				"!return $contactId + \" \" + $signalName");
		defaultFunction("$sysml_cable_label", "$designator, $type, $partNumber, $details", //
				"!return $designator");
		defaultFunction("$sysml_link_label", "$protocol, $name", //
				"!return $protocol");
	}

	private void defaultFunction(String name, String parameters, String... body) {
		if (isFunctionDefined.test(name))
			return;
		out.add("!function " + name + "(" + parameters + ")");
		Collections.addAll(out, body);
		out.add("!endfunction");
	}

	private void writeStyle() {
		out.add("skinparam component {");
		out.add("  style rectangle");
		out.add("  portLabels inside");
		out.add("}");
		out.add("<style>");
		out.add("component {");
		category("PCBAUnit", "#EAF1FB", "#3F6FB2");
		category("Module", "#F0EAF7", "#7E5BA0");
		category("Sensor", "#EAF5EA", "#2E7D32");
		category("Actuator", "#FBEAE9", "#C0504D");
		category("Motor", "#FBEAE9", "#C0504D");
		category("Solenoid", "#FBEAE9", "#C0504D");
		category("PowerSource", "#FFF5D6", "#B89A2A");
		category("UserInterfaceDevice", "#E6F4F4", "#2F8F8F");
		category("Device", "#F2F2F2", "#666666");
		category("Unit", "#F2F2F2", "#666666");
		out.add("  .Cable { BackGroundColor #FFFFFF; LineColor #8C6D4B; RoundCorner 20 }");
		out.add("}");
		out.add("rectangle {");
		out.add("  .Assembly { BackGroundColor #FFFFFF; LineColor #7F7F7F; LineStyle 6-3 }");
		out.add("  .InterconnectSystem { BackGroundColor #FFFFFF; LineColor #404040 }");
		out.add("}");
		out.add("port {");
		out.add("  .unmated { BackGroundColor #FFB3B3; LineColor #C00000 }");
		out.add("  .NoMate { BackGroundColor #E0E0E0; LineColor #9E9E9E }");
		out.add("  .power { BackGroundColor #F8CBAD }");
		out.add("  .ground { BackGroundColor #7F7F7F }");
		out.add("  .shield { BackGroundColor #C5E0B4 }");
		out.add("  .nc { BackGroundColor #FFFFFF; LineColor #BFBFBF }");
		out.add("}");
		out.add("arrow {");
		out.add("  .direct { LineThickness 2.5 }");
		out.add("  .Cable { LineColor #8C6D4B; LineThickness 2; FontColor #8C6D4B }");
		out.add("  .InterfaceLink { LineColor #7E5BA0; LineStyle 6-4; FontColor #7E5BA0 }");
		out.add("  .UnitLink { LineColor #7E5BA0; LineStyle 2-4; FontColor #7E5BA0 }");
		out.add("}");
		out.add("</style>");
	}

	private void category(String name, String back, String line) {
		out.add("  ." + name + " { BackGroundColor " + back + "; LineColor " + line + " }");
	}

	// ------------------------------------------------------------------
	// Elements
	// ------------------------------------------------------------------

	private void writeInstance(IcInstance inst, String indent) {
		if (isDrawn(inst) == false)
			return;
		final String stereotype = " <<" + inst.getCategory().getProfileName() + ">>";
		final String label = unitLabel(inst.getDesignator(), inst.getTypeName(), inst.getPartNumber(),
				inst.getCategory().getProfileName(), String.join(", ", inst.getDetails()));
		if (inst.getCategory() == IcCategory.ASSEMBLY) {
			out.add(indent + "rectangle \"" + label + "\" as " + inst.getAlias() + stereotype + " {");
			for (IcInstance child : inst.getChildren())
				writeInstance(child, indent + "  ");
			out.add(indent + "}");
			return;
		}
		if (inst.getConnectors().isEmpty()) {
			out.add(indent + "component \"" + label + "\" as " + inst.getAlias() + stereotype);
			return;
		}
		out.add(indent + "component \"" + label + "\" as " + inst.getAlias() + stereotype + " {");
		for (IcConnector c : inst.getConnectors())
			writeConnector(c, indent + "  ");
		out.add(indent + "}");
	}

	private void writeConnector(IcConnector c, String indent) {
		final String keyword = portKeyword(c);
		final String state = connectorState(c);
		final String label = "$sysml_connector_label(" + args(c.getDesignator(), c.getSeries(), c.getGender(),
				c.getKind(), c.getPartNumber()) + ")";
		if (contacts() && c.getContacts().isEmpty() == false) {
			out.add(indent + "group \"" + label + "\" {");
			for (IcContact ct : c.getContacts())
				out.add(indent + "  " + keyword + " \"$sysml_contact_label("
						+ args(ct.getContactId(), ct.getSignalName(), ct.getKind()) + ")\" as " + contactAlias(ct)
						+ stereotypes(ct.getKind(), state));
			out.add(indent + "}");
			return;
		}
		out.add(indent + keyword + " \"" + label + "\" as " + aliases.get(c)
				+ stereotypes(c.getGender(), c.getKind(), state));
	}

	private String portKeyword(IcConnector c) {
		final Direction d = directions.get(c);
		if (d == Direction.IN)
			return "portin";
		if (d == Direction.OUT)
			return "portout";
		return "port";
	}

	/** NoMate / unmated, shown only where Mates are drawn. */
	private String connectorState(IcConnector c) {
		if (physical() == false)
			return null;
		if (noMated.contains(c))
			return IcCategory.NO_MATE.getProfileName();
		if (mated.contains(c) == false)
			return "unmated";
		return null;
	}

	private String contactAlias(IcContact ct) {
		return aliases.get(ct.getConnector()) + "_" + sanitize(ct.getName());
	}

	/** The port an edge to this connector attaches to. */
	private String anchor(IcConnector c) {
		if (contacts() && c.getContacts().isEmpty() == false)
			return contactAlias(c.getContacts().get(0));
		return aliases.get(c);
	}

	// ------------------------------------------------------------------
	// Connections
	// ------------------------------------------------------------------

	private void writeConnections() {
		final Set<IcConnection> replaced = new HashSet<>();
		for (IcConnection[] pair : cableEdges.values())
			Collections.addAll(replaced, pair);

		for (IcConnection c : model.getConnections()) {
			if (replaced.contains(c))
				continue;
			if (c.getCategory() == IcCategory.MATE && physical())
				writeMate(c);
			else if (c.getCategory() == IcCategory.INTERFACE_LINK && logical())
				writeInterfaceLink(c);
			else if (c.getCategory() == IcCategory.UNIT_LINK && logical())
				writeUnitLink(c);
		}
		for (Map.Entry<IcInstance, IcConnection[]> ent : cableEdges.entrySet())
			writeCableEdge(ent.getKey(), ent.getValue());
	}

	private void writeMate(IcConnection mate) {
		final IcConnector a = source(mate);
		final IcConnector b = target(mate);
		if (a == null || b == null || isDrawn(a.getOwner()) == false || isDrawn(b.getOwner()) == false)
			return;
		final boolean direct = a.getOwner().getCategory() != IcCategory.CABLE
				&& b.getOwner().getCategory() != IcCategory.CABLE;
		final String stereotype = " <<" + IcCategory.MATE.getProfileName() + ">>" + (direct ? " <<direct>>" : "");

		if (contacts() && a.getContacts().isEmpty() == false && b.getContacts().isEmpty() == false) {
			final IcOptions.Mates mates = options.getMates();
			final String wireStereotype = mates == IcOptions.Mates.WIRES ? stereotype : "";
			final List<String> lines = new ArrayList<>();
			for (IcContact ca : a.getContacts()) {
				final IcContact cb = b.getContactById(ca.getContactId());
				// Two nc contacts mate a pin position, not a conductor
				if (cb != null && (isNc(ca) && isNc(cb)) == false)
					lines.add(contactAlias(ca) + " -- " + contactAlias(cb) + wireStereotype);
			}
			if (lines.isEmpty() == false) {
				if (mates == IcOptions.Mates.WIRES) {
					out.addAll(lines);
					return;
				}
				out.add("harness \"" + quote(a.getOwner().getDesignator() + "." + a.getDesignator() + "\u2013"
						+ b.getOwner().getDesignator() + "." + b.getDesignator()) + "\""
						+ (mates == IcOptions.Mates.AUTO ? " [bundle=auto]" : "") + " {");
				for (String line : lines)
					out.add("  " + line);
				out.add("}");
				return;
			}
		}
		out.add(anchor(a) + " -- " + anchor(b) + stereotype);
	}

	private static boolean isNc(IcContact contact) {
		return "nc".equals(contact.getKind());
	}

	private void writeCableEdge(IcInstance cable, IcConnection[] mates) {
		IcConnector from = otherEnd(mates[0], cable);
		IcConnector to = otherEnd(mates[1], cable);
		// Draw from the side the model writes as a source
		if (mates[0].getSource().getInstance() == cable && mates[1].getTarget().getInstance() == cable) {
			final IcConnector tmp = from;
			from = to;
			to = tmp;
		}
		out.add(anchor(from) + " -- " + anchor(to) + " <<" + IcCategory.CABLE.getProfileName() + ">> : "
				+ "$sysml_cable_label(" + args(cable.getDesignator(), cable.getTypeName(), cable.getPartNumber(),
						String.join(", ", cable.getDetails()))
				+ ")");
	}

	private void writeInterfaceLink(IcConnection link) {
		final IcConnector a = link.getSource().getConnector();
		final IcConnector b = link.getTarget().getConnector();
		if (a == null || b == null || isDrawn(a.getOwner()) == false || isDrawn(b.getOwner()) == false)
			return;
		out.add(anchor(a) + " ..> " + anchor(b) + linkStereotypes(link) + " : $sysml_link_label("
				+ args(link.getProtocol(), link.getName()) + ")");
	}

	private void writeUnitLink(IcConnection link) {
		final IcInstance a = link.getSource().getInstance();
		final IcInstance b = link.getTarget().getInstance();
		out.add(a.getAlias() + " ..> " + b.getAlias() + linkStereotypes(link) + " : $sysml_link_label("
				+ args(link.getProtocol(), link.getName()) + ")");
	}

	private static String linkStereotypes(IcConnection link) {
		return stereotypes(link.getCategory().getProfileName(), link.getProtocol());
	}

	// ------------------------------------------------------------------
	// Text helpers
	// ------------------------------------------------------------------

	private static String unitLabel(String designator, String type, String partNumber, String category,
			String details) {
		return "$sysml_unit_label(" + args(designator, type, partNumber, category, details) + ")";
	}

	private static String stereotypes(String... names) {
		final StringBuilder sb = new StringBuilder();
		for (String n : names)
			if (n != null && n.isEmpty() == false)
				sb.append(" <<").append(n.replace('>', '_').replace('<', '_')).append(">>");
		return sb.toString();
	}

	private static String args(String... values) {
		final StringBuilder sb = new StringBuilder();
		for (String v : values) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append('"').append(quote(v)).append('"');
		}
		return sb.toString();
	}

	/** Makes a model string safe inside a double-quoted preprocessor string. */
	static String quote(String s) {
		if (s == null)
			return "";
		return s.replace('"', '\'').replace("\n", " ");
	}

}
