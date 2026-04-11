package net.sourceforge.plantuml.svek.orthoroute;

import java.util.Map;
import java.util.TreeMap;

/**
 * Maintains a sorted set of non-overlapping intervals representing
 * obstacle shadows on the perpendicular axis during a sweep.
 * Backed by a TreeMap mapping interval start to interval end.
 */
public final class ActiveIntervalSet {

	private final TreeMap<Double, Double> intervals =
			new TreeMap<Double, Double>();

	public void insert(double min, double max) {
		if (min >= max)
			return;
		final Map.Entry<Double, Double> floor = intervals.floorEntry(min);
		double newMin = min;
		double newMax = max;

		if (floor != null && floor.getValue() >= min) {
			newMin = Math.min(newMin, floor.getKey());
			newMax = Math.max(newMax, floor.getValue());
			intervals.remove(floor.getKey());
		}

		while (true) {
			final Map.Entry<Double, Double> next =
					intervals.ceilingEntry(newMin);
			if (next == null || next.getKey() > newMax)
				break;
			newMax = Math.max(newMax, next.getValue());
			intervals.remove(next.getKey());
		}

		intervals.put(newMin, newMax);
	}

	public void remove(double min, double max) {
		if (min >= max)
			return;
		final Map.Entry<Double, Double> floor = intervals.floorEntry(min);
		if (floor != null && floor.getValue() > min) {
			final double origEnd = floor.getValue();
			if (floor.getKey() < min)
				intervals.put(floor.getKey(), min);
			else
				intervals.remove(floor.getKey());
			if (origEnd > max)
				intervals.put(max, origEnd);
		}

		while (true) {
			final Map.Entry<Double, Double> next =
					intervals.ceilingEntry(min);
			if (next == null || next.getKey() >= max)
				break;
			final double origEnd = next.getValue();
			intervals.remove(next.getKey());
			if (origEnd > max)
				intervals.put(max, origEnd);
		}
	}

	/**
	 * Find the maximal unblocked range around the query coordinate.
	 * Returns {lowerBound, upperBound} where the bounds are the
	 * edges of the nearest active intervals that bracket the query.
	 */
	public double[] findVisibleRange(double query) {
		double lo = Double.NEGATIVE_INFINITY;
		double hi = Double.POSITIVE_INFINITY;

		final Map.Entry<Double, Double> floor =
				intervals.floorEntry(query);
		if (floor != null) {
			if (floor.getValue() > query)
				return new double[]{floor.getKey(), floor.getValue()};
			lo = floor.getValue();
		}

		final Map.Entry<Double, Double> ceiling =
				intervals.higherEntry(query);
		if (ceiling != null)
			hi = ceiling.getKey();

		return new double[]{lo, hi};
	}

	/**
	 * Check whether a coordinate is inside an active interval.
	 */
	public boolean isBlocked(double query) {
		final Map.Entry<Double, Double> floor =
				intervals.floorEntry(query);
		return floor != null && floor.getValue() > query;
	}

	public boolean isEmpty() {
		return intervals.isEmpty();
	}
}
