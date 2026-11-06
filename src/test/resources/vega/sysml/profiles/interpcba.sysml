package Rule1_EndpointReachability_Test {
  doc /* Structural test fixture for InterPCBAProfile.
     *
     * Two Systems in this file:
     *   GoodSystem - two PCBAs joined by one multi-conductor cable
     *                and one single-wire jumper. Every conductor on
     *                each cable is mapped by exactly one ConductorPath,
     *                and every ConductorPath endpoint resolves to a
     *                boundary port on a SystemPCBA that the System
     *                owns.
     *   BadSystem  - same physical layout, but the jumper terminates
     *                on a SystemPCBA (A3) that is NOT listed in the
     *                System's 'pcbas'. Rule 1 must reject this once
     *                the external rule pack is active.
     *
     * As with the PCBAProfile fixtures, the rule defs in
     * InterPCBAProfile are currently doc-only, so both Systems parse
     * and type-check today. The rule pack distinguishes them. */

  private import InterPCBAProfile::*;
  private import PCBAProfile::*;

  // -----------------------------------------------------------------
  // Two tiny PCBAs, each with a single 2-pin off-board connector.
  // The on-board details are deliberately thin - the test cares about
  // what happens above the boundary, not inside the boards.
  // -----------------------------------------------------------------

  part def TinyHostBoard :> PCBA {
    attribute redefines partNumber = "HOST-001";
    attribute redefines revision   = "A";

    part J1 : Connector {
      attribute redefines designator = "J1";
    }
  }

  part def TinyPeripheralBoard :> PCBA {
    attribute redefines partNumber = "PERIPH-001";
    attribute redefines revision   = "A";

    part J1 : Connector {
      attribute redefines designator = "J1";
    }
  }

  // -----------------------------------------------------------------
  // Reusable SystemPCBA wrappers. Each exposes two boundary ports:
  //   sig  - signal pin (J1.1)
  //   pwr  - 5V power  (J1.2)
  // -----------------------------------------------------------------

  part def HostNode :> SystemPCBA {
    part redefines board : TinyHostBoard;

    port sig : InterBoardSignalPort {
      attribute redefines name = "J1.1";
      attribute redefines direction = SinglePinDirection::output;
      attribute redefines role      = SinglePinRole::data;
    }
    port pwr : InterBoardPowerPort {
      attribute redefines name = "J1.2";
      attribute redefines direction = PowerDirection::source;
      attribute redefines role      = PowerRole::rail;
    }
  }

  part def PeripheralNode :> SystemPCBA {
    part redefines board : TinyPeripheralBoard;

    port sig : InterBoardSignalPort {
      attribute redefines name = "J1.1";
      attribute redefines direction = SinglePinDirection::input;
      attribute redefines role      = SinglePinRole::data;
    }
    port pwr : InterBoardPowerPort {
      attribute redefines name = "J1.2";
      attribute redefines direction = PowerDirection::sink;
      attribute redefines role      = PowerRole::rail;
    }
  }

  // -----------------------------------------------------------------
  // Good system - both interconnect ends terminate on PCBAs the
  // System actually owns.
  // -----------------------------------------------------------------

  part GoodSystem : System {
    attribute redefines name     = "SYS-GOOD";
    attribute redefines revision = "A";

    part A1 : HostNode {
      attribute redefines refDesignator = "A1";
    }
    part A2 : PeripheralNode {
      attribute redefines refDesignator = "A2";
    }

    // Multi-conductor harness: one signal + one power conductor.
    connection HARNESS1 : Interconnect connect A1 to A2 {
      part redefines cable : Cable {
        attribute redefines partNumber = "CBL-001";
        attribute redefines form       = CableForm::multi_conductor;

        part cond_sig : Conductor {
          attribute redefines label = "DATA";
          attribute redefines kind  = ConductorKind::signal;
        }
        part cond_pwr : Conductor {
          attribute redefines label = "5V";
          attribute redefines kind  = ConductorKind::power;
        }

        part redefines endA : CableEnd {
          attribute redefines label = "P1";
          part redefines target : HostNode = A1;
        }
        part redefines endB : CableEnd {
          attribute redefines label = "P2";
          part redefines target : PeripheralNode = A2;
        }
      }

      part redefines paths : ConductorPath {
        attribute redefines conductorLabel = "DATA";
        end redefines source = A1.sig;
        end redefines target = A2.sig;
      }
      part redefines paths : ConductorPath {
        attribute redefines conductorLabel = "5V";
        end redefines source = A1.pwr;
        end redefines target = A2.pwr;
      }
    }
  }

  // -----------------------------------------------------------------
  // Bad system - the jumper's endB points at A3, which is NOT in
  // the System's 'pcbas' list. Rule 1 (InterconnectEndpointReachability)
  // is the violation the rule pack must report.
  // -----------------------------------------------------------------

  part A3_external : PeripheralNode {
    attribute redefines refDesignator = "A3";
  }

  part BadSystem : System {
    attribute redefines name     = "SYS-BAD";
    attribute redefines revision = "A";

    part A1 : HostNode {
      attribute redefines refDesignator = "A1";
    }
    part A2 : PeripheralNode {
      attribute redefines refDesignator = "A2";
    }
    // Note: A3_external is intentionally NOT listed here.

    connection JUMPER1 : Interconnect connect A1 to A3_external {
      part redefines cable : Cable {
        attribute redefines partNumber = "CBL-JMP";
        attribute redefines form       = CableForm::discrete_wire;

        part cond_sig : Conductor {
          attribute redefines label = "DATA";
          attribute redefines kind  = ConductorKind::signal;
        }

        part redefines endA : CableEnd {
          attribute redefines label = "P1";
          part redefines target : HostNode = A1;
        }
        part redefines endB : CableEnd {
          attribute redefines label = "P2";
          part redefines target : PeripheralNode = A3_external;
        }
      }

      part redefines paths : ConductorPath {
        attribute redefines conductorLabel = "DATA";
        end redefines source = A1.sig;
        end redefines target = A3_external.sig;
      }
    }
  }
}
