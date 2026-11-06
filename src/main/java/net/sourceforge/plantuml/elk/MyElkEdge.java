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
package net.sourceforge.plantuml.elk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.atmp.CucaDiagram;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.annotation.DuplicateCode;
import net.sourceforge.plantuml.decoration.LinkDecor;
import net.sourceforge.plantuml.decoration.LinkType;

/*
 * You can choose between real "org.eclipse.elk..." classes or proxied "net.sourceforge.plantuml.elk.proxy..."
 *
 * Using proxied classes allows to compile PlantUML without having ELK available on the classpath.
 * Since GraphViz is the default layout engine up to now, we do not want to enforce the use of ELK just for compilation.
 * (for people not using maven)
 *
 * If you are debugging, you should probably switch to "org.eclipse.elk..." classes
 *
 */

/*
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkLabel;
*/

import net.sourceforge.plantuml.elk.proxy.graph.ElkBendPoint;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdgeSection;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.klimt.UPath;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.MagneticBorder;
import net.sourceforge.plantuml.klimt.geom.MagneticBorderNone;
import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.skin.LineParam;
import net.sourceforge.plantuml.skin.PragmaKey;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleQueries;
import net.sourceforge.plantuml.style.StyleQuery;
import net.sourceforge.plantuml.utils.Log;
import net.sourceforge.plantuml.svek.RoleLabels;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactory;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactoryExtends;

public class MyElkEdge implements UDrawable {
	private final Link link;
	private final ElkEdge edge;

	private final CucaDiagram diagram;
	private final TextBlock centerLabel;
	private final TextBlock headLabel;
	private final TextBlock tailLabel;
	private final TextBlock tailRole;
	private final TextBlock headRole;

	private final SName styleName;

	private final Map<Entity, MyElkCluster> elkClusters;

	private final Map<Entity, IEntityImage> nodeImages;

	private final double magicY2;

	private final UTranslate translate;

	private final Map<ElkEdgeSection, ElkStaircaseSimplifier.Collapse> collapses;

	public MyElkEdge(CucaDiagram diagram, SName styleName, Link link, ElkEdge edge, TextBlock centerLabel,
			TextBlock tailLabel, TextBlock headLabel, TextBlock tailRole, TextBlock headRole,
			double magicY2, Map<Entity, MyElkCluster> elkClusters,
			UTranslate translate, Map<Entity, IEntityImage> nodeImages,
			Map<ElkEdgeSection, ElkStaircaseSimplifier.Collapse> collapses) {
		this.link = link;
		this.edge = edge;
		this.translate = translate;
		this.nodeImages = nodeImages;
		this.collapses = collapses;

		this.diagram = diagram;
		this.centerLabel = centerLabel;
		this.tailLabel = tailLabel;
		this.headLabel = headLabel;
		this.tailRole = tailRole;
		this.headRole = headRole;
		this.styleName = styleName;
		this.magicY2 = magicY2;
		this.elkClusters = elkClusters;

	}

	private Style getStyle() {
		final StyleQuery result = StyleQueries.ARROW.add(diagram.getDiagramType().getStyleName())
				.withStereotype(link.getStereotype());
		return diagram.getSkinParam().getCurrentStyleBuilder().getMergedStyle(result);
	}

