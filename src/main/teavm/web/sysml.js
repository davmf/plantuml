// SysML import playground: SysML v2 files on the left, the PlantUML they
// import into in the middle, the rendered diagram on the right.
//
// Two ways to fill the middle pane:
//   - "generated": sysmlToPlantUml() runs the import of !include_sysml on the
//     files of the left pane and the middle pane shows the source it produces
//     (what -preproc shows for the directive), wrapped in @startuml/@enduml.
//   - "directive": the middle pane is a diagram of your own with an
//     !include_sysml line; PLANTUML_FILE_LOADER serves it the files of the left
//     pane by name, so the real directive runs in the browser engine.
import { render, sysmlToPlantUml } from "./plantuml.js";
import { createZoomController } from "./zoom.js";

const STORAGE_KEY = "plantuml-sysml-playground";
const DEBOUNCE_MS = 300;

const sysmlArea = document.getElementById("sysml");
const editor = document.getElementById("editor");
const tabsEl = document.getElementById("tabs");
const importStatus = document.getElementById("import-status");
const modeSelect = document.getElementById("mode");
const autoBox = document.getElementById("auto");

const optionInputs = {
	system: document.getElementById("opt-system"),
	view: document.getElementById("opt-view"),
	detail: document.getElementById("opt-detail"),
	cables: document.getElementById("opt-cables"),
	frame: document.getElementById("opt-frame"),
	style: document.getElementById("opt-style")
};
const OPTION_DEFAULTS = {system: "", view: "physical", detail: "connector", cables: "node", frame: false, style: true};

let files = [];          // [{name, text}]
let active = 0;
let dark = false;
let importTimer = null;
let renderTimer = null;
let saveTimer = null;
let toastTimer = null;

// The engine asks for the files of !include_sysml (and of any local !include)
// through this hook; answer with the left pane's files, matched by name.
window.PLANTUML_FILE_LOADER = function (path, from, ok) {
	const file = findFile(path);
	if (file == null) {
		return false;
	}
	ok(file.name, file.text);
};

