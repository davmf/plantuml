# Investigation: can scenario state-diagram extracts use node-style edge labels?

## Goal

The report's two full state diagrams (`charge-post`, `authorise-driver`, both
defined in `charge-post-state-machine.puml`) render transition labels as
separate intermediate nodes (PlantUML `skinparam stateDiagramEdgeLabelStyle
node`). This gives cleaner label placement and routing than the default
inline-on-the-arrow style.

The per-scenario "compact extract" diagrams
(`report/state-machine-analysis-report/scenarios/*.typ`, one `state-diagram
extract` figure per scenario) currently do **not** use this style. The ask is
to make them visually consistent with the two full diagrams by turning the
same skinparam on for extracts too.

## Why extracts don't already do this

`charge-post-state-machine.puml` defines two nearly-identical shared blocks
that diagrams pull in with `!includesub`:

- `COMMON` (used by the two full diagrams) — includes
  `skinparam stateDiagramEdgeLabelStyle node`.
- `COMMON_EXTRACT` (used by every scenario extract) — identical, minus that
  one skinparam line.

The comment directly above `COMMON_EXTRACT` in the source
(`charge-post-state-machine.puml` lines ~125-136) explains why, and it's the
reason this needs investigation rather than a one-line change:

> Same as COMMON, minus `stateDiagramEdgeLabelStyle node`, for a scenario's
> compact extract to `!includesub` instead of COMMON. That one skinparam
> turns every transition label into its own intermediate node (PlantUML
> source: `CommandLinkStateCommon.createTransitionWithIntermediateNode`) —
> fine for the two real diagrams, where nothing is ever removed, but fatal
> for an extract: the label node is not itself tagged, so `remove`/`hide`
> takes away the transition's real endpoint and leaves the label node
> floating with a dangling arrow to nowhere.

## How extracts work (context needed to understand the bug)

Every scenario extract is built from the same master diagram body
(`LIFETIME` or `AUTHORISE` sub-block in `charge-post-state-machine.puml`),
which is authored once and reused everywhere. Most `state` declarations in
that body carry PlantUML tags (e.g. `$d_p $d_s1 $d_f6 $scenario_state`)
recording which scenarios reach that state. A scenario's own `.typ` file
does:

```
!includesub charge-post-state-machine.puml!COMMON_EXTRACT
!includesub charge-post-state-machine.puml!AUTHORISE   ' or !LIFETIME
remove $scenario_state
restore $d_f1                                          ' e.g. this scenario's own tag
```

`remove $scenario_state` hides every tagged state; `restore $d_f1` shows
back only the states relevant to that scenario. PlantUML's hide/remove
cascades: hiding a state also hides every edge attached to it. This is what
keeps each extract "compact" — it draws only the states and transitions a
given scenario actually visits.

## Empirical repro

Reproduced just now: took the `D-F1` scenario's extract
(`report/state-machine-analysis-report/scenarios/d-f1-idle-timeout.typ`,
figure `df1-state`) and swapped its `!includesub ...!COMMON_EXTRACT` for
`!includesub ...!COMMON` (i.e. turned the node label style back on),
otherwise unchanged, then rendered with the repo's `plantuml.jar`.

