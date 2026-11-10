# `view=definitions`: SysML v2 definitions as a class diagram

`view=definitions` draws the definitions of any SysML v2 file set, written
with a profile or not, as a class diagram. It is only tried without being
asked for when the files hold no profile root, no use case and no requirement:

```plantuml
@startuml
!include_sysml vehicle.sysml view=definitions
@enduml
```

Run `java -jar plantuml.jar -preproc diagram.puml` to see the generated
PlantUML.

## Mapping

| SysML v2 | PlantUML |
|----------|----------|
| `part def`, `item def`, `port def`, `attribute def`, `action def`, `calc def`, `constraint def`, `requirement def`, `interface def`, `connection def`, `occurrence def`, `use case def`, `verification def`... | `class X <<part def>>`: the stereotype is the kind of definition |
| `abstract part def` | `abstract class` |
| `enum def` with `enum red;` (or `red;`) literals | `enum X <<enum def>>` listing the literals |
| `:>` / `specializes` a drawn definition | `General <\|-- Specific` |
| `:>` a definition that is not drawn | in the label: `Truck :> Hauler` |
| a definition nested in another | drawn next to it, linked `Owner +-- Nested` |
| `attribute x : T[m..n] = v` | field `x : T [m..n] = v` |
| `in`/`out`/`inout`/`return` parameters | fields, led by their direction: `in fuel : Fuel` |
| a `part`/`item`/`port`/`action`/`state`/... usage typed by a drawn definition | composition `Owner *-- "m..n" Type : name`; a usage other than `part` carries its keyword as a stereotype (`fuelIn <<port>>`) |
| `ref` usage (`ref part`, `ref item`, `ref x`), `subject`, `actor`, `stakeholder` typed by a drawn definition | association `Owner --> "m..n" Type : name` |
| the `end`s of a connection or interface definition typed by a drawn definition | one dependency per type, `Net ..> Pin : source, target` |
| a usage whose type is not drawn | field with its keyword: `part radio : Radio [0..1]` |
| a redefinition `:>> mass = 7500;` | field `:>> mass = 7500` |
| `package`, `library package` | `package X <<package>>` / `<<library package>>` frames, merged by qualified name across files |
| `doc` | nothing by default; `doc=tooltip` or `doc=note` |

Types keep their last segment only (`ISQ::MassValue` is `MassValue`). A
type that is not defined in the file set (`ScalarValues::String`,
`ISQ::MassValue`) stays as text in the field and gets no box; so does a
definition the options leave out. Default values are written as in the
model.

Ports are compositions, like parts, so that the port definitions they use sit
next to their owners; the `<<port>>` stereotype on the link tells them apart.
Attributes are always fields, even when typed by a drawn `enum def` or
`attribute def`.

Not drawn: connections (`connect a to b`), metadata annotations
(`@Reviewed { ... }`), and the relationship usages (`perform`, `exhibit`,
`satisfy`, `bind`, `succession`, `transition`...). Anonymous members are
skipped.

## Choosing what is drawn

Without `root=`, every definition of the file set is drawn. `root=` takes the
simple or qualified name of a package or of a definition:

* `root=Package`: the package, its sub-packages and their definitions; the
  package frame is the outermost one.
* `root=Definition`: the definition, the definitions nested in it, and its
  neighbours: its general types, the definitions that specialize it, the
  types of its usages, and the definitions that have a usage of it.

Library packages: when the scope (the file set, or the root) holds
definitions outside a `library package`, the definitions in library packages
are left out, so that a model listed with its libraries shows the model.
`libraries=true` draws them too. A file set made only of libraries, such as a
profile file on its own, is drawn whole.

## Options

The [common options](SYSML_PROFILES.md#common-options) apply (`root`,
`style`, `frame`); `profile=` cannot be combined with `view=`.

| Option       | Values                     | Default | Meaning |
|--------------|----------------------------|---------|---------|
| `attributes` | `true`, `false`            | `true`  | Draw attribute fields (enum literals are always drawn) |
| `usages`     | `true`, `false`            | `true`  | Draw the other usages: compositions, associations, ends, and the usage fields. `false` with `attributes=false` leaves the taxonomy |
| `packages`   | `true`, `false`            | `true`  | Draw package frames; `false` flattens |
| `libraries`  | `false`, `true`            | `false` | Draw library packages when other definitions are drawn (see above) |
| `doc`        | `none`, `tooltip`, `note`  | `none`  | Show each definition's `doc` as an SVG tooltip, or as a note above it |
| `direction`  | `top`, `left`              | `top`   | `left` lays the diagram out left to right |
| `style`      | `true`, `false`            | `true`  | A background colour per kind of definition (`skinparam class { BackgroundColor<<part def>> ... }`) and no visibility icons |
| `frame`      | `true`, `false`            | `false` | An outer `<<Frame>>` package named after the root |

`hide empty members` is always written, so definitions without fields are
plain boxes. A tooltip is a PlantUML link with an empty target: clicking it
reloads the page, which is why it is not the default.

## Labels and names

Aliases are the definition names (sanitized; a second definition with the
same name gets `_2`), so notes, layout hints and `hide`/`remove` can be
written after the directive. Packages get their name as alias unless a
definition already has it.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_def_label`     | `$name, $kind, $general`                                         | name, then ` :> general` for undrawn general types |
| `$sysml_feature_label` | `$keyword, $direction, $name, $type, $multiplicity, $value`      | `direction keyword name : type [m..n] = value` |
| `$sysml_literal_label` | `$name`                                                          | name |
| `$sysml_usage_label`   | `$keyword, $name`                                                | the usage name (the end names for `..>`) |
| `$sysml_package_label` | `$name, $kind`                                                   | name |
| `$sysml_doc_label`     | `$name, $doc`                                                    | the doc (with `doc=note`) |

`$keyword` is empty for attributes, `:>>` is added for a redefinition. Each
field and literal is written with `{field}`, so that a label with
parentheses (`return : Real = f(x)`) is not taken for a method.

`$sysml_root` and `$sysml_name` hold the root's name; for the whole file set,
the name of its only top-level package, or `model`.

## Example

```sysml
package Shop {
  enum def Size { enum small; enum large; }
  abstract part def Item {
    attribute price : Real;
  }
  part def Cup :> Item {
    attribute size : Size = Size::small;
    port handle : Grip[0..1];
  }
  port def Grip;
}
```

becomes (with `style=false`):

```plantuml
hide empty members
package "Shop" as Shop <<package>> {
  enum "Size" as Size <<enum def>> {
    {field} small
    {field} large
  }
  abstract class "Item" as Item <<part def>> {
    {field} price : Real
  }
  class "Cup" as Cup <<part def>> {
    {field} size : Size = Size::small
  }
  class "Grip" as Grip <<port def>>
}
Item <|-- Cup
Cup *-- "0..1" Grip : handle <<port>>
```

(the labels are calls to the functions above; `-preproc` shows them
expanded).

## Code and tests

`net.sourceforge.plantuml.sysml.definitions`: `DefProfile` (registered in
`SysmlImport.VIEWS`; its roots are the whole file set, then every package and
definition) and `DefWriter`. The parser keeps each member's multiplicity and
modifiers (`abstract`, `ref`, `in`, `out`, `end`, `return`, `@` for a metadata
usage) for it.

Regression tests: `src/test/resources/vega/sysml/views/definitions/`, on the
synthetic `vehicle.sysml`.
