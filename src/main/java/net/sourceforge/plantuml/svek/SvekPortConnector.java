package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.svek.image.EntityImagePort;
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
	private static final double PORT_WIDTH = 2 * PORT_RADIUS;
	private static final double MIN_STUB = 2 * PORT_WIDTH;
	private static final double SCAN_STEP = 4.0;
	private static final double SCAN_LIMIT = 2000.0;
	private static final int ARROW_WING = 9;
	private static final int ARROW_APERTURE = 4;
	private static final int ARROW_CONTACT = 5;

	private final SvekEdge edge;
	private final ISkinParam skinParam;
	private final Bibliotekon bibliotekon;
	private List<XPoint2D> waypoints;

	// Stub data computed in Phase 1
	private double srcPortX;
	private double srcPortY;
	private double dstPortX;
	private double dstPortY;
	private double srcDir;
	private double dstDir;
	private double srcTipX;
	private double dstTipX;

	public SvekPortConnector(SvekEdge edge, ISkinParam skinParam,
			Bibliotekon bibliotekon) {
		this.edge = edge;
		this.skinParam = skinParam;
		this.bibliotekon = bibliotekon;
	}

	SvekEdge getEdge() {
		return edge;
	}

	// ==================================================================
	// Pre-pass: compute all paths before drawing
	// ==================================================================

	public static void resolveAllPaths(List<SvekPortConnector> connectors,
			List<RectangleArea> harnessSpines, RectangleArea boardBounds) {

		// Phase 1: compute stub directions and initial tip positions
		for (SvekPortConnector pc : connectors)
			pc.computeStubs();

		// Phase 1b: separate stub tips that are too close at the same X
		// Only separate tips that share the same direction
		separateStubTips(connectors);

		// Phase 2: route the middle (between stub tips)
		for (SvekPortConnector pc : connectors)
			pc.routeMiddle(harnessSpines, boardBounds);

		// Phase 2b: separate parallel segments that are too close
		separateParallelSegments(connectors);

		// Phase 2c: re-check crossover horizontals that may now collide
		// after separation shifted verticals
		for (SvekPortConnector pc : connectors)
			pc.fixCrossoversAfterSeparation(harnessSpines);

		// Phase 3: validate all segments
		for (SvekPortConnector pc : connectors)
			pc.validate(harnessSpines, boardBounds);
	}

	// ==================================================================
	// Phase 1: Stubs
	// ==================================================================

	private void computeStubs() {
		final XPoint2D startPt = resolveEntityCenter(
				edge.getLink().getEntity1());
		final XPoint2D endPt = resolveEntityCenter(
				edge.getLink().getEntity2());
		if (startPt == null || endPt == null)
			return;

		srcPortX = startPt.getX();
		srcPortY = startPt.getY();
		dstPortX = endPt.getX();
		dstPortY = endPt.getY();

		// Direction: always away from parent component centre
		srcDir = stubDirection(edge.getLink().getEntity1(), srcPortX);
		dstDir = stubDirection(edge.getLink().getEntity2(), dstPortX);

		// Initial tip = port edge + minimum stub length,
		// but at least past the parent component boundary
		final double srcEdge = srcPortX + srcDir * PORT_RADIUS;
		final double dstEdge = dstPortX + dstDir * PORT_RADIUS;

		srcTipX = srcEdge + srcDir * MIN_STUB;
		dstTipX = dstEdge + dstDir * MIN_STUB;

		final RectangleArea srcBounds = getParentClusterBounds(
				edge.getLink().getEntity1());
		final RectangleArea dstBounds = getParentClusterBounds(
				edge.getLink().getEntity2());

		if (srcBounds != null) {
			if (EntityImagePort.isBoardPort(edge.getLink().getEntity1())) {
				if (srcDir > 0)
					srcTipX = Math.max(srcTipX,
							srcBounds.getMinX() + PORT_WIDTH);
				else
					srcTipX = Math.min(srcTipX,
							srcBounds.getMaxX() - PORT_WIDTH);
			} else {
				if (srcDir > 0)
					srcTipX = Math.max(srcTipX,
							srcBounds.getMaxX() + PORT_WIDTH);
				else
					srcTipX = Math.min(srcTipX,
							srcBounds.getMinX() - PORT_WIDTH);
			}
		}
		if (dstBounds != null) {
			if (EntityImagePort.isBoardPort(edge.getLink().getEntity2())) {
				if (dstDir > 0)
					dstTipX = Math.max(dstTipX,
							dstBounds.getMinX() + PORT_WIDTH);
				else
					dstTipX = Math.min(dstTipX,
							dstBounds.getMaxX() - PORT_WIDTH);
			} else {
				if (dstDir > 0)
					dstTipX = Math.max(dstTipX,
							dstBounds.getMaxX() + PORT_WIDTH);
				else
					dstTipX = Math.min(dstTipX,
							dstBounds.getMinX() - PORT_WIDTH);
			}
		}
	}

	/**
	 * Determine stub direction by comparing port X to the centre of its
	 * parent component. Port on the right half exits right, left half
	 * exits left. Board ports are inverted: stubs point inward.
	 */
	private double stubDirection(Entity portEntity, double portX) {
		final RectangleArea bounds = getParentClusterBounds(portEntity);
		if (bounds == null)
			return 1.0;
		final double centreX = (bounds.getMinX() + bounds.getMaxX()) / 2;
		if (EntityImagePort.isBoardPort(portEntity))
			return (portX >= centreX) ? -1.0 : 1.0;
		return (portX >= centreX) ? 1.0 : -1.0;
	}

	// ------------------------------------------------------------------
	// Phase 1b: separate stub tips that land at similar X
	// ------------------------------------------------------------------

	private static final class TipRef implements Comparable<TipRef> {
		final SvekPortConnector owner;
		final boolean isSrc;
		TipRef(SvekPortConnector owner, boolean isSrc) {
			this.owner = owner;
			this.isSrc = isSrc;
		}
		double tipX() {
			return isSrc ? owner.srcTipX : owner.dstTipX;
		}
		double tipY() {
			return isSrc ? owner.srcPortY : owner.dstPortY;
		}
		double dir() {
			return isSrc ? owner.srcDir : owner.dstDir;
		}
		void setTipX(double x) {
			if (isSrc)
				owner.srcTipX = x;
			else
				owner.dstTipX = x;
		}
		public int compareTo(TipRef o) {
			return Double.compare(tipX(), o.tipX());
		}
	}

	private static void separateStubTips(List<SvekPortConnector> connectors) {
		// Separate right-going and left-going tips independently
		final List<TipRef> rightTips = new ArrayList<TipRef>();
		final List<TipRef> leftTips = new ArrayList<TipRef>();
		for (SvekPortConnector pc : connectors) {
			if (pc.srcPortX == 0 && pc.srcPortY == 0)
				continue;
			final TipRef src = new TipRef(pc, true);
			final TipRef dst = new TipRef(pc, false);
			if (src.dir() > 0)
				rightTips.add(src);
			else
				leftTips.add(src);
			if (dst.dir() > 0)
				rightTips.add(dst);
			else
				leftTips.add(dst);
		}
		separateTipGroup(rightTips);
		separateTipGroup(leftTips);
	}

	private static void separateTipGroup(List<TipRef> tips) {
		Collections.sort(tips);
		int i = 0;
		while (i < tips.size()) {
			final List<TipRef> cluster = new ArrayList<TipRef>();
			cluster.add(tips.get(i));
			int j = i + 1;
			while (j < tips.size()
					&& tips.get(j).tipX() - cluster.get(0).tipX()
							< PORT_WIDTH) {
				cluster.add(tips.get(j));
				j++;
			}
			if (cluster.size() > 1)
				distributeStubCluster(cluster);
			i = j;
		}
	}

	private static void distributeStubCluster(List<TipRef> cluster) {
		double sumX = 0;
		for (TipRef t : cluster)
			sumX += t.tipX();
		final double centreX = sumX / cluster.size();
		final int n = cluster.size();
		final double totalSpan = (n - 1) * PORT_WIDTH;
		final double startX = centreX - totalSpan / 2;

		// Sort within cluster so stubs going in the same direction
		// are ordered consistently
		Collections.sort(cluster, new java.util.Comparator<TipRef>() {
			public int compare(TipRef a, TipRef b) {
				return Double.compare(a.tipY(), b.tipY());
			}
		});

		for (int k = 0; k < n; k++) {
			final double newX = startX + k * PORT_WIDTH;
			final TipRef tip = cluster.get(k);
			// Only extend outward — never shorten the stub
			if (tip.dir() > 0)
				tip.setTipX(Math.max(tip.tipX(), newX));
			else
				tip.setTipX(Math.min(tip.tipX(), newX));
		}
	}

	// ==================================================================
	// Single vertical attempt
	// ==================================================================

	/**
	 * Try to connect src and dst with a single vertical segment
	 * (3-segment path: srcStub → vertical → dstStub). Scans for a
	 * clear X between the two tips where the vertical from srcPortY
	 * to dstPortY and both horizontal stubs are all clear.
	 * Returns true and populates waypoints if successful.
	 */
	private boolean trySingleVertical(double srcEdge, double dstEdge,
			List<RectangleArea> obstacles, RectangleArea boardBounds) {
		final double yMin = Math.min(srcPortY, dstPortY);
		final double yMax = Math.max(srcPortY, dstPortY);
		final double lo = Math.min(srcTipX, dstTipX);
		final double hi = Math.max(srcTipX, dstTipX);
		final double naturalX = (srcTipX + dstTipX) / 2;

		// Enforce board boundary on scan range
		double scanLo = lo;
		double scanHi = hi;
		if (boardBounds != null) {
			scanLo = Math.max(scanLo,
					boardBounds.getMinX() + PORT_WIDTH);
			scanHi = Math.min(scanHi,
					boardBounds.getMaxX() - PORT_WIDTH);
		}

		for (double offset = 0; offset < SCAN_LIMIT;
				offset += SCAN_STEP) {
			final double x1 = naturalX + offset;
			if (x1 >= scanLo && x1 <= scanHi
					&& isSingleVerticalClear(x1, yMin, yMax,
							srcEdge, dstEdge, obstacles)) {
				buildSingleVerticalPath(srcEdge, dstEdge, x1);
				return true;
			}
			if (offset == 0)
				continue;
			final double x2 = naturalX - offset;
			if (x2 >= scanLo && x2 <= scanHi
					&& isSingleVerticalClear(x2, yMin, yMax,
							srcEdge, dstEdge, obstacles)) {
				buildSingleVerticalPath(srcEdge, dstEdge, x2);
				return true;
			}
		}
		return false;
	}

	private boolean isSingleVerticalClear(double x, double yMin,
			double yMax, double srcEdge, double dstEdge,
			List<RectangleArea> obstacles) {
		// Vertical must be outside both parent component boundaries
		final RectangleArea srcBounds = getParentClusterBounds(
				edge.getLink().getEntity1());
		final RectangleArea dstBounds = getParentClusterBounds(
				edge.getLink().getEntity2());
		if (srcBounds != null && x > srcBounds.getMinX() - PORT_WIDTH
				&& x < srcBounds.getMaxX() + PORT_WIDTH
				&& yMax > srcBounds.getMinY() && yMin < srcBounds.getMaxY())
			return false;
		if (dstBounds != null && x > dstBounds.getMinX() - PORT_WIDTH
				&& x < dstBounds.getMaxX() + PORT_WIDTH
				&& yMax > dstBounds.getMinY() && yMin < dstBounds.getMaxY())
			return false;
		if (verticalCollides(x, yMin, yMax, obstacles))
			return false;
		// Check stubs against general obstacles
		if (horizontalCollides(srcPortY,
				Math.min(srcEdge, x), Math.max(srcEdge, x),
				obstacles))
			return false;
		if (horizontalCollides(dstPortY,
				Math.min(dstEdge, x), Math.max(dstEdge, x),
				obstacles))
			return false;
		// Stubs must not cross through their own parent body —
		// the vertical must be on the port's side of its parent
		if (srcBounds != null
				&& stubCrossesParent(x, srcEdge, srcPortY, srcBounds))
			return false;
		if (dstBounds != null
				&& stubCrossesParent(x, dstEdge, dstPortY, dstBounds))
			return false;
		return true;
	}

	private static boolean stubCrossesParent(double vertX,
			double portEdge, double stubY, RectangleArea parent) {
		if (stubY < parent.getMinY() || stubY > parent.getMaxY())
			return false;
		final double xMin = Math.min(vertX, portEdge);
		final double xMax = Math.max(vertX, portEdge);
		return (xMin < parent.getMinX() && xMax > parent.getMinX())
				|| (xMin < parent.getMaxX() && xMax > parent.getMaxX());
	}

	private void buildSingleVerticalPath(double srcEdge,
			double dstEdge, double vertX) {
		waypoints.add(new XPoint2D(srcEdge, srcPortY));
		waypoints.add(new XPoint2D(vertX, srcPortY));
		waypoints.add(new XPoint2D(vertX, dstPortY));
		waypoints.add(new XPoint2D(dstEdge, dstPortY));
	}

	// ==================================================================
	// Phase 2c: Fix crossovers after separation
	// ==================================================================

	private void fixCrossoversAfterSeparation(
			List<RectangleArea> harnessSpines) {
		if (waypoints == null || waypoints.size() < 4)
			return;
		final List<RectangleArea> obstacles =
				collectObstacles(harnessSpines);
		for (int i = 1; i < waypoints.size() - 2; i++) {
			final XPoint2D a = waypoints.get(i);
			final XPoint2D b = waypoints.get(i + 1);
			if (Math.abs(a.getY() - b.getY()) < 0.5) {
				// Horizontal segment
				final double y = a.getY();
				final double xMin = Math.min(a.getX(), b.getX());
				final double xMax = Math.max(a.getX(), b.getX());
				if (horizontalCollides(y, xMin, xMax, obstacles)) {
					final double newY = findClearHorizontal(y, xMin,
							xMax, obstacles);
					waypoints.set(i, new XPoint2D(a.getX(), newY));
					waypoints.set(i + 1, new XPoint2D(b.getX(), newY));
				}
			} else if (Math.abs(a.getX() - b.getX()) < 0.5) {
				// Vertical segment shifted by separation
				final double x = a.getX();
				final double yMin = Math.min(a.getY(), b.getY());
				final double yMax = Math.max(a.getY(), b.getY());
				if (verticalCollides(x, yMin, yMax, obstacles)) {
					final double newX =
							findClearVerticalBidirectional(x,
									yMin, yMax, obstacles);
					waypoints.set(i, new XPoint2D(newX, a.getY()));
					waypoints.set(i + 1,
							new XPoint2D(newX, b.getY()));
					// Update connected horizontal endpoints
					if (i > 0) {
						final XPoint2D prev = waypoints.get(i - 1);
						if (Math.abs(prev.getY() - a.getY()) < 0.5)
							waypoints.set(i - 1,
									new XPoint2D(prev.getX(),
											a.getY()));
					}
					if (i + 2 < waypoints.size()) {
						final XPoint2D next =
								waypoints.get(i + 2);
						if (Math.abs(next.getY() - b.getY()) < 0.5)
							waypoints.set(i + 2,
									new XPoint2D(next.getX(),
											b.getY()));
					}
				}
			}
		}
	}

	// ==================================================================
	// Phase 2b: Separate parallel segments
	// ==================================================================

	private static final class SegRef {
		final SvekPortConnector owner;
		final int index;
		SegRef(SvekPortConnector owner, int index) {
			this.owner = owner;
			this.index = index;
		}
		XPoint2D a() { return owner.waypoints.get(index); }
		XPoint2D b() { return owner.waypoints.get(index + 1); }
	}

	private static void separateParallelSegments(
			List<SvekPortConnector> connectors) {
		final List<SegRef> verticals = new ArrayList<SegRef>();
		final List<SegRef> horizontals = new ArrayList<SegRef>();
		for (SvekPortConnector pc : connectors) {
			if (pc.waypoints == null || pc.waypoints.size() < 2)
				continue;
			final int lastSeg = pc.waypoints.size() - 2;
			for (int i = 0; i <= lastSeg; i++) {
				// Skip stub segments — their endpoints are
				// anchored to port positions
				if (i == 0 || i == lastSeg)
					continue;
				final XPoint2D a = pc.waypoints.get(i);
				final XPoint2D b = pc.waypoints.get(i + 1);
				if (Math.abs(a.getX() - b.getX()) < 0.5
						&& Math.abs(a.getY() - b.getY()) > 1)
					verticals.add(new SegRef(pc, i));
				else if (Math.abs(a.getY() - b.getY()) < 0.5
						&& Math.abs(a.getX() - b.getX()) > 1)
					horizontals.add(new SegRef(pc, i));
			}
		}
		separateVerticalSegs(verticals);
		separateHorizontalSegs(horizontals);
	}

	private static void separateVerticalSegs(List<SegRef> segments) {
		Collections.sort(segments,
				new java.util.Comparator<SegRef>() {
					public int compare(SegRef a, SegRef b) {
						return Double.compare(a.a().getX(), b.a().getX());
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
					if (Math.abs(candidate.a().getX()
							- member.a().getX()) < PORT_WIDTH
							&& yOverlap(candidate, member)) {
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
				distributeVerticalSegs(cluster);
			i = j;
		}
	}

	private static void separateHorizontalSegs(List<SegRef> segments) {
		Collections.sort(segments,
				new java.util.Comparator<SegRef>() {
					public int compare(SegRef a, SegRef b) {
						return Double.compare(a.a().getY(), b.a().getY());
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
					if (Math.abs(candidate.a().getY()
							- member.a().getY()) < PORT_WIDTH
							&& xOverlap(candidate, member)) {
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
				distributeHorizontalSegs(cluster);
			i = j;
		}
	}

	private static boolean yOverlap(SegRef a, SegRef b) {
		final double aMin = Math.min(a.a().getY(), a.b().getY());
		final double aMax = Math.max(a.a().getY(), a.b().getY());
		final double bMin = Math.min(b.a().getY(), b.b().getY());
		final double bMax = Math.max(b.a().getY(), b.b().getY());
		return aMax > bMin && bMax > aMin;
	}

	private static boolean xOverlap(SegRef a, SegRef b) {
		final double aMin = Math.min(a.a().getX(), a.b().getX());
		final double aMax = Math.max(a.a().getX(), a.b().getX());
		final double bMin = Math.min(b.a().getX(), b.b().getX());
		final double bMax = Math.max(b.a().getX(), b.b().getX());
		return aMax > bMin && bMax > aMin;
	}

	private static void distributeVerticalSegs(List<SegRef> cluster) {
		double sumX = 0;
		for (SegRef s : cluster)
			sumX += s.a().getX();
		final double centreX = sumX / cluster.size();
		final int n = cluster.size();
		final double span = (n - 1) * PORT_WIDTH;
		final double startX = centreX - span / 2;
		for (int k = 0; k < n; k++)
			shiftVertical(cluster.get(k), startX + k * PORT_WIDTH);
	}

	private static void distributeHorizontalSegs(List<SegRef> cluster) {
		double sumY = 0;
		for (SegRef s : cluster)
			sumY += s.a().getY();
		final double centreY = sumY / cluster.size();
		final int n = cluster.size();
		final double span = (n - 1) * PORT_WIDTH;
		final double startY = centreY - span / 2;
		for (int k = 0; k < n; k++)
			shiftHorizontal(cluster.get(k), startY + k * PORT_WIDTH);
	}

	private static void shiftVertical(SegRef seg, double newX) {
		final List<XPoint2D> wp = seg.owner.waypoints;
		final int ia = seg.index;
		final int ib = seg.index + 1;
		if (ia != 0)
			wp.set(ia, new XPoint2D(newX, wp.get(ia).getY()));
		if (ib != wp.size() - 1)
			wp.set(ib, new XPoint2D(newX, wp.get(ib).getY()));
	}

	private static void shiftHorizontal(SegRef seg, double newY) {
		final List<XPoint2D> wp = seg.owner.waypoints;
		final int ia = seg.index;
		final int ib = seg.index + 1;
		if (ia != 0)
			wp.set(ia, new XPoint2D(wp.get(ia).getX(), newY));
		if (ib != wp.size() - 1)
			wp.set(ib, new XPoint2D(wp.get(ib).getX(), newY));
	}

	// ==================================================================
	// Phase 2: Route middle
	// ==================================================================

	private void routeMiddle(List<RectangleArea> harnessSpines,
			RectangleArea boardBounds) {
		if (srcPortX == 0 && srcPortY == 0)
			return;

		final double srcEdge = srcPortX + srcDir * PORT_RADIUS;
		final double dstEdge = dstPortX + dstDir * PORT_RADIUS;

		final List<RectangleArea> obstacles = collectObstacles(harnessSpines);

		// Push stub tips past component obstacles (not harness spines —
		// stubs share space with harness connections to the same component)
		final List<RectangleArea> clusterObstacles =
				collectObstacles(Collections.<RectangleArea>emptyList());
		srcTipX = findClearStubX(srcTipX, srcEdge, srcPortY, srcDir,
				clusterObstacles);
		dstTipX = findClearStubX(dstTipX, dstEdge, dstPortY, dstDir,
				clusterObstacles);

		// Enforce board boundary on tips
		if (boardBounds != null) {
			final double innerLeft = boardBounds.getMinX() + PORT_WIDTH;
			final double innerRight = boardBounds.getMaxX() - PORT_WIDTH;
			srcTipX = Math.max(innerLeft, Math.min(innerRight, srcTipX));
			dstTipX = Math.max(innerLeft, Math.min(innerRight, dstTipX));
		}

		// Route from srcTip to dstTip
		waypoints = new ArrayList<XPoint2D>();

		final RectangleArea srcParent = getParentClusterBounds(
				edge.getLink().getEntity1());
		final RectangleArea dstParent = getParentClusterBounds(
				edge.getLink().getEntity2());
		final boolean sameTipClear = Math.abs(srcTipX - dstTipX) < 1.0
				&& (srcParent == null || stubCrossesParent(
						srcTipX, srcEdge, srcPortY, srcParent) == false)
				&& (dstParent == null || stubCrossesParent(
						srcTipX, dstEdge, dstPortY, dstParent) == false);
		if (sameTipClear) {
			// Tips at same X — single vertical connects them
			waypoints.add(new XPoint2D(srcEdge, srcPortY));
			waypoints.add(new XPoint2D(srcTipX, srcPortY));
			waypoints.add(new XPoint2D(srcTipX, dstPortY));
			waypoints.add(new XPoint2D(dstEdge, dstPortY));
		} else if (trySingleVertical(srcEdge, dstEdge, obstacles,
				boardBounds)) {
			// Found a single vertical that connects both stubs
		} else {
			// Need a horizontal crossover between the two verticals
			final double xMin = Math.min(srcTipX, dstTipX);
			final double xMax = Math.max(srcTipX, dstTipX);
			final double naturalCrossY = (srcPortY + dstPortY) / 2;
			double crossY = findClearHorizontal(naturalCrossY, xMin,
					xMax, obstacles);

			// Enforce board boundary on crossover Y
			if (boardBounds != null) {
				final double innerTop = boardBounds.getMinY()
						+ PORT_WIDTH;
				final double innerBottom = boardBounds.getMaxY()
						- PORT_WIDTH;
				if (crossY < innerTop || crossY > innerBottom)
					crossY = findClearHorizontalInBounds(
							naturalCrossY, xMin, xMax, obstacles,
							innerTop, innerBottom);
			}

			// Check verticals over their actual Y spans —
			// try outward first (keeps stubs short), then inward
			final double srcVMin = Math.min(srcPortY, crossY);
			final double srcVMax = Math.max(srcPortY, crossY);
			final double dstVMin = Math.min(dstPortY, crossY);
			final double dstVMax = Math.max(dstPortY, crossY);
			if (verticalCollides(srcTipX, srcVMin, srcVMax, obstacles)) {
				final double outward = findClearVertical(srcTipX,
						srcVMin, srcVMax, srcDir, obstacles);
				if (verticalCollides(outward, srcVMin, srcVMax,
						obstacles) == false)
					srcTipX = outward;
				else
					srcTipX = findClearVerticalBidirectional(srcTipX,
							srcVMin, srcVMax, obstacles);
			}
			if (verticalCollides(dstTipX, dstVMin, dstVMax, obstacles)) {
				final double outward = findClearVertical(dstTipX,
						dstVMin, dstVMax, dstDir, obstacles);
				if (verticalCollides(outward, dstVMin, dstVMax,
						obstacles) == false)
					dstTipX = outward;
				else
					dstTipX = findClearVerticalBidirectional(dstTipX,
							dstVMin, dstVMax, obstacles);
			}

			// Re-check crossover with final positions
			final double xMin2 = Math.min(srcTipX, dstTipX);
			final double xMax2 = Math.max(srcTipX, dstTipX);
			crossY = findClearHorizontal(crossY, xMin2, xMax2,
					obstacles);

			waypoints.add(new XPoint2D(srcEdge, srcPortY));
			waypoints.add(new XPoint2D(srcTipX, srcPortY));
			waypoints.add(new XPoint2D(srcTipX, crossY));
			waypoints.add(new XPoint2D(dstTipX, crossY));
			waypoints.add(new XPoint2D(dstTipX, dstPortY));
			waypoints.add(new XPoint2D(dstEdge, dstPortY));
		}
	}

	// ==================================================================
	// Phase 3: Validate
	// ==================================================================

	private void validate(List<RectangleArea> harnessSpines,
			RectangleArea boardBounds) {
		if (waypoints == null || waypoints.size() < 2)
			return;

		final List<RectangleArea> allObstacles =
				collectObstacles(harnessSpines);
		final List<RectangleArea> clusterOnly =
				collectObstacles(Collections.<RectangleArea>emptyList());
		final String ent1 = edge.getLink().getEntity1().getName();
		final String ent2 = edge.getLink().getEntity2().getName();
		final int last = waypoints.size() - 2;

		for (int i = 0; i < waypoints.size() - 1; i++) {
			final XPoint2D a = waypoints.get(i);
			final XPoint2D b = waypoints.get(i + 1);
			// Stubs (first and last segments) only check against clusters,
			// not harness spines
			final List<RectangleArea> obs = (i == 0 || i == last)
					? clusterOnly : allObstacles;
			if (segmentCollides(a.getX(), a.getY(), b.getX(), b.getY(),
					obs)) {
				Log.error("Port connector " + ent1 + "->" + ent2
						+ " seg " + i + " obstacle collision ("
						+ a.getX() + "," + a.getY() + ")->("
						+ b.getX() + "," + b.getY() + ")");
				for (RectangleArea r : obs)
					if (segmentCollides(a.getX(), a.getY(),
							b.getX(), b.getY(),
							Collections.singletonList(r)))
						Log.error("  hit: x=[" + r.getMinX()
								+ ".." + r.getMaxX() + "] y=["
								+ r.getMinY() + ".."
								+ r.getMaxY() + "]");
			}

			final boolean srcBoardStub = i == 0
					&& EntityImagePort.isBoardPort(
							edge.getLink().getEntity1());
			final boolean dstBoardStub = i == last
					&& EntityImagePort.isBoardPort(
							edge.getLink().getEntity2());
			if (boardBounds != null
					&& srcBoardStub == false && dstBoardStub == false
					&& segmentOutsideBoard(a.getX(), a.getY(),
							b.getX(), b.getY(), boardBounds))
				Log.error("Port connector " + ent1 + "->" + ent2
						+ " seg " + i + " outside board boundary");
		}
	}

	private static boolean segmentOutsideBoard(double x1, double y1,
			double x2, double y2, RectangleArea board) {
		final double margin = PORT_WIDTH;
		final double left = board.getMinX() + margin;
		final double right = board.getMaxX() - margin;
		final double top = board.getMinY() + margin;
		final double bottom = board.getMaxY() - margin;
		return Math.min(x1, x2) < left || Math.max(x1, x2) > right
				|| Math.min(y1, y2) < top || Math.max(y1, y2) > bottom;
	}

	// ==================================================================
	// Collision detection
	// ==================================================================

	static boolean verticalCollides(double x, double yMin, double yMax,
			List<RectangleArea> obstacles) {
		for (RectangleArea rect : obstacles) {
			if (x > rect.getMinX() - PORT_WIDTH
					&& x < rect.getMaxX() + PORT_WIDTH
					&& yMax > rect.getMinY() - PORT_WIDTH
					&& yMin < rect.getMaxY() + PORT_WIDTH)
				return true;
		}
		return false;
	}

	static boolean horizontalCollides(double y, double xMin, double xMax,
			List<RectangleArea> obstacles) {
		for (RectangleArea rect : obstacles) {
			if (y > rect.getMinY() - PORT_WIDTH
					&& y < rect.getMaxY() + PORT_WIDTH
					&& xMax > rect.getMinX() - PORT_WIDTH
					&& xMin < rect.getMaxX() + PORT_WIDTH)
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

	/**
	 * Scan outward from proposedX (both directions) to find a clear
	 * vertical position. Used by SvekHarness for spine avoidance.
	 */
	static double findClearVerticalBidirectional(double proposedX,
			double yMin, double yMax, List<RectangleArea> obstacles) {
		for (double offset = 0; offset < SCAN_LIMIT; offset += SCAN_STEP) {
			if (verticalCollides(proposedX + offset, yMin, yMax,
					obstacles) == false)
				return proposedX + offset;
			if (offset > 0 && verticalCollides(proposedX - offset, yMin,
					yMax, obstacles) == false)
				return proposedX - offset;
		}
		return proposedX;
	}

	// ==================================================================
	// Scan helpers
	// ==================================================================

	private static double findClearVertical(double vertX, double yMin,
			double yMax, double dir, List<RectangleArea> obstacles) {
		for (double candidate = vertX;
				Math.abs(candidate - vertX) < SCAN_LIMIT;
				candidate += dir * SCAN_STEP) {
			if (verticalCollides(candidate, yMin, yMax,
					obstacles) == false)
				return candidate;
		}
		return vertX;
	}

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

	private static double findClearHorizontal(double proposedY,
			double xMin, double xMax, List<RectangleArea> obstacles) {
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

	private static double findClearHorizontalInBounds(double proposedY,
			double xMin, double xMax, List<RectangleArea> obstacles,
			double boundsTop, double boundsBottom) {
		for (double offset = 0; offset < SCAN_LIMIT; offset += SCAN_STEP) {
			final double y1 = proposedY + offset;
			if (y1 >= boundsTop && y1 <= boundsBottom
					&& horizontalCollides(y1, xMin, xMax,
							obstacles) == false)
				return y1;
			if (offset == 0)
				continue;
			final double y2 = proposedY - offset;
			if (y2 >= boundsTop && y2 <= boundsBottom
					&& horizontalCollides(y2, xMin, xMax,
							obstacles) == false)
				return y2;
		}
		return proposedY;
	}

	// ==================================================================
	// Obstacle collection
	// ==================================================================

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

		final Entity entity1 = edge.getLink().getEntity1();
		final Entity entity2 = edge.getLink().getEntity2();
		final Entity parent1 = entity1.getParentContainer();
		final Entity parent2 = entity2.getParentContainer();
		Entity board;
		if (EntityImagePort.isBoardPort(entity1))
			board = parent1;
		else if (EntityImagePort.isBoardPort(entity2))
			board = parent2;
		else
			board = (parent1 != null)
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

	// ==================================================================
	// Drawing
	// ==================================================================

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
			drawLine(ugLine, waypoints.get(i).getX(),
					waypoints.get(i).getY(),
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
