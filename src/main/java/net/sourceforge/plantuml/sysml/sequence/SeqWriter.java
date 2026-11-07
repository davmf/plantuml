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
package net.sourceforge.plantuml.sysml.sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlNodes;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlScope;
import net.sourceforge.plantuml.sysml.SysmlText;
import net.sourceforge.plantuml.sysml.SysmlWriter;

/**
 * Writes one Interaction as sequence diagram source.
 * <ul>
 * <li>Lifelines become participants (<code>actor</code> for an
 * actorParticipant), in model order; a Lifeline created by a createMessage is
 * declared where it is created.</li>
 * <li>Messages are sorted by <code>order</code> and drawn with the arrow of
 * their kind: <code>-&gt;</code>, <code>-&gt;&gt;</code>,
 * <code>--&gt;</code>, a create, a destroy (<code>!!</code>), found
 * (<code>[o-&gt;</code>), lost (<code>-&gt;o]</code>); a GateMessage comes in
 * from the frame (<code>[-&gt;</code>).</li>
 * <li>CombinedFragments become <code>alt</code>/<code>else</code>,
 * <code>opt</code>, <code>loop</code>, <code>par</code>, <code>break</code>,
 * <code>critical</code> or a <code>group</code> named after the operator,
 * spanning the messages their operands list.</li>
 * <li>ExecutionSpecifications become <code>activate</code>/<code>deactivate</code>
 * after their start and finish messages; TimeObservations and
 * DurationObservations a note on their message.</li>
 * </ul>
 */
final class SeqWriter extends SysmlWriter {

	private static final class Lifeline {
		SysmlNode node;
		String alias;
		String name;
		String className;
		boolean actor;
		boolean created;
		final List<String> invariants = new ArrayList<>();
	}

	private static final class Message {
		SysmlNode node;
		List<String> path;
		int index;
		long order;
		Lifeline source;
		Lifeline target;
		boolean fromGate;
		String kind;
		final List<String> activate = new ArrayList<>();
		final List<String> deactivate = new ArrayList<>();
	}

	private static final class Fragment {
		String operator;
		final List<Operand> operands = new ArrayList<>();
		int first = Integer.MAX_VALUE;
		int last = -1;
		boolean opened;
	}

	private static final class Operand {
		String guard;
		long order;
		int first = Integer.MAX_VALUE;
		final Set<Message> messages = new HashSet<>();
		final List<Fragment> fragments = new ArrayList<>();
	}

	private final SysmlModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final SysmlScope<Object> scope = new SysmlScope<>();
	private final List<Lifeline> lifelines = new ArrayList<>();
	private final List<Message> messages = new ArrayList<>();
	private final List<Fragment> fragments = new ArrayList<>();
	private final List<Object[]> executions = new ArrayList<>();
	private final Set<String> aliases = new HashSet<>();

	SeqWriter(SysmlModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
	}

	// ------------------------------------------------------------------
	// Reading
	// ------------------------------------------------------------------

	private void read() {
		final List<String> rootPath = Arrays.asList(root.getName());
		walk(model, root, rootPath, new Visitor() {
			public boolean visit(SysmlNode node, List<String> path, String category) {
				scope.register(path, node);
				if (SeqProfile.LIFELINE.equals(category))
					readLifeline(node, path);
				else if (SeqProfile.MESSAGE.equals(category) || SeqProfile.GATE_MESSAGE.equals(category)) {
					final Message m = new Message();
					m.node = node;
					m.path = path;
					m.index = messages.size();
					m.fromGate = SeqProfile.GATE_MESSAGE.equals(category);
					messages.add(m);
					return false;
				}
				return true;
			}
		});
		// Lifelines and gates are registered: resolve the messages
		for (Message m : messages)
			readMessage(m);
		Collections.sort(messages, new Comparator<Message>() {
			public int compare(Message a, Message b) {
				final int c = Long.compare(a.order, b.order);
				return c != 0 ? c : Integer.compare(a.index, b.index);
			}
		});
		for (int i = 0; i < messages.size(); i++)
			messages.get(i).index = i;

		walk(model, root, rootPath, new Visitor() {
			public boolean visit(SysmlNode node, List<String> path, String category) {
				if (SeqProfile.FRAGMENT.equals(category)) {
					fragments.add(readFragment(node, path));
					return false;
				}
				if (SeqProfile.EXECUTION.equals(category))
					executions.add(new Object[] { node, path });
				return true;
			}
		});
		for (Object[] e : executions)
			readExecution((SysmlNode) e[0], (List<String>) e[1]);
	}

