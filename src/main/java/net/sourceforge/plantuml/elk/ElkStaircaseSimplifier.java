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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.plantuml.klimt.geom.XPoint2D;

/**
 * Draws ELK's monotone orthogonal staircases as a single corner where that is
 * safe.
 * <p>
 * ELK often routes a simple "across and up" edge through two or more columns,
 * one in each gap it passes, giving five or more segments where three would
 * do. Collapsing every such staircase onto one of its columns, chosen per edge
 * in isolation, throws away the order ELK gave the columns and makes
 * neighbouring edges cross. So the collapse is planned for all edges together:
 * an edge only becomes a single corner when the result crosses or overlaps
 * the other edges no more often than its staircase did.
 */
public final class ElkStaircaseSimplifier {

	private static final double EPS = 1.0;

	private ElkStaircaseSimplifier() {
	}

	/**
	 * How to collapse one staircase: the coordinate of the column (when the
	 * staircase starts horizontally) or row (when it starts vertically) that the
	 * single corner turns on.
	 */
	public static final class Collapse {

		private final boolean startsHorizontally;
		private final double pivot;

		Collapse(boolean startsHorizontally, double pivot) {
			this.startsHorizontally = startsHorizontally;
			this.pivot = pivot;
		}

		/** The same collapse in a frame translated by (-dx, -dy). */
		public Collapse translated(double dx, double dy) {
			return new Collapse(startsHorizontally, pivot - (startsHorizontally ? dx : dy));
		}

		/** The single-corner polyline joining the first and last of pts. */
		public List<XPoint2D> apply(List<XPoint2D> pts) {
			final XPoint2D start = pts.get(0);
			final XPoint2D end = pts.get(pts.size() - 1);
			final List<XPoint2D> result = new ArrayList<XPoint2D>();
			result.add(start);
			if (startsHorizontally) {
				result.add(new XPoint2D(pivot, start.getY()));
				result.add(new XPoint2D(pivot, end.getY()));
			} else {
				result.add(new XPoint2D(start.getX(), pivot));
				result.add(new XPoint2D(end.getX(), pivot));
			}
			result.add(end);
			return result;
		}
	}

