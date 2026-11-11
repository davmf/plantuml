# Requirements and their traces: `!include_sysml ... view=requirements`

`view=requirements` draws plain SysML v2 requirements (no profile needed) and
the relations that trace them: what satisfies, verifies, derives from or
refines a requirement, the requirements nested in it, and the type references
between requirements and verifications (a test run that specialises a test
case). It reads the relations the way StrictDoc's SysML backend does
(`strictdoc/backend/sysml/reader.py` and `diagram.py`), so the diagram shows
the same traces as StrictDoc's trace screens.

```plantuml
@startuml
!include_sysml requirements.sysml design.sysml tests.sysml view=requirements focus=SYS-001 depth=2
title $sysml_focus $sysml_focusName
@enduml
```

Without `focus=`, every requirement of the file set (or of the package
`root=` names) is drawn with the relations between them. With `focus=`, the
diagram is centred on one element (highlighted) and holds what lies within
`depth` relations of it.

Use cases with their actors and objectives are drawn by
[`view=usecases`](SYSML_VIEW_USECASES.md).

## Options

| Option      | Values                  | Default                 | Meaning |
|-------------|-------------------------|-------------------------|---------|
| `focus`     | short id, name or qualified name | none           | The element at the centre, drawn `<<focus>>` (highlighted) |
| `depth`     | 0, 1, 2...              | 1 with focus, 0 without | How many relations away from the focus (or from the requirements) to go |
| `direction` | `both`, `up`, `down`    | `both`                  | Follow relations both ways, only towards what they trace to (`up`: the requirement a test verifies, the requirement a requirement derives from, the owner of a nested requirement), or only towards what traces to the element (`down`) |
| `text`      | `true`, `false`         | `true`                  | Show the doc text, cut after 160 characters |
| `packages`  | `true`, `false`         | `false`                 | Draw the packages as frames around their elements; in the tracker layout, the trackers and folders that include them, titled from their metadata |
| `layout`    | `elk`, `default`        | `elk`                   | Common to every profile and view (see SYSML_PROFILES.md): emit `!pragma layout elk`; `default` leaves the layout to the diagram |
| `root`      | package name            | the whole file set      | Without focus: only the requirements of this package (any depth; in the [tracker layout](SYSML_WORKSPACE.md), what it includes through `public import`). With focus: only look the focus up in this package |
| `scope`     | package names, comma-separated | none             | Like `root`, for several packages at once (`scope=TRK-STK,TRK-SYS`) |
| `links`     | URL pattern             | none                    | Common option: link each element with an ID (`links=/item/{id}`) |
| `style`     | `true`, `false`         | `true`                  | Emit the default colours |

