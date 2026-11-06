# Scenario-tagging examples

A worked alternative to `state_machine_routines.iuml`'s `!$in`/`!$out`
preprocessor filtering, using PlantUML's native element tags (`$name`) and
`hide`/`show`/`remove`/`restore` commands instead.

> **Update:** state diagrams now support `$tag` directly on a transition
> (`A --> B $tag`), attaching the tag to the link itself rather than to
> either endpoint. This was a genuine gap when this README was written (see
> "Tagging convention" below, and the superseded `$scenario_link`
> connector-state workaround) — it silently misparsed or hard-failed
> depending on the rest of the file, and never actually attached anything.
> It's fixed now: `hide`/`show`/`remove`/`restore $tag` matches a
> directly-tagged transition on its own, with no connector-state needed. See
> the new Finding below on `PickProfile`/`RetryOrBlock` for the case this
> unlocks that endpoint-only tagging genuinely couldn't reach.

- **`charge-post-state-machine.puml`** — the original, untouched, copied
  verbatim from `/home/david/state-machine-analysis` for reference/diffing.
- **`charge-post-model.iuml`** — its top-level lifetime diagram, retrofitted
  with tags for 4 of that report's 9 catalogued scenarios. See "Tagging
  convention" below for what the tags mean.
- **`pseudostate-hiding.puml`** — a small, standalone demonstration (not
  part of the charge-post model) of a gap in `hide`/`show`/`remove`/`restore`
  that specifically bites `[*]` and other pseudostates: see Findings below.

Every `view_charge_post_*.puml` file `!include`s `charge-post-model.iuml`
and differs only in the `hide`/`show`/`remove`/`restore` lines at the
bottom — that's the entire "scenario selection" logic, no
`!function`/`!procedure`/`!if`/`!while` involved. Render any of them
directly, e.g.:

```bash
java -jar plantuml.jar -tsvg -pipe < view_charge_post_s01_lockout_decommissioning.puml
```

(`.svg` and `.png` outputs are already checked in next to each `.puml` so
you can look without rendering. **Use a recent PlantUML build** —
`/opt/plantuml.jar` 1.2026.5 fails to parse the model's `-[nonode]->`
self-transition with a multi-line label at all, falling back to "Assumed
diagram type: sequence"; 1.2026.7beta12 handles it fine. If a `.puml` here
won't render, check the jar version before assuming the source is wrong.)

Only 4 of the report's 9 scenarios are tagged
(`s01_lockout_decommissioning`, `s08_emergency_stop`, and one transition
each for `s02_nominal_session` / `s03_cached_token_brownout`), chosen to
cover three different shapes of problem; extending to the other five is the
same pattern repeated, not shown here to keep the file readable. Nothing
about the machine itself was added or simplified for the demo: nested and
concurrent composite states, a sub-machine, choice/junction pseudostates,
shallow and deep history, fork/join are all still there.

## What each view demonstrates

| File | Scoping | Layout |
|---|---|---|
| `view_charge_post_s01_lockout_decommissioning.puml` | states + transitions | `remove`/`restore` |
| `view_charge_post_s08_emergency_stop.puml` | states + transitions | `hide`/`show` |
| `view_charge_post_s02_nominal_entry.puml` | transitions only | `hide`/`show` |
| `view_charge_post_s03_cached_entry.puml` | transitions only | `hide`/`show` |
| `view_charge_post_union_s01_s08.puml` | two scenarios at once | `remove`/`restore` |

## Findings

**Tagging only transitions can be entirely correct, or leave orphans,
depending on what's at the endpoints.** `view_charge_post_s02_nominal_entry.puml`
and `view_charge_post_s03_cached_entry.puml` tag only one transition each
(`connectorInserted`/`cardPresented`, into `authFull`/`authCached`) and
touch nothing else — no orphans result, because `WaitingForDriver`,
`authFull` and `authCached` are all genuinely shared infrastructure that
should stay visible regardless of which entry route is being shown.
Hiding/removing an element cascades to *its own* links, never onward to
whatever's at the far end of them — there's no built-in "hide a state if
none of its transitions are visible" behaviour — so this only works when
every state at either end is meant to stay as shared context. A state that
exists *only* for one scenario still needs its own tag, or it's left behind
as a disconnected, arrow-less box.

**Endpoint-only tagging cannot scope a single branch out of a *shared*
pseudostate, and `charge-post-model.iuml` has two live examples:**
`PickProfile`'s three branches all fan into `ChargeSession` (an
untagged shared target), and `RetryOrBlock`'s two branches fan into
`WaitingForDriver`/`CardBlocked` (also untagged, also shared). Tagging the
*targets* does nothing useful here — they're shared context, so tagging them
would wrongly pull them into only one scenario. Tagging the *pseudostate*
would be worse: `remove`/`hide` on a pseudostate cascades to all of its own
links, taking every sibling branch with it, not just the one the scenario
cares about. Neither endpoint is the right thing to tag; the branch itself
is. This is exactly what the `$scenario_link` connector-state workaround
existed for, and it's exactly what direct-tag-on-transition (see the Update
above) now does in one line instead:
`PickProfile --> ChargeSession $s0X : [...]` tags that one branch, leaving
`PickProfile` and the other two branches completely untouched by
`remove $s0X`. Neither of these two pairs is tagged yet in
`charge-post-model.iuml` — this was a real, still-open gap in the model, not
a hypothetical.