	@DuplicateCode(reference = "SvekEdge")
	public void drawU(UGraphic ug) {

		if (link.isHidden())
			return;

		if (link.isPartOfHarness())
			return;

		if (link.isJumper())
			return;

		ug = ug.apply(translate);
		UGraphic ugOrig = ug;

		final ISkinParam skinParam = diagram.getSkinParam();
		final Style styleLine = getStyle();

		HColor color = styleLine.value(PName.LineColor).asColor(skinParam.getIHtmlColorSet());

		if (this.link.getColors() != null) {
			final HColor newColor = this.link.getColors().getColor(ColorType.ARROW, ColorType.LINE);
			if (newColor != null)
				color = newColor;
		} else if (this.link.getSpecificColor() != null) {
			color = this.link.getSpecificColor();
		}

		final LinkType linkType = link.getType();
		final UStroke suggestedStroke = styleLine.getStroke();
		final UStroke defaultThickness = diagram.getSkinParam().getThickness(LineParam.arrow, null);

		UStroke stroke;
		if (suggestedStroke == null || linkType.getStyle().isNormal() == false)
			stroke = linkType.getStroke3(defaultThickness);
		else
			stroke = linkType.getStroke3(suggestedStroke);

		if (link.getColors() != null && link.getColors().getSpecificLineStroke() != null)
			stroke = link.getColors().getSpecificLineStroke();

		final MyElkCluster elkCluster1 = elkClusters.get(link.getEntity1());
		final MyElkCluster elkCluster2 = elkClusters.get(link.getEntity2());
		final IEntityImage elkNode1 = nodeImages.get(link.getEntity1());
		final IEntityImage elkNode2 = nodeImages.get(link.getEntity2());
		MagneticBorder magneticBorder1 = new MagneticBorderNone();
		MagneticBorder magneticBorder2 = new MagneticBorderNone();
		if (elkNode1 != null)
			magneticBorder1 = elkNode1.getMagneticBorder();
		else if (elkCluster1 != null)
			magneticBorder1 = elkCluster1.getMagneticBorder(ug.getStringBounder());

		if (elkNode2 != null)
			magneticBorder2 = elkNode2.getMagneticBorder();
		if (elkCluster2 != null)
			magneticBorder2 = elkCluster2.getMagneticBorder(ug.getStringBounder());

		ug = ug.apply(stroke).apply(color);

		final List<ElkEdgeSection> sections = edge.getSections();
		if (sections.size() == 0) {
			// ELK leaves some hierarchy-crossing edges unrouted: skip them rather than fail
			Log.info(() -> "ELK: no section for edge " + link);
			return;
		}

		List<XPoint2D> first = null;
		List<XPoint2D> last = null;
		final double radius = getCornerRadius();
		for (ElkEdgeSection section : sections) {
			final List<XPoint2D> polyline = getPolyline(ug.getStringBounder(), section, magneticBorder1,
					magneticBorder2);
			// A monotone staircase planned to be drawn as one corner
			final ElkStaircaseSimplifier.Collapse collapse = collapses.get(section);
			final List<XPoint2D> points = collapse == null ? polyline : collapse.apply(polyline);
			ug.apply(HColors.none().bg()).draw(toPath(points, radius));
			if (first == null)
				first = points;
			last = points;
		}

		// Each arrowhead points along the segment it ends
		final XPoint2D end = last.get(last.size() - 1);
		final XPoint2D start = first.get(0);
		final UDrawable extremityFactory1 = getDecors(link.getType().getDecor1(), angle(beforeLast(last), end),
				HColors.WHITE);
		final UDrawable extremityFactory2 = getDecors(link.getType().getDecor2(), angle(afterFirst(first), start),
				HColors.WHITE);

		if (extremityFactory1 != null)
			extremityFactory1.drawU(ug.apply(stroke.onlyThickness()).apply(UTranslate.point(end)));

		if (extremityFactory2 != null)
			extremityFactory2.drawU(ug.apply(stroke.onlyThickness()).apply(UTranslate.point(start)));

		drawLabels(ugOrig);
	}

	private UDrawable getDecors(LinkDecor decors, double angle, HColor backColor) {
		// For legacy reason, extends are treated differently
		if (decors == LinkDecor.EXTENDS)
			return new ExtremityFactoryExtends(backColor).createUDrawable(new XPoint2D(0, 0), angle, null);

		final ExtremityFactory extremityFactory = decors.getExtremityFactoryLegacy(backColor);
		if (extremityFactory == null)
			return null;

		return extremityFactory.createUDrawable(new XPoint2D(0, 0), angle, null);
	}

	private void drawLabels(UGraphic ug) {
		XPoint2D tailLabelPos = null;
		XPoint2D headLabelPos = null;
		for (ElkLabel label : edge.getLabels()) {
			final double x = label.getX();
			final double y = label.getY();
			final TextBlock labelLink;
			// Nasty trick: we store the type of label (center/head/tail) in the text
			final String type = label.getText();
			if ("X".equals(type))
				labelLink = centerLabel;
			else if ("1".equals(type)) {
				labelLink = tailLabel;
				tailLabelPos = new XPoint2D(x, y);
			} else if ("2".equals(type)) {
				labelLink = headLabel;
				headLabelPos = new XPoint2D(x, y);
			} else
				continue;

			labelLink.drawU(ug.apply(new UTranslate(x, y)));
		}

		// Draw role labels at reflected positions
		final List<ElkEdgeSection> sections = edge.getSections();
		if (sections.size() > 0) {
			final ElkEdgeSection section = sections.get(0);
			final XPoint2D startPt = new XPoint2D(section.getStartX(), section.getStartY());
			final XPoint2D endPt = new XPoint2D(section.getEndX(), section.getEndY());

			if (tailRole != null && tailLabelPos != null)
				drawRoleLabel(ug, tailRole, tailLabel, tailLabelPos, startPt, endPt);

			if (headRole != null && headLabelPos != null)
				drawRoleLabel(ug, headRole, headLabel, headLabelPos, endPt, startPt);
		}
	}

