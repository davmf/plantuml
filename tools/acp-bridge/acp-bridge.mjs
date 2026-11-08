#!/usr/bin/env node
// ACP bridge for the browser playgrounds (sysml.html).
//
// Serves the static TeaVM build (like `python3 -m http.server`) and relays
// the Agent Client Protocol between the page and a local agent process (the
// Claude ACP adapter by default). The browser cannot spawn processes, so this
// script does it; the page is the ACP client, this script only carries the
// newline-delimited JSON-RPC both ways:
//
//   GET  /acp/status          {"available": true, ...}: the page shows its chat
//                             button only when this answers
//   GET  /acp/events          Server-Sent Events: spawns one agent for this
//                             stream; first event "hello" carries the
//                             connection token, then one "message" per
//                             JSON-RPC line the agent writes. Closing the
//                             stream kills the agent.
//   POST /acp/send?c=<token>  one JSON-RPC message to the agent's stdin
//
// and the workspace (--cwd), so that the page's files are files the agent
// can read and edit:
//
//   POST /acp/files/save      {name, text}: writes a file of the workspace
//                             (a plain name, no directories)
//   GET  /acp/files/events    Server-Sent Events: "file" {name, text} for
//                             every file of the workspace, then again each
//                             time one changes on disk (an agent's edit)
//
// Security (the agent reads and writes files, and runs commands, as you):
//   - listens on 127.0.0.1 only;
//   - Host must be localhost/127.0.0.1 on our port (DNS rebinding);
//   - Origin, when sent, must be this server: other sites cannot open the
//     stream or post to it (cross-site request forgery);
//   - POST needs Content-Type: application/json (a cross-site fetch would
//     need a CORS preflight, which is never granted) and the stream's
//     random token;
//   - the cwd of session/new and session/load is forced to --cwd, and only
//     the client-to-agent methods the page uses are passed on;
//   - the agent keeps its credentials (your `claude` login): nothing secret
//     goes to the page.
//
// Usage:
//   node tools/acp-bridge/acp-bridge.mjs [--port 8765] [--root build/generated/teavm/js]
//        [--cwd build/acp-workspace] [--agent "<command line>"]
//   open http://localhost:8765/sysml.html
//
// The agent command defaults to $ACP_AGENT, else `npx -y
// @agentclientprotocol/claude-agent-acp`.

import { spawn } from "node:child_process";
import { randomBytes } from "node:crypto";
import { createReadStream, mkdirSync, readFileSync, readdirSync, statSync, watch, writeFileSync } from "node:fs";
import { createServer } from "node:http";
import { delimiter, dirname, extname, join, normalize, resolve, sep } from "node:path";

const DEFAULT_AGENT = "npx -y @agentclientprotocol/claude-agent-acp";
const MAX_BODY = 8 * 1024 * 1024;

// Client-to-agent methods the page may call; responses (no method) always pass.
const ALLOWED_METHODS = new Set([
	"initialize",
	"session/new",
	"session/load",
	"session/prompt",
	"session/cancel",
	"session/set_mode",
	"session/set_config_option",
	"session/set_model",
]);

const MIME = {
	".html": "text/html; charset=utf-8",
	".js": "text/javascript; charset=utf-8",
	".mjs": "text/javascript; charset=utf-8",
	".css": "text/css; charset=utf-8",
	".json": "application/json; charset=utf-8",
	".svg": "image/svg+xml",
	".png": "image/png",
	".ico": "image/x-icon",
	".wasm": "application/wasm",
	".map": "application/json; charset=utf-8",
	".txt": "text/plain; charset=utf-8",
	".sysml": "text/plain; charset=utf-8",
	".puml": "text/plain; charset=utf-8",
};

function parseArgs(argv) {
	const args = {
		port: 8765,
		root: "build/generated/teavm/js",
		cwd: "build/acp-workspace",
		agent: process.env.ACP_AGENT || DEFAULT_AGENT,
	};
	for (let i = 0; i < argv.length; i++) {
		const key = argv[i];
		const value = argv[i + 1];
		if (key === "--port") {
			args.port = Number(value);
			i++;
		} else if (key === "--root") {
			args.root = value;
			i++;
		} else if (key === "--cwd") {
			args.cwd = value;
			i++;
		} else if (key === "--agent") {
			args.agent = value;
			i++;
		} else if (key === "--help" || key === "-h") {
			console.log("usage: acp-bridge.mjs [--port N] [--root DIR] [--cwd DIR] [--agent CMD]");
			process.exit(0);
		} else {
			console.error("unknown argument: " + key);
			process.exit(2);
		}
	}
	if (Number.isInteger(args.port) === false || args.port <= 0) {
		console.error("bad --port");
		process.exit(2);
	}
	args.root = resolve(args.root);
	args.cwd = resolve(args.cwd);
	return args;
}

