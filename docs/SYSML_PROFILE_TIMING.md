# TimingProfile: timing diagrams from SysML v2

`TimingProfile` (in `sysmlv2/profiles/TimingProfile`) models a UML timing
diagram: lifelines whose value changes over time, with delay constraints,
messages and highlighted time ranges. `!include_sysml` draws a
`part x : TimingDiagram` as a PlantUML timing diagram (see
[SYSML_PROFILES.md](SYSML_PROFILES.md) for what is common to all profiles).

```plantuml
@startuml
!include_sysml power-up.sysml board.sysml mcu-boot.sysml
title $sysml_name
@enduml
```

List the board (PCBAProfile) and state machine (StateMachineProfile) files
the diagram references with it: their nets and states give the lifelines
their labels and the robust axis its states. The order does not matter, the
TimingDiagram is drawn.

## The model

| Element            | Features | Drawn as |
|--------------------|----------|----------|
| `TimingDiagram`    | `name`, `timeUnit` (`ns`, `us`, `ms`, `s`), `endNs` | the diagram; `time-axis unit`, an empty `@endNs` block |
| `Lifeline`         | `name`, `kind`, `minValue`/`maxValue`/`valueUnit` (analog), `periodNs`/`pulseNs`/`offsetNs` (clock), `net`, `machine`, `changes` | a player of its kind |
| `StateChange`      | `atNs`, `value`, `machineState` | `alias is value` in an `@t` block |
| `TimingConstraint` | `kind` (`atMost`, `atLeast`, `exactly`, `between`), `lowerNs`, `upperNs`, `fromChange`, `toChange`, `drawnOn` | `alias@t1 <-> @t2 : label` |
| `TimingMessage`    | `name`, `sender`, `sendNs`, `receiver`, `receiveNs` | `a@t1 -> b@t2 : label` |
| `TimeHighlight`    | `name`, `fromNs`, `toNs` | `highlight t1 to t2 : caption` |

All times are integer nanoseconds. `net` references a PCBAProfile `Net` or
`PowerRail` (power-up and reset sequencing: rails, nRESET, enables);
`machine` a StateMachineProfile `StateMachine`, whose states the lifeline's
changes then name through `machineState` (or by name in `value`).

## Mapping

* Lifelines are declared in model order as `robust`, `concise`, `binary`,
  `analog "…" between min and max` or
  `clock "…" with period p pulse w offset o`, aliased by their usage name
  and stereotyped with their kind, plus `<<net>>` or `<<machine>>` when
  they reference one.
* A robust lifeline declares its states with `has`, top to bottom: the
  states of its machine in machine order (only those the diagram uses;
  `states=all` lists every State, CompositeState and SubmachineState of the
  machine), otherwise in order of first use.
* StateChanges are grouped by time. Binary values `high`/`1` and `low`/`0`
  become `high`/`low`; analog values are written as numbers; concise
  values are quoted; braced values (`{-}`, `{?}`, `{...}`, `{hidden}`) are
  passed through.
* A TimingConstraint is drawn on `drawnOn`, by default on the lifeline of
  `fromChange`, from that change's time to `toChange`'s. Its bound reads
  `≤ 2 ms`, `≥ 100 µs`, `= 8 µs` or `300 µs – 1 ms`, each value in the
  largest unit that keeps it whole.
* Times are written in the display unit: `unit=` if given, else the
  diagram's `timeUnit`, else the largest unit the last change is at least
  one of. Fractions are kept (`@1.2` for 1 200 000 ns in ms).

| Option    | Values                           | Default | Meaning |
|-----------|----------------------------------|---------|---------|
| `unit`    | `model`, `ns`, `us`, `ms`, `s`   | `model` | Display unit of the time axis |
| `states`  | `used`, `all`                    | `used`  | Which machine states a robust lifeline lists |
| `compact` | `false`, `true`                  | `false` | `mode compact` |

`style=true` (default) colours `<<net>>` lines blue, `<<machine>>` lines
green, constraint arrows red and highlights pale yellow. `frame=` has no
effect on a timing diagram.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_lifeline_label`   | `$name, $kind, $ref, $unit` | name, `(unit)` for analog; `$ref` is the net's or machine's name |
| `$sysml_value_label`      | `$value, $kind`             | the value (concise values and robust states) |
| `$sysml_constraint_label` | `$name, $bound`             | `name {bound}` |
| `$sysml_message_label`    | `$name`                     | name |
| `$sysml_highlight_label`  | `$name`                     | name |

Besides `$sysml_root` and `$sysml_name`, `$sysml_timeUnit` holds the
model's unit and `$sysml_unit` the unit used.

Written as `' sysml warning:` comments: a change outside a lifeline or
without a value, a second change of a lifeline at the same time, a binary
or analog value that cannot be drawn, a clock without a period (not drawn)
or with changes (ignored), an unresolved machine or machine state, a value
that is not a state of the lifeline's machine, and a constraint or message
referring to something outside the diagram (not drawn).

## Example

The `test-timing-1.sysml` fixture (a board power-up: 3V3 then 1V8 rail,
PG_3V3, nRESET, the MCU boot machine and a SysTick clock) gives:

```plantuml
time-axis unit "ms"
analog "+3V3 (V)" between 0.0 and 3.6 as r3v3 <<analog>> <<net>>
binary "PG_3V3" as pg3v3 <<binary>> <<net>>
analog "+1V8 (V)" between 0.0 and 2.0 as r1v8 <<analog>> <<net>>
binary "nRESET" as nreset <<binary>> <<net>>
robust "MCU" as mcu <<robust>> <<machine>>
clock "SysTick" as sysTick with period 1 pulse 0.5 offset 7 <<clock>>
mcu has "Off" as Off
mcu has "Reset" as Reset
...
@1.2
r3v3 is 3.3
...
@5.2
nreset is high
mcu is ROM_boot
...
@10
r3v3@1.2 <-> @1.5 : t_seq {≥ 100 µs}
nreset@1.2 <-> @5.2 : t_rst {2 ms – 5 ms}
mcu@5.2 <-> @7 : t_boot {≤ 2 ms}
pg3v3@1.4 -> r1v8@1.5 : EN
nreset@5.2 -> mcu@5.2 : resetRelease
highlight 2 to 5.2 : reset held
```

(labels shown after preprocessing).

## Checking the model

The generator does not check the model. `scripts/timing_rules.py` in the
profile repository does, against the OMG pilot's export: clock parameters,
analog range, change times (unique per lifeline, within `endNs`), values
that fit the lifeline kind, nets only on binary or analog lifelines, machine
states that belong to the referenced machine, constraint bounds, constraints
the drawn waveform satisfies, references kept inside the diagram, message
causality and highlight ranges.

## Limits of PlantUML's timing syntax

* A clock runs from its offset to the end of the diagram: it cannot stop
  (an SPI SCK that only runs during a transfer is drawn running on).
* An analog lifeline is drawn as straight segments between changes; a step
  needs two changes close together.
* A constraint is drawn on one lifeline, even between changes of two.
* Messages and constraints are anonymous in PlantUML: the model's
  `TimeConstraintKind` survives only as the label text.
* Robust and concise lifelines carry no per-value colour or stereotype.
* Times are decimals in one unit; the axis has no logarithmic or broken
  scale beyond PlantUML's own `break`, which the profile does not model.

## Tests

`src/test/resources/vega/sysml/profiles/timing*.puml`: the power-up and SPI
fixtures (preproc), the options and label hooks, an SVG render with the
machine and board files listed first, and an error for an unknown `unit=`.