	private void drawRoleLabel(UGraphic ug, TextBlock role, TextBlock quantifier,
			XPoint2D quantifierPos, XPoint2D thisEndpoint, XPoint2D otherEndpoint) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XPoint2D pos = RoleLabels.getPosition(quantifier.calculateDimension(stringBounder),
				role.calculateDimension(stringBounder), quantifierPos, thisEndpoint, otherEndpoint, null,
				Collections.<RectangleArea>emptyList());
		role.drawU(ug.apply(new UTranslate(pos.getX(), pos.getY())));
	}

	// The points of a section, its ends moved onto the outline of their nodes
	private List<XPoint2D> getPolyline(StringBounder stringBounder, ElkEdgeSection section,
			MagneticBorder magneticBorder1, MagneticBorder magneticBorder2) {
		final List<XPoint2D> result = new ArrayList<>();
		final XPoint2D tmpStart = new XPoint2D(section.getStartX(), section.getStartY());
		final UTranslate force1 = magneticBorder1.getForceAt(stringBounder, translate.getTranslated(tmpStart));
		result.add(force1.getTranslated(tmpStart));

		for (ElkBendPoint pt : section.getBendPoints())
			result.add(new XPoint2D(pt.getX(), pt.getY()));

		final XPoint2D tmpEnd = new XPoint2D(section.getEndX(), section.getEndY() + magicY2);
		final UTranslate force2 = magneticBorder2.getForceAt(stringBounder, translate.getTranslated(tmpEnd));
		result.add(force2.getTranslated(tmpEnd));
		return result;
	}

	private double getCornerRadius() {
		final String value = diagram.getPragma().getValue(PragmaKey.EDGE_CORNER_RADIUS);
		if (value == null)
			return 0;
		try {
			return Math.max(0, Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	// One path per section, so the joints of a thick line have no notch; each
	// bend rounded by "!pragma edgeCornerRadius"
	static UPath toPath(List<XPoint2D> points, double radius) {
		final UPath path = UPath.none();
		path.moveTo(points.get(0));
		for (int i = 1; i < points.size() - 1; i++) {
			final XPoint2D before = points.get(i - 1);
			final XPoint2D corner = points.get(i);
			final XPoint2D after = points.get(i + 1);
			final double in = before.distance(corner);
			final double out = corner.distance(after);
			final double r = Math.min(radius, Math.min(in, out) / 2);
			if (r <= 0) {
				path.lineTo(corner);
				continue;
			}
			path.lineTo(towards(corner, before, r / in));
			path.quadTo(corner, towards(corner, after, r / out));
		}
		path.lineTo(points.get(points.size() - 1));
		return path;
	}

	private static XPoint2D towards(XPoint2D from, XPoint2D to, double ratio) {
		return new XPoint2D(from.getX() + (to.getX() - from.getX()) * ratio,
				from.getY() + (to.getY() - from.getY()) * ratio);
	}

	// The last point before the end that is not the end itself
	private static XPoint2D beforeLast(List<XPoint2D> points) {
		final XPoint2D end = points.get(points.size() - 1);
		for (int i = points.size() - 2; i >= 0; i--)
			if (points.get(i).distance(end) > 0.01)
				return points.get(i);
		return end.move(0, -1);
	}

	private static XPoint2D afterFirst(List<XPoint2D> points) {
		final XPoint2D start = points.get(0);
		for (int i = 1; i < points.size(); i++)
			if (points.get(i).distance(start) > 0.01)
				return points.get(i);
		return start.move(0, 1);
	}

	// Direction from a to b: PI/2 is down
	private static double angle(XPoint2D a, XPoint2D b) {
		return Math.atan2(b.getY() - a.getY(), b.getX() - a.getX());
	}

}
