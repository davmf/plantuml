# Use cases, their actors and their requirements: `!include_sysml ... view=usecases`

`view=usecases` draws plain SysML v2 use cases (no profile needed) as a use
case diagram: the actors, the subject as the system boundary, the use cases a
use case includes, and its objective with the requirements it holds.

```sysml
package ClimateDefinitions {
	part def SmartHome { attribute currentTemp : ThermodynamicTemperatureValue; }
	part def HomeOwner;

	requirement def TemperatureRequirement {
		doc /* The room temperature shall stay below 25 degrees Celsius (298.15 K). */
		subject home : SmartHome;
		require constraint { home.currentTemp <= 298.15[K] }
	}

	use case def ManageClimate {
		subject home : SmartHome;
		actor owner : HomeOwner;
		objective climateGoal {
			doc /* Goal: Maintain safe and comfortable indoor conditions. */
			requirement tempLimit : TemperatureRequirement;
		}
	}
}
```

```plantuml
@startuml
!include_sysml climate.sysml view=usecases
@enduml
```

`view=usecases` may be left out here. When the files hold no root of any
profile, and neither `profile=` nor `view=` is given, plain SysML is drawn by
the first view that has something to draw: `usecases` when there is a use
case, else `requirements` when there is a requirement, else `definitions`.
The SysML Import Playground (`sysml.html`) does the same, so a model like the
one above is drawn as soon as it is pasted; its profile list offers
`view: usecases` and `view: requirements` to choose between them.

## Options

| Option         | Values                  | Default | Meaning |
|----------------|-------------------------|---------|---------|
| `requirements` | `all`, `linked`, `none` | `all`   | `all`: the objectives and every requirement of the root. `linked`: only the requirements the use cases hold (in their objective), with what those are defined by, specialise, derive from or hold. `none`: use cases and actors only |
| `text`         | `true`, `false`         | `true`  | Show the doc text (cut after 160 characters), the subject and the constraints in the requirement boxes |
| `roles`        | `false`, `true`         | `false` | Label each actor link with the name the use case gives the actor (`owner` for `actor owner : HomeOwner`) |
| `direction`    | `left`, `top`           | `left`  | `left to right direction` (actors on the left), or top to bottom |
| `layout`       | `elk`, `default`        | `elk`   | Common to every profile and view (see SYSML_PROFILES.md) |
| `root`         | package name            | the whole file set | Only the use cases and requirements of this package (any depth) |
| `style`        | `true`, `false`         | `true`  | Emit the default colours |

`frame=` is accepted (it is common to every profile) but has no effect.

## What is drawn

| SysML                                              | PlantUML |
|----------------------------------------------------|----------|
| `use case def U`, `use case u`                     | `usecase` labelled «use case def» / «use case», `<<useCaseDef>>` / `<<useCase>>` |
| `subject s : S;` in a use case                     | `rectangle "S" <<subject>> { ... }` around every use case with a subject of that type (of that name, without a type) |
| a use case without a subject                       | the subject of the use case it is defined by or specialises, else outside any rectangle |
| `actor a : A;`                                     | `actor "A" <<actor>>`, drawn once for every actor of that type (of that name, without a type), linked `A -- U <<actor>>` |
| `include use case x : X;`, `include X;`            | `U ..> X <<include>>` «include»; an X that names no use case is a dashed `<<unresolved>>` use case, with a warning |
| a use case nested in a use case                    | `Owner +-- Nested <<containment>>` |
| `use case u : U`, `use case def V :> U`, `use case v :> u` | `u ..\|> U` «defined by», `V --\|> U` «specializes», `v --\|> u` «subsets» |
| `objective name { doc ...; requirement r : R; }`   | `class` labelled «objective», the doc text below; `U +-- objective <<objective>>` and `objective +-- r <<containment>>` |
| `requirement`, `requirement def`, `concern`        | the box of the requirements view, with up to three compartments: `subject s : S`, the doc text, and one line per `require constraint { ... }` or `assume constraint { ... }` |
| relations between drawn requirements               | as in the requirements view: «defined by», «specializes», «subsets», «derive», «refine», «dependency», containment |

