package net.sourceforge.plantuml.svek.orthoroute;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

/**
 * The orthogonal router on its own: plain rectangles and points, no diagram
 * and no Graphviz.
 */
class OrthoRouteTest {

	private static final double CLEARANCE = 12;
	private static final double EPSILON = 0.1;

	@Test
	void interval_edges_are_not_blocked() {
		final ActiveIntervalSet set = new ActiveIntervalSet();
		set.insert(10, 20);
		assertFalse(set.isBlocked(10), "lower edge");
		assertTrue(set.isBlocked(15), "inside");
		assertFalse(set.isBlocked(20), "upper edge");
		assertEquals(Arrays.asList(Double.NEGATIVE_INFINITY, 10.0), boxed(set.findVisibleRange(10)));
		assertEquals(Arrays.asList(20.0, Double.POSITIVE_INFINITY), boxed(set.findVisibleRange(20)));
	}

	@Test
	void removing_an_interval_keeps_an_overlapping_one() {
		// The shadows of two obstacles closer than twice the clearance overlap.
		final ActiveIntervalSet set = new ActiveIntervalSet();
		set.insert(0, 10);
		set.insert(5, 15);
		set.remove(0, 10);
		assertTrue(set.isBlocked(7), "the overlap went with the first shadow");
		assertTrue(set.isBlocked(12));
		assertFalse(set.isBlocked(3));
	}

	@Test
	void path_goes_around_an_obstacle_between_the_tips() {
		final RectangleArea obstacle = new RectangleArea(100, 0, 200, 100);
		// Both tips sit exactly on the obstacle's clearance, as a stub tip
		// sits on its own component's.
		final XPoint2D src = new XPoint2D(100 - CLEARANCE, 50);
		final XPoint2D dst = new XPoint2D(200 + CLEARANCE, 50);
		final List<XPoint2D> path = route(Collections.singletonList(obstacle), src, dst);
		assertFalse(path.isEmpty(), "no path");
		assertOrthogonal(path);
		assertAvoids(path, obstacle);
	}

	@Test
	void tip_crowded_by_a_neighbour_is_still_routed() {
		// B is closer to A than the clearance, so the tip of a port on A's
		// right side lies on B's border.
		final RectangleArea a = new RectangleArea(0, 0, 100, 100);
		final RectangleArea b = new RectangleArea(100 + CLEARANCE, 0, 212, 100);
		final XPoint2D src = new XPoint2D(100 + CLEARANCE, 50);
		final XPoint2D dst = new XPoint2D(400, 50);
		final List<XPoint2D> path = route(Arrays.asList(a, b), src, dst);
		assertFalse(path.isEmpty(), "no path");
		assertOrthogonal(path);
		assertAvoids(path, a);
		assertAvoids(path, b);
	}

	@Test
	void simplify_drops_back_tracking_and_collinear_points() {
		final List<XPoint2D> path = OrthoRouteEngine.simplify(points(0, 0, 30, 0, 12, 0, 12, 40, 12, 40, 12, 80, 50, 80));
		assertEquals(points(0, 0, 12, 0, 12, 80, 50, 80), path);
	}

	@Test
	void nudging_separates_overlapping_segments_and_keeps_them_clear_of_obstacles() {
		// Two connectors share the vertical x = 50; an obstacle stands just
		// to its right, nothing to its left and there is no board.
		final List<XPoint2D> c1 = points(0, 0, 50, 0, 50, 100, 100, 100);
		final List<XPoint2D> c2 = points(0, 10, 50, 10, 50, 90, 100, 90);
		final RectangleArea obstacle = new RectangleArea(60, 0, 160, 100);
		NudgeEngine.nudge(Arrays.asList(c1, c2), Collections.singletonList(obstacle), null);

		assertOrthogonal(c1);
		assertOrthogonal(c2);
		final double x1 = c1.get(1).getX();
		final double x2 = c2.get(1).getX();
		assertTrue(Math.abs(x1 - x2) >= CLEARANCE - EPSILON, "segments still overlap: " + x1 + ", " + x2);
		for (double x : new double[] { x1, x2 }) {
			assertTrue(x <= obstacle.getMinX() - CLEARANCE + EPSILON, x + " is too close to the obstacle");
			assertTrue(Math.abs(x - 50) <= 2 * CLEARANCE, x + " moved too far from 50");
		}
	}

	private static List<XPoint2D> route(List<RectangleArea> obstacles, XPoint2D src, XPoint2D dst) {
		final List<XPoint2D> tips = Arrays.asList(src, dst);
		final VisGraph graph = new VisGraphBuilder(OrthoRouteEngine.clearTips(obstacles, tips), tips, null, CLEARANCE)
				.build();
		return new AStarRouter(graph).findPath(src, true, dst);
	}

	private static void assertOrthogonal(List<XPoint2D> path) {
		for (int i = 0; i + 1 < path.size(); i++) {
			final XPoint2D a = path.get(i);
			final XPoint2D b = path.get(i + 1);
			assertTrue(Math.abs(a.getX() - b.getX()) < EPSILON || Math.abs(a.getY() - b.getY()) < EPSILON,
					"diagonal segment " + a + " - " + b);
		}
	}

	/** No segment enters the inside of the rectangle (running along its border is allowed). */
	private static void assertAvoids(List<XPoint2D> path, RectangleArea rect) {
		for (int i = 0; i + 1 < path.size(); i++) {
			final XPoint2D a = path.get(i);
			final XPoint2D b = path.get(i + 1);
			final boolean crosses = Math.max(a.getX(), b.getX()) > rect.getMinX() + EPSILON
					&& Math.min(a.getX(), b.getX()) < rect.getMaxX() - EPSILON
					&& Math.max(a.getY(), b.getY()) > rect.getMinY() + EPSILON
					&& Math.min(a.getY(), b.getY()) < rect.getMaxY() - EPSILON;
			assertFalse(crosses, "segment " + a + " - " + b + " goes through " + rect);
		}
	}

	private static List<XPoint2D> points(double... xy) {
		final List<XPoint2D> result = new ArrayList<>();
		for (int i = 0; i + 1 < xy.length; i += 2)
			result.add(new XPoint2D(xy[i], xy[i + 1]));
		return result;
	}

	private static List<Double> boxed(double[] values) {
		final List<Double> result = new ArrayList<>();
		for (double v : values)
			result.add(v);
		return result;
	}

}
