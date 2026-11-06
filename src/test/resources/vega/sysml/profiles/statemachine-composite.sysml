package TestStateMachine5 {
  doc /* YASMI sample 5: two composite states wired into a cycle. The
     * second is orthogonal — its body has two parallel regions
     * separated by '||' in YASMI, modelled here as
     * CompositeState.regions[2..*]. Each region runs its own initial
     * -> NewState -> NewState -> final pipeline concurrently.
     *
     * Mirrors state_machines.puml lines 75-109.
     */

  private import StateMachineProfile::*;

  part sm : StateMachine {
    :>> name = "Test State Machine 5";

    item :>> events {
      item event1 : Event { :>> name = "event_1"; }
      item event2 : Event { :>> name = "event_2"; }
      item event3 : Event { :>> name = "event_3"; }
    }

    action :>> actions {
      action entryActionsImpl : Action { :>> name = "entry_actions()"; }
      action doActionsImpl    : Action { :>> name = "do_actions()"; }
      action exitActionsImpl  : Action { :>> name = "exit_actions()"; }
    }

    part :>> top : Region {
      :>> name = "top";

      part outerInit : Pseudostate {
        :>> name = "outerInit";
        :>> kind = PseudostateKind::initial;
      }

      part newCompositeState1 : CompositeState {
        :>> name         = "NewCompositeState1";
        :>> entryActions = (sm.actions.entryActionsImpl);
        :>> doActions    = (sm.actions.doActionsImpl);
        :>> exitActions  = (sm.actions.exitActionsImpl);

        part :>> regions {
          part inner : Region {
            :>> name = "inner1";

            part init : Pseudostate { :>> name = "init1";  :>> kind = PseudostateKind::initial; }
            part fin  : Pseudostate { :>> name = "final1"; :>> kind = PseudostateKind::final;   }

            part newState1 : State {
              :>> name         = "NewState1";
              :>> entryActions = (sm.actions.entryActionsImpl);
              :>> doActions    = (sm.actions.doActionsImpl);
              :>> exitActions  = (sm.actions.exitActionsImpl);
            }

            part newState2 : State {
              :>> name         = "NewState2";
              :>> entryActions = (sm.actions.entryActionsImpl);
              :>> doActions    = (sm.actions.doActionsImpl);
              :>> exitActions  = (sm.actions.exitActionsImpl);
            }

            connection initToNewState1 : Transition connect init.outgoing to newState1.incoming {
              :>> name           = "init -> NewState1";
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }

            connection newState1ToNewState2 : Transition connect newState1.outgoing to newState2.incoming {
              :>> name           = "NewState1 -> NewState2";
              :>> trigger        = sm.events.event1;
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }

            connection newState2ToFinal : Transition connect newState2.outgoing to fin.incoming {
              :>> name           = "NewState2 -> final";
              :>> trigger        = sm.events.event1;
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }
          }
        }
      }

      part newCompositeState2 : CompositeState {
        doc /* Orthogonal: two parallel regions execute concurrently
           * once the composite is entered. */
        :>> name         = "NewCompositeState2";
        :>> entryActions = (sm.actions.entryActionsImpl);
        :>> doActions    = (sm.actions.doActionsImpl);
        :>> exitActions  = (sm.actions.exitActionsImpl);

        part :>> regions {
          part regionA : Region {
            :>> name = "regionA";

            part init : Pseudostate { :>> name = "initA";  :>> kind = PseudostateKind::initial; }
            part fin  : Pseudostate { :>> name = "finalA"; :>> kind = PseudostateKind::final;   }

            part newState3 : State {
              :>> name         = "NewState3";
              :>> entryActions = (sm.actions.entryActionsImpl);
              :>> doActions    = (sm.actions.doActionsImpl);
              :>> exitActions  = (sm.actions.exitActionsImpl);
            }

            part newState4 : State {
              :>> name         = "NewState4";
              :>> entryActions = (sm.actions.entryActionsImpl);
              :>> doActions    = (sm.actions.doActionsImpl);
              :>> exitActions  = (sm.actions.exitActionsImpl);
            }

            connection initToNewState3 : Transition connect init.outgoing to newState3.incoming {
              :>> name           = "initA -> NewState3";
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }

            connection newState3ToNewState4 : Transition connect newState3.outgoing to newState4.incoming {
              :>> name           = "NewState3 -> NewState4";
              :>> trigger        = sm.events.event1;
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }

            connection newState4ToFinal : Transition connect newState4.outgoing to fin.incoming {
              :>> name           = "NewState4 -> finalA";
              :>> trigger        = sm.events.event1;
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }
          }

          part regionB : Region {
            :>> name = "regionB";

            part init : Pseudostate { :>> name = "initB";  :>> kind = PseudostateKind::initial; }
            part fin  : Pseudostate { :>> name = "finalB"; :>> kind = PseudostateKind::final;   }

            part newState5 : State {
              :>> name         = "NewState5";
              :>> entryActions = (sm.actions.entryActionsImpl);
              :>> doActions    = (sm.actions.doActionsImpl);
              :>> exitActions  = (sm.actions.exitActionsImpl);
            }

            part newState6 : State {
              :>> name         = "NewState6";
              :>> entryActions = (sm.actions.entryActionsImpl);
              :>> doActions    = (sm.actions.doActionsImpl);
              :>> exitActions  = (sm.actions.exitActionsImpl);
            }

            connection initToNewState5 : Transition connect init.outgoing to newState5.incoming {
              :>> name           = "initB -> NewState5";
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }

            connection newState5ToNewState6 : Transition connect newState5.outgoing to newState6.incoming {
              :>> name           = "NewState5 -> NewState6";
              :>> trigger        = sm.events.event3;
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }

            connection newState6ToFinal : Transition connect newState6.outgoing to fin.incoming {
              :>> name           = "NewState6 -> finalB";
              :>> trigger        = sm.events.event3;
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::unclassified;
            }
          }
        }
      }

      connection outerInitToCS1 : Transition connect outerInit.outgoing to newCompositeState1.incoming {
        :>> name           = "outerInit -> NewCompositeState1";
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::unclassified;
      }

      connection cs1ToCS2Default : Transition connect newCompositeState1.outgoing to newCompositeState2.incoming {
        :>> name           = "NewCompositeState1 -> NewCompositeState2 (default)";
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::unclassified;
      }

      connection cs1ToCS2OnEvent : Transition connect newCompositeState1.outgoing to newCompositeState2.incoming {
        :>> name           = "NewCompositeState1 -> NewCompositeState2 (event_2)";
        :>> trigger        = sm.events.event2;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::unclassified;
      }

      connection cs2ToCS1Default : Transition connect newCompositeState2.outgoing to newCompositeState1.incoming {
        :>> name           = "NewCompositeState2 -> NewCompositeState1 (default)";
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::unclassified;
      }

      connection cs2ToCS1OnEvent : Transition connect newCompositeState2.outgoing to newCompositeState1.incoming {
        :>> name           = "NewCompositeState2 -> NewCompositeState1 (event_2)";
        :>> trigger        = sm.events.event2;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::unclassified;
      }
    }
  }
}