An included use case declared outside the `root=` package is drawn too.
An objective with no name, no doc and no drawn requirement is left out.
Unlike the requirements view, links are written from the client to the
supplier (`tempLimit ..|> TemperatureRequirement`), so that with the default
direction the diagram reads actors, use cases, objectives, requirements from
left to right.

`satisfy` and `verify` are not drawn here: use
[`view=requirements`](SYSML_VIEW_REQUIREMENTS.md) for the traces of a
requirement.

## Diagram form

A class diagram with `allowmixing`: use cases, actors and subject rectangles
are the elements of a use case diagram, objectives and requirements are
classes (for their compartments and `MaximumWidth` wrapping). Stereotypes are
hidden; a `<style>` block after the directive can use them
(`.useCaseDef`, `.objective`, `.requirementDef`, `arrow { .include { ... } }`).

Aliases are the short id or else the name, made into identifiers; a subject
is `subject_<label>` and an objective `<use case>_<objective name>`
(`ManageClimate_climateGoal`), so notes can be added after the directive:
`note bottom of ManageClimate : reviewed`.

| Function | Parameters | Default |
|----------|------------|---------|
| `$sysml_element_label`    | `$id, $name, $kind` | `«kind»`, the id in bold, the name |
| `$sysml_actor_label`      | `$name, $type`      | the type, else the name |
| `$sysml_subject_label`    | `$name, $type`      | the type, else the name |
| `$sysml_text_label`       | `$text, $id`        | the doc text |
| `$sysml_constraint_label` | `$text, $kind`      | `require { text }` / `assume { text }` |
| `$sysml_relation_label`   | `$kind`             | `«kind»` |

Generated for the model above (label functions and style left out):

```plantuml
' ---- !include_sysml climate.sysml :  (usecases) ----
!$sysml_root = ""
!pragma layout elk
allowmixing
left to right direction
hide circle
hide empty members
hide stereotype
actor "$sysml_actor_label("owner", "HomeOwner")" as HomeOwner <<actor>>
rectangle "$sysml_subject_label("home", "SmartHome")" as subject_SmartHome <<subject>> {
  usecase "$sysml_element_label("", "ManageClimate", "use case def")" as ManageClimate <<useCaseDef>>
}
class "$sysml_element_label("", "climateGoal", "objective")" as ManageClimate_climateGoal <<objective>> {
  {field} $sysml_text_label("Goal: Maintain safe and comfortable indoor conditions.", "")
}
class "$sysml_element_label("", "TemperatureRequirement", "requirement def")" as TemperatureRequirement <<requirementDef>> {
  {field} subject home : SmartHome
  --
  {field} $sysml_text_label("The room temperature shall stay below 25 degrees Celsius (298.15 K).", "")
  --
  {field} $sysml_constraint_label("home.currentTemp <= 298.15[K]", "require")
}
class "$sysml_element_label("", "tempLimit", "requirement")" as tempLimit <<requirement>>
HomeOwner -- ManageClimate <<actor>>
ManageClimate +-- ManageClimate_climateGoal <<objective>>
ManageClimate_climateGoal +-- tempLimit <<containment>>
tempLimit ..|> TemperatureRequirement <<definedBy>> : $sysml_relation_label("defined by")
' ---- end of !include_sysml climate.sysml ----
```

## Limits

* The doc text of a use case itself is not shown (an oval has no
  compartment).
* Actors are not inherited: a use case usage is linked to its definition, and
  the definition to its actors.
* Constraint expressions are shown as written, re-spaced; a
  `require constraint c : C;` that only names a constraint is not shown.
* Multiplicities of actors are not shown.

## Code

`net.sourceforge.plantuml.sysml.requirements`: `UseCaseView` (the view,
registered in `SysmlImport.VIEWS`) and `UseCaseWriter`, on the `ReqModel` of
the requirements view. The parser knows `include [use case]` and keeps the
text of the expressions in `require`/`assume`/`constraint` bodies
(`SysmlNode.getExpression`).

Regression tests: `src/test/resources/vega/sysml/views/usecases/`.
