package McuBootStateMachine {
  doc /* StateMachineProfile model of the MCU's boot: off until its
     * rails are up, held in reset by the supervisor, then ROM boot,
     * application init and run; a watchdog bite ends in Fault, a
     * state the power-up never reaches. test-timing-1.sysml draws this
     * machine's state over time (Rule 7 of TimingProfile checks the
     * timing diagram only names these states). */

  private import StateMachineProfile::*;

  part mcuBootSm : StateMachine {
    :>> name = "MCU boot";

    item :>> events {
      item railsUp      : Event { :>> name = "railsUp"; }
      item resetRelease : Event { :>> name = "resetRelease"; }
      item romDone      : Event { :>> name = "romDone"; }
      item initDone     : Event { :>> name = "initDone"; }
      item brownOut     : Event { :>> name = "brownOut"; }
      item watchdog     : Event { :>> name = "watchdog"; }
    }

    part :>> top : Region {
      :>> name = "top";

      part init : Pseudostate {
        :>> name = "init";
        :>> kind = PseudostateKind::initial;
      }

      part off      : State { :>> name = "Off"; }
      part inReset  : State { :>> name = "Reset"; }
      part romBoot  : State { :>> name = "ROM boot"; }
      part appInit  : State { :>> name = "App init"; }
      part running  : State { :>> name = "Running"; }
      part fault    : State { :>> name = "Fault"; }

      connection initToOff : Transition connect init.outgoing to off.incoming {
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::unclassified;
      }
      connection offToReset : Transition connect off.outgoing to inReset.incoming {
        :>> trigger        = mcuBootSm.events.railsUp;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection resetToRom : Transition connect inReset.outgoing to romBoot.incoming {
        :>> trigger        = mcuBootSm.events.resetRelease;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection romToInit : Transition connect romBoot.outgoing to appInit.incoming {
        :>> trigger        = mcuBootSm.events.romDone;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection initToRunning : Transition connect appInit.outgoing to running.incoming {
        :>> trigger        = mcuBootSm.events.initDone;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection runningToReset : Transition connect running.outgoing to inReset.incoming {
        :>> trigger        = mcuBootSm.events.brownOut;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
      connection runningToFault : Transition connect running.outgoing to fault.incoming {
        :>> trigger        = mcuBootSm.events.watchdog;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
    }
  }
}
