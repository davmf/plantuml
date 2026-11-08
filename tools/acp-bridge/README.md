# acp-bridge

Lets the SysML playground (`sysml.html`) chat with a local coding agent over the
[Agent Client Protocol](https://agentclientprotocol.com). A web page cannot spawn
processes, so this script serves the static TeaVM build (in place of
`python3 -m http.server`) and relays ACP between the page and the agent.
It has no dependencies beyond Node 18+.

```bash
./gradlew teavm -Pfast                      # build/generated/teavm/js
node tools/acp-bridge/acp-bridge.mjs        # http://localhost:8765/sysml.html
```

An **Agent** button appears in the playground's header only when the page is
served by the bridge. Under a plain static server the chat stays hidden.

## Agent

The default is `npx -y @agentclientprotocol/claude-agent-acp`, the Claude ACP
adapter. It signs in with your existing `claude` login, so no API key is involved
and none reaches the page. To run another agent, pass `--agent "<command line>"`
or set `ACP_AGENT`. For example, Zed's installed copy:

```bash
node tools/acp-bridge/acp-bridge.mjs --agent \
  "node $HOME/.local/share/zed/external_agents/registry/npx/claude-acp/node_modules/@agentclientprotocol/claude-agent-acp/dist/index.js"
```

| option    | default                    |                                              |
|-----------|----------------------------|----------------------------------------------|
| `--port`  | `8765`                     | listens on 127.0.0.1 only                     |
| `--root`  | `build/generated/teavm/js` | static files                                  |
| `--cwd`   | `build/acp-workspace`      | the agent's working directory, forced on every session |
| `--agent` | see above                  | the ACP agent command                         |

## How it works

| endpoint                 |                                                                   |
|--------------------------|-------------------------------------------------------------------|
| `GET /acp/status`        | the page shows the chat button when this answers                  |
| `GET /acp/events`        | Server-Sent Events. Spawns one agent per stream and sends a `hello` event with a random token, then one `message` event per JSON-RPC line from the agent. Closing the stream kills the agent. |
| `POST /acp/send?c=token` | one JSON-RPC message to the agent's stdin                         |
| `POST /acp/files/save`   | `{name, text}`: writes a file in the workspace. The name must be a plain file name: no directories, no leading dot. |
| `GET /acp/files/events`  | Server-Sent Events: a `file` event `{name, text}` for every file in the workspace, then `ready`, then a `file` event each time a file changes on disk |

### Saved tabs

**Save** (Ctrl+S) writes the active SysML tab into the workspace under the
tab's name. The tab is then linked to that file:

- The agent can read and edit it with its own tools.
- The bridge watches the workspace, so each edit to the file replaces the
  tab's text immediately. The replacement is a normal edit, which Ctrl+Z
  undoes. If the tab had unsaved edits, a warning says they were replaced;
  Ctrl+Z brings them back.
- A new `.sysml` or `.kerml` file created in the workspace opens as a tab.

A saved tab shows a filled dot. The dot is hollow when the tab has edits that
aren't saved yet. Renaming a tab unlinks it. **Download** always saves the tab
to your own machine. Without the bridge, Ctrl+S downloads.

Each prompt tells the agent which tabs are saved. The agent is told to edit
saved tabs on disk, and to propose changes to unsaved tabs as code blocks.

The page (`src/main/teavm/web/acp-chat.js`) is the ACP client. It does not offer
`fs` or `terminal` capabilities, so the agent uses its own tools inside `--cwd`.
It starts each session in **Manual** mode, where edits and commands ask first,
and shows each `session/request_permission` with the agent's options.
The agent's other session options, such as model, effort and fast, appear as
selects built from the `configOptions` it reports. They are remembered in
`localStorage` and set again on each new chat. In the input box, Enter sends;
Alt+Enter or Shift+Enter starts a new line. Each prompt
can attach the open SysML files and the PlantUML pane as embedded resources.
Code blocks in answers get buttons to open them as a new SysML tab, replace the
active tab, or load them into the PlantUML pane.

## Security

The agent runs with your user's permissions, so the bridge:

- binds to `127.0.0.1` only;
- rejects `/acp/*` requests whose `Host` is not `localhost` or `127.0.0.1` on its
  port, which blocks DNS rebinding;
- rejects requests with an `Origin` header that is not the bridge itself, which
  blocks other sites open in your browser;
- requires `Content-Type: application/json` and the stream's random token on
  every POST;
- forwards only the client methods the page uses (`initialize`, `session/new`,
  `session/load`, `session/prompt`, `session/cancel`, `session/set_mode`,
  `session/set_config_option`, `session/set_model`), plus responses;
- forces `cwd` on `session/new` and `session/load`.

`--cwd` sets where the agent starts. It is **not a sandbox**: in Auto or Bypass
mode, the agent can reach files outside it.
