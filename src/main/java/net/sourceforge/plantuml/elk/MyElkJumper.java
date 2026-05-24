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
import net.sourceforge.plantuml.elk.proxy.graph.ElkPort;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.URectangle;

// Renders a jumper as a light-grey bar between two paired pins of a
// <<header>> component. The pins sit on opposing faces (WEST and EAST) of
// the same cluster, sharing a Y row. Jumpers are non-directional: any
// edge direction in the source PlantUML is ignored at render time.
class MyElkJumper {

	private static final double BAR_HEIGHT = 4.0;
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
		final double leftX = Math.min(c1.getX(), c2.getX());
		final double rightX = Math.max(c1.getX(), c2.getX());
		final double yMid = (c1.getY() + c2.getY()) / 2;

		// Bar spans pin-to-pin so it visually connects to both glyphs.
		// Thickness is small enough that inside-rendered pin labels are
		// still readable above and below the bar.
		final double barLeft = leftX + PORT_RADIUS;
		final double barRight = rightX - PORT_RADIUS;
		if (barRight <= barLeft)
			return;

		// Bar fill is light grey so pin number labels drawn over it stay
		// readable; stroke is a touch darker so the bar's outline reads
		// at a distance.
		final UGraphic ugBar = ug.apply(translate)
				.apply(HColors.GRAY).apply(HColors.LIGHT_GRAY.bg())
				.apply(UStroke.withThickness(1));
		final URectangle bar = URectangle.build(barRight - barLeft, BAR_HEIGHT);
		ugBar.apply(new UTranslate(barLeft, yMid - BAR_HEIGHT / 2)).draw(bar);
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
