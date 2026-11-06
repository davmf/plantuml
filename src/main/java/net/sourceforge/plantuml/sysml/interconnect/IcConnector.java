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
 * A ConnectorPort of a Unit or Cable, with its contacts.
 */
public final class IcConnector {

	private final IcInstance owner;
	private final String name;
	private final String designator;
	private final String gender;
	private final String kind;
	private final String series;
	private final String partNumber;
	private final List<IcContact> contacts = new ArrayList<>();

	IcConnector(IcInstance owner, String name, String designator, String gender, String kind, String series,
			String partNumber) {
		this.owner = owner;
		this.name = name;
		this.designator = designator;
		this.gender = gender;
		this.kind = kind;
		this.series = series;
		this.partNumber = partNumber;
	}

	public IcInstance getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public String getDesignator() {
		return designator;
	}

	/** <code>male</code>, <code>female</code>... or <code>null</code>. */
	public String getGender() {
		return gender;
	}

	/** <code>wire_to_board</code>, <code>terminal</code>... or <code>null</code>. */
	public String getKind() {
		return kind;
	}

	public String getSeries() {
		return series;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public List<IcContact> getContacts() {
		return Collections.unmodifiableList(contacts);
	}

	public IcContact getContact(String contactName) {
		for (IcContact c : contacts)
			if (c.getName().equals(contactName))
				return c;
		return null;
	}

	public IcContact getContactById(String contactId) {
		for (IcContact c : contacts)
			if (c.getContactId().equals(contactId))
				return c;
		return null;
	}

	void addContact(IcContact contact) {
		contacts.add(contact);
	}

	/** <code>A5.A3.J1</code> */
	public String getPath() {
		return owner.getPath() + "." + name;
	}

	@Override
	public String toString() {
		return getPath();
	}

}
