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
package net.sourceforge.plantuml.sysml.requirements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlException;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlText;
import net.sourceforge.plantuml.sysml.SysmlWriter;
import net.sourceforge.plantuml.sysml.requirements.ReqModel.Element;
import net.sourceforge.plantuml.sysml.requirements.ReqModel.Relation;

/**
 * Writes the use case view.
 * <p>
 * A class diagram with <code>allowmixing</code>: use cases are
 * <code>usecase</code> ovals inside a <code>rectangle</code> per subject (the
 * system boundary) and actors are stick figures, as in a use case diagram,
 * while objectives and requirements are the compartmented <code>class</code>
 * boxes of the requirements view. Actors are parameters of each use case in
 * SysML v2: the ones that share a type are drawn once.
 * <p>
 * Unlike the requirements view, relations are written from the client to the
 * supplier: with the default left to right direction, the actors come first,
 * then the use cases, then what they include and the requirements they aim at.
 */
final class UseCaseWriter extends SysmlWriter {

	/** How far a use case looks for the subject of what it is defined by or specialises. */
	private static final int INHERITANCE_DEPTH = 8;

	private final ReqModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final Set<String> aliases = new HashSet<>();

	/** An actor or a subject: a name, a type, or both. */
	private static final class Party {
		final String name;
		final String type;
		String alias;
		final List<Element> useCases = new ArrayList<>();

		Party(String name, String type) {
			this.name = name == null ? "" : name;
			this.type = type == null ? "" : type;
		}

		String getLabel() {
			return type.isEmpty() ? name : type;
		}
	}

	/** The use cases drawn, in file order, then the ones only reached by an include. */
	private final List<Element> useCases = new ArrayList<>();
	private final Map<String, Party> actors = new LinkedHashMap<>();
	private final Map<String, Party> subjects = new LinkedHashMap<>();
	private final List<Element> withoutSubject = new ArrayList<>();
	/** Included use cases that name nothing, by name; the value is the alias. */
	private final Map<String, String> unresolved = new LinkedHashMap<>();
	private final Map<SysmlNode, String> objectives = new LinkedHashMap<>();
	private final List<String> links = new ArrayList<>();

	/**
	 * @param root the package to draw, or <code>null</code> for the whole file
	 *             set
	 */
	UseCaseWriter(ReqModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
	}

	private boolean inRoot(Element e) {
		if (root == null)
			return true;
		for (SysmlNode n = e.node; n != null; n = n.getParent())
			if (n == root)
				return true;
		return false;
	}

	private static boolean isUseCase(Element e) {
		return e != null && e.node != null && e.node.isKeyword(UseCaseView.USE_CASE);
	}

	private static String lastSegment(String reference) {
		final List<String> segments = ReqModel.split(reference);
		return segments.isEmpty() ? reference : segments.get(segments.size() - 1);
	}

	/** What a member is typed by, or else what it subsets. */
	private static String reference(SysmlNode member) {
		if (member.getType() != null)
			return member.getType();
		if (member.getSpecializes().isEmpty() == false)
			return member.getSpecializes().get(0);
		return null;
	}

	/**
	 * The actor or subject a member declares, shared by every member of the
	 * same type (or of the same name, when it has no type).
	 */
	private Party party(Map<String, Party> parties, SysmlNode member, Element owner) {
		final String reference = reference(member);
		if (reference == null && member.getName() == null)
			return null;
		final String key;
		final String type;
		if (reference == null) {
			key = "name " + member.getName();
			type = null;
		} else {
			final Element typed = model.resolve(reference, owner.path, true);
			key = typed == null ? "type " + lastSegment(reference) : "element " + typed.index;
			type = typed == null || typed.name == null ? lastSegment(reference) : typed.name;
		}
		Party result = parties.get(key);
		if (result == null) {
			result = new Party(member.getName(), type);
			parties.put(key, result);
		}
		return result;
	}

