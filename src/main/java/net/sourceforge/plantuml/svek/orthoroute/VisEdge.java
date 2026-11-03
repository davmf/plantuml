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

public final class VisEdge {

	private final VisNode from;
	private final VisNode to;
	private final boolean horizontal;
	private final double length;

	public VisEdge(VisNode from, VisNode to) {
		this.from = from;
		this.to = to;
		final double dx = Math.abs(to.getX() - from.getX());
		final double dy = Math.abs(to.getY() - from.getY());
		if (dx > 0.5 && dy > 0.5)
			throw new IllegalArgumentException(
					"VisEdge must be orthogonal: " + from + " -> " + to);
		this.horizontal = dy < 0.5;
		this.length = horizontal ? dx : dy;
	}

	public VisNode getFrom() {
		return from;
	}

	public VisNode getTo() {
		return to;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public double getLength() {
		return length;
	}

	public VisNode getOther(VisNode node) {
		if (node.equals(from))
			return to;
		if (node.equals(to))
			return from;
		throw new IllegalArgumentException(
				"Node not part of this edge: " + node);
	}

	@Override
	public String toString() {
		return "VisEdge(" + from + " -> " + to
				+ (horizontal ? " H" : " V")
				+ " len=" + length + ")";
	}
}
