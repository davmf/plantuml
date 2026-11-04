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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.utils.Log;

/**
 * Facade that routes all port connectors using the orthogonal
 * visibility graph algorithm (Wybrow/Marriott/Stuckey, GD 2009).
 *
 * <p>Builds a shared visibility graph from all obstacles, then
 * uses A* search to find optimal paths for each connector.</p>
 */
public final class OrthoRouteEngine {

	private static final double PORT_RADIUS = 6.0;
	private static final double PORT_WIDTH = 2 * PORT_RADIUS;

	/**
	 * Route all connectors. Each connector must have had its stub
	 * geometry computed (Phase 1/1b) before calling this method.
	 *
	 * @param connectors    connectors with srcTipX/dstTipX set
	 * @param extraObstacles additional rectangles to route around
	 * @param boardBounds   board cluster boundary (nullable)
	 * @param bibliotekon   graph structure for cluster lookup
	 * @return true if all connectors were routed successfully
	 */
	public static boolean routeConnectors(
			List<ConnectorData> connectors,
			List<RectangleArea> obstacles,
			RectangleArea boardBounds) {

		if (connectors.isEmpty())
			return true;

		// Collect all port connection points (stub tips)
		final List<XPoint2D> portPoints = new ArrayList<XPoint2D>();
		for (ConnectorData cd : connectors) {
			portPoints.add(new XPoint2D(cd.srcTipX, cd.srcPortY));
			portPoints.add(new XPoint2D(cd.dstTipX, cd.dstPortY));
		}

		// Build the shared visibility graph
		final VisGraphBuilder builder = new VisGraphBuilder(
				clearTips(obstacles, portPoints), portPoints,
				boardBounds, PORT_WIDTH);
		final VisGraph graph = builder.build();

		// Route each connector via A*
		final AStarRouter router = new AStarRouter(graph);
		boolean allOk = true;

		for (ConnectorData cd : connectors) {
			final XPoint2D srcTip = new XPoint2D(
					cd.srcTipX, cd.srcPortY);
			final XPoint2D dstTip = new XPoint2D(
					cd.dstTipX, cd.dstPortY);

			final List<XPoint2D> path = router.findPath(
					srcTip, true, dstTip);

			if (path.isEmpty()) {
				Log.info(() -> "OrthoRouteEngine: no path for "
						+ cd.entity1Name + " -> "
						+ cd.entity2Name);
				allOk = false;
				continue;
			}

			// Build full waypoint list: stub + A* path + stub
			final List<XPoint2D> waypoints =
					new ArrayList<XPoint2D>();
			final double srcEdge = cd.srcPortX
					+ cd.srcDir * PORT_RADIUS;
			final double dstEdge = cd.dstPortX
					+ cd.dstDir * PORT_RADIUS;

			waypoints.add(new XPoint2D(srcEdge, cd.srcPortY));
			for (XPoint2D pt : path)
				waypoints.add(pt);
			waypoints.add(new XPoint2D(dstEdge, cd.dstPortY));

			cd.resultWaypoints = simplify(waypoints);
		}

		// Nudge overlapping parallel segments apart
		final List<List<XPoint2D>> allWaypoints =
				new ArrayList<List<XPoint2D>>();
		for (ConnectorData cd : connectors)
			allWaypoints.add(cd.resultWaypoints);
		NudgeEngine.nudge(allWaypoints, obstacles, boardBounds);

		return allOk;
	}

	/**
	 * The graph keeps PORT_WIDTH of clearance around every obstacle. Where
	 * components are packed closer than that, a stub tip falls inside a
	 * neighbour's clearance and could not be reached at all. Each obstacle
	 * is shrunk just enough for the tips to sit on or outside its clearance,
	 * so only the obstacles that crowd a tip lose any of it.
	 */
	static List<RectangleArea> clearTips(List<RectangleArea> obstacles,
			List<XPoint2D> tips) {
		final List<RectangleArea> result = new ArrayList<RectangleArea>();
		for (RectangleArea obs : obstacles) {
			double clearance = PORT_WIDTH;
			for (XPoint2D tip : tips)
				clearance = Math.min(clearance, distance(obs, tip));
			final double shrink = PORT_WIDTH - clearance;
			if (shrink > 0 && obs.getWidth() > 2 * shrink
					&& obs.getHeight() > 2 * shrink)
				result.add(new RectangleArea(obs.getMinX() + shrink,
						obs.getMinY() + shrink, obs.getMaxX() - shrink,
						obs.getMaxY() - shrink));
			else
				result.add(obs);
		}
		return result;
	}

