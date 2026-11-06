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
 * One end of a connection: a whole instance (UnitLink), a connector (Mate,
 * NoMate, InterfaceLink), a contact (Wire) or a Cable's splice node (Wire).
 */
public final class IcEnd {

	private final IcInstance instance;
	private final IcConnector connector;
	private final IcContact contact;
	private final String splice;

	IcEnd(IcInstance instance, IcConnector connector, IcContact contact, String splice) {
		this.instance = instance;
		this.connector = connector;
		this.contact = contact;
		this.splice = splice;
	}

	public IcInstance getInstance() {
		return instance;
	}

	/** May be <code>null</code> for a UnitLink end or a splice. */
	public IcConnector getConnector() {
		return connector;
	}

	public IcContact getContact() {
		return contact;
	}

	/** The splice name when the end is a splice node. */
	public String getSplice() {
		return splice;
	}

	@Override
	public String toString() {
		if (splice != null)
			return instance.getPath() + "." + splice;
		if (contact != null)
			return contact.toString();
		if (connector != null)
			return connector.toString();
		return instance.getPath();
	}

}
