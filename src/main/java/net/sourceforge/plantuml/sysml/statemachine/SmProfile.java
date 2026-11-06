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
package net.sourceforge.plantuml.sysml.statemachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlException;
import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlNodes;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlProfile;

/**
 * StateMachineProfile: a <code>StateMachine</code> drawn as a state diagram by
 * {@link SmWriter}.
 */
public final class SmProfile implements SysmlProfile {

	static final String STATE_MACHINE = "StateMachine";
	static final String REGION = "Region";
	static final String STATE = "State";
	static final String PSEUDOSTATE = "Pseudostate";
	static final String COMPOSITE_STATE = "CompositeState";
	static final String SUBMACHINE_STATE = "SubmachineState";
	static final String TRANSITION = "Transition";

	public String getName() {
		return "statemachine";
	}

	public String getRootDescription() {
		return "part x : StateMachine";
	}

	public Collection<String> getStopNames() {
		return Arrays.asList(STATE_MACHINE, REGION, STATE, PSEUDOSTATE, COMPOSITE_STATE, SUBMACHINE_STATE,
				TRANSITION, "StateVertex", "Event", "Action", "Guard");
	}

	public Map<String, List<String>> getOptionKeys() {
		final Map<String, List<String>> result = new LinkedHashMap<>();
		result.put("actions", Arrays.asList("true", "false"));
		result.put("regions", Arrays.asList("horizontal", "vertical"));
		return result;
	}

	/**
	 * Every StateMachine, the ones no SubmachineState refers to first: a
	 * diagram that lists a machine and its sub-machines draws the machine.
	 */
	public List<SysmlNode> findRoots(SysmlModel model) {
		final List<SysmlNode> machines = new ArrayList<>();
		for (SysmlNode usage : model.getTopLevelUsages())
			if (STATE_MACHINE.equals(model.classify(usage.getType())))
				machines.add(usage);
		final Set<String> referenced = new HashSet<>();
		for (SysmlNode m : machines)
			collectSubmachines(model, m, referenced);
		final List<SysmlNode> result = new ArrayList<>();
		for (SysmlNode m : machines)
			if (referenced.contains(m.getName()) == false)
				result.add(m);
		for (SysmlNode m : machines)
			if (result.contains(m) == false)
				result.add(m);
		return result;
	}

	private static void collectSubmachines(SysmlModel model, SysmlNode node, Set<String> referenced) {
		for (SysmlNode child : node.getChildren()) {
			if (child.isDefinition())
				continue;
			if ("submachine".equals(child.getName()) && child.getValue() != null)
				referenced.add(SysmlNodes.lastSegment(child.getValue().getRaw()));
			collectSubmachines(model, child, referenced);
		}
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) throws SysmlException {
		final SmWriter writer = new SmWriter(model, root, options, isFunctionDefined);
		writer.run(source);
		return writer.getLines();
	}

}