	private static SysmlNode member(Element e, String keyword) {
		for (SysmlNode child : e.node.getChildren())
			if (child.isKeyword(keyword))
				return child;
		return null;
	}

	/**
	 * The use case that declares the subject: the use case itself, or else the
	 * one it is defined by or specialises.
	 */
	private Element subjectOwner(Element useCase, int depth) {
		if (member(useCase, "subject") != null)
			return useCase;
		if (depth == 0)
			return null;
		final List<String> generals = new ArrayList<>(useCase.node.getTypes());
		generals.addAll(useCase.node.getSpecializes());
		for (String general : generals) {
			final Element g = model.resolve(general, useCase.path, true);
			if (isUseCase(g) && g != useCase) {
				final Element result = subjectOwner(g, depth - 1);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	/** The element that owns this one, if any. */
	private Element owner(Element e) {
		for (SysmlNode n = e.node.getParent(); n != null; n = n.getParent()) {
			final Element result = model.getElement(n);
			if (result != null)
				return result;
		}
		return null;
	}

	private void link(String from, String arrow, String to, String kind, String label) {
		final String line = from + " " + arrow + " " + to + SysmlText.stereotypes(kind)
				+ (label == null ? "" : " : " + label);
		if (links.contains(line) == false)
			links.add(line);
	}

	private static String relationLabel(String kind) {
		return SysmlText.call("$sysml_relation_label", ReqModel.DEFINED_BY.equals(kind) ? "defined by" : kind);
	}

	void run(String source) throws SysmlException {
		final String requirements = options.get(UseCaseView.REQUIREMENTS, "all");
		final boolean withRequirements = requirements.equals("none") == false;
		final boolean text = options.getBoolean(UseCaseView.TEXT, true);
		final boolean roles = options.getBoolean(UseCaseView.ROLES, false);

		for (Element e : model.getElements())
			if (isUseCase(e) && inRoot(e))
				useCases.add(e);

		// Includes first: they may add use cases declared outside the root
		final List<Object[]> includes = new ArrayList<>();
		for (int i = 0; i < useCases.size(); i++) {
			final Element useCase = useCases.get(i);
			for (SysmlNode child : useCase.node.getChildren()) {
				if (child.isKeyword("include") == false)
					continue;
				final String reference = reference(child) != null ? reference(child) : child.getName();
				if (reference == null)
					continue;
				final Element target = model.resolve(reference, useCase.path, true);
				if (isUseCase(target)) {
					if (useCases.contains(target) == false)
						useCases.add(target);
					if (target != useCase)
						includes.add(new Object[] { useCase, target });
				} else {
					final String name = lastSegment(reference);
					unresolved.put(name, null);
					includes.add(new Object[] { useCase, name });
					warn(child.getLocation() + ": include '" + reference + "' names no use case");
				}
			}
		}

		final Map<Element, List<Party>> actorsOf = new LinkedHashMap<>();
		for (Element useCase : useCases) {
			final Element declaring = subjectOwner(useCase, INHERITANCE_DEPTH);
			final Party subject = declaring == null ? null : party(subjects, member(declaring, "subject"), declaring);
			if (subject == null)
				withoutSubject.add(useCase);
			else
				subject.useCases.add(useCase);
			for (SysmlNode child : useCase.node.getChildren())
				if (child.isKeyword("actor")) {
					final Party actor = party(actors, child, useCase);
					if (actor != null)
						actor.useCases.add(useCase);
				}
		}

		final Set<Element> drawnRequirements = withRequirements ? selectRequirements(requirements.equals("all"))
				: Collections.<Element>emptySet();

		// Aliases, in the order the elements are written
		for (Party actor : actors.values())
			actor.alias = SysmlText.unique(SysmlText.sanitize(actor.getLabel()), aliases);
		for (Party subject : subjects.values())
			subject.alias = SysmlText.unique(SysmlText.sanitize("subject_" + subject.getLabel()), aliases);
		for (Element useCase : useCases)
			useCase.alias = SysmlText.unique(SysmlText.sanitize(useCase.getLabel()), aliases);
		for (Map.Entry<String, String> ent : unresolved.entrySet())
			ent.setValue(SysmlText.unique(SysmlText.sanitize(ent.getKey()), aliases));
		if (withRequirements)
			for (Element useCase : useCases) {
				final SysmlNode objective = member(useCase, "objective");
				if (objective != null && isWorthDrawing(objective, drawnRequirements))
					objectives.put(objective, SysmlText.unique(SysmlText.sanitize(
							useCase.getLabel() + "_" + (objective.getName() == null ? "objective" : objective.getName())),
							aliases));
			}
		for (Element requirement : drawnRequirements)
			requirement.alias = SysmlText.unique(SysmlText.sanitize(requirement.getLabel()), aliases);

		header(source, "usecases", root == null ? "" : root.getName(), Collections.<String, String>emptyMap());
		defaultFunction("$sysml_element_label", "$id, $name, $kind", //
				"!$sysml_l = \"\"", //
				"!if $kind != \"\"", //
				"!$sysml_l = \"«\" + $kind + \"»\\n\"", //
				"!endif", //
				"!if $id != \"\"", //
				"!$sysml_l = $sysml_l + \"<b>\" + $id + \"</b>\"", //
				"!if $name != \"\" && $name != $id", //
				"!$sysml_l = $sysml_l + \"\\n\"", //
				"!endif", //
				"!endif", //
				"!if $name != $id", //
				"!$sysml_l = $sysml_l + $name", //
				"!endif", //
				"!return $sysml_l");
		defaultFunction("$sysml_actor_label", "$name, $type", //
				"!if $type != \"\"", //
				"!return $type", //
				"!endif", //
				"!return $name");
		defaultFunction("$sysml_subject_label", "$name, $type", //
				"!if $type != \"\"", //
				"!return $type", //
				"!endif", //
				"!return $name");
		defaultFunction("$sysml_text_label", "$text, $id", "!return $text");
		defaultFunction("$sysml_relation_label", "$kind", "!return \"«\" + $kind + \"»\"");
		if (text && ReqWriter.hasConstraints(drawnRequirements))
			defaultFunction("$sysml_constraint_label", "$text, $kind", "!return $kind + \" { \" + $text + \" }\"");
		out.add("allowmixing");
		if (options.get(UseCaseView.DIRECTION, "left").equals("left"))
			out.add("left to right direction");
		out.add("hide circle");
		out.add("hide empty members");
		out.add("hide stereotype");
		if (options.isStyle())
			writeStyle();

		for (Party actor : actors.values())
			out.add("actor \"" + SysmlText.call("$sysml_actor_label", actor.name, actor.type) + "\" as " + actor.alias
					+ " <<actor>>");
		for (Party subject : subjects.values()) {
			out.add("rectangle \"" + SysmlText.call("$sysml_subject_label", subject.name, subject.type) + "\" as "
					+ subject.alias + " <<subject>> {");
			for (Element useCase : subject.useCases)
				writeUseCase(useCase, "  ");
			out.add("}");
		}
		for (Element useCase : withoutSubject)
			writeUseCase(useCase, "");
		for (Map.Entry<String, String> ent : unresolved.entrySet())
			out.add("usecase \"" + SysmlText.call("$sysml_element_label", "", ent.getKey(), UseCaseView.USE_CASE)
					+ "\" as " + ent.getValue() + " <<useCase>> <<unresolved>>");
		for (Map.Entry<SysmlNode, String> ent : objectives.entrySet())
			writeObjective(ent.getKey(), ent.getValue(), text);
		for (Element requirement : drawnRequirements)
			writeRequirement(requirement, text);

		for (Party actor : actors.values())
			for (Element useCase : actor.useCases)
				link(actor.alias, "--", useCase.alias, "actor", roles ? role(actor, useCase) : null);
		for (Object[] include : includes)
			link(((Element) include[0]).alias, "..>",
					include[1] instanceof Element ? ((Element) include[1]).alias : unresolved.get(include[1]), "include",
					relationLabel("include"));
		for (Element useCase : useCases) {
			final Element owner = owner(useCase);
			if (isUseCase(owner) && useCases.contains(owner))
				link(owner.alias, "+--", useCase.alias, ReqModel.CONTAINMENT, null);
			final SysmlNode objective = member(useCase, "objective");
			if (objectives.containsKey(objective))
				link(useCase.alias, "+--", objectives.get(objective), "objective", null);
		}
		for (Relation r : model.getRelations())
			writeRelation(r, drawnRequirements);
		out.addAll(links);

		for (String w : model.getWarnings())
			warn(w);
		if (useCases.isEmpty())
			warn(root == null ? "no use case found" : "no use case in " + root.getQualifiedName());
		footer(source);
	}

	/** The name the use case gives the actor: <code>owner</code> for <code>actor owner : HomeOwner</code>. */
	private static String role(Party actor, Element useCase) {
		for (SysmlNode child : useCase.node.getChildren())
			if (child.isKeyword("actor") && child.getName() != null && reference(child) != null
					&& (actor.name.equals(child.getName()) || actor.type.equals(lastSegment(reference(child)))))
				return SysmlText.quote(child.getName());
		return null;
	}

	/** An objective that says something: a name, a doc text or a requirement. */
	private boolean isWorthDrawing(SysmlNode objective, Set<Element> drawnRequirements) {
		if (objective.getName() != null || ReqWriter.clean(objective.getDoc()) != null)
			return true;
		for (SysmlNode child : objective.getChildren())
			if (drawnRequirements.contains(model.getElement(child)))
				return true;
		return false;
	}

	/**
	 * The requirements drawn: every requirement of the root, or only the ones
	 * the use cases hold (in their objective), then what those are defined
	 * by, specialise, derive from or hold, as far as it goes.
	 */
	private Set<Element> selectRequirements(boolean all) {
		final Set<Element> result = new LinkedHashSet<>();
		final Set<Element> seeds = new HashSet<>();
		for (Element e : model.getElements()) {
			if (e.isRequirement() == false)
				continue;
			if (all ? inRoot(e) : useCases.contains(owner(e)))
				seeds.add(e);
		}
		boolean changed = true;
		while (changed) {
			changed = false;
			for (Relation r : model.getRelations()) {
				if (isTrace(r) == false || r.from.isRequirement() == false || r.to.isRequirement() == false)
					continue;
				if (seeds.contains(r.from) && seeds.add(r.to))
					changed = true;
				if (ReqModel.CONTAINMENT.equals(r.kind) && seeds.contains(r.to) && seeds.add(r.from))
					changed = true;
			}
		}
		// In file order
		for (Element e : model.getElements())
			if (seeds.contains(e))
				result.add(e);
		return result;
	}

	/** Subjects and other member typings are not drawn as links here. */
	private static boolean isTrace(Relation r) {
		return ReqModel.SUBJECT.equals(r.kind) == false && ReqModel.REFERENCES.equals(r.kind) == false;
	}

	private boolean isDrawn(Element e, Set<Element> drawnRequirements) {
		return drawnRequirements.contains(e) || useCases.contains(e);
	}

	private void writeRelation(Relation r, Set<Element> drawnRequirements) {
		if (isTrace(r) == false || isDrawn(r.from, drawnRequirements) == false
				|| isDrawn(r.to, drawnRequirements) == false)
			return;
		if (ReqModel.CONTAINMENT.equals(r.kind)) {
			// A requirement of the objective hangs from the objective's box
			final String objective = objectives.get(r.from.node.getParent());
			link(objective != null ? objective : r.to.alias, "+--", r.from.alias, r.kind, null);
			return;
		}
		final String arrow;
		if (ReqModel.SPECIALIZES.equals(r.kind) || ReqModel.SUBSETS.equals(r.kind))
			arrow = "--|>";
		else if (ReqModel.DEFINED_BY.equals(r.kind))
			arrow = "..|>";
		else
			arrow = "..>";
		link(r.from.alias, arrow, r.to.alias, r.kind, relationLabel(r.kind));
	}

	private void writeUseCase(Element e, String indent) {
		out.add(indent + "usecase \""
				+ SysmlText.call("$sysml_element_label", e.id == null ? "" : e.id, e.name == null ? "" : e.name, e.kind)
				+ "\" as " + e.alias + SysmlText.stereotypes(ReqWriter.stereotype(e.kind)));
	}

	private void writeObjective(SysmlNode objective, String alias, boolean text) {
		final String declaration = "class \""
				// Without a name, the keyword is the whole label
				+ (objective.getName() == null ? SysmlText.call("$sysml_element_label", "", "«objective»", "")
						: SysmlText.call("$sysml_element_label", "", objective.getName(), "objective"))
				+ "\" as " + alias + " <<objective>>";
		final String doc = text ? ReqWriter.clean(objective.getDoc()) : null;
		ReqWriter.body(out, "", declaration,
				Collections.singletonList(doc == null ? Collections.<String>emptyList()
						: Collections.singletonList("{field} " + SysmlText.call("$sysml_text_label", doc, ""))));
	}

	private void writeRequirement(Element e, boolean text) {
		final String id = e.id == null ? "" : e.id;
		final String declaration = "class \"" + SysmlText.call("$sysml_element_label", id, e.name == null ? "" : e.name, e.kind)
				+ "\" as " + e.alias + SysmlText.stereotypes(ReqWriter.stereotype(e.kind));
		final List<List<String>> compartments = new ArrayList<>();
		if (text) {
			// The subject is a boundary here, not a box a link could reach
			final List<String> subject = new ArrayList<>();
			final SysmlNode declared = member(e, "subject");
			if (declared != null && (declared.getName() != null || declared.getType() != null))
				subject.add("{field} subject " + (declared.getName() == null ? "" : declared.getName())
						+ (declared.getType() == null ? "" : " : " + declared.getType()));
			compartments.add(subject);
			final String doc = ReqWriter.clean(e.getDoc());
			compartments.add(doc == null ? Collections.<String>emptyList()
					: Collections.singletonList("{field} " + SysmlText.call("$sysml_text_label", doc, id)));
			compartments.add(ReqWriter.constraints(e.node));
		}
		ReqWriter.body(out, "", declaration, compartments);
	}

	private void writeStyle() {
		out.add("skinparam classAttributeIconSize 0");
		out.add("<style>");
		out.add("classDiagram {");
		out.add("  class { BackGroundColor #FFFFFF; LineColor #404040; MaximumWidth 280 }");
		out.add("  usecase { BackGroundColor #FFF8E1; LineColor #404040 }");
		out.add("  actor { BackGroundColor #FFFFFF; LineColor #404040 }");
		out.add("  rectangle { BackGroundColor #FFFFFF; LineColor #404040 }");
		out.add("  .requirement { BackGroundColor #EAF1FB }");
		out.add("  .requirementDef { BackGroundColor #DCE7F7 }");
		out.add("  .concern { BackGroundColor #F0EAF7 }");
		out.add("  .objective { BackGroundColor #EAF5EA }");
		out.add("  .unresolved { BackGroundColor #FFFFFF; LineColor #9E9E9E; LineStyle 5; FontColor #757575 }");
		out.add("  arrow {");
		out.add("    .include { LineColor #EF6C00; FontColor #EF6C00 }");
		out.add("    .derive { LineColor #6A1B9A; FontColor #6A1B9A }");
		out.add("    .refine { LineColor #6A1B9A; FontColor #6A1B9A }");
		out.add("    .definedBy { LineColor #757575; FontColor #757575 }");
		out.add("    .specializes { LineColor #757575; FontColor #757575 }");
		out.add("    .subsets { LineColor #757575; FontColor #757575 }");
		out.add("  }");
		out.add("}");
		out.add("</style>");
	}

}
