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
package net.sourceforge.plantuml.elk;

import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.EntityPosition;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.decoration.LinkDecor;
import net.sourceforge.plantuml.elk.proxy.graph.ElkPort;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.klimt.shape.URectangle;

// Renders a jumper as a filled black bar between two paired pins of a
// <<header>> component. The pins sit on opposing faces (WEST and EAST) of
// the same cluster, sharing a Y row. The bar spans the inner faces of the
// two pin glyphs.
//
// Direction:
//   p1 --> p2   arrow head at p2 end
//   p1 <-- p2   arrow head at p1 end
//   p1 --  p2   no arrow head (bidirectional / unmarked)
class MyElkJumper {

	private static final double BAR_HEIGHT = 4.0;
	private static final double ARROW_LEN = 6.0;
	private static final double ARROW_HALF_WIDTH = 4.0;
	private static final double PORT_RADIUS = EntityPosition.RADIUS;

	private final Link link;
	private final Map<Entity, ElkPort> ports;
	private final UTranslate translate;

	public MyElkJumper(Link link, Map<Entity, ElkPort> ports, UTranslate translate) {
		this.link = link;
		this.ports = ports;
		this.translate = translate;
	}

	public void drawU(UGraphic ug) {
		final ElkPort port1 = ports.get(link.getEntity1());
		final ElkPort port2 = ports.get(link.getEntity2());
		if (port1 == null || port2 == null)
			return;
		final XPoint2D c1 = portCenter(port1);
		final XPoint2D c2 = portCenter(port2);
		// Sort by X so left/right are unambiguous regardless of which
		// pin the parser put on entity1.
		final boolean swap = c1.getX() > c2.getX();
		final XPoint2D leftCenter = swap ? c2 : c1;
		final XPoint2D rightCenter = swap ? c1 : c2;
		// Direction relative to the unswapped link: forward = entity1 -> entity2.
		final LinkDecor headDecor = link.getType().getDecor1();
		final LinkDecor tailDecor = link.getType().getDecor2();
		final boolean forward = headDecor != LinkDecor.NONE;
		final boolean backward = tailDecor != LinkDecor.NONE;
		final boolean arrowRight = swap ? backward : forward;
		final boolean arrowLeft = swap ? forward : backward;

		final double yMid = (leftCenter.getY() + rightCenter.getY()) / 2;
		// Bar spans pin-to-pin so it visually connects to both glyphs.
		// Thickness is small enough that inside-rendered pin labels are
		// still readable above and below the bar.
		final double barLeft = leftCenter.getX() + PORT_RADIUS;
		final double barRight = rightCenter.getX() - PORT_RADIUS;
		if (barRight <= barLeft)
			return;

		final UGraphic ug2 = ug.apply(translate);
		final UGraphic ugBar = ug2.apply(HColors.BLACK).apply(HColors.BLACK.bg())
				.apply(UStroke.withThickness(1));

		// Bar segment: full width when neither end has an arrowhead,
		// shortened when an arrow is drawn at that end so the arrowhead
		// reads as a distinct shape rather than as a notched bar.
		double left = barLeft;
		double right = barRight;
		if (arrowLeft)
			left += ARROW_LEN;
		if (arrowRight)
			right -= ARROW_LEN;
		if (right > left) {
			final URectangle bar = URectangle.build(right - left, BAR_HEIGHT);
			ugBar.apply(new UTranslate(left, yMid - BAR_HEIGHT / 2)).draw(bar);
		}
		if (arrowRight)
			drawArrowhead(ugBar, right, yMid, +1);
		if (arrowLeft)
			drawArrowhead(ugBar, left, yMid, -1);
	}

	// Triangular arrowhead. dir=+1 points right, dir=-1 points left.
	// (tipX, yMid) is the base of the arrow (where it meets the bar).
	private void drawArrowhead(UGraphic ug, double tipX, double yMid, int dir) {
		final UPolygon arrow = new UPolygon();
		arrow.addPoint(tipX, yMid - ARROW_HALF_WIDTH);
		arrow.addPoint(tipX + dir * ARROW_LEN, yMid);
		arrow.addPoint(tipX, yMid + ARROW_HALF_WIDTH);
		ug.draw(arrow);
	}

	private XPoint2D portCenter(ElkPort port) {
		final XPoint2D abs = CucaDiagramFileMakerElk.getPosition(port);
		return new XPoint2D(abs.getX() + port.getWidth() / 2,
				abs.getY() + port.getHeight() / 2);
	}

	static void drawAll(UGraphic ug, List<Link> jumpers,
			Map<Entity, ElkPort> ports, UTranslate translate) {
		if (jumpers == null)
			return;
		for (Link link : jumpers)
			new MyElkJumper(link, ports, translate).drawU(ug);
	}
}
