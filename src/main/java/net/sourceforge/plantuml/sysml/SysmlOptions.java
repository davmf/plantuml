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
package net.sourceforge.plantuml.sysml;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The <code>key=value</code> options of <code>!include_sysml</code>.
 * <p>
 * A few keys apply to every profile ({@link #COMMON}); each profile declares
 * the others it accepts, with their allowed values.
 */
public final class SysmlOptions {

	/** Which diagram root to draw (any profile). */
	public static final String ROOT = "root";
	/** The InterconnectProfile spelling of {@link #ROOT}, kept for compatibility. */
	public static final String SYSTEM = "system";
	/** Forces the profile instead of detecting it. */
	public static final String PROFILE = "profile";
	/**
	 * Selects a view when its value names one of {@link SysmlImport#VIEWS}: a
	 * generator that draws any SysML (not one profile) and is never tried
	 * without being asked for. Any other value is left to the profile, since
	 * InterconnectProfile has its own <code>view=physical|logical|both</code>.
	 */
	public static final String VIEW = "view";
	public static final String STYLE = "style";
	/**
	 * <code>elk</code> (default) writes <code>!pragma layout elk</code> before
	 * the generated source; <code>default</code> leaves the layout to the
	 * diagram.
	 */
	public static final String LAYOUT = "layout";
	public static final String FRAME = "frame";
	/**
	 * A URL pattern: elements that stand for an item get a link, with
	 * <code>{id}</code> replaced by the item's ID (<code>links=/item/{id}</code>).
	 * Generators that draw no such element ignore it.
	 */
	public static final String LINKS = "links";
	/**
	 * A saved diagram: the ID of a SysML <code>view</code> whose
	 * <code>expose</code> and <code>DiagramView</code> metadata give the root
	 * and the options (see {@link SysmlDiagramView}).
	 */
	public static final String DIAGRAM = "diagram";

	public static final List<String> COMMON = Collections
			.unmodifiableList(Arrays.asList(ROOT, SYSTEM, PROFILE, STYLE, FRAME, LAYOUT, LINKS, DIAGRAM));

	private static final List<String> BOOLEAN = Arrays.asList("true", "false");
	private static final List<String> LAYOUTS = Arrays.asList("elk", "default");

	private final Map<String, String> values;

	public SysmlOptions(Map<String, String> values) {
		this.values = new LinkedHashMap<>(values);
	}

	/** The root asked for with <code>root=</code> or <code>system=</code>, or <code>null</code>. */
	public String getRoot() {
		final String root = values.get(ROOT);
		return root != null ? root : values.get(SYSTEM);
	}

	public String getProfile() {
		return values.get(PROFILE);
	}

	/** The view asked for with <code>view=</code>, or <code>null</code>. */
	public String getView() {
		return values.get(VIEW);
	}

	/**
	 * Rejects the keys the profile does not know and the values it does not
	 * allow.
	 *
	 * @param profileKeys the profile's own keys, each with its allowed values or
	 *                    <code>null</code> for any value
	 */
	public void check(String profile, Map<String, List<String>> profileKeys) throws SysmlException {
		final Set<String> known = new LinkedHashSet<>(COMMON);
		known.addAll(profileKeys.keySet());
		for (Map.Entry<String, String> ent : values.entrySet()) {
			final String key = ent.getKey();
			if (known.contains(key) == false)
				throw new SysmlException("unknown option '" + key + "' for " + profile + " (expected one of " + known
						+ ")");
			List<String> allowed = profileKeys.get(key);
			if (key.equals(STYLE) || key.equals(FRAME))
				allowed = BOOLEAN;
			if (key.equals(LAYOUT))
				allowed = LAYOUTS;
			if (allowed != null && allowed.contains(ent.getValue()) == false)
				throw new SysmlException("option " + key + "=" + ent.getValue() + ": expected one of " + allowed);
		}
	}

	public String get(String key, String defaultValue) {
		final String result = values.get(key);
		return result == null ? defaultValue : result;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		final String result = values.get(key);
		return result == null ? defaultValue : result.equals("true");
	}

	/** Emit the default skinparams and style (default true). */
	public boolean isStyle() {
		return getBoolean(STYLE, true);
	}

	/** Draw the root itself as an outer frame (default false). */
	public boolean isFrame() {
		return getBoolean(FRAME, false);
	}

	/** Lay graph diagrams out with ELK (default true). */
	public boolean isElk() {
		return get(LAYOUT, "elk").equals("elk");
	}

	public Map<String, String> asMap() {
		return Collections.unmodifiableMap(values);
	}

}
