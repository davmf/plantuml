/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2025, Arnaud Roques
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
 */
package net.sourceforge.plantuml.teavm.js;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonLayouter;
import net.sourceforge.plantuml.teavm.js.browser.PendingResources;
import net.sourceforge.plantuml.teavm.js.browser.ScriptLoaderJs;

/**
 * Lays out ELK graphs ({@code !pragma layout elk}) in the browser with
 * <a href="https://github.com/kieler/elkjs">elkjs</a>, the official JavaScript
 * build of the Eclipse Layout Kernel, synchronously.
 * <p>
 * elkjs only offers a Promise based API, but its in-page worker (the one
 * {@code elk.bundled.js} uses) dispatches a layout request synchronously: only
 * the messages around it are delayed. So the request is handed to that
 * dispatcher directly, with a listener that catches the answer, and the layout
 * is there when the call returns. This relies on the internals of elkjs
 * 0.12; an elkjs that does not have them is reported as such.
 * <p>
 * {@code elk.bundled.js} (1.6 MB) is loaded on demand, the first time a diagram
 * asks for ELK, like {@code jlatexmath.js}: that rendering attempt is discarded
 * and the diagram is rendered again once the script is there (see
 * {@link PendingResources}). A host that bundles elkjs itself sets
 * {@code globalThis.PLANTUML_ELK = ELK} (the constructor exported by
 * {@code elkjs/lib/elk.bundled.js}), which takes precedence over the global
 * {@code ELK} the script tag defines.
 */
public final class ElkEngineJs implements ElkJsonLayouter {

	// ::remove file when JAVA8

	/** The script, deployed next to plantuml.js like viz-global.js. */
	public static final String SCRIPT = "elk.bundled.js";

	private static final ElkEngineJs INSTANCE = new ElkEngineJs();

	private static final String FIND_ELK_STMT = "var E = globalThis.PLANTUML_ELK || (typeof ELK !== 'undefined' ? ELK : null);";

	public enum Status {
		/** elkjs is there: lay out with it. */
		READY,
		/** elkjs is being loaded: this attempt is discarded and rendered again. */
		LOADING,
		/** elkjs cannot be had: lay out with the default engine instead. */
		UNAVAILABLE
	}

	private ElkEngineJs() {
	}

	/**
	 * Whether a diagram that asks for ELK can be laid out with elkjs now. When it
	 * can, the elkjs layouter is registered (see {@link ElkJsonGraph}). When it
	 * cannot, the reason is written to the console once per rendering.
	 */
	public static Status status() {
		if (isLoaded() == false) {
			try {
				if (ScriptLoaderJs.require(SCRIPT) == false || isLoaded() == false) {
					if (PendingResources.isAttempting())
						return Status.LOADING;
					PendingResources.warn("PlantUML: elkjs (" + SCRIPT
							+ ") is not loaded, '!pragma layout elk' uses the default layout engine instead");
					return Status.UNAVAILABLE;
				}
			} catch (RuntimeException e) {
				PendingResources.warn("PlantUML: " + e.getMessage()
						+ ", '!pragma layout elk' uses the default layout engine instead");
				return Status.UNAVAILABLE;
			}
		}
		ElkJsonGraph.setLayouter(INSTANCE);
		return Status.READY;
	}

	@Override
	public String layout(String graphJson) {
		final JSObject result = layoutJs(graphJson);
		final String error = getProperty(result, "error");
		if (error != null)
			throw new RuntimeException("elkjs: " + error);
		return getProperty(result, "graph");
	}

	@JSBody(script = FIND_ELK_STMT + "return typeof E === 'function';")
	private static native boolean isLoaded();

	/**
	 * Creates the ELK instance once, registers the layered algorithm with its
	 * in-page worker, and lays the graph out. Nothing thrown by JavaScript crosses
	 * into Java: a failure is returned as {@code error}.
	 */
	@JSBody(params = "graphJson", script = FIND_ELK_STMT
			+ "var g = globalThis;"
			+ "var message = function(e) { return String((e && e.message) || e); };"
			+ "try {"
			+ "  var st = g.__plantumlElk;"
			+ "  if (!st || st.ctor !== E) {"
			+ "    var elk = new E();"
			+ "    var fw = elk.worker && elk.worker.worker;"
			+ "    if (!fw || !fw.dispatcher || typeof fw.dispatcher.saveDispatch !== 'function')"
			+ "      return { error: 'this elkjs build has no in-page worker to call synchronously (use elk.bundled.js 0.12)' };"
			+ "    st = g.__plantumlElk = { ctor: E, worker: fw, nextId: 0, registered: false };"
			+ "  }"
			+ "  var call = function(msg) {"
			+ "    var answer = null, saved = st.worker.onmessage;"
			+ "    msg.id = 'plantuml-' + (st.nextId++);"
			+ "    st.worker.onmessage = function(m) { answer = m.data; };"
			+ "    try { st.worker.dispatcher.saveDispatch({ data: msg }); }"
			+ "    finally { st.worker.onmessage = saved; }"
			+ "    if (answer === null) throw new Error('elkjs did not answer synchronously');"
			+ "    if (answer.error) throw answer.error;"
			+ "    return answer.data;"
			+ "  };"
			+ "  if (!st.registered) { call({ cmd: 'register', algorithms: ['layered'] }); st.registered = true; }"
			+ "  return { graph: JSON.stringify(call({ cmd: 'layout', graph: JSON.parse(graphJson), layoutOptions: {} })) };"
			+ "} catch (e) {"
			+ "  return { error: message(e) };"
			+ "}")
	private static native JSObject layoutJs(String graphJson);

	@JSBody(params = { "obj", "key" }, script = "var v = obj[key]; return (typeof v === 'string') ? v : null;")
	private static native String getProperty(JSObject obj, String key);

}
