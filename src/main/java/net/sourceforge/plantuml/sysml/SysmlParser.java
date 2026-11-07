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
import java.util.List;
import java.util.Set;

/**
 * A tolerant reader for the SysML v2 textual notation.
 * <p>
 * It does not check the language: it builds the tree of packages,
 * definitions and usages and records, for each member, its name, type,
 * specializations, redefinition, bound value and <code>connect</code> ends.
 * The multiplicity and the modifiers (<code>abstract</code>,
 * <code>ref</code>, <code>in</code>...) are kept too. Anything else in a
 * declaration (expressions, metadata...) is skipped by balancing brackets, so constructs it does not know degrade
 * gracefully instead of failing.
 */
public final class SysmlParser {

	private static final Set<String> MODIFIERS = new HashSet<>(Arrays.asList("private", "public", "protected",
			"abstract", "ref", "readonly", "derived", "variation", "individual", "in", "out", "inout", "constant",
			"end", "standard", "library", "nonunique", "ordered", "var", "composite", "portion", "snapshot",
			"timeslice", "return"));

	private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList("package", "part", "port", "attribute",
			"connection", "interface", "enum", "item", "action", "state", "constraint", "requirement", "occurrence",
			"metadata", "doc", "comment", "import", "alias", "bind", "flow", "allocation", "allocate", "calc", "case",
			"view", "viewpoint", "rendering", "concern", "use", "analysis", "verification", "satisfy", "assert",
			"exhibit", "perform", "message", "succession", "transition", "accept", "send", "assume", "require",
			"subject", "objective", "actor", "stakeholder", "frame", "expose", "render", "filter", "dependency",
			"namespace", "language", "rep", "event", "individual", "snapshot", "timeslice", "connector", "verify"));

	/** The keywords that <code>case</code> may follow. */
	private static final Set<String> CASES = new HashSet<>(Arrays.asList("use", "analysis", "verification"));

	private final String source;
	private final List<SysmlToken> tokens;
	private int pos;

	private SysmlParser(String source, List<SysmlToken> tokens) {
		this.source = source;
		this.tokens = tokens;
	}

	/**
	 * Parses one file.
	 *
	 * @param source the file name, for error messages
	 * @param text   the file content
	 * @return a root node (keyword <code>root</code>) owning the top-level
	 *         members
	 */
	public static SysmlNode parse(String source, String text) throws SysmlException {
		final List<SysmlToken> tokens = new SysmlLexer(source, text).tokenize();
		final SysmlParser parser = new SysmlParser(source, tokens);
		final SysmlNode root = new SysmlNode("root", false, false, null, false, null,
				Collections.<String>emptyList(), null, Collections.<String>emptyList(), source, 1);
		parser.parseMembers(root, false);
		return root;
	}

	private SysmlToken peek() {
		return pos < tokens.size() ? tokens.get(pos) : null;
	}

	private SysmlException error(String message, int line) {
		return new SysmlException(source + ":" + line + ": " + message);
	}

	private void parseMembers(SysmlNode owner, boolean inBody) throws SysmlException {
		while (true) {
			final SysmlToken t = peek();
			if (t == null) {
				if (inBody)
					throw error("missing '}' to close " + owner.getName(), owner.getLine());
				return;
			}
			if (t.isSymbol("}")) {
				if (inBody == false)
					throw error("unexpected '}'", t.getLine());
				pos++;
				return;
			}
			if (t.isSymbol(";") || t.getKind() == SysmlToken.Kind.BLOCK_COMMENT) {
				// Stray separator, or a standalone comment element
				pos++;
				continue;
			}
			parseMember(owner);
		}
	}

	private void parseMember(SysmlNode owner) throws SysmlException {
		final int line = peek().getLine();
		final List<SysmlToken> header = new ArrayList<>();
		int depth = 0;
		boolean hasBody = false;
		while (true) {
			final SysmlToken t = peek();
			if (t == null) {
				if (header.isEmpty() == false && isDoc(header))
					break;
				throw error("unexpected end of file (missing ';'?)", line);
			}
			if (t.getKind() == SysmlToken.Kind.BLOCK_COMMENT) {
				pos++;
				if (depth == 0 && isDoc(header)) {
					header.add(t);
					break;
				}
				continue;
			}
			if (t.isSymbol("(") || t.isSymbol("["))
				depth++;
			else if (t.isSymbol(")") || t.isSymbol("]"))
				depth--;
			if (depth <= 0 && t.isSymbol(";")) {
				pos++;
				break;
			}
			if (depth <= 0 && t.isSymbol("{")) {
				pos++;
				hasBody = true;
				break;
			}
			if (depth <= 0 && t.isSymbol("}"))
				// A missing ';' before the closing brace: let the caller close the body
				break;
			header.add(t);
			pos++;
		}
		final SysmlNode node = interpret(header, line);
		owner.addChild(node);
		if (hasBody)
			parseMembers(node, true);
	}

	private static boolean isDoc(List<SysmlToken> header) {
		for (SysmlToken t : header) {
			if (t.getKind() == SysmlToken.Kind.NAME && MODIFIERS.contains(t.getText()))
				continue;
			return t.isName("doc") || t.isName("comment");
		}
		return false;
	}

	private SysmlNode interpret(List<SysmlToken> h, int line) {
		int i = 0;
		boolean library = false;
		String lastModifier = null;
		final List<String> modifiers = new ArrayList<>();
		final List<String> annotations = new ArrayList<>();
		while (i < h.size()) {
			final SysmlToken t = h.get(i);
			if (t.isSymbol("#") || t.isSymbol("@")) {
				// Metadata prefix: #Name or @Name (a metadata usage: kept as the modifier "@")
				if (t.isSymbol("@") && i == 0)
					modifiers.add("@");
				if (t.isSymbol("#") && i + 1 < h.size() && h.get(i + 1).getKind() == SysmlToken.Kind.NAME)
					annotations.add(h.get(i + 1).getText());
				i += 2;
				continue;
			}
			if (t.getKind() == SysmlToken.Kind.NAME && MODIFIERS.contains(t.getText()) && i + 1 < h.size()
					&& isModifierFollower(h.get(i + 1))) {
				if (t.isName("library"))
					library = true;
				lastModifier = t.getText();
				modifiers.add(lastModifier);
				i++;
				continue;
			}
			break;
		}

		String keyword = null;
		boolean definition = false;
		if (i < h.size() && h.get(i).getKind() == SysmlToken.Kind.NAME && KEYWORDS.contains(h.get(i).getText())) {
			keyword = h.get(i).getText();
			i++;
			if (CASES.contains(keyword) && i < h.size() && h.get(i).isName("case")) {
				// use case, analysis case, verification case
				keyword = keyword + " case";
				i++;
			}
			if (i < h.size() && h.get(i).isName("def")) {
				definition = true;
				i++;
			}
		} else if (i < h.size() && h.get(i).isName("connect")) {
			keyword = "connection";
		} else if ("ref".equals(lastModifier) || "end".equals(lastModifier)) {
			keyword = lastModifier;
		}
		if ("assert".equals(keyword) && i < h.size() && (h.get(i).isName("satisfy") || h.get(i).isName("not"))) {
			// assert satisfy R by X, assert not satisfy R by X
			while (i < h.size() && h.get(i).isName("satisfy") == false)
				i++;
			keyword = "satisfy";
			i++;
		}

		String shortName = null;
		if (i + 2 < h.size() && h.get(i).isSymbol("<") && h.get(i + 1).getKind() == SysmlToken.Kind.NAME
				&& h.get(i + 2).isSymbol(">")) {
			shortName = h.get(i + 1).getText();
			i += 3;
		}
		if ("satisfy".equals(keyword) || "verify".equals(keyword) || "dependency".equals(keyword)
				|| ("allocate".equals(keyword) && definition == false))
			return relationship(keyword, h, i, shortName, annotations, line);

		if ("doc".equals(keyword) || "comment".equals(keyword)) {
			final SysmlToken last = h.get(h.size() - 1);
			final String body = last.getKind() == SysmlToken.Kind.BLOCK_COMMENT ? cleanComment(last.getText()) : "";
			return new SysmlNode(keyword, false, false, null, false, null, Collections.<String>emptyList(),
					new SysmlValue(body, body), Collections.<String>emptyList(), source, line);
		}
		if ("import".equals(keyword) || "alias".equals(keyword)) {
			final String raw = raw(h, i, h.size());
			return new SysmlNode(keyword, false, false, null, false, null, Collections.<String>emptyList(),
					new SysmlValue(raw, raw), Collections.<String>emptyList(), source, line);
		}

		String name = null;
		boolean redefines = false;
		String type = null;
		final List<String> types = new ArrayList<>();
		final List<String> specializes = new ArrayList<>();
		SysmlValue value = null;
		final List<String> ends = new ArrayList<>();
		String multiplicity = null;

		while (i < h.size()) {
			final SysmlToken t = h.get(i);
			if (t.isName("redefines") || t.isSymbol(":>>")) {
				redefines = true;
				i++;
				final int[] next = new int[1];
				final String n = readQualified(h, i, next);
				if (n != null) {
					if (name == null)
						name = lastSegment(n);
					i = next[0];
				}
			} else if (t.isName("specializes") || t.isName("subsets") || t.isSymbol(":>") || t.isSymbol("::>")) {
				i++;
				while (i < h.size()) {
					final int[] next = new int[1];
					final String n = readQualified(h, i, next);
					if (n == null)
						break;
					specializes.add(n);
					i = next[0];
					if (i < h.size() && h.get(i).isSymbol(","))
						i++;
					else
						break;
				}
			} else if (t.isSymbol(":") || t.isName("defined") || t.isName("typed")) {
				i++;
				if (i < h.size() && h.get(i).isName("by"))
					i++;
				if (i < h.size() && h.get(i).isSymbol("~"))
					i++;
				final int[] next = new int[1];
				String n = readQualified(h, i, next);
				if (n != null) {
					type = n;
					i = next[0];
				}
				while (n != null && i < h.size() && h.get(i).isSymbol(",")) {
					// subject dut : A, B;
					types.add(n);
					n = readQualified(h, i + 1, next);
					if (n != null)
						i = next[0];
				}
				if (n != null)
					types.add(n);
			} else if (t.isSymbol("[")) {
				final int close = skipBracket(h, i, "[", "]");
				if (multiplicity == null && close - 1 > i + 1 && h.get(close - 1).isSymbol("]"))
					multiplicity = raw(h, i + 1, close - 1);
				i = close;
			} else if (t.isSymbol("=") || t.isSymbol(":=") || t.isName("default")) {
				i++;
				if (t.isName("default") && i < h.size() && (h.get(i).isSymbol("=") || h.get(i).isSymbol(":=")))
					i++;
				int end = i;
				while (end < h.size() && h.get(end).isName("connect") == false)
					end++;
				value = toValue(h, i, end);
				i = end;
			} else if (t.isName("connect")) {
				i++;
				if (i < h.size() && h.get(i).isSymbol("(")) {
					i++;
					while (i < h.size() && h.get(i).isSymbol(")") == false) {
						final int[] next = new int[1];
						final String n = readQualified(h, i, next);
						if (n == null) {
							i++;
							continue;
						}
						ends.add(n);
						i = next[0];
					}
					i++;
				} else {
					final int[] next = new int[1];
					final String a = readQualified(h, i, next);
					if (a != null) {
						ends.add(a);
						i = next[0];
					}
					if (i < h.size() && h.get(i).isName("to")) {
						i++;
						final String b = readQualified(h, i, next);
						if (b != null) {
							ends.add(b);
							i = next[0];
						}
					}
				}
			} else if (t.getKind() == SysmlToken.Kind.NAME && name == null && type == null && redefines == false) {
				final int[] next = new int[1];
				final String n = readQualified(h, i, next);
				name = n;
				i = next[0];
			} else {
				i++;
			}
		}
		final SysmlNode node = new SysmlNode(keyword, definition, library, name, redefines, type, specializes, value,
				ends, source, line);
		node.setMultiplicity(multiplicity);
		for (String modifier : modifiers)
			node.addModifier(modifier);
		node.setShortName(shortName);
		node.setAnnotations(annotations);
		if (types.size() > 1)
			node.setTypes(types);
		return node;
	}

	/**
	 * <code>satisfy [requirement] R [by X]</code>, <code>verify [requirement]
	 * R</code>, <code>dependency [name from] A, B to C, D</code> and
	 * <code>allocate A to B</code>: the ends become the sources and targets of
	 * the node.
	 */
	private SysmlNode relationship(String keyword, List<SysmlToken> h, int start, String shortName,
			List<String> annotations, int line) {
		final List<String> sources = new ArrayList<>();
		final List<String> targets = new ArrayList<>();
		String name = null;
		int i = start;
		final int[] next = new int[1];
		if ("satisfy".equals(keyword) || "verify".equals(keyword)) {
			if (i < h.size() && h.get(i).isName("requirement"))
				i++;
			String requirement = readQualified(h, i, next);
			if (requirement != null) {
				i = next[0];
				if (i < h.size() && h.get(i).isSymbol(":")) {
					// satisfy requirement s : R by X
					final String typed = readQualified(h, i + 1, next);
					if (typed != null) {
						name = requirement;
						requirement = typed;
						i = next[0];
					}
				}
				targets.add(requirement);
			}
			while (i < h.size() && h.get(i).isName("by") == false)
				i++;
			if (i < h.size()) {
				final String by = readQualified(h, i + 1, next);
				if (by != null)
					sources.add(by);
			}
		} else {
			final List<String> first = readList(h, i, next);
			i = next[0];
			if (i < h.size() && h.get(i).isName("from")) {
				if (first.isEmpty() == false)
					name = first.get(0);
				sources.addAll(readList(h, i + 1, next));
				i = next[0];
			} else {
				sources.addAll(first);
			}
			if (i < h.size() && h.get(i).isName("to"))
				targets.addAll(readList(h, i + 1, next));
		}
		final SysmlNode node = new SysmlNode(keyword, false, false, name, false, null,
				Collections.<String>emptyList(), null, Collections.<String>emptyList(), source, line);
		node.setShortName(shortName);
		node.setAnnotations(annotations);
		node.setRelationEnds(sources, targets);
		return node;
	}

	/** Reads <code>A, B::C, d.e</code>; stops at the first token that is not part of it. */
	private static List<String> readList(List<SysmlToken> h, int i, int[] next) {
		final List<String> result = new ArrayList<>();
		while (true) {
			final String n = readQualified(h, i, next);
			if (n == null || n.equals("from") || n.equals("to"))
				break;
			result.add(n);
			i = next[0];
			if (i < h.size() && h.get(i).isSymbol(","))
				i++;
			else
				break;
		}
		next[0] = i;
		return result;
	}

	private static boolean isModifierFollower(SysmlToken next) {
		// "ref part x" is a modifier; "ref : T" or "end ;" are not
		return next.getKind() == SysmlToken.Kind.NAME || next.isSymbol("#") || next.isSymbol(":>>");
	}

	private static int skipBracket(List<SysmlToken> h, int i, String open, String close) {
		int depth = 0;
		while (i < h.size()) {
			final SysmlToken t = h.get(i);
			if (t.isSymbol(open))
				depth++;
			else if (t.isSymbol(close) && --depth == 0)
				return i + 1;
			i++;
		}
		return i;
	}

	/**
	 * Reads <code>A::B::C</code> or a feature chain <code>A.B.C</code>.
	 */
	private static String readQualified(List<SysmlToken> h, int i, int[] next) {
		if (i >= h.size() || h.get(i).getKind() != SysmlToken.Kind.NAME)
			return null;
		final StringBuilder sb = new StringBuilder(h.get(i).getText());
		i++;
		while (i + 1 < h.size() && (h.get(i).isSymbol("::") || h.get(i).isSymbol("."))
				&& (h.get(i + 1).getKind() == SysmlToken.Kind.NAME || h.get(i + 1).isSymbol("*"))) {
			sb.append(h.get(i).getText()).append(h.get(i + 1).getText());
			i += 2;
		}
		next[0] = i;
		return sb.toString();
	}

	static String lastSegment(String qualified) {
		int start = 0;
		final int colons = qualified.lastIndexOf("::");
		if (colons >= 0)
			start = colons + 2;
		final int dot = qualified.lastIndexOf('.');
		if (dot + 1 > start)
			start = dot + 1;
		return qualified.substring(start);
	}

	private static String raw(List<SysmlToken> h, int from, int to) {
		final StringBuilder sb = new StringBuilder();
		for (int i = from; i < to; i++) {
			final SysmlToken t = h.get(i);
			if (sb.length() > 0 && t.getKind() != SysmlToken.Kind.SYMBOL && needsSpace(h.get(i - 1)))
				sb.append(' ');
			if (t.getKind() == SysmlToken.Kind.STRING)
				sb.append('"').append(t.getText()).append('"');
			else
				sb.append(t.getText());
		}
		return sb.toString();
	}

	private static boolean needsSpace(SysmlToken previous) {
		return previous.getKind() != SysmlToken.Kind.SYMBOL || previous.isSymbol("]") || previous.isSymbol(",")
				|| previous.isSymbol("=");
	}

	private static SysmlValue toValue(List<SysmlToken> h, int from, int to) {
		final String raw = raw(h, from, to);
		final int n = to - from;
		if (n == 1 && h.get(from).getKind() == SysmlToken.Kind.STRING)
			return new SysmlValue(h.get(from).getText(), raw);
		if (n >= 1 && h.get(from).getKind() == SysmlToken.Kind.NUMBER) {
			// 600 [mm], 2 [A], 24.5
			if (n >= 4 && h.get(from + 1).isSymbol("[") && h.get(to - 1).isSymbol("]"))
				return new SysmlValue(h.get(from).getText() + " " + raw(h, from + 2, to - 1), raw);
			return new SysmlValue(raw, raw);
		}
		final int[] next = new int[1];
		final String q = readQualified(h, from, next);
		if (q != null && next[0] == to)
			return new SysmlValue(lastSegment(q), raw);
		return new SysmlValue(raw, raw);
	}

	/**
	 * Removes the leading <code>*</code> of each line of a block comment and the
	 * surrounding blank lines.
	 */
	static String cleanComment(String body) {
		final StringBuilder sb = new StringBuilder();
		for (String l : body.split("\n", -1)) {
			String s = l.trim();
			if (s.startsWith("*"))
				s = s.substring(1);
			if (s.startsWith(" "))
				s = s.substring(1);
			sb.append(s).append('\n');
		}
		return sb.toString().trim();
	}

}
