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
package net.sourceforge.plantuml.abel;

import net.sourceforge.plantuml.klimt.color.Colors;

// Scope marker pushed onto CucaDiagram.stacks when a `group "Name" { ... }`
// block opens inside a component. Ports created while a PortGroup is on the
// stack record (groupOrder, intraGroupOrder) so the layout backend can keep
// them together and impose a defined vertical order on the parent face.
public class PortGroup implements Bag {

	private final String label;
	// 0-based ordinal among all port groups declared in the SAME parent
	// component, in declaration order.
	private final int groupOrder;
	// Running count of ports added to this group so far.
	private int portCount;
	// Optional colour applied to every port in the group (its glyph fill
	// and its label text). Set via `group "Name" #Color { ... }`.
	private Colors colors;

	public PortGroup(String label, int groupOrder) {
		this.label = label;
		this.groupOrder = groupOrder;
	}

	public String getLabel() {
		return label;
	}

	public int getGroupOrder() {
		return groupOrder;
	}

	public int nextPortIndex() {
		return portCount++;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public Colors getColors() {
		return colors;
	}
}
