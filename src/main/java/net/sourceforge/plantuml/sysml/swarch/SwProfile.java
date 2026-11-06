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
package net.sourceforge.plantuml.sysml.swarch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlFeature;
import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlRootProfile;

/**
 * SoftwareArchitectureProfile: anything holding Layers, drawn by
 * {@link SwWriter}. The profile has no root type: the samples hold their
 * Layers in a <code>part def</code>, so a root is any top-level part, or part
 * definition no such part is typed by, that has a Layer.
 */
public final class SwProfile extends SysmlRootProfile {

	static final String LAYER = "Layer";
	static final String COMPONENT = "LayerComponent";
	static final String SIGNAL_FLOW = "SignalFlow";
	static final String DEPENDENCY = "Dependency";

	public SwProfile() {
		super("swarch", LAYER, COMPONENT, SIGNAL_FLOW, DEPENDENCY, "SignalPort", "DependencyPort");
		option("order", "kind", "model");
	}

	@Override
	public String getRootDescription() {
		return "a part or part def with parts : Layer";
	}

	@Override
	public List<SysmlNode> findRoots(SysmlModel model) {
		final List<SysmlNode> result = new ArrayList<>();
		final Set<String> usedTypes = new HashSet<>();
		for (SysmlNode usage : model.getTopLevelUsages())
			if (usage.isKeyword("part") && hasLayer(model, usage)) {
				result.add(usage);
				if (usage.getType() != null)
					usedTypes.add(usage.getType());
			}
		for (SysmlNode def : model.getTopLevelDefinitions())
			if (def.isKeyword("part") && usedTypes.contains(def.getName()) == false
					&& usedTypes.contains(def.getQualifiedName()) == false && hasLayer(model, def))
				result.add(def);
		return result;
	}

	private static boolean hasLayer(SysmlModel model, SysmlNode node) {
		if (LAYER.equals(model.classify(node.getType())))
			return false;
		for (SysmlFeature f : model.resolve(node).getFeatures())
			if (LAYER.equals(model.classify(f)))
				return true;
		return false;
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) {
		final SwWriter writer = new SwWriter(model, root, options, isFunctionDefined);
		writer.run(source);
		return writer.getLines();
	}

}
