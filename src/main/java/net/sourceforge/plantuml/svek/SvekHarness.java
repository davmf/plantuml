package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Harness;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.Colors;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XCubicCurve2D;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.skin.PragmaKey;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.utils.Log;

public class SvekHarness implements UDrawable {

	private static final double TRUNK_STROKE_WIDTH = 4.5;
	private static final double FAN_GAP = 20.0;
	private static final double LABEL_GAP = 3.0;
	private static final double PORT_RADIUS = 6.0;
	private static final int ARROW_WING = 9;
	private static final int ARROW_APERTURE = 4;
	private static final int ARROW_CONTACT = 5;

	private static final double PORT_WIDTH = 2 * PORT_RADIUS;
	private static final double MIN_STUB_LENGTH = 5 * PORT_WIDTH;
	private static final double LABEL_SCALE = 0.8;

	private final Harness harness;
	private final List<Link> memberLinks;
	private final ISkinParam skinParam;
	private final Bibliotekon bibliotekon;
	private double spineXOffset;
	private double dualSpine1XOffset;
	private double dualSpine2XOffset;
	private boolean willUseDualSpine;
	private double cachedNaturalSpine1X = Double.NaN;
	private double cachedNaturalSpine2X = Double.NaN;
	private double cachedSpine1Top, cachedSpine1Bottom;
	private double cachedSpine2Top, cachedSpine2Bottom;
	private List<double[]> nonHarnessVerts;
	private double cachedSrcX = Double.NaN;
	private double cachedDestX = Double.NaN;
	private final List<RectangleArea> renderedSpines = new ArrayList<RectangleArea>();

	public SvekHarness(Harness harness, List<Link> memberLinks, ISkinParam skinParam,
			Bibliotekon bibliotekon) {
		this.harness = harness;
		this.memberLinks = memberLinks;
		this.skinParam = skinParam;
		this.bibliotekon = bibliotekon;
	}

	public List<RectangleArea> getRenderedSpines() {
		return Collections.unmodifiableList(renderedSpines);
	}

	// nonHarnessVerts: pre-computed list of {x, yMin, yMax} segments that
	// must be avoided. Svek callers harvest these from rendered SvekEdge
	// paths; ELK callers can pass an empty list (overlap resolution is not
	// yet wired for ELK).
	//
	// Each harness's spine is iteratively re-placed so it stays at least
	// PORT_WIDTH away from every non-harness vertical AND from every other
	// harness's spine whose Y range overlaps. Iterating to convergence
	// avoids the previous two-pass bug where the inter-harness separation
	// could push a spine back into a non-harness conflict zone.
	public static void resolveOverlaps(List<SvekHarness> harnesses,
			List<double[]> nonHarnessVerts) {
		final List<double[]> spines = new ArrayList<double[]>();
		for (SvekHarness h : harnesses) {
			spines.add(h.computeNaturalSpineX());
			h.nonHarnessVerts = nonHarnessVerts;
		}

		if (harnesses.isEmpty())
			return;

		final int maxIterations = 8;
		for (int iter = 0; iter < maxIterations; iter++) {
			boolean stable = true;
			for (int i = 0; i < harnesses.size(); i++) {
				final double[] spine = spines.get(i);
				if (Double.isNaN(spine[0]))
					continue;
				final SvekHarness h = harnesses.get(i);
				final double naturalX = spine[0];
				final double topY = spine[1];
				final double bottomY = spine[2];

				// Collect X positions of conflicting vertical segments.
				// Conflicts are non-harness verts AND every OTHER harness's
				// spine (using its current offset) whose Y range overlaps.
				final List<Double> conflictXs = new ArrayList<Double>();
				for (double[] seg : nonHarnessVerts) {
					final double overlapMin = Math.max(topY, seg[1]);
					final double overlapMax = Math.min(bottomY, seg[2]);
					if (overlapMax > overlapMin + 1)
						conflictXs.add(seg[0]);
				}
				for (int j = 0; j < harnesses.size(); j++) {
					if (j == i)
						continue;
					final double[] other = spines.get(j);
					if (Double.isNaN(other[0]))
						continue;
					final double overlapMin = Math.max(topY, other[1]);
					final double overlapMax = Math.min(bottomY, other[2]);
					if (overlapMax > overlapMin + 1)
						conflictXs.add(other[0] + harnesses.get(j).spineXOffset);
				}

				final double oldOffset = h.spineXOffset;
				if (conflictXs.isEmpty()) {
					if (Math.abs(oldOffset) > 0.5)
						stable = false;
					h.spineXOffset = 0;
					continue;
				}

				// If naturalX is already clear, keep offset at 0.
				boolean clear = true;
				for (double cx : conflictXs)
					if (Math.abs(naturalX - cx) < PORT_WIDTH)
						clear = false;
				if (clear) {
					if (Math.abs(oldOffset) > 0.5)
						stable = false;
					h.spineXOffset = 0;
					continue;
				}

				java.util.Collections.sort(conflictXs);
				// Build the list of feasible clear positions: just beyond the
				// leftmost conflict, just beyond the rightmost, and the
				// centre of any wide-enough gap between consecutive conflicts.
				final List<Double> candidates = new ArrayList<Double>();
				candidates.add(conflictXs.get(0) - PORT_WIDTH);
				candidates.add(conflictXs.get(conflictXs.size() - 1) + PORT_WIDTH);
				for (int g = 0; g < conflictXs.size() - 1; g++) {
					final double gapWidth = conflictXs.get(g + 1) - conflictXs.get(g);
					if (gapWidth >= PORT_WIDTH * 2)
						candidates.add((conflictXs.get(g) + conflictXs.get(g + 1)) / 2);
				}
				// Prefer the most destination-ward candidate that stays within
				// the harness's allowed band [minSpineX, maxSpineX]. Falls
				// back to the nearest candidate when no in-band one exists.
				final int destDir = h.destinationDirection();
				final double bandMin = h.minSpineX();
				final double bandMax = h.maxSpineX();
				double bestX = naturalX;
				double bestScore = -Double.MAX_VALUE;
				double fallbackX = naturalX;
				double fallbackDist = Double.MAX_VALUE;
				for (double c : candidates) {
					final double dist = Math.abs(c - naturalX);
					if (dist < fallbackDist) {
						fallbackDist = dist;
						fallbackX = c;
					}
					if (c >= bandMin - 0.5 && c <= bandMax + 0.5) {
						final double score = (destDir == 0)
								? -dist
								: destDir * c;
						if (score > bestScore) {
							bestScore = score;
							bestX = c;
						}
					}
				}
				if (bestScore == -Double.MAX_VALUE)
					bestX = fallbackX;
				boolean stillConflicting = false;
				for (double cx : conflictXs) {
					if (Math.abs(bestX - cx) < PORT_WIDTH) {
						stillConflicting = true;
						break;
					}
				}
				if (stillConflicting && iter == maxIterations - 1)
					Log.error("Harness '"
							+ h.harness.getLabel()
							+ "' spine cannot achieve minimum clearance ("
							+ PORT_WIDTH
							+ "px) from adjacent connectors. "
							+ "Consider increasing ranksep to provide more space.");
				final double newOffset = bestX - naturalX;
				if (Math.abs(newOffset - oldOffset) > 0.5)
					stable = false;
				h.spineXOffset = newOffset;
			}
			if (stable)
				break;
		}
		// After single-flow harnesses have settled, do a parallel pass for
		// dual-spine harnesses. Each dual-spine harness contributes TWO
		// spines (source-side and dest-side) and both must stay clear of
		// non-harness verticals AND of every other harness spine (single
		// or dual) that overlaps in Y.
		resolveDualSpineOverlaps(harnesses, nonHarnessVerts);
	}

