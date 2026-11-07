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
package net.sourceforge.plantuml.sysml.timing;

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
import java.util.TreeMap;
import java.util.function.Predicate;

import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlNode;
import net.sourceforge.plantuml.sysml.SysmlNodes;
import net.sourceforge.plantuml.sysml.SysmlOptions;
import net.sourceforge.plantuml.sysml.SysmlScope;
import net.sourceforge.plantuml.sysml.SysmlText;
import net.sourceforge.plantuml.sysml.SysmlValue;
import net.sourceforge.plantuml.sysml.SysmlWriter;

/**
 * Writes one TimingDiagram as timing diagram source.
 * <ul>
 * <li>Lifelines become players of their kind, in model order:
 * <code>robust</code>, <code>concise</code>, <code>binary</code>,
 * <code>analog</code> (with its <code>between min and max</code> range) and
 * <code>clock</code> (<code>with period p pulse w offset o</code>). A robust
 * lifeline declares its states (<code>has</code>) top to bottom: the states of
 * its machine in machine order, otherwise in order of first use.</li>
 * <li>StateChanges are grouped in <code>@t</code> blocks, earliest first, as
 * <code>alias is value</code>; a change naming a machine state shows the
 * state's name.</li>
 * <li>TimingConstraints become <code>alias@t1 &lt;-&gt; @t2 : label</code> on
 * their <code>drawnOn</code> lifeline (by default the one owning
 * <code>fromChange</code>), TimingMessages <code>a@t1 -&gt; b@t2 : label</code>
 * and TimeHighlights <code>highlight t1 to t2 : caption</code>.</li>
 * </ul>
 * Times are integer nanoseconds in the model and are written in the display
 * unit: <code>unit=</code>, else the diagram's <code>timeUnit</code>, else the
 * largest unit the last instant is at least one of.
 */
final class TimingWriter extends SysmlWriter {

	private static final String[] UNITS = { "ns", "us", "ms", "s" };
	private static final long[] FACTORS = { 1L, 1000L, 1000000L, 1000000000L };

	private static final class Lifeline {
		SysmlNode node;
		String alias;
		String name;
		String kind;
		String ref = "";
		boolean net;
		SysmlNode machine;
		final List<Change> changes = new ArrayList<>();
		/** Robust states: value to code, in axis order. */
		final Map<String, String> states = new LinkedHashMap<>();
	}

	private static final class Change {
		SysmlNode node;
		Lifeline lifeline;
		long at;
		String value;
	}

	private final SysmlModel model;
	private final SysmlNode root;
	private final SysmlOptions options;
	private final SysmlScope<SysmlNode> scope = new SysmlScope<>();
	private final List<Lifeline> lifelines = new ArrayList<>();
	private final Map<SysmlNode, Lifeline> lifelineByNode = new HashMap<>();
	private final Map<SysmlNode, Change> changeByNode = new HashMap<>();
	private final List<Object[]> pendingChanges = new ArrayList<>();
	private final List<SysmlNode> constraints = new ArrayList<>();
	private final List<SysmlNode> messages = new ArrayList<>();
	private final List<SysmlNode> highlights = new ArrayList<>();
	private final Map<SysmlNode, List<String>> paths = new HashMap<>();
	private final Set<String> aliases = new HashSet<>();
	private int unit;

	TimingWriter(SysmlModel model, SysmlNode root, SysmlOptions options, Predicate<String> isFunctionDefined) {
		super(isFunctionDefined);
		this.model = model;
		this.root = root;
		this.options = options;
	}

	// ------------------------------------------------------------------
	// Reading
	// ------------------------------------------------------------------

	private static List<String> qualifiedPath(SysmlNode node) {
		return Arrays.asList(node.getQualifiedName().split("::"));
	}

