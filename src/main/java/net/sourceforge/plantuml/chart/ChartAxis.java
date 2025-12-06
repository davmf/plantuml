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
 * Original Author:  David Fyfe
 *
 */
package net.sourceforge.plantuml.chart;

import java.util.Map;

public class ChartAxis {

	public enum LabelPosition {
		DEFAULT, // Vertical for v-axis, horizontal below for h-axis
		TOP,     // Horizontal at top for v-axis
		RIGHT    // At right for h-axis
	}

	private String title;
	private double min;
	private double max;
	private boolean autoScale;
	private Map<Double, String> customTicks;
	private Double tickSpacing;
	private LabelPosition labelPosition;
	private boolean logScale;
	private double scale = 1.0;  // Scale factor for axis dimension

	public ChartAxis() {
		this.title = "";
		this.min = 0;
		this.max = 100;
		this.autoScale = true;
		this.customTicks = null;
		this.tickSpacing = null;
		this.labelPosition = LabelPosition.DEFAULT;
		this.logScale = false;
	}

	public ChartAxis(String title, double min, double max) {
		this.title = title;
		this.min = min;
		this.max = max;
		this.autoScale = false;
		this.customTicks = null;
		this.tickSpacing = null;
		this.labelPosition = LabelPosition.DEFAULT;
		this.logScale = false;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
		this.autoScale = false;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
		this.autoScale = false;
	}

	public boolean isAutoScale() {
		return autoScale;
	}

	public void setAutoScale(boolean autoScale) {
		this.autoScale = autoScale;
	}

	/**
	 * Convert a data value to pixel coordinate
	 */
	public double valueToPixel(double value, double pixelMin, double pixelMax) {
		if (max == min)
			return pixelMin;

		if (logScale) {
			// Logarithmic scale transformation
			if (value <= 0 || min <= 0 || max <= 0) {
				// For invalid values in log scale, return boundary
				return value <= min ? pixelMin : pixelMax;
			}
			double logMin = Math.log10(min);
			double logMax = Math.log10(max);
			double logValue = Math.log10(value);
			return pixelMin + (logValue - logMin) / (logMax - logMin) * (pixelMax - pixelMin);
		} else {
			// Linear scale transformation (existing)
			return pixelMin + (value - min) / (max - min) * (pixelMax - pixelMin);
		}
	}

	/**
	 * Update axis range to include the given value
	 */
	public void includeValue(double value) {
		if (autoScale) {
			if (logScale && value <= 0) {
				// Skip non-positive values for log scale
				return;
			}
			if (value < min)
				min = value;
			if (value > max)
				max = value;
		}
	}

	/**
	 * Get custom tick labels map
	 */
	public Map<Double, String> getCustomTicks() {
		return customTicks;
	}

	/**
	 * Set custom tick labels. The map keys are tick values and values are labels.
	 */
	public void setCustomTicks(Map<Double, String> customTicks) {
		this.customTicks = customTicks;
	}

	/**
	 * Check if custom ticks are defined
	 */
	public boolean hasCustomTicks() {
		return customTicks != null && !customTicks.isEmpty();
	}

	/**
	 * Get tick spacing value
	 */
	public Double getTickSpacing() {
		return tickSpacing;
	}

	/**
	 * Set tick spacing. The value represents the interval between ticks.
	 */
	public void setTickSpacing(Double tickSpacing) {
		this.tickSpacing = tickSpacing;
	}

	/**
	 * Check if custom tick spacing is defined
	 */
	public boolean hasTickSpacing() {
		return tickSpacing != null && tickSpacing > 0;
	}

	/**
	 * Get label position
	 */
	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	/**
	 * Set label position
	 */
	public void setLabelPosition(LabelPosition labelPosition) {
		this.labelPosition = labelPosition;
	}

	/**
	 * Check if this axis uses logarithmic scale
	 */
	public boolean isLogScale() {
		return logScale;
	}

	/**
	 * Set whether this axis uses logarithmic scale
	 */
	public void setLogScale(boolean logScale) {
		this.logScale = logScale;
		// Validate that min and max are positive for log scale
		if (logScale) {
			if (min <= 0) {
				min = 1;  // Default to 1 if min is not positive
			}
			if (max <= 0) {
				max = 10;  // Default to 10 if max is not positive
			}
			if (max <= min) {
				max = min * 10;  // Ensure max > min
			}
		}
	}

	/**
	 * Get the scale factor for this axis dimension
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Set the scale factor for this axis dimension.
	 * A scale of 2.0 means the axis will be twice as long as default.
	 */
	public void setScale(double scale) {
		if (scale > 0) {
			this.scale = scale;
		}
	}
}
