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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.abel.Harness;
import net.sourceforge.plantuml.annotation.Fast;
import net.sourceforge.plantuml.annotation.PerformanceIssue;
import net.sourceforge.plantuml.dot.DotData;
import net.sourceforge.plantuml.dot.DotSplines;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.MinMax;
import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.klimt.shape.UHidden;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.StyleSignatureBasic;

public final class SvekResult implements IEntityImage {

	private final DotData dotData;
	private final DotStringFactory clusterManager;

	public SvekResult(DotData dotData, DotStringFactory clusterManager) {
		this.dotData = dotData;
		this.clusterManager = clusterManager;
	}

	public void drawU(UGraphic ug) {

		for (Cluster cluster : clusterManager.getBibliotekon().allCluster())
			if (cluster.getGroup().isPacked() == false)
				cluster.drawU(ug);

		final Style style2 = getDefaultStyleDefinition(null)
				.getMergedStyle(dotData.getSkinParam().getCurrentStyleBuilder());

		final HColor borderColor = HColors
				.noGradient(style2.value(PName.LineColor).asColor(dotData.getSkinParam().getIHtmlColorSet()));

		for (SvekNode node : clusterManager.getBibliotekon().allNodes()) {
			final double minX = node.getMinX();
			final double minY = node.getMinY();
			final UGraphic ug2 = node.isHidden() ? ug.apply(UHidden.HIDDEN) : ug;
			final IEntityImage image = node.getImage();
			image.drawU(ug2.apply(new UTranslate(minX, minY)));
			if (image instanceof Untranslated)
				((Untranslated) image).drawUntranslated(ug.apply(borderColor), minX, minY);

		}

		final Set<String> ids = new HashSet<>();

		computeKal();

		clusterManager.getBibliotekon().clearPlacedSegments();
		clusterManager.getBibliotekon().clearBlockedShifts();
		final Map<Harness, List<SvekEdge>> harnessMap = new LinkedHashMap<Harness, List<SvekEdge>>();
		for (SvekEdge svekEdge : clusterManager.getBibliotekon().allLines()) {
			final UGraphic ug2 = svekEdge.isHidden() ? ug.apply(UHidden.HIDDEN) : ug;
			svekEdge.setSharedIds(ids);
			svekEdge.drawU(ug2);

			final Harness h = svekEdge.getLink().getHarness();
			if (h != null) {
				List<SvekEdge> list = harnessMap.get(h);
				if (list == null) {
					list = new ArrayList<SvekEdge>();
					harnessMap.put(h, list);
				}
				list.add(svekEdge);
			}
		}

		for (Map.Entry<Harness, List<SvekEdge>> entry : harnessMap.entrySet()) {
			final SvekHarness svekHarness = new SvekHarness(entry.getKey(), entry.getValue(),
					dotData.getSkinParam(), clusterManager.getBibliotekon());
			svekHarness.drawU(ug);
		}
	}

	private void computeKal() {
		for (SvekEdge line : clusterManager.getBibliotekon().allLines())
			line.computeKal();
		for (SvekNode node : clusterManager.getBibliotekon().allNodes())
			node.fixOverlap();
	}

	private StyleSignature getDefaultStyleDefinition(Stereotype stereotype) {
		StyleSignature result = StyleSignatureBasic.of(SName.root, SName.element,
				dotData.geDiagramType().getStyleName(), SName.arrow);

		return result.withTOBECHANGED(stereotype);
	}

	// Duplicate SvekResult / GeneralImageBuilder
	public HColor getBackcolor() {
		final Style style = StyleSignatureBasic.of(SName.root, SName.document)
				.getMergedStyle(dotData.getSkinParam().getCurrentStyleBuilder());
		return style.value(PName.BackGroundColor).asColor(dotData.getSkinParam().getIHtmlColorSet());
	}

	private MinMax minMax;
	private int clusterSpacingPasses;

	@PerformanceIssue
	@Fast
	@Override
	public XDimension2D calculateDimension(StringBounder stringBounder) {
		if (minMax == null) {
			minMax = TextBlockUtils.getMinMax(this, stringBounder, false);
			clusterManager.moveDelta(6 - minMax.getMinX(), 6 - minMax.getMinY());
			if (clusterSpacingPasses < 3 && adjustClusterSpacingIfNeeded()) {
				clusterSpacingPasses++;
				minMax = null;
				return calculateDimension(stringBounder);
			}
		}
		return minMax.getDimension().delta(15, 15);
	}

	/**
	 * After the first layout pass, check for blocked segment shifts that need
	 * more cluster spacing. Applies the largest needed shift per cluster.
	 * Returns true if adjustments were made.
	 */
	private boolean adjustClusterSpacingIfNeeded() {
		if (dotData.getSkinParam().getDotSplines() != DotSplines.ORTHO)
			return false;
		final Bibliotekon bib = clusterManager.getBibliotekon();
		final List<double[]> blocked = bib.getBlockedShifts();
		if (blocked.isEmpty())
			return false;
		// Aggregate: for each cluster, find the max absolute shift needed per axis
		final List<Cluster> clusters = bib.allCluster();
		final double[] maxDx = new double[clusters.size()];
		final double[] maxDy = new double[clusters.size()];
		for (double[] bs : blocked) {
			final int idx = (int) bs[0];
			if (idx < 0 || idx >= clusters.size())
				continue;
			if (Math.abs(bs[1]) > Math.abs(maxDx[idx]))
				maxDx[idx] = bs[1];
			if (Math.abs(bs[2]) > Math.abs(maxDy[idx]))
				maxDy[idx] = bs[2];
		}
		boolean adjusted = false;
		for (int i = 0; i < clusters.size(); i++) {
			final double dx = maxDx[i];
			final double dy = maxDy[i];
			if (Math.abs(dx) < 0.1 && Math.abs(dy) < 0.1)
				continue;
			final Cluster target = clusters.get(i);
			final RectangleArea targetRect = target.getRectangleArea();
			if (targetRect == null)
				continue;
			// Push the target cluster and all clusters further in the shift direction
			final double targetCenter = dx > 0
					? (targetRect.getMinX() + targetRect.getMaxX()) / 2.0
					: dy > 0 ? (targetRect.getMinY() + targetRect.getMaxY()) / 2.0 : 0;
			for (Cluster cl : clusters) {
				final RectangleArea rect = cl.getRectangleArea();
				if (rect == null)
					continue;
				final double clCenter = dx != 0
						? (rect.getMinX() + rect.getMaxX()) / 2.0
						: (rect.getMinY() + rect.getMaxY()) / 2.0;
				if (clCenter >= targetCenter - 1) {
					cl.moveDelta(dx, dy);
					for (SvekNode node : cl.getNodes())
						node.moveDelta(dx, dy);
				}
			}
			adjusted = true;
		}
		bib.clearBlockedShifts();
		return adjusted;
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}

	public boolean isHidden() {
		return false;
	}

	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}

}
