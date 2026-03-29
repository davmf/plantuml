package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.EntityPosition;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.utils.Log;

public class SvekPortConnector implements UDrawable {

	private static final double PORT_RADIUS = 6.0;
	private static final double PADDING = 6.0;
	private static final double SCAN_STEP = 4.0;
	private static final double SCAN_LIMIT = 2000.0;
	private static final int ARROW_WING = 9;
	private static final int ARROW_APERTURE = 4;
	private static final int ARROW_CONTACT = 5;

	private final SvekEdge edge;
	private final ISkinParam skinParam;
	private final Bibliotekon bibliotekon;
	private List<XPoint2D> waypoints;

	public SvekPortConnector(SvekEdge edge, ISkinParam skinParam, Bibliotekon bibliotekon) {
		this.edge = edge;
		this.skinParam = skinParam;
		this.bibliotekon = bibliotekon;
	}

	// ------------------------------------------------------------------
	// Pre-pass: compute all paths before drawing
	// ------------------------------------------------------------------

	public static void resolveAllPaths(List<SvekPortConnector> connectors,
			List<RectangleArea> harnessSpines) {

		for (SvekPortConnector pc : connectors)
			pc.computePath(harnessSpines);
	}

	private void computePath(List<RectangleArea> harnessSpines) {
		final XPoint2D startPt = resolveEntityCenter(edge.getLink().getEntity1());
		final XPoint2D endPt = resolveEntityCenter(edge.getLink().getEntity2());
		if (startPt == null || endPt == null)
			return;

		final double srcX = startPt.getX();
		final double srcY = startPt.getY();
		final double dstX = endPt.getX();
		final double dstY = endPt.getY();

		final double dirSrc = portExitDir(edge.getLink().getEntity1(), dstX - srcX);
		final double dirDst = portEntryDir(edge.getLink().getEntity2(), dstX - srcX);
		final double srcEdge = srcX + dirSrc * PORT_RADIUS;
		final double dstEdge = dstX + dirDst * PORT_RADIUS;

		final List<RectangleArea> obstacles = collectObstacles(harnessSpines);

		if (Math.abs(srcY - dstY) < 1.0 && dirSrc == -dirDst
				&& Math.signum(dstX - srcX) == dirSrc) {
			// Same Y, ports face each other, source toward destination
			waypoints = new ArrayList<XPoint2D>();
			waypoints.add(new XPoint2D(srcEdge, srcY));
			waypoints.add(new XPoint2D(dstEdge, dstY));
		} else if (dirSrc == -dirDst && Math.signum(dstX - srcX) == dirSrc) {
			// Ports face each other, source toward destination — try 3-segment
			waypoints = try3Segment(srcEdge, srcY, dstEdge, dstY, obstacles);
			if (waypoints == null)
				waypoints = build5Segment(srcEdge, srcY, dstEdge, dstY,
						dirSrc, dirDst, obstacles);
		} else {
			// Ports face away or same direction — 5-segment
			waypoints = build5Segment(srcEdge, srcY, dstEdge, dstY,
					dirSrc, dirDst, obstacles);
		}

		validatePath(waypoints, obstacles);
	}

	// ------------------------------------------------------------------
	// 3-segment path: horizontal → vertical → horizontal
	// ------------------------------------------------------------------

	private List<XPoint2D> try3Segment(double srcEdge, double srcY,
			double dstEdge, double dstY, List<RectangleArea> obstacles) {

		final double yMin = Math.min(srcY, dstY);
		final double yMax = Math.max(srcY, dstY);
		final double naturalMidX = (srcEdge + dstEdge) / 2;

		// Scan outward from the natural midpoint for a clear vertical channel
		// that also has clear horizontal stubs
		final double midX = findClear3SegmentX(naturalMidX, srcEdge, srcY,
				dstEdge, dstY, yMin, yMax, obstacles);
		if (Double.isNaN(midX))
			return null;

		final List<XPoint2D> path = new ArrayList<XPoint2D>();
		path.add(new XPoint2D(srcEdge, srcY));
		path.add(new XPoint2D(midX, srcY));
		path.add(new XPoint2D(midX, dstY));
		path.add(new XPoint2D(dstEdge, dstY));
		return path;
	}

