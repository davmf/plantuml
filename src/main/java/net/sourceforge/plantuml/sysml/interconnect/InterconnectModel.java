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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.sysml.SysmlException;
import net.sourceforge.plantuml.sysml.SysmlFeature;
import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;

/**
 * One InterconnectSystem read from SysML v2 models written with
 * InterconnectProfile: its Units, Cables and Assemblies, their connectors and
 * contacts, and the connections between them.
 * <p>
 * Problems that do not stop a diagram (a connection end that names nothing,
 * a part whose type is unknown) are collected as warnings.
 */
public final class InterconnectModel {

	private final IcInstance system;
	private final List<IcConnection> connections = new ArrayList<>();
	private final List<String> warnings = new ArrayList<>();
	private final Map<String, String> systemAttributes = new LinkedHashMap<>();

	private InterconnectModel(IcInstance system) {
		this.system = system;
	}

	/** The simple names of the profile types, for {@link SysmlModel}. */
	public static List<String> profileNames() {
		final List<String> result = new ArrayList<>();
		for (IcCategory c : IcCategory.values())
			result.add(c.getProfileName());
		return result;
	}

	public static SysmlModel newSysmlModel() {
		return new SysmlModel(profileNames());
	}

	/**
	 * @param systemName the name (simple or qualified) of the
	 *                   InterconnectSystem part, or <code>null</code> for the
	 *                   first one found
	 */
	public static InterconnectModel build(SysmlModel model, String systemName) throws SysmlException {
		final List<SysmlNode> systems = new ArrayList<>();
		for (SysmlNode usage : model.getTopLevelUsages())
			if (usage.isKeyword("part") && IcCategory.fromProfileName(model.classify(usage.getType())) == IcCategory.SYSTEM)
				systems.add(usage);

		if (systems.isEmpty())
			throw new SysmlException("no InterconnectSystem part found (expected 'part x : InterconnectSystem')");

		SysmlNode selected = null;
		if (systemName == null) {
			selected = systems.get(0);
		} else {
			for (SysmlNode s : systems)
				if (systemName.equals(s.getName()) || systemName.equals(s.getQualifiedName()))
					selected = s;
			if (selected == null) {
				final List<String> names = new ArrayList<>();
				for (SysmlNode s : systems)
					names.add(s.getName());
				throw new SysmlException("no InterconnectSystem named '" + systemName + "' (found: "
						+ String.join(", ", names) + ")");
			}
		}

		final SysmlFeature feature = model.resolve(selected);
		final IcInstance system = new IcInstance(null, selected.getName(), IcCategory.SYSTEM, typeName(feature),
				selected.getName(), feature.getAttribute("partNumber"), feature.getDoc());
		final InterconnectModel result = new InterconnectModel(system);
		for (Map.Entry<String, String> ent : feature.getAttributes().entrySet())
			if (ent.getValue() != null)
				result.systemAttributes.put(ent.getKey(), ent.getValue());

		final Map<IcInstance, SysmlFeature> features = new LinkedHashMap<>();
		features.put(system, feature);
		result.buildChildren(model, system, feature, features);
		for (Map.Entry<IcInstance, SysmlFeature> ent : features.entrySet())
			for (SysmlNode connection : ent.getValue().getConnections())
				result.addConnection(model, ent.getKey(), connection);

		return result;
	}

	private static String typeName(SysmlFeature feature) {
		final String type = feature.getType();
		if (type == null)
			return null;
		final int idx = type.lastIndexOf("::");
		return idx == -1 ? type : type.substring(idx + 2);
	}

	private void buildChildren(SysmlModel model, IcInstance owner, SysmlFeature ownerFeature,
			Map<IcInstance, SysmlFeature> features) {
		for (SysmlFeature f : ownerFeature.getFeatures()) {
			if (f.isFromLibrary() || f.isKeyword("part") == false)
				continue;
			final IcCategory category = IcCategory.fromProfileName(model.classify(f));
			if (category == null) {
				if (f.getType() != null)
					warnings.add("part " + f.getName() + " : " + f.getType()
							+ " is not an InterconnectProfile Unit, Cable or Assembly");
				continue;
			}
			if (category.isUnit() == false && category != IcCategory.CABLE && category != IcCategory.ASSEMBLY)
				continue;

			final String designator = valueOr(f.getAttribute("designator"), f.getName());
			final IcInstance instance = new IcInstance(owner, f.getName(), category, typeName(f), designator,
					f.getAttribute("partNumber"), f.getDoc());
			addDetails(instance, f);
			features.put(instance, f);
			for (SysmlFeature nested : f.getFeatures()) {
				if (nested.isFromLibrary())
					continue;
				final IcCategory nestedCategory = IcCategory.fromProfileName(model.classify(nested));
				if (nested.isKeyword("port") && nestedCategory == IcCategory.CONNECTOR_PORT)
					instance.addConnector(buildConnector(model, instance, nested));
				else if (nested.isKeyword("part") && nestedCategory == IcCategory.SPLICE)
					instance.addSplice(nested.getName());
			}
			if (category == IcCategory.ASSEMBLY)
				buildChildren(model, instance, f, features);
		}
	}

