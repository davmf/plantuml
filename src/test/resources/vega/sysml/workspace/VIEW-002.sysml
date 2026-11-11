package 'VIEW-002' {
    private import 'TRK-BEH'::*;

    view <'VIEW-002'> 'Needs and requirements' {
        @StrictDocWorkspace::DiagramView { options = "packages=true text=false"; plantuml = ""; }
        expose 'TRK-STK'::**;
        expose 'TRK-SYS'::**;
    }
}