	/** Chebyshev distance from a point to a rectangle (0 inside). */
	private static double distance(RectangleArea rect, XPoint2D pt) {
		final double dx = Math.max(0, Math.max(rect.getMinX() - pt.getX(),
				pt.getX() - rect.getMaxX()));
		final double dy = Math.max(0, Math.max(rect.getMinY() - pt.getY(),
				pt.getY() - rect.getMaxY()));
		return Math.max(dx, dy);
	}

	/**
	 * Drops repeated points and the middle point of any three collinear ones,
	 * so that consecutive segments always turn. This also removes the
	 * back-tracking where a path leaves a stub tip the way it came, and gives
	 * the nudging pass whole segments to move: two collinear pieces of one
	 * segment would otherwise be moved apart, leaving a diagonal between them.
	 * The first and last points (the port edges) are kept.
	 */
	static List<XPoint2D> simplify(List<XPoint2D> points) {
		final List<XPoint2D> result = new ArrayList<XPoint2D>();
		for (XPoint2D pt : points) {
			if (result.isEmpty() == false && same(result.get(result.size() - 1), pt))
				continue;
			while (result.size() >= 2 && collinear(result.get(result.size() - 2), result.get(result.size() - 1), pt))
				result.remove(result.size() - 1);
			result.add(pt);
		}
		return result;
	}

	private static final double EPSILON = 0.1;

	private static boolean same(XPoint2D a, XPoint2D b) {
		return Math.abs(a.getX() - b.getX()) < EPSILON && Math.abs(a.getY() - b.getY()) < EPSILON;
	}

	private static boolean collinear(XPoint2D a, XPoint2D b, XPoint2D c) {
		return (Math.abs(a.getX() - b.getX()) < EPSILON && Math.abs(b.getX() - c.getX()) < EPSILON)
				|| (Math.abs(a.getY() - b.getY()) < EPSILON && Math.abs(b.getY() - c.getY()) < EPSILON);
	}


	/**
	 * Data transfer object carrying connector geometry from
	 * SvekPortConnector into the routing engine.
	 */
	public static final class ConnectorData {
		public final net.sourceforge.plantuml.abel.Entity entity1;
		public final net.sourceforge.plantuml.abel.Entity entity2;
		public final net.sourceforge.plantuml.abel.Entity srcParent;
		public final String entity1Name;
		public final String entity2Name;
		public final double srcPortX;
		public final double srcPortY;
		public final double dstPortX;
		public final double dstPortY;
		public final double srcDir;
		public final double dstDir;
		public final double srcTipX;
		public final double dstTipX;

		public List<XPoint2D> resultWaypoints;

		public ConnectorData(
				net.sourceforge.plantuml.abel.Entity entity1,
				net.sourceforge.plantuml.abel.Entity entity2,
				double srcPortX, double srcPortY,
				double dstPortX, double dstPortY,
				double srcDir, double dstDir,
				double srcTipX, double dstTipX) {
			this.entity1 = entity1;
			this.entity2 = entity2;
			this.srcParent = entity1.getParentContainer();
			this.entity1Name = entity1.getName();
			this.entity2Name = entity2.getName();
			this.srcPortX = srcPortX;
			this.srcPortY = srcPortY;
			this.dstPortX = dstPortX;
			this.dstPortY = dstPortY;
			this.srcDir = srcDir;
			this.dstDir = dstDir;
			this.srcTipX = srcTipX;
			this.dstTipX = dstTipX;
		}
	}
}