	// Iteratively offset each dual-spine harness's spine1 and spine2 so
	// that they maintain PORT_WIDTH clearance from non-harness verticals,
	// from single-flow harness spines (using their resolved spineXOffset),
	// and from every other dual-spine slot.
	private static void resolveDualSpineOverlaps(List<SvekHarness> harnesses,
			List<double[]> nonHarnessVerts) {
		final List<SvekHarness> duals = new ArrayList<SvekHarness>();
		for (SvekHarness h : harnesses)
			if (h.willUseDualSpine)
				duals.add(h);
		if (duals.isEmpty())
			return;

		final int n = duals.size() * 2;
		final double[] currentX = new double[n];
		final double[] naturalX = new double[n];
		final double[] topY = new double[n];
		final double[] botY = new double[n];
		for (int i = 0; i < duals.size(); i++) {
			final SvekHarness h = duals.get(i);
			final int s1 = 2 * i;
			final int s2 = 2 * i + 1;
			naturalX[s1] = currentX[s1] = h.cachedNaturalSpine1X;
			topY[s1] = h.cachedSpine1Top;
			botY[s1] = h.cachedSpine1Bottom;
			naturalX[s2] = currentX[s2] = h.cachedNaturalSpine2X;
			topY[s2] = h.cachedSpine2Top;
			botY[s2] = h.cachedSpine2Bottom;
		}

		// Single-flow harness spines as immovable obstacles.
		final List<double[]> singleFlowSpines = new ArrayList<double[]>();
		for (SvekHarness h : harnesses) {
			if (h.willUseDualSpine)
				continue;
			final double[] nat = h.computeNaturalSpineX();
			if (Double.isNaN(nat[0]))
				continue;
			singleFlowSpines.add(new double[]{nat[0] + h.spineXOffset, nat[1], nat[2]});
		}

		final int maxIterations = 8;
		for (int iter = 0; iter < maxIterations; iter++) {
			boolean stable = true;
			for (int i = 0; i < n; i++) {
				if (Double.isNaN(naturalX[i]))
					continue;
				final double myTop = topY[i];
				final double myBot = botY[i];
				final List<Double> conflictXs = new ArrayList<Double>();
				for (double[] seg : nonHarnessVerts) {
					final double overlapMin = Math.max(myTop, seg[1]);
					final double overlapMax = Math.min(myBot, seg[2]);
					if (overlapMax > overlapMin + 1)
						conflictXs.add(seg[0]);
				}
				for (double[] sf : singleFlowSpines) {
					final double overlapMin = Math.max(myTop, sf[1]);
					final double overlapMax = Math.min(myBot, sf[2]);
					if (overlapMax > overlapMin + 1)
						conflictXs.add(sf[0]);
				}
				for (int j = 0; j < n; j++) {
					if (i == j)
						continue;
					if (Double.isNaN(naturalX[j]))
						continue;
					final double overlapMin = Math.max(myTop, topY[j]);
					final double overlapMax = Math.min(myBot, botY[j]);
					if (overlapMax > overlapMin + 1)
						conflictXs.add(currentX[j]);
				}

				boolean clear = true;
				for (double cx : conflictXs)
					if (Math.abs(naturalX[i] - cx) < PORT_WIDTH) {
						clear = false;
						break;
					}
				final double oldX = currentX[i];
				if (clear) {
					currentX[i] = naturalX[i];
				} else {
					java.util.Collections.sort(conflictXs);
					double bestX = naturalX[i];
					double bestDist = Double.MAX_VALUE;
					final double below = conflictXs.get(0) - PORT_WIDTH;
					if (Math.abs(below - naturalX[i]) < bestDist) {
						bestX = below;
						bestDist = Math.abs(below - naturalX[i]);
					}
					final double above = conflictXs.get(conflictXs.size() - 1) + PORT_WIDTH;
					if (Math.abs(above - naturalX[i]) < bestDist) {
						bestX = above;
						bestDist = Math.abs(above - naturalX[i]);
					}
					for (int g = 0; g < conflictXs.size() - 1; g++) {
						final double gapCenter = (conflictXs.get(g) + conflictXs.get(g + 1)) / 2;
						final double gapWidth = conflictXs.get(g + 1) - conflictXs.get(g);
						if (gapWidth >= PORT_WIDTH * 2
								&& Math.abs(gapCenter - naturalX[i]) < bestDist) {
							bestX = gapCenter;
							bestDist = Math.abs(gapCenter - naturalX[i]);
						}
					}
					currentX[i] = bestX;
				}
				if (Math.abs(currentX[i] - oldX) > 0.5)
					stable = false;
			}
			if (stable)
				break;
		}

		for (int i = 0; i < duals.size(); i++) {
			final SvekHarness h = duals.get(i);
			h.dualSpine1XOffset = currentX[2 * i] - naturalX[2 * i];
			h.dualSpine2XOffset = currentX[2 * i + 1] - naturalX[2 * i + 1];
		}
	}

	private double[] computeNaturalSpineX() {
		final List<EdgeData> edges = buildEdgeData();
		if (edges.isEmpty())
			return new double[]{Double.NaN, 0, 0};
		final double srcX = medianX(startPointsOf(edges));
		final double dstX = medianX(endPointsOf(edges));
		cachedSrcX = srcX;
		cachedDestX = dstX;
		// Dual-spine harnesses opt out of single-flow conflict resolution
		// (Double.NaN return signals "skip") and instead get their two
		// spines resolved by resolveDualSpineOverlaps after the single-flow
		// pass settles.
		if (shouldUseDualSpine(edges, srcX, dstX)) {
			willUseDualSpine = true;
			final boolean leftToRight = dstX > srcX;
			final double offset = MIN_STUB_LENGTH + PORT_RADIUS;
			cachedNaturalSpine1X = leftToRight ? srcX + offset : srcX - offset;
			cachedNaturalSpine2X = leftToRight ? dstX - offset : dstX + offset;
			double s1Top = edges.get(0).start.getY();
			double s1Bot = s1Top;
			double s2Top = edges.get(0).end.getY();
			double s2Bot = s2Top;
			for (EdgeData e : edges) {
				s1Top = Math.min(s1Top, e.start.getY());
				s1Bot = Math.max(s1Bot, e.start.getY());
				s2Top = Math.min(s2Top, e.end.getY());
				s2Bot = Math.max(s2Bot, e.end.getY());
			}
			// Effective Y range used by conflict resolution. Each spine
			// actually extends from its source/destination row band to
			// the trunk Y (which wraps above all stubs by default). We
			// approximate the wrap-extended range as [allTop - margin,
			// spine.Bottom] so two spines sharing a natural X but with
			// different source/dest Ys get detected as overlapping along
			// the part of their length that runs up to the trunk.
			final double allTopApprox = Math.min(s1Top, s2Top);
			final double trunkMarginApprox =
					Math.max(cornerRadius(), 0) + PORT_RADIUS + PORT_WIDTH;
			final double effectiveTop = allTopApprox - trunkMarginApprox;
			cachedSpine1Top = effectiveTop;
			cachedSpine1Bottom = s1Bot;
			cachedSpine2Top = effectiveTop;
			cachedSpine2Bottom = s2Bot;
			return new double[]{Double.NaN, 0, 0};
		}
		final double spineX = clampSpineForMinStub((srcX + dstX) / 2, edges, srcX);
		double topY = edges.get(0).start.getY();
		double bottomY = topY;
		for (EdgeData e : edges) {
			topY = Math.min(topY, Math.min(e.start.getY(), e.end.getY()));
			bottomY = Math.max(bottomY, Math.max(e.start.getY(), e.end.getY()));
		}
		return new double[]{spineX, topY, bottomY};
	}

