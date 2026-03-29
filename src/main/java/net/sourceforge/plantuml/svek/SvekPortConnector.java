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

public class SvekPortConnector implements UDrawable {

	private static final double PORT_RADIUS = 6.0;
	private static final double OBSTACLE_MARGIN = 4.0;
	private static final double OBSTACLE_PADDING = 6.0;
	private static final int ARROW_WING = 9;
	private static final int ARROW_APERTURE = 4;
	private static final int ARROW_CONTACT = 5;

	private final SvekEdge edge;
	private final ISkinParam skinParam;
	private final Bibliotekon bibliotekon;
	private double verticalSegmentX = Double.NaN;

	public SvekPortConnector(SvekEdge edge, ISkinParam skinParam, Bibliotekon bibliotekon) {
		this.edge = edge;
		this.skinParam = skinParam;
		this.bibliotekon = bibliotekon;
	}

	public double getVerticalSegmentX() {
		return verticalSegmentX;
	}

	@Override
	public void drawU(UGraphic ug) {
		final XPoint2D startPt = resolveEntityCenter(edge.getLink().getEntity1());
		final XPoint2D endPt = resolveEntityCenter(edge.getLink().getEntity2());
		if (startPt == null || endPt == null)
			return;

		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.arrow)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor color = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
		final UGraphic ugLine = ug.apply(color).apply(HColors.none().bg()).apply(UStroke.simple());

		final double srcX = startPt.getX();
		final double srcY = startPt.getY();
		final double dstX = endPt.getX();
		final double dstY = endPt.getY();

		// Determine exit/entry direction from port type, not relative position
		final double dirSrc = portExitDir(edge.getLink().getEntity1(), dstX - srcX);
		final double dirDst = portEntryDir(edge.getLink().getEntity2(), dstX - srcX);
		final double srcEdge = srcX + dirSrc * PORT_RADIUS;
		final double dstEdge = dstX + dirDst * PORT_RADIUS;

