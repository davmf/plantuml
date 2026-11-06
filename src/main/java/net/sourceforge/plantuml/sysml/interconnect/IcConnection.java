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
 * A Mate, Wire, NoMate, InterfaceLink or UnitLink with resolved ends.
 */
public final class IcConnection {

	private final IcCategory category;
	private final String name;
	private final IcInstance owner;
	private final IcEnd source;
	private final IcEnd target;
	private final String protocol;
	private final String signalName;
	private final String color;
	private final String location;

	IcConnection(IcCategory category, String name, IcInstance owner, IcEnd source, IcEnd target, String protocol,
			String signalName, String color, String location) {
		this.category = category;
		this.name = name;
		this.owner = owner;
		this.source = source;
		this.target = target;
		this.protocol = protocol;
		this.signalName = signalName;
		this.color = color;
		this.location = location;
	}

	public IcCategory getCategory() {
		return category;
	}

	/** The connection name, or <code>null</code> when anonymous. */
	public String getName() {
		return name;
	}

	/** The system, Assembly or Cable that owns the connection. */
	public IcInstance getOwner() {
		return owner;
	}

	public IcEnd getSource() {
		return source;
	}

	public IcEnd getTarget() {
		return target;
	}

	/** InterfaceLink / UnitLink protocol (<code>can</code>, <code>power</code>...). */
	public String getProtocol() {
		return protocol;
	}

	/** Wire signal name. */
	public String getSignalName() {
		return signalName;
	}

	/** Wire colour, as written in the model (<code>red</code>). */
	public String getColor() {
		return color;
	}

	/** <code>file:line</code> of the declaration. */
	public String getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return category + " " + source + " -> " + target;
	}

}
