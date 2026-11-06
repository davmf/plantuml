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
 * COTSDeviceProfile on its own: one COTSDevice (a usage, or a catalogue
 * <code>part def</code> when the files hold no usage) drawn as a black box
 * with its connectors, channels and internal blocks. A COTSDevice inside a
 * System is drawn by {@link IpProfile}.
 */
public final class CotsProfile extends SysmlRootProfile {

	public CotsProfile() {
		super("cots", IpProfile.COTS_DEVICE, IpProfile.STOP_NAMES);
		option("channels", "true", "false");
	}

	@Override
	public String getRootDescription() {
		return "part x : COTSDevice, or part def X :> COTSDevice";
	}

	@Override
	public List<SysmlNode> findRoots(SysmlModel model) {
		final List<SysmlNode> result = super.findRoots(model);
		for (SysmlNode def : model.getTopLevelDefinitions())
			if (def.isKeyword("part") && IpProfile.COTS_DEVICE.equals(model.classify(def.getQualifiedName()))
					&& def.getName().equals(IpProfile.COTS_DEVICE) == false)
				result.add(def);
		return result;
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) {
		final IpWriter writer = new IpWriter(model, root, options, isFunctionDefined, true);
		writer.run(source);
		return writer.getLines();
	}

}