	private static void addDetails(IcInstance instance, SysmlFeature f) {
		for (String attribute : new String[] { "nominalVoltage", "maxCurrent", "motorKind", "ratedVoltage",
				"ratedCurrent", "measurand", "length" }) {
			final String value = f.getAttribute(attribute);
			if (value != null)
				instance.addDetail(value);
		}
		if ("true".equals(f.getAttribute("shielded")))
			instance.addDetail("shielded");
	}

	private static IcConnector buildConnector(SysmlModel model, IcInstance owner, SysmlFeature f) {
		final IcConnector connector = new IcConnector(owner, f.getName(),
				valueOr(f.getAttribute("designator"), f.getName()), f.getAttribute("gender"), f.getAttribute("kind"),
				f.getAttribute("series"), f.getAttribute("partNumber"));
		for (SysmlFeature nested : f.getFeatures())
			if (nested.isKeyword("port")
					&& IcCategory.fromProfileName(model.classify(nested)) == IcCategory.CONTACT_PORT)
				connector.addContact(new IcContact(connector, nested.getName(),
						valueOr(nested.getAttribute("contactId"), nested.getName()), nested.getAttribute("kind"),
						nested.getAttribute("signalName")));
		return connector;
	}

	private static String valueOr(String value, String fallback) {
		return value == null || value.isEmpty() ? fallback : value;
	}

	private void addConnection(SysmlModel model, IcInstance owner, SysmlNode node) {
		final List<String> ends = node.getConnectEnds();
		if (ends.size() != 2) {
			warnings.add(node.getLocation() + ": only binary connections are drawn");
			return;
		}
		final IcEnd source = resolveEnd(owner, ends.get(0));
		final IcEnd target = resolveEnd(owner, ends.get(1));
		if (source == null || target == null) {
			warnings.add(node.getLocation() + ": cannot resolve " + (source == null ? ends.get(0) : ends.get(1))
					+ " from " + (owner.getParent() == null ? "the system" : owner.getPath()));
			return;
		}
		IcCategory category = IcCategory.fromProfileName(model.classify(node.getType()));
		if (category == null)
			// An untyped "connect a to b": guess from what the ends are
			category = source.getContact() != null || source.getSplice() != null ? IcCategory.WIRE
					: source.getConnector() != null ? IcCategory.MATE : IcCategory.UNIT_LINK;

		final SysmlFeature f = model.resolve(node);
		connections.add(new IcConnection(category, node.getName(), owner, source, target,
				f.getAttribute("protocol"), f.getAttribute("signalName"), f.getAttribute("color"),
				node.getLocation()));
	}

	/**
	 * Walks a feature chain such as <code>A5.A3.J1</code> or
	 * <code>P1.c1</code> from an owner.
	 */
	static IcEnd resolveEnd(IcInstance owner, String chain) {
		IcInstance current = owner;
		IcConnector connector = null;
		IcContact contact = null;
		for (String segment : chain.split("\\.")) {
			if (contact != null)
				return null;
			if (connector != null) {
				contact = connector.getContact(segment);
				if (contact == null)
					return null;
				continue;
			}
			final IcInstance child = current.getChild(segment);
			if (child != null) {
				current = child;
				continue;
			}
			connector = current.getConnector(segment);
			if (connector != null)
				continue;
			if (current.getSplices().contains(segment))
				return new IcEnd(current, null, null, segment);
			return null;
		}
		if (current == owner && connector == null)
			return null;
		return new IcEnd(current, connector, contact, null);
	}

	public IcInstance getSystem() {
		return system;
	}

	/** <code>partNumber</code>, <code>revision</code>... of the system part. */
	public Map<String, String> getSystemAttributes() {
		return Collections.unmodifiableMap(systemAttributes);
	}

	public List<IcConnection> getConnections() {
		return Collections.unmodifiableList(connections);
	}

	public List<String> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	/** Every Unit, Cable and Assembly, depth first, in declaration order. */
	public List<IcInstance> getInstances() {
		final List<IcInstance> result = new ArrayList<>();
		collect(system, result);
		return result;
	}

	private static void collect(IcInstance instance, List<IcInstance> result) {
		for (IcInstance child : instance.getChildren()) {
			result.add(child);
			collect(child, result);
		}
	}

}