		if (Math.abs(srcY - dstY) < 1.0 && dirSrc == -dirDst) {
			// Same Y, ports face each other — straight line
			drawLine(ugLine, srcEdge, srcY, dstEdge, dstY);
			drawHArrow(ugLine, dstEdge, dstY, -dirDst);
			verticalSegmentX = Double.NaN;
		} else if (dirSrc == -dirDst && Math.signum(dstX - srcX) == dirSrc) {
			// Ports face each other, source toward destination — 3-segment path
			final double naturalMidX = (srcEdge + dstEdge) / 2;
			final double yMin = Math.min(srcY, dstY);
			final double yMax = Math.max(srcY, dstY);
			final List<RectangleArea> obstacles = collectObstacles();
			final double midX = avoidObstacles(naturalMidX, yMin, yMax, obstacles, srcEdge, dstEdge);

			drawLine(ugLine, srcEdge, srcY, midX, srcY);
			drawLine(ugLine, midX, srcY, midX, dstY);
			drawLine(ugLine, midX, dstY, dstEdge, dstY);
			drawHArrow(ugLine, dstEdge, dstY, -dirDst);
			verticalSegmentX = midX;
		} else {
			// Ports face away from each other (or same direction) — 5-segment path
			// Route: src stub → vertical clear of src component → horizontal →
			//         vertical to dst Y → dst stub
			drawWrapAround(ugLine, srcX, srcY, dstX, dstY, dirSrc, dirDst);
		}
	}

	private double portExitDir(Entity entity, double fallbackDx) {
		final EntityPosition pos = entity.getEntityPosition();
		if (pos.isOutput())
			return 1.0;
		if (pos.isInput())
			return -1.0;
		return Math.signum(fallbackDx) != 0 ? Math.signum(fallbackDx) : 1.0;
	}

	private double portEntryDir(Entity entity, double fallbackDx) {
		final EntityPosition pos = entity.getEntityPosition();
		if (pos.isInput())
			return -1.0;
		if (pos.isOutput())
			return 1.0;
		return Math.signum(fallbackDx) != 0 ? -Math.signum(fallbackDx) : -1.0;
	}

	private void drawWrapAround(UGraphic ug, double srcX, double srcY,
			double dstX, double dstY, double dirSrc, double dirDst) {

		final double srcEdge = srcX + dirSrc * PORT_RADIUS;
		final double dstEdge = dstX + dirDst * PORT_RADIUS;

		// Find how far out verticals need to go to clear parent components
		final RectangleArea srcBounds = getParentClusterBounds(edge.getLink().getEntity1());
		final RectangleArea dstBounds = getParentClusterBounds(edge.getLink().getEntity2());

		final double stubLength = OBSTACLE_PADDING + PORT_RADIUS;
		double vert1X = srcEdge + dirSrc * stubLength;
		double vert2X = dstEdge + dirDst * stubLength;

		// Extend verticals to clear parent component boundaries
		if (srcBounds != null) {
			if (dirSrc > 0)
				vert1X = Math.max(vert1X, srcBounds.getMaxX() + OBSTACLE_PADDING);
			else
				vert1X = Math.min(vert1X, srcBounds.getMinX() - OBSTACLE_PADDING);
		}
		if (dstBounds != null) {
			if (dirDst > 0)
				vert2X = Math.max(vert2X, dstBounds.getMaxX() + OBSTACLE_PADDING);
			else
				vert2X = Math.min(vert2X, dstBounds.getMinX() - OBSTACLE_PADDING);
		}

		// Avoid obstacles on both vertical segments
		final double yMin = Math.min(srcY, dstY);
		final double yMax = Math.max(srcY, dstY);
		final List<RectangleArea> obstacles = collectObstacles();
		vert1X = avoidObstacles(vert1X, yMin, yMax, obstacles,
				dirSrc > 0 ? srcEdge : -Double.MAX_VALUE,
				dirSrc > 0 ? Double.MAX_VALUE : srcEdge);
		vert2X = avoidObstacles(vert2X, yMin, yMax, obstacles,
				dirDst > 0 ? dstEdge : -Double.MAX_VALUE,
				dirDst > 0 ? Double.MAX_VALUE : dstEdge);

		// Pick a horizontal Y for the crossover — use midpoint
		final double crossY = (srcY + dstY) / 2;

		// 5-segment path: src stub, vert1, horizontal, vert2, dst stub
		drawLine(ug, srcEdge, srcY, vert1X, srcY);
		drawLine(ug, vert1X, srcY, vert1X, crossY);
		drawLine(ug, vert1X, crossY, vert2X, crossY);
		drawLine(ug, vert2X, crossY, vert2X, dstY);
		drawLine(ug, vert2X, dstY, dstEdge, dstY);
		drawHArrow(ug, dstEdge, dstY, -dirDst);
		verticalSegmentX = vert1X;
	}

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

	private List<RectangleArea> collectObstacles() {
		final List<RectangleArea> obstacles = new ArrayList<RectangleArea>();
		if (bibliotekon == null)
			return obstacles;

		final Entity parent1 = edge.getLink().getEntity1().getParentContainer();
		final Entity parent2 = edge.getLink().getEntity2().getParentContainer();
		final Entity board = (parent1 != null) ? parent1.getParentContainer() : null;

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
		return obstacles;
	}

	static double avoidObstacles(double proposedX, double yMin, double yMax,
			List<RectangleArea> obstacles, double leftBound, double rightBound) {

		if (collides(proposedX, yMin, yMax, obstacles) == false)
			return proposedX;

		// Try shifting left and right to find clear positions
		for (double offset = OBSTACLE_MARGIN; offset < 2000; offset += OBSTACLE_MARGIN) {
			final double leftCandidate = proposedX - offset;
			if (collides(leftCandidate, yMin, yMax, obstacles) == false)
				return leftCandidate;

			final double rightCandidate = proposedX + offset;
			if (collides(rightCandidate, yMin, yMax, obstacles) == false)
				return rightCandidate;
		}
		return proposedX;
	}

	private static boolean collides(double x, double yMin, double yMax,
			List<RectangleArea> obstacles) {
		for (RectangleArea rect : obstacles) {
			if (x > rect.getMinX() - OBSTACLE_PADDING && x < rect.getMaxX() + OBSTACLE_PADDING
					&& yMax > rect.getMinY() - OBSTACLE_PADDING && yMin < rect.getMaxY() + OBSTACLE_PADDING)
				return true;
		}
		return false;
	}

	private void drawLine(UGraphic ug, double x1, double y1, double x2, double y2) {
		final double lineDx = x2 - x1;
		final double lineDy = y2 - y1;
		if (Math.abs(lineDx) < 0.5 && Math.abs(lineDy) < 0.5)
			return;
		ug.apply(new UTranslate(x1, y1)).draw(new ULine(lineDx, lineDy));
	}

	private void drawHArrow(UGraphic ug, double tipX, double tipY, double dir) {
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
