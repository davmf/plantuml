package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
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
	private final Bibliotekon bibliotekon;

	public SvekHarness(Harness harness, List<SvekEdge> memberEdges, ISkinParam skinParam,
			Bibliotekon bibliotekon) {
		this.harness = harness;
		this.memberEdges = memberEdges;
		this.skinParam = skinParam;
		this.bibliotekon = bibliotekon;
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
			final XPoint2D startPt = resolveEntityCenter(edge.getLink().getEntity1(),
					path.getStartPoint());
			final XPoint2D endPt = resolveEntityCenter(edge.getLink().getEntity2(),
					path.getEndPoint());
			edges.add(new EdgeData(startPt, endPt, edge.getLink().getLabel()));
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
		final double spineX;
		if (gapSize > FAN_GAP * 4) {
			srcFanX = gapStart + sign * gapSize / 3;
			spineX = gapStart + sign * gapSize * 2 / 3;
		} else if (gapSize > FAN_GAP * 2) {
			srcFanX = gapStart + sign * FAN_GAP;
			spineX = gapEnd - sign * FAN_GAP;
		} else {
			final double mid = (gapStart + gapEnd) / 2;
			srcFanX = mid - sign * 2;
			spineX = mid + sign * 2;
		}

		// Compute spine vertical extent: covers all source and destination ports
		final double srcTrunkY = medianY(startPointsOf(edges));
		double spineTopY = srcTrunkY;
		double spineBottomY = srcTrunkY;
		for (EdgeData e : edges) {
			spineTopY = Math.min(spineTopY, e.end.getY());
			spineBottomY = Math.max(spineBottomY, e.end.getY());
		}

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final UGraphic ugFan = ugLine.apply(UStroke.simple());
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Source fan: converge to srcFanX, then horizontal trunk to spine
		for (EdgeData e : edges) {
			drawLine(ugFan, e.start.getX(), e.start.getY(), srcFanX, e.start.getY());
			drawLine(ugFan, srcFanX, e.start.getY(), srcFanX, srcTrunkY);
		}

		// Horizontal trunk from source fan to spine
		drawLine(ugTrunk, srcFanX, srcTrunkY, spineX, srcTrunkY);

		// Vertical spine spanning all destination ports
		drawLine(ugTrunk, spineX, spineTopY, spineX, spineBottomY);

		// Destination stubs: horizontal from spine to each destination port
		for (EdgeData e : edges) {
			final double endX = e.end.getX();
			final double endY = e.end.getY();
			if (Math.abs(endX - spineX) > 0.5 || Math.abs(endY - spineTopY) > 0.5)
				drawLine(ugFan, spineX, endY, endX, endY);
			if (e.hasLabel())
				drawStubLabel(ugLine, fontConfig, e.label, spineX,
						endY, endX, endY, true);
		}

		drawLabelOnTrunk(ugLine, spineX, spineTopY, spineX, spineBottomY, false, style);
	}

	private void drawVerticalFlow(UGraphic ugLine, List<EdgeData> edges,
			boolean topToBottom, Style style) {

		// Spine X: midway between source and nearest destination X
		final double srcX = medianX(startPointsOf(edges));
		final double dstX = medianX(endPointsOf(edges));
		final double spineX = (srcX + dstX) / 2;

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

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final UGraphic ugFan = ugLine.apply(UStroke.simple());
		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));

		// Source stub: horizontal from source port to spine
		for (EdgeData e : edges)
			drawLine(ugFan, e.start.getX(), e.start.getY(), spineX, e.start.getY());

		// Vertical spine
		drawLine(ugTrunk, spineX, spineTopY, spineX, spineBottomY);

		// Horizontal trunk connecting source to spine (if source Y not on spine)
		drawLine(ugTrunk, srcX, srcY, spineX, srcY);

		// Destination stubs: horizontal from spine to each destination port
		for (EdgeData e : edges) {
			drawLine(ugFan, spineX, e.end.getY(), e.end.getX(), e.end.getY());
			if (e.hasLabel())
				drawStubLabel(ugLine, fontConfig, e.label, spineX,
						e.end.getY(), e.end.getX(), e.end.getY(), true);
		}

		drawLabelOnTrunk(ugLine, spineX, spineTopY, spineX, spineBottomY, false, style);
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

}
