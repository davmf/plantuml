package net.sourceforge.plantuml.svek.orthoroute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

/**
 * Constructs an orthogonal visibility graph from a set of obstacle
 * rectangles and port connection points using two sweep-line passes
 * (Wybrow/Marriott/Stuckey, GD 2009).
 *
 * <p>The vertical-edge pass sweeps left to right, generating vertical
 * visibility segments. The horizontal-edge pass sweeps top to bottom,
 * generating horizontal visibility segments.</p>
 */
public final class VisGraphBuilder {

	private final List<RectangleArea> obstacles;
	private final List<XPoint2D> portConnections;
	private final double margin;
	private final double boardMinX;
	private final double boardMinY;
	private final double boardMaxX;
	private final double boardMaxY;

	public VisGraphBuilder(List<RectangleArea> obstacles,
			List<XPoint2D> portConnections,
			RectangleArea boardBounds, double margin) {
		this.obstacles = obstacles;
		this.portConnections = portConnections;
		this.margin = margin;
		if (boardBounds != null) {
			this.boardMinX = boardBounds.getMinX();
			this.boardMinY = boardBounds.getMinY();
			this.boardMaxX = boardBounds.getMaxX();
			this.boardMaxY = boardBounds.getMaxY();
		} else {
			this.boardMinX = Double.NEGATIVE_INFINITY;
			this.boardMinY = Double.NEGATIVE_INFINITY;
			this.boardMaxX = Double.POSITIVE_INFINITY;
			this.boardMaxY = Double.POSITIVE_INFINITY;
		}
	}

	public VisGraph build() {
		final VisGraph graph = new VisGraph();
		sweepVerticalEdges(graph);
		sweepHorizontalEdges(graph);
		return graph;
	}

	/**
	 * Left-to-right sweep: generates vertical visibility segments.
	 * At each event X, for each interesting Y, find the maximal
	 * unblocked vertical range and add edges between consecutive
	 * nodes along that range.
	 */
	private void sweepVerticalEdges(VisGraph graph) {
		final List<SweepEvent> events = new ArrayList<SweepEvent>();
		for (RectangleArea obs : obstacles) {
			final double minX = obs.getMinX() - margin;
			final double maxX = obs.getMaxX() + margin;
			events.add(SweepEvent.open(minX, obs));
			events.add(SweepEvent.close(maxX, obs));
		}
		for (XPoint2D pt : portConnections)
			events.add(SweepEvent.point(pt.getX(), pt));

		Collections.sort(events);

		final ActiveIntervalSet active = new ActiveIntervalSet();

		int i = 0;
		while (i < events.size()) {
			final double eventX = events.get(i).getCoord();
			final TreeSet<Double> interestingYs = new TreeSet<Double>();

			// Process all events at this X coordinate
			while (i < events.size()
					&& Math.abs(events.get(i).getCoord() - eventX)
							< 0.1) {
				final SweepEvent ev = events.get(i);
				switch (ev.getKind()) {
				case CLOSE:
					active.remove(
							ev.getObstacle().getMinY() - margin,
							ev.getObstacle().getMaxY() + margin);
					// Add corner Ys for the closing obstacle
					interestingYs.add(
							ev.getObstacle().getMinY() - margin);
					interestingYs.add(
							ev.getObstacle().getMaxY() + margin);
					break;
				case POINT:
					interestingYs.add(ev.getPoint().getY());
					break;
				case OPEN:
					// Defer — process interesting Ys before opening
					break;
				}
				i++;
			}

			// Collect corner Ys from obstacles being opened
			int j = i - 1;
			while (j >= 0 && Math.abs(
					events.get(j).getCoord() - eventX) < 0.1) {
				final SweepEvent ev = events.get(j);
				if (ev.getKind() == SweepEventKind.OPEN) {
					interestingYs.add(
							ev.getObstacle().getMinY() - margin);
					interestingYs.add(
							ev.getObstacle().getMaxY() + margin);
				}
				j--;
			}

			// Also add Ys from currently active obstacle boundaries
			// (these create segments in corridors between obstacles)
			for (RectangleArea obs : obstacles) {
				final double obsMinX = obs.getMinX() - margin;
				final double obsMaxX = obs.getMaxX() + margin;
				if (obsMinX < eventX && obsMaxX > eventX) {
					interestingYs.add(obs.getMinY() - margin);
					interestingYs.add(obs.getMaxY() + margin);
				}
			}

			// For each interesting Y, find vertical visibility range
			// and add edges between consecutive visible nodes
			for (Double iy : interestingYs) {
				if (active.isBlocked(iy))
					continue;
				final double[] range = active.findVisibleRange(iy);
				final double lo = Math.max(range[0], boardMinY);
				final double hi = Math.min(range[1], boardMaxY);
				if (lo >= hi)
					continue;

				// Collect all interesting Ys within this range
				final List<Double> segmentYs =
						new ArrayList<Double>();
				for (Double sy : interestingYs.subSet(lo, true,
						hi, true)) {
					if (active.isBlocked(sy) == false)
						segmentYs.add(sy);
				}
				// Also add port Ys within range at this X
				for (XPoint2D pt : portConnections) {
					if (Math.abs(pt.getX() - eventX) < 0.1
							&& pt.getY() >= lo && pt.getY() <= hi
							&& active.isBlocked(pt.getY()) == false)
						segmentYs.add(pt.getY());
				}
				Collections.sort(segmentYs);

				// Add edges between consecutive nodes
				for (int k = 0; k < segmentYs.size() - 1; k++) {
					final double y1 = segmentYs.get(k);
					final double y2 = segmentYs.get(k + 1);
					if (Math.abs(y2 - y1) < 0.1)
						continue;
					final VisNode n1 = graph.addNode(eventX, y1,
							kindFor(eventX, y1));
					final VisNode n2 = graph.addNode(eventX, y2,
							kindFor(eventX, y2));
					graph.addEdge(n1, n2);
				}
			}

			// Now open deferred obstacles
			j = i - 1;
			while (j >= 0 && Math.abs(
					events.get(j).getCoord() - eventX) < 0.1) {
				final SweepEvent ev = events.get(j);
				if (ev.getKind() == SweepEventKind.OPEN)
					active.insert(
							ev.getObstacle().getMinY() - margin,
							ev.getObstacle().getMaxY() + margin);
				j--;
			}
		}
	}

