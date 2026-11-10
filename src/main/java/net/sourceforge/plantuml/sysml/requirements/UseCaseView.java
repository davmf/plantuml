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
 * <code>view=usecases</code>: plain SysML v2 use cases drawn as a use case
 * diagram, with their actors, the subject they belong to as a boundary, the
 * use cases they include, and their objective with the requirements it
 * holds.
 * <p>
 * It lives next to the requirements view because it draws requirements the
 * same way and reads them from the same {@link ReqModel}.
 * <p>
 * The roots are the packages, so <code>root=</code> names one; without it the
 * whole file set is drawn. There is no root when the files hold no use case.
 */
public final class UseCaseView implements SysmlProfile {

	static final String REQUIREMENTS = "requirements";
	static final String TEXT = "text";
	static final String ROLES = "roles";
	static final String DIRECTION = "direction";

	static final String USE_CASE = "use case";

	private final Map<String, List<String>> optionKeys = new LinkedHashMap<>();

	public UseCaseView() {
		optionKeys.put(REQUIREMENTS, Arrays.asList("all", "linked", "none"));
		optionKeys.put(TEXT, Arrays.asList("true", "false"));
		optionKeys.put(ROLES, Arrays.asList("false", "true"));
		optionKeys.put(DIRECTION, Arrays.asList("left", "top"));
	}

	public String getName() {
		return "usecases";
	}

	public String getRootDescription() {
		return "a package holding use cases, or the whole file set";
	}

	public Collection<String> getStopNames() {
		return Collections.emptyList();
	}

	public Map<String, List<String>> getOptionKeys() {
		return Collections.unmodifiableMap(optionKeys);
	}

	/** The packages, as for the requirements view; none without a use case. */
	public List<SysmlNode> findRoots(SysmlModel model) {
		for (SysmlNode root : model.getRoots())
			if (ReqView.holds(root, Collections.singleton(USE_CASE)))
				return ReqView.packages(model);
		return Collections.emptyList();
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) throws SysmlException {
		final UseCaseWriter writer = new UseCaseWriter(new ReqModel(model), options.getRoot() == null ? null : root,
				options, isFunctionDefined);
		writer.run(source);
		return writer.getLines();
	}

}