	// Sign of (destX - srcX): +1 when destinations sit to the right of the
	// source (typical left-to-right flow), -1 when to the left, 0 when the
	// medians coincide. Used to bias spine placement toward the destination
	// side so harness stubs don't cross intervening non-harness verticals or
	// other harness spines unnecessarily.
	//
	// Only applies to single-source harnesses (every link shares the same
	// entity1). For multi-source harnesses (e.g. MCU GP0..GP7 fanning to
	// 8 channel chip-selects) biasing toward destinations stretches the
	// source-side stubs across the whole diagram. Such harnesses fall back
	// to nearest-clear placement near the natural midpoint.
	private int destinationDirection() {
		if (Double.isNaN(cachedSrcX) || Double.isNaN(cachedDestX))
			return 0;
		if (hasSingleSource() == false)
			return 0;
		final double dx = cachedDestX - cachedSrcX;
		if (dx > 1)
			return 1;
		if (dx < -1)
			return -1;
		return 0;
	}

	private boolean hasSingleSource() {
		if (memberLinks.isEmpty())
			return false;
		final Entity first = memberLinks.get(0).getEntity1();
		for (Link link : memberLinks)
			if (link.getEntity1() != first)
				return false;
		return true;
	}

	// Allowed spine X range respecting MIN_STUB_LENGTH on both sides.
	private double minSpineX() {
		if (Double.isNaN(cachedSrcX) || Double.isNaN(cachedDestX))
			return -Double.MAX_VALUE;
		return Math.min(cachedSrcX, cachedDestX) + MIN_STUB_LENGTH + PORT_RADIUS;
	}

	private double maxSpineX() {
		if (Double.isNaN(cachedSrcX) || Double.isNaN(cachedDestX))
			return Double.MAX_VALUE;
		return Math.max(cachedSrcX, cachedDestX) - MIN_STUB_LENGTH - PORT_RADIUS;
	}

	private List<EdgeData> buildEdgeData() {
		final List<EdgeData> edges = new ArrayList<EdgeData>();
		for (Link link : memberLinks) {
			final XPoint2D startPt = resolveEntityCenter(link.getEntity1());
			final XPoint2D endPt = resolveEntityCenter(link.getEntity2());
			if (startPt == null || endPt == null)
				continue;
			edges.add(new EdgeData(startPt, endPt, link.getLabel(),
					link.getSourceLabel()));
		}
		return edges;
	}

	private static final class EdgeData {
		final XPoint2D start;
		final XPoint2D end;
		final Display label;
		final String sourceLabel;

		EdgeData(XPoint2D start, XPoint2D end, Display label, String sourceLabel) {
			this.start = start;
			this.end = end;
			this.label = label;
			this.sourceLabel = sourceLabel;
		}

		boolean hasLabel() {
			return Display.isNull(label) == false;
		}

		boolean hasSourceLabel() {
			return sourceLabel != null && sourceLabel.isEmpty() == false;
		}
	}

	// Destination stubs prefer the link's own label; if the link has none
	// (typical for harness members declared without an explicit `: label`),
	// fall back to the source-label string so every stub still carries the
	// nested-harness name. Returns null when the link offers neither.
	private Display destinationStubLabel(EdgeData e) {
		if (e.hasLabel())
			return e.label;
		if (e.hasSourceLabel())
			return Display.getWithNewlines(skinParam.getPragma(), e.sourceLabel);
		return null;
	}

	@Override
	public void drawU(UGraphic ug) {
		if (memberLinks.isEmpty())
			return;

		final List<EdgeData> edges = buildEdgeData();
		if (edges.isEmpty())
			return;

		final Style style = StyleSignatureBasic.of(SName.root,
				SName.element, SName.arrow)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		HColor color = style.value(PName.LineColor)
				.asColor(skinParam.getIHtmlColorSet());
		final Colors harnessColors = harness.getRoot().getColors();
		if (harnessColors != null) {
			final HColor direct = harnessColors.getColor(
					ColorType.LINE);
			if (direct != null)
				color = direct;
		}
		final UGraphic ugLine = ug.apply(color)
				.apply(HColors.none().bg());

		// Determine primary flow direction.
		final List<XPoint2D> startPoints = new ArrayList<XPoint2D>();
		final List<XPoint2D> endPoints = new ArrayList<XPoint2D>();
		for (EdgeData e : edges) {
			startPoints.add(e.start);
			endPoints.add(e.end);
		}

		final XPoint2D startMedian = median(startPoints);
		final XPoint2D endMedian = median(endPoints);
		final double flowDx = endMedian.getX() - startMedian.getX();
		final double flowDy = endMedian.getY() - startMedian.getY();

		double srcSpreadX = 0;
		double srcSpreadY = 0;
		for (XPoint2D p : startPoints) {
			srcSpreadX = Math.max(srcSpreadX,
					Math.abs(p.getX() - startMedian.getX()));
			srcSpreadY = Math.max(srcSpreadY,
					Math.abs(p.getY() - startMedian.getY()));
		}

		final boolean horizontal;
		if (srcSpreadY > srcSpreadX * 2)
			horizontal = true;
		else if (srcSpreadX > srcSpreadY * 2)
			horizontal = false;
		else
			// Sources and destinations separated by at least a min-stub
			// in X means the spine should be vertical and stubs horizontal
			// — that's the drawHorizontalFlow geometry, even when the Y
			// spread of destinations is larger than the X separation.
			horizontal = Math.abs(flowDx) >= MIN_STUB_LENGTH;

		if (horizontal)
			drawHorizontalFlow(ugLine, edges, flowDx >= 0, style);
		else
			drawVerticalFlow(ugLine, edges, flowDy >= 0, style);
	}

