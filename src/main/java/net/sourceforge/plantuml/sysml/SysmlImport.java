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
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.activity.ActProfile;
import net.sourceforge.plantuml.sysml.interconnect.IcProfile;
import net.sourceforge.plantuml.sysml.interpcba.CotsProfile;
import net.sourceforge.plantuml.sysml.interpcba.IpProfile;
import net.sourceforge.plantuml.sysml.pcba.PcbaProfile;
import net.sourceforge.plantuml.sysml.sequence.SeqProfile;
import net.sourceforge.plantuml.sysml.statemachine.SmProfile;
import net.sourceforge.plantuml.sysml.swarch.SwProfile;

/**
 * The whole import of <code>!include_sysml</code>, from the text of the files
 * to the generated source: parses every file, finds the diagram root, picks
 * the profile that draws it and writes it. Shared by the directive and by the
 * browser engine, which hands over text it holds rather than files.
 * <p>
 * Without <code>profile=</code>, the profiles are tried in the order of
 * {@link #PROFILES}: system-level profiles come before the ones whose roots
 * they use as parts (an InterconnectSystem file set often holds PCBAs too).
 */
public final class SysmlImport {

	public static final List<SysmlProfile> PROFILES = Collections.unmodifiableList(Arrays.<SysmlProfile>asList(
			new IcProfile(), new IpProfile(), new SmProfile(), new SeqProfile(), new ActProfile(), new SwProfile(),
			new PcbaProfile(), new CotsProfile()));

	private SysmlImport() {
	}

	/**
	 * @param names             the file names, for messages and the leading
	 *                          comment
	 * @param texts             the content of each file, in the same order
	 * @param options           the <code>key=value</code> options of the
	 *                          directive
	 * @param isFunctionDefined tells which label functions the diagram already
	 *                          defines: only the others get a default
	 */
	public static List<String> importSysml(List<String> names, List<String> texts, Map<String, String> options,
			Predicate<String> isFunctionDefined) throws SysmlException {
		if (names.size() != texts.size())
			throw new IllegalArgumentException("names and texts differ in size");
		final List<SysmlNode> parsed = new ArrayList<>();
		for (int i = 0; i < names.size(); i++)
			parsed.add(SysmlParser.parse(names.get(i), texts.get(i)));

		final SysmlOptions parsedOptions = new SysmlOptions(options);
		final String rootName = parsedOptions.getRoot();
		final List<SysmlProfile> profiles = selectProfiles(parsedOptions.getProfile());

		final List<String> found = new ArrayList<>();
		for (SysmlProfile profile : profiles) {
			final SysmlModel model = new SysmlModel(profile.getStopNames());
			for (SysmlNode root : parsed)
				model.add(root);
			final List<SysmlNode> roots = profile.findRoots(model);
			SysmlNode selected = null;
			for (SysmlNode root : roots) {
				if (rootName == null || rootName.equals(root.getName()) || rootName.equals(root.getQualifiedName())) {
					selected = root;
					break;
				}
				found.add(root.getName() + " (" + profile.getName() + ")");
			}
			if (selected == null)
				continue;
			parsedOptions.check(profile.getName(), profile.getOptionKeys());
			return profile.generate(model, selected, parsedOptions, String.join(" ", names), isFunctionDefined);
		}

		if (rootName != null)
			throw new SysmlException("no diagram root named '" + rootName + "'"
					+ (found.isEmpty() ? "" : " (found: " + String.join(", ", found) + ")"));
		final List<String> expected = new ArrayList<>();
		for (SysmlProfile profile : profiles)
			expected.add("'" + profile.getRootDescription() + "'");
		throw new SysmlException("no diagram root found (expected " + String.join(", ", expected) + ")");
	}

	private static List<SysmlProfile> selectProfiles(String name) throws SysmlException {
		if (name == null)
			return PROFILES;
		final List<String> names = new ArrayList<>();
		for (SysmlProfile profile : PROFILES) {
			if (profile.getName().equals(name))
				return Collections.singletonList(profile);
			names.add(profile.getName());
		}
		throw new SysmlException("unknown profile '" + name + "' (expected one of " + names + ")");
	}

}
