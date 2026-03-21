package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.List;

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
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;

public class SvekHarness implements UDrawable {

	private static final double TRUNK_STROKE_WIDTH = 3.0;
	private static final double FAN_GAP = 20.0;

	private final Harness harness;
	private final List<SvekEdge> memberEdges;
	private final ISkinParam skinParam;

	public SvekHarness(Harness harness, List<SvekEdge> memberEdges, ISkinParam skinParam) {
		this.harness = harness;
		this.memberEdges = memberEdges;
		this.skinParam = skinParam;
	}

	@Override
	public void drawU(UGraphic ug) {
		if (memberEdges.isEmpty())
			return;

		final List<XPoint2D> startPoints = new ArrayList<XPoint2D>();
		final List<XPoint2D> endPoints = new ArrayList<XPoint2D>();

		for (SvekEdge edge : memberEdges) {
			final DotPath path = edge.getDotPath();
			if (path == null)
				continue;
			startPoints.add(path.getStartPoint());
			endPoints.add(path.getEndPoint());
		}

		if (startPoints.isEmpty())
			return;

		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.arrow)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor color = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
		final UGraphic ugLine = ug.apply(color).apply(HColors.none().bg());

		// Determine primary flow direction.
		// If start points are more vertically spread than horizontally,
		// ports are stacked vertically (typical for horizontal flow in
		// LTR/RTL layouts). Use this together with the median displacement
		// to determine the dominant axis.
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

		// If source ports are vertically stacked (spreadY > spreadX),
		// the flow exits horizontally. Vice versa for horizontal spread.
		final boolean horizontal;
		if (srcSpreadY > srcSpreadX * 2)
			horizontal = true;
		else if (srcSpreadX > srcSpreadY * 2)
			horizontal = false;
		else
			horizontal = Math.abs(flowDx) >= Math.abs(flowDy);

