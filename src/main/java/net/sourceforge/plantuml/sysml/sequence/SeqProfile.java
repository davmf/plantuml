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
package net.sourceforge.plantuml.sysml.sequence;

import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlRootProfile;

/**
 * SequenceProfile: an <code>Interaction</code> drawn as a sequence diagram by
 * {@link SeqWriter}.
 */
public final class SeqProfile extends SysmlRootProfile {

	@Override
	public boolean isGraphLayout() {
		return false;
	}


	static final String INTERACTION = "Interaction";
	static final String LIFELINE = "Lifeline";
	static final String MESSAGE = "Message";
	static final String GATE_MESSAGE = "GateMessage";
	static final String GATE = "Gate";
	static final String FRAGMENT = "CombinedFragment";
	static final String OPERAND = "InteractionOperand";
	static final String EXECUTION = "ExecutionSpecification";
	static final String INVARIANT = "StateInvariant";
	static final String TIME = "TimeObservation";
	static final String DURATION = "DurationObservation";
	static final String PARTICIPANT_CLASS = "ParticipantClass";

	public SeqProfile() {
		super("sequence", INTERACTION, LIFELINE, MESSAGE, GATE_MESSAGE, GATE, FRAGMENT, OPERAND, EXECUTION,
				INVARIANT, TIME, DURATION, PARTICIPANT_CLASS, "InteractionElement");
		option("autonumber", "false", "true");
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) {
		final SeqWriter writer = new SeqWriter(model, root, options, isFunctionDefined);
		writer.run(source);
		return writer.getLines();
	}

}
