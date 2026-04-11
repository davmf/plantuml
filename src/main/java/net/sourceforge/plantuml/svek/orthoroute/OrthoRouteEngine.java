package net.sourceforge.plantuml.svek.orthoroute;

import java.util.ArrayList;
import java.util.Collections;
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
	 * @param harnessSpines harness trunk rectangles as obstacles
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
				obstacles, portPoints, boardBounds, PORT_WIDTH);
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
				Log.error("OrthoRouteEngine: no path for "
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

			cd.resultWaypoints = waypoints;
		}

		return allOk;
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
