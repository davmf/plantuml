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
package net.sourceforge.plantuml.sysml.definitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
 * The <code>view=definitions</code> view: the definitions of any SysML v2
 * file set (<code>part def</code>, <code>port def</code>,
 * <code>enum def</code>...) drawn as a class diagram by {@link DefWriter}.
 * <p>
 * The first root is the whole file set; the others are every package and
 * named definition, so that <code>root=</code> can restrict the diagram to one
 * of them.
 */
public final class DefProfile implements SysmlProfile {

	public String getName() {
		return "definitions";
	}

	public String getRootDescription() {
		return "a package or a definition";
	}

	public Collection<String> getStopNames() {
		return Collections.emptyList();
	}

	public Map<String, List<String>> getOptionKeys() {
		final Map<String, List<String>> result = new LinkedHashMap<>();
		result.put("attributes", Arrays.asList("true", "false"));
		result.put("usages", Arrays.asList("true", "false"));
		result.put("packages", Arrays.asList("true", "false"));
		result.put("libraries", Arrays.asList("false", "true"));
		result.put("doc", Arrays.asList("none", "tooltip", "note"));
		result.put("direction", Arrays.asList("top", "left"));
		return result;
	}

	/**
	 * The whole file set first (the root node of the first file, which has no
	 * name), then every package and named definition in file order; nothing
	 * when the files hold no definition.
	 */
	public List<SysmlNode> findRoots(SysmlModel model) {
		final List<SysmlNode> result = new ArrayList<>();
		for (SysmlNode file : model.getRoots())
			collect(file, result);
		boolean hasDefinition = false;
		for (SysmlNode node : result)
			if (node.isDefinition())
				hasDefinition = true;
		if (hasDefinition == false)
			return Collections.emptyList();
		result.add(0, model.getRoots().get(0));
		return result;
	}

	private static void collect(SysmlNode owner, List<SysmlNode> result) {
		for (SysmlNode child : owner.getChildren()) {
			if (child.getName() == null)
				continue;
			if (child.isKeyword("package") || child.isDefinition()) {
				result.add(child);
				collect(child, result);
			}
		}
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) throws SysmlException {
		final DefWriter writer = new DefWriter(model, root, options, isFunctionDefined);
		writer.run(source);
		return writer.getLines();
	}

}
