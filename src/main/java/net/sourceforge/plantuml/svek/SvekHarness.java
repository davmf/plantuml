package net.sourceforge.plantuml.svek;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.abel.Harness;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.shape.DotPath;
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
	private static final double FAN_OFFSET = 15.0;

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

		final XPoint2D startCentroid = centroid(startPoints);
		final XPoint2D endCentroid = centroid(endPoints);

		final double dx = endCentroid.getX() - startCentroid.getX();
		final double dy = endCentroid.getY() - startCentroid.getY();
		final boolean horizontal = Math.abs(dx) >= Math.abs(dy);

		final XPoint2D sourceMerge;
		final XPoint2D destMerge;
		if (horizontal) {
			final double midY = (startCentroid.getY() + endCentroid.getY()) / 2;
			final double sign = dx >= 0 ? 1.0 : -1.0;
			final double sX = startCentroid.getX() + sign * FAN_OFFSET;
			final double dX = endCentroid.getX() - sign * FAN_OFFSET;
			if ((sign > 0 && sX < dX) || (sign < 0 && sX > dX)) {
				sourceMerge = new XPoint2D(sX, midY);
				destMerge = new XPoint2D(dX, midY);
			} else {
				final double midX = (startCentroid.getX() + endCentroid.getX()) / 2;
				sourceMerge = new XPoint2D(midX, midY);
				destMerge = new XPoint2D(midX, midY);
			}
		} else {
			final double midX = (startCentroid.getX() + endCentroid.getX()) / 2;
			final double sign = dy >= 0 ? 1.0 : -1.0;
			final double sY = startCentroid.getY() + sign * FAN_OFFSET;
			final double dY = endCentroid.getY() - sign * FAN_OFFSET;
			if ((sign > 0 && sY < dY) || (sign < 0 && sY > dY)) {
				sourceMerge = new XPoint2D(midX, sY);
				destMerge = new XPoint2D(midX, dY);
			} else {
				final double midY = (startCentroid.getY() + endCentroid.getY()) / 2;
				sourceMerge = new XPoint2D(midX, midY);
				destMerge = new XPoint2D(midX, midY);
			}
		}

		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.arrow)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor color = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
		final UGraphic ugLine = ug.apply(color).apply(HColors.none().bg());

		for (XPoint2D start : startPoints) {
			final UGraphic ugFan = ugLine.apply(UStroke.simple());
			ugFan.apply(new UTranslate(start.getX(), start.getY())).draw(ULine.create(start, sourceMerge));
		}

		final UGraphic ugTrunk = ugLine.apply(UStroke.withThickness(TRUNK_STROKE_WIDTH));
		ugTrunk.apply(new UTranslate(sourceMerge.getX(), sourceMerge.getY()))
				.draw(ULine.create(sourceMerge, destMerge));

		for (XPoint2D end : endPoints) {
			final UGraphic ugFan = ugLine.apply(UStroke.simple());
			ugFan.apply(new UTranslate(destMerge.getX(), destMerge.getY())).draw(ULine.create(destMerge, end));
		}

		drawLabel(ug, sourceMerge, destMerge, style);
	}

	private void drawLabel(UGraphic ug, XPoint2D sourceMerge, XPoint2D destMerge, Style style) {
		final String label = harness.getLabel();
		if (label == null || label.isEmpty())
			return;

		final FontConfiguration fontConfig = FontConfiguration.create(skinParam, style);
		final TextBlock textBlock = Display.getWithNewlines(skinParam.getPragma(), label)
				.create(fontConfig, HorizontalAlignment.CENTER, skinParam);

		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D textDim = textBlock.calculateDimension(stringBounder);

		final double midX = (sourceMerge.getX() + destMerge.getX()) / 2;
		final double midY = (sourceMerge.getY() + destMerge.getY()) / 2;

		final double labelX = midX - textDim.getWidth() / 2;
		final double labelY = midY - textDim.getHeight() - 4;

		textBlock.drawU(ug.apply(new UTranslate(labelX, labelY)));
	}

	private static XPoint2D centroid(List<XPoint2D> points) {
		double sumX = 0;
		double sumY = 0;
		for (XPoint2D p : points) {
			sumX += p.getX();
			sumY += p.getY();
		}
		return new XPoint2D(sumX / points.size(), sumY / points.size());
	}

}
