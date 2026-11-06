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

/**
 * One token of the SysML v2 textual notation.
 */
public final class SysmlToken {

	public enum Kind {
		/** A name: <code>J1</code>, or an unrestricted name <code>'my part'</code> */
		NAME,
		/** A string literal, without its quotes */
		STRING,
		NUMBER,
		/** Punctuation: <code>{ } ; : :&gt; :&gt;&gt; :: = := [ ] ( ) , . ~ # @ *</code> */
		SYMBOL,
		/** The body of a <code>/* ... *&#47;</code> comment, kept for <code>doc</code> */
		BLOCK_COMMENT
	}

	private final Kind kind;
	private final String text;
	private final int line;

	public SysmlToken(Kind kind, String text, int line) {
		this.kind = kind;
		this.text = text;
		this.line = line;
	}

	public Kind getKind() {
		return kind;
	}

	public String getText() {
		return text;
	}

	public int getLine() {
		return line;
	}

	public boolean isSymbol(String s) {
		return kind == Kind.SYMBOL && text.equals(s);
	}

	public boolean isName(String s) {
		return kind == Kind.NAME && text.equals(s);
	}

	@Override
	public String toString() {
		return kind + ":" + text;
	}

}