	/**
	 * Top-to-bottom sweep: generates horizontal visibility segments.
	 * Identical to the vertical pass but transposed.
	 */
	private void sweepHorizontalEdges(VisGraph graph) {
		final List<SweepEvent> events = new ArrayList<SweepEvent>();
		for (RectangleArea obs : obstacles) {
			final double minY = obs.getMinY() - margin;
			final double maxY = obs.getMaxY() + margin;
			events.add(SweepEvent.open(minY, obs));
			events.add(SweepEvent.close(maxY, obs));
		}
		for (XPoint2D pt : portConnections)
			events.add(SweepEvent.point(pt.getY(), pt));

		Collections.sort(events);

		final ActiveIntervalSet active = new ActiveIntervalSet();

		int i = 0;
		while (i < events.size()) {
			final double eventY = events.get(i).getCoord();
			final TreeSet<Double> interestingXs = new TreeSet<Double>();

			while (i < events.size()
					&& Math.abs(events.get(i).getCoord() - eventY)
							< 0.1) {
				final SweepEvent ev = events.get(i);
				switch (ev.getKind()) {
				case CLOSE:
					active.remove(
							ev.getObstacle().getMinX() - margin,
							ev.getObstacle().getMaxX() + margin);
					interestingXs.add(
							ev.getObstacle().getMinX() - margin);
					interestingXs.add(
							ev.getObstacle().getMaxX() + margin);
					break;
				case POINT:
					interestingXs.add(ev.getPoint().getX());
					break;
				case OPEN:
					break;
				}
				i++;
			}

			int j = i - 1;
			while (j >= 0 && Math.abs(
					events.get(j).getCoord() - eventY) < 0.1) {
				final SweepEvent ev = events.get(j);
				if (ev.getKind() == SweepEventKind.OPEN) {
					interestingXs.add(
							ev.getObstacle().getMinX() - margin);
					interestingXs.add(
							ev.getObstacle().getMaxX() + margin);
				}
				j--;
			}

			for (RectangleArea obs : obstacles) {
				final double obsMinY = obs.getMinY() - margin;
				final double obsMaxY = obs.getMaxY() + margin;
				if (obsMinY < eventY && obsMaxY > eventY) {
					interestingXs.add(obs.getMinX() - margin);
					interestingXs.add(obs.getMaxX() + margin);
				}
			}

			for (Double ix : interestingXs) {
				if (active.isBlocked(ix))
					continue;
				final double[] range = active.findVisibleRange(ix);
				final double lo = Math.max(range[0], boardMinX);
				final double hi = Math.min(range[1], boardMaxX);
				if (lo >= hi)
					continue;

				final List<Double> segmentXs =
						new ArrayList<Double>();
				for (Double sx : interestingXs.subSet(lo, true,
						hi, true)) {
					if (active.isBlocked(sx) == false)
						segmentXs.add(sx);
				}
				for (XPoint2D pt : portConnections) {
					if (Math.abs(pt.getY() - eventY) < 0.1
							&& pt.getX() >= lo && pt.getX() <= hi
							&& active.isBlocked(pt.getX()) == false)
						segmentXs.add(pt.getX());
				}
				Collections.sort(segmentXs);

				for (int k = 0; k < segmentXs.size() - 1; k++) {
					final double x1 = segmentXs.get(k);
					final double x2 = segmentXs.get(k + 1);
					if (Math.abs(x2 - x1) < 0.1)
						continue;
					final VisNode n1 = graph.addNode(x1, eventY,
							kindFor(x1, eventY));
					final VisNode n2 = graph.addNode(x2, eventY,
							kindFor(x2, eventY));
					graph.addEdge(n1, n2);
				}
			}

			j = i - 1;
			while (j >= 0 && Math.abs(
					events.get(j).getCoord() - eventY) < 0.1) {
				final SweepEvent ev = events.get(j);
				if (ev.getKind() == SweepEventKind.OPEN)
					active.insert(
							ev.getObstacle().getMinX() - margin,
							ev.getObstacle().getMaxX() + margin);
				j--;
			}
		}
	}

	private VisNodeKind kindFor(double x, double y) {
		for (XPoint2D pt : portConnections)
			if (Math.abs(pt.getX() - x) < 0.1
					&& Math.abs(pt.getY() - y) < 0.1)
				return VisNodeKind.PORT_CONNECTION;
		return VisNodeKind.OBSTACLE_CORNER;
	}
}