	private void read() {
		// Every usage of every file is in scope: nets, machines and their
		// states live in the board and machine files
		for (SysmlNode usage : model.getTopLevelUsages()) {
			if (usage == root || usage.getQualifiedName() == null)
				continue;
			final List<String> path = qualifiedPath(usage);
			scope.register(path, usage);
			walk(model, usage, path, new Visitor() {
				public boolean visit(SysmlNode node, List<String> p, String category) {
					scope.register(p, node);
					return true;
				}
			});
		}
		final List<String> rootPath = qualifiedPath(root);
		scope.register(rootPath, root);
		walk(model, root, rootPath, new Visitor() {
			public boolean visit(SysmlNode node, List<String> path, String category) {
				scope.register(path, node);
				paths.put(node, path);
				if (TimingProfile.LIFELINE.equals(category))
					readLifeline(node);
				else if (TimingProfile.CHANGE.equals(category))
					pendingChanges.add(new Object[] { node, path });
				else if (TimingProfile.CONSTRAINT.equals(category))
					constraints.add(node);
				else if (TimingProfile.MESSAGE.equals(category))
					messages.add(node);
				else if (TimingProfile.HIGHLIGHT.equals(category))
					highlights.add(node);
				return true;
			}
		});
		for (Lifeline l : lifelines)
			readLifelineRefs(l);
		for (Object[] pending : pendingChanges)
			readChange((SysmlNode) pending[0], (List<String>) pending[1]);
		for (Lifeline l : lifelines)
			Collections.sort(l.changes, new Comparator<Change>() {
				public int compare(Change a, Change b) {
					return Long.compare(a.at, b.at);
				}
			});
		unit = selectUnit();
	}

	private void readLifeline(SysmlNode node) {
		final Lifeline l = new Lifeline();
		l.node = node;
		l.name = SysmlNodes.text(model, node, "name", node.getName());
		l.alias = SysmlText.unique(SysmlText.sanitize(node.getName()), aliases);
		l.kind = SysmlNodes.text(model, node, "kind", "");
		if (Arrays.asList("robust", "concise", "binary", "analog", "clock").contains(l.kind) == false) {
			warn(node.getName() + ": " + (l.kind.isEmpty() ? "no kind" : "unknown kind '" + l.kind + "'")
					+ ", drawn as concise");
			l.kind = "concise";
		}
		lifelines.add(l);
		lifelineByNode.put(node, l);
	}

	private void readLifelineRefs(Lifeline l) {
		final List<String> path = paths.get(l.node);
		final String netRef = raw(l.node, "net");
		if (netRef != null) {
			l.net = true;
			final SysmlNode net = resolve(netRef, path);
			l.ref = net == null ? SysmlNodes.lastSegment(netRef) : SysmlNodes.text(model, net, "name", net.getName());
		}
		final String machineRef = raw(l.node, "machine");
		if (machineRef != null) {
			l.machine = resolve(machineRef, path);
			if (l.machine == null)
				warn(l.node.getName() + ": cannot resolve machine '" + machineRef + "'");
			l.ref = l.machine == null ? SysmlNodes.lastSegment(machineRef)
					: SysmlNodes.text(model, l.machine, "name", l.machine.getName());
		}
	}

	private void readChange(SysmlNode node, List<String> path) {
		Lifeline owner = null;
		for (SysmlNode n = node.getParent(); n != null && owner == null; n = n.getParent())
			owner = lifelineByNode.get(n);
		if (owner == null) {
			warn(node.getName() + ": StateChange outside a Lifeline");
			return;
		}
		final Change c = new Change();
		c.node = node;
		c.lifeline = owner;
		final Long at = parseLong(node, "atNs");
		if (at == null)
			return;
		c.at = at;
		c.value = SysmlNodes.text(model, node, "value", "");
		final String stateRef = raw(node, "machineState");
		if (stateRef != null && c.value.isEmpty()) {
			final SysmlNode state = resolve(stateRef, path);
			if (state == null) {
				warn(node.getName() + ": cannot resolve machineState '" + stateRef + "'");
				c.value = SysmlNodes.lastSegment(stateRef);
			} else {
				c.value = SysmlNodes.text(model, state, "name", state.getName());
			}
		}
		if (c.value.isEmpty()) {
			warn(node.getName() + ": StateChange without a value");
			return;
		}
		if ("clock".equals(owner.kind)) {
			warn(node.getName() + ": StateChange of clock '" + owner.node.getName() + "' ignored");
			return;
		}
		for (Change other : owner.changes)
			if (other.at == c.at) {
				warn(node.getName() + ": '" + owner.node.getName() + "' already changes at " + c.at
						+ " ns, ignored");
				return;
			}
		owner.changes.add(c);
		changeByNode.put(node, c);
	}

	private String raw(SysmlNode node, String feature) {
		final SysmlValue v = SysmlNodes.value(model, node, feature);
		return v == null ? null : v.getRaw();
	}

	private SysmlNode resolve(String reference, List<String> context) {
		if (reference == null)
			return null;
		final SysmlScope.Found<SysmlNode> found = scope.resolve(reference, context);
		if (found == null || found.getPort() != null)
			return null;
		return found.getElement();
	}

