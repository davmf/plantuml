package net.sourceforge.plantuml.svek;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;

public class SvekPortConnector implements UDrawable {

	private static final double PORT_RADIUS = 6.0;
	private static final int ARROW_WING = 9;
	private static final int ARROW_APERTURE = 4;
	private static final int ARROW_CONTACT = 5;

	private final SvekEdge edge;
	private final ISkinParam skinParam;
	private final Bibliotekon bibliotekon;
	private double verticalSegmentX = Double.NaN;

	public SvekPortConnector(SvekEdge edge, ISkinParam skinParam, Bibliotekon bibliotekon) {
		this.edge = edge;
		this.skinParam = skinParam;
		this.bibliotekon = bibliotekon;
	}

	public double getVerticalSegmentX() {
		return verticalSegmentX;
	}

	@Override
	public void drawU(UGraphic ug) {
		final XPoint2D startPt = resolveEntityCenter(edge.getLink().getEntity1());
		final XPoint2D endPt = resolveEntityCenter(edge.getLink().getEntity2());
		if (startPt == null || endPt == null)
			return;

		final Style style = StyleSignatureBasic.of(SName.root, SName.element, SName.arrow)
				.getMergedStyle(skinParam.getCurrentStyleBuilder());
		final HColor color = style.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());
		final UGraphic ugLine = ug.apply(color).apply(HColors.none().bg()).apply(UStroke.simple());

		final double srcX = startPt.getX();
		final double srcY = startPt.getY();
		final double dstX = endPt.getX();
		final double dstY = endPt.getY();

		final double dirSrc = Math.signum(dstX - srcX);
		final double dirDst = Math.signum(dstX - srcX);
		final double srcEdge = srcX + dirSrc * PORT_RADIUS;
		final double dstEdge = dstX - dirDst * PORT_RADIUS;

		if (Math.abs(srcY - dstY) < 1.0) {
			// Straight horizontal line
			drawLine(ugLine, srcEdge, srcY, dstEdge, dstY);
			drawHArrow(ugLine, dstEdge, dstY, dirDst);
			verticalSegmentX = Double.NaN;
		} else {
			// 3-segment orthogonal: horizontal, vertical, horizontal
			final double midX = (srcEdge + dstEdge) / 2;
			drawLine(ugLine, srcEdge, srcY, midX, srcY);
			drawLine(ugLine, midX, srcY, midX, dstY);
			drawLine(ugLine, midX, dstY, dstEdge, dstY);
			drawHArrow(ugLine, dstEdge, dstY, dirDst);
			verticalSegmentX = midX;
		}
	}

	private void drawLine(UGraphic ug, double x1, double y1, double x2, double y2) {
		final double lineDx = x2 - x1;
		final double lineDy = y2 - y1;
		if (Math.abs(lineDx) < 0.5 && Math.abs(lineDy) < 0.5)
			return;
		ug.apply(new UTranslate(x1, y1)).draw(new ULine(lineDx, lineDy));
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

}