	private void drawHorizontalFlow(UGraphic ugLine, List<EdgeData> edges,
			boolean leftToRight, Style style) {

		// Spine X: midway between source and nearest destination X,
		// clamped so every destination stub is at least MIN_STUB_LENGTH long,
		// then offset for overlap resolution.
		final double srcX = medianX(startPointsOf(edges));
		final double dstX = medianX(endPointsOf(edges));
		double spineX = clampSpineForMinStub((srcX + dstX) / 2, edges, srcX)
				+ spineXOffset;

		// Spine vertical extent: covers all source and destination ports
		double spineTopY = edges.get(0).start.getY();
		double spineBottomY = spineTopY;
		for (EdgeData e : edges) {
			spineTopY = Math.min(spineTopY, Math.min(e.start.getY(), e.end.getY()));
			spineBottomY = Math.max(spineBottomY, Math.max(e.start.getY(), e.end.getY()));
		}

		// Avoid routing spine through component clusters
		final List<RectangleArea> obstacles = collectObstacles();
		spineX = SvekPortConnector.findClearVerticalBidirectional(spineX,
				spineTopY, spineBottomY, obstacles);

		// Dual-spine detection: when the source-to-destination X distance
		// is large AND there are multiple distinct source and destination
		// rows, single-spine placement either stretches source stubs (spine
		// near dest) or dest stubs (spine near source) across the diagram.
		// A two-spine topology (vertical spine at source side, horizontal
		// trunk through the middle, vertical spine at dest side) keeps both
		// stub families short.
		if (shouldUseDualSpine(edges, srcX, dstX)) {
			drawDualSpineHorizontalFlow(ugLine, edges, style);
			return;
		}

		// Split-flow detection: if destinations cluster in two X-bands with a
		// gap wider than 2*MIN_STUB_LENGTH between them, route as inverted-U
		// (or U) so far-group stubs don't have to cross near-group cluster
		// bodies. The near group is the band closer to the source, the far
		// group is the other.
		final List<EdgeData> sortedByEndX = new ArrayList<EdgeData>(edges);
		java.util.Collections.sort(sortedByEndX, new java.util.Comparator<EdgeData>() {
			@Override
			public int compare(EdgeData a, EdgeData b) {
				return Double.compare(a.end.getX(), b.end.getX());
			}
		});
		int splitIdx = -1;
		double maxGap = 0;
		for (int i = 1; i < sortedByEndX.size(); i++) {
			final double gap = sortedByEndX.get(i).end.getX()
					- sortedByEndX.get(i - 1).end.getX();
			if (gap > maxGap) {
				maxGap = gap;
				splitIdx = i;
			}
		}
		final double splitGapThreshold = 2 * MIN_STUB_LENGTH;
		if (maxGap > splitGapThreshold && splitIdx > 0) {
			final List<EdgeData> low = new ArrayList<EdgeData>(
					sortedByEndX.subList(0, splitIdx));
			final List<EdgeData> high = new ArrayList<EdgeData>(
					sortedByEndX.subList(splitIdx, sortedByEndX.size()));
			final double lowMidX = (low.get(0).end.getX()
					+ low.get(low.size() - 1).end.getX()) / 2;
			final double highMidX = (high.get(0).end.getX()
					+ high.get(high.size() - 1).end.getX()) / 2;
			final List<EdgeData> nearEdges;
			final List<EdgeData> farEdges;
			if (Math.abs(lowMidX - srcX) < Math.abs(highMidX - srcX)) {
				nearEdges = low;
				farEdges = high;
			} else {
				nearEdges = high;
				farEdges = low;
			}
			// Recompute spine1X to sit between source and the near group only.
			// Don't reuse spineXOffset (computed by resolveOverlaps against the
			// single-flow naturalX): in split flow spine1 and spine2 are at
			// distinct X positions and need their own per-spine conflict
			// resolution against non-harness verticals.
			final double nearSrcX = medianX(startPointsOf(nearEdges));
			final double nearDstX = medianX(endPointsOf(nearEdges));
			double spine1X = clampSpineForMinStub(
					(nearSrcX + nearDstX) / 2, nearEdges, nearSrcX);
			spine1X = SvekPortConnector.findClearVerticalBidirectional(
					spine1X, spineTopY, spineBottomY, obstacles);
			spine1X = findClearXAgainstVerts(spine1X, spineTopY, spineBottomY);
			drawSplitHorizontalFlow(ugLine, edges, nearEdges, farEdges,
					spine1X, srcX, obstacles, style);
			return;
		}

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final UGraphic ugFan = ugLine.apply(fanStroke(style));
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		final double r = cornerRadius();

		// Source stubs: stubs at the spine endpoints share an L-corner with
		// the spine and get rounded; intermediate stubs are T-junctions and
		// stay as straight lines.
		final java.util.Set<Long> labeledSourceY = new java.util.HashSet<Long>();
		for (EdgeData e : edges) {
			final double srcDir = Math.signum(spineX - e.start.getX());
			final double srcEdge = e.start.getX() + srcDir * PORT_RADIUS;
			drawStubWithOptionalCorner(ugFan, srcEdge, e.start.getY(),
					spineX, e.start.getY(), spineTopY, spineBottomY, r);
			if (e.hasSourceLabel() && labeledSourceY.add(Double.doubleToLongBits(e.start.getY())))
				drawStubLabel(ugLine, fontConfig,
						Display.getWithNewlines(skinParam.getPragma(), e.sourceLabel),
						spineX, e.start.getY(), e.start.getX(), true);
		}

		// Vertical spine. Inset by the corner radius at each end so the L-
		// corners drawn by the endpoint stubs are not over-painted by the
		// thicker trunk stroke.
		final double trunkTop = (r > 0 ? spineTopY + r : spineTopY);
		final double trunkBottom = (r > 0 ? spineBottomY - r : spineBottomY);
		drawLine(ugTrunk, spineX, trunkTop, spineX, trunkBottom);
		renderedSpines.add(new RectangleArea(spineX - TRUNK_STROKE_WIDTH,
				spineTopY, spineX + TRUNK_STROKE_WIDTH, spineBottomY));

		// Destination stubs
		for (EdgeData e : edges) {
			final double endX = e.end.getX();
			final double endY = e.end.getY();
			final double dir = Math.signum(endX - spineX);
			final double tipX = endX - dir * PORT_RADIUS;
			drawStubWithOptionalCorner(ugFan, tipX, endY,
					spineX, endY, spineTopY, spineBottomY, r);
			drawHArrow(ugFan, tipX, endY, dir);
			final Display destLabel = destinationStubLabel(e);
			if (destLabel != null)
				drawStubLabel(ugLine, fontConfig, destLabel,
						spineX, endY, endX, false);
		}

		drawLabelOnTrunk(ugLine, spineX, spineTopY, spineX, spineBottomY, false, style);
	}

	// Draw a horizontal stub from (stubFarX, stubY) to (spineX, stubY). When
	// stubY lies on the spine's top or bottom endpoint the stub is rendered
	// as an L-polyline that extends `radius` into the spine, letting
	// drawRoundedPolyline curve the corner. For intermediate (T-junction)
	// stubs a straight line is drawn.
	private void drawStubWithOptionalCorner(UGraphic ug, double stubFarX,
			double stubY, double spineX, double spineMatchY,
			double spineTopY, double spineBottomY, double radius) {
		final boolean atTop = Math.abs(stubY - spineTopY) < 1;
		final boolean atBottom = Math.abs(stubY - spineBottomY) < 1;
		if (radius > 0 && (atTop || atBottom)) {
			final double extDir = atTop ? 1 : -1;
			final List<XPoint2D> pts = new ArrayList<XPoint2D>();
			pts.add(new XPoint2D(stubFarX, stubY));
			pts.add(new XPoint2D(spineX, stubY));
			pts.add(new XPoint2D(spineX, stubY + extDir * radius));
			drawRoundedPolyline(ug, pts);
		} else {
			drawLine(ug, stubFarX, stubY, spineX, stubY);
		}
	}

