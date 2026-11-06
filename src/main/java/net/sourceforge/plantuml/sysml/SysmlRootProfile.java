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
package net.sourceforge.plantuml.sysml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A profile whose diagram roots are the top-level usages of one type, such as
 * <code>part x : Interaction</code>.
 */
public abstract class SysmlRootProfile implements SysmlProfile {

	private final String name;
	private final String rootType;
	private final List<String> stopNames;
	private final Map<String, List<String>> optionKeys = new LinkedHashMap<>();

	protected SysmlRootProfile(String name, String rootType, String... stopNames) {
		this.name = name;
		this.rootType = rootType;
		final List<String> names = new ArrayList<>(Arrays.asList(stopNames));
		if (names.contains(rootType) == false)
			names.add(rootType);
		this.stopNames = Collections.unmodifiableList(names);
	}

	/** Declares an option, its default value first; no values means any value. */
	protected final void option(String key, String... values) {
		optionKeys.put(key, values.length == 0 ? null : Arrays.asList(values));
	}

	public final String getName() {
		return name;
	}

	public final String getRootType() {
		return rootType;
	}

	public String getRootDescription() {
		return "part x : " + rootType;
	}

	public final Collection<String> getStopNames() {
		return stopNames;
	}

	public final Map<String, List<String>> getOptionKeys() {
		return Collections.unmodifiableMap(optionKeys);
	}

	public List<SysmlNode> findRoots(SysmlModel model) {
		final List<SysmlNode> result = new ArrayList<>();
		for (SysmlNode usage : model.getTopLevelUsages())
			if (rootType.equals(model.classify(usage.getType())))
				result.add(usage);
		return result;
	}

}