	private final Map<SysmlNode, Lifeline> lifelineByNode = new HashMap<>();
	private final Map<SysmlNode, Message> messageByNode = new HashMap<>();

	private void readLifeline(SysmlNode node, List<String> path) {
		final Lifeline l = new Lifeline();
		l.node = node;
		l.name = SysmlNodes.text(model, node, "name", node.getName());
		l.alias = SysmlText.unique(SysmlText.sanitize(node.getName()), aliases);
		l.actor = "actorParticipant".equals(SysmlNodes.text(model, node, "kind"));
		final SysmlValueRef represents = ref(node, "represents", path);
		l.className = represents == null ? "" : SysmlNodes.text(model, represents.node, "name", represents.node.getName());
		lifelines.add(l);
		lifelineByNode.put(node, l);
		for (SysmlNode member : SysmlNodes.members(node))
			collectInvariants(l, member);
	}

	private void collectInvariants(Lifeline l, SysmlNode node) {
		if (SeqProfile.INVARIANT.equals(model.classify(node.getType()))) {
			final String expression = SysmlNodes.text(model, node, "expression", "");
			l.invariants.add(expression.isEmpty() ? SysmlNodes.text(model, node, "name", node.getName()) : expression);
			return;
		}
		if (node.getType() == null)
			for (SysmlNode member : SysmlNodes.members(node))
				collectInvariants(l, member);
	}

	/** A referenced node. */
	private static final class SysmlValueRef {
		final SysmlNode node;

		SysmlValueRef(SysmlNode node) {
			this.node = node;
		}
	}

	private SysmlValueRef ref(SysmlNode node, String feature, List<String> context) {
		final String raw = SysmlNodes.value(model, node, feature) == null ? null
				: SysmlNodes.value(model, node, feature).getRaw();
		return resolve(raw, context);
	}

	private SysmlValueRef resolve(String reference, List<String> context) {
		if (reference == null)
			return null;
		final SysmlScope.Found<Object> found = scope.resolve(reference, context);
		if (found == null || found.getPort() != null)
			return null;
		return new SysmlValueRef((SysmlNode) found.getElement());
	}

	private void readMessage(Message m) {
		messageByNode.put(m.node, m);
		m.kind = SysmlNodes.text(model, m.node, "kind", "synchronous");
		try {
			m.order = Long.parseLong(SysmlNodes.text(model, m.node, "order", "0").trim());
		} catch (NumberFormatException e) {
			warn(m.node.getName() + ": order is not an integer");
		}
		final String[] ends = SysmlNodes.ends(m.node);
		if (ends == null) {
			warn(m.node.getName() + ": message without source and target");
			return;
		}
		m.source = lifeline(ends[0], m);
		m.target = lifeline(ends[1], m);
	}

	private Lifeline lifeline(String reference, Message m) {
		final SysmlScope.Found<Object> found = scope.resolve(reference, m.path);
		if (found == null) {
			warn(m.node.getName() + ": cannot resolve '" + reference + "'");
			return null;
		}
		final SysmlNode node = (SysmlNode) found.getElement();
		final Lifeline l = lifelineByNode.get(node);
		if (l == null && SeqProfile.GATE.equals(model.classify(node.getType())) == false)
			warn(m.node.getName() + ": '" + reference + "' is neither a Lifeline nor a Gate");
		return l;
	}

	private Fragment readFragment(SysmlNode node, List<String> path) {
		final Fragment f = new Fragment();
		f.operator = SysmlNodes.text(model, node, "op", "seqFragment");
		collectOperands(node, path, f);
		Collections.sort(f.operands, new Comparator<Operand>() {
			public int compare(Operand a, Operand b) {
				return Long.compare(a.order, b.order);
			}
		});
		for (Operand o : f.operands) {
			for (Message m : o.messages) {
				o.first = Math.min(o.first, m.index);
				f.last = Math.max(f.last, m.index);
			}
			for (Fragment nested : o.fragments) {
				o.first = Math.min(o.first, nested.first);
				f.last = Math.max(f.last, nested.last);
			}
			f.first = Math.min(f.first, o.first);
		}
		if (f.last == -1)
			warn(node.getName() + ": CombinedFragment without messages, not drawn");
		return f;
	}

	private void collectOperands(SysmlNode node, List<String> path, Fragment f) {
		for (SysmlNode member : SysmlNodes.members(node)) {
			final List<String> p = append(path, member.getName());
			final String c = model.classify(member.getType());
			if (SeqProfile.OPERAND.equals(c))
				f.operands.add(readOperand(member, p));
			else if (c == null && member.getType() == null)
				collectOperands(member, p, f);
		}
	}

