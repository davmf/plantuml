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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.atmp.CucaDiagram;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.abel.CucaNote;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.EntityPosition;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.abel.LinkArrow;
import net.sourceforge.plantuml.annotation.DuplicateCode;
import net.sourceforge.plantuml.core.DiagramType;

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
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.core.options.HierarchyHandling;
import org.eclipse.elk.core.options.NodeLabelPlacement;
import org.eclipse.elk.core.util.NullElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkLabel;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
*/

import net.sourceforge.plantuml.elk.proxy.core.RecursiveGraphLayoutEngine;
import net.sourceforge.plantuml.elk.proxy.core.math.ElkPadding;
import net.sourceforge.plantuml.elk.proxy.core.options.CoreOptions;
import net.sourceforge.plantuml.elk.proxy.core.options.Direction;
import net.sourceforge.plantuml.elk.proxy.core.options.EdgeLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.options.HierarchyHandling;
import net.sourceforge.plantuml.elk.proxy.core.options.NodeLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.math.KVector;
import net.sourceforge.plantuml.elk.proxy.core.options.PortConstraints;
import net.sourceforge.plantuml.elk.proxy.core.options.PortLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.options.PortSide;
import net.sourceforge.plantuml.elk.proxy.core.options.SizeConstraint;
import net.sourceforge.plantuml.elk.proxy.core.util.NullElkProgressMonitor;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.graph.ElkPort;
import net.sourceforge.plantuml.elk.proxy.graph.util.ElkGraphUtil;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.creole.CreoleMode;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.MinMax;
import net.sourceforge.plantuml.klimt.geom.Rankdir;
import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.VerticalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.skin.AlignmentParam;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.ClusterHeader;
import net.sourceforge.plantuml.svek.CucaDiagramFileMaker;
import net.sourceforge.plantuml.svek.GeneralImageBuilder;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.SvekNode;
import net.sourceforge.plantuml.svek.image.EntityImageNoteLink;
import net.sourceforge.plantuml.svek.image.EntityImagePort;
import net.sourceforge.plantuml.utils.Position;

/*
 * Some notes:
 * 
https://www.eclipse.org/elk/documentation/tooldevelopers/graphdatastructure.html
https://www.eclipse.org/elk/documentation/tooldevelopers/graphdatastructure/coordinatesystem.html

Long hierarchical edge

https://rtsys.informatik.uni-kiel.de/~biblio/downloads/theses/yab-bt.pdf
https://rtsys.informatik.uni-kiel.de/~biblio/downloads/theses/thw-bt.pdf
 */
@DuplicateCode(reference = "SvekEdge, CucaDiagramFileMakerElk, CucaDiagramFileMakerSmetana")
public class CucaDiagramFileMakerElk extends CucaDiagramFileMaker {

	private final Map<Entity, ElkNode> nodes = new LinkedHashMap<Entity, ElkNode>();
	private final Map<Entity, ElkPort> ports = new LinkedHashMap<Entity, ElkPort>();
	private final Map<Entity, ElkNode> clusters = new LinkedHashMap<Entity, ElkNode>();
	private final Map<Link, ElkEdge> edges = new LinkedHashMap<Link, ElkEdge>();

	public CucaDiagramFileMakerElk(CucaDiagram diagram) {
		super(diagram);
	}

	// Duplication from SvekEdge
	final public StyleSignature getDefaultStyleDefinitionArrow(Stereotype stereotype, SName styleName) {
		StyleSignature result = StyleSignatureBasic.of(SName.root, SName.element, styleName, SName.arrow);
		if (stereotype != null)
			result = result.withTOBECHANGED(stereotype);

		return result;
	}

	private FontConfiguration getFontForLink(Link link, final ISkinParam skinParam) {
		final SName styleName = skinParam.getDiagramType().getStyleName();

		final Style style = getDefaultStyleDefinitionArrow(link.getStereotype(), styleName)
				.getMergedStyle(link.getStyleBuilder());
		return style.getFontConfiguration(skinParam.getIHtmlColorSet());
	}

