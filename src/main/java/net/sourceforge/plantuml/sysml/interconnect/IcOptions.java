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
 *
 */
package net.sourceforge.plantuml.sysml.interconnect;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.sysml.SysmlException;

/**
 * The <code>key=value</code> options of <code>!include_sysml</code>.
 */
public final class IcOptions {

	public enum View {
		/** Units, Cables and Mates */
		PHYSICAL,
		/** Units and InterfaceLinks / UnitLinks, no Cables */
		LOGICAL,
		/** Both overlaid */
		BOTH
	}

	public enum Detail {
		/** One port per connector */
		CONNECTOR,
		/** One port group per connector, one port per contact; Mates as harnesses */
		CONTACT
	}

	public enum Cables {
		/** Every Cable is a component with its connector ports */
		NODE,
		/** A two-ended Cable between two Units is drawn as one edge */
		EDGE
	}

	public enum Mates {
		/** A harness that falls back to plain wires when they would run straight */
		AUTO,
		/** Always a harness */
		HARNESS,
		/** Always one edge per contact pair */
		WIRES
	}

	private static final Map<String, List<String>> KEYS = new LinkedHashMap<>();
	static {
		KEYS.put("system", null);
		KEYS.put("view", Arrays.asList("physical", "logical", "both"));
		KEYS.put("detail", Arrays.asList("connector", "contact"));
		KEYS.put("cables", Arrays.asList("node", "edge"));
		KEYS.put("mates", Arrays.asList("auto", "harness", "wires"));
		KEYS.put("frame", Arrays.asList("true", "false"));
		KEYS.put("style", Arrays.asList("true", "false"));
	}

	private String system;
	private View view = View.PHYSICAL;
	private Detail detail = Detail.CONNECTOR;
	private Cables cables = Cables.NODE;
	private Mates mates = Mates.AUTO;
	private boolean frame;
	private boolean style = true;

	public static IcOptions parse(Map<String, String> values) throws SysmlException {
		final IcOptions result = new IcOptions();
		for (Map.Entry<String, String> ent : values.entrySet()) {
			final String key = ent.getKey();
			final String value = ent.getValue();
			if (KEYS.containsKey(key) == false)
				throw new SysmlException("unknown option '" + key + "' (expected one of " + KEYS.keySet() + ")");
			final List<String> allowed = KEYS.get(key);
			if (allowed != null && allowed.contains(value) == false)
				throw new SysmlException("option " + key + "=" + value + ": expected one of " + allowed);
			if (key.equals("system"))
				result.system = value;
			else if (key.equals("view"))
				result.view = View.valueOf(value.toUpperCase());
			else if (key.equals("detail"))
				result.detail = Detail.valueOf(value.toUpperCase());
			else if (key.equals("cables"))
				result.cables = Cables.valueOf(value.toUpperCase());
			else if (key.equals("mates"))
				result.mates = Mates.valueOf(value.toUpperCase());
			else if (key.equals("frame"))
				result.frame = value.equals("true");
			else if (key.equals("style"))
				result.style = value.equals("true");
		}
		return result;
	}

	/** The InterconnectSystem part to draw, or <code>null</code> for the first. */
	public String getSystem() {
		return system;
	}

	public View getView() {
		return view;
	}

	public Detail getDetail() {
		return detail;
	}

	public Cables getCables() {
		return cables;
	}

	/** How a Mate is drawn with <code>detail=contact</code>. */
	public Mates getMates() {
		return mates;
	}

	/** Draw the system itself as an outer rectangle. */
	public boolean isFrame() {
		return frame;
	}

	/** Emit the default skinparams and style. */
	public boolean isStyle() {
		return style;
	}

}