	private Operand readOperand(SysmlNode node, List<String> path) {
		final Operand o = new Operand();
		o.guard = SysmlNodes.text(model, node, "guard", "");
		try {
			o.order = Long.parseLong(SysmlNodes.text(model, node, "order", "0").trim());
		} catch (NumberFormatException e) {
			o.order = 0;
		}
		for (String ref : SysmlNodes.list(model, node, "messages")) {
			final SysmlValueRef r = resolve(ref, path);
			final Message m = r == null ? null : messageByNode.get(r.node);
			if (m == null)
				warn(node.getName() + ": cannot resolve message '" + ref + "'");
			else
				o.messages.add(m);
		}
		walk(model, node, path, new Visitor() {
			public boolean visit(SysmlNode member, List<String> p, String category) {
				if (SeqProfile.FRAGMENT.equals(category)) {
					o.fragments.add(readFragment(member, p));
					return false;
				}
				return true;
			}
		});
		return o;
	}

	private void readExecution(SysmlNode node, List<String> path) {
		Lifeline owner = null;
		for (Lifeline l : lifelines)
			if (path.size() > 1 && isUnder(node, l.node))
				owner = l;
		if (owner == null) {
			warn(node.getName() + ": ExecutionSpecification outside a Lifeline");
			return;
		}
		final SysmlValueRef start = ref(node, "startMessage", path);
		final SysmlValueRef finish = ref(node, "finishMessage", path);
		final Message startMessage = start == null ? null : messageByNode.get(start.node);
		final Message finishMessage = finish == null ? null : messageByNode.get(finish.node);
		if (startMessage == null) {
			warn(node.getName() + ": cannot resolve startMessage");
			return;
		}
		startMessage.activate.add(owner.alias);
		if (finishMessage != null)
			finishMessage.deactivate.add(owner.alias);
	}

	private static boolean isUnder(SysmlNode node, SysmlNode ancestor) {
		for (SysmlNode n = node.getParent(); n != null; n = n.getParent())
			if (n == ancestor)
				return true;
		return false;
	}

	// ------------------------------------------------------------------
	// Writing
	// ------------------------------------------------------------------

	void run(String source) {
		read();
		final Map<String, String> attributes = new LinkedHashMap<>();
		attributes.put("name", SysmlNodes.text(model, root, "name", root.getName()));
		header(source, "SequenceProfile", root.getName(), attributes);
		writeLabelFunctions();
		if (options.getBoolean("autonumber", false))
			out.add("autonumber");

		for (Message m : messages)
			if ("createMessage".equals(m.kind) && m.target != null)
				m.target.created = true;
		for (Lifeline l : lifelines)
			if (l.created == false)
				out.add(participant(l));

		final List<Fragment> all = new ArrayList<>();
		flatten(fragments, all);
		for (Message m : messages) {
			writeFragmentStarts(all, m.index);
			writeMessage(m);
			writeFragmentEnds(all, m.index);
		}
		for (Lifeline l : lifelines)
			for (String invariant : l.invariants)
				out.add("hnote over " + l.alias + " : " + SysmlText.call("$sysml_invariant_label", invariant));
		footer(source);
	}

	private static void flatten(List<Fragment> fragments, List<Fragment> result) {
		for (Fragment f : fragments) {
			if (f.last == -1)
				continue;
			result.add(f);
			for (Operand o : f.operands)
				flatten(o.fragments, result);
		}
	}

	private void writeLabelFunctions() {
		defaultFunction("$sysml_lifeline_label", "$name, $class", //
				"!if $class == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $name + \" : \" + $class");
		defaultFunction("$sysml_message_label", "$signature, $arguments, $kind, $name", //
				"!if $kind == \"reply\" && $arguments != \"\"", //
				"!return $arguments", //
				"!endif", //
				"!if $signature == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $signature");
		defaultFunction("$sysml_time_label", "$name, $bound", //
				"!return $bound");
		defaultFunction("$sysml_invariant_label", "$expression", //
				"!return \"{\" + $expression + \"}\"");
	}

	private String participant(Lifeline l) {
		return (l.actor ? "actor" : "participant") + " \"" + SysmlText.call("$sysml_lifeline_label", l.name,
				l.className) + "\" as " + l.alias;
	}

