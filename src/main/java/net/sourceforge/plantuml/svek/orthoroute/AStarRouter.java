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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import net.sourceforge.plantuml.klimt.geom.XPoint2D;

/**
 * A* pathfinder over an orthogonal visibility graph. The cost
 * function minimises a weighted combination of path length and
 * number of bends (direction changes).
 *
 * <p>Based on Wybrow/Marriott/Stuckey, GD 2009, Section 3.</p>
 */
public final class AStarRouter {

	private final VisGraph graph;
	private final double lengthWeight;
	private final double bendWeight;

	public AStarRouter(VisGraph graph, double lengthWeight,
			double bendWeight) {
		this.graph = graph;
		this.lengthWeight = lengthWeight;
		this.bendWeight = bendWeight;
	}

	public AStarRouter(VisGraph graph) {
		this(graph, 1.0, 50.0);
	}

	/**
	 * Find the optimal orthogonal path from source to dest.
	 *
	 * @param source          start point (must be a node in the graph)
	 * @param sourceHorizontal direction of the incoming stub
	 *                         (true if arriving horizontally)
	 * @param dest            end point (must be a node in the graph)
	 * @return ordered list of waypoints, or empty list if no path
	 */
	public List<XPoint2D> findPath(XPoint2D source,
			boolean sourceHorizontal, XPoint2D dest) {
		final VisNode srcNode = graph.getNode(source);
		final VisNode dstNode = graph.getNode(dest);
		if (srcNode == null || dstNode == null)
			return Collections.emptyList();

		final PriorityQueue<Entry> open = new PriorityQueue<Entry>(
				16, new Comparator<Entry>() {
					public int compare(Entry a, Entry b) {
						return Double.compare(a.fCost, b.fCost);
					}
				});
		final Set<AStarState> closed = new HashSet<AStarState>();

		// Seed with both possible initial directions from source
		final AStarState startH = new AStarState(srcNode, true);
		final AStarState startV = new AStarState(srcNode, false);
		final double hSrc = heuristic(srcNode, dstNode);
		// If source stub is horizontal, starting with a vertical
		// edge is a bend; starting horizontal is free
		final double bendH = sourceHorizontal ? 0 : bendWeight;
		final double bendV = sourceHorizontal ? bendWeight : 0;
		open.add(new Entry(startH, bendH, bendH + hSrc, null));
		open.add(new Entry(startV, bendV, bendV + hSrc, null));

		while (open.isEmpty() == false) {
			final Entry current = open.poll();
			if (closed.contains(current.state))
				continue;
			closed.add(current.state);

			if (current.state.getNode().equals(dstNode))
				return reconstructPath(current);

			for (VisEdge edge : graph.getNeighbors(
					current.state.getNode())) {
				final VisNode neighbor = edge.getOther(
						current.state.getNode());
				final boolean edgeHorizontal = edge.isHorizontal();
				final boolean bend = edgeHorizontal
						!= current.state.isLastHorizontal();
				final double edgeCost = lengthWeight * edge.getLength()
						+ (bend ? bendWeight : 0);
				final double newG = current.gCost + edgeCost;
				final AStarState nextState = new AStarState(
						neighbor, edgeHorizontal);
				if (closed.contains(nextState))
					continue;
				final double h = heuristic(neighbor, dstNode);
				open.add(new Entry(nextState, newG,
						newG + h, current));
			}
		}

		return Collections.emptyList();
	}

	private double heuristic(VisNode from, VisNode to) {
		final double dx = Math.abs(to.getX() - from.getX());
		final double dy = Math.abs(to.getY() - from.getY());
		return lengthWeight * (dx + dy);
	}

	private List<XPoint2D> reconstructPath(Entry goal) {
		final List<XPoint2D> path = new ArrayList<XPoint2D>();
		Entry current = goal;
		while (current != null) {
			final XPoint2D pt = current.state.getNode().getPoint();
			if (path.isEmpty() == false) {
				final XPoint2D last = path.get(path.size() - 1);
				if (Math.abs(last.getX() - pt.getX()) < 0.1
						&& Math.abs(last.getY() - pt.getY()) < 0.1) {
					current = current.parent;
					continue;
				}
			}
			path.add(pt);
			current = current.parent;
		}
		Collections.reverse(path);
		return path;
	}

	private static final class Entry {
		final AStarState state;
		final double gCost;
		final double fCost;
		final Entry parent;

		Entry(AStarState state, double gCost, double fCost,
				Entry parent) {
			this.state = state;
			this.gCost = gCost;
			this.fCost = fCost;
			this.parent = parent;
		}
	}
}
