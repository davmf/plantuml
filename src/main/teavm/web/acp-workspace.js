// The ACP bridge's workspace (tools/acp-bridge): the directory the agent works
// in. A SysML tab saved there is a file the agent can read and edit, and the
// bridge reports every change of the directory, so an agent's edit reaches
// the tab at once. Served by a plain static server, there is no workspace:
// connectWorkspace() resolves to null.

/**
 * onFile(name, text, initial): a file of the workspace, first every file
 * there (initial true, also after a reconnection), then each change.
 *
 * Resolves to {cwd, save(name, text)} or null; save resolves to the file's
 * path on the bridge's machine.
 */
export async function connectWorkspace(onFile) {
	let status;
	try {
		const res = await fetch("acp/status", {cache: "no-store"});
		status = res.ok ? await res.json() : null;
	} catch (err) {
		status = null;
	}
	if (status == null || status.available !== true) {
		return null;
	}

	// EventSource reconnects by itself; each connection starts with the
	// whole directory again, until "ready"
	let initial = true;
	const source = new EventSource("acp/files/events");
	source.addEventListener("open", () => (initial = true));
	source.addEventListener("file", e => {
		const file = JSON.parse(e.data);
		onFile(file.name, file.text, initial);
	});
	source.addEventListener("ready", () => (initial = false));

	return {
		cwd: status.cwd,
		async save(name, text) {
			const res = await fetch("acp/files/save", {
				method: "POST",
				headers: {"Content-Type": "application/json"},
				body: JSON.stringify({name, text}),
			});
			const body = await res.json().catch(() => ({}));
			if (res.ok === false) {
				throw new Error(body.error || String(res.status));
			}
			return body.path;
		}
	};
}
