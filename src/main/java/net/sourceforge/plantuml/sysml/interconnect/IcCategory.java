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
 * What an InterconnectProfile type is, found by following a part
 * definition's specialization chain up to the profile.
 */
public enum IcCategory {

	PCBA_UNIT("PCBAUnit"), MODULE("Module"), SENSOR("Sensor"), ACTUATOR("Actuator"), MOTOR("Motor"),
	SOLENOID("Solenoid"), POWER_SOURCE("PowerSource"), USER_INTERFACE_DEVICE("UserInterfaceDevice"),
	DEVICE("Device"), UNIT("Unit"), ASSEMBLY("Assembly"), CABLE("Cable"), SPLICE("Splice"),
	SYSTEM("InterconnectSystem"), CONNECTOR_PORT("ConnectorPort"), CONTACT_PORT("ContactPort"), MATE("Mate"),
	WIRE("Wire"), NO_MATE("NoMate"), INTERFACE_LINK("InterfaceLink"), UNIT_LINK("UnitLink");

	private final String profileName;

	IcCategory(String profileName) {
		this.profileName = profileName;
	}

	/** The type name in InterconnectProfile, also used as the stereotype. */
	public String getProfileName() {
		return profileName;
	}

	public boolean isUnit() {
		switch (this) {
		case PCBA_UNIT:
		case MODULE:
		case SENSOR:
		case ACTUATOR:
		case MOTOR:
		case SOLENOID:
		case POWER_SOURCE:
		case USER_INTERFACE_DEVICE:
		case DEVICE:
		case UNIT:
			return true;
		default:
			return false;
		}
	}

	public static IcCategory fromProfileName(String name) {
		if (name == null)
			return null;
		for (IcCategory c : values())
			if (c.profileName.equals(name))
				return c;
		return null;
	}

}