	// Find the closest X to `proposedX` that is at least PORT_WIDTH from
	// every non-harness vertical segment whose Y range overlaps [topY, bottomY].
	// Returns proposedX unchanged when there is no conflict, or when the
	// harness has no non-harness verts wired in (older callers).
	private double findClearXAgainstVerts(double proposedX, double topY, double bottomY) {
		if (nonHarnessVerts == null || nonHarnessVerts.isEmpty())
			return proposedX;
		final List<Double> conflictXs = new ArrayList<Double>();
		for (double[] seg : nonHarnessVerts) {
			final double overlapMin = Math.max(topY, seg[1]);
			final double overlapMax = Math.min(bottomY, seg[2]);
			if (overlapMax > overlapMin + 1)
				conflictXs.add(seg[0]);
		}
		if (conflictXs.isEmpty())
			return proposedX;
		boolean clear = true;
		for (double cx : conflictXs)
			if (Math.abs(proposedX - cx) < PORT_WIDTH) {
				clear = false;
				break;
			}
		if (clear)
			return proposedX;
		java.util.Collections.sort(conflictXs);
		double bestX = proposedX;
		double bestDist = Double.MAX_VALUE;
		final double below = conflictXs.get(0) - PORT_WIDTH;
		if (Math.abs(below - proposedX) < bestDist) {
			bestX = below;
			bestDist = Math.abs(below - proposedX);
		}
		final double above = conflictXs.get(conflictXs.size() - 1) + PORT_WIDTH;
		if (Math.abs(above - proposedX) < bestDist) {
			bestX = above;
			bestDist = Math.abs(above - proposedX);
		}
		for (int g = 0; g < conflictXs.size() - 1; g++) {
			final double gapCenter = (conflictXs.get(g) + conflictXs.get(g + 1)) / 2;
			final double gapWidth = conflictXs.get(g + 1) - conflictXs.get(g);
			if (gapWidth >= PORT_WIDTH * 2
					&& Math.abs(gapCenter - proposedX) < bestDist) {
				bestX = gapCenter;
				bestDist = Math.abs(gapCenter - proposedX);
			}
		}
		return bestX;
	}