**A whole uninvolved composite region can be collapsed by tagging just the
container, not its contents.** S1 never enters `InService` at all (the
report says so explicitly), but `InService` contains most of the rest of
the machine — dozens of untagged states. Tagging only the four S1-exclusive
leaf states left `InService` itself untagged, so `remove $scenario_state`
had nothing to remove there and the "S1 only" view still dragged in the
entire session sub-machine (1677×3654, barely smaller than the full
1866×3645 diagram). Tagging the `InService {` declaration line itself with
every scenario that *does* touch it (`$s02_nominal_session
$s03_cached_token_brownout $s08_emergency_stop $scenario_state` — everything
except `$s01_lockout_decommissioning`) fixed it in one line: removal
cascades from a container to every child regardless of the child's own
tags, so the whole subtree disappeared for S1's view without tagging
anything inside it. Rendered size dropped to 1008×620. The reverse held up
too: the S8 view, which does need `InService` visible (the transition
originates from its frame), correctly shows the *entire* untagged interior
as context — which is exactly right for that scenario, since S8 can fire
from arbitrarily deep inside a live session and the real report's sequence
diagram shows exactly that.

**States and transitions can each belong to several scenarios for free.**
`ServiceMode` carries both `$s01_lockout_decommissioning` and
`$s08_emergency_stop` — no duplication, no extra bookkeeping.
`view_charge_post_union_s01_s08.puml` renders both scenarios together and
`ServiceMode` appears exactly once, with both its scenario-tagged
transitions (`technicianLocksOut`, `emergencyStopPressed`) correctly
attached to that single copy.

**`hide`/`show` vs `remove`/`restore` are genuinely independent axes**, not
two spellings of the same thing: both are used above to select the
identical set of visible elements for different reasons — `remove`/`restore`
when the view should reflow to a minimal diagram (S1, and the S1∪S8 union),
`hide`/`show` when the layout should hold its shape (S8, and the two
entry-point views). Which one to use is a per-view choice — swap the two
lines, nothing else in the model changes.

**The tag-cluster/stereotype spacing rule bit twice while building this.**
`state "✕" as HardStop $s08_emergency_stop $scenario_state<<terminate>>` —
note the space *between* the two `$tag`s (required) and the absence of one
*before* `<<terminate>>` (also required). Writing `$tag1$tag2<<stereo>>`
(no space between tags) or `$tag1 $tag2 <<stereo>>` (a stray space before
the stereotype) both fail to parse, silently falling back to "Assumed
diagram type: sequence" with no indication of which line is actually wrong.
Worth a linter check if this is ever done at scale.

**A pseudostate written with its shorthand syntax (`[*]`, or a bare
`<<choice>>`/`<<fork>>`/`<<join>>`/history/entry-exit-point stereotype with
no name) can't be tagged, so it never hides along with whatever it
connects to — confirmed with `pseudostate-hiding.puml`.** Hiding/removing a
state hides its own links (cascade from node to link, same as always), but
a pseudostate reached only by that link was never itself tagged, so it
stays fully rendered — now with nothing attached to it.
`pseudostate-hiding-broken.png` shows exactly that: a filled initial-state
dot floating with no arrow once its only neighbour is hidden. This is worse
for pseudostates than ordinary states, because an ordinary state usually
has several neighbours (losing one is often unremarkable) while an initial
or final pseudostate typically has exactly *one* connector, so losing it
always leaves an orphan glyph, never a partial one. The fix, confirmed in
`pseudostate-hiding-fixed.png`: give the pseudostate an explicit name and
stereotype instead of the shorthand (`state SI $tag $scenario_state<<start>>`
in place of bare `[*]`) and tag it identically to whatever it connects to —
it then hides/removes right along with it, no dangling glyph.
`charge-post-model.iuml` isn't currently exposed to this: every `[*]` in it
either connects to an untagged (always-shown) state, or sits inside the
`InService` container, and container-level `hide`/`remove` cascades to
every descendant regardless of the descendant's own tags (`Entity.isHidden()`/
`isRemoved()` both check the parent container first) — so the pseudostates
inside get swept up correctly without needing their own tags. It would bite
immediately if a future scenario tagged one of those `[*]` transitions
individually rather than via the container — S9 (join-route), one of the
five not yet tagged here, does exactly that (`ChargeSessionJoin --> [*]`).

