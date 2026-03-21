/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2025, Arnaud Roques
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
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.abel.CucaNote;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.abel.LinkArrow;
import net.sourceforge.plantuml.abel.LinkStrategy;
import net.sourceforge.plantuml.abel.NoteLinkStrategy;
import net.sourceforge.plantuml.annotation.DuplicateCode;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.cruise.XAbstractEdge;
import net.sourceforge.plantuml.cruise.XEdge;
import net.sourceforge.plantuml.cucadiagram.EntityPort;
import net.sourceforge.plantuml.decoration.LinkDecor;
import net.sourceforge.plantuml.decoration.LinkMiddleDecor;
import net.sourceforge.plantuml.decoration.LinkType;
import net.sourceforge.plantuml.decoration.Rainbow;
import net.sourceforge.plantuml.descdiagram.command.StringWithArrow;
import net.sourceforge.plantuml.dot.DotSplines;
import net.sourceforge.plantuml.dot.GraphvizVersion;
import net.sourceforge.plantuml.klimt.UGroup;
import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.awt.XColor;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.Colors;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.CreoleMode;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.BezierUtils;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.MagneticBorder;
import net.sourceforge.plantuml.klimt.geom.PointAndAngle;
import net.sourceforge.plantuml.klimt.geom.Positionable;
import net.sourceforge.plantuml.klimt.geom.PositionableUtils;
import net.sourceforge.plantuml.klimt.geom.Side;
import net.sourceforge.plantuml.klimt.geom.VerticalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XCubicCurve2D;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.skin.AlignmentParam;
import net.sourceforge.plantuml.skin.ColorParam;
import net.sourceforge.plantuml.skin.LineParam;
import net.sourceforge.plantuml.skin.Pragma;
import net.sourceforge.plantuml.skin.PragmaKey;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.extremity.Extremity;
import net.sourceforge.plantuml.svek.extremity.ExtremityArrow;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactory;
import net.sourceforge.plantuml.svek.extremity.ExtremityFactoryExtends;
import net.sourceforge.plantuml.svek.extremity.ExtremityOther;
import net.sourceforge.plantuml.svek.image.EntityImageNoteLink;
import net.sourceforge.plantuml.abel.EntityPosition;
import net.sourceforge.plantuml.svek.image.EntityImagePort;
import net.sourceforge.plantuml.teavm.TeaVM;
import net.sourceforge.plantuml.url.Url;
import net.sourceforge.plantuml.utils.Direction;
import net.sourceforge.plantuml.utils.Log;
import net.sourceforge.plantuml.utils.Position;

@DuplicateCode(reference = "SvekEdge, CucaDiagramFileMakerElk, CucaDiagramFileMakerSmetana")
public class SvekEdge extends XAbstractEdge implements XEdge, UDrawable {

	private static final XDimension2D CONSTRAINT_SPOT = new XDimension2D(10, 10);

	private final Cluster ltail;
	private final Cluster lhead;

	private final EntityPort startUid;
	private final EntityPort endUid;

	private final TextBlock startTailText;
	private final TextBlock endHeadText;
	private final TextBlock startTailRoleText;
	private final TextBlock endHeadRoleText;
	private final TextBlock labelText;
	private boolean divideLabelWidthByTwo = false;

	private final int lineColor;
	private final int noteLabelColor;
	private final int startTailColor;
	private final int endHeadColor;

	private final StringBounder stringBounder;

	private DotPath dotPath;
	private DotPath dotPathInit;

	private Positionable startTailLabelXY;
	private Positionable endHeadLabelXY;
	private Positionable labelXY;

	private UDrawable extremity1;
	private UDrawable extremity2;

	private double dx;
	private double dy;

	private boolean opale;
	private Cluster projectionCluster;

	private final Pragma pragma;
	private final HColor backgroundColor;
	private final boolean useRankSame;
	private final UStroke defaultThickness;
	private HColor arrowLollipopColor;

	private final double labelShield;

	// Tee junction points where this edge branches off a shared trunk
	private final List<XPoint2D> teeJunctions = new ArrayList<XPoint2D>();
	// Trunk extension lines: from myTurnX to farthestTurnX at portY
	private final List<double[]> teeTrunks = new ArrayList<double[]>();

	@Override
	public String toString() {
		return super.toString() + " color=" + lineColor;
	}

	private LinkStrategy getLinkStrategy() {
		return link.getLinkStrategy();
	}

	public Direction getArrowDirection() {
		if (getLinkArrow() == LinkArrow.BACKWARD)
			return getArrowDirectionInternal().getInv();

		return getArrowDirectionInternal();
	}

	private Direction getArrowDirectionInternal() {
		if (isAutolink()) {
			final double startAngle = dotPath.getStartAngle();
			return Direction.LEFT;
		}
		final XPoint2D start = dotPath.getStartPoint();
		final XPoint2D end = dotPath.getEndPoint();
		final double ang = Math.atan2(end.getX() - start.getX(), end.getY() - start.getY());
		if (ang > -Math.PI / 4 && ang < Math.PI / 4)
			return Direction.DOWN;

		if (ang > Math.PI * 3 / 4 || ang < -Math.PI * 3 / 4)
			return Direction.UP;

		return end.getX() > start.getX() ? Direction.RIGHT : Direction.LEFT;
	}

	public double getArrowDirectionInRadian() {
		if (getLinkArrow() == LinkArrow.BACKWARD)
			return Math.PI + getArrowDirectionInRadianInternal();

		return getArrowDirectionInRadianInternal();
	}

	private double getArrowDirectionInRadianInternal() {
		if (isAutolink()) {
			final double startAngle = dotPath.getStartAngle();
			return startAngle;
		}
		final XPoint2D start = dotPath.getStartPoint();
		final XPoint2D end = dotPath.getEndPoint();
		final double ang = Math.atan2(end.getX() - start.getX(), end.getY() - start.getY());
		return ang;
	}

	private Cluster getCluster2(Bibliotekon bibliotekon, Entity entityMutable) {
		for (Cluster cl : bibliotekon.allCluster())
			if (cl.getGroups().contains(entityMutable))
				return cl;

		throw new IllegalArgumentException();
	}