	private Long parseLong(SysmlNode node, String feature) {
		final String text = SysmlNodes.text(model, node, feature);
		if (text == null) {
			warn(node.getName() + ": no " + feature);
			return null;
		}
		try {
			return Long.parseLong(text.trim());
		} catch (NumberFormatException e) {
			warn(node.getName() + ": " + feature + " '" + text + "' is not an integer");
			return null;
		}
	}

	/** Optional integer: <code>null</code> when unset, warned when not an integer. */
	private Long optionalLong(SysmlNode node, String feature) {
		if (SysmlNodes.text(model, node, feature) == null)
			return null;
		return parseLong(node, feature);
	}

	/** The states of a machine, in model order. */
	private List<SysmlNode> machineStates(SysmlNode machine) {
		final List<SysmlNode> result = new ArrayList<>();
		walk(model, machine, qualifiedPath(machine), new Visitor() {
			public boolean visit(SysmlNode node, List<String> path, String category) {
				if (TimingProfile.STATE.equals(category) || TimingProfile.COMPOSITE_STATE.equals(category)
						|| TimingProfile.SUBMACHINE_STATE.equals(category))
					result.add(node);
				return true;
			}
		});
		return result;
	}

	private int selectUnit() {
		String name = options.get(TimingProfile.UNIT, "model");
		if (name.equals("model"))
			name = SysmlNodes.text(model, root, "timeUnit", "");
		for (int i = 0; i < UNITS.length; i++)
			if (UNITS[i].equals(name))
				return i;
		if (name.isEmpty() == false)
			warn("unknown timeUnit '" + name + "'");
		long last = 0;
		for (Lifeline l : lifelines)
			for (Change c : l.changes)
				last = Math.max(last, c.at);
		int result = 0;
		for (int i = 0; i < FACTORS.length; i++)
			if (last >= FACTORS[i])
				result = i;
		return result;
	}

	// ------------------------------------------------------------------
	// Writing
	// ------------------------------------------------------------------

	void run(String source) {
		read();
		final Map<String, String> attributes = new LinkedHashMap<>();
		attributes.put("name", SysmlNodes.text(model, root, "name", root.getName()));
		final String timeUnit = SysmlNodes.text(model, root, "timeUnit");
		if (timeUnit != null)
			attributes.put("timeUnit", timeUnit);
		attributes.put("unit", UNITS[unit]);
		header(source, "TimingProfile", root.getName(), attributes);
		writeLabelFunctions();
		if (options.isStyle())
			writeStyle();
		if (options.getBoolean(TimingProfile.COMPACT, false))
			out.add("mode compact");
		out.add("time-axis unit \"" + axisUnit() + "\"");

		final List<Lifeline> drawn = new ArrayList<>();
		for (Lifeline l : lifelines)
			if (declare(l))
				drawn.add(l);
		for (Lifeline l : drawn)
			if ("robust".equals(l.kind))
				declareStates(l);

		final TreeMap<Long, List<Change>> byTime = new TreeMap<>();
		for (Lifeline l : drawn)
			for (Change c : l.changes) {
				if (byTime.containsKey(c.at) == false)
					byTime.put(c.at, new ArrayList<Change>());
				byTime.get(c.at).add(c);
			}
		for (Map.Entry<Long, List<Change>> ent : byTime.entrySet()) {
			final List<String> block = new ArrayList<>();
			for (Change c : ent.getValue()) {
				final String value = value(c);
				if (value != null)
					block.add(c.lifeline.alias + " is " + value);
			}
			if (block.isEmpty() == false) {
				out.add("@" + time(ent.getKey()));
				out.addAll(block);
			}
		}
		// An empty block at endNs stretches the time axis
		final Long end = optionalLong(root, "endNs");
		if (end != null) {
			if (byTime.isEmpty() || end > byTime.lastKey())
				out.add("@" + time(end));
			else if (end < byTime.lastKey())
				warn("endNs " + end + " is before the last StateChange");
		}

		for (SysmlNode c : constraints)
			writeConstraint(c, drawn);
		for (SysmlNode m : messages)
			writeMessage(m, drawn);
		for (SysmlNode h : highlights)
			writeHighlight(h);
		footer(source);
	}

	private String axisUnit() {
		return "us".equals(UNITS[unit]) ? "µs" : UNITS[unit];
	}

