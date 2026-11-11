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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A saved diagram: <code>diagram=VIEW-001</code> draws the SysML
 * <code>view</code> with this ID.
 *
 * <pre>
 * view &lt;'VIEW-002'&gt; 'Inside composite state 1' {
 *     &#64;StrictDocWorkspace::DiagramView { options = "regions=vertical"; plantuml = "note right of s1 : ok"; }
 *     expose 'SM-001'::top::newCompositeState1::**;
 * }
 * </pre>
 *
 * The view is turned into ordinary options:
 * <ul>
 * <li>the words of the <code>options</code> metadata attribute
 * (<code>key=value</code>; a value with spaces is written
 * <code>key="a b"</code>);</li>
 * <li>from the <code>expose</code>s, when the options do not say it already:
 * <ul>
 * <li>a tracker, folder or other package: <code>view=requirements</code> and
 * <code>root=</code> the package (<code>scope=</code> the packages, when
 * several are exposed);</li>
 * <li>a requirement or another element that no profile draws:
 * <code>view=requirements focus=</code> the element;</li>
 * <li>an element a profile draws (a state machine...): <code>root=</code> the
 * element, and <code>within=</code> the rest of the path when the expose names
 * a part of it (<code>'SM-001'::top::idle</code>).</li>
 * </ul>
 * </li>
 * </ul>
 * The options of the directive itself win over the view's. The lines of the
 * <code>plantuml</code> metadata attribute (<code>\n</code> separated) are
 * added after the generated source, and the view sets
 * <code>$sysml_view</code> and <code>$sysml_viewName</code>.
 * <p>
 * The first segment of an expose is an ID: the short name of an element, or
 * the name of a top-level package (an item, a tracker, a folder).
 */
public final class SysmlDiagramView {

	/** Keywords drawn by the requirements view rather than by a profile. */
	private static final Set<String> REQUIREMENT_KEYWORDS = new HashSet<>(Arrays.asList("requirement", "concern",
			"verification", "verification case", "analysis", "analysis case", "use case", "case"));

	private final String id;
	private final String name;
	private final Map<String, String> options;
	private final List<String> before = new ArrayList<>();
	private final List<String> after = new ArrayList<>();

	private SysmlDiagramView(String id, String name, Map<String, String> options) {
		this.id = id;
		this.name = name;
		this.options = options;
	}

	/** The options to draw with: the view's, then the directive's. */
	public Map<String, String> getOptions() {
		return Collections.unmodifiableMap(options);
	}

	/** Lines before the generated source: the view variables. */
	public List<String> getBefore() {
		return Collections.unmodifiableList(before);
	}