	public SvekEdge(Link link, ISkinParam skinParam, StringBounder stringBounder, FontConfiguration font,
			FontConfiguration cardinalityFont, Bibliotekon bibliotekon, Pragma pragma,
			GraphvizVersion graphvizVersion) {
		super(link, skinParam, bibliotekon);

		if (!TeaVM.isTeaVM()) {
			if (graphvizVersion.useShieldForQuantifier()
					&& (link.getLinkArg().getQuantifier1() != null || link.getLinkArg().getRole1() != null))
				link.getEntity1().ensureMargins(Margins.uniform(16));

			if (graphvizVersion.useShieldForQuantifier()
					&& (link.getLinkArg().getQuantifier2() != null || link.getLinkArg().getRole2() != null))
				link.getEntity2().ensureMargins(Margins.uniform(16));
		}

		if (link.getLinkArg().getKal1() != null)
			this.kal1 = new Kal(this, link.getLinkArg().getKal1(), skinParam, link.getEntity1(), link, stringBounder);

		if (link.getLinkArg().getKal2() != null)
			this.kal2 = new Kal(this, link.getLinkArg().getKal2(), skinParam, link.getEntity2(), link, stringBounder);

		this.useRankSame = skinParam.useRankSame();
		this.startUid = link.getEntityPort1(bibliotekon);
		this.endUid = link.getEntityPort2(bibliotekon);

		Cluster ltail = null;
		if (startUid.startsWith(Cluster.CENTER_ID))
			ltail = getCluster2(bibliotekon, link.getEntity1());

		Cluster lhead = null;
		if (endUid.startsWith(Cluster.CENTER_ID))
			lhead = getCluster2(bibliotekon, link.getEntity2());

		if (link.getColors() != null) {
			skinParam = link.getColors().mute(skinParam);
			font = font.mute(link.getColors());
		}
		this.backgroundColor = skinParam.getBackgroundColor();
		this.defaultThickness = skinParam.getThickness(LineParam.arrow, null);
		this.arrowLollipopColor = skinParam.getHtmlColor(ColorParam.arrowLollipop, null, false);
		if (arrowLollipopColor == null)
			this.arrowLollipopColor = backgroundColor;

		this.pragma = pragma;
		this.stringBounder = stringBounder;
		this.ltail = ltail;
		this.lhead = lhead;

		this.lineColor = bibliotekon.getColorSequence().getValue();
		this.noteLabelColor = bibliotekon.getColorSequence().getValue();
		this.startTailColor = bibliotekon.getColorSequence().getValue();
		this.endHeadColor = bibliotekon.getColorSequence().getValue();

		TextBlock labelOnly;
		if (Display.isNull(link.getLabel())) {
			labelOnly = TextBlockUtils.EMPTY_TEXT_BLOCK;
			if (getLinkArrow() != LinkArrow.NONE_OR_SEVERAL)
				labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);

		} else {
			final HorizontalAlignment alignment = getMessageTextAlignment(diagramType(), skinParam);
			final boolean hasSeveralGuideLines = link.getLabel().hasSeveralGuideLines();
			final TextBlock block;
			if (hasSeveralGuideLines)
				block = StringWithArrow.addSeveralMagicArrows(link.getLabel(), this, font, alignment, skinParam);
			else
				block = link.getLabel().create0(font, alignment, skinParam, skinParam.maxMessageSize(),
						CreoleMode.SIMPLE_LINE, null, null);

			labelOnly = addVisibilityModifier(block, link, skinParam);
			if (getLinkArrow() != LinkArrow.NONE_OR_SEVERAL && hasSeveralGuideLines == false)
				labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);

		}

		final CucaNote note = link.getNote();
		if (note == null) {
			labelText = labelOnly;
		} else {
			final TextBlock noteOnly = new EntityImageNoteLink(note.getDisplay(), note.getColors(), skinParam,
					link.getStyleBuilder());
			if (note.getStrategy() == NoteLinkStrategy.HALF_NOT_PRINTED
					|| note.getStrategy() == NoteLinkStrategy.HALF_PRINTED_FULL)
				divideLabelWidthByTwo = true;

			if (note.getPosition() == Position.LEFT)
				labelText = TextBlockUtils.mergeLR(noteOnly, labelOnly, VerticalAlignment.CENTER);
			else if (note.getPosition() == Position.RIGHT)
				labelText = TextBlockUtils.mergeLR(labelOnly, noteOnly, VerticalAlignment.CENTER);
			else if (note.getPosition() == Position.TOP)
				labelText = TextBlockUtils.mergeTB(noteOnly, labelOnly, HorizontalAlignment.CENTER);
			else
				labelText = TextBlockUtils.mergeTB(labelOnly, noteOnly, HorizontalAlignment.CENTER);

		}

		if (link.getQuantifier1() == null)
			startTailText = null;
		else
			startTailText = Display.getWithNewlines(skinParam.getPragma(), link.getQuantifier1())
					.create(cardinalityFont, HorizontalAlignment.CENTER, skinParam);

		if (link.getQuantifier2() == null)
			endHeadText = null;
		else
			endHeadText = Display.getWithNewlines(skinParam.getPragma(), link.getQuantifier2()).create(cardinalityFont,
					HorizontalAlignment.CENTER, skinParam);

		if (link.getRole1() == null)
			startTailRoleText = null;
		else
			startTailRoleText = Display.getWithNewlines(skinParam.getPragma(), link.getRole1()).create(cardinalityFont,
					HorizontalAlignment.CENTER, skinParam);

		if (link.getRole2() == null)
			endHeadRoleText = null;
		else
			endHeadRoleText = Display.getWithNewlines(skinParam.getPragma(), link.getRole2()).create(cardinalityFont,
					HorizontalAlignment.CENTER, skinParam);

		if (link.getType().getMiddleDecor() == LinkMiddleDecor.NONE)
			this.labelShield = 0;
		else
			this.labelShield = 7;

	}

	private Kal kal1;
	private Kal kal2;

	private TextBlock addVisibilityModifier(TextBlock block, Link link, ISkinParam skinParam) {
		final VisibilityModifier visibilityModifier = link.getVisibilityModifier();
		if (visibilityModifier != null) {
			final Rose rose = new Rose();
			final HColor fore = rose.getHtmlColor(skinParam, visibilityModifier.getForeground());
			TextBlock visibility = visibilityModifier.getUBlock(skinParam.classAttributeIconSize(), fore, null, false);
			visibility = TextBlockUtils.withMargin(visibility, 0, 1, 2, 0);
			block = TextBlockUtils.mergeLR(visibility, block, VerticalAlignment.CENTER);
		}
		final double marginLabel = startUid.equalsId(endUid) ? 6 : 1;
		return TextBlockUtils.withMargin(block, marginLabel, marginLabel);
	}

	private HorizontalAlignment getMessageTextAlignment(DiagramType diagramType, ISkinParam skinParam) {
		if (diagramType == DiagramType.STATE)
			return skinParam.getHorizontalAlignment(AlignmentParam.stateMessageAlignment, null, false, null);

		return skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER);
	}

	public boolean hasNoteLabelText() {
		return labelText != null && labelText != TextBlockUtils.EMPTY_TEXT_BLOCK;
	}

	private LinkArrow getLinkArrow() {
		return link.getLinkArrow();
	}

	public void appendLine(GraphvizVersion graphvizVersion, StringBuilder sb, DotMode dotMode, DotSplines dotSplines) {
		// Log.println("inverted=" + isInverted());
		// if (isInverted()) {
		// sb.append(endUid);
		// sb.append("->");
		// sb.append(startUid);
		// } else {
		sb.append(startUid.getFullString());
		sb.append("->");
		sb.append(endUid.getFullString());
		// }
		sb.append("[");
		final LinkType linkType = link.getTypePatchCluster();
		String decoration = linkType.getSpecificDecorationSvek(getLinkStrategy());
		if (decoration.length() > 0 && decoration.endsWith(",") == false)
			decoration += ",";

		sb.append(decoration);

		int length = link.getLength();

		if (!TeaVM.isTeaVM()) {
			if (graphvizVersion.ignoreHorizontalLinks() && length == 1)
				length = 2;
		}

		if (useRankSame) {
			if (pragma.isDefine(PragmaKey.HORIZONTAL_LINE_BETWEEN_DIFFERENT_PACKAGE_ALLOWED) || link.isInvis()
					|| length != 1) {
				// if (graphvizVersion.isJs() == false) {
				sb.append("minlen=" + (length - 1));
				sb.append(",");
				// }
			}
		} else {
			sb.append("minlen=" + (length - 1));
			sb.append(",");
		}
		sb.append("color=\"" + XColor.toHexRGBColor(lineColor) + "\"");
		if (hasNoteLabelText() || link.getLinkConstraint() != null) {
			sb.append(",");
			if (TeaVM.isTeaVM()) {
				sb.append("label=<");
			} else if (graphvizVersion.useXLabelInsteadOfLabel() || dotMode == DotMode.NO_LEFT_RIGHT_AND_XLABEL
					|| dotSplines == DotSplines.ORTHO) {
				sb.append("xlabel=<");
			} else {
				sb.append("label=<");
			}
			XDimension2D dimNote = hasNoteLabelText() ? labelText.calculateDimension(stringBounder) : CONSTRAINT_SPOT;
			dimNote = dimNote.delta(2 * labelShield);

			appendTable(sb, eventuallyDivideByTwo(dimNote), noteLabelColor, graphvizVersion);
			sb.append(">");
		}

		if (startTailText != null) {
			sb.append(",");
			sb.append("taillabel=<");
			appendTable(sb, startTailText.calculateDimension(stringBounder), startTailColor, graphvizVersion);
			sb.append(">");
		} else if (startTailRoleText != null) {
			sb.append(",");
			sb.append("taillabel=<");
			appendTable(sb, startTailRoleText.calculateDimension(stringBounder), startTailColor, graphvizVersion);
			sb.append(">");
		}
		if (endHeadText != null) {
			sb.append(",");
			sb.append("headlabel=<");
			appendTable(sb, endHeadText.calculateDimension(stringBounder), endHeadColor, graphvizVersion);
			sb.append(">");
		} else if (endHeadRoleText != null) {
			sb.append(",");
			sb.append("headlabel=<");
			appendTable(sb, endHeadRoleText.calculateDimension(stringBounder), endHeadColor, graphvizVersion);
			sb.append(">");
		}

		if (link.isInvis()) {
			sb.append(",");
			sb.append("style=invis");
		}

		if (link.isConstraint() == false || link.hasTwoEntryPointsSameContainer())
			sb.append(",constraint=false");

		if (link.getSametail() != null)
			sb.append(",sametail=" + link.getSametail());

		sb.append("];");
		SvekUtils.println(sb);
	}

	private XDimension2D eventuallyDivideByTwo(XDimension2D dim) {
		if (divideLabelWidthByTwo)
			return new XDimension2D(dim.getWidth() / 2, dim.getHeight());

		return dim;
	}

	public String rankSame() {
		// if (graphvizVersion == GraphvizVersion.V2_34_0) {
		// return null;
		// }
		if (pragma.isDefine(PragmaKey.HORIZONTAL_LINE_BETWEEN_DIFFERENT_PACKAGE_ALLOWED) == false
				&& link.getLength() == 1
		/* && graphvizVersion.isJs() == false */) {
			return "{rank=same; " + getStartUidPrefix() + "; " + getEndUidPrefix() + "}";
		}
		return null;
	}

	public static void appendTable(StringBuilder sb, XDimension2D dim, int col, GraphvizVersion graphvizVersion) {
		final int w = (int) dim.getWidth();
		final int h = (int) dim.getHeight();
		appendTable(sb, w, h, col);
	}

	public static void appendTable(StringBuilder sb, int w, int h, int col) {
		sb.append("<TABLE ");
		sb.append("BGCOLOR=\"" + XColor.toHexRGBColor(col) + "\" ");
		sb.append("FIXEDSIZE=\"TRUE\" WIDTH=\"" + w + "\" HEIGHT=\"" + h + "\">");
		sb.append("<TR");
		sb.append(">");
		sb.append("<TD");
		// sb.append(" FIXEDSIZE=\"TRUE\" WIDTH=\"" + 0 + "\" HEIGHT=\"" + 0 +
		// "\"");
		sb.append(">");
		sb.append("</TD>");
		sb.append("</TR>");
		sb.append("</TABLE>");
	}

	public final String getStartUidPrefix() {
		return startUid.getPrefix();
	}

	public final String getEndUidPrefix() {
		return endUid.getPrefix();
	}

	private UDrawable getExtremitySpecial(XPoint2D center, LinkDecor decor, double angle, Cluster cluster,
			SvekNode nodeContact) {
		final ExtremityFactory extremityFactory = decor.getExtremityFactoryLegacy(backgroundColor);
		return extremityFactory.createUDrawable(center, angle, null);
	}

	private UDrawable getExtremitySimplier(XPoint2D center, ExtremityFactory extremityFactory, double angle,
			Cluster cluster, SvekNode nodeContact, boolean isStart, Kal kal) {
		if (extremityFactory == null)
			return null;

		Side side = null;
		if (nodeContact != null)
			side = nodeContact.getRectangleArea().getClosestSide(center);

		final UTranslate translateForKal;
		if (kal == null) {
			translateForKal = new UTranslate(0, 0);
		} else {
			translateForKal = kal.getTranslateForDecoration();
			center = translateForKal.getTranslated(center);
		}

		final Extremity extremity = (Extremity) extremityFactory.createUDrawable(center, angle, side);
		final double decorationLength = extremity.getDecorationLength();
		if (isStart)
			dotPath.moveStartPoint(
					translateForKal.compose(new UTranslate(decorationLength, 0).rotate(angle - Math.PI)));
		else
			dotPath.moveEndPoint(translateForKal.compose(new UTranslate(decorationLength, 0).rotate(angle - Math.PI)));

		return extremity;
	}

	private UDrawable getExtremity(final XPoint2D center, LinkDecor decor, PointListIterator pointListIterator,
			double angle, Cluster cluster, SvekNode nodeContact) {
		final ExtremityFactory extremityFactory = decor.getExtremityFactoryLegacy(backgroundColor);

		if (cluster != null) {
			if (extremityFactory != null) {
				// System.err.println("angle=" + angle * 180 / Math.PI);
				return extremityFactory.createUDrawable(center, angle, null);
			}
			if (decor == LinkDecor.EXTENDS)
				return new ExtremityFactoryExtends(backgroundColor).createUDrawable(center, angle, null);

			return null;
		}

		if (extremityFactory != null) {
			final List<XPoint2D> points = pointListIterator.next();
			if (points.size() == 0)
				return null;
			// throw new IllegalStateException();
			// return extremityFactory.createUDrawable(center, angle, null);

			final XPoint2D p0 = points.get(0);
			final XPoint2D p1 = points.get(1);
			final XPoint2D p2 = points.get(2);

			Side side = null;
			if (nodeContact != null)
				side = nodeContact.getRectangleArea().getClosestSide(p1);

			return extremityFactory.createTBRDrawableLegacy(p0, p1, p2, side);
		} else if (decor == LinkDecor.NONE) {
			final UPolygon sh = new UPolygon(pointListIterator.cloneMe().next());
			final XPoint2D contact = sh.checkMiddleContactForSpecificTriangle(center);
			if (contact != null) {
				return new UDrawable() {
					public void drawU(UGraphic ug) {
						ULine line = new ULine(contact.getX() - center.getX(), contact.getY() - center.getY());
						ug = ug.apply(UTranslate.point(center));
						ug.draw(line);
					}
				};
			}
		} else if (decor != LinkDecor.NONE) {
			final UPolygon sh = new UPolygon(pointListIterator.next());
			return new ExtremityOther(sh);
		}
		return null;

	}

	public void solveLine(SvgResult fullSvg) {
		if (this.link.isInvis())
			return;

		int idx = fullSvg.getIndexFromColor(this.lineColor);
		if (idx == -1) {
			return;
			// throw new IllegalStateException();
		}
		idx = fullSvg.indexOf("d=\"", idx);
		if (idx == -1)
			throw new IllegalStateException();

		final int end = fullSvg.indexOf("\"", idx + 3);
		final SvgResult path = fullSvg.substring(idx + 3, end);

		if (path.isPathConsistent() == false)
			return;

		dotPath = path.toDotPath();

		final XPoint2D tmpStartPoint = dotPath.getStartPoint();
		final XPoint2D tmpEndPoint = dotPath.getEndPoint();

		final SvekNode svekNode1 = getSvekNode1();
		final SvekNode svekNode2 = getSvekNode2();
		if (svekNode1 != null && svekNode2 != null) {
			final XPoint2D tmpPos1 = svekNode1.getRectangleArea().getPointCenter();
			final XPoint2D tmpPos2 = svekNode2.getRectangleArea().getPointCenter();

			final double normal = tmpStartPoint.distance(tmpPos1) + tmpEndPoint.distance(tmpPos2);
			final double inversed = tmpStartPoint.distance(tmpPos2) + tmpEndPoint.distance(tmpPos1);

			// Sometime, GraphViz inverses the result line.
			if (inversed < normal)
				// So we reverse the inversion...
				dotPath = dotPath.reverse();
		}

		// Used for Kal
		dotPathInit = dotPath.copy();

		if (projectionCluster != null) {
			projectionCluster.manageEntryExitPoint(stringBounder);
		}
		dotPath = dotPath.simulateCompound(lhead == null ? null : lhead.getRectangleArea(),
				ltail == null ? null : ltail.getRectangleArea());

		final SvgResult lineSvg = fullSvg.substring(end);
		PointListIterator pointListIterator = null;

		final LinkType linkType = link.getType();

		if (getLinkStrategy() == LinkStrategy.SIMPLIER) {
			this.extremity1 = getExtremitySimplier(dotPath.getStartPoint(),
					linkType.getDecor2().getExtremityFactoryComplete(backgroundColor),
					dotPath.getStartAngle() + Math.PI, ltail, svekNode1, true, kal1);
			this.extremity2 = getExtremitySimplier(dotPath.getEndPoint(),
					linkType.getDecor1().getExtremityFactoryComplete(backgroundColor), dotPath.getEndAngle(), lhead,
					svekNode2, false, kal2);
		} else {
			pointListIterator = lineSvg.getPointsWithThisColor(lineColor);
			if (link.getLength() == 1 && isThereTwo(linkType) && count(pointListIterator.cloneMe()) == 2) {
				// Sorry, this is ugly because of
				// https://github.com/plantuml/plantuml/issues/1353

				final List<XPoint2D> points = pointListIterator.next();
				final XPoint2D p1 = points.get(1);

				XPoint2D startPoint = dotPath.getStartPoint();
				XPoint2D endPoint = dotPath.getEndPoint();
				if (p1.distance(startPoint) < p1.distance(endPoint))
					startPoint = p1;
				else
					endPoint = p1;

				this.extremity1 = getExtremitySpecial(startPoint, linkType.getDecor2(),
						dotPath.getStartAngle() + Math.PI, ltail, svekNode1);
				this.extremity2 = getExtremitySpecial(endPoint, linkType.getDecor1(), dotPath.getEndAngle(), lhead,
						svekNode2);
			} else {
				this.extremity1 = getExtremity(dotPath.getStartPoint(), linkType.getDecor2(), pointListIterator,
						dotPath.getStartAngle() + Math.PI, ltail, svekNode1);
				this.extremity2 = getExtremity(dotPath.getEndPoint(), linkType.getDecor1(), pointListIterator,
						dotPath.getEndAngle(), lhead, svekNode2);
			}
		}

		if (link.getEntity1().getLeafType() == LeafType.LOLLIPOP_HALF)
			svekNode1.addImpact(dotPath.getStartAngle() + Math.PI);

		if (link.getEntity2().getLeafType() == LeafType.LOLLIPOP_HALF)
			svekNode2.addImpact(dotPath.getEndAngle());

		if (getLinkStrategy() == LinkStrategy.LEGACY_toberemoved && extremity1 instanceof Extremity
				&& extremity2 instanceof Extremity) {
			final XPoint2D p1 = ((Extremity) extremity1).somePoint();
			final XPoint2D p2 = ((Extremity) extremity2).somePoint();
			if (p1 != null && p2 != null) {
				// http://plantuml.sourceforge.net/qa/?qa=4240/some-relations-point-wrong-direction-when-the-linetype-ortho
				final double dist1start = p1.distance(dotPath.getStartPoint());
				final double dist1end = p1.distance(dotPath.getEndPoint());
				final double dist2start = p2.distance(dotPath.getStartPoint());
				final double dist2end = p2.distance(dotPath.getEndPoint());
				if (dist1start > dist1end && dist2end > dist2start) {
					pointListIterator = lineSvg.getPointsWithThisColor(lineColor);
					this.extremity2 = getExtremity(dotPath.getEndPoint(), linkType.getDecor1(), pointListIterator,
							dotPath.getEndAngle(), lhead, svekNode2);
					this.extremity1 = getExtremity(dotPath.getStartPoint(), linkType.getDecor2(), pointListIterator,
							dotPath.getStartAngle() + Math.PI, ltail, svekNode1);
				}
			}

		}

		if (hasNoteLabelText() || link.getLinkConstraint() != null) {
			final XPoint2D pos = getXY(fullSvg, this.noteLabelColor);
			if (pos != null) {
//				corner1.manage(pos);
				this.labelXY = hasNoteLabelText() ? TextBlockUtils.asPositionable(labelText, stringBounder, pos)
						: TextBlockUtils.asPositionable(CONSTRAINT_SPOT, stringBounder, pos);
			}
		}

		if (this.startTailText != null || this.startTailRoleText != null) {
			final XPoint2D pos = getXY(fullSvg, this.startTailColor);
			if (pos != null) {
//				corner1.manage(pos);
				final TextBlock forSize = this.startTailText != null ? startTailText : startTailRoleText;
				this.startTailLabelXY = TextBlockUtils.asPositionable(forSize, stringBounder, pos);
			}
		}

		if (this.endHeadText != null || this.endHeadRoleText != null) {
			final XPoint2D pos = getXY(fullSvg, this.endHeadColor);
			if (pos != null) {
//				corner1.manage(pos);
				final TextBlock forSize = this.endHeadText != null ? endHeadText : endHeadRoleText;
				this.endHeadLabelXY = TextBlockUtils.asPositionable(forSize, stringBounder, pos);
//				corner1.manage(pos.getX() - 15, pos.getY());
			}
		}

		if (isOpalisable() == false)
			setOpale(false);

	}

	private boolean isThereTwo(final LinkType linkType) {
		return linkType.getDecor2().getExtremityFactoryLegacy(backgroundColor) != null
				&& linkType.getDecor1().getExtremityFactoryLegacy(backgroundColor) != null;
	}

	private int count(PointListIterator it) {
		int nb = 0;
		while (it.hasNext()) {
			it.next();
			nb++;
		}
		return nb;
	}

	private DotPath adjustPathForInsidePorts(DotPath path, SvekNode node1, SvekNode node2,
			double offsetX, double offsetY) {
		final boolean startInside = node1 != null && node1.getEntityPosition().isPort()
				&& EntityImagePort.hasInsideLabel(node1.getEntity());
		final boolean endInside = node2 != null && node2.getEntityPosition().isPort()
				&& EntityImagePort.hasInsideLabel(node2.getEntity());
		if (!startInside && !endInside)
			return path;
		final double symbolSize = 2 * EntityPosition.RADIUS;
		final XPoint2D startPoint;
		final boolean startIsEast;
		if (startInside) {
			final XPoint2D center = node1.getRectangleArea().getPointCenter();
			startIsEast = !node1.getEntityPosition().isInput();
			final double edgeX = startIsEast
					? center.getX() + symbolSize / 2 : center.getX() - symbolSize / 2;
			startPoint = new XPoint2D(edgeX - offsetX, center.getY() - offsetY);
		} else {
			startPoint = path.getStartPoint();
			startIsEast = false;
		}
		final XPoint2D endPoint;
		final boolean endIsEast;
		if (endInside) {
			final XPoint2D center = node2.getRectangleArea().getPointCenter();
			endIsEast = !node2.getEntityPosition().isInput();
			final double edgeX = endIsEast
					? center.getX() + symbolSize / 2 : center.getX() - symbolSize / 2;
			endPoint = new XPoint2D(edgeX - offsetX, center.getY() - offsetY);
		} else {
			endPoint = path.getEndPoint();
			endIsEast = false;
		}
		final List<XCubicCurve2D> beziers = new ArrayList<XCubicCurve2D>();
		if (skinParam.getDotSplines() == DotSplines.ORTHO) {
			buildOrthoPath(beziers, startPoint, endPoint, node1, node2,
					startIsEast, endIsEast, offsetX, offsetY);
		} else {
			beziers.add(new XCubicCurve2D(
					startPoint.getX(), startPoint.getY(),
					startPoint.getX(), startPoint.getY(),
					endPoint.getX(), endPoint.getY(),
					endPoint.getX(), endPoint.getY()));
		}
		return DotPath.fromBeziers(beziers);
	}

	private static void addOrthoSegment(List<XCubicCurve2D> beziers,
			double x1, double y1, double x2, double y2) {
		beziers.add(new XCubicCurve2D(x1, y1, x1, y1, x2, y2, x2, y2));
	}

	private void buildOrthoPath(List<XCubicCurve2D> beziers,
			XPoint2D startPoint, XPoint2D endPoint,
			SvekNode node1, SvekNode node2,
			boolean startIsEast, boolean endIsEast,
			double offsetX, double offsetY) {
		final double margin = 8;
		final double minStub = 3 * 2 * EntityPosition.RADIUS;
		final double staggerSpacing = 3 * EntityPosition.RADIUS;
		Cluster srcCluster = null;
		Cluster dstCluster = null;
		for (Cluster cl : bibliotekon.allCluster()) {
			if (node1 != null && cl.getNodes().contains(node1))
				srcCluster = cl;
			if (node2 != null && cl.getNodes().contains(node2))
				dstCluster = cl;
		}
		// Compute stagger indices for start and end cluster edges
		final int startEdgeIndex = computeClusterEdgeIndex(srcCluster, startIsEast, node1);
		final int endEdgeIndex = computeClusterEdgeIndex(dstCluster, endIsEast, node2);
		// Use start edge index for bridge Y staggering (separates bridges from different ports)
		if (startIsEast == endIsEast) {
			// Same-side routing (EAST→EAST or WEST→WEST)
			double startTurnX = getClusterEdgeX(srcCluster, startIsEast, offsetX, margin);
			double endTurnX = getClusterEdgeX(dstCluster, endIsEast, offsetX, margin);
			// Enforce minimum stub, then stagger outward from there
			if (startIsEast) {
				startTurnX = Math.max(startTurnX, startPoint.getX() + minStub)
						+ startEdgeIndex * staggerSpacing;
				endTurnX = Math.max(endTurnX, endPoint.getX() + minStub)
						+ endEdgeIndex * staggerSpacing;
			} else {
				startTurnX = Math.min(startTurnX, startPoint.getX() - minStub)
						- startEdgeIndex * staggerSpacing;
				endTurnX = Math.min(endTurnX, endPoint.getX() - minStub)
						- endEdgeIndex * staggerSpacing;
			}
			// Check if the direct H-V-H path would cross any cluster
			boolean needsDetour = false;
			final double farX = startIsEast
					? Math.max(startTurnX, endTurnX) : Math.min(startTurnX, endTurnX);
			for (Cluster cl : bibliotekon.allCluster()) {
				final RectangleArea rect = cl.getRectangleArea();
				if (rect == null)
					continue;
				final double rMinX = rect.getMinX() - offsetX;
				final double rMaxX = rect.getMaxX() - offsetX;
				final double rMinY = rect.getMinY() - offsetY;
				final double rMaxY = rect.getMaxY() - offsetY;
				// Check start horizontal against all clusters except src
				if (cl != srcCluster) {
					final double hMinX1 = Math.min(startPoint.getX(), farX);
					final double hMaxX1 = Math.max(startPoint.getX(), farX);
					if (hMaxX1 > rMinX && hMinX1 < rMaxX
							&& startPoint.getY() >= rMinY && startPoint.getY() <= rMaxY) {
						needsDetour = true;
						break;
					}
				}
				// Check end horizontal against all clusters except dst
				if (cl != dstCluster) {
					final double hMinX2 = Math.min(endPoint.getX(), farX);
					final double hMaxX2 = Math.max(endPoint.getX(), farX);
					if (hMaxX2 > rMinX && hMinX2 < rMaxX
							&& endPoint.getY() >= rMinY && endPoint.getY() <= rMaxY) {
						needsDetour = true;
						break;
					}
				}
			}
			if (needsDetour) {
				// 5-segment path: H-V-H-V-H
				// Each port goes to just past its own parent cluster edge,
				// then a connecting horizontal bridges at a Y that avoids obstacles
				// Clamp turn X to avoid crossing intermediate clusters
				startTurnX = clampTurnX(startTurnX, startPoint, startIsEast,
						srcCluster, offsetX, offsetY, margin);
				endTurnX = clampTurnX(endTurnX, endPoint, endIsEast,
						dstCluster, offsetX, offsetY, margin);
				double midY = findSafeBridgeY(startTurnX, endTurnX,
						startPoint.getY(), endPoint.getY(),
						srcCluster, dstCluster, offsetX, offsetY, margin,
						startEdgeIndex, staggerSpacing);
				addOrthoSegment(beziers, startPoint.getX(), startPoint.getY(),
						startTurnX, startPoint.getY());
				addOrthoSegment(beziers, startTurnX, startPoint.getY(),
						startTurnX, midY);
				addOrthoSegment(beziers, startTurnX, midY,
						endTurnX, midY);
				addOrthoSegment(beziers, endTurnX, midY,
						endTurnX, endPoint.getY());
				addOrthoSegment(beziers, endTurnX, endPoint.getY(),
						endPoint.getX(), endPoint.getY());
				addTeeJunctionsForPort(node1, startPoint, startTurnX, startIsEast, srcCluster, startEdgeIndex, staggerSpacing);
				addTeeJunctionsForPort(node2, endPoint, endTurnX, endIsEast, dstCluster, endEdgeIndex, staggerSpacing);
			} else {
				addOrthoSegment(beziers, startPoint.getX(), startPoint.getY(), farX, startPoint.getY());
				addOrthoSegment(beziers, farX, startPoint.getY(), farX, endPoint.getY());
				addOrthoSegment(beziers, farX, endPoint.getY(), endPoint.getX(), endPoint.getY());
				// For simple H-V-H, the turn is at farX for both endpoints
				addTeeJunctionsForPort(node1, startPoint, farX, startIsEast, srcCluster, startEdgeIndex, staggerSpacing);
				addTeeJunctionsForPort(node2, endPoint, farX, endIsEast, dstCluster, endEdgeIndex, staggerSpacing);
			}
		} else {
			// Opposite-side routing (EAST→WEST or WEST→EAST)
			double midX = (startPoint.getX() + endPoint.getX()) / 2;
			// Stagger parallel connections so vertical segments don't overlap
			final int staggerIndex = computeStaggerIndex(srcCluster, dstCluster, node1);
			midX += staggerIndex * staggerSpacing;
			// Ensure minimum stub length from each port
			if (startIsEast)
				midX = Math.max(midX, startPoint.getX() + minStub);
			else
				midX = Math.min(midX, startPoint.getX() - minStub);
			if (endIsEast)
				midX = Math.max(midX, endPoint.getX() + minStub);
			else
				midX = Math.min(midX, endPoint.getX() - minStub);
			// Check all three segments against all clusters (except parent of each endpoint)
			// and determine if a simple H-V-H works or if we need a 5-segment detour
			boolean needsOppDetour = false;
			for (Cluster cl : bibliotekon.allCluster()) {
				final RectangleArea rect = cl.getRectangleArea();
				if (rect == null)
					continue;
				final double rMinX = rect.getMinX() - offsetX;
				final double rMaxX = rect.getMaxX() - offsetX;
				final double rMinY = rect.getMinY() - offsetY;
				final double rMaxY = rect.getMaxY() - offsetY;
				// Check start horizontal (startPoint → midX at startY) against non-src clusters
				if (cl != srcCluster) {
					final double hMinX = Math.min(startPoint.getX(), midX);
					final double hMaxX = Math.max(startPoint.getX(), midX);
					if (hMaxX > rMinX && hMinX < rMaxX
							&& startPoint.getY() >= rMinY && startPoint.getY() <= rMaxY) {
						needsOppDetour = true;
						break;
					}
				}
				// Check end horizontal (midX → endPoint at endY) against non-dst clusters
				if (cl != dstCluster) {
					final double hMinX = Math.min(midX, endPoint.getX());
					final double hMaxX = Math.max(midX, endPoint.getX());
					if (hMaxX > rMinX && hMinX < rMaxX
							&& endPoint.getY() >= rMinY && endPoint.getY() <= rMaxY) {
						needsOppDetour = true;
						break;
					}
				}
				// Check vertical segment (midX, from startY to endY) against non-src, non-dst
				if (cl != srcCluster && cl != dstCluster) {
					final double vMinY = Math.min(startPoint.getY(), endPoint.getY());
					final double vMaxY = Math.max(startPoint.getY(), endPoint.getY());
					if (midX >= rMinX && midX <= rMaxX && vMaxY >= rMinY && vMinY <= rMaxY) {
						needsOppDetour = true;
						break;
					}
				}
			}
			if (needsOppDetour) {
				// 5-segment path: route each endpoint past its own cluster edge on the port's side,
				// then bridge horizontally at a safe Y
				double startTurnX = getClusterEdgeX(srcCluster, startIsEast, offsetX, margin);
				double endTurnX = getClusterEdgeX(dstCluster, endIsEast, offsetX, margin);
				// Enforce minimum stub, then stagger outward from there
				if (startIsEast)
					startTurnX = Math.max(startTurnX, startPoint.getX() + minStub)
							+ startEdgeIndex * staggerSpacing;
				else
					startTurnX = Math.min(startTurnX, startPoint.getX() - minStub)
							- startEdgeIndex * staggerSpacing;
				if (endIsEast)
					endTurnX = Math.max(endTurnX, endPoint.getX() + minStub)
							+ endEdgeIndex * staggerSpacing;
				else
					endTurnX = Math.min(endTurnX, endPoint.getX() - minStub)
							- endEdgeIndex * staggerSpacing;
				// Clamp turn X to avoid crossing intermediate clusters
				startTurnX = clampTurnX(startTurnX, startPoint, startIsEast,
						srcCluster, offsetX, offsetY, margin);
				endTurnX = clampTurnX(endTurnX, endPoint, endIsEast,
						dstCluster, offsetX, offsetY, margin);
				double bridgeY = findSafeBridgeY(startTurnX, endTurnX,
						startPoint.getY(), endPoint.getY(),
						srcCluster, dstCluster, offsetX, offsetY, margin,
						startEdgeIndex, staggerSpacing);
				addOrthoSegment(beziers, startPoint.getX(), startPoint.getY(),
						startTurnX, startPoint.getY());
				addOrthoSegment(beziers, startTurnX, startPoint.getY(),
						startTurnX, bridgeY);
				addOrthoSegment(beziers, startTurnX, bridgeY,
						endTurnX, bridgeY);
				addOrthoSegment(beziers, endTurnX, bridgeY,
						endTurnX, endPoint.getY());
				addOrthoSegment(beziers, endTurnX, endPoint.getY(),
						endPoint.getX(), endPoint.getY());
				addTeeJunctionsForPort(node1, startPoint, startTurnX, startIsEast, srcCluster, startEdgeIndex, staggerSpacing);
				addTeeJunctionsForPort(node2, endPoint, endTurnX, endIsEast, dstCluster, endEdgeIndex, staggerSpacing);
			} else {
				addOrthoSegment(beziers, startPoint.getX(), startPoint.getY(), midX, startPoint.getY());
				addOrthoSegment(beziers, midX, startPoint.getY(), midX, endPoint.getY());
				addOrthoSegment(beziers, midX, endPoint.getY(), endPoint.getX(), endPoint.getY());
				addTeeJunctionsForPort(node1, startPoint, midX, startIsEast, srcCluster, startEdgeIndex, staggerSpacing);
				addTeeJunctionsForPort(node2, endPoint, midX, endIsEast, dstCluster, endEdgeIndex, staggerSpacing);
			}
		}
	}

	/**
	 * If this port has multiple edges (tee), add a junction dot at the turn point
	 * and record the trunk extension so the tee shape is visible.
	 * The edge with the highest cluster edge index (trunk terminus) gets no dot.
	 */
	private void addTeeJunctionsForPort(SvekNode portNode, XPoint2D portPoint,
			double turnX, boolean isEast, Cluster cluster, int myEdgeIndex,
			double staggerSpacing) {
		if (portNode == null || !portNode.getEntityPosition().isPort()
				|| !EntityImagePort.hasInsideLabel(portNode.getEntity()))
			return;
		int maxIndex = myEdgeIndex;
		int siblingCount = 0;
		for (SvekEdge edge : bibliotekon.allLines()) {
			final SvekNode n1 = bibliotekon.getNode(edge.link.getEntity1());
			final SvekNode n2 = bibliotekon.getNode(edge.link.getEntity2());
			if (n1 != portNode && n2 != portNode)
				continue;
			siblingCount++;
			if (edge == this)
				continue;
			final int idx = edge.computeClusterEdgeIndex(cluster, isEast, portNode);
			if (idx > maxIndex)
				maxIndex = idx;
		}
		if (siblingCount <= 1)
			return;
		if (myEdgeIndex < maxIndex) {
			teeJunctions.add(new XPoint2D(turnX, portPoint.getY()));
			// Draw trunk continuation past the junction in the outward direction
			final double trunkEndX = isEast
					? turnX + staggerSpacing : turnX - staggerSpacing;
			teeTrunks.add(new double[] { turnX, portPoint.getY(), trunkEndX, portPoint.getY() });
		}
	}

	private double clampTurnX(double turnX, XPoint2D point, boolean isEast,
			Cluster ownCluster, double offsetX, double offsetY, double margin) {
		// Clamp turnX so that:
		// 1. The horizontal from point to turnX doesn't cross any cluster
		// 2. turnX doesn't fall inside any cluster's X range (vertical segment would cross it)
		for (Cluster cl : bibliotekon.allCluster()) {
			if (cl == ownCluster)
				continue;
			final RectangleArea rect = cl.getRectangleArea();
			if (rect == null)
				continue;
			final double rMinX = rect.getMinX() - offsetX;
			final double rMaxX = rect.getMaxX() - offsetX;
			final double rMinY = rect.getMinY() - offsetY;
			final double rMaxY = rect.getMaxY() - offsetY;
			// Check 1: horizontal segment crosses cluster
			if (point.getY() >= rMinY && point.getY() <= rMaxY) {
				final double hMinX = Math.min(point.getX(), turnX);
				final double hMaxX = Math.max(point.getX(), turnX);
				if (hMaxX > rMinX && hMinX < rMaxX) {
					if (isEast)
						turnX = Math.min(turnX, rMinX - margin);
					else
						turnX = Math.max(turnX, rMaxX + margin);
				}
			}
			// Check 2: turnX falls inside cluster X range — vertical would cross it
			if (turnX > rMinX && turnX < rMaxX) {
				if (isEast)
					turnX = Math.min(turnX, rMinX - margin);
				else
					turnX = Math.max(turnX, rMaxX + margin);
			}
		}
		return turnX;
	}

	private double getClusterEdgeX(Cluster cluster, boolean eastSide, double offsetX, double margin) {
		if (cluster == null || cluster.getRectangleArea() == null)
			return 0;
		final RectangleArea rect = cluster.getRectangleArea();
		if (eastSide)
			return rect.getMaxX() - offsetX + margin;
		else
			return rect.getMinX() - offsetX - margin;
	}

	private int computeClusterEdgeIndex(Cluster cluster, boolean eastSide, SvekNode thisNode) {
		if (cluster == null || thisNode == null)
			return 0;
		// Collect all edges that exit the given side of this cluster via inside ports
		final List<SvekEdge> edgeList = new ArrayList<SvekEdge>();
		for (SvekEdge edge : bibliotekon.allLines()) {
			final SvekNode n1 = bibliotekon.getNode(edge.link.getEntity1());
			final SvekNode n2 = bibliotekon.getNode(edge.link.getEntity2());
			if (n1 == null || n2 == null)
				continue;
			// Check if either endpoint is an inside port on the given side of this cluster
			for (SvekNode n : new SvekNode[] { n1, n2 }) {
				if (!n.getEntityPosition().isPort() || !EntityImagePort.hasInsideLabel(n.getEntity()))
					continue;
				final boolean nIsEast = !n.getEntityPosition().isInput();
				if (nIsEast != eastSide)
					continue;
				if (cluster.getNodes().contains(n)) {
					edgeList.add(edge);
					break;
				}
			}
		}
		// Sort by the Y of the port node in this cluster
		edgeList.sort((a, b) -> {
			final SvekNode na = getPortNodeInCluster(a, cluster, eastSide);
			final SvekNode nb = getPortNodeInCluster(b, cluster, eastSide);
			return Double.compare(
					na.getRectangleArea().getPointCenter().getY(),
					nb.getRectangleArea().getPointCenter().getY());
		});
		for (int i = 0; i < edgeList.size(); i++)
			if (edgeList.get(i) == this)
				return i;
		return 0;
	}

	private SvekNode getPortNodeInCluster(SvekEdge edge, Cluster cluster, boolean eastSide) {
		final SvekNode n1 = bibliotekon.getNode(edge.link.getEntity1());
		final SvekNode n2 = bibliotekon.getNode(edge.link.getEntity2());
		for (SvekNode n : new SvekNode[] { n1, n2 }) {
			if (n != null && n.getEntityPosition().isPort()
					&& EntityImagePort.hasInsideLabel(n.getEntity())
					&& (!n.getEntityPosition().isInput()) == eastSide
					&& cluster.getNodes().contains(n))
				return n;
		}
		return n1;
	}

	private int computeStaggerIndex(Cluster srcCluster, Cluster dstCluster, SvekNode node1) {
		if (srcCluster == null || dstCluster == null)
			return 0;
		// Collect sibling edges between the same cluster pair that use inside ports
		final List<SvekNode> siblings = new ArrayList<SvekNode>();
		for (SvekEdge edge : bibliotekon.allLines()) {
			final SvekNode n1 = bibliotekon.getNode(edge.link.getEntity1());
			final SvekNode n2 = bibliotekon.getNode(edge.link.getEntity2());
			if (n1 == null || n2 == null)
				continue;
			if (!n1.getEntityPosition().isPort() || !EntityImagePort.hasInsideLabel(n1.getEntity()))
				continue;
			if (!n2.getEntityPosition().isPort() || !EntityImagePort.hasInsideLabel(n2.getEntity()))
				continue;
			Cluster c1 = null, c2 = null;
			for (Cluster cl : bibliotekon.allCluster()) {
				if (cl.getNodes().contains(n1))
					c1 = cl;
				if (cl.getNodes().contains(n2))
					c2 = cl;
			}
			if ((c1 == srcCluster && c2 == dstCluster) || (c1 == dstCluster && c2 == srcCluster))
				siblings.add(n1);
		}
		// Sort by Y center; reverse index so top connections get farthest midX
		siblings.sort((a, b) -> Double.compare(
				a.getRectangleArea().getPointCenter().getY(),
				b.getRectangleArea().getPointCenter().getY()));
		for (int i = 0; i < siblings.size(); i++)
			if (siblings.get(i) == node1)
				return siblings.size() - 1 - i;
		return 0;
	}

	private double findSafeBridgeY(double bridgeMinX, double bridgeMaxX,
			double startY, double endY,
			Cluster srcCluster, Cluster dstCluster,
			double offsetX, double offsetY, double margin,
			int staggerIndex, double staggerSpacing) {
		final double hMinX = Math.min(bridgeMinX, bridgeMaxX);
		final double hMaxX = Math.max(bridgeMinX, bridgeMaxX);
		// Collect Y intervals of clusters that overlap the bridge X range
		final List<double[]> obstacles = new ArrayList<double[]>();
		for (Cluster cl : bibliotekon.allCluster()) {
			final RectangleArea rect = cl.getRectangleArea();
			if (rect == null)
				continue;
			final double rMinX = rect.getMinX() - offsetX;
			final double rMaxX = rect.getMaxX() - offsetX;
			if (hMaxX <= rMinX || hMinX >= rMaxX)
				continue;
			obstacles.add(new double[] { rect.getMinY() - offsetY, rect.getMaxY() - offsetY });
		}
		if (obstacles.isEmpty())
			return (startY + endY) / 2 + staggerIndex * staggerSpacing;
		// Sort by top edge and merge overlapping intervals
		obstacles.sort((a, b) -> Double.compare(a[0], b[0]));
		final List<double[]> merged = new ArrayList<double[]>();
		merged.add(new double[] { obstacles.get(0)[0], obstacles.get(0)[1] });
		for (int i = 1; i < obstacles.size(); i++) {
			final double[] last = merged.get(merged.size() - 1);
			if (obstacles.get(i)[0] <= last[1]) {
				last[1] = Math.max(last[1], obstacles.get(i)[1]);
			} else {
				merged.add(new double[] { obstacles.get(i)[0], obstacles.get(i)[1] });
			}
		}
		// Find the best gap between merged obstacles (or above/below all)
		// For above/below: offset by stagger index away from obstacles
		// For gaps: subdivide the gap space
		double bestY = Double.NaN;
		double bestDist = Double.MAX_VALUE;
		final double targetY = (startY + endY) / 2;
		// Try above the first obstacle (stagger upward)
		final double aboveY = merged.get(0)[0] - margin - staggerIndex * staggerSpacing;
		double dist = Math.abs(targetY - aboveY);
		if (dist < bestDist) {
			bestDist = dist;
			bestY = aboveY;
		}
		// Try gaps between merged obstacles
		for (int i = 0; i < merged.size() - 1; i++) {
			final double gapTop = merged.get(i)[1] + margin;
			final double gapBottom = merged.get(i + 1)[0] - margin;
			final double gapSize = gapBottom - gapTop;
			if (gapSize >= staggerSpacing) {
				// Place within gap, offset by stagger index
				final double slotY = gapTop + staggerIndex * staggerSpacing;
				if (slotY <= gapBottom) {
					final double gapY = Math.max(gapTop, Math.min(gapBottom, slotY));
					dist = Math.abs(targetY - gapY);
					if (dist < bestDist) {
						bestDist = dist;
						bestY = gapY;
					}
				}
			} else if (gapTop < gapBottom) {
				final double gapY = Math.max(gapTop, Math.min(gapBottom, targetY));
				dist = Math.abs(targetY - gapY);
				if (dist < bestDist) {
					bestDist = dist;
					bestY = gapY;
				}
			}
		}
		// Try below the last obstacle (stagger downward)
		final double belowY = merged.get(merged.size() - 1)[1] + margin + staggerIndex * staggerSpacing;
		dist = Math.abs(targetY - belowY);
		if (dist < bestDist) {
			bestDist = dist;
			bestY = belowY;
		}
		return bestY;
	}

	private SvekNode getSvekNode2() {
		return bibliotekon.getNode(link.getEntity2());
	}

	private SvekNode getSvekNode1() {
		return bibliotekon.getNode(link.getEntity1());
	}

	private Cluster getSvekCluster1() {
		return bibliotekon.getCluster(link.getEntity1());
	}

	private Cluster getSvekCluster2() {
		return bibliotekon.getCluster(link.getEntity2());
	}

	private boolean isOpalisable() {
		return dotPath.getBeziers().size() <= 1;
	}

	private XPoint2D getXY(SvgResult svgResult, int color) {
		final int idx = svgResult.getIndexFromColor(color);
		if (idx == -1)
			return null;

		return SvekUtils.getMinXY(svgResult.substring(idx).extractList(SvgResult.POINTS_EQUALS));

	}

	private StyleSignature getDefaultStyleDefinition(Stereotype stereotype) {
		final StyleSignature result = StyleSignatureBasic.of(SName.root, SName.element, diagramType().getStyleName(),
				SName.arrow);

		return result.withTOBECHANGED(stereotype);
	}

	private Set<String> ids;

	public void setSharedIds(Set<String> ids) {
		this.ids = ids;
	}

	@DuplicateCode(reference = "MyElkPath")
	public void drawU(UGraphic ug) {

		if (opale)
			return;

		if (link.isInvis())
			return;

		if (dotPath == null) {
			Log.info(() -> "DotPath is null for " + this);
			return;
		}

		ug.draw(link.commentForSvg());
		final UGroup group = new UGroup(link.getLocation());
		group.put(UGroupType.DATA_UID, link.getUid());
		group.put(UGroupType.CLASS, "link");
		group.put(UGroupType.ID, "link_" + link.getEntity1().getName() + "_" + link.getEntity2().getName());
		group.put(UGroupType.DATA_ENTITY_1, link.getEntity1().getName());
		group.put(UGroupType.DATA_ENTITY_2, link.getEntity2().getName());
		group.put(UGroupType.DATA_ENTITY_1_UID, link.getEntity1().getUid());
		group.put(UGroupType.DATA_ENTITY_2_UID, link.getEntity2().getUid());
		final String linkTypeName = link.getType().getLinkTypeName();
		if (linkTypeName != null)
			group.put(UGroupType.DATA_LINK_TYPE, linkTypeName);
		ug.startGroup(group);
		double x = 0;
		double y = 0;
		final Url url = link.getUrl();
		if (url != null)
			ug.startUrl(url);

		if (link.isAutoLinkOfAGroup()) {
			final Cluster cl = bibliotekon.getCluster((Entity) link.getEntity1());
			if (cl != null) {
				x += cl.getRectangleArea().getWidth();
				x -= dotPath.getStartPoint().getX() - cl.getRectangleArea().getMinX();
			}
		}

		x += dx;
		y += dy;

		final StyleBuilder currentStyleBuilder = this.getCurrentStyleBuilder();
		final Style styleLine = getDefaultStyleDefinition(this.getStereotype()).getMergedStyle(currentStyleBuilder);
		final UStroke suggestedStroke = styleLine.getStroke();
		final Rainbow rainbow = Rainbow.build(styleLine, this.skinParam.getIHtmlColorSet());

		// Warning: duplicated from SmetanaPath and SvekEdge

		HColor arrowHeadColor = rainbow.getArrowHeadColor();
		HColor color = rainbow.getColor();

		if (this.link.getColors() != null) {
			final HColor newColor = this.link.getColors().getColor(ColorType.ARROW, ColorType.LINE);
			if (newColor != null) {
				color = newColor;
				arrowHeadColor = color;
			}
		} else if (this.link.getSpecificColor() != null) {
			color = this.link.getSpecificColor();
			arrowHeadColor = color;
		}

		ug = ug.apply(HColors.none().bg()).apply(color);
		final LinkType linkType = link.getType();
		UStroke stroke;
		if (suggestedStroke == null || linkType.getStyle().isNormal() == false)
			stroke = linkType.getStroke3(defaultThickness);
		else
			stroke = linkType.getStroke3(suggestedStroke);

		if (link.getColors() != null && link.getColors().getSpecificLineStroke() != null)
			stroke = link.getColors().getSpecificLineStroke();

		ug = ug.apply(stroke);

		DotPath todraw = dotPath.copy();

		UTranslate insidePortShift1 = UTranslate.none();
		UTranslate insidePortShift2 = UTranslate.none();
		final DotPath adjustedPath = adjustPathForInsidePorts(todraw, getSvekNode1(), getSvekNode2(), x, y);
		if (adjustedPath != todraw) {
			todraw = adjustedPath;
			final XPoint2D newStart = todraw.getStartPoint();
			final XPoint2D newEnd = todraw.getEndPoint();
			// Recreate extremities with correct angle from adjusted path
			if (this.extremity1 instanceof ExtremityArrow) {
				final double angle = todraw.getStartAngle() + Math.PI;
				this.extremity1 = new ExtremityArrow(newStart, angle);
				insidePortShift1 = UTranslate.none();
			}
			if (this.extremity2 instanceof ExtremityArrow) {
				final double angle = todraw.getEndAngle();
				this.extremity2 = new ExtremityArrow(newEnd, angle);
				insidePortShift2 = UTranslate.none();
			}
		}

		// Apply corner rounding if pragma is set
		final String radiusStr = pragma.getValue(PragmaKey.EDGE_CORNER_RADIUS);
		if (radiusStr != null) {
			try {
				final double radius = Double.parseDouble(radiusStr);
				if (radius > 0)
					todraw.muteToRoundOrthogonalPaths(radius);
			} catch (NumberFormatException e) {
				// Ignore invalid radius values
			}
		}

		UTranslate magneticForce1 = UTranslate.none();
		if (getSvekNode1() != null) {
			final MagneticBorder magneticBorder1 = getSvekNode1().getMagneticBorder();
			magneticForce1 = magneticBorder1.getForceAt(ug.getStringBounder(), todraw.getStartPoint().move(dx, dy));
			todraw.moveStartPoint(magneticForce1);
		} else if (getSvekCluster1() != null) {
			final MagneticBorder magneticBorder1 = getSvekCluster1().getMagneticBorder();
			magneticForce1 = magneticBorder1.getForceAt(ug.getStringBounder(), todraw.getStartPoint().move(dx, dy));
			todraw.moveStartPoint(magneticForce1);
		}

		UTranslate magneticForce2 = UTranslate.none();
		if (getSvekNode2() != null) {
			final MagneticBorder magneticBorder2 = getSvekNode2().getMagneticBorder();
			magneticForce2 = magneticBorder2.getForceAt(ug.getStringBounder(), todraw.getEndPoint().move(dx, dy));
			todraw.moveEndPoint(magneticForce2);
		} else if (getSvekCluster2() != null) {
			final MagneticBorder magneticBorder2 = getSvekCluster2().getMagneticBorder();
			magneticForce2 = magneticBorder2.getForceAt(ug.getStringBounder(), todraw.getEndPoint().move(dx, dy));
			todraw.moveEndPoint(magneticForce2);
		}

		todraw.setCommentAndCodeLine(uniq(ids, link.idCommentForSvg()), link.getCodeLine());

		drawRainbow(ug.apply(new UTranslate(x, y)), color, arrowHeadColor, todraw, link.getSupplementaryColors(),
				stroke, magneticForce1.compose(insidePortShift1), magneticForce2.compose(insidePortShift2));

		// Draw trunk extensions and filled dots at tee junction points
		if (!teeJunctions.isEmpty()) {
			final UGraphic ugTee = ug.apply(new UTranslate(x, y)).apply(color).apply(stroke);
			// Draw trunk continuation lines through junction points
			for (double[] trunk : teeTrunks)
				ugTee.apply(new UTranslate(trunk[0], trunk[1]))
						.draw(new ULine(trunk[2] - trunk[0], trunk[3] - trunk[1]));
			// Draw filled junction dots
			final double dotRadius = 3;
			final UEllipse dot = UEllipse.build(2 * dotRadius, 2 * dotRadius);
			final UGraphic ugDot = ugTee.apply(color.bg()).apply(UStroke.withThickness(1));
			for (XPoint2D junction : teeJunctions)
				ugDot.apply(new UTranslate(junction.getX() - dotRadius, junction.getY() - dotRadius))
						.draw(dot);
		}

		ug = ug.apply(UStroke.simple()).apply(color);

		if (hasNoteLabelText() && this.labelXY != null
				&& (link.getNote() == null || link.getNote().getStrategy() != NoteLinkStrategy.HALF_NOT_PRINTED))
			this.labelText.drawU(ug.apply(new UTranslate(x + this.labelXY.getPosition().getX() + labelShield,
					y + this.labelXY.getPosition().getY() + labelShield)));

		if (this.startTailLabelXY != null && this.startTailLabelXY.getPosition() != null) {
			final double labelX = x + this.startTailLabelXY.getPosition().getX();
			final double labelY = y + this.startTailLabelXY.getPosition().getY();
			if (this.startTailText != null) {
				this.startTailText.drawU(ug.apply(new UTranslate(labelX, labelY)));
				if (this.startTailRoleText != null)
					drawRoleLabel(ug, this.startTailRoleText, this.startTailText, this.startTailLabelXY.getPosition(),
							dotPath.getStartPoint(), dotPath.getEndPoint(), x, y);
			} else if (this.startTailRoleText != null) {
				this.startTailRoleText.drawU(ug.apply(new UTranslate(labelX, labelY)));
			}
		}

		if (this.endHeadLabelXY != null && this.endHeadLabelXY.getPosition() != null) {
			final double labelX = x + this.endHeadLabelXY.getPosition().getX();
			final double labelY = y + this.endHeadLabelXY.getPosition().getY();
			if (this.endHeadText != null) {
				this.endHeadText.drawU(ug.apply(new UTranslate(labelX, labelY)));
				if (this.endHeadRoleText != null)
					drawRoleLabel(ug, this.endHeadRoleText, this.endHeadText, this.endHeadLabelXY.getPosition(),
							dotPath.getEndPoint(), dotPath.getStartPoint(), x, y);
			} else if (this.endHeadRoleText != null) {
				this.endHeadRoleText.drawU(ug.apply(new UTranslate(labelX, labelY)));
			}
		}

		if (linkType.getMiddleDecor() != LinkMiddleDecor.NONE) {
			final PointAndAngle middle = dotPath.getMiddle();
			final double angleRad = middle.getAngle();
			final double angleDeg = -angleRad * 180.0 / Math.PI;
			final UDrawable mi = linkType.getMiddleDecor().getMiddleFactory(arrowLollipopColor, backgroundColor)
					.createUDrawable(angleDeg - 45);
			mi.drawU(ug.apply(new UTranslate(x + middle.getX(), y + middle.getY())));
		}

		if (url != null)
			ug.closeUrl();

		if (link.getLinkConstraint() != null) {
			final double xConstraint = x + this.labelXY.getPosition().getX();
			final double yConstraint = y + this.labelXY.getPosition().getY();
//			ug.apply(new UTranslate(xConstraint, yConstraint)).draw(URectangle.build(10, 10));
			final List<XPoint2D> square = getSquare(xConstraint, yConstraint);
			final Set<XPoint2D> bez = todraw.sample();
			XPoint2D minPt = null;
			double minDist = Double.MAX_VALUE;
			for (XPoint2D pt : square)
				for (XPoint2D pt2 : bez) {
					final double distance = pt2.distance(pt);
					if (minPt == null || distance < minDist) {
						minPt = pt;
						minDist = distance;
					}
				}

			link.getLinkConstraint().setPosition(link, minPt);
			link.getLinkConstraint().drawMe(ug, skinParam);
		}

		if (kal1 != null)
			kal1.drawU(ug);

		if (kal2 != null)
			kal2.drawU(ug);

		ug.closeGroup();
	}

	/**
	 * Draws a role label on the opposite side of the line from the quantifier. For
	 * vertical lines, the role is placed on the other side of the line's X. For
	 * horizontal lines, the role is placed on the other side of the line's Y.
	 */
	private void drawRoleLabel(UGraphic ug, TextBlock role, TextBlock quantifier, XPoint2D quantifierPos,
			XPoint2D thisEndpoint, XPoint2D otherEndpoint, double x, double y) {
		final XDimension2D qDim = quantifier.calculateDimension(stringBounder);
		final XDimension2D rDim = role.calculateDimension(stringBounder);

		final double dirX = otherEndpoint.getX() - thisEndpoint.getX();
		final double dirY = otherEndpoint.getY() - thisEndpoint.getY();

		if (Math.abs(dirX) + Math.abs(dirY) < 0.001) {
			role.drawU(ug.apply(new UTranslate(x + quantifierPos.getX(), y + quantifierPos.getY() + qDim.getHeight())));
			return;
		}

		final double gap = 2;
		final double roleX;
		final double roleY;

		if (Math.abs(dirY) >= Math.abs(dirX)) {
			// Mostly vertical: mirror across line X
			final double qCenterX = quantifierPos.getX() + qDim.getWidth() / 2;
			final double lineX = thisEndpoint.getX();
			if (qCenterX < lineX)
				roleX = lineX + gap;
			else
				roleX = lineX - rDim.getWidth() - gap;
			roleY = quantifierPos.getY();
		} else {
			// Mostly horizontal: mirror across line Y
			final double qCenterY = quantifierPos.getY() + qDim.getHeight() / 2;
			final double lineY = thisEndpoint.getY();
			if (qCenterY < lineY)
				roleY = lineY + gap;
			else
				roleY = lineY - rDim.getHeight() - gap;
			roleX = quantifierPos.getX();
		}

		role.drawU(ug.apply(new UTranslate(x + roleX, y + roleY)));
	}

	public void computeKal() {
		if (kal1 != null) {
			final UTranslate tr = UTranslate.point(dotPathInit.getStartPoint()).compose(new UTranslate(dx, dy));
			kal1.setTranslate(tr, extremity1);
		}
		if (kal2 != null) {
			final UTranslate tr = UTranslate.point(dotPathInit.getEndPoint()).compose(new UTranslate(dx, dy));
			kal2.setTranslate(tr, extremity2);
		}
	}

	private List<XPoint2D> getSquare(double x, double y) {
		final List<XPoint2D> result = new ArrayList<>();
		result.add(new XPoint2D(x, y));
		result.add(new XPoint2D(x + 5, y));
		result.add(new XPoint2D(x + 10, y));
		result.add(new XPoint2D(x, y + 5));
		result.add(new XPoint2D(x + 10, y + 5));
		result.add(new XPoint2D(x, y + 10));
		result.add(new XPoint2D(x + 5, y + 10));
		result.add(new XPoint2D(x + 10, y + 10));
		return result;
	}

	private String uniq(final Set<String> ids, final String comment) {
		boolean changed = ids.add(comment);
		if (changed)
			return comment;

		int i = 1;
		while (true) {
			final String candidate = comment + "-" + i;
			changed = ids.add(candidate);
			if (changed)
				return candidate;
			i++;
		}
	}

	private void drawRainbow(UGraphic ug, HColor color, HColor headColor, DotPath todraw,
			List<Colors> supplementaryColors, UStroke stroke, UTranslate magneticForce1, UTranslate magneticForce2) {
		ug.draw(todraw);
		final LinkType linkType = link.getType();

		if (headColor.isTransparent()) {
			if (this.extremity1 instanceof ExtremityArrow) {
				final UGraphic ugHead = ug.apply(color).apply(stroke.onlyThickness());
				((ExtremityArrow) this.extremity1).drawLineIfTransparent(ugHead.apply(magneticForce1));
			}
		} else if (this.extremity1 != null) {
			UGraphic ugHead = ug.apply(headColor).apply(stroke.onlyThickness());
			if (linkType.getDecor2().isFill())
				ugHead = ugHead.apply(color.bg());
			else
				ugHead = ugHead.apply(HColors.none().bg());
			this.extremity1.drawU(ugHead.apply(magneticForce1));
		}

		if (headColor.isTransparent()) {
			if (this.extremity2 instanceof ExtremityArrow) {
				final UGraphic ugHead = ug.apply(color).apply(stroke.onlyThickness());
				((ExtremityArrow) this.extremity2).drawLineIfTransparent(ugHead.apply(magneticForce2));
			}
		} else if (this.extremity2 != null) {
			UGraphic ugHead = ug.apply(headColor).apply(stroke.onlyThickness());
			if (linkType.getDecor1().isFill())
				ugHead = ugHead.apply(color.bg());
			else
				ugHead = ugHead.apply(HColors.none().bg());
			this.extremity2.drawU(ugHead.apply(magneticForce2));
		}

		int i = 0;
		for (Colors colors : supplementaryColors) {
			ug.apply(new UTranslate(2 * (i + 1), 2 * (i + 1))).apply(colors.getColor(ColorType.LINE)).draw(todraw);
			i++;
		}
	}

	public boolean isInverted() {
		return link.isInverted();
	}

	private double getDecorDzeta() {
		final LinkType linkType = link.getType();
		final int size1 = linkType.getDecor1().getMargin();
		final int size2 = linkType.getDecor2().getMargin();
		return size1 + size2;
	}

	public double getHorizontalDzeta(StringBounder stringBounder) {
		if (startUid.equalsId(endUid))
			return getDecorDzeta();

		final ArithmeticStrategy strategy;
		if (isHorizontal())
			strategy = new ArithmeticStrategySum();
		else
			return 0;

		if (hasNoteLabelText())
			strategy.eat(labelText.calculateDimension(stringBounder).getWidth());

		if (startTailText != null)
			strategy.eat(startTailText.calculateDimension(stringBounder).getWidth());

		if (endHeadText != null)
			strategy.eat(endHeadText.calculateDimension(stringBounder).getWidth());

		return strategy.getResult() + getDecorDzeta();
	}

	private boolean isHorizontal() {
		return link.getLength() == 1;
	}

	public double getVerticalDzeta(StringBounder stringBounder) {
		if (startUid.equalsId(endUid))
			return getDecorDzeta();

		if (isHorizontal())
			return 0;

		final ArithmeticStrategy strategy = new ArithmeticStrategySum();
		if (hasNoteLabelText())
			strategy.eat(labelText.calculateDimension(stringBounder).getHeight());

		if (startTailText != null)
			strategy.eat(startTailText.calculateDimension(stringBounder).getHeight());

		if (endHeadText != null)
			strategy.eat(endHeadText.calculateDimension(stringBounder).getHeight());

		return strategy.getResult() + getDecorDzeta();
	}

	public void manageCollision(Collection<SvekNode> allNodes) {
		for (SvekNode sh : allNodes) {
			final Positionable cl = PositionableUtils.addMargin(sh, 8, 8);
			if ((startTailText != null || startTailRoleText != null) && startTailLabelXY != null
					&& PositionableUtils.intersect(cl, startTailLabelXY))
				startTailLabelXY = PositionableUtils.moveAwayFrom(cl, startTailLabelXY);

			if ((endHeadText != null || endHeadRoleText != null) && endHeadLabelXY != null
					&& PositionableUtils.intersect(cl, endHeadLabelXY))
				endHeadLabelXY = PositionableUtils.moveAwayFrom(cl, endHeadLabelXY);

		}

	}

	private XPoint2D avoid2(XPoint2D move, Positionable pos, SvekNode sh) {
		final Oscillator oscillator = new Oscillator();
		final XPoint2D orig = new XPoint2D(move.x, move.y);
		while (cut(pos, sh)) {
			final XPoint2D m = oscillator.nextPosition();
			move = new XPoint2D(orig.x + m.x, orig.y + m.y);
		}
		return move;
	}

	private boolean cut(Positionable pos, SvekNode sh) {
		return BezierUtils.intersect(pos, sh) || tooClose(pos);
	}

	private boolean tooClose(Positionable pos) {
		final double dist = dotPath.getMinDist(BezierUtils.getCenter(pos));
		final XDimension2D dim = pos.getSize();
		// Log.println("dist=" + dist);
		return dist < (dim.getWidth() / 2 + 2) || dist < (dim.getHeight() / 2 + 2);
	}

	public void moveDelta(double deltaX, double deltaY) {
		this.dx += deltaX;
		this.dy += deltaY;
	}

	public final DotPath getDotPath() {
		final DotPath result = dotPath.copy();
		result.moveDelta(dx, dy);
		return result;
	}

	public int getLength() {
		return link.getLength();
	}

	public void setOpale(boolean opale) {
		this.link.setOpale(opale);
		this.opale = opale;

	}

	public boolean isOpale() {
		return opale;
	}

	public boolean isHorizontalSolitary() {
		return link.isHorizontalSolitary();
	}

	public boolean isLinkFromOrTo(Entity group) {
		return link.getEntity1() == group || link.getEntity2() == group;
	}

	public boolean hasEntryPoint() {
		return link.hasEntryPoint();
	}

	public void setProjectionCluster(Cluster cluster) {
		this.projectionCluster = cluster;

	}

	public boolean isHidden() {
		return link.isHidden();
	}

	public boolean sameConnections(SvekEdge other) {
		return link.sameConnections(other.link);
	}

	private boolean isAutolink() {
		return link.getEntity1() == link.getEntity2();
	}

	public XPoint2D getMyPoint(Entity entity) {
		if (link.getEntity1() == entity)
			return moveDelta(dotPath.getStartPoint());

		if (link.getEntity2() == entity)
			return moveDelta(dotPath.getEndPoint());

		throw new IllegalArgumentException();
	}

	private XPoint2D moveDelta(XPoint2D pt) {
		return new UTranslate(dx, dy).getTranslated(pt);
		// return new XPoint2D(pt.getX() + dx, pt.getY() + dy);
	}

	public boolean isLink(Link link) {
		return this.link == link;
	}

	public XPoint2D getStartContactPoint() {
		if (dotPath == null)
			return null;
		final XPoint2D start = dotPath.getStartPoint();
		if (start == null)
			return null;

		return new UTranslate(dx, dy).getTranslated(start);
		// return new XPoint2D(dx + start.getX(), dy + start.getY());
	}

	public XPoint2D getEndContactPoint() {
		final XPoint2D end = dotPath.getEndPoint();
		if (end == null)
			return null;

		return new UTranslate(dx, dy).getTranslated(end);
		// return new XPoint2D(dx + end.getX(), dy + end.getY());
	}

	public StyleBuilder getCurrentStyleBuilder() {
		return link.getStyleBuilder();
	}

	public Stereotype getStereotype() {
		return link.getStereotype();
	}

	public void moveStartPoint(double dx, double dy) {
		dotPath.moveStartPoint(dx, dy);
		dotPathInit.moveStartPoint(dx, dy);
	}

	public void moveEndPoint(double dx, double dy) {
		dotPath.moveEndPoint(dx, dy);
		dotPathInit.moveEndPoint(dx, dy);
	}

	public void replaceDotPath(net.sourceforge.plantuml.klimt.shape.DotPath newPath) {
		this.dotPath = newPath;
		this.dotPathInit = newPath.copy();
	}

}
