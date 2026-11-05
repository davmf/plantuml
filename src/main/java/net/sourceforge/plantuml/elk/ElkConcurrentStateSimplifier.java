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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import net.atmp.CucaDiagram;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.sdot.TextBlockToEntityImage;
import net.sourceforge.plantuml.svek.ConcurrentStates;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.InnerStateAutonom;

/**
 * ELK counterpart of CucaDiagramSimplifierState, for concurrent states only.
 * <p>
 * Each region of a concurrent state is laid out on its own by a nested ELK
 * layout and becomes a single node; the state holding the regions, when no
 * transition crosses its border, stacks them with their separators (as the dot
 * pipeline does) and becomes a single node too. Other composite states are left
 * to ELK's hierarchical layout.
 */
final class ElkConcurrentStateSimplifier {

	// ConcurrentStates draws its separator this much (its DASH) past the regions
	private static final double SEPARATOR_OVERSHOOT = 8;

	private final CucaDiagram diagram;
	private final StringBounder stringBounder;

	ElkConcurrentStateSimplifier(CucaDiagram diagram, StringBounder stringBounder) {
		this.diagram = diagram;
		this.stringBounder = stringBounder;
	}

	void simplify() {
		// Innermost first, so a region holding concurrent states sees them as nodes
		for (Entity g : getOrdered(diagram.getRootGroup())) {
			if (g.isGroup() == false || g.isRemoved())
				continue;
			if (g.getGroupType() == GroupType.CONCURRENT_STATE)
				g.overrideImage(layout(g), LeafType.STATE_CONCURRENT);
			else if (g.getGroupType() == GroupType.STATE && containsRegions(g) && g.isAutarkic())
				g.overrideImage(new InnerStateAutonom(stack(g), g), LeafType.STATE);
		}
	}

	private IEntityImage layout(Entity group) {
		return new CucaDiagramFileMakerElk(diagram, group).getImage(stringBounder);
	}

	// The state's own content is the first region, then the others
	private IEntityImage stack(Entity group) {
		final List<IEntityImage> regions = new ArrayList<>();
		regions.add(layout(group));
		for (Entity leaf : group.leafs())
			if (leaf.getLeafType() == LeafType.STATE_CONCURRENT)
				regions.add(leaf.getSvekImage());

		final char separator = group.getConcurrentSeparator();
		final IEntityImage stacked = new ConcurrentStates(regions, separator, diagram.getSkinParam(),
				group.getStereotype());
		// The dash runs past the bottom for '|' (side by side), past the right for '-'
		final double right = separator == '-' ? SEPARATOR_OVERSHOOT : 0;
		final double bottom = separator == '|' ? SEPARATOR_OVERSHOOT : 0;
		return new TextBlockToEntityImage(TextBlockUtils.withMargin(stacked, 0, right, 0, bottom));
	}

	private static boolean containsRegions(Entity group) {
		for (Entity leaf : group.leafs())
			if (leaf.getLeafType() == LeafType.STATE_CONCURRENT)
				return true;
		for (Entity child : group.groups())
			if (child.getGroupType() == GroupType.CONCURRENT_STATE)
				return true;
		return false;
	}

	private static List<Entity> getOrdered(Entity root) {
		final Collection<Entity> ordered = new LinkedHashSet<>();
		ordered.add(root);
		int size;
		do {
			size = ordered.size();
			for (Entity g : new ArrayList<>(ordered))
				for (Entity child : g.groups())
					ordered.add(child);
		} while (size != ordered.size());

		final List<Entity> result = new ArrayList<>();
		for (Entity g : ordered)
			if (g.isRoot() == false)
				result.add(0, g);
		return result;
	}

}
