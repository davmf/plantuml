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
import java.util.List;

/**
 * Finds the element a feature chain such as <code>top.idle.outgoing</code> or
 * <code>interaction.lifelines.user.incoming</code> designates.
 * <p>
 * Each element is registered with its path, the names of the usages that own
 * it from the diagram root down. A reference matches an element when the
 * reference, minus an optional trailing port name, is a suffix of the
 * element's path; when several elements match, the one whose path shares the
 * longest prefix with the place the reference is written wins. This is not
 * SysML name resolution, but it finds what the profiles' models write,
 * whether chains are absolute (<code>sm.top.a</code>) or relative to the
 * enclosing region (<code>a.outgoing</code>).
 *
 * @param <T> the elements
 */
public final class SysmlScope<T> {

	/** An element found by {@link SysmlScope#resolve}, and the port named after it. */
	public static final class Found<T> {
		private final T element;
		private final String port;

		Found(T element, String port) {
			this.element = element;
			this.port = port;
		}

		public T getElement() {
			return element;
		}

		/** The segment after the element (<code>outgoing</code>), or <code>null</code>. */
		public String getPort() {
			return port;
		}
	}

	private final List<List<String>> paths = new ArrayList<>();
	private final List<T> elements = new ArrayList<>();

	public void register(List<String> path, T element) {
		paths.add(new ArrayList<>(path));
		elements.add(element);
	}

	public List<T> getElements() {
		return elements;
	}

	/**
	 * @param reference the chain as written
	 * @param context   the path of the usage the reference is written in
	 * @return the element, or <code>null</code> when nothing (or nothing
	 *         unambiguous) matches
	 */
	public Found<T> resolve(String reference, List<String> context) {
		if (reference == null)
			return null;
		final List<String> segments = new ArrayList<>(Arrays.asList(reference.trim().split("\\s*(\\.|::)\\s*")));
		Found<T> result = find(segments, context, null);
		if (result == null && segments.size() > 1) {
			final String port = segments.remove(segments.size() - 1);
			result = find(segments, context, port);
		}
		return result;
	}

	private Found<T> find(List<String> segments, List<String> context, String port) {
		int best = -1;
		int bestScore = Integer.MIN_VALUE;
		boolean ambiguous = false;
		for (int i = 0; i < paths.size(); i++) {
			final List<String> path = paths.get(i);
			if (endsWith(path, segments) == false)
				continue;
			final int score = commonPrefix(path, context) * 1000 - path.size();
			if (score > bestScore) {
				best = i;
				bestScore = score;
				ambiguous = false;
			} else if (score == bestScore) {
				ambiguous = true;
			}
		}
		if (best == -1 || ambiguous)
			return null;
		return new Found<T>(elements.get(best), port);
	}

	private static boolean endsWith(List<String> path, List<String> suffix) {
		if (suffix.size() > path.size())
			return false;
		final int offset = path.size() - suffix.size();
		for (int i = 0; i < suffix.size(); i++)
			if (suffix.get(i).equals(path.get(offset + i)) == false)
				return false;
		return true;
	}

	private static int commonPrefix(List<String> a, List<String> b) {
		if (b == null)
			return 0;
		int n = 0;
		while (n < a.size() && n < b.size() && a.get(n).equals(b.get(n)))
			n++;
		return n;
	}

}