	private HorizontalAlignment getMessageTextAlignment(DiagramType diagramType, ISkinParam skinParam) {
		if (diagramType == DiagramType.STATE)
			return skinParam.getHorizontalAlignment(AlignmentParam.stateMessageAlignment, null, false, null);

		return skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER);
	}

	private TextBlock addVisibilityModifier(TextBlock block, Link link, ISkinParam skinParam) {
		final VisibilityModifier visibilityModifier = link.getVisibilityModifier();
		if (visibilityModifier != null) {
			final Rose rose = new Rose();
			final HColor fore = rose.getHtmlColor(skinParam, visibilityModifier.getForeground());
			TextBlock visibility = visibilityModifier.getUBlock(skinParam.classAttributeIconSize(), fore, null, false);
			visibility = TextBlockUtils.withMargin(visibility, 0, 1, 2, 0);
			block = TextBlockUtils.mergeLR(visibility, block, VerticalAlignment.CENTER);
		}
		final double marginLabel = 1; // startUid.equalsId(endUid) ? 6 : 1;
		return TextBlockUtils.withMargin(block, marginLabel, marginLabel);
	}

	private LinkArrow getLinkArrow(Link link) {
		return link.getLinkArrow();
	}

	private TextBlock getLabel(StringBounder stringBounder, Link link) {
		ISkinParam skinParam = diagram.getSkinParam();
		final double marginLabel = 1; // startUid.equals(endUid) ? 6 : 1;

		// final FontConfiguration labelFont =
		// style.getFontConfiguration(skinParam.getIHtmlColorSet());
//		TextBlock labelOnly = link.getLabel().create(labelFont,
//				skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER), skinParam);

		final DiagramType type = skinParam.getDiagramType();
		final FontConfiguration font = getFontForLink(link, skinParam);

		TextBlock labelOnly;
		// toto2
		if (Display.isNull(link.getLabel())) {
			labelOnly = TextBlockUtils.EMPTY_TEXT_BLOCK;
			if (getLinkArrow(link) != LinkArrow.NONE_OR_SEVERAL) {
				// labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);
			}

		} else {
			final HorizontalAlignment alignment = getMessageTextAlignment(type, skinParam);
			final boolean hasSeveralGuideLines = link.getLabel().hasSeveralGuideLines();
			final TextBlock block;
			// if (hasSeveralGuideLines)
			// block = StringWithArrow.addSeveralMagicArrows(link.getLabel(), this, font,
			// alignment, skinParam);
			// else
			block = link.getLabel().create0(font, alignment, skinParam, skinParam.maxMessageSize(),
					CreoleMode.SIMPLE_LINE, null, null);

			labelOnly = addVisibilityModifier(block, link, skinParam);
			if (getLinkArrow(link) != LinkArrow.NONE_OR_SEVERAL && hasSeveralGuideLines == false) {
				// labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);
			}

		}

		final CucaNote note = link.getNote();
		if (note == null) {
			if (TextBlockUtils.isEmpty(labelOnly, stringBounder) == false)
				labelOnly = TextBlockUtils.withMargin(labelOnly, marginLabel, marginLabel);
			return labelOnly;
		}
		final TextBlock noteOnly = new EntityImageNoteLink(note.getDisplay(), note.getColors(), skinParam,
				link.getStyleBuilder());

		if (note.getPosition() == Position.LEFT)
			return TextBlockUtils.mergeLR(noteOnly, labelOnly, VerticalAlignment.CENTER);
		else if (note.getPosition() == Position.RIGHT)
			return TextBlockUtils.mergeLR(labelOnly, noteOnly, VerticalAlignment.CENTER);
		else if (note.getPosition() == Position.TOP)
			return TextBlockUtils.mergeTB(noteOnly, labelOnly, HorizontalAlignment.CENTER);
		else
			return TextBlockUtils.mergeTB(labelOnly, noteOnly, HorizontalAlignment.CENTER);

	}

	private TextBlock getQuantifier(StringBounder stringBounder, Link link, int n) {
		final String tmp = n == 1 ? link.getQuantifier1() : link.getQuantifier2();
		if (tmp == null)
			return null;

		final ISkinParam skinParam = diagram.getSkinParam();
		final FontConfiguration labelFont = FontConfiguration.create(skinParam, FontParam.ARROW, null);
		final TextBlock label = Display.getWithNewlines(diagram.getPragma(), tmp).create(labelFont,
				skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER), skinParam);
		if (TextBlockUtils.isEmpty(label, stringBounder))
			return null;

		return label;
	}

	private TextBlock getRoleLabel(StringBounder stringBounder, Link link, int n) {
		final String role = n == 1 ? link.getRole1() : link.getRole2();
		if (role == null)
			return null;

		final ISkinParam skinParam = diagram.getSkinParam();
		final FontConfiguration labelFont = FontConfiguration.create(skinParam, FontParam.ARROW, null);
		final TextBlock label = Display.getWithNewlines(diagram.getPragma(), role).create(labelFont,
				skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER), skinParam);
		if (TextBlockUtils.isEmpty(label, stringBounder))
			return null;

		return label;
	}

	// Retrieve the real position of a node, depending on its parents
	public static XPoint2D getPosition(ElkNode elkNode) {
		final ElkNode parent = elkNode.getParent();

		final double x = elkNode.getX();
		final double y = elkNode.getY();

		// This nasty test checks that parent is "root"
		if (parent == null || parent.getLabels().size() == 0) {
			return new XPoint2D(x, y);
		}

		// Right now, this is recursive
		final XPoint2D parentPosition = getPosition(parent);
		return new XPoint2D(parentPosition.getX() + x, parentPosition.getY() + y);

	}

	// Port coordinates are reported by ELK relative to the parent ElkNode.
	public static XPoint2D getPosition(ElkPort port) {
		final ElkNode parent = port.getParent();
		final double x = port.getX();
		final double y = port.getY();
		if (parent == null)
			return new XPoint2D(x, y);
		final XPoint2D parentPosition = getPosition(parent);
		return new XPoint2D(parentPosition.getX() + x, parentPosition.getY() + y);
	}

	private Collection<Entity> getUnpackagedEntities() {
		final List<Entity> result = new ArrayList<>();
		for (Entity ent : diagram.leafs())
			if (diagram.getRootGroup() == ent.getParentContainer())
				result.add(ent);

		return result;
	}

	// Translate PlantUML's `left to right direction` directive into the
	// ELK Direction property. ELK uses this to choose layered layout flow.
	private Object getElkDirection() {
		if (diagram.getSkinParam().getRankdir() == Rankdir.LEFT_TO_RIGHT)
			return Direction.RIGHT;
		return Direction.DOWN;
	}

	private ElkNode getElkNode(final Entity entity) {
		ElkNode node = nodes.get(entity);
		if (node == null)
			node = clusters.get(entity);

		return node;
	}

	private ElkEdge createEdgeForEndpoints(Entity e1, Entity e2) {
		final ElkPort p1 = ports.get(e1);
		final ElkPort p2 = ports.get(e2);
		if (p1 != null && p2 != null)
			return ElkGraphUtil.createSimpleEdge(p1, p2);
		if (p1 != null)
			return ElkGraphUtil.createSimpleEdge(p1, getElkNode(e2));
		if (p2 != null)
			return ElkGraphUtil.createSimpleEdge(getElkNode(e1), p2);
		return ElkGraphUtil.createSimpleEdge(getElkNode(e1), getElkNode(e2));
	}

	private void printAllSubgroups(StringBounder stringBounder, ElkNode cluster, Entity group) {
		for (Entity g : diagram.getChildrenGroups(group)) {
			if (g.isRemoved())
				continue;

			if (diagram.isEmpty(g) && g.getGroupType() == GroupType.PACKAGE) {
				g.muteToType(LeafType.EMPTY_PACKAGE);
				this.prinEntity(stringBounder, g, cluster);
			} else {

				// We create the "cluster" in ELK for this group
				final ElkNode elkCluster = ElkGraphUtil.createNode(cluster);
				elkCluster.setProperty(CoreOptions.DIRECTION, getElkDirection());
				elkCluster.setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_SIDE);
				elkCluster.setProperty(CoreOptions.NODE_SIZE_CONSTRAINTS,
						EnumSet.of(SizeConstraint.NODE_LABELS, SizeConstraint.PORTS,
								SizeConstraint.PORT_LABELS, SizeConstraint.MINIMUM_SIZE));
				final ClusterHeader clusterHeader = new ClusterHeader(g, diagram, stringBounder);

				final int titleAndAttributeHeight = clusterHeader.getTitleAndAttributeHeight();
				final int titleAndAttributeWidth = clusterHeader.getTitleAndAttributeWidth();

				// For inside-label-port clusters (e.g. nested
				// components) we draw the cluster title *above* the
				// rectangle rather than inside it, so the cluster
				// body only needs to be wide enough for left/right
				// port labels. ELK is told to place the label
				// outside-top so it reserves vertical space above
				// the cluster, and the cluster's top padding shrinks
				// to a small breathing-room value.
				final boolean externalTitle = hasInsideLabelPort(g);
				if (externalTitle) {
					elkCluster.setProperty(CoreOptions.PADDING, new ElkPadding(15, 15, 15, 15));
					elkCluster.setProperty(CoreOptions.SPACING_PORT_PORT, 10.0);
				} else {
					final double topPadding = Math.max(25, titleAndAttributeHeight) + 15;
					elkCluster.setProperty(CoreOptions.PADDING, new ElkPadding(topPadding, 15, 15, 15));
				}

				final ElkLabel label = ElkGraphUtil.createLabel(elkCluster);
				label.setText("C");
				// Tell ELK the size of the title label so it can
				// reserve space - inside (default) or outside-top.
				label.setDimensions(titleAndAttributeWidth, titleAndAttributeHeight);
				if (externalTitle)
					label.setProperty(CoreOptions.NODE_LABELS_PLACEMENT,
							EnumSet.of(NodeLabelPlacement.OUTSIDE, NodeLabelPlacement.V_TOP,
									NodeLabelPlacement.H_CENTER));

				this.clusters.put(g, elkCluster);

				// Also register a Svek Cluster in the bibliotekon so
				// EntityImagePort can resolve its parent. The
				// rectangleArea is back-filled from ELK after layout.
				clusterManager.openCluster(g, clusterHeader);
				this.printSingleGroup(stringBounder, g);
				clusterManager.closeCluster();

				// Pre-size the cluster to fit its port labels on each
				// side. ELK's PORT_LABELS size constraint alone does
				// not widen a cluster with no body children.
				sizeClusterForPortLabels(g, elkCluster, clusterHeader, stringBounder);
			}
		}

	}

	private boolean hasInsideLabelPort(Entity group) {
		for (Entity leaf : group.leafs()) {
			final EntityPosition pos = leaf.getEntityPosition();
			if (pos == null || pos.isPort() == false)
				continue;
			if (EntityImagePort.hasInsideLabel(leaf))
				return true;
		}
		return false;
	}

	// Widen and height-constrain a cluster so that:
	//  - west-side and east-side inside-port labels don't collide with
	//    each other or with the cluster title (width).
	//  - the cluster is tall enough to host its inside-label ports
	//    with ~30px row spacing but not stretched beyond that (height).
	// ELK's NODE_SIZE_CONSTRAINTS.PORT_LABELS does not reliably size
	// a cluster whose only children are ports, and the default
	// JUSTIFIED port alignment then spreads ports across the full
	// cluster height which can make clusters very tall.
	private void sizeClusterForPortLabels(Entity group, ElkNode elkCluster,
			ClusterHeader clusterHeader, StringBounder stringBounder) {
		double widestWest = 0;
		double widestEast = 0;
		int countWest = 0;
		int countEast = 0;
		for (Entity leaf : group.leafs()) {
			final EntityPosition pos = leaf.getEntityPosition();
			if (pos == null || pos.isPort() == false)
				continue;
			final IEntityImage img = printEntityInternal(leaf);
			if (img instanceof EntityImagePort == false)
				continue;
			final EntityImagePort portImg = (EntityImagePort) img;
			final boolean insideLabel = EntityImagePort.hasInsideLabel(leaf);
			if (insideLabel == false)
				continue;
			final double w = portImg.getInsideLabelWidth(stringBounder);
			if (pos.isInput()) {
				if (w > widestWest)
					widestWest = w;
				countWest++;
			} else if (pos.isOutput()) {
				if (w > widestEast)
					widestEast = w;
				countEast++;
			}
		}
		if (widestWest == 0 && widestEast == 0)
			return;
		final double portSize = 2 * EntityPosition.RADIUS;
		final double labelGap = 5;
		// The cluster body only needs to fit the two port-label
		// columns plus a small interior gap. The cluster title is
		// rendered above the rectangle (outside the cluster body)
		// so it no longer constrains horizontal width.
		final double interior = 30;
		final double minWidth = widestWest + labelGap + portSize + interior
				+ portSize + labelGap + widestEast;
		// Height: top padding + per-port row * max ports per side.
		final double rowHeight = 30;
		final int maxRows = Math.max(countWest, countEast);
		final double minHeight = 15 + maxRows * rowHeight + 15;
		elkCluster.setProperty(CoreOptions.NODE_SIZE_MINIMUM, new KVector(minWidth, minHeight));
	}

	private void printSingleGroup(StringBounder stringBounder, Entity g) {
		if (g.getGroupType() == GroupType.CONCURRENT_STATE)
			return;

		this.printEntities(stringBounder, clusters.get(g), g.leafs());
		this.printAllSubgroups(stringBounder, clusters.get(g), g);
	}

	private void printEntities(StringBounder stringBounder, ElkNode parent, Collection<Entity> entities) {
		// Convert all "leaf" to ELK node
		for (Entity ent : entities) {
			if (ent.isRemoved())
				continue;

			this.prinEntity(stringBounder, ent, parent);
		}
	}

	private void manageAllEdges(StringBounder stringBounder) {
		// Convert all "link" to ELK edge
		for (final Link link : diagram.getLinks())
			this.manageSingleEdge(stringBounder, link);

	}

	@DuplicateCode(reference = "CucaDiagramFileMakerSmetana::printEntity")
	private void prinEntity(StringBounder stringBounder, Entity ent, ElkNode parent) {
		final IEntityImage image = printEntityInternal(ent);

		// Expected dimension of the node
		final XDimension2D dimension = image.calculateDimension(stringBounder);

		final SvekNode node = getBibliotekon().createNode(ent, image, stringBounder);
		clusterManager.addNode(node);

		// Ports are emitted as ElkPort attached to the parent ElkNode
		// so ELK places them on the cluster boundary. Side is derived
		// from entityPosition.isInput (WEST) / isOutput (EAST), matching
		// EntityImagePort.getPortSide for inside-label rendering.
		final EntityPosition pos = ent.getEntityPosition();
		if (pos != null && pos.isPort() && image instanceof EntityImagePort) {
			final EntityImagePort portImage = (EntityImagePort) image;
			final ElkPort port = ElkGraphUtil.createPort(parent);
			final double portSize = 2 * EntityPosition.RADIUS;
			port.setDimensions(portSize, portSize);
			port.setProperty(CoreOptions.PORT_SIDE,
					pos.isInput() ? PortSide.WEST : PortSide.EAST);
			// portLabels inside is the PlantUML default; the <<board>>
			// stereotype rule (portLabels<<board>> outside) flips this
			// for board ports. Use EntityImagePort.hasInsideLabel so
			// ELK matches what the port image will draw.
			final boolean insideLabel = EntityImagePort.hasInsideLabel(ent);
			port.setProperty(CoreOptions.PORT_LABELS_PLACEMENT,
					EnumSet.of(insideLabel ? PortLabelPlacement.INSIDE : PortLabelPlacement.OUTSIDE,
							PortLabelPlacement.NEXT_TO_PORT_IF_POSSIBLE));
			// Reserve label space so ELK widens the cluster to fit.
			// EntityImagePort.calculateDimensionSlow returns just the
			// glyph dim, so query the desc text width directly.
			final double labelW = insideLabel
					? portImage.getInsideLabelWidth(stringBounder)
					: portImage.getMaxWidthFromLabelForEntryExit(stringBounder);
			if (labelW > 0) {
				final ElkLabel portLabel = ElkGraphUtil.createLabel(port);
				portLabel.setText("X");
				portLabel.setDimensions(labelW, portSize);
			}
			ports.put(ent, port);
			return;
		}

		// Here, we try to tell ELK to use this dimension as node dimension
		final ElkNode elkNode = ElkGraphUtil.createNode(parent);
		elkNode.setDimensions(dimension.getWidth(), dimension.getHeight());

		// There is no real "label" here
		// We just would like to force node dimension
		final ElkLabel label = ElkGraphUtil.createLabel(elkNode);
		label.setText("X");

		// I don't know why we have to do this hack, but somebody has to fix it
		final double VERY_STRANGE_OFFSET = 10;
		label.setDimensions(dimension.getWidth(), dimension.getHeight() - VERY_STRANGE_OFFSET);

		// No idea of what we are doing here :-)
		label.setProperty(CoreOptions.NODE_LABELS_PLACEMENT,
				EnumSet.of(NodeLabelPlacement.INSIDE, NodeLabelPlacement.H_CENTER, NodeLabelPlacement.V_CENTER));

		// This padding setting have no impact ?
		// label.setProperty(CoreOptions.NODE_LABELS_PADDING, new ElkPadding(100.0));

		// final EnumSet<SizeConstraint> constraints =
		// EnumSet.of(SizeConstraint.NODE_LABELS);
		// node.setProperty(CoreOptions.NODE_SIZE_CONSTRAINTS, constraints);

		// node.setProperty(CoreOptions.NODE_SIZE_OPTIONS,
		// EnumSet.noneOf(SizeOptions.class));

		// Let's store this
		nodes.put(ent, elkNode);
	}

	private void manageSingleEdge(StringBounder stringBounder, final Link link) {
		final ElkEdge edge = createEdgeForEndpoints(link.getEntity1(), link.getEntity2());

		final TextBlock labelLink = getLabel(stringBounder, link);
		if (labelLink != null) {
			final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
			final XDimension2D dim = labelLink.calculateDimension(stringBounder);
			edgeLabel.setText("X");
			edgeLabel.setDimensions(dim.getWidth(), dim.getHeight());
			// Duplicated, with qualifier, but who cares?
			edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
			// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
		}
		if (link.getQuantifier1() != null || link.getRole1() != null) {
			final TextBlock q1 = getQuantifier(stringBounder, link, 1);
			final TextBlock r1 = getRoleLabel(stringBounder, link, 1);
			final TextBlock forLayout = q1 != null ? q1 : r1;
			final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
			final XDimension2D dim = forLayout.calculateDimension(stringBounder);
			// Nasty trick, we store the kind of label in the text
			edgeLabel.setText("1");
			edgeLabel.setDimensions(dim.getWidth(), dim.getHeight());
			edgeLabel.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.TAIL);
			// Duplicated, with main label, but who cares?
			edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
			// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
		}
		if (link.getQuantifier2() != null || link.getRole2() != null) {
			final TextBlock q2 = getQuantifier(stringBounder, link, 2);
			final TextBlock r2 = getRoleLabel(stringBounder, link, 2);
			final TextBlock forLayout = q2 != null ? q2 : r2;
			final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
			final XDimension2D dim = forLayout.calculateDimension(stringBounder);
			// Nasty trick, we store the kind of label in the text
			edgeLabel.setText("2");
			edgeLabel.setDimensions(dim.getWidth(), dim.getHeight());
			edgeLabel.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.HEAD);
			// Duplicated, with main label, but who cares?
			edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
			// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
		}

		edges.put(link, edge);
	}

	private IEntityImage printEntityInternal(Entity ent) {
		if (ent.isRemoved())
			throw new IllegalStateException();

		if (ent.getSvekImage() == null) {
			final ISkinParam skinParam = diagram.getSkinParam();
			if (skinParam.sameClassWidth())
				System.err.println("NOT YET IMPLEMENED");

			return GeneralImageBuilder.createEntityImageBlock(ent, diagram.isHideEmptyDescriptionForState(), diagram,
					getBibliotekon(), null, diagram.getLinks());
		}
		return ent.getSvekImage();
	}

	@Override
	public TextBlock getTextBlock12026(List<String> dotStrings, FileFormatOption fileFormatOption)
			throws IOException, InterruptedException {

		final ElkNode root = ElkGraphUtil.createGraph();
		root.setProperty(CoreOptions.DIRECTION, getElkDirection());
		root.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.INCLUDE_CHILDREN);
		root.setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_SIDE);

		final StringBounder stringBounder = fileFormatOption.getDefaultStringBounder(diagram.getSkinParam());

		this.printAllSubgroups(stringBounder, root, diagram.getRootGroup());
		this.printEntities(stringBounder, root, getUnpackagedEntities());

		this.manageAllEdges(stringBounder);

		new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor());

		// Back-fill the Svek Cluster.rectangleArea from ELK-computed
		// bounds so EntityImagePort.getPortSide and friends work.
		for (Map.Entry<Entity, ElkNode> entry : clusters.entrySet()) {
			final Cluster cl = getBibliotekon().getCluster(entry.getKey());
			if (cl == null)
				continue;
			final ElkNode elkCluster = entry.getValue();
			final XPoint2D corner = getPosition(elkCluster);
			cl.setRectangleArea(new RectangleArea(corner.getX(), corner.getY(),
					corner.getX() + elkCluster.getWidth(),
					corner.getY() + elkCluster.getHeight()));
		}

		final MinMax minMax = TextBlockUtils.getMinMax(
				new MyElkDrawing(clusterManager, diagram, null, clusters, edges, nodes, ports),
				stringBounder, false);

		return new MyElkDrawing(clusterManager, diagram, minMax, clusters, edges, nodes, ports);
	}

}
