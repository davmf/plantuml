// The SysML pane's editor: CodeMirror 6 (vendor/codemirror-6.min.js) with a
// small SysML v2 / KerML highlighter, behind the few calls sysml.js needs.
//
// Each file keeps its own EditorState, so undo history, selections and scroll
// survive switching tabs, and an undo never reaches into another file.
//
// Multi-cursor and friends come from CodeMirror's basicSetup:
//   Alt+click                     add a cursor
//   Alt+drag                      rectangular (column) selection
//   Ctrl/Cmd+Alt+Up/Down          add a cursor on the line above/below
//   Ctrl/Cmd+D                    select the next occurrence
//   Ctrl/Cmd+Shift+L              select every occurrence
//   Escape                        back to a single cursor
//   Ctrl/Cmd+F, Ctrl/Cmd+H        search, replace
//   Alt+Up/Down, Shift+Alt+Up/Down  move, copy the line
//   Ctrl/Cmd+/                    toggle // comment
//   Tab, Shift+Tab                indent, dedent (Escape then Tab leaves the editor)
import {
	basicSetup, EditorView, EditorState, keymap, placeholder, indentWithTab,
	StreamLanguage, syntaxHighlighting, indentUnit, classHighlighter
} from "./vendor/codemirror-6.min.js";

const KEYWORDS = new Set((
	"about abstract accept action actor after alias all allocate allocation analysis and as assert assign assoc "
	+ "assume at attribute bind binding by calc case chains class classifier comment composite concern conjugate "
	+ "conjugates conjugation connect connection connector const constant constraint crosses datatype decide def "
	+ "default defined dependency derived differences disjoining disjoint do doc else end entry enum event exhibit "
	+ "exit expose expr false feature featured featuring filter first flow for fork frame from function hastype if "
	+ "implies import in include individual inout interaction interface intersects inv inverse inverting istype "
	+ "item join language library locale loop merge message meta metaclass metadata multiplicity namespace new "
	+ "nonunique not null objective occurrence of or ordered out package parallel part perform port portion "
	+ "predicate private protected public redefines redefinition ref references render rendering rep require "
	+ "requirement return satisfy send snapshot specialization specializes stakeholder standard state step struct "
	+ "subclassifier subject subset subsets subtype succession terminate then this timeslice to transition true "
	+ "type typed typing unions until use var variant variation verification verify via view viewpoint when while "
	+ "xor"
).split(" "));

const ATOMS = new Set(["true", "false", "null"]);

// Brace depth drives the indentation of a new line; a name following "def"
// is a definition, a name following ":" or ":>" a type.
const sysmlLanguage = StreamLanguage.define({
	name: "sysml",
	startState: () => ({depth: 0, inComment: false, expect: null}),
	copyState: s => ({depth: s.depth, inComment: s.inComment, expect: s.expect}),
	token(stream, state) {
		if (state.inComment) {
			return blockComment(stream, state);
		}
		if (stream.eatSpace()) {
			return null;
		}
		if (stream.match("//")) {
			stream.skipToEnd();
			return "comment";
		}
		if (stream.match("/*")) {
			state.inComment = true;
			return blockComment(stream, state);
		}
		if (stream.match(/^"(?:[^"\\]|\\.)*"?/)) {
			return "string";
		}
		// Unrestricted name: 'any text'
		if (stream.match(/^'(?:[^'\\]|\\.)*'?/)) {
			return takeName(state);
		}
		if (stream.match(/^(?:\d+\.?\d*|\.\d+)(?:[eE][+-]?\d+)?/)) {
			return "number";
		}
		if (stream.match(/^[#@][A-Za-z_][\w:]*/)) {
			return "meta";
		}
		if (stream.match(/^[A-Za-z_]\w*/)) {
			const word = stream.current();
			if (ATOMS.has(word)) {
				return "atom";
			}
			if (KEYWORDS.has(word)) {
				state.expect = word === "def" ? "def" : null;
				return "keyword";
			}
			return takeName(state);
		}
		if (stream.match(/^(?::>>|:>|::>|:=|::|=>|->|~)/)) {
			state.expect = stream.current() === "::" ? state.expect : "type";
			return "operator";
		}
		const ch = stream.next();
		if (ch === "{") {
			state.depth++;
			state.expect = null;
			return "brace";
		}
		if (ch === "}") {
			state.depth = Math.max(0, state.depth - 1);
			return "brace";
		}
		if (ch === ":") {
			state.expect = "type";
			return "operator";
		}
		if (ch === ";" || ch === ",") {
			state.expect = null;
			return "punctuation";
		}
		return /[=<>!+\-*\/%^&|]/.test(ch) ? "operator" : null;
	},
	indent(state, textAfter, cx) {
		if (state.inComment) {
			return null;
		}
		const depth = Math.max(0, state.depth - (/^\s*\}/.test(textAfter) ? 1 : 0));
		return depth * cx.unit;
	},
	languageData: {
		commentTokens: {line: "//", block: {open: "/*", close: "*/"}},
		indentOnInput: /^\s*\}$/
	}
});

