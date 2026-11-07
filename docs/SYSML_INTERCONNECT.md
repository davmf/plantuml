# Interconnect diagrams from SysML v2: `!include_sysml`

`!include_sysml` reads SysML v2 files written with **InterconnectProfile**
(Units, Cables, Assemblies, ConnectorPorts, ContactPorts, Mates, Wires,
NoMates, InterfaceLinks, UnitLinks) and turns one `InterconnectSystem` into
component diagram source. That source then runs where the directive appears,
as if it had been `!include`d, so the importing diagram keeps full control
over layout, styling and labels.

```plantuml
@startuml
left to right direction
!pragma layout elk
!include_sysml test-interconnect-2.sysml
title $sysml_system — $sysml_partNumber rev $sysml_revision
@enduml
```

Run `java -jar plantuml.jar -preproc diagram.puml` to see the generated
PlantUML.

The directive also reads the other profiles (state machines, sequences,
activities, software architecture, PCBAs, inter-PCBA systems, COTS devices):
see [SYSML_PROFILES.md](SYSML_PROFILES.md) for how the root and the profile
are chosen.

## Syntax

```
!include_sysml file.sysml [more.sysml ...] [key=value ...]
```

* Paths are resolved like `!include` (relative to the including file). Each
  word that contains `=` is an option; any other word, or a double-quoted
  string, is a file. Variables are expanded first: `!include_sysml $model`.
* List several files when a system uses definitions kept elsewhere. The
  InterconnectProfile library itself does not need to be listed: its type
  names are known by name.

| Option    | Values                              | Default     | Meaning |
|-----------|-------------------------------------|-------------|---------|
| `system`  | part name                           | first found | Which `part x : InterconnectSystem` to draw (same as `root=`) |
| `view`    | `physical`, `logical`, `both`       | `physical`  | Cables and Mates; InterfaceLinks and UnitLinks (no Cables); or both overlaid |
| `detail`  | `connector`, `contact`              | `connector` | One port per connector; or one port group per connector, one port per contact, each Mate drawn as a `harness` pairing contacts by `contactId` (physical view only) |
| `cables`  | `node`, `edge`                      | `node`      | A two-ended Cable mated to two Units can be drawn as one labelled edge; multi-ended harnesses stay components |
| `frame`   | `true`, `false`                     | `false`     | Draw the system itself as an outer rectangle |
| `style`   | `true`, `false`                     | `true`      | Emit the default skinparams and `<style>` |
| `profile` | `interconnect`...                   | detected    | See [SYSML_PROFILES.md](SYSML_PROFILES.md) |

## What is generated

| Model                | PlantUML                                    | Alias       | Stereotypes |
|----------------------|---------------------------------------------|-------------|-------------|
| Unit (PCBAUnit, Sensor, ...) | `component` with ports              | `A1`        | profile category: `<<PCBAUnit>>`, `<<Sensor>>`, `<<Motor>>`, `<<PowerSource>>`... |
| Cable                | `component`, or an edge with `cables=edge`  | `W1`        | `<<Cable>>` |
| Assembly             | `rectangle` around its children             | `A5`        | `<<Assembly>>` |
| ConnectorPort        | `portin` / `portout` / `port`               | `A1_J2`     | gender, kind, and `<<NoMate>>` or `<<unmated>>` |
| ContactPort (`detail=contact`) | port inside a `group` named after the connector | `A1_J2_c1` | contact kind: `<<power>>`, `<<ground>>`, `<<signal>>`, `<<shield>>`, `<<nc>>` |
| Mate                 | `--` edge (`harness` with `detail=contact`) |             | `<<Mate>>`, plus `<<direct>>` when no Cable is involved |
| InterfaceLink        | `..>` edge labelled with the protocol       |             | `<<InterfaceLink>>` and the protocol, e.g. `<<can>>` |
| UnitLink             | `..>` edge between the two Units            |             | `<<UnitLink>>` and the protocol |

Aliases use the part name (or the path, joined by `_`, when a name repeats
inside different Assemblies), so the diagram can add notes, links or layout
hints after the import: `note right of A4 : ...`, `A1 -[hidden]- G1`.

Port sides follow the order the model writes connections in: in
`connect a to b`, `a` is an output and `b` an input. When every Mate of a
Cable points the same way, the first end stays as written and the others are
turned round, so a Cable always has ends on both sides.

Wires and Splices are not drawn: the Cable stands for them. Connections the
model cannot resolve are reported as `' sysml warning:` comments in the
generated source.

The system's attributes become variables: `$sysml_system`, and one
`$sysml_<attribute>` per attribute with a value (`$sysml_partNumber`,
`$sysml_revision`).

