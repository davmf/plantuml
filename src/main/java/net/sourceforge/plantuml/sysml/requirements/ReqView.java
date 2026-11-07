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
package net.sourceforge.plantuml.sysml.requirements;

import java.util.ArrayList;
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
 * <code>view=requirements</code>: plain SysML v2 requirements and the
 * relations that trace them (containment, satisfy, verify, derivation,
 * refinement, dependency, allocation, subject and type references), drawn as
 * a class diagram around one focused element or for a whole package.
 * <p>
 * The roots are the packages, so <code>root=</code> names one; without it the
 * whole file set is drawn.
 */
public final class ReqView implements SysmlProfile {

	static final String FOCUS = "focus";
	static final String DEPTH = "depth";
	static final String DIRECTION = "direction";
	static final String TEXT = "text";
	static final String PACKAGES = "packages";
	static final String LAYOUT = "layout";

	private final Map<String, List<String>> optionKeys = new LinkedHashMap<>();

	public ReqView() {
		optionKeys.put(FOCUS, null);
		optionKeys.put(DEPTH, null);
		optionKeys.put(DIRECTION, list("both", "up", "down"));
		optionKeys.put(TEXT, list("true", "false"));
		optionKeys.put(PACKAGES, list("false", "true"));
		optionKeys.put(LAYOUT, list("elk", "default"));
	}

	private static List<String> list(String... values) {
		final List<String> result = new ArrayList<>();
		Collections.addAll(result, values);
		return Collections.unmodifiableList(result);
	}

	public String getName() {
		return "requirements";
	}

	public String getRootDescription() {
		return "a package, or the whole file set";
	}

	public Collection<String> getStopNames() {
		return Collections.emptyList();
	}

	public Map<String, List<String>> getOptionKeys() {
		return Collections.unmodifiableMap(optionKeys);
	}

	/**
	 * Every package outside the libraries, in file order; the file itself
	 * when a file has no package.
	 */
	public List<SysmlNode> findRoots(SysmlModel model) {
		final List<SysmlNode> result = new ArrayList<>();
		for (SysmlNode root : model.getRoots()) {
			final int before = result.size();
			collectPackages(root, result);
			if (result.size() == before && root.getChildren().isEmpty() == false)
				result.add(root);
		}
		return result;
	}

	private static void collectPackages(SysmlNode node, List<SysmlNode> result) {
		for (SysmlNode child : node.getChildren())
			if (child.isKeyword("package") && child.isLibrary() == false && child.getName() != null) {
				result.add(child);
				collectPackages(child, result);
			}
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) throws SysmlException {
		final ReqWriter writer = new ReqWriter(new ReqModel(model), options.getRoot() == null ? null : root, options,
				isFunctionDefined);
		writer.run(source);
		return writer.getLines();
	}

}
