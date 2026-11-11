package 'VIEW-003' {
    private import 'TRK-BEH'::*;

    view <'VIEW-003'> 'Traces of the boil time' {
        @StrictDocWorkspace::DiagramView { options = "depth=2"; plantuml = ""; }
        expose 'SYS-001';
    }
}