function findFile(path) {
	const wanted = path.replace(/^\.\//, "");
	const base = wanted.replace(/^.*[\\/]/, "");
	return files.find(f => f.name === wanted) || files.find(f => f.name === base) || null;
}

// ---------------------------------------------------------------------------
// State: restore, save, reset
// ---------------------------------------------------------------------------

function sampleFile() {
	const el = document.getElementById("sample-sysml");
	return {name: el.dataset.name, text: el.textContent.replace(/^\n/, "")};
}

function restore() {
	let saved = null;
	try {
		saved = JSON.parse(localStorage.getItem(STORAGE_KEY));
	} catch (err) {
		console.warn("Ignoring the saved SysML session:", err);
	}
	if (saved == null || Array.isArray(saved.files) === false || saved.files.length === 0) {
		files = [sampleFile()];
		active = 0;
		setOptions(OPTION_DEFAULTS);
		modeSelect.value = "generated";
		autoBox.checked = true;
		return false;
	}
	files = saved.files.map(f => ({name: String(f.name), text: String(f.text)}));
	active = Math.min(Math.max(0, saved.active | 0), files.length - 1);
	setOptions(Object.assign({}, OPTION_DEFAULTS, saved.options || {}));
	modeSelect.value = saved.mode === "directive" ? "directive" : "generated";
	autoBox.checked = saved.auto !== false;
	editor.value = typeof saved.puml === "string" ? saved.puml : "";
	return true;
}

function scheduleSave() {
	clearTimeout(saveTimer);
	saveTimer = setTimeout(() => {
		try {
			localStorage.setItem(STORAGE_KEY, JSON.stringify({
				files, active, options: getOptions(), mode: modeSelect.value, auto: autoBox.checked, puml: editor.value
			}));
		} catch (err) {
			console.warn("Could not save the SysML session:", err);
		}
	}, DEBOUNCE_MS);
}

// ---------------------------------------------------------------------------
// Options
// ---------------------------------------------------------------------------

function getOptions() {
	return {
		system: optionInputs.system.value.trim(),
		view: optionInputs.view.value,
		detail: optionInputs.detail.value,
		cables: optionInputs.cables.value,
		frame: optionInputs.frame.checked,
		style: optionInputs.style.checked
	};
}

function setOptions(options) {
	optionInputs.system.value = options.system;
	optionInputs.view.value = options.view;
	optionInputs.detail.value = options.detail;
	optionInputs.cables.value = options.cables;
	optionInputs.frame.checked = options.frame === true;
	optionInputs.style.checked = options.style !== false;
}

// The options as the directive's key=value words, defaults left out.
function optionWords() {
	const options = getOptions();
	const words = [];
	for (const key of Object.keys(OPTION_DEFAULTS)) {
		const value = options[key];
		if (value === OPTION_DEFAULTS[key] || value === "") {
			continue;
		}
		words.push(key + "=" + (/\s/.test(String(value)) ? '"' + value + '"' : value));
	}
	return words;
}

function quoteFile(name) {
	return /[\s=]/.test(name) ? '"' + name + '"' : name;
}

function directiveLine() {
	return ["!include_sysml"].concat(files.map(f => quoteFile(f.name)), optionWords()).join(" ");
}

// ---------------------------------------------------------------------------
// Import into the middle pane, render the middle pane
// ---------------------------------------------------------------------------

function setImportStatus(message, isError) {
	importStatus.textContent = message;
	importStatus.classList.toggle("error", isError === true);
}

function importNow() {
	clearTimeout(importTimer);
	if (modeSelect.value === "directive") {
		importDirective();
	} else {
		importGenerated();
	}
	renderNow();
	scheduleSave();
}

function importGenerated() {
	const started = performance.now();
	let generated = null;
	let error = null;
	sysmlToPlantUml(files.map(f => f.name), files.map(f => f.text), optionWords(),
		source => (generated = source),
		message => (error = message));
	if (error != null) {
		// Keep the last good import, so the diagram does not vanish while typing
		setImportStatus(error, true);
		return;
	}
	editor.value = "@startuml\n" + generated + "\n@enduml\n";
	const warnings = generated.split("\n").filter(l => l.startsWith("' sysml warning")).length;
	setImportStatus("Imported " + files.length + " file" + (files.length === 1 ? "" : "s") + " in "
		+ Math.round(performance.now() - started) + " ms"
		+ (warnings ? ", " + warnings + " warning" + (warnings === 1 ? "" : "s") + " (see ' sysml warning lines)" : ""),
		false);
}

// Directive mode: keep the user's diagram, only rewrite its !include_sysml line
// (or start a diagram when there is none).
function importDirective() {
	const line = directiveLine();
	const source = editor.value;
	if (/^[ \t]*!include_sysml\b.*$/m.test(source)) {
		editor.value = source.replace(/^[ \t]*!include_sysml\b.*$/m, line);
	} else {
		editor.value = "@startuml\n" + line + "\n@enduml\n";
	}
	setImportStatus("The left pane's files are served to !include_sysml by name; edit the diagram around the directive freely.", false);
}

function scheduleImport() {
	scheduleSave();
	if (autoBox.checked === false) {
		return;
	}
	clearTimeout(importTimer);
	importTimer = setTimeout(importNow, DEBOUNCE_MS);
}

function renderNow() {
	clearTimeout(renderTimer);
	render(editor.value.split(/\r\n|\r|\n/), "out", {dark: dark});
}

function scheduleRender() {
	scheduleSave();
	clearTimeout(renderTimer);
	renderTimer = setTimeout(renderNow, DEBOUNCE_MS);
}

function updateTitle() {
	document.getElementById("puml-title").textContent = modeSelect.value === "directive"
		? "PlantUML with !include_sysml"
		: "Generated PlantUML";
}

// ---------------------------------------------------------------------------
// SysML file tabs
// ---------------------------------------------------------------------------

function renderTabs() {
	for (const old of tabsEl.querySelectorAll(".tab")) {
		old.remove();
	}
	const actions = tabsEl.querySelector(".tab-actions");
	files.forEach((file, index) => {
		const tab = document.createElement("span");
		tab.className = "tab" + (index === active ? " active" : "");
		tab.setAttribute("role", "tab");
		tab.setAttribute("aria-selected", index === active ? "true" : "false");
		tab.title = "Click to edit, double-click to rename";

		const label = document.createElement("span");
		label.textContent = file.name;
		tab.appendChild(label);
		tab.addEventListener("click", () => selectTab(index));
		tab.addEventListener("dblclick", () => renameTab(index));

		if (files.length > 1) {
			const close = document.createElement("button");
			close.type = "button";
			close.className = "tab-close";
			close.title = "Close " + file.name;
			close.textContent = "×";
			close.addEventListener("click", e => {
				e.stopPropagation();
				closeTab(index);
			});
			tab.appendChild(close);
		}
		tabsEl.insertBefore(tab, actions);
	});
	sysmlArea.value = files[active].text;
}

function selectTab(index) {
	active = index;
	renderTabs();
	scheduleSave();
}

function uniqueName(name) {
	if (findExact(name) == null) {
		return name;
	}
	const dot = name.lastIndexOf(".");
	const stem = dot > 0 ? name.slice(0, dot) : name;
	const ext = dot > 0 ? name.slice(dot) : "";
	for (let i = 2; ; i++) {
		if (findExact(stem + i + ext) == null) {
			return stem + i + ext;
		}
	}
}

function findExact(name) {
	return files.find(f => f.name === name) || null;
}

function renameTab(index) {
	const current = files[index].name;
	const name = (prompt("File name (as !include_sysml refers to it):", current) || "").trim();
	if (name === "" || name === current) {
		return;
	}
	if (findExact(name) != null) {
		showToast("A file is already called " + name, true);
		return;
	}
	files[index].name = name;
	renderTabs();
	scheduleImport();
}

function closeTab(index) {
	if (files[index].text.trim() !== "" && confirm("Close " + files[index].name + "?") === false) {
		return;
	}
	files.splice(index, 1);
	active = Math.min(active > index ? active - 1 : active, files.length - 1);
	renderTabs();
	scheduleImport();
}

function newTab() {
	files.push({name: uniqueName("model.sysml"), text: ""});
	active = files.length - 1;
	renderTabs();
	sysmlArea.focus();
	scheduleSave();
}

// A lone tab that is empty, or still the untouched sample, is only a
// placeholder: an opened file takes its place rather than joining it (the
// import would otherwise see the sample's system too, and draw it first).
function isPlaceholder(file) {
	const sample = sampleFile();
	return file.text.trim() === "" || (file.name === sample.name && file.text === sample.text);
}

// Opened or dropped files: a file with the name of an open tab replaces it.
async function addFiles(fileList) {
	const list = Array.from(fileList || []);
	if (list.length === 0) {
		return;
	}
	for (const file of list) {
		const text = await file.text();
		const existing = findExact(file.name);
		if (existing != null) {
			existing.text = text;
			active = files.indexOf(existing);
		} else if (files.length === 1 && isPlaceholder(files[0])) {
			files[0] = {name: file.name, text};
			active = 0;
		} else {
			files.push({name: file.name, text});
			active = files.length - 1;
		}
	}
	renderTabs();
	showToast("Loaded " + list.map(f => f.name).join(", "));
	scheduleImport();
}

function fileTabs() {
	sysmlArea.addEventListener("input", () => {
		files[active].text = sysmlArea.value;
		scheduleImport();
	});
	sysmlArea.addEventListener("keydown", e => {
		if (e.key === "Tab" && e.ctrlKey === false && e.altKey === false && e.metaKey === false) {
			e.preventDefault();
			document.execCommand("insertText", false, "  ");
		}
	});

	document.getElementById("tab-new").addEventListener("click", newTab);
	const input = document.getElementById("file-input");
	document.getElementById("tab-open").addEventListener("click", () => input.click());
	input.addEventListener("change", () => {
		addFiles(input.files);
		input.value = "";
	});

	const pane = document.getElementById("sysml-pane");
	pane.addEventListener("dragover", e => {
		e.preventDefault();
		pane.classList.add("drop");
	});
	pane.addEventListener("dragleave", () => pane.classList.remove("drop"));
	pane.addEventListener("drop", e => {
		e.preventDefault();
		pane.classList.remove("drop");
		addFiles(e.dataTransfer.files);
	});
}

// ---------------------------------------------------------------------------
// Toolbar, resizing, toast
// ---------------------------------------------------------------------------

function controls() {
	for (const input of Object.values(optionInputs)) {
		input.addEventListener(input.type === "text" ? "input" : "change", scheduleImport);
	}
	autoBox.addEventListener("change", () => {
		scheduleSave();
		if (autoBox.checked) {
			importNow();
		}
	});
	modeSelect.addEventListener("change", () => {
		updateTitle();
		if (modeSelect.value === "directive") {
			editor.value = "";   // start a fresh wrapper diagram
		}
		importNow();
	});
	document.getElementById("import").addEventListener("click", importNow);
	editor.addEventListener("input", scheduleRender);

	document.getElementById("theme").addEventListener("click", () => {
		dark = !dark;
		document.documentElement.classList.toggle("dark", dark);
		document.documentElement.style.colorScheme = dark ? "dark" : "light";
		renderNow();
	});

	document.getElementById("copy-puml").addEventListener("click", () => {
		navigator.clipboard.writeText(editor.value).then(
			() => showToast("PlantUML copied"),
			err => showToast("Copy failed: " + err, true));
	});
	document.getElementById("save-puml").addEventListener("click", () => {
		download(editor.value, "text/plain", stem() + ".puml");
	});
	document.getElementById("save").addEventListener("click", () => {
		const svg = document.getElementById("out").querySelector("svg");
		if (svg == null) {
			showToast("Nothing to save", true);
			return;
		}
		const clone = svg.cloneNode(true);
		if (clone.getAttribute("xmlns") == null) {
			clone.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		}
		download(new XMLSerializer().serializeToString(clone), "image/svg+xml", stem() + ".svg");
	});
	document.getElementById("reset").addEventListener("click", () => {
		if (confirm("Forget the open files and options, and load the sample model again?") === false) {
			return;
		}
		localStorage.removeItem(STORAGE_KEY);
		restore();
		editor.value = "";
		updateTitle();
		renderTabs();
		importNow();
	});
}

function stem() {
	return files[0].name.replace(/\.[^.]*$/, "") || "diagram";
}

function download(content, type, name) {
	const url = URL.createObjectURL(new Blob([content], {type}));
	const a = document.createElement("a");
	a.href = url;
	a.download = name;
	a.click();
	URL.revokeObjectURL(url);
}

function showToast(message, isError = false) {
	const toast = document.getElementById("toast");
	clearTimeout(toastTimer);
	toast.textContent = message;
	toast.classList.toggle("error", isError);
	toast.classList.add("visible");
	toastTimer = setTimeout(() => toast.classList.remove("visible"), 1600);
}

// Each splitter resizes the pane on its left.
function resizers() {
	for (const handle of document.querySelectorAll(".resize")) {
		const pane = document.getElementById(handle.dataset.pane);
		let offset = null;
		handle.addEventListener("mousedown", e => {
			offset = e.clientX - pane.getBoundingClientRect().right;
			e.preventDefault();
		});
		document.addEventListener("mouseup", () => (offset = null));
		document.addEventListener("mousemove", e => {
			if (offset === null) {
				return;
			}
			const width = e.clientX - pane.getBoundingClientRect().left - offset;
			const max = document.documentElement.clientWidth * 0.7;
			pane.style.flexBasis = Math.max(120, Math.min(width, max)) + "px";
		});
	}
}

// ---------------------------------------------------------------------------

const restored = restore();
updateTitle();
renderTabs();
fileTabs();
controls();
resizers();
createZoomController({
	viewport: document.getElementById("out"),
	zoomOut: document.getElementById("zoom-out"),
	zoomReset: document.getElementById("zoom-reset"),
	zoomIn: document.getElementById("zoom-in"),
	panToggle: document.getElementById("pan-tool")
});
// A restored session with auto-import off keeps the middle pane as it was left
if (restored && autoBox.checked === false && editor.value !== "") {
	renderNow();
} else {
	importNow();
}
