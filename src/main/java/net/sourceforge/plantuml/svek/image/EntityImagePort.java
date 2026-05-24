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
 * Creator:  Hisashi Miyashita
 *
 * 
 */

package net.sourceforge.plantuml.svek.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.EntityPosition;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.decoration.symbol.USymbol;
import net.sourceforge.plantuml.decoration.symbol.USymbols;
import net.sourceforge.plantuml.annotation.Fast;
import net.sourceforge.plantuml.klimt.Shadowable;
import net.sourceforge.plantuml.klimt.UGroup;
import net.sourceforge.plantuml.klimt.UGroupType;
import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.Side;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.SvekNode;

public class EntityImagePort extends AbstractEntityImageBorder {

	public EntityImagePort(Entity leaf, Cluster parent, Bibliotekon bibliotekon) {
		super(leaf, parent, bibliotekon, FontParam.BOUNDARY);
	}

	@Override
	protected StyleSignatureBasic getSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, getStyleName(), SName.port);
	}

	private boolean upPosition() {
		if (parent == null)
			return false;
		final XPoint2D clusterCenter = parent.getRectangleArea().getPointCenter();
		final SvekNode node = bibliotekon.getNode(getEntity());
		return node.getMinY() < clusterCenter.getY();
	}

	private boolean isLabelInside() {
		return hasInsideLabel(getEntity());
	}

	public static boolean isBoardPort(Entity entity) {
		final Entity parent = (Entity) entity.getParentContainer();
		if (parent == null || parent.isRoot())
			return false;
		if (parent.isGroup() == false || parent.getGroupType() != GroupType.PACKAGE)
			return false;
		final USymbol parentSymbol = parent.getUSymbol();
		if (parentSymbol != USymbols.RECTANGLE
				&& parentSymbol != USymbols.COMPONENT_RECTANGLE)
			return false;
		// Only the outermost rectangle (whose parent is root) is the
		// "board". Ports on nested rectangle components are not
		// board ports and must stub outward from their own parent.
		final Entity grandparent = (Entity) parent.getParentContainer();
		return grandparent == null || grandparent.isRoot();
	}

	public static boolean hasInsideLabel(Entity entity) {
		if (entity.getLeafType() == LeafType.PIN)
			return true;
		return entity.getSkinParam().portLabelsInside(entity.getStereotype());
	}

	// 0-based index of `ent` within its parent header's declaration-order
	// port list, or -1 if `ent` is not a port of a header. Used to decide
	// which side of the header (WEST or EAST) the port sits on.
	public static int headerPinIndex(Entity ent) {
		final Entity parent = (Entity) ent.getParentContainer();
		if (parent == null || parent.isHeader() == false)
			return -1;
		int idx = 0;
		for (Entity child : parent.leafs()) {
			final EntityPosition cp = child.getEntityPosition();
			if (cp == null || cp.isPort() == false)
				continue;
			if (child == ent)
				return idx;
			idx++;
		}
		return -1;
	}

	public static double getInsidePortSpacing() {
		return EntityPosition.RADIUS * 2 * 2.5;
	}

	private Side getPortSide() {
		if (getEntity().getLeafType() == LeafType.PIN) {
			final Entity parentEntity = (Entity) getEntity().getParentContainer();
			final Side connectorSide = parentEntity.getConnectorSide();
			if (connectorSide == Side.EAST || connectorSide == Side.WEST)
				return Side.WEST;
			if (connectorSide == Side.NORTH)
				return Side.SOUTH;
			return Side.NORTH;
		}
		// Header pins ignore portin/portout and instead alternate by
		// declaration order so paired pins sit on opposing faces. Match
		// the side CucaDiagramFileMakerElk feeds ELK (even index = WEST,
		// odd index = EAST) so the inside label lands on the right side
		// of the glyph.
		final Entity parentEntity = (Entity) getEntity().getParentContainer();
		if (parentEntity != null && parentEntity.isHeader())
			return (headerPinIndex(getEntity()) % 2 == 0) ? Side.WEST : Side.EAST;
		if (isLabelInside()) {
			if (entityPosition.isInput())
				return Side.WEST;
			else
				return Side.EAST;
		}
		final SvekNode node = bibliotekon.getNode(getEntity());
		if (parent == null)
			return entityPosition.isInput() ? Side.WEST : Side.EAST;
		return parent.getRectangleArea().getClosestSide(node.getPosition());
	}

	private double getEdgeAdjustX() {
		if (getEntity().getLeafType() == LeafType.PIN)
			return 0;
		if (isBoardPort(getEntity()))
			return 0;
		if (parent == null)
			return 0;
		// Under ELK the port has already been positioned on the
		// cluster boundary; Svek's edge-centre adjustment is not
		// needed and would shift the glyph away from where ELK
		// placed it.
		if (getEntity().getDiagram().isUseElk())
			return 0;
		final SvekNode thisNode = bibliotekon.getNode(getEntity());
		final Side side = getPortSide();
		final double symbolSize = 2 * EntityPosition.RADIUS;
		if (side == Side.WEST) {
			final double targetX = parent.getRectangleArea().getMinX() - symbolSize / 2;
			return targetX - thisNode.getMinX();
		} else if (side == Side.EAST) {
			final double targetX = parent.getRectangleArea().getMaxX() - symbolSize / 2;
			return targetX - thisNode.getMinX();
		}
		return 0;
	}

	private double getEvenSpacingAdjustY() {
		if (getEntity().getLeafType() == LeafType.PIN)
			return 0;
		if (isBoardPort(getEntity()))
			return 0;
		if (parent == null)
			return 0;
		// Under ELK the port row Y has been chosen by ELK; do not
		// re-space ports according to Svek's row layout.
		if (getEntity().getDiagram().isUseElk())
			return 0;
		final SvekNode thisNode = bibliotekon.getNode(getEntity());
		final EnumSet<EntityPosition> positions = entityPosition.isInput()
				? EntityPosition.getInputs() : EntityPosition.getOutputs();
		final List<SvekNode> sameSide = new ArrayList<SvekNode>(parent.getNodes(positions));
		Collections.sort(sameSide, new Comparator<SvekNode>() {
			public int compare(SvekNode a, SvekNode b) {
				return Double.compare(a.getMinY(), b.getMinY());
			}
		});
		int index = -1;
		for (int i = 0; i < sameSide.size(); i++) {
			if (sameSide.get(i) == thisNode) {
				index = i;
				break;
			}
		}
		if (index < 0)
			return 0;

		final double symbolSize = 2 * EntityPosition.RADIUS;
		final double spacing = symbolSize * 2.5;
		final double clusterMinY = parent.getRectangleArea().getMinY();
		final int titleHeight = parent.getTitleAndAttributeHeight();
		final double startY = clusterMinY + titleHeight + LABEL_GAP * 3;
		final double targetY = startY + index * spacing;
		return targetY - thisNode.getMinY();
	}

	@Override
	final public XDimension2D calculateDimensionSlow(StringBounder stringBounder) {
		double sp = EntityPosition.RADIUS * 2;
		return new XDimension2D(sp, sp);
	}

	public double getMaxWidthFromLabelForEntryExit(StringBounder stringBounder) {
		if (isLabelInside())
			return 0;
		final TextBlock desc = getDesc();
		final XDimension2D dimDesc = desc.calculateDimension(stringBounder);
		return dimDesc.getWidth();
	}

	public double getInsideLabelWidth(StringBounder stringBounder) {
		if (!isLabelInside())
			return 0;
		final TextBlock desc = getDesc();
		return desc.calculateDimension(stringBounder).getWidth();
	}

	private void drawSymbol(UGraphic ug) {
		final Shadowable rect = URectangle.build(EntityPosition.RADIUS * 2, EntityPosition.RADIUS * 2);
		ug.draw(rect);
	}

	private static final double LABEL_GAP = 5;

	final public void drawU(UGraphic ug) {
		final TextBlock desc = getDesc();
		final XDimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
		final double symbolSize = 2 * EntityPosition.RADIUS;
		double x;
		double y;
		double titleShiftY = 0;
		double edgeShiftX = 0;

		if (isLabelInside()) {
			final Side side = getPortSide();
			if (side == Side.WEST) {
				x = symbolSize + LABEL_GAP;
				y = (symbolSize - dimDesc.getHeight()) / 2;
				titleShiftY = getEvenSpacingAdjustY();
				edgeShiftX = getEdgeAdjustX();
			} else if (side == Side.EAST) {
				x = -dimDesc.getWidth() - LABEL_GAP;
				y = (symbolSize - dimDesc.getHeight()) / 2;
				titleShiftY = getEvenSpacingAdjustY();
				edgeShiftX = getEdgeAdjustX();
			} else if (side == Side.NORTH) {
				x = -(dimDesc.getWidth() - symbolSize) / 2;
				y = symbolSize + LABEL_GAP;
			} else {
				x = -(dimDesc.getWidth() - symbolSize) / 2;
				y = -dimDesc.getHeight() - LABEL_GAP;
			}
		} else if (isBoardPort(getEntity())) {
			final Side side = getPortSide();
			if (side == Side.WEST) {
				x = -dimDesc.getWidth() - LABEL_GAP;
				y = (symbolSize - dimDesc.getHeight()) / 2;
			} else if (side == Side.EAST) {
				x = symbolSize + LABEL_GAP;
				y = (symbolSize - dimDesc.getHeight()) / 2;
			} else {
				x = -(dimDesc.getWidth() - symbolSize) / 2;
				y = (side == Side.NORTH)
						? -dimDesc.getHeight() - LABEL_GAP
						: symbolSize + LABEL_GAP;
			}
		} else {
			x = -(dimDesc.getWidth() - symbolSize) / 2;
			y = upPosition() ? -(symbolSize + dimDesc.getHeight()) : symbolSize;
		}

		if (titleShiftY != 0 || edgeShiftX != 0)
			ug = ug.apply(new UTranslate(edgeShiftX, titleShiftY));

		final UGroup group = new UGroup(getEntity().getLocation());
		group.put(UGroupType.CLASS, "entity");
		group.put(UGroupType.ID, "entity_" + getEntity().getName());
		group.put(UGroupType.DATA_ENTITY, getEntity().getName());
		group.put(UGroupType.DATA_UID, getEntity().getUid());
		group.put(UGroupType.DATA_QUALIFIED_NAME, getEntity().getQuark().getQualifiedName());
		ug.startGroup(group);

		desc.drawU(ug.apply(new UTranslate(x, y)));

		final Style style = getStyle();

		HColor backcolor = getEntity().getColors().getColor(ColorType.BACK);
		HColor borderColor = getEntity().getColors().getColor(ColorType.LINE);

		if (borderColor == null)
			borderColor = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());

		if (backcolor == null)
			backcolor = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());

		ug = ug.apply(borderColor);
		ug = ug.apply(getUStroke()).apply(backcolor.bg());

		drawSymbol(ug);

		ug.closeGroup();
	}

	private UStroke getUStroke() {
		return UStroke.withThickness(1.5);
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE_PORT;
	}
}
