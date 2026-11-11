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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The tracker layout of a file set (StrictDoc workspace): one file per item,
 * each file one top-level package named by its ID, and parents that include
 * their children with <code>public import '&lt;ID&gt;'::*;</code>.
 *
 * <pre>
 * package 'TRK-SYS' {                     tracker
 *     &#64;StrictDocWorkspace::Tracker { title = "System requirements"; ... }
 *     public import 'SYS-F-THROUGHPUT'::*;
 * }
 * package 'SYS-F-THROUGHPUT' {            folder
 *     &#64;StrictDocWorkspace::Folder { title = "Throughput"; }
 *     public import 'SYS-THR-001'::*;
 * }
 * package 'SYS-THR-001' {                 item
 *     requirement &lt;'SYS-THR-001'&gt; 'Shift throughput' { ... }
 * }
 * </pre>
 *
 * A package owns nothing it includes, so containment alone says nothing about
 * trackers and folders: this class answers what a package includes, the
 * chain of packages above an item, the ID of an item and the titles of
 * trackers and folders. A file set without public imports of top-level
 * packages simply has no includes.
 */
public final class SysmlWorkspace {

	/** Metadata names of the layout (<code>StrictDocWorkspace::Tracker</code>...). */
	public static final String MODEL = "Model";
	public static final String TRACKER = "Tracker";
	public static final String FOLDER = "Folder";

	private final Map<String, SysmlNode> topLevel = new LinkedHashMap<>();
	private final Map<SysmlNode, List<SysmlNode>> includes = new LinkedHashMap<>();
	private final Map<SysmlNode, SysmlNode> includer = new LinkedHashMap<>();

	public SysmlWorkspace(List<SysmlNode> fileRoots) {
		for (SysmlNode root : fileRoots)
			for (SysmlNode child : root.getChildren())
				if (child.isKeyword("package") && child.getName() != null && topLevel.containsKey(child.getName()) == false)
					topLevel.put(child.getName(), child);
		for (SysmlNode pkg : topLevel.values()) {
			final List<SysmlNode> children = new ArrayList<>();
			for (SysmlNode member : pkg.getChildren()) {
				final SysmlNode included = includedBy(member);
				if (included != null && included != pkg) {
					children.add(included);
					if (includer.containsKey(included) == false)
						includer.put(included, pkg);
				}
			}
			if (children.isEmpty() == false)
				includes.put(pkg, children);
		}
	}

	/**
	 * The top-level package a member includes: <code>public import 'X'::*;</code>,
	 * <code>public import 'X'::**;</code> or <code>public import 'X';</code>
	 * naming a top-level package of the file set.
	 */
	private SysmlNode includedBy(SysmlNode member) {
		if (member.isKeyword("import") == false || member.hasModifier("public") == false
				|| member.getValue() == null)
			return null;
		String target = member.getValue().getRaw().trim();
		if (target.endsWith("::**"))
			target = target.substring(0, target.length() - 4);
		else if (target.endsWith("::*"))
			target = target.substring(0, target.length() - 3);
		return topLevel.get(unquote(target.trim()));
	}

	private static String unquote(String s) {
		return s.length() > 1 && s.startsWith("'") && s.endsWith("'") ? s.substring(1, s.length() - 1) : s;
	}

	/** Whether any package of the file set includes another one. */
	public boolean isLayout() {
		return includes.isEmpty() == false;
	}

	/** The top-level package of this name, or <code>null</code>. */
	public SysmlNode getTopLevel(String name) {
		return topLevel.get(name);
	}

	/** The top-level packages a package includes, in the order written. */
	public List<SysmlNode> getIncludes(SysmlNode pkg) {
		final List<SysmlNode> result = includes.get(pkg);
		return result == null ? Collections.<SysmlNode>emptyList() : Collections.unmodifiableList(result);
	}

	/** The package that includes this one (the first, if several do), or <code>null</code>. */
	public SysmlNode getIncluder(SysmlNode pkg) {
		return includer.get(pkg);
	}

	/** The top-level package a node is declared in, or <code>null</code> for a file root. */
	public static SysmlNode topLevelOf(SysmlNode node) {
		SysmlNode result = null;
		for (SysmlNode n = node; n != null && n.getParent() != null; n = n.getParent())
			result = n;
		return result;
	}

	/**
	 * Every top-level package a package includes, directly or through other
	 * packages, in include order.
	 */
	public List<SysmlNode> getIncludedTransitively(SysmlNode pkg) {
		final List<SysmlNode> result = new ArrayList<>();
		final Set<SysmlNode> seen = new HashSet<>();
		seen.add(pkg);
		collectIncluded(pkg, result, seen);
		return result;
	}

