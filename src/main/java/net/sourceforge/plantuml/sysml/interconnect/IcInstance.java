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
import java.util.List;

/**
 * A Unit, Cable or Assembly of an InterconnectSystem (or the system itself),
 * placed in the system tree.
 */
public final class IcInstance {

	private final IcInstance parent;
	private final String name;
	private final IcCategory category;
	private final String typeName;
	private final String designator;
	private final String partNumber;
	private final String doc;
	private final List<IcInstance> children = new ArrayList<>();
	private final List<IcConnector> connectors = new ArrayList<>();
	private final List<String> splices = new ArrayList<>();
	private final List<String> details = new ArrayList<>();
	private String alias;

	IcInstance(IcInstance parent, String name, IcCategory category, String typeName, String designator,
			String partNumber, String doc) {
		this.parent = parent;
		this.name = name;
		this.category = category;
		this.typeName = typeName;
		this.designator = designator;
		this.partNumber = partNumber;
		this.doc = doc;
		if (parent != null)
			parent.children.add(this);
	}

	public IcInstance getParent() {
		return parent;
	}

	/** The feature name in the owner (<code>A3</code> in <code>A5.A3</code>). */
	public String getName() {
		return name;
	}

	public IcCategory getCategory() {
		return category;
	}

	/** The simple name of the part definition (<code>MotorDriver</code>). */
	public String getTypeName() {
		return typeName;
	}

	/** The designator attribute, or the feature name when it has none. */
	public String getDesignator() {
		return designator;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public String getDoc() {
		return doc;
	}

	/** <code>A5.A3</code>: the feature chain from the system. */
	public String getPath() {
		if (parent == null || parent.parent == null)
			return name;
		return parent.getPath() + "." + name;
	}

	public List<IcInstance> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public IcInstance getChild(String childName) {
		for (IcInstance child : children)
			if (child.name.equals(childName))
				return child;
		return null;
	}

	public List<IcConnector> getConnectors() {
		return Collections.unmodifiableList(connectors);
	}

	public IcConnector getConnector(String connectorName) {
		for (IcConnector c : connectors)
			if (c.getName().equals(connectorName))
				return c;
		return null;
	}

	void addConnector(IcConnector connector) {
		connectors.add(connector);
	}

	/** Splice names of a Cable. */
	public List<String> getSplices() {
		return Collections.unmodifiableList(splices);
	}

	void addSplice(String splice) {
		splices.add(splice);
	}

	/**
	 * Category-specific attributes worth showing, already formatted (
	 * <code>24 V</code>, <code>1500 mm</code>, <code>shielded</code>).
	 */
	public List<String> getDetails() {
		return Collections.unmodifiableList(details);
	}

	void addDetail(String detail) {
		details.add(detail);
	}

	/** The PlantUML alias, set by the writer. */
	public String getAlias() {
		return alias;
	}

	void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return category + " " + getPath();
	}

}
