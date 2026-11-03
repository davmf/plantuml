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

import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public final class SweepEvent implements Comparable<SweepEvent> {

	private final double coord;
	private final SweepEventKind kind;
	private final RectangleArea obstacle;
	private final XPoint2D point;

	private SweepEvent(double coord, SweepEventKind kind,
			RectangleArea obstacle, XPoint2D point) {
		this.coord = coord;
		this.kind = kind;
		this.obstacle = obstacle;
		this.point = point;
	}

	public static SweepEvent open(double coord, RectangleArea obstacle) {
		return new SweepEvent(coord, SweepEventKind.OPEN, obstacle, null);
	}

	public static SweepEvent close(double coord, RectangleArea obstacle) {
		return new SweepEvent(coord, SweepEventKind.CLOSE, obstacle, null);
	}

	public static SweepEvent point(double coord, XPoint2D point) {
		return new SweepEvent(coord, SweepEventKind.POINT, null, point);
	}

	public double getCoord() {
		return coord;
	}

	public SweepEventKind getKind() {
		return kind;
	}

	public RectangleArea getObstacle() {
		return obstacle;
	}

	public XPoint2D getPoint() {
		return point;
	}

	@Override
	public int compareTo(SweepEvent other) {
		final int coordCmp = Double.compare(coord, other.coord);
		if (coordCmp != 0)
			return coordCmp;
		return kind.compareTo(other.kind);
	}
}
