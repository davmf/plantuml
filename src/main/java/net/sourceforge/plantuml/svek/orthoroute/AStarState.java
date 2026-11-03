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
package net.sourceforge.plantuml.svek.orthoroute;

/**
 * A* search state: a visibility graph node plus the direction
 * of the last edge taken to reach it. This allows the cost
 * function to penalise direction changes (bends).
 */
public final class AStarState {

	private final VisNode node;
	private final boolean lastHorizontal;

	public AStarState(VisNode node, boolean lastHorizontal) {
		this.node = node;
		this.lastHorizontal = lastHorizontal;
	}

	public VisNode getNode() {
		return node;
	}

	public boolean isLastHorizontal() {
		return lastHorizontal;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AStarState == false)
			return false;
		final AStarState other = (AStarState) obj;
		return node.equals(other.node)
				&& lastHorizontal == other.lastHorizontal;
	}

	@Override
	public int hashCode() {
		return node.hashCode() * 31
				+ (lastHorizontal ? 1 : 0);
	}
}