	private void writeLabelFunctions() {
		defaultFunction("$sysml_lifeline_label", "$name, $kind, $ref, $unit", //
				"!if $unit == \"\"", //
				"!return $name", //
				"!endif", //
				"!return $name + \" (\" + $unit + \")\"");
		defaultFunction("$sysml_value_label", "$value, $kind", //
				"!return $value");
		defaultFunction("$sysml_constraint_label", "$name, $bound", //
				"!if $name == \"\"", //
				"!return \"{\" + $bound + \"}\"", //
				"!endif", //
				"!return $name + \" {\" + $bound + \"}\"");
		defaultFunction("$sysml_message_label", "$name", //
				"!return $name");
		defaultFunction("$sysml_highlight_label", "$name", //
				"!return $name");
	}

	private void writeStyle() {
		out.add("<style>");
		out.add("timingDiagram {");
		out.add("  .net { LineColor #1565C0 }");
		out.add("  .machine { LineColor #2E7D32 }");
		out.add("  constraintArrow { LineColor #B00020; FontColor #B00020 }");
		out.add("  highlight { BackgroundColor #FFF4D6 }");
		out.add("}");
		out.add("</style>");
	}

	private String stereotypes(Lifeline l) {
		return SysmlText.stereotypes(l.kind, l.net ? "net" : "", l.machine != null || raw(l.node, "machine") != null
				? "machine" : "");
	}

	private boolean declare(Lifeline l) {
		final String unitText = "analog".equals(l.kind) ? SysmlNodes.text(model, l.node, "valueUnit", "") : "";
		final String label = "\"" + SysmlText.call("$sysml_lifeline_label", l.name, l.kind, l.ref, unitText)
				+ "\"";
		if ("clock".equals(l.kind)) {
			final Long period = optionalLong(l.node, "periodNs");
			if (period == null || period <= 0) {
				warn(l.node.getName() + ": clock without a positive periodNs, not drawn");
				return false;
			}
			final Long pulse = optionalLong(l.node, "pulseNs");
			final Long offset = optionalLong(l.node, "offsetNs");
			out.add("clock " + label + " as " + l.alias + " with period " + time(period)
					+ (pulse == null ? "" : " pulse " + time(pulse)) + (offset == null ? "" : " offset " + time(offset))
					+ stereotypes(l));
			return true;
		}
		if ("analog".equals(l.kind)) {
			final String min = SysmlNodes.text(model, l.node, "minValue");
			final String max = SysmlNodes.text(model, l.node, "maxValue");
			final String range = number(min) != null && number(max) != null
					? " between " + min.trim() + " and " + max.trim() : "";
			out.add("analog " + label + range + " as " + l.alias + stereotypes(l));
			return true;
		}
		out.add(l.kind + " " + label + " as " + l.alias + stereotypes(l));
		return true;
	}

	private void declareStates(Lifeline l) {
		final List<String> used = new ArrayList<>();
		for (Change c : l.changes)
			if (isSpecial(c.value) == false && used.contains(c.value) == false)
				used.add(c.value);
		final List<String> order = new ArrayList<>();
		if (l.machine != null) {
			final boolean all = "all".equals(options.get(TimingProfile.STATES, "used"));
			final List<String> names = new ArrayList<>();
			for (SysmlNode state : machineStates(l.machine))
				names.add(SysmlNodes.text(model, state, "name", state.getName()));
			for (String name : names)
				if ((all || used.contains(name)) && order.contains(name) == false)
					order.add(name);
			for (String value : used)
				if (names.contains(value) == false) {
					warn(l.node.getName() + ": '" + value + "' is not a state of '" + l.ref + "'");
					order.add(value);
				}
		} else {
			order.addAll(used);
		}
		final Set<String> codes = new HashSet<>();
		for (String value : order) {
			final String code = SysmlText.unique(SysmlText.sanitize(value), codes);
			l.states.put(value, code);
			out.add(l.alias + " has \"" + SysmlText.call("$sysml_value_label", value, l.kind) + "\" as " + code);
		}
	}

	private static boolean isSpecial(String value) {
		return value.startsWith("{") && value.endsWith("}");
	}

	private String value(Change c) {
		final String v = c.value.trim();
		final String kind = c.lifeline.kind;
		if (isSpecial(v))
			return v;
		if ("binary".equals(kind)) {
			final String lower = v.toLowerCase();
			if (lower.equals("high") || lower.equals("1"))
				return "high";
			if (lower.equals("low") || lower.equals("0"))
				return "low";
			warn(c.node.getName() + ": '" + v + "' is not a binary value, not drawn");
			return null;
		}
		if ("analog".equals(kind)) {
			if (number(v) == null) {
				warn(c.node.getName() + ": '" + v + "' is not a number, not drawn");
				return null;
			}
			return v;
		}
		if ("robust".equals(kind))
			return c.lifeline.states.get(c.value);
		return "\"" + SysmlText.call("$sysml_value_label", v, kind) + "\"";
	}