	private static double findClear3SegmentX(double naturalX,
			double srcEdge, double srcY, double dstEdge, double dstY,
			double yMin, double yMax, List<RectangleArea> obstacles) {

		final double lo = Math.min(srcEdge, dstEdge);
		final double hi = Math.max(srcEdge, dstEdge);

		for (double offset = 0; offset < SCAN_LIMIT; offset += SCAN_STEP) {
			final double x1 = naturalX + offset;
			if (x1 >= lo && x1 <= hi && is3SegmentClear(x1, srcEdge, srcY,
					dstEdge, dstY, yMin, yMax, obstacles))
				return x1;

			if (offset == 0)
				continue;

			final double x2 = naturalX - offset;
			if (x2 >= lo && x2 <= hi && is3SegmentClear(x2, srcEdge, srcY,
					dstEdge, dstY, yMin, yMax, obstacles))
				return x2;
		}
		return Double.NaN;
	}

	private static boolean is3SegmentClear(double midX,
			double srcEdge, double srcY, double dstEdge, double dstY,
			double yMin, double yMax, List<RectangleArea> obstacles) {
		// Check vertical segment
		if (verticalCollides(midX, yMin, yMax, obstacles))
			return false;
		// Check both horizontal stubs
		if (horizontalCollides(srcY, Math.min(srcEdge, midX),
				Math.max(srcEdge, midX), obstacles))
			return false;
		if (horizontalCollides(dstY, Math.min(dstEdge, midX),
				Math.max(dstEdge, midX), obstacles))
			return false;
		return true;
	}

	// ------------------------------------------------------------------
	// 5-segment path: stub → vertical → horizontal → vertical → stub
	// ------------------------------------------------------------------

	private List<XPoint2D> build5Segment(double srcEdge, double srcY,
			double dstEdge, double dstY, double dirSrc, double dirDst,
			List<RectangleArea> obstacles) {

		// Start verticals just past parent component boundaries
		final RectangleArea srcBounds = getParentClusterBounds(
				edge.getLink().getEntity1());
		final RectangleArea dstBounds = getParentClusterBounds(
				edge.getLink().getEntity2());

		double vert1X = initialVerticalX(srcEdge, dirSrc, srcBounds);
		double vert2X = initialVerticalX(dstEdge, dirDst, dstBounds);

		// Push each vertical outward until its horizontal stub is clear
		vert1X = findClearStubX(vert1X, srcEdge, srcY, dirSrc, obstacles);
		vert2X = findClearStubX(vert2X, dstEdge, dstY, dirDst, obstacles);

		// Initial vertical clearance check over src/dst Y span
		final double yMin = Math.min(srcY, dstY);
		final double yMax = Math.max(srcY, dstY);
		vert1X = findClearVertical(vert1X, yMin, yMax, dirSrc, obstacles);
		vert2X = findClearVertical(vert2X, yMin, yMax, dirDst, obstacles);

		// Find clear horizontal crossover Y between the two verticals
		final double xMin = Math.min(vert1X, vert2X);
		final double xMax = Math.max(vert1X, vert2X);
		final double naturalCrossY = (srcY + dstY) / 2;
		double crossY = findClearHorizontal(naturalCrossY, xMin, xMax, obstacles);

		// Re-check verticals over their actual Y spans (which include crossY)
		final double vert1YMin = Math.min(srcY, crossY);
		final double vert1YMax = Math.max(srcY, crossY);
		final double vert2YMin = Math.min(dstY, crossY);
		final double vert2YMax = Math.max(dstY, crossY);
		vert1X = findClearVertical(vert1X, vert1YMin, vert1YMax, dirSrc, obstacles);
		vert2X = findClearVertical(vert2X, vert2YMin, vert2YMax, dirDst, obstacles);

		// If verticals moved, re-check stubs and crossover
		vert1X = findClearStubX(vert1X, srcEdge, srcY, dirSrc, obstacles);
		vert2X = findClearStubX(vert2X, dstEdge, dstY, dirDst, obstacles);
		final double xMin2 = Math.min(vert1X, vert2X);
		final double xMax2 = Math.max(vert1X, vert2X);
		crossY = findClearHorizontal(crossY, xMin2, xMax2, obstacles);

		// If both verticals ended up at the same X (same-direction ports),
		// collapse to a 3-segment U-shape
		if (Math.abs(vert1X - vert2X) < 1.0) {
			final List<XPoint2D> path = new ArrayList<XPoint2D>();
			path.add(new XPoint2D(srcEdge, srcY));
			path.add(new XPoint2D(vert1X, srcY));
			path.add(new XPoint2D(vert1X, dstY));
			path.add(new XPoint2D(dstEdge, dstY));
			return path;
		}

		final List<XPoint2D> path = new ArrayList<XPoint2D>();
		path.add(new XPoint2D(srcEdge, srcY));
		path.add(new XPoint2D(vert1X, srcY));
		path.add(new XPoint2D(vert1X, crossY));
		path.add(new XPoint2D(vert2X, crossY));
		path.add(new XPoint2D(vert2X, dstY));
		path.add(new XPoint2D(dstEdge, dstY));
		return path;
	}

