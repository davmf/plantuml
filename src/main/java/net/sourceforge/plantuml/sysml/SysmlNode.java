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
import java.util.List;

/**
 * One parsed SysML v2 member: a package, a definition (<code>part def</code>,
 * <code>port def</code>...) or a usage (<code>part</code>, <code>port</code>,
 * <code>attribute</code>, <code>connection</code>...), with its body.
 * <p>
 * Only what an interconnect diagram needs is kept; everything else in the
 * declaration is skipped by the parser.
 */
public final class SysmlNode {

	private final String keyword;
	private final boolean definition;
	private final boolean library;
	private final String name;
	private final boolean redefines;
	private final String type;
	private final List<String> specializes;
	private final SysmlValue value;
	private final List<String> connectEnds;
	private final String source;
	private final int line;
	private final List<SysmlNode> children = new ArrayList<>();
	private final List<String> modifiers = new ArrayList<>();
	private String multiplicity;
	private String doc;
	private SysmlNode parent;

	SysmlNode(String keyword, boolean definition, boolean library, String name, boolean redefines, String type,
			List<String> specializes, SysmlValue value, List<String> connectEnds, String source, int line) {
		this.keyword = keyword;
		this.definition = definition;
		this.library = library;
		this.name = name;
		this.redefines = redefines;
		this.type = type;
		this.specializes = specializes;
		this.value = value;
		this.connectEnds = connectEnds;
		this.source = source;
		this.line = line;
	}

	/**
	 * The element keyword: <code>package</code>, <code>part</code>,
	 * <code>port</code>, <code>attribute</code>, <code>connection</code>,
	 * <code>doc</code>, <code>import</code>... or <code>null</code> for a
	 * shorthand redefinition such as <code>:&gt;&gt; designator = "J1";</code>.
	 */
	public String getKeyword() {
		return keyword;
	}

	public boolean isKeyword(String s) {
		return s.equals(keyword);
	}

	/** True for <code>part def</code>, <code>port def</code>... */
	public boolean isDefinition() {
		return definition;
	}

	/** True for a <code>library package</code>. */
	public boolean isLibrary() {
		return library;
	}

	/** True when this member or one of its owners is a library package. */
	public boolean isInLibrary() {
		for (SysmlNode n = this; n != null; n = n.parent)
			if (n.library)
				return true;
		return false;
	}

	/** The declared (or redefined) name, or <code>null</code> when anonymous. */
	public String getName() {
		return name;
	}

	public boolean isRedefines() {
		return redefines;
	}

	/** The type after <code>:</code>, as written (may be qualified). */
	public String getType() {
		return type;
	}

	/** The general types after <code>:&gt;</code> / <code>specializes</code>. */
	public List<String> getSpecializes() {
		return Collections.unmodifiableList(specializes);
	}

	/** The value after <code>=</code>, <code>:=</code> or <code>default</code>. */
	public SysmlValue getValue() {
		return value;
	}

	/** The ends of <code>connect a to b</code>, as written (feature chains). */
	public List<String> getConnectEnds() {
		return Collections.unmodifiableList(connectEnds);
	}

	/**
	 * The multiplicity as written between the brackets (<code>0..*</code>,
	 * <code>4</code>), or <code>null</code>.
	 */
	public String getMultiplicity() {
		return multiplicity;
	}

	/**
	 * The modifiers written before the keyword: <code>abstract</code>,
	 * <code>ref</code>, <code>in</code>, <code>out</code>, <code>end</code>...
	 */
	public List<String> getModifiers() {
		return Collections.unmodifiableList(modifiers);
	}

	public boolean hasModifier(String modifier) {
		return modifiers.contains(modifier);
	}

	void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

	void addModifier(String modifier) {
		modifiers.add(modifier);
	}

	public List<SysmlNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public SysmlNode getParent() {
		return parent;
	}

	public String getDoc() {
		return doc;
	}

	public String getSource() {
		return source;
	}

	public int getLine() {
		return line;
	}

	public String getLocation() {
		return source + ":" + line;
	}

	/** Qualified name built from the owning packages and definitions. */
	public String getQualifiedName() {
		if (parent == null || parent.name == null)
			return name;
		return parent.getQualifiedName() + "::" + name;
	}

	void addChild(SysmlNode child) {
		child.parent = this;
		if (child.isKeyword("doc") && doc == null)
			doc = child.value == null ? null : child.value.getText();
		children.add(child);
	}

	@Override
	public String toString() {
		return (keyword == null ? "" : keyword) + (definition ? " def " : " ") + name + (type == null ? "" : " : " + type)
				+ (value == null ? "" : " = " + value);
	}

}