	private double cornerRadius() {
		final String radiusStr = skinParam.getPragma()
				.getValue(PragmaKey.EDGE_CORNER_RADIUS);
		if (radiusStr == null)
			return 0;
		try {
			final double r = Double.parseDouble(radiusStr);
			return r > 0 ? r : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	// Predicate: should this harness use the dual-spine topology?
	// Triggers when the source-to-destination X distance is too large for
	// a single spine to keep stubs short, AND at least one side has
	// multiple distinct rows so at least one of the two spines has
	// meaningful vertical extent. (A harness with a single source and a
	// single destination is just one edge; not a dual-spine candidate.)
	private static boolean shouldUseDualSpine(List<EdgeData> edges,
			double srcX, double dstX) {
		final double distance = Math.abs(dstX - srcX);
		// Need enough room for two stub bands (one on each side) plus a
		// horizontal trunk running between them.
		if (distance < 6 * MIN_STUB_LENGTH + 2 * PORT_RADIUS)
			return false;
		final java.util.Set<Long> sourceYs = new java.util.HashSet<Long>();
		final java.util.Set<Long> destYs = new java.util.HashSet<Long>();
		for (EdgeData e : edges) {
			sourceYs.add(Double.doubleToLongBits(e.start.getY()));
			destYs.add(Double.doubleToLongBits(e.end.getY()));
		}
		return sourceYs.size() >= 2 || destYs.size() >= 2;
	}

	// Two-spine topology: vertical spine just outside the source column,
	// horizontal trunk through the middle, vertical spine just outside the
	// destination column. Every source stub is at most ~MIN_STUB_LENGTH and
	// every destination stub is similarly short.
	private void drawDualSpineHorizontalFlow(UGraphic ugLine,
			List<EdgeData> edges, Style style) {
		final double srcX = medianX(startPointsOf(edges));
		final double dstX = medianX(endPointsOf(edges));
		final boolean leftToRight = dstX > srcX;

		// Spines hug their respective column at MIN_STUB_LENGTH offset,
		// adjusted by the offsets computed by resolveDualSpineOverlaps so
		// neighbouring dual-spine harnesses don't collide on the same X.
		final double offset = MIN_STUB_LENGTH + PORT_RADIUS;
		double spine1X = (leftToRight ? srcX + offset : srcX - offset)
				+ dualSpine1XOffset;
		double spine2X = (leftToRight ? dstX - offset : dstX + offset)
				+ dualSpine2XOffset;

		// Per-spine vertical extent: spine1 covers source Y range,
		// spine2 covers destination Y range.
		double spine1Top = edges.get(0).start.getY();
		double spine1Bottom = spine1Top;
		double spine2Top = edges.get(0).end.getY();
		double spine2Bottom = spine2Top;
		for (EdgeData e : edges) {
			spine1Top = Math.min(spine1Top, e.start.getY());
			spine1Bottom = Math.max(spine1Bottom, e.start.getY());
			spine2Top = Math.min(spine2Top, e.end.getY());
			spine2Bottom = Math.max(spine2Bottom, e.end.getY());
		}

		final List<RectangleArea> obstacles = collectObstacles();
		spine1X = SvekPortConnector.findClearVerticalBidirectional(
				spine1X, spine1Top, spine1Bottom, obstacles);
		spine2X = SvekPortConnector.findClearVerticalBidirectional(
				spine2X, spine2Top, spine2Bottom, obstacles);
		spine1X = findClearXAgainstVerts(spine1X, spine1Top, spine1Bottom);
		spine2X = findClearXAgainstVerts(spine2X, spine2Top, spine2Bottom);

		// Wrap the trunk over the top (or under the bottom) of every stub
		// so the harness reads as a single continuous bundle with two
		// ends, not an H with T-junctions. The trunk Y lies just outside
		// the highest/lowest stub Y; spines extend in one direction only.
		final double allTop = Math.min(spine1Top, spine2Top);
		final double allBottom = Math.max(spine1Bottom, spine2Bottom);
		final double r = cornerRadius();
		final double trunkMargin = Math.max(r, 0) + PORT_RADIUS + PORT_WIDTH;
		final double xMin = Math.min(spine1X, spine2X);
		final double xMax = Math.max(spine1X, spine2X);
		final double trunkAbove = allTop - trunkMargin;
		final double trunkBelow = allBottom + trunkMargin;
		final double clearAbove = findClearCrossbarY(trunkAbove, xMin, xMax,
				obstacles);
		final double clearBelow = findClearCrossbarY(trunkBelow, xMin, xMax,
				obstacles);
		// Prefer whichever side is closer to its natural placement (less
		// vertical detour). Tie -> top.
		final boolean trunkAtTop =
				Math.abs(clearAbove - trunkAbove) <= Math.abs(clearBelow - trunkBelow);
		final double trunkY = trunkAtTop ? clearAbove : clearBelow;
		// End points of the polyline: the spine's far side from the trunk.
		final double spine1Far = trunkAtTop ? spine1Bottom : spine1Top;
		final double spine2Far = trunkAtTop ? spine2Bottom : spine2Top;

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final UGraphic ugFan = ugLine.apply(fanStroke(style));
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Source stubs to spine1
		final java.util.Set<Long> labeledSourceY = new java.util.HashSet<Long>();
		for (EdgeData e : edges) {
			final double srcDir = Math.signum(spine1X - e.start.getX());
			final double srcEdge = e.start.getX() + srcDir * PORT_RADIUS;
			drawLine(ugFan, srcEdge, e.start.getY(), spine1X, e.start.getY());
			if (e.hasSourceLabel()
					&& labeledSourceY.add(Double.doubleToLongBits(e.start.getY())))
				drawStubLabel(ugLine, fontConfig,
						Display.getWithNewlines(skinParam.getPragma(), e.sourceLabel),
						spine1X, e.start.getY(), e.start.getX(), true);
		}

		// Single continuous polyline: spine1Far -> trunk corner -> trunk
		// horizontal -> trunk corner -> spine2Far. Corners are rounded by
		// the edgeCornerRadius pragma.
		final List<XPoint2D> trunkPts = new ArrayList<XPoint2D>();
		trunkPts.add(new XPoint2D(spine1X, spine1Far));
		trunkPts.add(new XPoint2D(spine1X, trunkY));
		trunkPts.add(new XPoint2D(spine2X, trunkY));
		trunkPts.add(new XPoint2D(spine2X, spine2Far));
		drawRoundedPolyline(ugTrunk, trunkPts);

		renderedSpines.add(new RectangleArea(spine1X - TRUNK_STROKE_WIDTH,
				Math.min(spine1Far, trunkY),
				spine1X + TRUNK_STROKE_WIDTH,
				Math.max(spine1Far, trunkY)));
		renderedSpines.add(new RectangleArea(spine2X - TRUNK_STROKE_WIDTH,
				Math.min(spine2Far, trunkY),
				spine2X + TRUNK_STROKE_WIDTH,
				Math.max(spine2Far, trunkY)));
		renderedSpines.add(new RectangleArea(xMin, trunkY - TRUNK_STROKE_WIDTH,
				xMax, trunkY + TRUNK_STROKE_WIDTH));

		// Destination stubs from spine2
		for (EdgeData e : edges) {
			final double endX = e.end.getX();
			final double endY = e.end.getY();
			final double dir = Math.signum(endX - spine2X);
			final double tipX = endX - dir * PORT_RADIUS;
			drawLine(ugFan, spine2X, endY, tipX, endY);
			drawHArrow(ugFan, tipX, endY, dir);
			final Display destLabel = destinationStubLabel(e);
			if (destLabel != null)
				drawStubLabel(ugLine, fontConfig, destLabel,
						spine2X, endY, endX, false);
		}

		drawLabelOnTrunk(ugLine, spine1X, trunkY, spine2X, trunkY, true, style);
	}

	private void drawSplitHorizontalFlow(UGraphic ugLine,
			List<EdgeData> allEdges, List<EdgeData> nearEdges,
			List<EdgeData> farEdges, double spine1X, double srcX,
			List<RectangleArea> obstacles, Style style) {

		final FontConfiguration fontConfig = FontConfiguration.create(
				skinParam, style);
		final UGraphic ugFan = ugLine.apply(fanStroke(style));
		final UGraphic ugTrunk = ugLine.apply(
				UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Spine2 X: close to the far group, clamped for min stub.
		// spine2's "source" for clamp purposes is spine1 (the crossbar
		// joins them), so pass spine1X to keep spine2 on the spine1 side
		// of every far destination. spineXOffset (computed for single-flow
		// naturalX by resolveOverlaps) is irrelevant here; spine2 does its
		// own conflict resolution further down.
		double spine2X = clampSpineForMinStub(
				medianX(endPointsOf(farEdges)), farEdges, spine1X);

		// Spine1 Y extent: source ports + near destinations
		double spine1Top = allEdges.get(0).start.getY();
		double spine1Bottom = spine1Top;
		for (EdgeData e : allEdges) {
			spine1Top = Math.min(spine1Top, e.start.getY());
			spine1Bottom = Math.max(spine1Bottom, e.start.getY());
		}
		for (EdgeData e : nearEdges) {
			spine1Top = Math.min(spine1Top, e.end.getY());
			spine1Bottom = Math.max(spine1Bottom, e.end.getY());
		}

		// Spine2 Y extent: far destinations only
		double spine2Top = farEdges.get(0).end.getY();
		double spine2Bottom = spine2Top;
		for (EdgeData e : farEdges) {
			spine2Top = Math.min(spine2Top, e.end.getY());
			spine2Bottom = Math.max(spine2Bottom, e.end.getY());
		}

		// Crossbar Y: place at the end of spine1 closest to the far
		// group — bottom for a U shape, top for an inverted U.
		final double farMidY = (spine2Top + spine2Bottom) / 2;
		final boolean crossbarAtBottom =
				Math.abs(farMidY - spine1Bottom)
						< Math.abs(farMidY - spine1Top);
		final double xMin = Math.min(spine1X, spine2X);
		final double xMax = Math.max(spine1X, spine2X);
		double crossbarY;
		if (crossbarAtBottom) {
			final double lowestY = Math.max(spine1Bottom,
					spine2Bottom);
			crossbarY = lowestY + MIN_STUB_LENGTH;
		} else {
			final double highestY = Math.min(spine1Top, spine2Top);
			crossbarY = highestY - MIN_STUB_LENGTH;
		}

		// Find a clear horizontal channel for the crossbar
		if (SvekPortConnector.horizontalCollides(crossbarY, xMin,
				xMax, obstacles))
			crossbarY = findClearCrossbarY(crossbarY, xMin, xMax,
					obstacles);

		// Extend spines to the crossbar
		if (crossbarAtBottom) {
			spine1Bottom = crossbarY;
			spine2Bottom = crossbarY;
		} else {
			spine1Top = crossbarY;
			spine2Top = crossbarY;
		}

		// Find clear position for spine2 vertical against cluster bodies
		// AND non-harness vertical segments.
		spine2X = SvekPortConnector.findClearVerticalBidirectional(
				spine2X, spine2Top, spine2Bottom, obstacles);
		spine2X = findClearXAgainstVerts(spine2X, spine2Top, spine2Bottom);

		// Source stubs: connect to spine1
		final java.util.Set<Long> labeledSourceY =
				new java.util.HashSet<Long>();
		for (EdgeData e : allEdges) {
			final double srcDir = Math.signum(
					spine1X - e.start.getX());
			final double srcEdge = e.start.getX()
					+ srcDir * PORT_RADIUS;
			drawLine(ugFan, srcEdge, e.start.getY(),
					spine1X, e.start.getY());
			if (e.hasSourceLabel()
					&& labeledSourceY.add(
							Double.doubleToLongBits(
									e.start.getY())))
				drawStubLabel(ugLine, fontConfig,
						Display.getWithNewlines(
								skinParam.getPragma(),
								e.sourceLabel),
						spine1X, e.start.getY(),
						e.start.getX(), true);
		}

		// Trunk: render spine1, crossbar and spine2 as one polyline so the
		// two corners at the crossbar/spine joins can be rounded by the
		// edgeCornerRadius pragma. The polyline runs from spine1's far end
		// (away from the crossbar) through both crossbar corners to spine2's
		// far end.
		final double spine1Far = crossbarAtBottom ? spine1Top : spine1Bottom;
		final double spine2Far = crossbarAtBottom ? spine2Top : spine2Bottom;
		final List<XPoint2D> trunk = new ArrayList<XPoint2D>();
		trunk.add(new XPoint2D(spine1X, spine1Far));
		trunk.add(new XPoint2D(spine1X, crossbarY));
		trunk.add(new XPoint2D(spine2X, crossbarY));
		trunk.add(new XPoint2D(spine2X, spine2Far));
		drawRoundedPolyline(ugTrunk, trunk);
		renderedSpines.add(new RectangleArea(
				spine1X - TRUNK_STROKE_WIDTH, spine1Top,
				spine1X + TRUNK_STROKE_WIDTH, spine1Bottom));
		renderedSpines.add(new RectangleArea(
				Math.min(spine1X, spine2X),
				crossbarY - TRUNK_STROKE_WIDTH,
				Math.max(spine1X, spine2X),
				crossbarY + TRUNK_STROKE_WIDTH));
		renderedSpines.add(new RectangleArea(
				spine2X - TRUNK_STROKE_WIDTH, spine2Top,
				spine2X + TRUNK_STROKE_WIDTH, spine2Bottom));

		// Near destination stubs: connect to spine1
		for (EdgeData e : nearEdges) {
			final double endX = e.end.getX();
			final double endY = e.end.getY();
			final double dir = Math.signum(endX - spine1X);
			final double tipX = endX - dir * PORT_RADIUS;
			drawLine(ugFan, spine1X, endY, tipX, endY);
			drawHArrow(ugFan, tipX, endY, dir);
			final Display nearLabel = destinationStubLabel(e);
			if (nearLabel != null)
				drawStubLabel(ugLine, fontConfig, nearLabel,
						spine1X, endY, endX, false);
		}

		// Far destination stubs: connect to spine2
		for (EdgeData e : farEdges) {
			final double endX = e.end.getX();
			final double endY = e.end.getY();
			final double dir = Math.signum(endX - spine2X);
			final double tipX = endX - dir * PORT_RADIUS;
			drawLine(ugFan, spine2X, endY, tipX, endY);
			drawHArrow(ugFan, tipX, endY, dir);
			final Display farLabel = destinationStubLabel(e);
			if (farLabel != null)
				drawStubLabel(ugLine, fontConfig, farLabel,
						spine2X, endY, endX, false);
		}

		drawLabelOnTrunk(ugLine, spine1X, spine1Top,
				spine1X, spine1Bottom, false, style);
	}

	private void drawVerticalFlow(UGraphic ugLine, List<EdgeData> edges,
			boolean topToBottom, Style style) {

		// Spine X: midway between source and nearest destination X,
		// clamped so every destination stub is at least MIN_STUB_LENGTH long,
		// then offset for overlap resolution.
		final double srcX = medianX(startPointsOf(edges));
		final double dstX = medianX(endPointsOf(edges));
		double spineX = clampSpineForMinStub((srcX + dstX) / 2, edges, srcX)
				+ spineXOffset;

		// Spine vertical extent: from topmost to bottommost destination port
		double spineTopY = edges.get(0).end.getY();
		double spineBottomY = spineTopY;
		for (EdgeData e : edges) {
			spineTopY = Math.min(spineTopY, e.end.getY());
			spineBottomY = Math.max(spineBottomY, e.end.getY());
		}

		// Extend spine to include source attachment point
		final double srcY = medianY(startPointsOf(edges));
		spineTopY = Math.min(spineTopY, srcY);
		spineBottomY = Math.max(spineBottomY, srcY);

		// Avoid routing spine through component clusters
		final List<RectangleArea> obstacles = collectObstacles();
		spineX = SvekPortConnector.findClearVerticalBidirectional(spineX,
				spineTopY, spineBottomY, obstacles);

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final UGraphic ugFan = ugLine.apply(fanStroke(style));
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Source stub: horizontal from source port edge to spine
		final java.util.Set<Long> labeledSourceY = new java.util.HashSet<Long>();
		for (EdgeData e : edges) {
			final double srcDir = Math.signum(spineX - e.start.getX());
			final double srcEdge = e.start.getX() + srcDir * PORT_RADIUS;
			drawLine(ugFan, srcEdge, e.start.getY(), spineX, e.start.getY());
			if (e.hasSourceLabel() && labeledSourceY.add(Double.doubleToLongBits(e.start.getY())))
				drawStubLabel(ugLine, fontConfig,
						Display.getWithNewlines(skinParam.getPragma(), e.sourceLabel),
						spineX, e.start.getY(), e.start.getX(), true);
		}

		// Vertical spine
		drawLine(ugTrunk, spineX, spineTopY, spineX, spineBottomY);
		renderedSpines.add(new RectangleArea(spineX - TRUNK_STROKE_WIDTH,
				spineTopY, spineX + TRUNK_STROKE_WIDTH, spineBottomY));

		// Destination stubs: horizontal from spine to each destination port
		for (EdgeData e : edges) {
			final double endX = e.end.getX();
			final double endY = e.end.getY();
			final double dir = Math.signum(endX - spineX);
			final double tipX = endX - dir * PORT_RADIUS;
			drawLine(ugFan, spineX, endY, tipX, endY);
			drawHArrow(ugFan, tipX, endY, dir);
			final Display destLabel = destinationStubLabel(e);
			if (destLabel != null)
				drawStubLabel(ugLine, fontConfig, destLabel,
						spineX, endY, endX, false);
		}

		drawLabelOnTrunk(ugLine, spineX, spineTopY, spineX, spineBottomY, false, style);
	}

	private void drawStubLabel(UGraphic ug, FontConfiguration fontConfig,
			Display label, double spineX, double stubY, double portX,
			boolean rightJustify) {
		final FontConfiguration smallFont = fontConfig.changeSize(
				(float) (fontConfig.getFont().getSize2D() * LABEL_SCALE));
		final TextBlock textBlock = label.create(smallFont,
				HorizontalAlignment.LEFT, skinParam);
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D textDim = textBlock.calculateDimension(stringBounder);

		final double labelX;
		if (rightJustify)
			labelX = spineX - textDim.getWidth() - 5;
		else
			labelX = spineX + 5;
		final double labelY = stubY - textDim.getHeight() - 3;
		textBlock.drawU(ug.apply(new UTranslate(labelX, labelY)));
	}

	private static double findClearCrossbarY(double proposedY,
			double xMin, double xMax,
			List<RectangleArea> obstacles) {
		final double step = 4.0;
		final double limit = 2000.0;
		for (double offset = 0; offset < limit; offset += step) {
			final double y1 = proposedY + offset;
			if (SvekPortConnector.horizontalCollides(y1, xMin, xMax,
					obstacles) == false)
				return y1;
			if (offset == 0)
				continue;
			final double y2 = proposedY - offset;
			if (SvekPortConnector.horizontalCollides(y2, xMin, xMax,
					obstacles) == false)
				return y2;
		}
		return proposedY;
	}

	private void drawOrthoTrunk(UGraphic ug, double x1, double y1,
			double x2, double y2) {
		final boolean sameX = Math.abs(x1 - x2) < 1.0;
		final boolean sameY = Math.abs(y1 - y2) < 1.0;

		if (sameX || sameY) {
			drawLine(ug, x1, y1, x2, y2);
		} else {
			final double midX = (x1 + x2) / 2;
			drawLine(ug, x1, y1, midX, y1);
			drawLine(ug, midX, y1, midX, y2);
			drawLine(ug, midX, y2, x2, y2);
		}
	}

	private void drawLine(UGraphic ug, double x1, double y1, double x2, double y2) {
		final double lineDx = x2 - x1;
		final double lineDy = y2 - y1;
		if (Math.abs(lineDx) < 0.5 && Math.abs(lineDy) < 0.5)
			return;
		ug.apply(new UTranslate(x1, y1)).draw(new ULine(lineDx, lineDy));
	}

	// Fan-line (stub) stroke matches the arrow style's stroke so harness
	// stubs are drawn at the same thickness as ordinary single connectors.
	// Falls back to UStroke.simple() when the style has no stroke set.
	private static UStroke fanStroke(Style style) {
		final UStroke s = style.getStroke();
		return s != null ? s : UStroke.simple();
	}

	// Draw a polyline through the given orthogonal points, applying the
	// edgeCornerRadius pragma to round each interior corner.  Used for the
	// trunk and stub geometry where a 90-degree bend occurs — straight
	// segments could equally be drawn with drawLine, but funnelling all
	// trunk and L-stub draws through this method keeps the corner-rounding
	// behaviour identical to MyElkEdge.drawPolyline.
	private void drawRoundedPolyline(UGraphic ug, List<XPoint2D> pts) {
		if (pts.size() < 2)
			return;
		final List<XCubicCurve2D> beziers = new ArrayList<XCubicCurve2D>();
		for (int i = 0; i < pts.size() - 1; i++) {
			final XPoint2D a = pts.get(i);
			final XPoint2D b = pts.get(i + 1);
			beziers.add(new XCubicCurve2D(
					a.getX(), a.getY(), a.getX(), a.getY(),
					b.getX(), b.getY(), b.getX(), b.getY()));
		}
		final DotPath path = DotPath.fromBeziers(beziers);
		final String radiusStr = skinParam.getPragma()
				.getValue(PragmaKey.EDGE_CORNER_RADIUS);
		if (radiusStr != null) {
			try {
				final double radius = Double.parseDouble(radiusStr);
				if (radius > 0)
					path.muteToRoundOrthogonalPaths(radius);
			} catch (NumberFormatException e) {
				// Ignore invalid radius values
			}
		}
		ug.draw(path);
	}

	private static double clampSpineForMinStub(double spineX, List<EdgeData> edges,
			double sourceX) {
		// Place the spine MIN_STUB_LENGTH+PORT_RADIUS away from the nearest
		// destination on each side. When every destination sits to one side
		// of the source, keep the spine on that same side — otherwise the
		// stubs would have to cross the destination cluster bodies to reach
		// the port from inside, and the arrowhead would land on the port's
		// inside face instead of the outside face.
		if (edges.isEmpty())
			return spineX;
		final double minGap = MIN_STUB_LENGTH + PORT_RADIUS;
		double minEndX = Double.MAX_VALUE;
		double maxEndX = -Double.MAX_VALUE;
		for (EdgeData e : edges) {
			final double ex = e.end.getX();
			if (ex < minEndX)
				minEndX = ex;
			if (ex > maxEndX)
				maxEndX = ex;
		}
		if (sourceX <= minEndX)
			// Source is left of every destination — spine on the left so
			// fan-lines approach each port from outside the cluster.
			return Math.min(spineX, minEndX - minGap);
		if (sourceX >= maxEndX)
			// Symmetric: source right of all destinations.
			return Math.max(spineX, maxEndX + minGap);
		// Genuinely mixed (e.g. star fan-out with the spine in the middle).
		// Use a bidirectional clamp; this can still oscillate in pathological
		// layouts but matches the historical Svek behaviour.
		double nearestRight = Double.MAX_VALUE;
		double nearestLeft = -Double.MAX_VALUE;
		for (EdgeData e : edges) {
			final double ex = e.end.getX();
			if (ex >= spineX)
				nearestRight = Math.min(nearestRight, ex);
			else
				nearestLeft = Math.max(nearestLeft, ex);
		}
		if (nearestRight < Double.MAX_VALUE && nearestRight - spineX < minGap)
			spineX = nearestRight - minGap;
		if (nearestLeft > -Double.MAX_VALUE && spineX - nearestLeft < minGap)
			spineX = nearestLeft + minGap;
		return spineX;
	}

	private void drawHArrow(UGraphic ug, double tipX, double tipY, double dir) {
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

	private void drawLabelOnTrunk(UGraphic ug, double srcX, double srcY,
			double dstX, double dstY, boolean horizontal, Style style) {
		final String label = harness.getLabel();
		if (label == null || label.isEmpty())
			return;

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final TextBlock textBlock = Display.getWithNewlines(skinParam.getPragma(), label)
				.create(fontConfig, HorizontalAlignment.CENTER, skinParam);

		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D textDim = textBlock.calculateDimension(stringBounder);

		final double midX = (srcX + dstX) / 2;
		final double midY = (srcY + dstY) / 2;

		if (horizontal) {
			final double labelX = midX - textDim.getWidth() / 2;
			final double labelY = midY - textDim.getHeight() - 4;
			textBlock.drawU(ug.apply(new UTranslate(labelX, labelY)));
		} else {
			final double labelX = midX - textDim.getWidth() - 6;
			final double labelY = midY - textDim.getHeight() / 2;
			textBlock.drawU(ug.apply(new UTranslate(labelX, labelY)));
		}
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

	private static List<XPoint2D> startPointsOf(List<EdgeData> edges) {
		final List<XPoint2D> result = new ArrayList<XPoint2D>();
		for (EdgeData e : edges)
			result.add(e.start);
		return result;
	}

	private static List<XPoint2D> endPointsOf(List<EdgeData> edges) {
		final List<XPoint2D> result = new ArrayList<XPoint2D>();
		for (EdgeData e : edges)
			result.add(e.end);
		return result;
	}

	private static double medianX(List<XPoint2D> points) {
		final double[] vals = new double[points.size()];
		for (int i = 0; i < vals.length; i++)
			vals[i] = points.get(i).getX();
		java.util.Arrays.sort(vals);
		final int n = vals.length;
		return (n % 2 == 0) ? (vals[n / 2 - 1] + vals[n / 2]) / 2 : vals[n / 2];
	}

	private static double medianY(List<XPoint2D> points) {
		final double[] vals = new double[points.size()];
		for (int i = 0; i < vals.length; i++)
			vals[i] = points.get(i).getY();
		java.util.Arrays.sort(vals);
		final int n = vals.length;
		return (n % 2 == 0) ? (vals[n / 2 - 1] + vals[n / 2]) / 2 : vals[n / 2];
	}

	private static XPoint2D median(List<XPoint2D> points) {
		return new XPoint2D(medianX(points), medianY(points));
	}

	private List<RectangleArea> collectObstacles() {
		final List<RectangleArea> obstacles = new ArrayList<RectangleArea>();
		if (bibliotekon == null)
			return obstacles;

		// Source-side clusters stay excluded so the spine can hug the
		// source's outer face. Destination clusters ARE obstacles: the
		// spine clamp keeps the spine MIN_STUB_LENGTH+PORT_RADIUS away
		// from each destination, and the crossbar (in split flow) must
		// not pass through a destination cluster body — fan-line stubs
		// approach the port from outside the cluster via PORT_BORDER_OFFSET.
		Entity boardEntity = null;
		final java.util.Set<Entity> sourceClusters = new java.util.HashSet<Entity>();
		for (Link link : memberLinks) {
			final Entity p1 = link.getEntity1().getParentContainer();
			if (p1 != null)
				sourceClusters.add(p1);
			if (boardEntity == null && p1 != null && p1.getParentContainer() != null)
				boardEntity = p1.getParentContainer();
		}

		for (Cluster cl : bibliotekon.allCluster()) {
			final RectangleArea rect = cl.getRectangleArea();
			if (rect == null)
				continue;
			if (cl.getGroup() == boardEntity)
				continue;
			if (sourceClusters.contains(cl.getGroup()))
				continue;
			obstacles.add(rect);
		}
		return obstacles;
	}

}