	private void collectIncluded(SysmlNode pkg, List<SysmlNode> result, Set<SysmlNode> seen) {
		for (SysmlNode child : getIncludes(pkg))
			if (seen.add(child)) {
				result.add(child);
				collectIncluded(child, result, seen);
			}
	}

	/**
	 * Whether <code>ancestor</code> is the top-level package of the node, or
	 * includes it, directly or through other packages.
	 */
	public boolean contains(SysmlNode ancestor, SysmlNode node) {
		final SysmlNode top = topLevelOf(node);
		if (top == null)
			return false;
		final Set<SysmlNode> seen = new HashSet<>();
		final List<SysmlNode> todo = new ArrayList<>();
		todo.add(ancestor);
		while (todo.isEmpty() == false) {
			final SysmlNode p = todo.remove(todo.size() - 1);
			if (p == top)
				return true;
			if (seen.add(p))
				todo.addAll(getIncludes(p));
		}
		return false;
	}

	/**
	 * The packages that include the node's top-level package, outermost first,
	 * without the model root (the package with <code>@Model</code>
	 * metadata): <code>[TRK-SYS, SYS-F-THROUGHPUT]</code> for an element of
	 * <code>SYS-THR-001</code>. Empty when nothing includes it.
	 */
	public List<SysmlNode> getIncluders(SysmlNode node) {
		final List<SysmlNode> result = new ArrayList<>();
		final Set<SysmlNode> seen = new HashSet<>();
		for (SysmlNode p = includer.get(topLevelOf(node)); p != null && seen.add(p); p = includer.get(p))
			if (getMetadata(p, MODEL) == null)
				result.add(0, p);
		return result;
	}

	/**
	 * The package of an item: the top-level package of the node when the node
	 * is the package's only element and a tracker or folder includes the
	 * package. <code>null</code> otherwise.
	 */
	public SysmlNode getItemPackage(SysmlNode node) {
		if (isElement(node) == false)
			return null;
		final SysmlNode pkg = node.getParent();
		if (pkg == null || pkg.getParent() == null || pkg.getParent().getParent() != null)
			return null;
		if (pkg.isKeyword("package") == false || includer.containsKey(pkg) == false)
			return null;
		for (SysmlNode child : pkg.getChildren())
			if (child != node && isElement(child))
				return null;
		return pkg;
	}

	private static boolean isElement(SysmlNode node) {
		final String k = node.getKeyword();
		return k != null && k.equals("import") == false && k.equals("doc") == false && k.equals("comment") == false
				&& k.equals("alias") == false && k.equals("dependency") == false && k.equals("satisfy") == false
				&& node.getModifiers().contains("@") == false;
	}

	/**
	 * The ID of an element: its short name, else the name of its item package
	 * (<code>SC-002</code> for <code>part baseSteady</code>), else
	 * <code>null</code>.
	 */
	public String getItemId(SysmlNode node) {
		if (node.getShortName() != null)
			return node.getShortName();
		final SysmlNode pkg = getItemPackage(node);
		return pkg == null ? null : pkg.getName();
	}

	/**
	 * The title of a tracker, folder or model (its <code>title</code>
	 * metadata attribute), else its name.
	 */
	public static String getTitle(SysmlNode pkg) {
		for (String metadata : new String[] { TRACKER, FOLDER, MODEL }) {
			final String title = getMetadataValue(pkg, metadata, "title");
			if (title != null && title.isEmpty() == false)
				return title;
		}
		return pkg.getName();
	}

	/**
	 * A metadata usage of a node: <code>&#64;StrictDocWorkspace::Tracker { ... }</code>
	 * is found as <code>Tracker</code>.
	 */
	public static SysmlNode getMetadata(SysmlNode owner, String name) {
		for (SysmlNode child : owner.getChildren())
			if (child.getModifiers().contains("@") && name.equals(child.getName()))
				return child;
		return null;
	}

	/**
	 * A string attribute of a metadata usage, unquoted and with <code>\n</code>
	 * escapes turned into line breaks, or <code>null</code>.
	 */
	public static String getMetadataValue(SysmlNode owner, String metadata, String attribute) {
		final SysmlNode usage = getMetadata(owner, metadata);
		if (usage == null)
			return null;
		for (SysmlNode child : usage.getChildren())
			if (attribute.equals(child.getName()) && child.getValue() != null) {
				String raw = child.getValue().getRaw().trim();
				if (raw.length() > 1 && raw.startsWith("\"") && raw.endsWith("\""))
					raw = raw.substring(1, raw.length() - 1);
				return raw.replace("\\n", "\n").replace("\\\"", "\"");
			}
		return null;
	}

}