## Tagging convention used here

- A **kind umbrella** tag per entity/link kind: `$scenario_state` for
  states, `$scenario_link` for transitions — either the small labelled
  connector-state workaround (needed when this README was written, and
  still the only option outside state diagrams), or, in a state diagram,
  a tag written directly on the `-->` line (`A --> B $scenario_link $s0X`)
  now that transitions can carry their own tag. Having two separate
  umbrellas, instead of one shared `$scenario`, is what makes
  "transitions-only" scoping possible — a view can `hide $scenario_link`
  without ever touching `$scenario_state`.
- One or more **specific scenario tags** (`$s01_lockout_decommissioning`,
  `$s08_emergency_stop`, `$s02_nominal_session`,
  `$s03_cached_token_brownout`) alongside the umbrella, on whichever
  elements belong to that scenario — states, transition-connectors, or a
  composite-state container when a whole sub-machine is exclusive to a
  scenario.
- No tag at all = permanent skeleton, always shown, in every view.
- A pseudostate that's exclusive to a scenario must be spelled out
  (`state SI ... <<start>>`) and tagged the same as its neighbour, not left
  as bare `[*]`/`<<choice>>`/etc. — see Findings above.

## Recommendation

**Prefer a direct `$tag` on the `-->` line over a `$scenario_link`
connector-state, in a state diagram.** The connector-state trick
(`state Conn $scenario_link $s0X<<transition>>` / `A --> Conn` /
`Conn --> B`) was the only way to tag a transition on its own when this
README was written; it also adds a visible box and two segments in place of
one arrow, which reads worse for anyone just trying to follow the machine.
A transition can now carry `$scenario_link $s0X` directly
(`A --> B $scenario_link $s0X : label`, tag before the `:label`, not after)
with identical `hide`/`show`/`remove`/`restore` behaviour and no extra node.
This is exactly what's needed for `PickProfile`/`RetryOrBlock` above: tag
the one branch that belongs to a scenario, leave the pseudostate and its
other branches alone.

**Tag states and transitions together, not transitions-only**, unless
every state at either end of a transition is genuinely meant to stay as
shared context across every scenario — that's already "untagged" by
default, no extra work needed. Otherwise a scenario-exclusive state left
untagged ends up an orphan box.

**Tag composite-state containers, not just their leaves, when a whole
sub-machine is scenario-exclusive.** Tagging the `state Foo { ... }` line
itself removes/hides the entire subtree in one line regardless of whether
anything inside is tagged, because removal cascades from parent to child.
Tagging every leaf individually to get the same effect is real, avoidable
work on any machine with nested composite states.

**Spell out any pseudostate that's exclusive to a scenario, instead of
leaving it as bare `[*]`.** Shorthand pseudostates can't carry a tag, so
they never disappear along with the scenario they belong to — they just
sit there disconnected. Give it a name and its stereotype explicitly
(`state SI $tag $scenario_state<<start>>`) and tag it like any other
scenario-exclusive element. Skip this only when the pseudostate's
connecting transition is untagged/shared, or the pseudostate sits inside a
container that's tagged as a whole — both cases cascade correctly without
it needing its own tag.

**Default to `remove`/`restore`; keep `hide`/`show` for the case where you
actually need it.** A scenario diagram's job is normally to communicate one
path clearly, and a compact, reflowed layout is what you'd put in a doc or
a ticket. Reach for `hide`/`show` specifically when several scenario
renders need to line up in the same position — flipping between slides, or
diffing two scenario SVGs pixel-by-pixel.

**Keep both kind-umbrella tags (`$scenario_state` / `$scenario_link`)
regardless**, even if the main path always hides/removes both together —
they cost nothing to maintain and they're what keeps the occasional
legitimate "scope the arrows only, leave the states as context" view
possible later without a redesign.

**Retire `$is_visible`/`$is_match`/`$tokenise`/`!$in`/`!$out`/
`$remove_hidden_items` from `state_machine_routines.iuml`.** All of that
logic collapses into two lines per view file, evaluated by the real
renderer instead of the preprocessor. Keep `$state`, `$transition`,
`$composite_state`, `$make_symbol` and the other formatting helpers — they
solve a different problem and have no native replacement.

**Migrate incrementally.** `state_machine_routines.iuml` is a substantial,
working file — don't rewrite it wholesale in one pass. The mechanical part
is small (in `$state`, swap the `tags` string parameter for literal
`$tag1 $tag2 $scenario_state` appended to the emitted `state` line; in
`$transition`, do the same for the `$transition_node` it already creates),
but prove it on one real diagram first and confirm the rendered output
matches what `!$in`/`!$out` used to produce before rolling it out further.
