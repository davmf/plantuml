# Diagrams from SysML v2 profiles: `!include_sysml`

`!include_sysml` reads SysML v2 files written with one of the profiles in
`sysmlv2/profiles` and runs the PlantUML generated from them where the
directive appears, as if it had been `!include`d. It finds the diagram root,
picks the profile from its type, and writes the kind of diagram that profile
describes:

| Profile                     | Root                                    | `profile=`     | Diagram |
|-----------------------------|-----------------------------------------|----------------|---------|
| TimingProfile               | `part x : TimingDiagram`                | `timing`       | timing ([details](SYSML_PROFILE_TIMING.md)) |
| InterconnectProfile         | `part x : InterconnectSystem`           | `interconnect` | component ([details](SYSML_INTERCONNECT.md)) |
| InterPCBAProfile            | `part x : System`                       | `interpcba`    | component: boards, COTS devices, cables |
| StateMachineProfile         | `part x : StateMachine`                 | `statemachine` | state |
| SequenceProfile             | `part x : Interaction`                  | `sequence`     | sequence |
| ActivityProfile             | `part x : Activity`                     | `activity`     | component-style activity graph |
| SoftwareArchitectureProfile | a part or `part def` with `Layer` parts | `swarch`       | component, one frame per layer |
| PCBAProfile                 | `part x : PCBA`                         | `pcba`         | component: parts, pins, nets |
| COTSDeviceProfile           | `part x : COTSDevice`, or `part def X :> COTSDevice` | `cots` | one device as a black box |

```plantuml
@startuml
!include_sysml charge-post.sysml authorise-driver.sysml
title $sysml_name
@enduml
```

Run `java -jar plantuml.jar -preproc diagram.puml` to see the generated
PlantUML.

## Choosing the root

Without options, the profiles are tried in the order of the table and the
first root found is drawn. System-level profiles come first, because their
file sets often hold the parts they use: an InterconnectSystem listed with
the PCBA its unit is bound to draws the system, not the board. TimingProfile
is tried before all of them: no other root contains a TimingDiagram, while a
timing diagram's file set holds the board and the state machines its
lifelines reference. Two cases need no option either:

* state machines: a machine that a `SubmachineState` refers to comes after
  the machines that use it, so listing a machine with its sub-machines, in
  any order, draws the machine;
* software architecture: a `part def` holding Layers is a root unless a
  top-level part is typed by it (then the part is the root).

`root=name` (simple or qualified name) picks any root; `system=` is the same
option, kept for InterconnectProfile diagrams. `profile=` restricts the search
to one profile.

## Common options

| Option    | Values                 | Default     | Meaning |
|-----------|------------------------|-------------|---------|
| `root`    | name                   | first found | The diagram root (`system` is an alias) |
| `profile` | see the table above    | detected    | Only look for roots of this profile |
| `style`   | `true`, `false`        | `true`      | Emit the default skinparams and `<style>` |
| `frame`   | `true`, `false`        | `false`     | Draw the root itself as an outer frame |

An option the selected profile does not know is an error. As for
InterconnectProfile, every element carries stereotypes (style them in a
`<style>` block written after the directive), every label is a preprocessor
function that a diagram can define before the directive to replace the
default, and aliases are the usage names, so notes and layout hints can be
added after the import.