// A command line split on blanks, with "double quoted" words.
function splitCommand(line) {
	return (line.match(/"[^"]*"|\S+/g) || []).map(w => w.replace(/^"(.*)"$/, "$1"));
}

const args = parseArgs(process.argv.slice(2));
mkdirSync(args.cwd, { recursive: true });

const connections = new Map(); // token -> {child, res}

// ---------------------------------------------------------------------------
// Request checks
// ---------------------------------------------------------------------------

function allowedHosts() {
	return new Set(["127.0.0.1:" + args.port, "localhost:" + args.port]);
}

// Host against DNS rebinding, Origin (when the browser sends one) against
// other sites.
function trusted(req) {
	const hosts = allowedHosts();
	if (hosts.has(req.headers.host || "") === false) {
		return false;
	}
	const origin = req.headers.origin;
	if (origin == null) {
		return true;
	}
	return origin === "http://" + req.headers.host;
}

function send(res, status, type, body) {
	res.writeHead(status, {
		"Content-Type": type,
		"Cache-Control": "no-store",
		"X-Content-Type-Options": "nosniff",
	});
	res.end(body);
}

function sendJson(res, status, value) {
	send(res, status, "application/json; charset=utf-8", JSON.stringify(value));
}

// ---------------------------------------------------------------------------
// ACP relay
// ---------------------------------------------------------------------------

function sseEvent(res, event, data) {
	res.write("event: " + event + "\ndata: " + data.replace(/\n/g, "\ndata: ") + "\n\n");
}

// The bridge's environment, with the directory of the node running it first
// on PATH: started as /path/to/node acp-bridge.mjs from a shell whose PATH
// has no node (Zed's private Node, say), the default agent still finds npx
// next to it, and npx's "#!/usr/bin/env node" finds node.
function agentEnv() {
	const nodeDir = dirname(process.execPath);
	const path = process.env.PATH || "";
	if (path.split(delimiter).includes(nodeDir)) {
		return process.env;
	}
	return Object.assign({}, process.env, { PATH: nodeDir + (path === "" ? "" : delimiter + path) });
}

function openStream(req, res) {
	const token = randomBytes(18).toString("base64url");
	const [command, ...commandArgs] = splitCommand(args.agent);
	let child;
	try {
		child = spawn(command, commandArgs, {
			cwd: args.cwd,
			stdio: ["pipe", "pipe", "pipe"],
			env: agentEnv(),
		});
	} catch (err) {
		sendJson(res, 500, { error: "cannot start the agent: " + err.message });
		return;
	}

	res.writeHead(200, {
		"Content-Type": "text/event-stream; charset=utf-8",
		"Cache-Control": "no-store",
		"Connection": "keep-alive",
		"X-Accel-Buffering": "no",
	});
	connections.set(token, { child, res });
	sseEvent(res, "hello", JSON.stringify({ token, cwd: args.cwd, agent: args.agent }));
	log("agent started (" + token.slice(0, 6) + "): " + args.agent);

	let buffer = "";
	child.stdout.setEncoding("utf8");
	child.stdout.on("data", chunk => {
		buffer += chunk;
		let nl;
		while ((nl = buffer.indexOf("\n")) >= 0) {
			const line = buffer.slice(0, nl).trim();
			buffer = buffer.slice(nl + 1);
			if (line !== "") {
				sseEvent(res, "message", line);
			}
		}
	});
	child.stderr.setEncoding("utf8");
	child.stderr.on("data", chunk => {
		for (const line of chunk.split("\n")) {
			if (line.trim() !== "") {
				log("agent " + token.slice(0, 6) + ": " + line);
			}
		}
	});
	child.on("error", err => {
		const hint = err.code === "ENOENT" ? " (not found on PATH; give the full path with --agent)" : "";
		log("cannot start the agent: " + err.message + hint);
		sseEvent(res, "agent-exit", JSON.stringify({ error: err.message + hint }));
		close(token);
	});
	child.on("exit", (code, signal) => {
		log("agent exited (" + token.slice(0, 6) + "): " + (signal || code));
		sseEvent(res, "agent-exit", JSON.stringify({ code, signal }));
		close(token);
	});

	const keepAlive = setInterval(() => res.write(": ping\n\n"), 20000);
	req.on("close", () => {
		clearInterval(keepAlive);
		close(token);
	});
}

function close(token) {
	const connection = connections.get(token);
	if (connection == null) {
		return;
	}
	connections.delete(token);
	if (connection.child.exitCode == null) {
		connection.child.kill();
	}
	connection.res.end();
}

// Checks one page-to-agent message, and forces the workspace on new sessions.
function filterMessage(message) {
	if (message == null || typeof message !== "object" || message.jsonrpc !== "2.0") {
		return { error: "not a JSON-RPC 2.0 message" };
	}
	if (message.method == null) {
		return { message }; // a response to a request of the agent
	}
	if (ALLOWED_METHODS.has(message.method) === false) {
		return { error: "method not allowed by the bridge: " + message.method };
	}
	if (message.method === "session/new" || message.method === "session/load") {
		message.params = Object.assign({}, message.params, { cwd: args.cwd });
		if (Array.isArray(message.params.mcpServers) === false) {
			message.params.mcpServers = [];
		}
	}
	return { message };
}

function postMessage(req, res, url) {
	const connection = connections.get(url.searchParams.get("c") || "");
	if (connection == null) {
		sendJson(res, 404, { error: "no such connection" });
		return;
	}
	if ((req.headers["content-type"] || "").split(";")[0].trim() !== "application/json") {
		sendJson(res, 415, { error: "Content-Type must be application/json" });
		return;
	}
	let size = 0;
	const chunks = [];
	req.on("data", chunk => {
		size += chunk.length;
		if (size > MAX_BODY) {
			sendJson(res, 413, { error: "message too large" });
			req.destroy();
			return;
		}
		chunks.push(chunk);
	});
	req.on("end", () => {
		let parsed;
		try {
			parsed = JSON.parse(Buffer.concat(chunks).toString("utf8"));
		} catch (err) {
			sendJson(res, 400, { error: "bad JSON: " + err.message });
			return;
		}
		const { message, error } = filterMessage(parsed);
		if (error != null) {
			sendJson(res, 403, { error });
			return;
		}
		connection.child.stdin.write(JSON.stringify(message) + "\n");
		sendJson(res, 200, { ok: true });
	});
}

// ---------------------------------------------------------------------------
// Workspace files
// ---------------------------------------------------------------------------

const MAX_FILE = 2 * 1024 * 1024;

// A file of the workspace itself: no directory part, nothing hidden.
function workspaceName(name) {
	if (typeof name !== "string" || name === "" || name.length > 255
		|| /[\/\\\0]/.test(name) || name.startsWith(".")) {
		return null;
	}
	return name;
}

function readWorkspaceFile(name) {
	try {
		const file = join(args.cwd, name);
		const stat = statSync(file);
		if (stat.isFile() === false || stat.size > MAX_FILE) {
			return null;
		}
		return readFileSync(file, "utf8");
	} catch {
		return null; // removed meanwhile
	}
}

function saveFile(req, res) {
	if ((req.headers["content-type"] || "").split(";")[0].trim() !== "application/json") {
		sendJson(res, 415, { error: "Content-Type must be application/json" });
		return;
	}
	const chunks = [];
	let size = 0;
	req.on("data", chunk => {
		size += chunk.length;
		if (size > MAX_FILE + 1024) {
			sendJson(res, 413, { error: "file too large" });
			req.destroy();
			return;
		}
		chunks.push(chunk);
	});
	req.on("end", () => {
		let body;
		try {
			body = JSON.parse(Buffer.concat(chunks).toString("utf8"));
		} catch (err) {
			sendJson(res, 400, { error: "bad JSON: " + err.message });
			return;
		}
		const name = workspaceName(body && body.name);
		if (name == null || typeof body.text !== "string") {
			sendJson(res, 400, { error: "expected {name, text} with a plain file name" });
			return;
		}
		try {
			writeFileSync(join(args.cwd, name), body.text, "utf8");
		} catch (err) {
			sendJson(res, 500, { error: err.message });
			return;
		}
		log("saved " + name);
		sendJson(res, 200, { ok: true, path: join(args.cwd, name) });
	});
}

// Every file of the workspace, then each change, debounced per file: an
// editor that writes a temporary file and renames it settles first.
function watchFiles(req, res) {
	res.writeHead(200, {
		"Content-Type": "text/event-stream; charset=utf-8",
		"Cache-Control": "no-store",
		"Connection": "keep-alive",
		"X-Accel-Buffering": "no",
	});
	const sendFile = name => {
		if (workspaceName(name) == null) {
			return;
		}
		const text = readWorkspaceFile(name);
		if (text != null) {
			sseEvent(res, "file", JSON.stringify({ name, text }));
		}
	};
	for (const name of readdirSync(args.cwd)) {
		sendFile(name);
	}
	sseEvent(res, "ready", "{}");

	const timers = new Map();
	let watcher;
	try {
		watcher = watch(args.cwd, (event, name) => {
			if (name == null) {
				return;
			}
			clearTimeout(timers.get(name));
			timers.set(name, setTimeout(() => {
				timers.delete(name);
				sendFile(name);
			}, 100));
		});
	} catch (err) {
		sseEvent(res, "error", JSON.stringify({ error: "cannot watch the workspace: " + err.message }));
	}
	const keepAlive = setInterval(() => res.write(": ping\n\n"), 20000);
	req.on("close", () => {
		clearInterval(keepAlive);
		for (const timer of timers.values()) {
			clearTimeout(timer);
		}
		if (watcher != null) {
			watcher.close();
		}
	});
}

// ---------------------------------------------------------------------------
// Static files
// ---------------------------------------------------------------------------

function serveStatic(req, res, url) {
	if (req.method !== "GET" && req.method !== "HEAD") {
		send(res, 405, "text/plain", "method not allowed");
		return;
	}
	let path;
	try {
		path = decodeURIComponent(url.pathname);
	} catch {
		send(res, 400, "text/plain", "bad path");
		return;
	}
	let file = normalize(join(args.root, path));
	if (file !== args.root && file.startsWith(args.root + sep) === false) {
		send(res, 403, "text/plain", "forbidden");
		return;
	}
	let stat;
	try {
		stat = statSync(file);
		if (stat.isDirectory()) {
			file = join(file, "index.html");
			stat = statSync(file);
		}
	} catch {
		send(res, 404, "text/plain", "not found: " + path);
		return;
	}
	res.writeHead(200, {
		"Content-Type": MIME[extname(file).toLowerCase()] || "application/octet-stream",
		"Content-Length": stat.size,
		"Cache-Control": "no-cache",
	});
	if (req.method === "HEAD") {
		res.end();
		return;
	}
	createReadStream(file).pipe(res);
}

// ---------------------------------------------------------------------------

function log(message) {
	console.log("[acp-bridge] " + message);
}

const server = createServer((req, res) => {
	const url = new URL(req.url, "http://" + (req.headers.host || "localhost"));
	if (url.pathname.startsWith("/acp/")) {
		if (trusted(req) === false) {
			sendJson(res, 403, { error: "untrusted Host or Origin" });
			return;
		}
		if (url.pathname === "/acp/status" && req.method === "GET") {
			sendJson(res, 200, { available: true, cwd: args.cwd, agent: args.agent });
		} else if (url.pathname === "/acp/events" && req.method === "GET") {
			openStream(req, res);
		} else if (url.pathname === "/acp/send" && req.method === "POST") {
			postMessage(req, res, url);
		} else if (url.pathname === "/acp/files/save" && req.method === "POST") {
			saveFile(req, res);
		} else if (url.pathname === "/acp/files/events" && req.method === "GET") {
			watchFiles(req, res);
		} else {
			sendJson(res, 404, { error: "unknown ACP endpoint" });
		}
		return;
	}
	serveStatic(req, res, url);
});

server.listen(args.port, "127.0.0.1", () => {
	log("serving " + args.root);
	log("agent workspace " + args.cwd);
	log("open http://localhost:" + args.port + "/sysml.html");
});

function shutdown() {
	for (const token of Array.from(connections.keys())) {
		close(token);
	}
	process.exit(0);
}
process.on("SIGINT", shutdown);
process.on("SIGTERM", shutdown);
