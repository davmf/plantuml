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
package net.sourceforge.plantuml.sysml.activity;

import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlRootProfile;

/**
 * ActivityProfile: an <code>Activity</code> drawn by {@link ActWriter}.
 */
public final class ActProfile extends SysmlRootProfile {

	static final String ACTIVITY = "Activity";
	static final String ACTION = "Action";
	static final String OBJECT_NODE = "ObjectNode";
	static final String PARTITION = "Partition";
	static final String EXPANSION_REGION = "ExpansionRegion";
	static final String INTERRUPTIBLE_REGION = "InterruptibleRegion";
	static final String EDGE = "ActivityEdge";
	static final String PIN = "Pin";

	public ActProfile() {
		super("activity", ACTIVITY, ACTION, OBJECT_NODE, PARTITION, EXPANSION_REGION, INTERRUPTIBLE_REGION, EDGE, PIN,
				"ActivityNode");
		option("direction", "top", "left");
	}

	public List<String> generate(SysmlModel model, SysmlNode root, SysmlOptions options, String source,
			Predicate<String> isFunctionDefined) {
		final ActWriter writer = new ActWriter(model, root, options, isFunctionDefined);
		writer.run(source);
		return writer.getLines();
	}

}
