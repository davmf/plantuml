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
package net.sourceforge.plantuml.sysml.interpcba;

import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlRootProfile;

/**
 * InterPCBAProfile: a <code>System</code> of SystemPCBAs and COTSDevices
 * joined by cabled Interconnects, drawn by {@link IpWriter}.
 */
public final class IpProfile extends SysmlRootProfile {

	static final String SYSTEM = "System";
	static final String SYSTEM_PCBA = "SystemPCBA";
	static final String COTS_DEVICE = "COTSDevice";
	static final String SYSTEM_NODE = "SystemNode";
	static final String INTERCONNECT = "Interconnect";
	static final String CONDUCTOR_PATH = "ConductorPath";
	static final String CABLE = "Cable";
	static final String CONDUCTOR = "Conductor";
	static final String EXTERNAL_CONNECTOR = "ExternalConnector";
	static final String CHANNEL = "Channel";
	static final String INTERNAL_BLOCK = "InternalBlock";

	static final String[] STOP_NAMES = { SYSTEM_PCBA, COTS_DEVICE, SYSTEM_NODE, INTERCONNECT, CONDUCTOR_PATH, CABLE,
			CONDUCTOR, "CableEnd", EXTERNAL_CONNECTOR, CHANNEL, INTERNAL_BLOCK, "InterBoardPort",
			"InterBoardSignalPort", "InterBoardPowerPort", "PCBA" };

	public IpProfile() {
		super("interpcba", SYSTEM, STOP_NAMES);
		option("detail", "conductor", "cable", "harness");
		option("channels", "true", "false");
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) {
		final IpWriter writer = new IpWriter(model, root, options, isFunctionDefined, false);
		writer.run(source);
		return writer.getLines();
	}

}