	private static double initialVerticalX(double portEdge, double dir,
			RectangleArea parentBounds) {
		double x = portEdge + dir * (PADDING + PORT_RADIUS);
		if (parentBounds != null) {
			if (dir > 0)
				x = Math.max(x, parentBounds.getMaxX() + PADDING);
			else
				x = Math.min(x, parentBounds.getMinX() - PADDING);
		}
		return x;
	}

	/**
	 * Push vertX outward (in dir) until the horizontal stub from portEdge
	 * to vertX at stubY is clear of all obstacles.
	 */
	private static double findClearStubX(double vertX, double portEdge,
			double stubY, double dir, List<RectangleArea> obstacles) {
		for (double candidate = vertX;
				Math.abs(candidate - portEdge) < SCAN_LIMIT;
				candidate += dir * SCAN_STEP) {
			final double xMin = Math.min(candidate, portEdge);
			final double xMax = Math.max(candidate, portEdge);
			if (horizontalCollides(stubY, xMin, xMax, obstacles) == false)
				return candidate;
		}
		return vertX;
	}

	/**
	 * Push vertX outward (in dir) until the vertical span yMin..yMax
	 * is clear of all obstacles.
	 */
	private static double findClearVertical(double vertX, double yMin,
			double yMax, double dir, List<RectangleArea> obstacles) {
		for (double candidate = vertX;
				Math.abs(candidate - vertX) < SCAN_LIMIT;
				candidate += dir * SCAN_STEP) {
			if (verticalCollides(candidate, yMin, yMax, obstacles) == false)
				return candidate;
		}
		return vertX;
	}

	/**
	 * Scan outward from proposedY to find a Y where a horizontal segment
	 * spanning xMin..xMax is clear of all obstacles.
	 */
	private static double findClearHorizontal(double proposedY, double xMin,
			double xMax, List<RectangleArea> obstacles) {
		for (double offset = 0; offset < SCAN_LIMIT; offset += SCAN_STEP) {
			final double y1 = proposedY + offset;
			if (horizontalCollides(y1, xMin, xMax, obstacles) == false)
				return y1;
			if (offset == 0)
				continue;
			final double y2 = proposedY - offset;
			if (horizontalCollides(y2, xMin, xMax, obstacles) == false)
				return y2;
		}
		return proposedY;
	}

	// ------------------------------------------------------------------
	// Collision detection
	// ------------------------------------------------------------------

