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

import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public final class VisNode {

	private final XPoint2D point;
	private final VisNodeKind kind;

	public VisNode(XPoint2D point, VisNodeKind kind) {
		this.point = point;
		this.kind = kind;
	}

	public VisNode(double x, double y, VisNodeKind kind) {
		this(new XPoint2D(x, y), kind);
	}

	public XPoint2D getPoint() {
		return point;
	}

	public double getX() {
		return point.getX();
	}

	public double getY() {
		return point.getY();
	}

	public VisNodeKind getKind() {
		return kind;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof VisNode == false)
			return false;
		final VisNode other = (VisNode) obj;
		return Double.compare(point.getX(), other.point.getX()) == 0
				&& Double.compare(point.getY(), other.point.getY()) == 0;
	}

	@Override
	public int hashCode() {
		final long xBits = Double.doubleToLongBits(point.getX());
		final long yBits = Double.doubleToLongBits(point.getY());
		return (int) (xBits ^ (xBits >>> 32)
				^ yBits ^ (yBits >>> 32));
	}

	@Override
	public String toString() {
		return "VisNode(" + point.getX() + ", " + point.getY()
				+ ", " + kind + ")";
	}
}