	/** Lines after the generated source: the view's own PlantUML. */
	public List<String> getAfter() {
		return Collections.unmodifiableList(after);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * @param parsed  the parsed files
	 * @param options the directive's options, with <code>diagram=</code>
	 */
	static SysmlDiagramView expand(List<SysmlNode> parsed, Map<String, String> options) throws SysmlException {
		final String id = options.get(SysmlOptions.DIAGRAM).replace("'", "").trim();
		final SysmlWorkspace workspace = new SysmlWorkspace(parsed);
		final SysmlNode view = findView(parsed, workspace, id);
		if (view == null)
			throw new SysmlException("diagram=" + id + ": no view with this ID (expected 'view <'" + id
					+ "'> name { expose ...; }')");

		final Map<String, String> result = new LinkedHashMap<>();
		final String written = SysmlWorkspace.getMetadataValue(view, "DiagramView", "options");
		if (written != null)
			for (String word : words(written)) {
				final int eq = word.indexOf('=');
				if (eq <= 0)
					throw new SysmlException(view.getLocation() + ": view option '" + word + "': expected key=value");
				result.put(word.substring(0, eq).trim(), unquote(word.substring(eq + 1).trim()));
			}
		for (Map.Entry<String, String> ent : options.entrySet())
			if (ent.getKey().equals(SysmlOptions.DIAGRAM) == false)
				result.put(ent.getKey(), ent.getValue());

		applyExposes(view, workspace, parsed, result);

		final SysmlDiagramView diagram = new SysmlDiagramView(id, view.getName() == null ? id : view.getName(),
				result);
		diagram.before.add("!$sysml_view = \"" + SysmlText.quote(id) + "\"");
		diagram.before.add("!$sysml_viewName = \"" + SysmlText.quote(diagram.name) + "\"");
		final String plantuml = SysmlWorkspace.getMetadataValue(view, "DiagramView", "plantuml");
		if (plantuml != null && plantuml.trim().isEmpty() == false)
			for (String line : plantuml.split("\n"))
				diagram.after.add(line);
		return diagram;
	}

	/** A saved view: its ID (as <code>diagram=</code> takes it) and its name. */
	public static final class ViewInfo {
		private final String id;
		private final String name;

		private ViewInfo(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	/** Every view usage of the files that has an ID, in file order. */
	static List<ViewInfo> findViews(List<SysmlNode> parsed) {
		final SysmlWorkspace workspace = new SysmlWorkspace(parsed);
		final List<ViewInfo> result = new ArrayList<>();
		final List<SysmlNode> todo = new ArrayList<>(parsed);
		while (todo.isEmpty() == false) {
			final SysmlNode n = todo.remove(0);
			if (n.isKeyword("view") && n.isDefinition() == false) {
				final String id = n.getShortName() != null ? n.getShortName() : workspace.getItemId(n);
				if (id != null)
					result.add(new ViewInfo(id, n.getName() == null ? id : n.getName()));
			}
			todo.addAll(0, n.getChildren());
		}
		return result;
	}

	private static SysmlNode findView(List<SysmlNode> parsed, SysmlWorkspace workspace, String id) {
		final List<SysmlNode> todo = new ArrayList<>(parsed);
		while (todo.isEmpty() == false) {
			final SysmlNode n = todo.remove(0);
			if (n.isKeyword("view") && n.isDefinition() == false
					&& (id.equals(n.getShortName()) || id.equals(n.getName()) || id.equals(workspace.getItemId(n))))
				return n;
			todo.addAll(n.getChildren());
		}
		return null;
	}

	private static void applyExposes(SysmlNode view, SysmlWorkspace workspace, List<SysmlNode> parsed,
			Map<String, String> result) throws SysmlException {
		final List<SysmlNode> packages = new ArrayList<>();
		for (SysmlNode member : view.getChildren()) {
			if (member.isKeyword("expose") == false || member.getValue() == null)
				continue;
			final List<String> path = segments(member.getValue().getRaw());
			if (path.isEmpty())
				continue;
			final SysmlNode first = findById(parsed, workspace, path.get(0));
			if (first == null)
				throw new SysmlException(member.getLocation() + ": expose '" + member.getValue().getRaw()
						+ "': '" + path.get(0) + "' names no element (expected an ID)");
			if (first.isKeyword("package")) {
				if (path.size() > 1)
					throw new SysmlException(member.getLocation() + ": expose '" + member.getValue().getRaw()
							+ "': name the element by its ID, not by a path through its package");
				packages.add(first);
				continue;
			}
			if (REQUIREMENT_KEYWORDS.contains(first.getKeyword()) && path.size() == 1) {
				putIfAbsent(result, SysmlOptions.VIEW, "requirements");
				putIfAbsent(result, "focus", path.get(0));
				continue;
			}
			putIfAbsent(result, SysmlOptions.ROOT, first.getQualifiedName());
			if (path.size() > 1)
				putIfAbsent(result, "within", String.join("::", path.subList(1, path.size())));
		}
		if (packages.isEmpty())
			return;
		putIfAbsent(result, SysmlOptions.VIEW, "requirements");
		if (packages.size() == 1) {
			putIfAbsent(result, SysmlOptions.ROOT, packages.get(0).getName());
			return;
		}
		final List<String> names = new ArrayList<>();
		for (SysmlNode p : packages)
			names.add(p.getName());
		putIfAbsent(result, "scope", String.join(",", names));
	}

	/**
	 * The element an ID names: a top-level package of this name (its only
	 * element when it is an item package), else the element with this short
	 * name.
	 */
	private static SysmlNode findById(List<SysmlNode> parsed, SysmlWorkspace workspace, String id) {
		final SysmlNode pkg = workspace.getTopLevel(id);
		if (pkg != null) {
			for (SysmlNode child : pkg.getChildren())
				if (child.getKeyword() != null && workspace.getItemPackage(child) == pkg)
					return child;
			return pkg;
		}
		final List<SysmlNode> todo = new ArrayList<>(parsed);
		while (todo.isEmpty() == false) {
			final SysmlNode n = todo.remove(0);
			if (id.equals(n.getShortName()))
				return n;
			todo.addAll(n.getChildren());
		}
		return null;
	}

	private static void putIfAbsent(Map<String, String> map, String key, String value) {
		if (map.containsKey(key) == false)
			map.put(key, value);
	}

	/** <code>'SM-001'::top::idle::**</code> gives <code>[SM-001, top, idle]</code>. */
	static List<String> segments(String raw) {
		final List<String> result = new ArrayList<>();
		for (String s : raw.trim().split("\\s*::\\s*")) {
			final String segment = unquote(s.trim());
			if (segment.isEmpty() == false && segment.equals("*") == false && segment.equals("**") == false)
				result.add(segment);
		}
		return result;
	}

	/** Splits on spaces, keeping <code>key="a b"</code> together. */
	static List<String> words(String text) {
		final List<String> result = new ArrayList<>();
		final StringBuilder sb = new StringBuilder();
		boolean quoted = false;
		for (char c : text.toCharArray()) {
			if (c == '"')
				quoted = quoted == false;
			if (Character.isWhitespace(c) && quoted == false) {
				if (sb.length() > 0)
					result.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 0)
			result.add(sb.toString());
		return result;
	}

	private static String unquote(String s) {
		if (s.length() > 1 && ((s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""))))
			return s.substring(1, s.length() - 1);
		return s;
	}

}