Every profile sets `$sysml_root` (the root's usage name) and one
`$sysml_<attribute>` per attribute of the root (`$sysml_name`,
`$sysml_partNumber`, `$sysml_revision`...).

Problems that do not stop the diagram (a reference that names nothing, a
pseudostate without a kind) are written as `' sysml warning:` comments. The
generators do not check the model: run the profile's rule pack for that.

## StateMachineProfile

* States, CompositeStates and SubmachineStates keep the model's
  containment; each Region of a CompositeState is a block separated by `--`
  (`regions=vertical`: `||`). Entry, do and exit actions are listed in the
  state (`actions=false` hides them).
* Pseudostates use PlantUML's stereotypes: `<<start>>`, `<<end>>`,
  `<<choice>>`, `<<junction>>`, `<<fork>>`, `<<join>>`, `<<history>>`,
  `<<history*>>`, `<<entryPoint>>`, `<<exitPoint>>`. A terminate
  pseudostate is a `<<terminate>>` state drawn as a red cross.
* A SubmachineState is collapsed, `<<submachine>>`, labelled
  `Name : SubMachine`, with its entry and exit points on its boundary. A
  machine whose top Region has entry or exit points is framed, so they sit
  on its border.
* Transitions are labelled `trigger(payload) [guard] / action; action` and
  carry their classification as a stereotype: `<<primary>>` (green, thick),
  `<<secondary>>` (blue), `<<exception>>` (red, thin), `<<unclassified>>`.
  An internal transition is listed inside its state. Transitions are
  written inside the Region that declares them (PlantUML only links into a
  concurrent region from inside it).

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_state_label`      | `$name, $category, $submachine`     | name, `: submachine` |
| `$sysml_transition_label` | `$trigger, $guard, $actions, $name` | `trigger [guard] / actions` |

## SequenceProfile

* Lifelines are participants (`actor` for an `actorParticipant`), labelled
  `name : Class`; a Lifeline created by a `createMessage` is declared where
  it is created.
* Messages are sorted by `order`. Arrows follow the kind: synchronous `->`,
  asynchronous `->>`, reply `-->`, create, destroy (`!!`), found (`[o->`),
  lost (`->o]`); a GateMessage comes in from the frame (`[->`).
* A CombinedFragment spans the messages its operands list: `alt`/`else`,
  `opt`, `loop`, `par`, `break`, `critical`, or `group <operator>` for the
  others. Operands are ordered by `order`, the guard follows the keyword.
* ExecutionSpecifications are `activate`/`deactivate` after their start and
  finish messages; a message's TimeObservation or DurationObservation is a
  note (`≤ 500 ms`); StateInvariants are `hnote`s at the end of their
  lifeline (the profile does not place them in time).
* `autonumber=true` numbers the messages.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_lifeline_label`  | `$name, $class`                         | `name : class` |
| `$sysml_message_label`   | `$signature, $arguments, $kind, $name`  | the signature; a reply shows its arguments |
| `$sysml_time_label`      | `$name, $bound`                         | the bound |
| `$sysml_invariant_label` | `$expression`                           | `{expression}` |

## ActivityProfile

The profile joins nodes by arbitrary edges and has no initial, decision or
merge nodes, which PlantUML's activity syntax (structured control flow) cannot
express. The activity is drawn as a graph instead:

* an Action is a rounded `component <<Action>>`, its Pins `portin`/`portout`;
* an ObjectNode is a `rectangle <<ObjectNode>>` labelled `name : type`;
* Partitions are frames around the nodes they reference (nested
  sub-partitions too); an ExpansionRegion is a dashed frame whose input and
  output collections are ports on its boundary (edges to the region attach
  to them) and that holds its per-iteration nodes; an InterruptibleRegion
  is a red dashed frame. A region whose contents share a partition is drawn
  in it;
* edges carry their kind: `<<controlFlow>>`, `<<objectFlow>>` (dashed),
  `<<interruptFlow>>` (red, labelled ↯), plus `[guard]` and `{weight=n}`.

`direction=left` lays the graph out left to right.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_action_label`    | `$name`                                | name |
| `$sysml_object_label`    | `$name, $typeName, $ordering, $upper`  | `name : type` |
| `$sysml_pin_label`       | `$name, $direction`                    | name |
| `$sysml_partition_label` | `$name, $represents`                   | name |
| `$sysml_region_label`    | `$name, $category, $mode`              | name and expansion mode |
| `$sysml_edge_label`      | `$kind, $guard, $weight, $name`        | ↯ `[guard]` `{weight=n}` |
| `$sysml_activity_label`  | `$name`                                | name (with `frame=true`) |

## SoftwareArchitectureProfile

Each Layer is a `rectangle <<Layer>> <<kind>>` holding its components.
Layers are stacked by kind, application and service on top and hardware at
the bottom (`order=model` keeps the model's order). SignalFlows are solid
arrows; two opposite flows between `signalsBidir` ports are one `<-->`
(`<<bidir>>`). Dependencies are dashed `..>`.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_layer_label`     | `$name, $kind`     | name |
| `$sysml_component_label` | `$name, $type`     | name |
| `$sysml_flow_label`      | `$name, $category` | the connection's `name` (only drawn when set) |

## PCBAProfile

* Components are `component`s stereotyped with their category (`<<IC>>`,
  `<<Connector>>`, `<<PowerComponent>>`...), labelled with the designator
  and the type. Subcircuits are dashed rectangles around their children.
* Each SinglePinPort or PowerPort is a port: `portin` for an input or a
  sink, `portout` for an output or a source, stereotyped with the port type
  and role.
* Each connection is an edge stereotyped with its type (`<<Net>>`,
  `<<PowerRail>>`, `<<BusHarness>>`...) and labelled with its name
  (`labels=none` drops the labels). A NoConnect marks its pin `<<nc>>`
  (grey) and draws nothing; a pin with no connection at all is
  `<<unconnected>>` (red), the profile's Rule 1.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_component_label` | `$designator, $type, $category`  | designator and type |
| `$sysml_pin_label`       | `$name, $direction, $role`       | name |
| `$sysml_net_label`       | `$name, $type`                   | name |
| `$sysml_board_label`     | `$name, $partNumber, $revision`  | with `frame=true` |

## InterPCBAProfile and COTSDeviceProfile

* Each SystemNode is a component: `<<SystemPCBA>>` (labelled with its
  refDesignator, type and its board's part number) or `<<COTSDevice>>`
  (manufacturer and model). Its InterBoardPorts are ports, as for PCBAs. A
  COTSDevice's ports are grouped by the ExternalConnector that lists them,
  carry their Channel's kind as a stereotype (`channels=false` drops it),
  and its InternalBlocks are drawn inside it.
* `detail=conductor` (default): one edge per ConductorPath between the two
  ports, stereotyped `<<ConductorPath>>` and with the conductor kind
  (`<<signal>>`, `<<power>>`, `<<ground>>`, `<<shield>>`).
  `detail=cable`: one edge per Interconnect between the two nodes, labelled
  with the cable and stereotyped with its form. `detail=harness`: a
  `harness` block per Interconnect bundling its conductors. An Interconnect
  without ConductorPaths is always drawn as a cable edge.
* A node an Interconnect reaches outside the System (Rule 1) is drawn
  `<<external>>` (red dashed) with a warning; a boundary port no conductor
  uses is `<<unconnected>>`.
* `$sysml_system` holds the System's `name`.

`profile=cots` draws one COTSDevice on its own; a catalogue file with no
usage is fine, since its `part def X :> COTSDevice` definitions are roots too.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_node_label`      | `$designator, $type, $category, $details` | designator, type, details |
| `$sysml_port_label`      | `$name, $direction, $role`                | the port's `name` attribute |
| `$sysml_connector_label` | `$label, $kind`                           | label |
| `$sysml_block_label`     | `$name, $note`                            | name |
| `$sysml_cable_label`     | `$name, $partNumber, $form, $conductors`  | name and part number |
| `$sysml_conductor_label` | `$label, $kind, $gauge, $cable`           | label |
| `$sysml_system_label`    | `$name, $revision`                        | with `frame=true` |

Note: PlantUML currently drops the label of a link between two ports, so
conductor labels only show with `detail=cable`.

## Code

`net.sourceforge.plantuml.sysml.SysmlImport` parses the files and picks the
profile; each profile implements `SysmlProfile` in its own package
(`timing`, `statemachine`, `sequence`, `activity`, `swarch`, `pcba`,
`interpcba`, `interconnect`). Shared pieces: `SysmlNodes` (values set in a usage body, or
its type's), `SysmlScope` (resolves feature chains such as
`top.idle.outgoing` to elements), `SysmlWriter` (header, label functions,
warnings) and `SysmlOptions`.

Regression tests: `src/test/resources/vega/sysml/profiles/`, one `.puml`
per case next to copies of the profile fixtures.
