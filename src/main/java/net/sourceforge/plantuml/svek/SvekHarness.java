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
	private static final double LABEL_GAP = 3.0;

	private final Harness harness;
	private final List<SvekEdge> memberEdges;
	private final ISkinParam skinParam;

	public SvekHarness(Harness harness, List<SvekEdge> memberEdges, ISkinParam skinParam) {
		this.harness = harness;
		this.memberEdges = memberEdges;
		this.skinParam = skinParam;
	}

	private static final class EdgeData {
		final XPoint2D start;
		final XPoint2D end;
		final Display label;

		EdgeData(XPoint2D start, XPoint2D end, Display label) {
			this.start = start;
			this.end = end;
			this.label = label;
		}

		boolean hasLabel() {
			return Display.isNull(label) == false;
		}
	}

	@Override
	public void drawU(UGraphic ug) {
		if (memberEdges.isEmpty())
			return;

		final List<EdgeData> edges = new ArrayList<EdgeData>();
		for (SvekEdge edge : memberEdges) {
			final DotPath path = edge.getDotPath();
			if (path == null)
				continue;
			edges.add(new EdgeData(
					path.getStartPoint(),
					path.getEndPoint(),
					edge.getLink().getLabel()));
		}

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
		final double sign = leftToRight ? 1.0 : -1.0;

		double srcEdgeX = edges.get(0).start.getX();
		for (EdgeData e : edges)
			srcEdgeX = leftToRight ? Math.max(srcEdgeX, e.start.getX())
					: Math.min(srcEdgeX, e.start.getX());

		double dstNearX = edges.get(0).end.getX();
		for (EdgeData e : edges)
			dstNearX = leftToRight ? Math.min(dstNearX, e.end.getX())
					: Math.max(dstNearX, e.end.getX());

		final double gapStart = srcEdgeX;
		final double gapEnd = dstNearX;
		final double gapSize = (gapEnd - gapStart) * sign;

		final double srcFanX;
		final double dstFanX;
		if (gapSize > FAN_GAP * 4) {
			srcFanX = gapStart + sign * gapSize / 3;
			dstFanX = gapStart + sign * gapSize * 2 / 3;
		} else if (gapSize > FAN_GAP * 2) {
			srcFanX = gapStart + sign * FAN_GAP;
			dstFanX = gapEnd - sign * FAN_GAP;
		} else {
			final double mid = (gapStart + gapEnd) / 2;
			srcFanX = mid - sign * 2;
			dstFanX = mid + sign * 2;
		}

		final double farThreshold = dstNearX + sign * FAN_GAP * 3;
		final List<EdgeData> nearEdges = new ArrayList<EdgeData>();
		final List<EdgeData> farEdges = new ArrayList<EdgeData>();
		for (EdgeData e : edges) {
			final boolean isFar = leftToRight
					? e.end.getX() > farThreshold
					: e.end.getX() < farThreshold;
			if (isFar)
				farEdges.add(e);
			else
				nearEdges.add(e);
		}

		final List<XPoint2D> startPoints = new ArrayList<XPoint2D>();
		final List<XPoint2D> nearEndPoints = new ArrayList<XPoint2D>();
		for (EdgeData e : edges)
			startPoints.add(e.start);
		for (EdgeData e : nearEdges)
			nearEndPoints.add(e.end);

		final double srcTrunkY = medianY(startPoints);
		final double dstTrunkY = nearEndPoints.isEmpty()
				? medianY(endPointsOf(edges)) : medianY(nearEndPoints);

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final UGraphic ugFan = ugLine.apply(UStroke.simple());
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Source fan
		for (EdgeData e : edges) {
			drawLine(ugFan, e.start.getX(), e.start.getY(), srcFanX, e.start.getY());
			drawLine(ugFan, srcFanX, e.start.getY(), srcFanX, srcTrunkY);
		}

		// Trunk
		drawOrthoTrunk(ugTrunk, srcFanX, srcTrunkY, dstFanX, dstTrunkY);

		// Near-side dest fan
		for (EdgeData e : nearEdges) {
			drawLine(ugFan, dstFanX, dstTrunkY, dstFanX, e.end.getY());
			drawLine(ugFan, dstFanX, e.end.getY(), e.end.getX(), e.end.getY());
			if (e.hasLabel())
				drawStubLabel(ugLine, fontConfig, e.label, dstFanX,
						e.end.getY(), e.end.getX(), e.end.getY(), true);
		}

		// Far-side dest fan
		if (farEdges.isEmpty() == false) {
			double nearTopY = Double.MAX_VALUE;
			double nearBottomY = -Double.MAX_VALUE;
			for (EdgeData e : nearEdges) {
				nearTopY = Math.min(nearTopY, e.end.getY());
				nearBottomY = Math.max(nearBottomY, e.end.getY());
			}
			final double portSpread = nearBottomY - nearTopY;
			final double estimatedPadding = Math.max(portSpread * 0.5, FAN_GAP * 2);
			final double jogY = nearBottomY + estimatedPadding;

			for (EdgeData e : farEdges) {
				drawLine(ugFan, dstFanX, dstTrunkY, dstFanX, jogY);
				drawLine(ugFan, dstFanX, jogY, e.end.getX(), jogY);
				drawLine(ugFan, e.end.getX(), jogY, e.end.getX(), e.end.getY());
				if (e.hasLabel())
					drawStubLabel(ugLine, fontConfig, e.label, e.end.getX(),
							jogY, e.end.getX(), e.end.getY(), false);
			}
		}

		drawLabelOnTrunk(ugLine, srcFanX, srcTrunkY, dstFanX, dstTrunkY, true, style);
	}

	private void drawVerticalFlow(UGraphic ugLine, List<EdgeData> edges,
			boolean topToBottom, Style style) {
		final double sign = topToBottom ? 1.0 : -1.0;

		double srcEdgeY = edges.get(0).start.getY();
		for (EdgeData e : edges)
			srcEdgeY = topToBottom ? Math.max(srcEdgeY, e.start.getY())
					: Math.min(srcEdgeY, e.start.getY());

		double dstNearY = edges.get(0).end.getY();
		for (EdgeData e : edges)
			dstNearY = topToBottom ? Math.min(dstNearY, e.end.getY())
					: Math.max(dstNearY, e.end.getY());

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

		final List<XPoint2D> startPoints = new ArrayList<XPoint2D>();
		final List<XPoint2D> endPoints = new ArrayList<XPoint2D>();
		for (EdgeData e : edges) {
			startPoints.add(e.start);
			endPoints.add(e.end);
		}

		final double srcTrunkX = medianX(startPoints);
		final double dstTrunkX = medianX(endPoints);

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final UGraphic ugFan = ugLine.apply(UStroke.simple());
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Source fan
		for (EdgeData e : edges) {
			drawLine(ugFan, e.start.getX(), e.start.getY(), e.start.getX(), srcFanY);
			drawLine(ugFan, e.start.getX(), srcFanY, srcTrunkX, srcFanY);
		}

		// Trunk
		drawOrthoTrunk(ugTrunk, srcTrunkX, srcFanY, dstTrunkX, dstFanY);

		// Dest fan
		for (EdgeData e : edges) {
			drawLine(ugFan, dstTrunkX, dstFanY, e.end.getX(), dstFanY);
			drawLine(ugFan, e.end.getX(), dstFanY, e.end.getX(), e.end.getY());
			if (e.hasLabel())
				drawStubLabel(ugLine, fontConfig, e.label, e.end.getX(),
						dstFanY, e.end.getX(), e.end.getY(), false);
		}

		drawLabelOnTrunk(ugLine, srcTrunkX, srcFanY, dstTrunkX, dstFanY, false, style);
	}

	private void drawStubLabel(UGraphic ug, FontConfiguration fontConfig,
			Display label, double x1, double y1, double x2, double y2,
			boolean horizontal) {
		final TextBlock textBlock = label.create(fontConfig,
				HorizontalAlignment.LEFT, skinParam);
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D textDim = textBlock.calculateDimension(stringBounder);

		if (horizontal) {
			// Place label on the horizontal stub, centered on both axes
			// of the segment so it sits directly on the line.
			final double midX = (x1 + x2) / 2;
			final double labelX = midX - textDim.getWidth() / 2;
			final double labelY = y1 - textDim.getHeight() / 2;
			textBlock.drawU(ug.apply(new UTranslate(labelX, labelY)));
		} else {
			// Place label beside the vertical stub, centered on the segment
			final double midY = (y1 + y2) / 2;
			final double labelX = Math.min(x1, x2) - textDim.getWidth() - LABEL_GAP;
			final double labelY = midY - textDim.getHeight() / 2;
			textBlock.drawU(ug.apply(new UTranslate(labelX, labelY)));
		}
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

}