## Styling

Every element carries stereotypes, so `<style>` and `skinparam` blocks
written **after** the directive override the defaults:

```plantuml
!include_sysml system.sysml
<style>
component { .PCBAUnit { BackGroundColor #FAFAFA; LineColor #5B7A99 } }
port      { .unmated  { BackGroundColor #FF0000 } }
arrow     { .Cable    { LineColor #2E7D32 } .can { LineColor #E08214 } }
</style>
```

Use `style=false` to start from PlantUML's own look.

## Labels

Labels come from preprocessor functions. A function the diagram defines
**before** the directive replaces the default:

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_unit_label`      | `$designator, $type, $partNumber, $category, $details` | designator, type, part number |
| `$sysml_connector_label` | `$designator, $series, $gender, $kind, $partNumber`    | designator |
| `$sysml_contact_label`   | `$contactId, $signalName, $kind`                       | contact id and signal name |
| `$sysml_cable_label`     | `$designator, $type, $partNumber, $details`            | designator |
| `$sysml_link_label`      | `$protocol, $name`                                     | protocol |

`$details` holds the category-specific attributes, already formatted
(`24 V, 10 A`, `1500 mm, shielded`).

```plantuml
!function $sysml_connector_label($designator, $series, $gender, $kind, $partNumber)
!return $designator + "\n<size:8>" + $series + "</size>"
!endfunction
!include_sysml system.sysml
```

## How the SysML is read

`net.sourceforge.plantuml.sysml` holds a tolerant reader for the SysML v2
textual notation. It keeps what a diagram needs (packages, definitions,
usages, types, `:>` specialisations, `redefines`, bound values,
`connect a to b`, `doc`) and skips anything else by balancing brackets.
Usages are resolved through their definition's specialisation chain, then
the definition body, then the usage body, so pinouts defined once as port
definitions and refined per usage (`port redefines c5 { ... }`) come out as
written. It does not check the model: run the InterconnectProfile rule pack
for that.

`net.sourceforge.plantuml.sysml.interconnect` maps the result onto the
profile (`InterconnectModel`) and writes the diagram (`InterconnectPlantUml`).

## Trying it in the browser

The browser build ships `sysml.html`, a playground with three panes: SysML
files on the left (tabs; **Open…** or drop `.sysml` files on the pane), the
PlantUML they import into in the middle, and the diagram on the right. It
works for every profile of [SYSML_PROFILES.md](SYSML_PROFILES.md). The
toolbar follows the files: **root** lists every diagram root they hold, with
the profile that draws it (*first found* is the directive's own choice),
picking a tab (or opening a file) draws the first root declared in that
file, unless the diagram already shows one of its roots or the file declares
none. **profile** restricts the search to one profile or picks a view (its
*views* group: `view=definitions`, `view=requirements`), and only the options
of the profile or view that draws the root are shown. **ELK** writes
`layout=default` when it is off (it is hidden for sequence and timing
diagrams, which have no graph layout). Each profile keeps its own option
values, and an option left at its default is not written. The middle pane can
show:

* **generated source**: what `!include_sysml` expands to (as `-preproc`
  shows it), from the files of the left pane. Turn **auto** off to edit it
  without the next import overwriting it.
* **`!include_sysml` directive**: a diagram of your own around an
  `!include_sysml` line, which the toolbar keeps up to date: the files
  (a new tab joins the line at once) and the toolbar's options are rewritten,
  other `key=value` words typed on the line are kept. The page serves
  the left pane's files to the engine by name through `PLANTUML_FILE_LOADER`,
  so this runs the real directive, label functions and styling included.

The session (files, options, middle pane) is kept in `localStorage`; a
session saved before the toolbar followed the profile keeps its
InterconnectProfile options. The reset button brings back the sample model.

```
./teavm.sh      # then open http://localhost:8080/sysml.html
```

The generated source comes from `sysmlToPlantUml(names, texts, options,
onSuccess, onError)`, exported by `plantuml.js` next to `render`. It runs the
same import as the directive (`SysmlImport.importSysml`) and calls
one of the callbacks before it returns; `options` are the directive's
`key=value` words. The toolbar comes from `sysmlDescribe(names, texts,
options, onSuccess, onError)`, which delivers JSON: every root found
(`name`, `qualifiedName`, `profile`, and the `file` it is declared in), the index of the one the import would
draw with these `root=`/`profile=`/`view=` options (`selected`, -1 for
none), and every profile (`profiles`) and view (`views`) with whether
`layout=` applies (`graphLayout`) and its options and their allowed values,
the default first.
