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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.klimt.font.StringBounder;

public class Bibliotekon {

	private final List<Cluster> allCluster = new ArrayList<>();

	private final Map<Entity, SvekNode> nodeMap = new LinkedHashMap<Entity, SvekNode>();

	private final List<SvekEdge> lines0 = new ArrayList<>();
	private final List<SvekEdge> lines1 = new ArrayList<>();
	private final List<SvekEdge> allLines = new ArrayList<>();

	private final Collection<Link> links;
	private final ColorSequence colorSequence;

	// Registry of placed ortho segments for overlap detection
	// Each entry: [isHorizontal(0/1), fixedCoord, rangeMin, rangeMax]
	private final List<double[]> placedSegments = new ArrayList<>();

	// Records of blocked segment shifts needing more cluster spacing.
	// Each entry: [clusterIndex, neededShiftX, neededShiftY]
	// clusterIndex is the index in allCluster of the cluster that needs to move.
	private final List<double[]> blockedShifts = new ArrayList<>();

	public void recordBlockedShift(Cluster cluster, double neededDx, double neededDy) {
		final int idx = allCluster.indexOf(cluster);
		if (idx >= 0)
			blockedShifts.add(new double[] { idx, neededDx, neededDy });
	}

	public List<double[]> getBlockedShifts() {
		return Collections.unmodifiableList(blockedShifts);
	}

	public void clearBlockedShifts() {
		blockedShifts.clear();
	}

	public void registerSegment(boolean horizontal, double fixedCoord, double rangeMin, double rangeMax) {
		placedSegments.add(new double[] { horizontal ? 1 : 0, fixedCoord, rangeMin, rangeMax });
	}

	public void clearPlacedSegments() {
		placedSegments.clear();
	}

	/**
	 * Find the best position for a segment that avoids spacing violations.
	 * Tries pushing in the preferred direction first, then opposite if needed.
	 * Returns the adjusted coordinate, or the original if no violation.
	 */
	public double findNonOverlappingPosition(boolean horizontal, double fixedCoord, double rangeMin, double rangeMax,
			double minSpacing, boolean preferPositive) {
		double candidate = fixedCoord;
		// Try up to 20 iterations to find a clear position
		for (int iter = 0; iter < 20; iter++) {
			boolean violation = false;
			for (double[] seg : placedSegments) {
				if ((seg[0] > 0.5) != horizontal)
					continue;
				final double dist = Math.abs(candidate - seg[1]);
				if (dist >= minSpacing)
					continue;
				final double overlapMin = Math.max(rangeMin, seg[2]);
				final double overlapMax = Math.min(rangeMax, seg[3]);
				if (overlapMax <= overlapMin + 1)
					continue;
				// Push away from this segment in the preferred direction
				if (preferPositive)
					candidate = seg[1] + minSpacing;
				else
					candidate = seg[1] - minSpacing;
				violation = true;
				break;
			}
			if (!violation)
				return candidate;
		}
		return candidate;
	}

	/**
	 * A mutable record representing one axis-aligned segment of an ortho edge.
	 */
	static class OrthoSegment {
		final SvekEdge edge;
		final int bezierIndex;
		final boolean horizontal;
		double fixedCoord;
		double rangeMin;
		double rangeMax;

		OrthoSegment(SvekEdge edge, int bezierIndex, boolean horizontal,
				double fixedCoord, double rangeMin, double rangeMax) {
			this.edge = edge;
			this.bezierIndex = bezierIndex;
			this.horizontal = horizontal;
			this.fixedCoord = fixedCoord;
			this.rangeMin = rangeMin;
			this.rangeMax = rangeMax;
		}
	}

	/**
	 * Iteratively push apart parallel segments that are too close and have
	 * overlapping ranges, using symmetric displacement. Modifies fixedCoord
	 * in-place on each OrthoSegment.
	 */
	public void separateParallelSegments(List<OrthoSegment> allSegments, double minSpacing) {
		final int maxIterations = 10;
		for (int iter = 0; iter < maxIterations; iter++) {
			boolean changed = false;
			for (int orientation = 0; orientation < 2; orientation++) {
				final boolean horiz = (orientation == 0);
				final List<OrthoSegment> group = new ArrayList<>();
				for (OrthoSegment seg : allSegments)
					if (seg.horizontal == horiz)
						group.add(seg);

				Collections.sort(group, new Comparator<OrthoSegment>() {
					public int compare(OrthoSegment a, OrthoSegment b) {
						return Double.compare(a.fixedCoord, b.fixedCoord);
					}
				});
				for (int i = 0; i < group.size(); i++) {
					final OrthoSegment si = group.get(i);
					for (int j = i + 1; j < group.size(); j++) {
						final OrthoSegment sj = group.get(j);
						final double gap = sj.fixedCoord - si.fixedCoord;
						if (gap >= minSpacing)
							break;
						final double overlapMin = Math.max(si.rangeMin, sj.rangeMin);
						final double overlapMax = Math.min(si.rangeMax, sj.rangeMax);
						if (overlapMax <= overlapMin + 1)
							continue;
						final double delta = (minSpacing - gap) / 2.0;
						si.fixedCoord -= delta;
						sj.fixedCoord += delta;
						changed = true;
					}
				}
			}
			if (changed == false)
				break;
		}
	}