	/**
	 * Scan outward from proposedX (both directions) to find a clear vertical
	 * position. Used by SvekHarness for spine avoidance.
	 */
	static double findClearVerticalBidirectional(double proposedX,
			double yMin, double yMax, List<RectangleArea> obstacles) {
		for (double offset = 0; offset < SCAN_LIMIT; offset += SCAN_STEP) {
			if (verticalCollides(proposedX + offset, yMin, yMax,
					obstacles) == false)
				return proposedX + offset;
			if (offset > 0 && verticalCollides(proposedX - offset, yMin, yMax,
					obstacles) == false)
				return proposedX - offset;
		}
		return proposedX;
	}

	static boolean verticalCollides(double x, double yMin, double yMax,
			List<RectangleArea> obstacles) {
		for (RectangleArea rect : obstacles) {
			if (x > rect.getMinX() - PADDING && x < rect.getMaxX() + PADDING
					&& yMax > rect.getMinY() - PADDING
					&& yMin < rect.getMaxY() + PADDING)
				return true;
		}
		return false;
	}

	static boolean horizontalCollides(double y, double xMin, double xMax,
			List<RectangleArea> obstacles) {
		for (RectangleArea rect : obstacles) {
			if (y > rect.getMinY() - PADDING && y < rect.getMaxY() + PADDING
					&& xMax > rect.getMinX() - PADDING
					&& xMin < rect.getMaxX() + PADDING)
				return true;
		}
		return false;
	}

	private static boolean segmentCollides(double x1, double y1,
			double x2, double y2, List<RectangleArea> obstacles) {
		if (Math.abs(x1 - x2) < 0.5)
			return verticalCollides((x1 + x2) / 2,
					Math.min(y1, y2), Math.max(y1, y2), obstacles);
		if (Math.abs(y1 - y2) < 0.5)
			return horizontalCollides((y1 + y2) / 2,
					Math.min(x1, x2), Math.max(x1, x2), obstacles);
		return false;
	}

	// ------------------------------------------------------------------
	// Validation
	// ------------------------------------------------------------------

	private void validatePath(List<XPoint2D> path,
			List<RectangleArea> obstacles) {
		if (path == null || path.size() < 2)
			return;
		for (int i = 0; i < path.size() - 1; i++) {
			final XPoint2D a = path.get(i);
			final XPoint2D b = path.get(i + 1);
			if (segmentCollides(a.getX(), a.getY(), b.getX(), b.getY(),
					obstacles)) {
				final String ent1 = edge.getLink().getEntity1().getName();
				final String ent2 = edge.getLink().getEntity2().getName();
				Log.error("Port connector " + ent1 + "->" + ent2
						+ " segment " + i + " collides: ("
						+ a.getX() + "," + a.getY() + ")->(" + b.getX()
						+ "," + b.getY() + ")");
				for (RectangleArea r : obstacles)
					if (segmentCollides(a.getX(), a.getY(), b.getX(),
							b.getY(),
							java.util.Collections.singletonList(r)))
						Log.error("  hit obstacle: x=["
								+ r.getMinX() + ".." + r.getMaxX()
								+ "] y=[" + r.getMinY() + ".."
								+ r.getMaxY() + "]");
			}
		}
	}

	// ------------------------------------------------------------------
	// Port direction helpers
	// ------------------------------------------------------------------

	private static double portExitDir(Entity entity, double fallbackDx) {
		final EntityPosition pos = entity.getEntityPosition();
		if (pos.isOutput())
			return 1.0;
		if (pos.isInput())
			return -1.0;
		return Math.signum(fallbackDx) != 0 ? Math.signum(fallbackDx) : 1.0;
	}

	private static double portEntryDir(Entity entity, double fallbackDx) {
		final EntityPosition pos = entity.getEntityPosition();
		if (pos.isInput())
			return -1.0;
		if (pos.isOutput())
			return 1.0;
		return Math.signum(fallbackDx) != 0 ? -Math.signum(fallbackDx) : -1.0;
	}

