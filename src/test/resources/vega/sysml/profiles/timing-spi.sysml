package TestTiming2 {
  doc /* SPI register read, without cross-profile references: the
     * host selects the sensor, clocks out a read command for
     * WHO_AM_I and clocks the answer back in.
     *
     * Features exercised:
     *   - a robust lifeline with plain string states (no machine);
     *   - concise bus lifelines with the special levels {-} (high
     *     impedance) and {?} (unknown);
     *   - binary chip select, a clock with an offset;
     *   - atLeast and exactly constraints, a message between two
     *     lifelines at the same instant, a highlighted transfer;
     *   - lifelines declared in a different order from their names'
     *     alphabetical order (model order is kept).
     *
     * Requires PCBAProfile, StateMachineProfile and TimingProfile.
     * Expected to pass every TimingProfile rule. */

  private import TimingProfile::*;

  part spiRead : TimingDiagram {
    :>> name     = "SPI read of WHO_AM_I";
    :>> timeUnit = TimeUnit::us;

    part :>> lifelines {
      part host : Lifeline {
        :>> name = "Host";
        :>> kind = LifelineKind::robust;

        part :>> changes {
          part idle1   : StateChange { :>> atNs = 0;     :>> value = "Idle"; }
          part select  : StateChange { :>> atNs = 1000;  :>> value = "Select"; }
          part command : StateChange { :>> atNs = 2000;  :>> value = "Command"; }
          part read    : StateChange { :>> atNs = 10000; :>> value = "Read"; }
          part idle2   : StateChange { :>> atNs = 18500; :>> value = "Idle"; }
        }
      }

      part ncs : Lifeline {
        :>> name = "nCS";
        :>> kind = LifelineKind::binary;

        part :>> changes {
          part deselected : StateChange { :>> atNs = 0;     :>> value = "1"; }
          part selected   : StateChange { :>> atNs = 1000;  :>> value = "0"; }
          part released   : StateChange { :>> atNs = 18500; :>> value = "1"; }
        }
      }

      part sck : Lifeline {
        :>> name     = "SCK";
        :>> kind     = LifelineKind::clock;
        :>> periodNs = 1000;
        :>> offsetNs = 2000;
      }

      part mosi : Lifeline {
        :>> name = "MOSI";
        :>> kind = LifelineKind::concise;

        part :>> changes {
          part idle    : StateChange { :>> atNs = 0;     :>> value = "{-}"; }
          part cmd     : StateChange { :>> atNs = 2000;  :>> value = "0x8F"; }
          part dummy   : StateChange { :>> atNs = 10000; :>> value = "{?}"; }
          part idleEnd : StateChange { :>> atNs = 18000; :>> value = "{-}"; }
        }
      }

      part miso : Lifeline {
        :>> name = "MISO";
        :>> kind = LifelineKind::concise;

        part :>> changes {
          part hiZ    : StateChange { :>> atNs = 0;     :>> value = "{-}"; }
          part driven : StateChange { :>> atNs = 1000;  :>> value = "{?}"; }
          part data   : StateChange { :>> atNs = 10000; :>> value = "0x33"; }
          part hiZEnd : StateChange { :>> atNs = 18500; :>> value = "{-}"; }
        }
      }
    }

    part :>> constraints {
      part tCss : TimingConstraint {
        :>> name       = "t_CSS";
        :>> kind       = TimeConstraintKind::atLeast;
        :>> lowerNs    = 500;
        :>> fromChange = spiRead.lifelines.ncs.changes.selected;
        :>> toChange   = spiRead.lifelines.mosi.changes.cmd;
        :>> drawnOn    = spiRead.lifelines.mosi;
      }
      part tCmd : TimingConstraint {
        :>> name       = "8 clocks";
        :>> kind       = TimeConstraintKind::exactly;
        :>> lowerNs    = 8000;
        :>> fromChange = spiRead.lifelines.mosi.changes.cmd;
        :>> toChange   = spiRead.lifelines.miso.changes.data;
      }
    }

    part :>> messages {
      part selectMsg : TimingMessage {
        :>> name     = "select";
        :>> sender   = spiRead.lifelines.host;
        :>> sendNs   = 1000;
        :>> receiver = spiRead.lifelines.ncs;
      }
    }

    part :>> highlights {
      part transfer : TimeHighlight {
        :>> name   = "transfer";
        :>> fromNs = 2000;
        :>> toNs   = 18000;
      }
    }
  }
}