Result: four transition labels render as floating, disconnected text with
dangling arrowheads, because their real endpoint state(s) — `ValidatingToken`
and `OfflineWhitelist`, both removed by this scenario's `remove
$scenario_state` (neither carries `$d_f1`) — are gone, but the label node
PlantUML silently created for each of their labelled transitions is not
itself tagged, so `remove` doesn't take it down too:

- `tokenAccepted / issueSessionId()`
- `[tokenOnWhitelist] / flagDeferredBilling()`
- `backOfficeUnreachable(5 s)`
- `[else] / showTryLater()`

This matches exactly what the existing code comment predicted. Every other
scenario extract that removes at least one state with an outgoing/incoming
labelled transition will hit the same failure — which, per the tag lists in
`charge-post-state-machine.puml`, is effectively all of them (that's the
entire point of the compact-extract mechanism: each one hides most of the
machine).

## What's already been ruled out / established

- This is not a PlantUML version bug local to one jar — the comment says it
  was "confirmed empirically with a cut-down repro before writing this" when
  `COMMON_EXTRACT` was first introduced, and the repro above reconfirms it
  on the jar currently checked into the repo (`plantuml.jar` at repo root).
- `linetype ortho` and the other `COMMON`/`COMMON_EXTRACT` skinparams do not
  have this problem — only `stateDiagramEdgeLabelStyle node` does — so this
  is isolated to that one setting.
- The label node PlantUML creates for `stateDiagramEdgeLabelStyle node` is
  internal to `CommandLinkStateCommon.createTransitionWithIntermediateNode`
  (per the existing comment) — it isn't a state the diagram author declared,
  so there's no obvious place in the `.puml` source to attach a `$tag` to it
  even in principle.

## What hasn't been tried / open questions for investigation

1. Is there any PlantUML mechanism (skinparam, preprocessor directive,
   `!function`, style rule, or newer PlantUML version) that tags/removes the
   auto-generated label node along with its real endpoint, rather than
   leaving it orphaned? Worth checking PlantUML's changelog/issue tracker
   for `stateDiagramEdgeLabelStyle` + `hide`/`remove` interaction.
2. Is there a different way to get the same *visual* effect as
   `stateDiagramEdgeLabelStyle node` (label boxes with routed connectors
   instead of inline arrow labels) that doesn't go through that specific
   code path — e.g. a manual PlantUML idiom (declaring the label as an
   explicit small state/note and wiring it in by hand) that could be
   generated or maintained without becoming unmanageable across ~14
   scenario files?
3. If no such mechanism exists: is it acceptable to apply
   `stateDiagramEdgeLabelStyle node` only to extracts (or transitions within
   extracts) where doing so is known not to orphan anything — i.e., only
   when neither endpoint of a transition is ever removed by any scenario
   (the "permanent skeleton" states/pseudo-states already called out
   elsewhere in the file, like `PickProfile`/`RetryOrBlock`)? This would
   give partial visual consistency without the breakage, but the two styles
   would then coexist within a single diagram, which may look inconsistent
   in its own right.
4. Whether the extracts could instead post-process PlantUML's *output* SVG
   to reposition/restyle inline labels to visually resemble the node style,
   sidestepping the PlantUML-side generation entirely. This would decouple
   the fix from PlantUML's internals but adds a rendering post-processing
   step to the build (`report/build.sh` /
   `render_plantuml.py`) that doesn't currently exist.

## Files involved

- `charge-post-state-machine.puml` — defines `COMMON` / `COMMON_EXTRACT`
  (lines ~111-148) and the tagged `LIFETIME`/`AUTHORISE` diagram bodies that
  extracts pull from.
- `report/state-machine-analysis-report/scenarios/*.typ` — one file per
  scenario, each with a `!includesub ...!COMMON_EXTRACT` figure (the
  "compact extract") and a separate sequence-diagram figure (unaffected).
- `report/build.sh` and `report/state-machine-analysis-report/render_plantuml.py`
  (referenced by `build.sh`) — the build pipeline that renders all `.puml`
  and inline ` ```plantuml ` blocks; relevant only if a fix requires a new
  build step (see open question 4).

## Recommendation from this investigation

No safe drop-in fix was found. Turning on
`stateDiagramEdgeLabelStyle node` for `COMMON_EXTRACT` as originally asked
would visibly break the majority of scenario diagrams (confirmed by direct
repro, not just by re-reading the existing comment). Any real fix needs to
either find a PlantUML-side way to tag/remove the generated label node, or
accept a different, more invasive approach (partial styling or SVG
post-processing) — this needs a decision from whoever picks this up, not
just an implementation.

## Resolution

Open question 1 turned out to have a positive answer, because
`stateDiagramEdgeLabelStyle node` is not third-party PlantUML behaviour to
work around — it's this fork's own feature (`CommandLinkStateCommon`,
introduced in commits `353ed4a55`/`9b613fbc0`/`ba9e6966b`), so the orphaning
was fixable at the source rather than something to route around.

The label node's own removal/hidden status was being decided purely by
`HideOrShow` tag matching (`net.sourceforge.plantuml.cucadiagram.HideOrShow`),
which only ever sees tags an author wrote in the `.puml` source — and no
`.puml` source can tag a node PlantUML synthesizes internally. Fixed in
`net.atmp.CucaDiagram.isRemoved`/`isHidden` instead, by adding a structural
check (`isOrphanedTransitionLabel`): a `STATE_TRANSITION_LABEL` entity is now
also considered removed/hidden whenever either of the two real entities its
synthetic links connect it to is itself removed/hidden — independent of
tags. This cascades correctly through the existing `Link.isRemoved()` logic
(`cl1.isRemoved() || cl2.isRemoved()`), so both synthetic links (source→label,
label→target) disappear along with the label whenever either real endpoint
does, for any combination of `remove`/`restore`/`hide`/`show`, not just the
specific tag pattern used by `COMMON_EXTRACT`'s scenarios.

This means `COMMON_EXTRACT` can safely turn `stateDiagramEdgeLabelStyle node`
on (i.e. become identical to `COMMON`, at which point the two blocks could
even be merged) once a `plantuml.jar` built from this fix is in use.

Verified with:
- A direct before/after repro (`remove $tag; restore $other-tag` orphaning a
  labelled transition's endpoint): dangling label text present before the
  fix, gone after, confirmed via `plantuml.jar -tsvg -pipe`.
- Two new Vega non-regression tests in `src/test/resources/vega/state/`:
  `node-style-transition-label.puml` (baseline: label still renders when
  both endpoints survive) and
  `node-style-transition-label-orphaned-by-remove.puml` (the bug: label must
  disappear when an endpoint is removed). The latter was confirmed to fail
  against the pre-fix code and pass against the fix.
- A full `VegaTest` run (243 tests, 0 failures, 4 pre-existing aborts)
  against `plantuml.jar` built from the fix.

Fix: `src/main/java/net/atmp/CucaDiagram.java` (`isRemoved`, `isHidden`,
new `isOrphanedTransitionLabel` helper).