	public Bibliotekon(Collection<Link> links) {
		this.links = links;
		this.colorSequence = new ColorSequence();
	}

	public ColorSequence getColorSequence() {
		return colorSequence;
	}

	public SvekNode createNode(Entity ent, IEntityImage image, StringBounder stringBounder) {
		final SvekNode node = new SvekNode(ent, image, colorSequence, stringBounder);
		nodeMap.put(ent, node);
		// System.err.println("createNode " + ent + " " + nodeMap.size());
		return node;
	}

	public Cluster getCluster(Entity ent) {
		for (Cluster cl : allCluster)
			if (cl.getGroups().contains(ent))
				return cl;

		return null;
	}

	public void addLine(SvekEdge line) {
		allLines.add(line);
		if (first(line)) {
			if (line.hasNoteLabelText()) {
				// lines0.add(0, line);
				for (int i = 0; i < lines0.size(); i++) {
					final SvekEdge other = lines0.get(i);
					if (other.hasNoteLabelText() == false && line.sameConnections(other)) {
						lines0.add(i, line);
						return;
					}
				}
				lines0.add(line);
			} else {
				lines0.add(line);
			}
		} else {
			lines1.add(line);
		}
	}

	private static boolean first(SvekEdge line) {
		final int length = line.getLength();
		if (length == 1)
			return true;

		return false;
	}

	public void addCluster(Cluster current) {
		allCluster.add(current);
	}

	public SvekNode getNode(Entity ent) {
		return nodeMap.get(ent);
	}

	public String getNodeUid(Entity ent) {
		// System.err.println("Getting for " + ent);
		final SvekNode result = getNode(ent);
		if (result != null) {
			String uid = result.getUid();
			if (result.isShielded())
				uid = uid + ":h";

			return uid;
		}
		if (ent.isGroup())
			return Cluster.getSpecialPointId(ent);

		throw new IllegalStateException();
	}

	public String getWarningOrError(int warningOrError) {
		final StringBuilder sb = new StringBuilder();
		for (Map.Entry<Entity, SvekNode> ent : nodeMap.entrySet()) {
			final SvekNode sh = ent.getValue();
			final double maxX = sh.getMinX() + sh.getWidth();
			if (maxX > warningOrError) {
				final Entity entity = ent.getKey();
				sb.append(entity.getName() + " is overpassing the width limit.");
				sb.append("\n");
			}

		}
		return sb.length() == 0 ? "" : sb.toString();
	}

	public Map<String, Double> getMaxX() {
		final Map<String, Double> result = new HashMap<String, Double>();
		for (Map.Entry<Entity, SvekNode> ent : nodeMap.entrySet()) {
			final SvekNode sh = ent.getValue();
			final double maxX = sh.getMinX() + sh.getWidth();
			final Entity entity = ent.getKey();
			result.put(entity.getName(), maxX);
		}
		return Collections.unmodifiableMap(result);
	}

	public List<SvekEdge> allLines() {
		return Collections.unmodifiableList(allLines);
	}

	public List<SvekEdge> lines0() {
		return Collections.unmodifiableList(lines0);
	}

	public List<SvekEdge> lines1() {
		return Collections.unmodifiableList(lines1);
	}

	public List<Cluster> allCluster() {
		return Collections.unmodifiableList(allCluster);
	}

	public Collection<SvekNode> allNodes() {
		return Collections.unmodifiableCollection(nodeMap.values());
	}

	public List<SvekEdge> getAllLineConnectedTo(Entity leaf) {
		final List<SvekEdge> result = new ArrayList<>();
		for (SvekEdge line : allLines)
			if (line.isLinkFromOrTo(leaf))
				result.add(line);

		return Collections.unmodifiableList(result);
	}

	public SvekEdge getLine(Link link) {
		for (SvekEdge line : allLines)
			if (line.isLink(link))
				return line;

		throw new IllegalArgumentException();
	}

	public Entity getOnlyOther(Entity entity) {
		for (Link link : links)
			if (link.contains(entity)) {
				final Entity other = link.getOther(entity);
				if (other != null)
					return other;

			}
		return null;
	}

	public Entity getLeaf(SvekNode node) {
		for (Map.Entry<Entity, SvekNode> ent : nodeMap.entrySet())
			if (ent.getValue() == node)
				return ent.getKey();

		throw new IllegalArgumentException();
	}
}
