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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.activity.ActProfile;
import net.sourceforge.plantuml.sysml.definitions.DefProfile;
import net.sourceforge.plantuml.sysml.interconnect.IcProfile;
import net.sourceforge.plantuml.sysml.interpcba.CotsProfile;
import net.sourceforge.plantuml.sysml.interpcba.IpProfile;
import net.sourceforge.plantuml.sysml.pcba.PcbaProfile;
import net.sourceforge.plantuml.sysml.requirements.ReqView;
import net.sourceforge.plantuml.sysml.sequence.SeqProfile;
import net.sourceforge.plantuml.sysml.statemachine.SmProfile;
import net.sourceforge.plantuml.sysml.swarch.SwProfile;
import net.sourceforge.plantuml.sysml.timing.TimingProfile;

/**
 * The whole import of <code>!include_sysml</code>, from the text of the files
 * to the generated source: parses every file, finds the diagram root, picks
 * the profile that draws it and writes it. Shared by the directive and by the
 * browser engine, which hands over text it holds rather than files.
 * <p>
 * Without <code>profile=</code>, the profiles are tried in the order of
 * {@link #PROFILES}: system-level profiles come before the ones whose roots
 * they use as parts (an InterconnectSystem file set often holds PCBAs too).
 * TimingProfile comes first: a TimingDiagram is never part of another
 * profile's root, but its file set holds the boards and state machines its
 * lifelines reference.
 * <p>
 * <code>view=</code> selects one of {@link #VIEWS} instead: generators that
 * draw plain SysML v2 (requirements, definitions...) rather than one profile.
 * Every file set has roots for them, so they are never tried unless asked for.
 */
public final class SysmlImport {

	public static final List<SysmlProfile> PROFILES = Collections.unmodifiableList(Arrays.<SysmlProfile>asList(
			new TimingProfile(), new IcProfile(), new IpProfile(), new SmProfile(), new SeqProfile(), new ActProfile(),
			new SwProfile(), new PcbaProfile(), new CotsProfile()));

	/**
	 * The views, selected by name with <code>view=</code>. A view implements
	 * {@link SysmlProfile}; its {@link SysmlProfile#getName()} is the value of
	 * <code>view=</code>.
	 */
	public static final List<SysmlProfile> VIEWS = Collections.unmodifiableList(Arrays.<SysmlProfile>asList(
	// views are registered here, one per line
			new DefProfile(),
			new ReqView()));

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
		final SysmlOptions parsedOptions = new SysmlOptions(options);
		final Description description = describe(parse(names, texts), parsedOptions);
		final Root selected = description.getSelected();
		if (selected == null) {
			if (parsedOptions.getRoot() != null)
				throw new SysmlException("no diagram root named '" + parsedOptions.getRoot() + "'"
						+ (description.roots.isEmpty() ? "" : " (found: " + description.foundList() + ")"));
			final List<String> expected = new ArrayList<>();
			for (SysmlProfile profile : selectProfiles(parsedOptions))
				expected.add("'" + profile.getRootDescription() + "'");
			throw new SysmlException("no diagram root found (expected " + String.join(", ", expected) + ")");
		}
		Map<String, List<String>> optionKeys = selected.profile.getOptionKeys();
		if (VIEWS.contains(selected.profile)) {
			optionKeys = new LinkedHashMap<>(optionKeys);
			optionKeys.put(SysmlOptions.VIEW, null);
		} else if (parsedOptions.getView() != null && optionKeys.containsKey(SysmlOptions.VIEW) == false) {
			throw new SysmlException("unknown view '" + parsedOptions.getView() + "' (expected one of "
					+ viewNames() + ")");
		}
		parsedOptions.check(selected.profile.getName(), optionKeys);
		return selected.profile.generate(selected.model, selected.node, parsedOptions, String.join(" ", names),
				isFunctionDefined);
	}

	/**
	 * What the files hold, for a user interface: every root of every profile
	 * (in the order {@link #importSysml} tries them) and the one it would
	 * draw with these options. Only <code>root=</code> and
	 * <code>profile=</code> matter here; the other options are not checked.
	 */
	public static Description describe(List<String> names, List<String> texts, Map<String, String> options)
			throws SysmlException {
		return describe(parse(names, texts), new SysmlOptions(options));
	}

	private static List<SysmlNode> parse(List<String> names, List<String> texts) throws SysmlException {
		if (names.size() != texts.size())
			throw new IllegalArgumentException("names and texts differ in size");
		final List<SysmlNode> parsed = new ArrayList<>();
		for (int i = 0; i < names.size(); i++)
			parsed.add(SysmlParser.parse(names.get(i), texts.get(i)));
		return parsed;
	}

	private static Description describe(List<SysmlNode> parsed, SysmlOptions options) throws SysmlException {
		final String rootName = options.getRoot();
		final Description result = new Description();
		for (SysmlProfile profile : selectProfiles(options)) {
			final SysmlModel model = new SysmlModel(profile.getStopNames());
			for (SysmlNode root : parsed)
				model.add(root);
			for (SysmlNode node : profile.findRoots(model)) {
				final Root root = new Root(profile, model, node);
				result.roots.add(root);
				if (result.selected == null && (rootName == null || rootName.equals(node.getName())
						|| rootName.equals(node.getQualifiedName())))
					result.selected = root;
			}
		}
		return result;
	}

	/** See {@link SysmlImport#describe}. */
	public static final class Description {
		private final List<Root> roots = new ArrayList<>();
		private Root selected;

		public List<Root> getRoots() {
			return Collections.unmodifiableList(roots);
		}

		/** The root that would be drawn, or <code>null</code>. */
		public Root getSelected() {
			return selected;
		}

		private String foundList() {
			final List<String> found = new ArrayList<>();
			for (Root r : roots)
				if (r.getName() != null)
					found.add(r.getName() + " (" + r.getProfile().getName() + ")");
			return String.join(", ", found);
		}
	}

	/** A diagram root found in the files. */
	public static final class Root {
		private final SysmlProfile profile;
		private final SysmlModel model;
		private final SysmlNode node;

		private Root(SysmlProfile profile, SysmlModel model, SysmlNode node) {
			this.profile = profile;
			this.model = model;
			this.node = node;
		}

		public SysmlProfile getProfile() {
			return profile;
		}

		public String getName() {
			return node.getName();
		}

		public String getQualifiedName() {
			return node.getQualifiedName();
		}
	}

	/**
	 * The view named by <code>view=</code>, or <code>null</code>: another value
	 * is an option of the profile (InterconnectProfile's
	 * <code>view=physical</code>).
	 */
	private static SysmlProfile findView(String name) {
		if (name != null)
			for (SysmlProfile candidate : VIEWS)
				if (candidate.getName().equals(name))
					return candidate;
		return null;
	}

	private static List<String> viewNames() {
		final List<String> names = new ArrayList<>();
		for (SysmlProfile candidate : VIEWS)
			names.add(candidate.getName());
		return names;
	}

	private static List<SysmlProfile> selectProfiles(SysmlOptions options) throws SysmlException {
		final SysmlProfile view = findView(options.getView());
		if (view != null) {
			if (options.getProfile() != null)
				throw new SysmlException("options profile= and view=" + view.getName() + " cannot be used together");
			return Collections.singletonList(view);
		}
		final String name = options.getProfile();
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
