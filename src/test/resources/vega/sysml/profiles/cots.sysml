package Rule4_ChannelPortTypeAgreement_Test {
  doc /* Structural test fixture for COTSDeviceProfile.
     *
     * Two Systems in this file:
     *   GoodSystem - a LabJack-like COTSDevice ("U1") cabled to a tiny
     *                PCBA ("A2"). The device's "AIN0" channel is
     *                kind = analog_in and lists a single boundary port
     *                that is an InterBoardSignalPort with role = analog
     *                and direction = analog_in. Rule 4 is satisfied.
     *   BadSystem  - same physical layout, but the "AIN0" channel
     *                lists a boundary port whose direction is 'output'
     *                and role is 'data' (a digital pin). Rule 4 must
     *                reject this once the external rule pack is active.
     *
     * As with the sibling fixtures, the rule defs in COTSDeviceProfile
     * are doc-only, so both Systems parse and type-check today; the
     * rule pack distinguishes them.
     *
     * This fixture also demonstrates the InterPCBAProfile retype: a
     * COTSDevice sits at one end of an Interconnect alongside a
     * SystemPCBA at the other. */

  private import COTSDeviceProfile::*;
  private import InterPCBAProfile::*;
  private import PCBAProfile::*;

  // -----------------------------------------------------------------
  // Tiny peripheral board with one 2-pin connector.
  // -----------------------------------------------------------------

  part def TinyPeripheralBoard :> PCBA {
    attribute redefines partNumber = "PERIPH-001";
    attribute redefines revision   = "A";

    part J1 : Connector {
      attribute redefines designator = "J1";
    }
  }

  part def PeripheralNode :> SystemPCBA {
    part redefines board : TinyPeripheralBoard;

    port sig : InterBoardSignalPort {
      attribute redefines name = "J1.1";
      attribute redefines direction = SinglePinDirection::input;
      attribute redefines role      = SinglePinRole::analog;
    }
    port pwr : InterBoardPowerPort {
      attribute redefines name = "J1.2";
      attribute redefines direction = PowerDirection::sink;
      attribute redefines role      = PowerRole::rail;
    }
  }

  // -----------------------------------------------------------------
  // GoodLabJack: AIN0 is analog_in / analog / analog_in - matches the
  // Channel.kind of analog_in. Rule 4 satisfied.
  // -----------------------------------------------------------------

  part def GoodLabJack :> COTSDevice {
    attribute redefines manufacturer = "LabJack";
    attribute redefines model        = "T7-LIKE-GOOD";

    port AIN0 : InterBoardSignalPort {
      attribute redefines name = "AIN0";
      attribute redefines direction = SinglePinDirection::analog_in;
      attribute redefines role      = SinglePinRole::analog;
    }
    port VS : InterBoardPowerPort {
      attribute redefines name = "VS";
      attribute redefines direction = PowerDirection::source;
      attribute redefines role      = PowerRole::rail;
    }

    part connector_AIN0 : ExternalConnector {
      attribute redefines label = "ST1.AIN0";
      attribute redefines kind  = ExternalConnectorKind::screw_terminal;
      attribute redefines portNames = "AIN0";
    }
    part connector_VS : ExternalConnector {
      attribute redefines label = "ST1.VS";
      attribute redefines kind  = ExternalConnectorKind::screw_terminal;
      attribute redefines portNames = "VS";
    }

    part channel_AIN0 : Channel {
      attribute redefines label = "AIN0";
      attribute redefines kind  = ChannelKind::analog_in;
      attribute redefines portNames = "AIN0";
    }
  }

  // -----------------------------------------------------------------
  // BadLabJack: AIN0 is mistyped as a digital output - Channel.kind
  // says analog_in but the port is direction = output / role = data.
  // Rule 4 violation (doc-only today; structurally valid).
  // -----------------------------------------------------------------

  part def BadLabJack :> COTSDevice {
    attribute redefines manufacturer = "LabJack";
    attribute redefines model        = "T7-LIKE-BAD";

    port AIN0 : InterBoardSignalPort {
      attribute redefines name = "AIN0";
      attribute redefines direction = SinglePinDirection::output;     // wrong
      attribute redefines role      = SinglePinRole::data;            // wrong
    }
    port VS : InterBoardPowerPort {
      attribute redefines name = "VS";
      attribute redefines direction = PowerDirection::source;
      attribute redefines role      = PowerRole::rail;
    }

    part connector_AIN0 : ExternalConnector {
      attribute redefines label = "ST1.AIN0";
      attribute redefines kind  = ExternalConnectorKind::screw_terminal;
      attribute redefines portNames = "AIN0";
    }
    part connector_VS : ExternalConnector {
      attribute redefines label = "ST1.VS";
      attribute redefines kind  = ExternalConnectorKind::screw_terminal;
      attribute redefines portNames = "VS";
    }

    part channel_AIN0 : Channel {
      attribute redefines label = "AIN0";
      attribute redefines kind  = ChannelKind::analog_in;
      attribute redefines portNames = "AIN0";
    }
  }

  // -----------------------------------------------------------------
  // Good system - COTSDevice (U1) cabled to a PCBA (A2). The cable
  // carries one signal conductor (AIN0) and one power conductor (VS).
  // -----------------------------------------------------------------

  part GoodSystem : System {
    attribute redefines name     = "SYS-GOOD";
    attribute redefines revision = "A";

    part U1 : GoodLabJack {
      attribute redefines refDesignator = "U1";
    }
    part A2 : PeripheralNode {
      attribute redefines refDesignator = "A2";
    }

    connection HARNESS1 : Interconnect connect U1 to A2 {
      part redefines cable : Cable {
        attribute redefines partNumber = "CBL-001";
        attribute redefines form       = CableForm::multi_conductor;

        part c_sig : Conductor {
          attribute redefines label = "AIN0";
          attribute redefines kind  = ConductorKind::signal;
        }
        part c_pwr : Conductor {
          attribute redefines label = "VS";
          attribute redefines kind  = ConductorKind::power;
        }

        part redefines endA : CableEnd {
          attribute redefines label = "P1";
          part redefines target : GoodLabJack = U1;
        }
        part redefines endB : CableEnd {
          attribute redefines label = "P2";
          part redefines target : PeripheralNode = A2;
        }
      }

      part redefines paths : ConductorPath {
        attribute redefines conductorLabel = "AIN0";
        end redefines source = U1.AIN0;
        end redefines target = A2.sig;
      }
      part redefines paths : ConductorPath {
        attribute redefines conductorLabel = "VS";
        end redefines source = U1.VS;
        end redefines target = A2.pwr;
      }
    }
  }

  // -----------------------------------------------------------------
  // Bad system - structurally identical, but U1 is a BadLabJack so the
  // analog_in channel points at a digital output pin. Rule 4 violation.
  // -----------------------------------------------------------------

  part BadSystem : System {
    attribute redefines name     = "SYS-BAD";
    attribute redefines revision = "A";

    part U1 : BadLabJack {
      attribute redefines refDesignator = "U1";
    }
    part A2 : PeripheralNode {
      attribute redefines refDesignator = "A2";
    }

    connection HARNESS1 : Interconnect connect U1 to A2 {
      part redefines cable : Cable {
        attribute redefines partNumber = "CBL-001";
        attribute redefines form       = CableForm::multi_conductor;

        part c_sig : Conductor {
          attribute redefines label = "AIN0";
          attribute redefines kind  = ConductorKind::signal;
        }
        part c_pwr : Conductor {
          attribute redefines label = "VS";
          attribute redefines kind  = ConductorKind::power;
        }

        part redefines endA : CableEnd {
          attribute redefines label = "P1";
          part redefines target : BadLabJack = U1;
        }
        part redefines endB : CableEnd {
          attribute redefines label = "P2";
          part redefines target : PeripheralNode = A2;
        }
      }

      part redefines paths : ConductorPath {
        attribute redefines conductorLabel = "AIN0";
        end redefines source = U1.AIN0;
        end redefines target = A2.sig;
      }
      part redefines paths : ConductorPath {
        attribute redefines conductorLabel = "VS";
        end redefines source = U1.VS;
        end redefines target = A2.pwr;
      }
    }
  }
}
