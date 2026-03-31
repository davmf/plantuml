package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Harness;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XCubicCurve2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.utils.Log;

public class SvekHarness implements UDrawable {

	private static final double TRUNK_STROKE_WIDTH = 3.0;
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
	private final List<SvekEdge> memberEdges;
	private final ISkinParam skinParam;
	private final Bibliotekon bibliotekon;
	private double spineXOffset;
	private final List<RectangleArea> renderedSpines = new ArrayList<RectangleArea>();

	public SvekHarness(Harness harness, List<SvekEdge> memberEdges, ISkinParam skinParam,
			Bibliotekon bibliotekon) {
		this.harness = harness;
		this.memberEdges = memberEdges;
		this.skinParam = skinParam;
		this.bibliotekon = bibliotekon;
	}

	public List<RectangleArea> getRenderedSpines() {
		return Collections.unmodifiableList(renderedSpines);
	}

	public static void resolveOverlaps(List<SvekHarness> harnesses) {
		final List<double[]> spines = new ArrayList<double[]>();
		for (SvekHarness h : harnesses)
			spines.add(h.computeNaturalSpineX());

		// Collect vertical segment X positions from non-harness connectors
		if (harnesses.isEmpty())
			return;
		final Bibliotekon bib = harnesses.get(0).bibliotekon;
		if (bib == null)
			return;
		final java.util.Set<SvekEdge> harnessEdges = new java.util.HashSet<SvekEdge>();
		for (SvekHarness h : harnesses)
			harnessEdges.addAll(h.memberEdges);

		final List<double[]> nonHarnessVerts = new ArrayList<double[]>();
		for (SvekEdge edge : bib.allLines()) {
			if (harnessEdges.contains(edge))
				continue;
			if (edge.isHidden())
				continue;
			final DotPath path = edge.getRenderedPath();
			if (path == null)
				continue;
			for (XCubicCurve2D seg : path.getBeziers()) {
				final double sx1 = seg.getX1(), sy1 = seg.getY1();
				final double sx2 = seg.getX2(), sy2 = seg.getY2();
				if (Math.abs(sx1 - sx2) < 0.5 && Math.abs(sy1 - sy2) > 1)
					nonHarnessVerts.add(new double[]{sx1,
							Math.min(sy1, sy2), Math.max(sy1, sy2)});
			}
		}

		// Separate harness spines from non-harness vertical segments
		for (int i = 0; i < harnesses.size(); i++) {
			final double[] spine = spines.get(i);
			if (Double.isNaN(spine[0]))
				continue;
			final SvekHarness h = harnesses.get(i);
			final double naturalX = spine[0];
			final double topY = spine[1];
			final double bottomY = spine[2];

			// Collect X positions of conflicting vertical segments
			// (those whose Y range overlaps the harness spine)
			final List<Double> conflictXs = new ArrayList<Double>();
			for (double[] seg : nonHarnessVerts) {
				final double overlapMin = Math.max(topY, seg[1]);
				final double overlapMax = Math.min(bottomY, seg[2]);
				if (overlapMax > overlapMin + 1)
					conflictXs.add(seg[0]);
			}
			if (conflictXs.isEmpty())
				continue;

			// Check if natural position is already clear
			boolean clear = true;
			for (double cx : conflictXs)
				if (Math.abs(naturalX - cx) < PORT_WIDTH)
					clear = false;
			if (clear)
				continue;

			// Sort conflict positions and find the nearest gap that fits
			java.util.Collections.sort(conflictXs);
			double bestX = naturalX;
			double bestDist = Double.MAX_VALUE;

			// Try below the lowest conflict
			final double belowCandidate = conflictXs.get(0) - PORT_WIDTH;
			if (Math.abs(belowCandidate - naturalX) < bestDist) {
				bestX = belowCandidate;
				bestDist = Math.abs(belowCandidate - naturalX);
			}
			// Try above the highest conflict
			final double aboveCandidate = conflictXs.get(conflictXs.size() - 1) + PORT_WIDTH;
			if (Math.abs(aboveCandidate - naturalX) < bestDist) {
				bestX = aboveCandidate;
				bestDist = Math.abs(aboveCandidate - naturalX);
			}
			// Try gaps between adjacent conflicts
			for (int g = 0; g < conflictXs.size() - 1; g++) {
				final double gapCenter = (conflictXs.get(g) + conflictXs.get(g + 1)) / 2;
				final double gapWidth = conflictXs.get(g + 1) - conflictXs.get(g);
				if (gapWidth >= PORT_WIDTH * 2
						&& Math.abs(gapCenter - naturalX) < bestDist) {
					bestX = gapCenter;
					bestDist = Math.abs(gapCenter - naturalX);
				}
			}
			// Check if the best position still violates minimum clearance
			boolean stillConflicting = false;
			for (double cx : conflictXs) {
				if (Math.abs(bestX - cx) < PORT_WIDTH) {
					stillConflicting = true;
					break;
				}
			}
			if (stillConflicting)
				Log.error("Harness '"
						+ h.harness.getLabel()
						+ "' spine cannot achieve minimum clearance ("
						+ PORT_WIDTH
						+ "px) from adjacent connectors. "
						+ "Consider increasing ranksep to provide more space.");
			h.spineXOffset = bestX - spine[0];
		}

		// Separate harness spines from each other (after non-harness separation)
		for (int i = 0; i < harnesses.size(); i++) {
			final double xi = spines.get(i)[0];
			if (Double.isNaN(xi))
				continue;
			for (int j = i + 1; j < harnesses.size(); j++) {
				final double xj = spines.get(j)[0];
				if (Double.isNaN(xj))
					continue;
				if (Math.abs(xi + harnesses.get(i).spineXOffset
						- (xj + harnesses.get(j).spineXOffset)) < PORT_WIDTH)
					harnesses.get(j).spineXOffset =
							xi + harnesses.get(i).spineXOffset + PORT_WIDTH - xj;
			}
		}
	}

