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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlException;
import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlProfile;

/**
 * InterconnectProfile: an <code>InterconnectSystem</code> drawn as a component
 * diagram by {@link InterconnectPlantUml}.
 */
public final class IcProfile implements SysmlProfile {

	public String getName() {
		return "interconnect";
	}

	public String getRootDescription() {
		return "part x : InterconnectSystem";
	}

	public Collection<String> getStopNames() {
		return InterconnectModel.profileNames();
	}

	public Map<String, List<String>> getOptionKeys() {
		final Map<String, List<String>> result = new LinkedHashMap<>();
		result.put("view", Arrays.asList("physical", "logical", "both"));
		result.put("detail", Arrays.asList("connector", "contact"));
		result.put("cables", Arrays.asList("node", "edge"));
		return result;
	}

	public List<SysmlNode> findRoots(SysmlModel model) {
		final List<SysmlNode> result = new ArrayList<>();
		for (SysmlNode usage : model.getTopLevelUsages())
			if (usage.isKeyword("part")
					&& IcCategory.fromProfileName(model.classify(usage.getType())) == IcCategory.SYSTEM)
				result.add(usage);
		return result;
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) throws SysmlException {
		final Map<String, String> values = new LinkedHashMap<>(options.asMap());
		values.remove(SysmlOptions.ROOT);
		values.remove(SysmlOptions.PROFILE);
		values.put(SysmlOptions.SYSTEM, root.getQualifiedName());
		final IcOptions parsed = IcOptions.parse(values);
		final InterconnectModel interconnect = InterconnectModel.build(model, parsed.getSystem());
		return InterconnectPlantUml.generate(interconnect, parsed, source, isFunctionDefined);
	}

}
