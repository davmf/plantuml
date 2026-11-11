# Trackers, items and saved views: `!include_sysml` on a StrictDoc workspace

A StrictDoc workspace keeps a SysML v2 model the way Codebeamer keeps a
project: trackers, folders inside them, and items, **one `.sysml` file per
item**. `!include_sysml` reads this layout: the requirements view follows
trackers and folders, elements link to their items, and a saved view (a SysML
`view` item) is drawn with `diagram=`.

## The layout

Every file holds one top-level package named by an ID. A parent includes its
children with `public import '<ID>'::*;` (the model root:
`public import '<ID>';`):

```
package 'TRK-SYS' {                                  // tracker
    @StrictDocWorkspace::Tracker { title = "System requirements"; kind = "requirements"; prefix = "SYS"; }
    public import 'SYS-F-HEATING'::*;
}

package 'SYS-F-HEATING' {                            // folder
    @StrictDocWorkspace::Folder { title = "Heating"; }
    public import 'SYS-001'::*;
}

package 'SYS-001' {                                  // item
    private import 'TRK-STK'::*;
    requirement <'SYS-001'> 'Boil time' { ... }
    dependency from 'SYS-001' to 'STK-001';
}
```

A `private import` (an item using its tracker's names) is not an include.
The metadata definitions are in a library package of the workspace
(`StrictDocWorkspace`); `!include_sysml` reads the metadata by name and does
not need the library.

What the layout changes:

| | Without the layout | With it |
|---|---|---|
| `root=TRK-SYS` (requirements view) | what the package owns | what it includes, through folders |
| `packages=true` | the enclosing packages | the trackers and folders, titled from their `title` metadata |
| `focus=`, `root=` | short name, name or qualified name | also the item ID of an element without a short name (`part kettle` in `package 'BEH-001'` is `BEH-001`) |
| `links=/item/{id}` | elements with a short name | also items without one |

`scope=TRK-STK,TRK-SYS` restricts the requirements view to several packages.

## Saved views: `diagram=`

A view is an item that exposes what it draws, so one state machine can have
several views:

```
package 'VIEW-001' {
    private import 'TRK-BEH'::*;

    view <'VIEW-001'> 'Heating only' {
        @StrictDocWorkspace::DiagramView {
            options = "transitions=primary";
            plantuml = "note right of ramp : reviewed\nnote right of hold : ok";
        }
        expose 'BEH-001'::top::heating::**;
    }
}
```

```plantuml
@startuml
!include_sysml MODEL.sysml TRK-BEH.sysml BEH-001.sysml VIEW-001.sysml ... diagram=VIEW-001
title $sysml_viewName
@enduml
```

The view becomes ordinary options:

1. the words of `options` (`key=value`; `key="a b"` for a value with spaces);
2. from each `expose`, unless the options say it already (the first segment
   is an ID: a short name, or the name of a top-level package):

   | `expose` names | Options |
   |---|---|
   | a tracker, folder or other package | `view=requirements root=<package>` (several: `scope=`) |
   | a requirement, verification, use case... | `view=requirements focus=<ID>` |
   | an element a profile draws (a state machine...) | `root=<element>` |
   | a part of it (`'BEH-001'::top::heating`) | `root=<element> within=top::heating` |

3. the options written on the `!include_sysml` line, which win.

The lines of `plantuml` (separated by `\n`) are added after the generated
source: notes, styles, layout hints on the generated aliases. The view sets
`$sysml_view` (its ID) and `$sysml_viewName`.

An unknown view ID and an unknown `within=` path are errors.

## Many diagrams of one file set

A host that draws many diagrams of the same files (StrictDoc draws one per
item) hands over all the files each time. Each file is parsed once and kept
with its text; it is parsed again only when its text changes. In the browser
engine, a 91-file model takes about 360 ms for the first diagram and about
60 ms for each one after it.

## Code

`net.sourceforge.plantuml.sysml`: `SysmlWorkspace` (includes, item IDs,
titles, metadata), `SysmlDiagramView` (`diagram=`), the parse cache in
`SysmlImport`. Tests: `src/test/resources/vega/sysml/workspace/` and
`SysmlWorkspaceTest`.