	private static Double number(String text) {
		if (text == null)
			return null;
		try {
			return Double.parseDouble(text.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** A referenced Lifeline of this diagram, or <code>null</code> (warned). */
	private Lifeline lifeline(SysmlNode node, String feature, List<Lifeline> drawn, boolean optional) {
		final String reference = raw(node, feature);
		if (reference == null) {
			if (optional == false)
				warn(node.getName() + ": no " + feature);
			return null;
		}
		final Lifeline l = lifelineByNode.get(resolve(reference, paths.get(node)));
		if (l == null || drawn.contains(l) == false) {
			warn(node.getName() + ": " + feature + " '" + reference + "' is not a drawn Lifeline of the diagram");
			return null;
		}
		return l;
	}

	private Change change(SysmlNode node, String feature) {
		final String reference = raw(node, feature);
		if (reference == null) {
			warn(node.getName() + ": no " + feature);
			return null;
		}
		final Change c = changeByNode.get(resolve(reference, paths.get(node)));
		if (c == null)
			warn(node.getName() + ": " + feature + " '" + reference + "' is not a StateChange of the diagram");
		return c;
	}

	private void writeConstraint(SysmlNode node, List<Lifeline> drawn) {
		final Change from = change(node, "fromChange");
		final Change to = change(node, "toChange");
		if (from == null || to == null)
			return;
		Lifeline on = lifeline(node, "drawnOn", drawn, true);
		if (on == null)
			on = from.lifeline;
		if (drawn.contains(on) == false)
			return;
		final String name = SysmlNodes.text(model, node, "name", "");
		out.add(on.alias + "@" + time(from.at) + " <-> @" + time(to.at) + " : "
				+ SysmlText.call("$sysml_constraint_label", name, bound(node)));
	}

	private String bound(SysmlNode node) {
		final String kind = SysmlNodes.text(model, node, "kind", "between");
		final Long lowerNs = optionalLong(node, "lowerNs");
		final Long upperNs = optionalLong(node, "upperNs");
		final String lower = lowerNs == null ? "" : duration(lowerNs);
		final String upper = upperNs == null ? "" : duration(upperNs);
		if ("atMost".equals(kind))
			return "≤ " + upper;
		if ("atLeast".equals(kind))
			return "≥ " + lower;
		if ("exactly".equals(kind))
			return "= " + (lower.isEmpty() ? upper : lower);
		return lower + " – " + upper;
	}

	private void writeMessage(SysmlNode node, List<Lifeline> drawn) {
		final Lifeline sender = lifeline(node, "sender", drawn, false);
		final Lifeline receiver = lifeline(node, "receiver", drawn, false);
		final Long send = parseLong(node, "sendNs");
		if (sender == null || receiver == null || send == null)
			return;
		final Long receive = optionalLong(node, "receiveNs");
		final String name = SysmlNodes.text(model, node, "name", "");
		out.add(sender.alias + "@" + time(send) + " -> " + receiver.alias + "@" + time(receive == null ? send : receive)
				+ " : " + SysmlText.call("$sysml_message_label", name));
	}

	private void writeHighlight(SysmlNode node) {
		final Long from = parseLong(node, "fromNs");
		final Long to = parseLong(node, "toNs");
		if (from == null || to == null)
			return;
		final String name = SysmlNodes.text(model, node, "name", "");
		out.add("highlight " + time(from) + " to " + time(to) + " : "
				+ SysmlText.call("$sysml_highlight_label", name));
	}

	/** Nanoseconds in the display unit, as a decimal without trailing zeros. */
	String time(long ns) {
		return decimal(ns, FACTORS[unit]);
	}

	static String decimal(long value, long factor) {
		final StringBuilder sb = new StringBuilder();
		if (value < 0)
			sb.append('-');
		final long abs = Math.abs(value);
		sb.append(abs / factor);
		long frac = abs % factor;
		if (frac != 0) {
			String digits = Long.toString(frac + factor).substring(1);
			while (digits.endsWith("0"))
				digits = digits.substring(0, digits.length() - 1);
			sb.append('.').append(digits);
		}
		return sb.toString();
	}

	/** A duration in the largest unit that keeps it whole. */
	static String duration(long ns) {
		for (int i = FACTORS.length - 1; i > 0; i--)
			if (ns != 0 && ns % FACTORS[i] == 0)
				return ns / FACTORS[i] + " " + ("us".equals(UNITS[i]) ? "µs" : UNITS[i]);
		return ns + " ns";
	}

}