		if (horizontal)
			drawHorizontalFlow(ugLine, startPoints, endPoints, flowDx >= 0, style);
		else
			drawVerticalFlow(ugLine, startPoints, endPoints, flowDy >= 0, style);
	}

	/**
	 * Horizontal flow: trunk runs horizontally between two vertical fan columns.
	 * Each endpoint connects to the trunk via orthogonal segments regardless of
	 * which side of the diagram it sits on.
	 */
	private void drawHorizontalFlow(UGraphic ugLine, List<XPoint2D> startPoints,
			List<XPoint2D> endPoints, boolean leftToRight, Style style) {
		final double sign = leftToRight ? 1.0 : -1.0;

		// Find the furthest source port in the flow direction
		double srcEdgeX = startPoints.get(0).getX();
		for (XPoint2D p : startPoints)
			srcEdgeX = leftToRight ? Math.max(srcEdgeX, p.getX())
					: Math.min(srcEdgeX, p.getX());

		// Find the nearest dest port in the flow direction
		double dstNearX = endPoints.get(0).getX();
		for (XPoint2D p : endPoints)
			dstNearX = leftToRight ? Math.min(dstNearX, p.getX())
					: Math.max(dstNearX, p.getX());

		// Place both fan columns in the gap between source and dest,
		// with even spacing
		final double gapStart = srcEdgeX;
		final double gapEnd = dstNearX;
		final double gapSize = (gapEnd - gapStart) * sign;

		final double srcFanX;
		final double dstFanX;
		if (gapSize > FAN_GAP * 4) {
			// Enough room: place fans at 1/3 and 2/3 of the gap
			srcFanX = gapStart + sign * gapSize / 3;
			dstFanX = gapStart + sign * gapSize * 2 / 3;
		} else if (gapSize > FAN_GAP * 2) {
			srcFanX = gapStart + sign * FAN_GAP;
			dstFanX = gapEnd - sign * FAN_GAP;
		} else {
			// Tight gap: place at midpoint
			final double mid = (gapStart + gapEnd) / 2;
			srcFanX = mid - sign * 2;
			dstFanX = mid + sign * 2;
		}

		// Separate dest endpoints into "near side" (same side as trunk)
		// and "far side" (opposite side of the dest component).
		final double farThreshold = dstNearX + sign * FAN_GAP * 3;
		final List<XPoint2D> nearEnds = new ArrayList<XPoint2D>();
		final List<XPoint2D> farEnds = new ArrayList<XPoint2D>();
		for (XPoint2D end : endPoints) {
			final boolean isFar = leftToRight
					? end.getX() > farThreshold
					: end.getX() < farThreshold;
			if (isFar)
				farEnds.add(end);
			else
				nearEnds.add(end);
		}

		// Trunk Y: use only near-side endpoints so far-side outliers
		// don't distort the trunk position.
		final double srcTrunkY = medianY(startPoints);
		final double dstTrunkY = nearEnds.isEmpty()
				? medianY(endPoints) : medianY(nearEnds);

		final UGraphic ugFan = ugLine.apply(UStroke.simple());
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Source fan: each port connects orthogonally to (srcFanX, srcTrunkY)
		for (XPoint2D start : startPoints) {
			drawLine(ugFan, start.getX(), start.getY(), srcFanX, start.getY());
			drawLine(ugFan, srcFanX, start.getY(), srcFanX, srcTrunkY);
		}

		// Trunk: orthogonal route from (srcFanX, srcTrunkY) to (dstFanX, dstTrunkY)
		drawOrthoTrunk(ugTrunk, srcFanX, srcTrunkY, dstFanX, dstTrunkY);

		// Near-side dest fan: simple orthogonal route
		for (XPoint2D end : nearEnds) {
			drawLine(ugFan, dstFanX, dstTrunkY, dstFanX, end.getY());
			drawLine(ugFan, dstFanX, end.getY(), end.getX(), end.getY());
		}

		// Far-side dest fan: route below the component body.
		// Use the near-side port spread as an estimate of component height,
		// then add generous clearance to pass below the component.
		if (farEnds.isEmpty() == false) {
			double nearTopY = Double.MAX_VALUE;
			double nearBottomY = -Double.MAX_VALUE;
			for (XPoint2D end : nearEnds) {
				nearTopY = Math.min(nearTopY, end.getY());
				nearBottomY = Math.max(nearBottomY, end.getY());
			}
			// Estimate component height from port spread, add padding for
			// the title area and borders above/below the port range
			final double portSpread = nearBottomY - nearTopY;
			final double estimatedPadding = Math.max(portSpread * 0.5, FAN_GAP * 2);
			final double jogY = nearBottomY + estimatedPadding;

			for (XPoint2D end : farEnds) {
				drawLine(ugFan, dstFanX, dstTrunkY, dstFanX, jogY);
				drawLine(ugFan, dstFanX, jogY, end.getX(), jogY);
				drawLine(ugFan, end.getX(), jogY, end.getX(), end.getY());
			}
		}

		drawLabelOnTrunk(ugLine, srcFanX, srcTrunkY, dstFanX, dstTrunkY, true, style);
	}

	/**
	 * Vertical flow: trunk runs vertically between two horizontal fan rows.
	 */
	private void drawVerticalFlow(UGraphic ugLine, List<XPoint2D> startPoints,
			List<XPoint2D> endPoints, boolean topToBottom, Style style) {
		final double sign = topToBottom ? 1.0 : -1.0;

		double srcEdgeY = startPoints.get(0).getY();
		for (XPoint2D p : startPoints)
			srcEdgeY = topToBottom ? Math.max(srcEdgeY, p.getY())
					: Math.min(srcEdgeY, p.getY());

		double dstNearY = endPoints.get(0).getY();
		for (XPoint2D p : endPoints)
			dstNearY = topToBottom ? Math.min(dstNearY, p.getY())
					: Math.max(dstNearY, p.getY());

		final double gapStart = srcEdgeY;
		final double gapEnd = dstNearY;
		final double gapSize = (gapEnd - gapStart) * sign;

		final double srcFanY;
		final double dstFanY;
		if (gapSize > FAN_GAP * 4) {
			srcFanY = gapStart + sign * gapSize / 3;
			dstFanY = gapStart + sign * gapSize * 2 / 3;
		} else if (gapSize > FAN_GAP * 2) {
			srcFanY = gapStart + sign * FAN_GAP;
			dstFanY = gapEnd - sign * FAN_GAP;
		} else {
			final double mid = (gapStart + gapEnd) / 2;
			srcFanY = mid - sign * 2;
			dstFanY = mid + sign * 2;
		}

		final double srcTrunkX = medianX(startPoints);
		final double dstTrunkX = medianX(endPoints);

		final UGraphic ugFan = ugLine.apply(UStroke.simple());
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		for (XPoint2D start : startPoints) {
			drawLine(ugFan, start.getX(), start.getY(), start.getX(), srcFanY);
			drawLine(ugFan, start.getX(), srcFanY, srcTrunkX, srcFanY);
		}

		drawOrthoTrunk(ugTrunk, srcTrunkX, srcFanY, dstTrunkX, dstFanY);

		for (XPoint2D end : endPoints) {
			drawLine(ugFan, dstTrunkX, dstFanY, end.getX(), dstFanY);
			drawLine(ugFan, end.getX(), dstFanY, end.getX(), end.getY());
		}

		drawLabelOnTrunk(ugLine, srcTrunkX, srcFanY, dstTrunkX, dstFanY, false, style);
	}

	/**
	 * Draw a trunk between two points using orthogonal segments.
	 * If already axis-aligned, draws a single segment.
	 * Otherwise draws an L or Z route.
	 */
	private void drawOrthoTrunk(UGraphic ug, double x1, double y1,
			double x2, double y2) {
		final boolean sameX = Math.abs(x1 - x2) < 1.0;
		final boolean sameY = Math.abs(y1 - y2) < 1.0;

		if (sameX || sameY) {
			drawLine(ug, x1, y1, x2, y2);
		} else {
			// Z-route with midpoint transition
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

}
