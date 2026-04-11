package net.sourceforge.plantuml.svek.orthoroute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

/**
 * Nudges apart parallel connector segments that share the same
 * corridor, and centers them within the available space between
 * obstacles. Based on Wybrow/Marriott/Stuckey, GD 2009, Section 4.
 */
public final class NudgeEngine {

	private static final double PORT_WIDTH = 12.0;
	private static final double MIN_SPACING = PORT_WIDTH;

	/**
	 * @param allWaypoints list of waypoint lists (one per connector)
	 * @param obstacles    all obstacle rectangles
	 */
	public static void nudge(List<List<XPoint2D>> allWaypoints,
			List<RectangleArea> obstacles,
			RectangleArea boardBounds) {
		final List<SegRef> verticals = new ArrayList<SegRef>();
		final List<SegRef> horizontals = new ArrayList<SegRef>();

		for (int ci = 0; ci < allWaypoints.size(); ci++) {
			final List<XPoint2D> wp = allWaypoints.get(ci);
			if (wp == null || wp.size() < 2)
				continue;
			final int lastSeg = wp.size() - 2;
			for (int si = 0; si <= lastSeg; si++) {
				// Skip stubs (first and last segments)
				if (si == 0 || si == lastSeg)
					continue;
				final XPoint2D a = wp.get(si);
				final XPoint2D b = wp.get(si + 1);
				final double dx = Math.abs(a.getX() - b.getX());
				final double dy = Math.abs(a.getY() - b.getY());
				if (dx < 0.5 && dy > 1)
					verticals.add(new SegRef(ci, si, wp));
				else if (dy < 0.5 && dx > 1)
					horizontals.add(new SegRef(ci, si, wp));
			}
		}

		nudgeVerticals(verticals, obstacles, boardBounds);
		nudgeHorizontals(horizontals, obstacles, boardBounds);
	}

	private static void nudgeVerticals(List<SegRef> segments,
			List<RectangleArea> obstacles,
			RectangleArea boardBounds) {
		Collections.sort(segments, new Comparator<SegRef>() {
			public int compare(SegRef a, SegRef b) {
				return Double.compare(a.fixedCoord(),
						b.fixedCoord());
			}
		});

		int i = 0;
		while (i < segments.size()) {
			final List<SegRef> cluster = new ArrayList<SegRef>();
			cluster.add(segments.get(i));
			int j = i + 1;
			while (j < segments.size()) {
				final SegRef candidate = segments.get(j);
				boolean close = false;
				for (SegRef member : cluster) {
					if (Math.abs(candidate.fixedCoord()
							- member.fixedCoord()) < MIN_SPACING
							&& rangeOverlaps(candidate, member)) {
						close = true;
						break;
					}
				}
				if (close) {
					cluster.add(candidate);
					j++;
				} else {
					break;
				}
			}
			if (cluster.size() > 1)
				distributeVerticals(cluster, obstacles,
						boardBounds);
			i = j;
		}
	}

	private static void nudgeHorizontals(List<SegRef> segments,
			List<RectangleArea> obstacles,
			RectangleArea boardBounds) {
		Collections.sort(segments, new Comparator<SegRef>() {
			public int compare(SegRef a, SegRef b) {
				return Double.compare(a.fixedCoord(),
						b.fixedCoord());
			}
		});

		int i = 0;
		while (i < segments.size()) {
			final List<SegRef> cluster = new ArrayList<SegRef>();
			cluster.add(segments.get(i));
			int j = i + 1;
			while (j < segments.size()) {
				final SegRef candidate = segments.get(j);
				boolean close = false;
				for (SegRef member : cluster) {
					if (Math.abs(candidate.fixedCoord()
							- member.fixedCoord()) < MIN_SPACING
							&& rangeOverlaps(candidate, member)) {
						close = true;
						break;
					}
				}
				if (close) {
					cluster.add(candidate);
					j++;
				} else {
					break;
				}
			}
			if (cluster.size() > 1)
				distributeHorizontals(cluster, obstacles,
						boardBounds);
			i = j;
		}
	}