	private double[] computeNaturalSpineX() {
		final List<EdgeData> edges = buildEdgeData();
		if (edges.isEmpty())
			return new double[]{Double.NaN, 0, 0};
		final double srcX = medianX(startPointsOf(edges));
		final double dstX = medianX(endPointsOf(edges));
		final double spineX = clampSpineForMinStub((srcX + dstX) / 2, edges);
		double topY = edges.get(0).start.getY();
		double bottomY = topY;
		for (EdgeData e : edges) {
			topY = Math.min(topY, Math.min(e.start.getY(), e.end.getY()));
			bottomY = Math.max(bottomY, Math.max(e.start.getY(), e.end.getY()));
		}
		return new double[]{spineX, topY, bottomY};
	}

	private List<EdgeData> buildEdgeData() {
		final List<EdgeData> edges = new ArrayList<EdgeData>();
		for (SvekEdge edge : memberEdges) {
			final DotPath path = edge.getDotPath();
			if (path == null)
				continue;
			final XPoint2D startPt = resolveEntityCenter(edge.getLink().getEntity1(),
					path.getStartPoint());
			final XPoint2D endPt = resolveEntityCenter(edge.getLink().getEntity2(),
					path.getEndPoint());
			edges.add(new EdgeData(startPt, endPt, edge.getLink().getLabel(),
					edge.getLink().getSourceLabel()));
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

	@Override
	public void drawU(UGraphic ug) {
		if (memberEdges.isEmpty())
			return;

		final List<EdgeData> edges = buildEdgeData();
		if (edges.isEmpty())
			return;

		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.arrow)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor color = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
		final UGraphic ugLine = ug.apply(color).apply(HColors.none().bg());

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
			horizontal = Math.abs(flowDx) >= Math.abs(flowDy);

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
		double spineX = clampSpineForMinStub((srcX + dstX) / 2, edges)
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

		// Check if destinations split into near/far X-groups
		final double farThreshold = 2 * MIN_STUB_LENGTH;
		final List<EdgeData> nearEdges = new ArrayList<EdgeData>();
		final List<EdgeData> farEdges = new ArrayList<EdgeData>();
		for (EdgeData e : edges) {
			if (Math.abs(e.end.getX() - spineX) > farThreshold)
				farEdges.add(e);
			else
				nearEdges.add(e);
		}
		if (farEdges.isEmpty() == false && nearEdges.isEmpty() == false) {
			drawSplitHorizontalFlow(ugLine, edges, nearEdges, farEdges,
					spineX, obstacles, style);
			return;
		}

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final UGraphic ugFan = ugLine.apply(UStroke.simple());
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Source stubs: single horizontal segment from source port edge to spine
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
			if (e.hasLabel())
				drawStubLabel(ugLine, fontConfig, e.label,
						spineX, endY, endX, false);
		}

		drawLabelOnTrunk(ugLine, spineX, spineTopY, spineX, spineBottomY, false, style);
	}

