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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A usage seen through its definition: the attributes, nested parts and
 * ports and the connections it ends up with once the definition's
 * specialization chain, the definition body and the usage body are applied
 * in that order (later redefinitions win).
 * <p>
 * Each resolved feature owns fresh copies of its nested features, so the
 * same port definition used twice gives two independent contact lists.
 */
public final class SysmlFeature {

	private final String name;
	private final String keyword;
	private final SysmlNode declaration;
	private final boolean fromLibrary;
	private final List<String> types = new ArrayList<>();
	private final Map<String, String> attributes = new LinkedHashMap<>();
	private final Map<String, SysmlFeature> features = new LinkedHashMap<>();
	private final List<SysmlNode> connections = new ArrayList<>();
	private String doc;
	private SysmlValue value;

	SysmlFeature(String name, String keyword, SysmlNode declaration, boolean fromLibrary) {
		this.name = name;
		this.keyword = keyword;
		this.declaration = declaration;
		this.fromLibrary = fromLibrary;
	}

	public String getName() {
		return name;
	}

	/** <code>part</code>, <code>port</code>... as declared. */
	public String getKeyword() {
		return keyword;
	}

	public boolean isKeyword(String s) {
		return s.equals(keyword);
	}

	public SysmlNode getDeclaration() {
		return declaration;
	}

	/** True when the feature comes from a library package (e.g. the profile). */
	public boolean isFromLibrary() {
		return fromLibrary;
	}

	/** The declared type, as written, or <code>null</code>. */
	public String getType() {
		return types.isEmpty() ? null : types.get(types.size() - 1);
	}

	void addType(String type) {
		types.add(type);
	}

	/** The value of an attribute (simplified), or <code>null</code>. */
	public String getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}

	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	void putAttribute(String attributeName, String attributeValue) {
		attributes.put(attributeName, attributeValue);
	}

	public SysmlFeature getFeature(String featureName) {
		return features.get(featureName);
	}

	/** Nested parts, ports and items, in declaration order. */
	public Collection<SysmlFeature> getFeatures() {
		return Collections.unmodifiableCollection(features.values());
	}

	void putFeature(SysmlFeature feature) {
		features.put(feature.getName(), feature);
	}

	/** Connection usages owned by the feature, its definition and their bodies. */
	public List<SysmlNode> getConnections() {
		return Collections.unmodifiableList(connections);
	}

	void addConnection(SysmlNode connection) {
		connections.add(connection);
	}

	public String getDoc() {
		return doc;
	}

	void setDoc(String doc) {
		this.doc = doc;
	}

	public SysmlValue getValue() {
		return value;
	}

	void setValue(SysmlValue value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return keyword + " " + name + " : " + getType() + " " + attributes + " " + features.keySet();
	}

}