	/**
	 * Plans the collapse of every polyline, all in one frame. Returns one entry
	 * per polyline, {@code null} where it must keep its route.
	 */
	public static List<Collapse> plan(List<List<XPoint2D>> polylines) {
		final List<List<XPoint2D>> current = new ArrayList<List<XPoint2D>>(polylines);
		final List<Collapse> result = new ArrayList<Collapse>(Collections.<Collapse>nCopies(polylines.size(), null));
		// A staircase can block its neighbour's collapse and then collapse
		// itself, so go round again until nothing changes. Each pass only
		// collapses staircases, so this ends within one pass per edge.
		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i < current.size(); i++) {
				final List<XPoint2D> pts = current.get(i);
				if (result.get(i) != null || isMonotoneStaircase(pts) == false)
					continue;
				final int before = conflicts(pts, current, i);
				for (Collapse candidate : candidates(pts)) {
					final List<XPoint2D> collapsed = candidate.apply(pts);
					if (conflicts(collapsed, current, i) <= before) {
						current.set(i, collapsed);
						result.set(i, candidate);
						changed = true;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * True for an orthogonal polyline of five or more points that never turns
	 * back in X or in Y. A path that turns back is steering round something,
	 * so it is left alone.
	 */
	static boolean isMonotoneStaircase(List<XPoint2D> pts) {
		if (pts.size() <= 4)
			return false;
		int xDir = 0;
		int yDir = 0;
		for (int i = 1; i < pts.size(); i++) {
			final double dx = pts.get(i).getX() - pts.get(i - 1).getX();
			final double dy = pts.get(i).getY() - pts.get(i - 1).getY();
			if (Math.abs(dx) > EPS) {
				final int dir = dx > 0 ? 1 : -1;
				if (xDir == 0)
					xDir = dir;
				else if (xDir != dir)
					return false;
			}
			if (Math.abs(dy) > EPS) {
				final int dir = dy > 0 ? 1 : -1;
				if (yDir == 0)
					yDir = dir;
				else if (yDir != dir)
					return false;
			}
		}
		return xDir != 0 && yDir != 0;
	}

	/**
	 * One candidate per column (or row) of the staircase, longest first, so
	 * that the first candidate is the one an edge on its own would take.
	 */
	private static List<Collapse> candidates(List<XPoint2D> pts) {
		final boolean startsHorizontally = Math.abs(pts.get(1).getY() - pts.get(0).getY()) < EPS;
		final List<double[]> pivots = new ArrayList<double[]>();
		for (int i = 1; i < pts.size(); i++) {
			final XPoint2D a = pts.get(i - 1);
			final XPoint2D b = pts.get(i);
			final double dx = Math.abs(b.getX() - a.getX());
			final double dy = Math.abs(b.getY() - a.getY());
			if (startsHorizontally && dx < EPS && dy > EPS)
				pivots.add(new double[] { b.getX(), dy });
			else if (startsHorizontally == false && dy < EPS && dx > EPS)
				pivots.add(new double[] { b.getY(), dx });
		}
		// Stable, so equal lengths keep the order along the edge.
		Collections.sort(pivots, new Comparator<double[]>() {
			public int compare(double[] p1, double[] p2) {
				return Double.compare(p2[1], p1[1]);
			}
		});
		final List<Collapse> result = new ArrayList<Collapse>();
		for (double[] p : pivots)
			result.add(new Collapse(startsHorizontally, p[0]));
		return result;
	}

	/** Crossings and overlaps between pts and every polyline but the one at skip. */
	private static int conflicts(List<XPoint2D> pts, List<List<XPoint2D>> others, int skip) {
		int result = 0;
		for (int j = 0; j < others.size(); j++)
			if (j != skip)
				result += conflicts(pts, others.get(j));
		return result;
	}

	static int conflicts(List<XPoint2D> p, List<XPoint2D> q) {
		int result = 0;
		for (int i = 1; i < p.size(); i++)
			for (int j = 1; j < q.size(); j++)
				if (conflict(p.get(i - 1), p.get(i), q.get(j - 1), q.get(j)))
					result++;
		return result;
	}

	/**
	 * True when two axis-aligned segments cross inside both, or lie on the same
	 * line and share more than a point. Segments that only touch, as edges
	 * leaving the same port do, do not count.
	 */
	private static boolean conflict(XPoint2D a1, XPoint2D a2, XPoint2D b1, XPoint2D b2) {
		final boolean aVertical = Math.abs(a1.getX() - a2.getX()) < EPS;
		final boolean bVertical = Math.abs(b1.getX() - b2.getX()) < EPS;
		if (aVertical && bVertical)
			return Math.abs(a1.getX() - b1.getX()) < EPS && overlap(a1.getY(), a2.getY(), b1.getY(), b2.getY());
		if (aVertical == false && bVertical == false)
			return Math.abs(a1.getY() - b1.getY()) < EPS && overlap(a1.getX(), a2.getX(), b1.getX(), b2.getX());
		if (aVertical)
			return strictlyInside(a1.getX(), b1.getX(), b2.getX()) && strictlyInside(b1.getY(), a1.getY(), a2.getY());
		return strictlyInside(b1.getX(), a1.getX(), a2.getX()) && strictlyInside(a1.getY(), b1.getY(), b2.getY());
	}

	private static boolean overlap(double a1, double a2, double b1, double b2) {
		final double lo = Math.max(Math.min(a1, a2), Math.min(b1, b2));
		final double hi = Math.min(Math.max(a1, a2), Math.max(b1, b2));
		return hi - lo > EPS;
	}

	private static boolean strictlyInside(double v, double end1, double end2) {
		return v > Math.min(end1, end2) + EPS && v < Math.max(end1, end2) - EPS;
	}

}
