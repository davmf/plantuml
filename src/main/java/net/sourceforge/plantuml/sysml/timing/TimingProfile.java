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
package net.sourceforge.plantuml.sysml.timing;

import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlRootProfile;

/**
 * TimingProfile: a <code>TimingDiagram</code> drawn as a timing diagram by
 * {@link TimingWriter}.
 * <p>
 * Besides its own types, the profile classifies the StateMachineProfile
 * states a state lifeline names, so that the states of a referenced machine
 * can be listed.
 */
public final class TimingProfile extends SysmlRootProfile {

	@Override
	public boolean isGraphLayout() {
		return false;
	}


	static final String DIAGRAM = "TimingDiagram";
	static final String LIFELINE = "Lifeline";
	static final String CHANGE = "StateChange";
	static final String CONSTRAINT = "TimingConstraint";
	static final String MESSAGE = "TimingMessage";
	static final String HIGHLIGHT = "TimeHighlight";
	static final String STATE = "State";
	static final String COMPOSITE_STATE = "CompositeState";
	static final String SUBMACHINE_STATE = "SubmachineState";

	static final String UNIT = "unit";
	static final String STATES = "states";
	static final String COMPACT = "compact";

	public TimingProfile() {
		super("timing", DIAGRAM, LIFELINE, CHANGE, CONSTRAINT, MESSAGE, HIGHLIGHT, "TimingElement", STATE,
				COMPOSITE_STATE, SUBMACHINE_STATE, "Pseudostate", "StateVertex", "StateMachine", "Region");
		option(UNIT, "model", "ns", "us", "ms", "s");
		option(STATES, "used", "all");
		option(COMPACT, "false", "true");
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) {
		final TimingWriter writer = new TimingWriter(model, root, options, isFunctionDefined);
		writer.run(source);
		return writer.getLines();
	}

}
