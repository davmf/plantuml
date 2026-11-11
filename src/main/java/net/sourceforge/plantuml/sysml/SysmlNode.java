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
	private String shortName;
	private List<String> types = Collections.emptyList();
	private List<String> annotations = Collections.emptyList();
	private List<String> sources = Collections.emptyList();
	private List<String> targets = Collections.emptyList();
	private String expression;

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

	/**
	 * Every type after <code>:</code>, as written: <code>subject dut : A, B;</code>
	 * has two. {@link #getType()} is the first.
	 */
	public List<String> getTypes() {
		if (types.isEmpty() && type != null)
			return Collections.singletonList(type);
		return Collections.unmodifiableList(types);
	}

	void setTypes(List<String> types) {
		this.types = new ArrayList<>(types);
	}

	/**
	 * The short name between <code>&lt; &gt;</code>, unquoted:
	 * <code>requirement &lt;'REQ-001'&gt; 'Speed'</code> gives <code>REQ-001</code>,
	 * or <code>null</code>.
	 */
	public String getShortName() {
		return shortName;
	}

	void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * The metadata prefixes of the declaration, by simple name:
	 * <code>#derivation dependency ...</code> gives <code>derivation</code>.
	 */
	public List<String> getAnnotations() {
		return Collections.unmodifiableList(annotations);
	}

	void setAnnotations(List<String> annotations) {
		this.annotations = new ArrayList<>(annotations);
	}

	/**
	 * The client side of a relationship member, as written:
	 * <code>dependency from A, B to C</code> gives A and B,
	 * <code>satisfy R by X</code> gives X, <code>allocate A to B</code> gives A.
	 * Empty for other members, and for a <code>satisfy</code> without
	 * <code>by</code> or a <code>verify</code> (the owner is the client).
	 */
	public List<String> getSources() {
		return Collections.unmodifiableList(sources);
	}

	/**
	 * The supplier side of a relationship member, as written: the
	 * <code>to</code> list of a dependency or an allocation, the requirement of
	 * <code>satisfy</code> and <code>verify</code>.
	 */
	public List<String> getTargets() {
		return Collections.unmodifiableList(targets);
	}

	void setRelationEnds(List<String> sources, List<String> targets) {
		this.sources = new ArrayList<>(sources);
		this.targets = new ArrayList<>(targets);
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

	/**
	 * The text of an expression written as a member of a constraint
	 * (<code>require constraint { x &lt;= 3 }</code>), or <code>null</code>.
	 */
	public String getExpression() {
		return expression;
	}

	void setExpression(String expression) {
		this.expression = expression;
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

	/**
	 * The text of the first <code>doc</code> comment, without its reStructuredText
	 * directives (see {@link SysmlText#withoutDirectives}).
	 */
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
			doc = child.value == null ? null : SysmlText.withoutDirectives(child.value.getText());
		children.add(child);
	}

	@Override
	public String toString() {
		return (keyword == null ? "" : keyword) + (definition ? " def " : " ") + name + (type == null ? "" : " : " + type)
				+ (value == null ? "" : " = " + value);
	}

}
