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
import java.util.List;

/**
 * Splits SysML v2 text into tokens. Line comments and note comments
 * (<code>//* ... *&#47;</code>) are dropped; block comments are kept because a
 * <code>doc</code> element carries its text in one.
 */
public final class SysmlLexer {

	private static final String[] SYMBOLS = { ":>>", "::>", ":>", "::", ":=", "==", "!=", "<=", ">=", "->", "..",
			"{", "}", ";", ":", "=", "[", "]", "(", ")", ",", ".", "~", "#", "@", "*", "+", "-", "/", "<", ">", "?",
			"!", "&", "|", "%", "^", "$" };

	private final String source;
	private final String text;
	private int pos;
	private int line = 1;

	public SysmlLexer(String source, String text) {
		this.source = source;
		this.text = text;
	}

	public List<SysmlToken> tokenize() throws SysmlException {
		final List<SysmlToken> result = new ArrayList<>();
		while (true) {
			skipBlanksAndLineComments();
			if (pos >= text.length())
				return result;

			final char c = text.charAt(pos);
			final int startLine = line;
			if (c == '/' && peek(1) == '*') {
				pos += 2;
				final int end = text.indexOf("*/", pos);
				if (end == -1)
					throw error("unterminated comment", startLine);
				final String body = text.substring(pos, end);
				advanceTo(end + 2);
				result.add(new SysmlToken(SysmlToken.Kind.BLOCK_COMMENT, body, startLine));
			} else if (c == '"') {
				result.add(new SysmlToken(SysmlToken.Kind.STRING, readQuoted('"'), startLine));
			} else if (c == '\'') {
				result.add(new SysmlToken(SysmlToken.Kind.NAME, readQuoted('\''), startLine));
			} else if (Character.isLetter(c) || c == '_') {
				final int start = pos;
				while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_'))
					pos++;
				result.add(new SysmlToken(SysmlToken.Kind.NAME, text.substring(start, pos), startLine));
			} else if (Character.isDigit(c)) {
				result.add(new SysmlToken(SysmlToken.Kind.NUMBER, readNumber(), startLine));
			} else {
				final String symbol = readSymbol();
				if (symbol == null)
					throw error("unexpected character '" + c + "'", startLine);
				result.add(new SysmlToken(SysmlToken.Kind.SYMBOL, symbol, startLine));
			}
		}
	}

	private char peek(int offset) {
		final int i = pos + offset;
		return i < text.length() ? text.charAt(i) : '\0';
	}

	private void advanceTo(int newPos) {
		for (int i = pos; i < newPos; i++)
			if (text.charAt(i) == '\n')
				line++;
		pos = newPos;
	}

	private void skipBlanksAndLineComments() throws SysmlException {
		while (pos < text.length()) {
			final char c = text.charAt(pos);
			if (c == '\n') {
				line++;
				pos++;
			} else if (Character.isWhitespace(c)) {
				pos++;
			} else if (c == '/' && peek(1) == '/' && peek(2) == '*') {
				// A note comment: //* ... */
				final int end = text.indexOf("*/", pos + 3);
				if (end == -1)
					throw error("unterminated comment", line);
				advanceTo(end + 2);
			} else if (c == '/' && peek(1) == '/') {
				while (pos < text.length() && text.charAt(pos) != '\n')
					pos++;
			} else {
				return;
			}
		}
	}

	private String readQuoted(char quote) throws SysmlException {
		final int startLine = line;
		final StringBuilder sb = new StringBuilder();
		pos++;
		while (pos < text.length()) {
			final char c = text.charAt(pos);
			if (c == '\\' && pos + 1 < text.length()) {
				final char next = text.charAt(pos + 1);
				sb.append(next == 'n' ? '\n' : next == 't' ? '\t' : next);
				pos += 2;
			} else if (c == quote) {
				pos++;
				return sb.toString();
			} else {
				if (c == '\n')
					line++;
				sb.append(c);
				pos++;
			}
		}
		throw error("unterminated " + (quote == '"' ? "string" : "name"), startLine);
	}

	private String readNumber() {
		final int start = pos;
		while (pos < text.length() && Character.isDigit(text.charAt(pos)))
			pos++;
		if (peek(0) == '.' && Character.isDigit(peek(1))) {
			pos++;
			while (pos < text.length() && Character.isDigit(text.charAt(pos)))
				pos++;
		}
		if ((peek(0) == 'e' || peek(0) == 'E')
				&& (Character.isDigit(peek(1)) || ((peek(1) == '-' || peek(1) == '+') && Character.isDigit(peek(2))))) {
			pos += 2;
			while (pos < text.length() && Character.isDigit(text.charAt(pos)))
				pos++;
		}
		return text.substring(start, pos);
	}

	private String readSymbol() {
		for (String s : SYMBOLS)
			if (text.startsWith(s, pos)) {
				pos += s.length();
				return s;
			}
		return null;
	}

	private SysmlException error(String message, int errorLine) {
		return new SysmlException(source + ":" + errorLine + ": " + message);
	}

}
