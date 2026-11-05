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
import net.sourceforge.plantuml.abel.EntityUtils;
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
import net.sourceforge.plantuml.elk.proxy.core.options.PortConstraints;
import net.sourceforge.plantuml.elk.proxy.core.options.PortSide;
import net.sourceforge.plantuml.elk.proxy.layered.options.LayeredOptions;
import net.sourceforge.plantuml.elk.proxy.core.util.NullElkProgressMonitor;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.graph.ElkPort;
import net.sourceforge.plantuml.elk.proxy.graph.util.ElkGraphUtil;
import net.sourceforge.plantuml.klimt.LineBreakStrategy;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.creole.CreoleMode;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.MinMax;
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
import net.sourceforge.plantuml.style.StyleQueries;
import net.sourceforge.plantuml.style.StyleQuery;
import net.sourceforge.plantuml.svek.RoleLabels;
import net.sourceforge.plantuml.svek.ClusterHeader;
import net.sourceforge.plantuml.svek.CucaDiagramFileMaker;
import net.sourceforge.plantuml.svek.GeneralImageBuilder;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.SvekNode;
import net.sourceforge.plantuml.sdot.TextBlockToEntityImage;
import net.sourceforge.plantuml.svek.image.EntityImageNoteLink;
import net.sourceforge.plantuml.svek.image.EntityImageState;
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
	private final Map<Entity, ElkNode> clusters = new LinkedHashMap<Entity, ElkNode>();
	private final Map<Link, ElkEdge> edges = new LinkedHashMap<Link, ElkEdge>();
	// Entry and exit points of a composite state: ports on its border
	private final Map<Entity, ElkPort> borderPorts = new LinkedHashMap<Entity, ElkPort>();
	// Round and diamond pseudo-states, and node-style transition labels: one port
	// in the middle of each side, so that edges meet them on a cardinal point
	private final Map<Entity, ElkPort[]> cardinalPorts = new LinkedHashMap<Entity, ElkPort[]>();
	private final Map<Entity, int[]> cardinalUse = new LinkedHashMap<Entity, int[]>();

	// The group laid out: the whole diagram, or a composite state laid out on its
	// own (a concurrent region, or a state made of regions)
	private final Entity root;

	public CucaDiagramFileMakerElk(CucaDiagram diagram) {
		this(diagram, diagram.getRootGroup());
	}

	public CucaDiagramFileMakerElk(CucaDiagram diagram, Entity root) {
		super(diagram, root);
		this.root = root;
	}

	private boolean isNestedLayout() {
		return root != diagram.getRootGroup();
	}

	// In a nested layout, the regions of a concurrent state are drawn apart
	private Collection<Entity> getChildrenGroups(Entity parent) {
		if (isNestedLayout() == false)
			return diagram.getChildrenGroups(parent);

		final List<Entity> result = new ArrayList<>();
		for (Entity g : diagram.getChildrenGroups(parent))
			if (g.getGroupType() != GroupType.CONCURRENT_STATE)
				result.add(g);
		return result;
	}

	private Collection<Link> getLocalLinks() {
		if (isNestedLayout() == false)
			return diagram.getLinks();

		final List<Link> result = new ArrayList<>();
		for (Link link : diagram.getLinks())
			if (EntityUtils.isPureInnerLink12(root, link))
				result.add(link);
		return result;
	}

	// Duplication from SvekEdge
	private StyleQuery getDefaultStyleDefinitionArrow(Stereotype stereotype, SName styleName) {
		StyleQuery result = StyleQueries.ARROW.add(styleName);
		if (stereotype != null)
			result = result.withStereotype(stereotype);

		return result;
	}

	private FontConfiguration getFontForLink(Link link, final ISkinParam skinParam) {
		final SName styleName = skinParam.getDiagramType().getStyleName();

		final Style style = link.getStyleBuilder()
				.getMergedStyle(getDefaultStyleDefinitionArrow(link.getStereotype(), styleName));
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
			final Style arrowStyle = link.getStyleBuilder().getMergedStyle(getDefaultStyleDefinitionArrow(
					link.getStereotype(), skinParam.getDiagramType().getStyleName()));
			final LineBreakStrategy styleWidth = arrowStyle.wrapWidth();
			final LineBreakStrategy wrapWidth = styleWidth.getMaxWidth() > 0 ? styleWidth : skinParam.maxMessageSize();
			block = link.getLabel().create0(font, alignment, skinParam, wrapWidth, CreoleMode.SIMPLE_LINE, null, null);

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
		final TextBlock label = RoleLabels.create(role, diagram.getPragma(), labelFont, skinParam);
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

	private Collection<Entity> getUnpackagedEntities() {
		final List<Entity> result = new ArrayList<>();
		for (Entity ent : diagram.leafs())
			if (root == ent.getParentContainer()) {
				// The other regions of a concurrent state are stacked beside this one
				if (isNestedLayout() && ent.getLeafType() == LeafType.STATE_CONCURRENT)
					continue;
				result.add(ent);
			}

		return result;
	}

	private ElkNode getElkNode(final Entity entity) {
		ElkNode node = nodes.get(entity);
		if (node == null)
			node = clusters.get(entity);

		return node;
	}

	private void printAllSubgroups(StringBounder stringBounder, ElkNode cluster, Entity group) {
		for (Entity g : getChildrenGroups(group)) {
			if (g.isRemoved())
				continue;

			if (diagram.isEmpty(g) && g.getGroupType() == GroupType.PACKAGE) {
				g.muteToType(LeafType.EMPTY_PACKAGE);
				this.prinEntity(stringBounder, g, cluster);
			} else if (diagram.isEmpty(g) && g.getGroupType() == GroupType.STATE) {
				// A composite state whose content is all removed is drawn as a plain
				// state, like the dot pipeline does
				g.overrideImage(new EntityImageState(g), LeafType.STATE);
				this.prinEntity(stringBounder, g, cluster);
			} else {

				// We create the "cluster" in ELK for this group
				final ElkNode elkCluster = ElkGraphUtil.createNode(cluster);
				elkCluster.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
				if (isStateDiagram())
					setStateSpacing(elkCluster);

				final ClusterHeader clusterHeader = new ClusterHeader(g, diagram, stringBounder);

				final int titleAndAttributeHeight = clusterHeader.getTitleAndAttributeHeight();

				final double topPadding = Math.max(25, titleAndAttributeHeight) + 15;
				elkCluster.setProperty(CoreOptions.PADDING, new ElkPadding(topPadding, 15, 15, 15));

				// Not sure this is usefull to put a label on a "cluster"
				final ElkLabel label = ElkGraphUtil.createLabel(elkCluster);
				label.setText("C");
				// We need it anyway to recurse up to the real "root"

				this.clusters.put(g, elkCluster);
				// The svek cluster draws the frame (and positions the entry/exit points)
				clusterManager.openCluster(g, clusterHeader);
				this.printSingleGroup(stringBounder, g);
				clusterManager.closeCluster();
				if (hasOnlyBorderPoints(g))
					sizeAroundBorderPoints(g, elkCluster, clusterHeader, topPadding);
			}
		}

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
		for (final Link link : getLocalLinks())
			this.manageSingleEdge(stringBounder, link);

	}

	@DuplicateCode(reference = "CucaDiagramFileMakerSmetana::printEntity")
	private void prinEntity(StringBounder stringBounder, Entity ent, ElkNode parent) {
		final IEntityImage image = printEntityInternal(ent);

		// Expected dimension of the node
		final XDimension2D dimension = image.calculateDimension(stringBounder);

		final SvekNode node = getBibliotekon().createNode(ent, image, stringBounder);

		if (isBorderPoint(ent, parent)) {
			// Not added to its svek cluster, which would move its frame onto the point
			createBorderPort(ent, parent, dimension);
			// Drawn where its port is; edges reach it through the port
			nodes.put(ent, parent);
			return;
		}
		clusterManager.addNode(node);

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

		if (isCardinal(ent))
			createCardinalPorts(ent, elkNode, dimension);

		// Let's store this
		nodes.put(ent, elkNode);
	}

	// A transition leaves and reaches a state straight for three arrowhead
	// widths before it turns
	private static final double STEM = 24;

	private void setStateSpacing(ElkNode node) {
		node.setProperty(LayeredOptions.SPACING_EDGE_NODE_BETWEEN_LAYERS, STEM);
		node.setProperty(CoreOptions.SPACING_NODE_NODE, 30.0);
	}

	private boolean isStateDiagram() {
		return diagram.getDiagramType() == DiagramType.STATE;
	}

	// An entry or exit point of a composite state sits on the border of the state
	private boolean isBorderPoint(Entity ent, ElkNode parent) {
		if (isStateDiagram() == false)
			return false;
		final EntityPosition position = ent.getEntityPosition();
		if (position != EntityPosition.ENTRY_POINT && position != EntityPosition.EXIT_POINT)
			return false;
		return clusters.containsValue(parent);
	}

	private void createBorderPort(Entity ent, ElkNode cluster, XDimension2D dimension) {
		final ElkPort port = ElkGraphUtil.createPort(cluster);
		port.setDimensions(dimension.getWidth(), dimension.getHeight());
		port.setProperty(CoreOptions.PORT_SIDE,
				ent.getEntityPosition() == EntityPosition.ENTRY_POINT ? PortSide.NORTH : PortSide.SOUTH);
		// Centred on the border line
		port.setProperty(CoreOptions.PORT_BORDER_OFFSET, -dimension.getHeight() / 2);
		cluster.setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_SIDE);
		borderPorts.put(ent, port);
	}

	// A composite state whose content is removed but its entry/exit points: ELK
	// sees a node with ports only, which has to be given a size
	private boolean hasOnlyBorderPoints(Entity g) {
		if (isStateDiagram() == false)
			return false;
		for (Entity child : diagram.getChildrenGroups(g))
			if (child.isRemoved() == false)
				return false;
		for (Entity leaf : g.leafs())
			if (leaf.isRemoved() == false && borderPorts.containsKey(leaf) == false)
				return false;
		return true;
	}

	private void sizeAroundBorderPoints(Entity g, ElkNode elkCluster, ClusterHeader clusterHeader,
			double topPadding) {
		int north = 0;
		int south = 0;
		for (Entity leaf : g.leafs())
			if (borderPorts.containsKey(leaf)) {
				if (leaf.getEntityPosition() == EntityPosition.ENTRY_POINT)
					north++;
				else
					south++;
			}
		final double spacing = 4 * EntityPosition.RADIUS + 30;
		final double width = Math.max(clusterHeader.getTitleAndAttributeWidth() + 30,
				spacing * (Math.max(north, south) + 1));
		elkCluster.setDimensions(width, topPadding + 40);
	}

	private boolean isCardinal(Entity ent) {
		if (isStateDiagram() == false)
			return false;
		switch (ent.getLeafType()) {
		case CIRCLE_START:
		case CIRCLE_END:
		case STATE_CHOICE:
		case JUNCTION:
		case PSEUDO_STATE:
		case DEEP_HISTORY:
		case STATE_TRANSITION_LABEL:
			return true;
		default:
			return false;
		}
	}

	private static final int NORTH = 0;
	private static final int EAST = 1;
	private static final int SOUTH = 2;
	private static final int WEST = 3;

	private void createCardinalPorts(Entity ent, ElkNode elkNode, XDimension2D dimension) {
		final double w = dimension.getWidth();
		final double h = dimension.getHeight();
		final ElkPort[] ports = new ElkPort[4];
		ports[NORTH] = createFixedPort(elkNode, PortSide.NORTH, w / 2, 0);
		ports[EAST] = createFixedPort(elkNode, PortSide.EAST, w, h / 2);
		ports[SOUTH] = createFixedPort(elkNode, PortSide.SOUTH, w / 2, h);
		ports[WEST] = createFixedPort(elkNode, PortSide.WEST, 0, h / 2);
		elkNode.setProperty(CoreOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
		cardinalPorts.put(ent, ports);
		cardinalUse.put(ent, new int[4]);
	}

	private ElkPort createFixedPort(ElkNode elkNode, PortSide side, double x, double y) {
		final ElkPort port = ElkGraphUtil.createPort(elkNode);
		port.setDimensions(0, 0);
		port.setLocation(x, y);
		port.setProperty(CoreOptions.PORT_SIDE, side);
		return port;
	}

	// Edges come in from the north and leave to the south; the sides take the
	// others, and a point is shared only when the four are taken. A label has
	// only its north and south points, so the line runs straight through it.
	private ElkPort pickCardinalPort(Entity ent, boolean incoming) {
		final ElkPort[] ports = cardinalPorts.get(ent);
		final int[] use = cardinalUse.get(ent);
		final int[] order;
		if (ent.getLeafType() == LeafType.STATE_TRANSITION_LABEL)
			order = incoming ? new int[] { NORTH } : new int[] { SOUTH };
		else
			order = incoming ? new int[] { NORTH, WEST, EAST, SOUTH } : new int[] { SOUTH, EAST, WEST, NORTH };

		int best = order[0];
		for (int side : order)
			if (use[side] < use[best])
				best = side;

		use[best]++;
		return ports[best];
	}

	private void manageSingleEdge(StringBounder stringBounder, final Link link) {
		final ElkNode node1 = getElkNode(link.getEntity1());
		final ElkNode node2 = getElkNode(link.getEntity2());
		// A link whose end was removed (e.g. "remove $tag") has no node to join
		if (link.isRemoved() || node1 == null || node2 == null)
			return;

		final ElkEdge edge = createEdge(link.getEntity1(), node1, link.getEntity2(), node2);

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

	private ElkEdge createEdge(Entity ent1, ElkNode node1, Entity ent2, ElkNode node2) {
		final ElkPort port1 = getPort(ent1, false);
		final ElkPort port2 = getPort(ent2, true);
		if (port1 != null && port2 != null)
			return ElkGraphUtil.createSimpleEdge(port1, port2);
		if (port1 != null)
			return ElkGraphUtil.createSimpleEdge(port1, node2);
		if (port2 != null)
			return ElkGraphUtil.createSimpleEdge(node1, port2);
		return ElkGraphUtil.createSimpleEdge(node1, node2);
	}

	private ElkPort getPort(Entity ent, boolean incoming) {
		final ElkPort border = borderPorts.get(ent);
		if (border != null)
			return border;
		if (cardinalPorts.containsKey(ent))
			return pickCardinalPort(ent, incoming);
		return null;
	}

	private IEntityImage printEntityInternal(Entity ent) {
		if (ent.isRemoved())
			throw new IllegalStateException();

		if (ent.getSvekImage() == null) {
			final ISkinParam skinParam = diagram.getSkinParam();
			if (skinParam.sameClassWidth())
				System.err.println("NOT YET IMPLEMENTED");

			return GeneralImageBuilder.createEntityImageBlock(ent, diagram.isHideEmptyDescriptionForState(), diagram,
					getBibliotekon(), null, diagram.getLinks());
		}
		return ent.getSvekImage();
	}

	@Override
	public TextBlock getTextBlock(List<String> dotStrings, FileFormatOption fileFormatOption)
			throws IOException, InterruptedException {

		final StringBounder stringBounder = fileFormatOption.getDefaultStringBounder(diagram.getSkinParam(),
				diagram.getPragma());

		// Concurrent states are laid out on their own and drawn as a single node
		if (isStateDiagram())
			new ElkConcurrentStateSimplifier(diagram, stringBounder).simplify();

		return layout(stringBounder);
	}

	// A composite state laid out on its own, as a node of the enclosing layout
	public IEntityImage getImage(StringBounder stringBounder) {
		return new TextBlockToEntityImage(layout(stringBounder));
	}

	private TextBlock layout(StringBounder stringBounder) {
		final ElkNode root = ElkGraphUtil.createGraph();
		root.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
		root.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.INCLUDE_CHILDREN);
		if (isStateDiagram())
			setStateSpacing(root);

		this.printAllSubgroups(stringBounder, root, this.root);
		this.printEntities(stringBounder, root, getUnpackagedEntities());

		this.manageAllEdges(stringBounder);

		new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor());

		final MinMax minMax = TextBlockUtils.getMinMax(
				new MyElkDrawing(clusterManager, diagram, null, clusters, edges, nodes, borderPorts), stringBounder, false);

		return new MyElkDrawing(clusterManager, diagram, minMax, clusters, edges, nodes, borderPorts);
	}

}
