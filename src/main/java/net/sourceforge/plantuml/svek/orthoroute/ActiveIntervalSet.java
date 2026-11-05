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

/**
 * The obstacle shadows on the perpendicular axis during a sweep. Shadows
 * of obstacles that overlap are kept apart, so that removing one leaves
 * the other in place.
 */
public final class ActiveIntervalSet {

	private final List<double[]> intervals = new ArrayList<double[]>();

	public void insert(double min, double max) {
		if (min >= max)
			return;
		intervals.add(new double[] { min, max });
	}

	public void remove(double min, double max) {
		for (int i = 0; i < intervals.size(); i++) {
			final double[] interval = intervals.get(i);
			if (interval[0] == min && interval[1] == max) {
				intervals.remove(i);
				return;
			}
		}
	}

	/**
	 * Find the maximal unblocked range around the query coordinate.
	 * Returns {lowerBound, upperBound} where the bounds are the
	 * edges of the nearest active intervals that bracket the query.
	 * A query on the edge of an interval sees up to that edge.
	 */
	public double[] findVisibleRange(double query) {
		double lo = Double.NEGATIVE_INFINITY;
		double hi = Double.POSITIVE_INFINITY;
		for (double[] interval : intervals) {
			if (interval[0] < query && interval[1] > query)
				return new double[] { interval[0], interval[1] };
			if (interval[1] <= query)
				lo = Math.max(lo, interval[1]);
			if (interval[0] >= query)
				hi = Math.min(hi, interval[0]);
		}
		return new double[] { lo, hi };
	}

	/**
	 * Check whether a coordinate is strictly inside an active interval.
	 * Intervals are open: a point on either edge is not blocked, so the
	 * corners of an obstacle and a stub tip on its margin stay reachable.
	 */
	public boolean isBlocked(double query) {
		for (double[] interval : intervals)
			if (interval[0] < query && interval[1] > query)
				return true;
		return false;
	}

	public boolean isEmpty() {
		return intervals.isEmpty();
	}
}