	private static void distributeVerticals(List<SegRef> cluster,
			List<RectangleArea> obstacles,
			RectangleArea boardBounds) {
		// Find corridor width: the space between nearest obstacles
		// on each side of this cluster
		double sumX = 0;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for (SegRef s : cluster) {
			sumX += s.fixedCoord();
			minY = Math.min(minY, s.rangeMin());
			maxY = Math.max(maxY, s.rangeMax());
		}
		final double centreX = sumX / cluster.size();

		// Find nearest obstacle boundaries on each side
		double corridorLeft = boardBounds != null
				? boardBounds.getMinX() : centreX - 500;
		double corridorRight = boardBounds != null
				? boardBounds.getMaxX() : centreX + 500;
		for (RectangleArea obs : obstacles) {
			if (obs.getMaxY() <= minY || obs.getMinY() >= maxY)
				continue;
			if (obs.getMaxX() <= centreX
					&& obs.getMaxX() > corridorLeft)
				corridorLeft = obs.getMaxX();
			if (obs.getMinX() >= centreX
					&& obs.getMinX() < corridorRight)
				corridorRight = obs.getMinX();
		}

		final int n = cluster.size();
		final double span = (n - 1) * MIN_SPACING;
		final double corridorCentre =
				(corridorLeft + corridorRight) / 2;
		final double startX = corridorCentre - span / 2;

		// Sort cluster by current X to maintain relative ordering
		Collections.sort(cluster, new Comparator<SegRef>() {
			public int compare(SegRef a, SegRef b) {
				return Double.compare(a.fixedCoord(),
						b.fixedCoord());
			}
		});

		for (int k = 0; k < n; k++) {
			final double newX = startX + k * MIN_SPACING;
			shiftVertical(cluster.get(k), newX);
		}
	}

	private static void distributeHorizontals(List<SegRef> cluster,
			List<RectangleArea> obstacles,
			RectangleArea boardBounds) {
		double sumY = 0;
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		for (SegRef s : cluster) {
			sumY += s.fixedCoord();
			minX = Math.min(minX, s.rangeMin());
			maxX = Math.max(maxX, s.rangeMax());
		}
		final double centreY = sumY / cluster.size();

		double corridorTop = boardBounds != null
				? boardBounds.getMinY() : centreY - 500;
		double corridorBottom = boardBounds != null
				? boardBounds.getMaxY() : centreY + 500;
		for (RectangleArea obs : obstacles) {
			if (obs.getMaxX() <= minX || obs.getMinX() >= maxX)
				continue;
			if (obs.getMaxY() <= centreY
					&& obs.getMaxY() > corridorTop)
				corridorTop = obs.getMaxY();
			if (obs.getMinY() >= centreY
					&& obs.getMinY() < corridorBottom)
				corridorBottom = obs.getMinY();
		}

		final int n = cluster.size();
		final double span = (n - 1) * MIN_SPACING;
		final double corridorCentre =
				(corridorTop + corridorBottom) / 2;
		final double startY = corridorCentre - span / 2;

		Collections.sort(cluster, new Comparator<SegRef>() {
			public int compare(SegRef a, SegRef b) {
				return Double.compare(a.fixedCoord(),
						b.fixedCoord());
			}
		});

		for (int k = 0; k < n; k++) {
			final double newY = startY + k * MIN_SPACING;
			shiftHorizontal(cluster.get(k), newY);
		}
	}

	private static boolean rangeOverlaps(SegRef a, SegRef b) {
		return a.rangeMax() > b.rangeMin()
				&& b.rangeMax() > a.rangeMin();
	}

	private static void shiftVertical(SegRef seg, double newX) {
		final List<XPoint2D> wp = seg.waypoints;
		final int ia = seg.segIndex;
		final int ib = seg.segIndex + 1;
		if (ia > 0)
			wp.set(ia, new XPoint2D(newX, wp.get(ia).getY()));
		if (ib < wp.size() - 1)
			wp.set(ib, new XPoint2D(newX, wp.get(ib).getY()));
	}

	private static void shiftHorizontal(SegRef seg, double newY) {
		final List<XPoint2D> wp = seg.waypoints;
		final int ia = seg.segIndex;
		final int ib = seg.segIndex + 1;
		if (ia > 0)
			wp.set(ia, new XPoint2D(wp.get(ia).getX(), newY));
		if (ib < wp.size() - 1)
			wp.set(ib, new XPoint2D(wp.get(ib).getX(), newY));
	}

	private static final class SegRef {
		final int connIndex;
		final int segIndex;
		final List<XPoint2D> waypoints;

		SegRef(int connIndex, int segIndex,
				List<XPoint2D> waypoints) {
			this.connIndex = connIndex;
			this.segIndex = segIndex;
			this.waypoints = waypoints;
		}

		XPoint2D a() { return waypoints.get(segIndex); }
		XPoint2D b() { return waypoints.get(segIndex + 1); }

		boolean isVertical() {
			return Math.abs(a().getX() - b().getX()) < 0.5;
		}

		double fixedCoord() {
			return isVertical() ? a().getX() : a().getY();
		}

		double rangeMin() {
			return isVertical()
					? Math.min(a().getY(), b().getY())
					: Math.min(a().getX(), b().getX());
		}

		double rangeMax() {
			return isVertical()
					? Math.max(a().getY(), b().getY())
					: Math.max(a().getX(), b().getX());
		}
	}
}
