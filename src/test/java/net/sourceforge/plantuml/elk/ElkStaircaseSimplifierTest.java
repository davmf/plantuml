package net.sourceforge.plantuml.elk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.klimt.geom.XPoint2D;

class ElkStaircaseSimplifierTest {

	// ELK's routes for three wires climbing from the AD5941 MCU Board to the
	// Interface Board: a crossing-free staircase through two gaps each.
	private static final List<XPoint2D> PA6 = path(193, 414, 324, 414, 324, 328, 394, 328, 394, 241, 572, 241);
	private static final List<XPoint2D> PA7 = path(193, 449, 334, 449, 334, 384, 404, 384, 404, 319, 572, 319);
	private static final List<XPoint2D> PD14 = path(193, 484, 344, 484, 344, 441, 384, 441, 384, 397, 572, 397);

	@Test
	void a_lone_staircase_turns_on_its_longest_column() {
		final List<ElkStaircaseSimplifier.Collapse> plan = ElkStaircaseSimplifier.plan(Arrays.asList(PD14));
		assertEquals(path(193, 484, 384, 484, 384, 397, 572, 397), plan.get(0).apply(PD14));
	}

	@Test
	void collapsing_neighbours_adds_no_crossing() {
		final List<List<XPoint2D>> wires = Arrays.asList(PA6, PA7, PD14);
		final List<ElkStaircaseSimplifier.Collapse> plan = ElkStaircaseSimplifier.plan(wires);
		final List<List<XPoint2D>> drawn = new ArrayList<List<XPoint2D>>();
		for (int i = 0; i < wires.size(); i++) {
			assertNotNull(plan.get(i), "wire " + i + " keeps its staircase");
			drawn.add(plan.get(i).apply(wires.get(i)));
		}
		for (int i = 0; i < drawn.size(); i++)
			for (int j = i + 1; j < drawn.size(); j++)
				assertEquals(0, ElkStaircaseSimplifier.conflicts(drawn.get(i), drawn.get(j)),
						"wires " + i + " and " + j + ": " + drawn.get(i) + " / " + drawn.get(j));
	}

	@Test
	void a_path_that_turns_back_is_left_alone() {
		final List<XPoint2D> detour = path(0, 0, 50, 0, 50, -40, 100, -40, 100, 20, 150, 20);
		assertNull(ElkStaircaseSimplifier.plan(Arrays.asList(detour)).get(0));
	}

	private static List<XPoint2D> path(double... xy) {
		final List<XPoint2D> result = new ArrayList<XPoint2D>();
		for (int i = 0; i + 1 < xy.length; i += 2)
			result.add(new XPoint2D(xy[i], xy[i + 1]));
		return result;
	}

}
