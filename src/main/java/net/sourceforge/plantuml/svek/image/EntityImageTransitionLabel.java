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
package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;

public class EntityImageTransitionLabel extends AbstractEntityImage {

	private final TextBlock textBlock;

	public EntityImageTransitionLabel(Entity entity) {
		super(entity);

		final Stereotype stereotype = entity.getStereotype();

		// Use smaller font for transition labels to make them less prominent
		final FontConfiguration fontConfiguration = FontConfiguration.create(getSkinParam(), FontParam.STATE, stereotype)
			.changeSize(-2); // Make font slightly smaller

		// Create text block from entity display
		this.textBlock = getEntity().getDisplay().create(fontConfiguration, HorizontalAlignment.CENTER, getSkinParam());
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		try {
			// Return dimensions based on the text content
			XDimension2D dim = textBlock.calculateDimension(stringBounder);

			// Ensure we have valid dimensions (avoid negative or zero values that could cause errors)
			double width = Math.max(dim.getWidth(), 10.0);
			double height = Math.max(dim.getHeight(), 10.0);

			return new XDimension2D(width, height);
		} catch (Exception e) {
			// Fallback to safe default dimensions if calculation fails
			return new XDimension2D(50.0, 20.0);
		}
	}

	final public void drawU(UGraphic ug) {
		// Simply draw the text without any background shape
		// This makes it appear as a label rather than a state box
		if (textBlock != null) {
			try {
				textBlock.drawU(ug);
			} catch (Exception e) {
				// If drawing fails, we'll just skip the label rather than crash
				// In a production environment, you might want to log this
			}
		}
	}

	public ShapeType getShapeType() {
		// Use rectangle shape type but we don't actually draw the rectangle
		return ShapeType.RECTANGLE;
	}
}