`focus=` accepts the short id (`REQ-001` for `requirement <'REQ-001'> ...`,
StrictDoc's UID), the item ID of an element without a short name in the
tracker layout (`SC-002`), the simple name, or the qualified name
(`Reqs::System::'Flight time'`, quotes optional; `focus="Reqs::Flight time"`
when it holds spaces). A simple name that two elements share is an error that
lists them. An unknown focus, a `depth` that is not a whole number and an
unknown `direction` are errors too.

The focus can be any drawable element, not only a requirement: a test case,
a configuration item, a part.

A relation reached through a `subject` (or another member typing, see below)
is drawn but not followed further: a configuration item that hundreds of test
cases name as their subject would otherwise pull them all in at depth 2.
Focus on the configuration item itself to see who uses it.

`frame=` is accepted (it is common to every profile) but has no effect.

## What is drawn

Elements are the named (or short-named) `requirement`, `concern`,
`verification`, `analysis`, `case`, `use case`, `part`, `action`, `calc`,
`item`, `port`, `state`, `occurrence`, `constraint`... usages and
definitions outside library packages. Requirements are seeds without focus;
the others are drawn when a relation reaches them.

Every relation points from the client (what satisfies, verifies, derives,
specialises, is contained...) to the supplier, the way StrictDoc stores them
as Parent relations on the client; the supplier is drawn above.

| SysML                                              | Link | Label | StrictDoc |
|----------------------------------------------------|------|-------|-----------|
| a requirement nested in an element; a verification nested in a verification | `owner +-- nested` | (containment ⊕) | not a relation (nested requirements make a design file) |
| `satisfy R by X;` / `satisfy R;` inside X          | `R <.. X` | «satisfy» | Satisfies on X (reader.py `_add_design_members`, `_resolve_design_relations`) |
| `verify R;` inside a verification (also inside its `objective`) | `R <.. V` | «verify» | Verifies on the enclosing element |
| `#derivation dependency from A to B;`              | `B <.. A` | «derive» | Parent on A (`_resolve_dependencies`) |
| `#derivation connection { end #original ::> B; end #derive ::> A; }` | `B <.. A` | «derive» | (SysML standard form, not read by StrictDoc) |
| `#refinement dependency from A to B;`              | `B <.. A` | «refine» | Parent on A |
| `dependency [d] from A to B;`, `dependency A to B;` | `B <.. A` | «dependency» | Parent on A |
| `allocate A to B;`                                 | `B <.. A` | «allocate» | drawn by diagram.py only |
| `x : T` (typing)                                   | `T <\|.. x` | «defined by» | Typed by |
| `def X :> Y` (specialization)                      | `Y <\|-- X` | «specializes» | Specializes |
| usage `x :> y` (subsetting, e.g. a test run of a test case) | `y <\|-- x` | «subsets» | Subsets |
| `subject s : T;` in an element E                   | `T <.. E` | «subject» | References |
| another member typed by an element (`attribute a : R;`) | `R <.. E` | «references» | References |

Type references are only drawn when they name an element of the file set
(`:> RequirementCheck` names a library type and is dropped), as StrictDoc
keeps a type reference only if it names a node.

A reference is resolved by short id first (an id may hold dots:
`'SYS-001.1'`), then by name in the enclosing scopes, then as the element
whose qualified name ends with it, the one closest to where it is written
winning. A path that names nothing (`satisfy R by drone.controller.port`)
resolves to the deepest element its leading segments name, like StrictDoc's
`_resolve_design_path`. What still names nothing is drawn as a dashed
`<<unresolved>>` box (labelled «requirement» when it is the requirement side
of a satisfy, verify or dependency), as diagram.py draws external boxes, with
a `' sysml warning:` comment.

## Diagram form

A class diagram: its boxes have compartments (keyword, id and name on top,
the doc text below, as SysML draws a requirement), its `+--` link is SysML's
containment (circled plus on the owner), links carry stereotypes that a
`<style>` block can colour, and `MaximumWidth` wraps the doc text without
hand-made line breaks. Large diagrams use ELK (`layout=elk`, the default):
without `packages=true`, which ELK lays out poorly when many relations cross
package boundaries.

* Each element is `class "<label>" as <alias> <<kind>>`, with the kind
  written without spaces (`<<requirement>>`, `<<requirementDef>>`,
  `<<verification>>`, `<<verificationDef>>`, `<<part>>`, `<<useCase>>`...),
  plus `<<focus>>` for the focus and `<<unresolved>>` for a reference that
  names nothing. Stereotypes are hidden (`hide stereotype`); the label shows
  the SysML keyword («requirement def»).
* Aliases are the short id or else the name, made into identifiers
  (`SYS-001.1` is `SYS_001_1`), so notes and hints can be added after the
  directive: `note right of SYS_001 : reviewed`.
* With `text=true`, a second compartment lists the expressions of the
  requirement's `require constraint { ... }` and `assume constraint { ... }`.
* Each relation is a link stereotyped with its kind: `<<containment>>`,
  `<<satisfy>>`, `<<verify>>`, `<<derive>>`, `<<refine>>`, `<<dependency>>`,
  `<<allocate>>`, `<<definedBy>>`, `<<specializes>>`, `<<subsets>>`,
  `<<subject>>`, `<<references>>`.
* Variables: `$sysml_root` (the `root=` package, or empty), and with focus
  `$sysml_focus` (its id, else its name), `$sysml_focusName`,
  `$sysml_focusKind`.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_element_label`  | `$id, $name, $kind` | `«kind»`, the id in bold, the name |
| `$sysml_text_label`     | `$text, $id`        | the doc text |
| `$sysml_constraint_label` | `$text, $kind`    | `require { text }` / `assume { text }` |
| `$sysml_relation_label` | `$kind`             | `«kind»` (`«defined by»` for `definedBy`) |

Generated for the focus `TC-002` of the test fixtures (label functions and
style left out):

```plantuml
' ---- !include_sysml drone-requirements.sysml drone-design.sysml drone-tests.sysml :  (requirements) ----
!$sysml_root = ""
!$sysml_focus = "TC-002"
!$sysml_focusName = "Battery alarm test"
!$sysml_focusKind = "verification"
!pragma layout elk
hide circle
hide empty members
hide stereotype
class "$sysml_element_label("SW-001", "BatteryMonitor", "requirement")" as SW_001 <<requirement>> {
  {field} $sysml_text_label("The flight software shall estimate the battery charge every second.", "SW-001")
}
class "$sysml_element_label("CONF-1", "prototype", "part")" as CONF_1 <<part>>
class "$sysml_element_label("TC-002", "Battery alarm test", "verification")" as TC_002 <<verification>> <<focus>>
class "$sysml_element_label("", "SYS-404", "requirement")" as SYS_404 <<requirement>> <<unresolved>>
CONF_1 <.. TC_002 <<subject>> : $sysml_relation_label("subject")
SW_001 <.. TC_002 <<verify>> : $sysml_relation_label("verify")
SYS_404 <.. TC_002 <<verify>> : $sysml_relation_label("verify")
' sysml warning: drone-tests.sysml:13: verify 'SYS-404' names no element
' ---- end of !include_sysml drone-requirements.sysml drone-design.sysml drone-tests.sysml ----
```

## Limits

* The doc text goes through Creole: `**bold**` and `//italic//` in a doc
  are rendered (StrictDoc's RST markup is shown as written otherwise).
* `assert not satisfy` is drawn as «satisfy».
* Nested requirements are drawn with containment links, not inside their
  owner's box (a class cannot hold a class).

## Code

`net.sourceforge.plantuml.sysml.requirements`: `ReqView` (the view,
registered in `SysmlImport.VIEWS`), `ReqModel` (elements, relations,
reference resolution), `ReqWriter` (selection by focus and depth, output).
The parser keeps what the view needs on every `SysmlNode`: the short name,
the `#` metadata prefixes, every type after `:`, and the ends of `satisfy`,
`verify`, `dependency` and `allocate`.

Regression tests: `src/test/resources/vega/sysml/views/requirements/`, on a
synthetic drone model (three files: requirements, design, tests).