	private void drawSplitHorizontalFlow(UGraphic ugLine,
			List<EdgeData> allEdges, List<EdgeData> nearEdges,
			List<EdgeData> farEdges, double spine1X,
			List<RectangleArea> obstacles, Style style) {

		final FontConfiguration fontConfig = FontConfiguration.create(
				skinParam, style);
		final UGraphic ugFan = ugLine.apply(UStroke.simple());
		final UGraphic ugTrunk = ugLine.apply(
				UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Spine2 X: close to the far group, clamped for min stub
		double spine2X = clampSpineForMinStub(
				medianX(endPointsOf(farEdges)), farEdges)
				+ spineXOffset;

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

		// Find clear position for spine2 vertical
		spine2X = SvekPortConnector.findClearVerticalBidirectional(
				spine2X, spine2Top, spine2Bottom, obstacles);

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

		// Draw spine1 vertical
		drawLine(ugTrunk, spine1X, spine1Top, spine1X, spine1Bottom);
		renderedSpines.add(new RectangleArea(
				spine1X - TRUNK_STROKE_WIDTH, spine1Top,
				spine1X + TRUNK_STROKE_WIDTH, spine1Bottom));

		// Draw horizontal crossbar
		drawLine(ugTrunk, spine1X, crossbarY, spine2X, crossbarY);
		renderedSpines.add(new RectangleArea(
				Math.min(spine1X, spine2X),
				crossbarY - TRUNK_STROKE_WIDTH,
				Math.max(spine1X, spine2X),
				crossbarY + TRUNK_STROKE_WIDTH));

		// Draw spine2 vertical
		drawLine(ugTrunk, spine2X, spine2Top, spine2X, spine2Bottom);
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
			if (e.hasLabel())
				drawStubLabel(ugLine, fontConfig, e.label,
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
			if (e.hasLabel())
				drawStubLabel(ugLine, fontConfig, e.label,
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
		double spineX = clampSpineForMinStub((srcX + dstX) / 2, edges)
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
		final UGraphic ugFan = ugLine.apply(UStroke.simple());
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
			if (e.hasLabel())
				drawStubLabel(ugLine, fontConfig, e.label,
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

	private static double clampSpineForMinStub(double spineX, List<EdgeData> edges) {
		// Find the nearest destination on each side and ensure the visible stub
		// (spine to arrow tip at port edge) is at least MIN_STUB_LENGTH long.
		final double minGap = MIN_STUB_LENGTH + PORT_RADIUS;
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

	private XPoint2D resolveEntityCenter(Entity entity, XPoint2D fallback) {
		if (bibliotekon == null)
			return fallback;
		final SvekNode node = bibliotekon.getNode(entity);
		if (node == null)
			return fallback;
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

		Entity boardEntity = null;
		final java.util.Set<Entity> endpointComponents = new java.util.HashSet<Entity>();
		for (SvekEdge edge : memberEdges) {
			final Entity p1 = edge.getLink().getEntity1().getParentContainer();
			final Entity p2 = edge.getLink().getEntity2().getParentContainer();
			if (p1 != null)
				endpointComponents.add(p1);
			if (p2 != null)
				endpointComponents.add(p2);
			if (boardEntity == null && p1 != null && p1.getParentContainer() != null)
				boardEntity = p1.getParentContainer();
		}

		for (Cluster cl : bibliotekon.allCluster()) {
			final RectangleArea rect = cl.getRectangleArea();
			if (rect == null)
				continue;
			if (cl.getGroup() == boardEntity)
				continue;
			if (endpointComponents.contains(cl.getGroup()))
				continue;
			obstacles.add(rect);
		}
		return obstacles;
	}

}
