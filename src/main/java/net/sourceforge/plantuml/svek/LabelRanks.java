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
 * Original Author:  Arnaud Roques
 *
 *
 */
package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.abel.LeafType;

/**
 * Finds node-style edge labels that Graphviz ranked outside the two nodes they
 * join.
 * <p>
 * A node-style label splits {@code A --> B : text} into {@code A -> label} and
 * {@code label -> B}, each with {@code minlen=1}: the label always takes the
 * rank after {@code A}. When Graphviz then puts {@code B} above {@code A}, the
 * label ends up below both and the edge has to come back up past {@code A}.
 * Given to Graphviz the other way round, the label is ranked between them.
 * <p>
 * This works on the SVG straight from Graphviz, before any node is moved, so
 * a misplaced label can be found and the layout redone from scratch.
 */
final class LabelRanks {

	/** How far apart, on the rank axis, the two nodes must be. */
	private static final double MIN_SPAN = 1;

	/** How far outside the two nodes a label must be to count as misplaced. */
	private static final double MARGIN = 1;

	private static final Pattern PAIR = Pattern.compile("(-?\\d+(?:\\.\\d+)?),(-?\\d+(?:\\.\\d+)?)");
	private static final Pattern CX = Pattern.compile("\\bcx=\"(-?\\d+(?:\\.\\d+)?)\"");
	private static final Pattern CY = Pattern.compile("\\bcy=\"(-?\\d+(?:\\.\\d+)?)\"");

	private LabelRanks() {
	}

	/**
	 * Both edges of every label of {@code bibliotekon} that {@code svg} places
	 * outside the two nodes it joins. The rank axis is y, or x when
	 * {@code leftToRight}.
	 */
	static List<SvekEdge> misplaced(String svg, Bibliotekon bibliotekon, boolean leftToRight) {
		final List<SvekEdge> result = new ArrayList<>();
		for (SvekNode label : bibliotekon.allNodes()) {
			if (label.getLeafType() != LeafType.STATE_TRANSITION_LABEL)
				continue;

			final List<SvekEdge> edges = new ArrayList<>();
			final List<SvekNode> ends = new ArrayList<>();
			for (SvekEdge edge : bibliotekon.allLines()) {
				final SvekNode node1 = edge.getSvekNode1();
				final SvekNode node2 = edge.getSvekNode2();
				if (node1 == label && node2 != label) {
					edges.add(edge);
					ends.add(node2);
				} else if (node2 == label && node1 != label) {
					edges.add(edge);
					ends.add(node1);
				}
			}
			if (edges.size() != 2 || ends.get(0) == null || ends.get(1) == null || ends.get(0) == ends.get(1))
				continue;

			final double at = rank(svg, label, leftToRight);
			final double a = rank(svg, ends.get(0), leftToRight);
			final double b = rank(svg, ends.get(1), leftToRight);
			if (Double.isNaN(at) || Double.isNaN(a) || Double.isNaN(b))
				continue;

			final double min = Math.min(a, b);
			final double max = Math.max(a, b);
			if (max - min < MIN_SPAN)
				continue;

			if (at < min - MARGIN || at > max + MARGIN)
				result.addAll(edges);
		}
		return result;
	}

	/**
	 * The centre of {@code node} on the rank axis, from the shapes Graphviz drew
	 * for it; NaN if it drew none.
	 */
	private static double rank(String svg, SvekNode node, boolean leftToRight) {
		final int start = svg.indexOf("<title>" + node.getUid() + "</title>");
		if (start == -1)
			return Double.NaN;

		int end = svg.indexOf("</g>", start);
		if (end == -1)
			end = svg.length();

		final String shapes = svg.substring(start, end);
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		final Matcher pair = PAIR.matcher(shapes);
		while (pair.find()) {
			final double v = Double.parseDouble(pair.group(leftToRight ? 1 : 2));
			min = Math.min(min, v);
			max = Math.max(max, v);
		}
		final Matcher center = (leftToRight ? CX : CY).matcher(shapes);
		while (center.find()) {
			final double v = Double.parseDouble(center.group(1));
			min = Math.min(min, v);
			max = Math.max(max, v);
		}
		if (min > max)
			return Double.NaN;

		return (min + max) / 2;
	}

}
