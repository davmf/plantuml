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

/**
 * A ContactPort (pin, socket, terminal) of a connector.
 */
public final class IcContact {

	private final IcConnector connector;
	private final String name;
	private final String contactId;
	private final String kind;
	private final String signalName;

	IcContact(IcConnector connector, String name, String contactId, String kind, String signalName) {
		this.connector = connector;
		this.name = name;
		this.contactId = contactId;
		this.kind = kind;
		this.signalName = signalName;
	}

	public IcConnector getConnector() {
		return connector;
	}

	public String getName() {
		return name;
	}

	/** The number printed on the housing; the feature name when unset. */
	public String getContactId() {
		return contactId;
	}

	/** <code>signal</code>, <code>power</code>, <code>ground</code>... or <code>null</code>. */
	public String getKind() {
		return kind;
	}

	public String getSignalName() {
		return signalName;
	}

	@Override
	public String toString() {
		return connector.getPath() + "." + name;
	}

}