	private void writeFragmentStarts(List<Fragment> all, int index) {
		// An operand after the first starts with 'else' in its open fragment
		for (Fragment f : all)
			if (f.opened && f.first < index && index <= f.last)
				for (int i = 1; i < f.operands.size(); i++)
					if (f.operands.get(i).first == index)
						out.add("else" + guard(f.operands.get(i).guard));
		// Outermost first: the larger span opens first
		final List<Fragment> starting = new ArrayList<>();
		for (Fragment f : all)
			if (f.first == index)
				starting.add(f);
		Collections.sort(starting, new Comparator<Fragment>() {
			public int compare(Fragment a, Fragment b) {
				return Integer.compare(b.last, a.last);
			}
		});
		for (Fragment f : starting) {
			f.opened = true;
			final String guard = f.operands.isEmpty() ? "" : f.operands.get(0).guard;
			out.add(keyword(f.operator) + guard(guard));
		}
	}

	private void writeFragmentEnds(List<Fragment> all, int index) {
		final List<Fragment> ending = new ArrayList<>();
		for (Fragment f : all)
			if (f.opened && f.last == index)
				ending.add(f);
		// Innermost first: the one that started last closes first
		Collections.sort(ending, new Comparator<Fragment>() {
			public int compare(Fragment a, Fragment b) {
				return Integer.compare(b.first, a.first);
			}
		});
		for (Fragment f : ending) {
			f.opened = false;
			out.add("end");
		}
	}

	private static String guard(String guard) {
		return guard == null || guard.isEmpty() ? "" : " " + guard;
	}

	private static String keyword(String operator) {
		switch (operator) {
		case "altFragment":
			return "alt";
		case "optFragment":
			return "opt";
		case "loopFragment":
			return "loop";
		case "parFragment":
			return "par";
		case "breakFragment":
			return "break";
		case "criticalFragment":
			return "critical";
		default:
			String name = operator.endsWith("Fragment") ? operator.substring(0, operator.length() - 8) : operator;
			return "group " + name;
		}
	}

	private void writeMessage(Message m) {
		final SysmlNode n = m.node;
		final String name = SysmlNodes.text(model, n, "name", n.getName());
		final String label = SysmlText.call("$sysml_message_label", SysmlNodes.text(model, n, "signature", ""),
				String.join(", ", SysmlNodes.list(model, n, "arguments")), m.kind, name);
		final String from = m.source == null ? null : m.source.alias;
		final String to = m.target == null ? null : m.target.alias;
		final String line;
		if (m.fromGate || "found".equals(m.kind)) {
			if (to == null)
				return;
			line = (m.fromGate ? "[" : "[o") + arrow(m.kind) + " " + to;
		} else if ("lost".equals(m.kind)) {
			if (from == null)
				return;
			line = from + " " + arrow(m.kind) + "o]";
		} else {
			if (from == null || to == null)
				return;
			if ("createMessage".equals(m.kind) && m.target.created) {
				out.add("create " + participant(m.target));
				m.target.created = false;
			}
			line = from + " " + arrow(m.kind) + " " + to + ("destroyMessage".equals(m.kind) ? " !!" : "");
		}
		out.add(line + " : " + label);
		writeTiming(m, "timing");
		writeTiming(m, "duration");
		for (String alias : m.activate)
			out.add("activate " + alias);
		for (String alias : m.deactivate)
			out.add("deactivate " + alias);
	}

	private static String arrow(String kind) {
		if ("asynchronous".equals(kind))
			return "->>";
		if ("reply".equals(kind))
			return "-->";
		return "->";
	}

	private void writeTiming(Message m, String feature) {
		final SysmlValueRef r = ref(m.node, feature, m.path);
		if (r == null)
			return;
		final String kind = SysmlNodes.text(model, r.node, "kind", "between");
		final String lower = time(SysmlNodes.text(model, r.node, "lowerNs"));
		final String upper = time(SysmlNodes.text(model, r.node, "upperNs"));
		final String bound;
		if ("atMost".equals(kind))
			bound = "≤ " + upper;
		else if ("atLeast".equals(kind))
			bound = "≥ " + lower;
		else if ("exactly".equals(kind))
			bound = "= " + (lower.isEmpty() ? upper : lower);
		else
			bound = lower + " – " + upper;
		out.add("note right : " + SysmlText.call("$sysml_time_label",
				SysmlNodes.text(model, r.node, "name", r.node.getName()), bound));
	}

	/** Integer nanoseconds in the largest unit that keeps them whole. */
	static String time(String ns) {
		if (ns == null || ns.isEmpty())
			return "";
		try {
			final long v = Long.parseLong(ns.trim());
			if (v != 0 && v % 1000000000L == 0)
				return v / 1000000000L + " s";
			if (v != 0 && v % 1000000L == 0)
				return v / 1000000L + " ms";
			if (v != 0 && v % 1000L == 0)
				return v / 1000L + " µs";
			return v + " ns";
		} catch (NumberFormatException e) {
			return ns;
		}
	}

}
