package TestTiming1 {
  doc /* Board power-up sequence for PowerUpBoard_PCBA, drawn against
     * the MCU boot machine of McuBootStateMachine:
     *
     *   - the 3V3 LDO soft-starts 0.2-1.2 ms; its power-good enables
     *     the 1V8 LDO, which soft-starts 1.5-2.0 ms (core after I/O);
     *   - the supervisor holds nRESET low until 5.2 ms;
     *   - the MCU is Off until both rails are up, in Reset until
     *     nRESET rises, then boots and runs from 7.0 ms;
     *   - the 1 kHz SysTick runs once the MCU is Running.
     *
     * The rails and reset line reference the board's PowerRail / Net
     * connections; the MCU lifeline references the boot machine and
     * names its states. Four min/max delay constraints, two cause-
     * effect messages and one highlighted reset window.
     *
     * Requires PCBAProfile, StateMachineProfile and TimingProfile,
     * then PowerUpBoard_PCBA.sysml and McuBoot_StateMachine.sysml.
     * Expected to pass every TimingProfile rule. */

  private import TimingProfile::*;
  private import PowerUpBoard_PCBA::board;
  private import McuBootStateMachine::mcuBootSm;

  part powerUp : TimingDiagram {
    :>> name     = "Board power-up";
    :>> timeUnit = TimeUnit::ms;
    :>> endNs    = 10000000;

    part :>> lifelines {
      part r3v3 : Lifeline {
        :>> name      = "+3V3";
        :>> kind      = LifelineKind::analog;
        :>> minValue  = 0.0;
        :>> maxValue  = 3.6;
        :>> valueUnit = "V";
        :>> net       = board.P3V3;

        part :>> changes {
          part off       : StateChange { :>> atNs = 0;       :>> value = "0"; }
          part rampStart : StateChange { :>> atNs = 200000;  :>> value = "0"; }
          part good      : StateChange { :>> atNs = 1200000; :>> value = "3.3"; }
        }
      }

      part pg3v3 : Lifeline {
        :>> name = "PG_3V3";
        :>> kind = LifelineKind::binary;
        :>> net  = board.PG_3V3;

        part :>> changes {
          part low  : StateChange { :>> atNs = 0;       :>> value = "low"; }
          part high : StateChange { :>> atNs = 1400000; :>> value = "high"; }
        }
      }

      part r1v8 : Lifeline {
        :>> name      = "+1V8";
        :>> kind      = LifelineKind::analog;
        :>> minValue  = 0.0;
        :>> maxValue  = 2.0;
        :>> valueUnit = "V";
        :>> net       = board.P1V8;

        part :>> changes {
          part off       : StateChange { :>> atNs = 0;       :>> value = "0"; }
          part rampStart : StateChange { :>> atNs = 1500000; :>> value = "0"; }
          part good      : StateChange { :>> atNs = 2000000; :>> value = "1.8"; }
        }
      }

      part nreset : Lifeline {
        :>> name = "nRESET";
        :>> kind = LifelineKind::binary;
        :>> net  = board.NRESET;

        part :>> changes {
          part asserted : StateChange { :>> atNs = 0;       :>> value = "low"; }
          part released : StateChange { :>> atNs = 5200000; :>> value = "high"; }
        }
      }

      part mcu : Lifeline {
        :>> name    = "MCU";
        :>> kind    = LifelineKind::robust;
        :>> machine = mcuBootSm;

        part :>> changes {
          part off     : StateChange { :>> atNs = 0;       :>> machineState = mcuBootSm.top.off; }
          part reset   : StateChange { :>> atNs = 2000000; :>> machineState = mcuBootSm.top.inReset; }
          part rom     : StateChange { :>> atNs = 5200000; :>> machineState = mcuBootSm.top.romBoot; }
          part appInit : StateChange { :>> atNs = 5600000; :>> machineState = mcuBootSm.top.appInit; }
          part running : StateChange { :>> atNs = 7000000; :>> machineState = mcuBootSm.top.running; }
        }
      }

      part sysTick : Lifeline {
        :>> name     = "SysTick";
        :>> kind     = LifelineKind::clock;
        :>> periodNs = 1000000;
        :>> pulseNs  = 500000;
        :>> offsetNs = 7000000;
      }
    }

    part :>> constraints {
      part tSeq : TimingConstraint {
        :>> name       = "t_seq";
        :>> kind       = TimeConstraintKind::atLeast;
        :>> lowerNs    = 100000;
        :>> fromChange = powerUp.lifelines.r3v3.changes.good;
        :>> toChange   = powerUp.lifelines.r1v8.changes.rampStart;
      }
      part tSoftStart : TimingConstraint {
        :>> name       = "t_ss";
        :>> kind       = TimeConstraintKind::between;
        :>> lowerNs    = 300000;
        :>> upperNs    = 1000000;
        :>> fromChange = powerUp.lifelines.r1v8.changes.rampStart;
        :>> toChange   = powerUp.lifelines.r1v8.changes.good;
      }
      part tReset : TimingConstraint {
        :>> name       = "t_rst";
        :>> kind       = TimeConstraintKind::between;
        :>> lowerNs    = 2000000;
        :>> upperNs    = 5000000;
        :>> fromChange = powerUp.lifelines.r3v3.changes.good;
        :>> toChange   = powerUp.lifelines.nreset.changes.released;
        :>> drawnOn    = powerUp.lifelines.nreset;
      }
      part tBoot : TimingConstraint {
        :>> name       = "t_boot";
        :>> kind       = TimeConstraintKind::atMost;
        :>> upperNs    = 2000000;
        :>> fromChange = powerUp.lifelines.mcu.changes.rom;
        :>> toChange   = powerUp.lifelines.mcu.changes.running;
      }
    }

    part :>> messages {
      part enable1v8 : TimingMessage {
        :>> name      = "EN";
        :>> sender    = powerUp.lifelines.pg3v3;
        :>> sendNs    = 1400000;
        :>> receiver  = powerUp.lifelines.r1v8;
        :>> receiveNs = 1500000;
      }
      part resetRelease : TimingMessage {
        :>> name     = "resetRelease";
        :>> sender   = powerUp.lifelines.nreset;
        :>> sendNs   = 5200000;
        :>> receiver = powerUp.lifelines.mcu;
      }
    }

    part :>> highlights {
      part resetWindow : TimeHighlight {
        :>> name   = "reset held";
        :>> fromNs = 2000000;
        :>> toNs   = 5200000;
      }
    }
  }
}