	// ------------------------------------------------------------------
	// Obstacle collection
	// ------------------------------------------------------------------

	private RectangleArea getParentClusterBounds(Entity entity) {
		if (bibliotekon == null)
			return null;
		final Entity parent = entity.getParentContainer();
		if (parent == null)
			return null;
		final Cluster cl = bibliotekon.getCluster(parent);
		if (cl == null)
			return null;
		return cl.getRectangleArea();
	}

	private List<RectangleArea> collectObstacles(
			List<RectangleArea> harnessSpines) {
		final List<RectangleArea> obstacles = new ArrayList<RectangleArea>();
		if (bibliotekon == null)
			return obstacles;

		final Entity parent1 = edge.getLink().getEntity1().getParentContainer();
		final Entity parent2 = edge.getLink().getEntity2().getParentContainer();
		final Entity board = (parent1 != null)
				? parent1.getParentContainer() : null;

		for (Cluster cl : bibliotekon.allCluster()) {
			final RectangleArea rect = cl.getRectangleArea();
			if (rect == null)
				continue;
			if (cl.getGroup() == board)
				continue;
			if (cl.getGroup() == parent1)
				continue;
			if (cl.getGroup() == parent2)
				continue;
			obstacles.add(rect);
		}

		for (RectangleArea spine : harnessSpines) {
			if (spine != null)
				obstacles.add(spine);
		}

		return obstacles;
	}

	// ------------------------------------------------------------------
	// Drawing
	// ------------------------------------------------------------------

	@Override
	public void drawU(UGraphic ug) {
		if (waypoints == null || waypoints.size() < 2)
			return;

		final Style style = StyleSignatureBasic.of(SName.root, SName.element,
				SName.arrow).getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor color = style.value(PName.LineColor)
				.asColor(skinParam.getIHtmlColorSet());
		final UGraphic ugLine = ug.apply(color).apply(HColors.none().bg())
				.apply(UStroke.simple());

		for (int i = 0; i < waypoints.size() - 1; i++)
			drawLine(ugLine, waypoints.get(i).getX(), waypoints.get(i).getY(),
					waypoints.get(i + 1).getX(),
					waypoints.get(i + 1).getY());

		// Arrow at destination
		final XPoint2D last = waypoints.get(waypoints.size() - 1);
		final XPoint2D prev = waypoints.get(waypoints.size() - 2);
		final double arrowDir = Math.signum(last.getX() - prev.getX());
		if (Math.abs(arrowDir) > 0.5)
			drawHArrow(ugLine, last.getX(), last.getY(), arrowDir);
	}

	private void drawLine(UGraphic ug, double x1, double y1,
			double x2, double y2) {
		final double dx = x2 - x1;
		final double dy = y2 - y1;
		if (Math.abs(dx) < 0.5 && Math.abs(dy) < 0.5)
			return;
		ug.apply(new UTranslate(x1, y1)).draw(new ULine(dx, dy));
	}

	private void drawHArrow(UGraphic ug, double tipX, double tipY,
			double dir) {
		final HColor color = ug.getParam().getColor();
		if (color != null)
			ug = ug.apply(color.bg());
		final UPolygon polygon = new UPolygon();
		polygon.addPoint(0, 0);
		polygon.addPoint(-dir * ARROW_WING, -ARROW_APERTURE);
		polygon.addPoint(-dir * ARROW_CONTACT, 0);
		polygon.addPoint(-dir * ARROW_WING, ARROW_APERTURE);
		polygon.addPoint(0, 0);
		ug.apply(new UTranslate(tipX, tipY)).draw(polygon);
	}

	private XPoint2D resolveEntityCenter(Entity entity) {
		if (bibliotekon == null)
			return null;
		final SvekNode node = bibliotekon.getNode(entity);
		if (node == null)
			return null;
		final double cx = node.getMinX() + node.getSize().getWidth() / 2;
		final double cy = node.getMinY() + node.getSize().getHeight() / 2;
		return new XPoint2D(cx, cy);
	}

}