// The rest of a /* */ comment on this line
function blockComment(stream, state) {
	if (stream.skipTo("*/")) {
		stream.match("*/");
		state.inComment = false;
	} else {
		stream.skipToEnd();
	}
	return "comment";
}

// A qualified name (A::B::C) keeps its role across the "::"
function takeName(state) {
	if (state.expect === "def") {
		state.expect = null;
		return "def";
	}
	if (state.expect === "type") {
		return "type";
	}
	return "variable";
}

// The pane's look: main.css variables, so the dark toggle needs no theme swap.
const paneTheme = EditorView.theme({
	"&": {
		flexGrow: "1",
		minHeight: "0",
		height: "100%",
		fontSize: "0.85rem",
		color: "var(--fg)",
		backgroundColor: "transparent"
	},
	"&.cm-focused": {outline: "none"},
	".cm-scroller": {
		fontFamily: "ui-monospace, SFMono-Regular, Menlo, Consolas, monospace",
		lineHeight: "1.45"
	},
	".cm-content": {caretColor: "var(--fg)", padding: "0.75rem 0 1.25rem"},
	".cm-cursor, .cm-dropCursor": {borderLeftColor: "var(--fg)"},
	".cm-gutters": {
		backgroundColor: "transparent",
		color: "var(--muted)",
		border: "none"
	},
	".cm-activeLine": {backgroundColor: "color-mix(in srgb, var(--accent) 12%, transparent)"},
	".cm-activeLineGutter": {backgroundColor: "transparent", color: "var(--fg)"},
	"&.cm-focused > .cm-scroller > .cm-selectionLayer .cm-selectionBackground, .cm-selectionBackground": {
		backgroundColor: "color-mix(in srgb, var(--accent) 45%, transparent)"
	},
	".cm-selectionMatch": {backgroundColor: "color-mix(in srgb, var(--accent) 25%, transparent)"},
	".cm-panels": {backgroundColor: "var(--surface)", color: "var(--fg)"},
	".cm-tooltip": {backgroundColor: "var(--surface)", color: "var(--fg)"}
});

/**
 * Replaces the textarea with an editor; onChange(text) follows every edit made
 * in the editor (never the ones made through setFile).
 *
 * Returns {setFile(file), focus()}: setFile shows a {name, text} file, with the
 * state it had when last shown unless its text has changed since.
 */
export function createSysmlEditor(textarea, onChange) {
	const states = new WeakMap();
	let shown = null;

	const extensions = [
		basicSetup,
		keymap.of([indentWithTab]),
		indentUnit.of("  "),
		EditorState.tabSize.of(4),
		sysmlLanguage,
		syntaxHighlighting(classHighlighter),
		paneTheme,
		placeholder(textarea.placeholder || ""),
		EditorView.contentAttributes.of({autocapitalize: "none", autocorrect: "off", spellcheck: "false"}),
		// Dropped files open as tabs (the pane's drop handler), not as inserted text
		EditorView.domEventHandlers({
			drop: e => e.dataTransfer != null && e.dataTransfer.files.length > 0
		}),
		EditorView.updateListener.of(update => {
			if (update.docChanged) {
				onChange(update.state.doc.toString());
			}
		})
	];

	const host = document.createElement("div");
	host.id = textarea.id;
	host.className = textarea.className + " cm-host";
	textarea.replaceWith(host);
	const view = new EditorView({parent: host, state: EditorState.create({doc: "", extensions})});

	return {
		setFile(file) {
			if (shown != null && shown !== file) {
				states.set(shown, view.state);
			}
			if (shown === file && view.state.doc.toString() === file.text) {
				return;
			}
			const saved = states.get(file);
			view.setState(saved != null && saved.doc.toString() === file.text
				? saved
				: EditorState.create({doc: file.text, extensions}));
			shown = file;
		},
		focus() {
			view.focus();
		},
		view
	};
}
