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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * One profile <code>!include_sysml</code> can draw: how to recognise its
 * diagram roots and how to write one of them as PlantUML.
 */
public interface SysmlProfile {

	/** The value of <code>profile=</code> that selects it, e.g. <code>statemachine</code>. */
	String getName();

	/** What a root looks like, for messages: <code>part x : StateMachine</code>. */
	String getRootDescription();

	/**
	 * The simple names of the library types the profile classifies by; the
	 * profile library itself need not be loaded.
	 */
	Collection<String> getStopNames();

	/**
	 * The options the profile adds to {@link SysmlOptions#COMMON}, with their
	 * allowed values, the default first (<code>null</code>: any value).
	 */
	Map<String, List<String>> getOptionKeys();

	/**
	 * Whether the diagram is laid out as a graph (by Graphviz or ELK), so that
	 * <code>layout=</code> applies. Sequence and timing diagrams are not.
	 */
	default boolean isGraphLayout() {
		return true;
	}

	/** The roots found in the model, in file order (best guess first). */
	List<SysmlNode> findRoots(SysmlModel model);

	/**
	 * Whether the model holds something for this generator to draw. A view
	 * that has roots in every file set (its roots are the packages) tells here
	 * whether it is worth falling back to when no profile has a root.
	 */
	default boolean isDrawable(SysmlModel model) {
		return findRoots(model).isEmpty() == false;
	}

	/**
	 * @param model             a model built with {@link #getStopNames()}
	 * @param root              one of {@link #findRoots}
	 * @param source            the files read, for the leading comment
	 * @param isFunctionDefined tells which label functions the diagram already
	 *                          defines
	 */
	List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) throws SysmlException;

}
