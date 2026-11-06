package TestInterconnect1 {
  doc /* Worked example for InterconnectProfile: a battery-powered pump.
     *
     *   G1  battery pack ----+
     *                        | W1 (Y-harness, splices SP1/SP2)
     *   A1  controller J1 <--+
     *   H1  power lamp   <---+
     *
     *   A1 J2 --- W2 --- M1 pump motor     (inside assembly A2)
     *   A1 J3 --- W3 --- B1 temp sensor
     *
     * Pattern used throughout: a "pinout" port def fixes a connector's
     * series and contact assignment once; each side of a Mate reuses
     * it and only sets designator and gender.
     *
     * Requires PCBAProfile, InterconnectProfile and
     * ControllerBoard_PCBA.sysml to be loaded first. Expected to pass
     * every InterconnectProfile rule. */

  private import ISQ::*;
  private import SI::*;
  private import InterconnectProfile::*;
  private import ControllerBoard_PCBA::controllerPcba;

  // =================================================================
  // Pinouts
  // =================================================================

  port def BatteryPinout :> ConnectorPort {
    attribute redefines series = "XT30";
    port c1 : ContactPort {
      attribute redefines contactId  = "+";
      attribute redefines kind       = ContactKind::power;
      attribute redefines signalName = "VBAT";
    }
    port c2 : ContactPort {
      attribute redefines contactId  = "-";
      attribute redefines kind       = ContactKind::ground;
      attribute redefines signalName = "GND";
    }
  }

  port def PowerInPinout :> ConnectorPort {
    attribute redefines series = "JST-VH";
    port c1 : ContactPort {
      attribute redefines contactId  = "1";
      attribute redefines kind       = ContactKind::power;
      attribute redefines signalName = "VBAT";
    }
    port c2 : ContactPort {
      attribute redefines contactId  = "2";
      attribute redefines kind       = ContactKind::ground;
      attribute redefines signalName = "GND";
    }
  }

  port def LampPinout :> ConnectorPort {
    attribute redefines series = "JST-PH";
    port c1 : ContactPort {
      attribute redefines contactId  = "1";
      attribute redefines kind       = ContactKind::power;
      attribute redefines signalName = "VBAT";
    }
    port c2 : ContactPort {
      attribute redefines contactId  = "2";
      attribute redefines kind       = ContactKind::ground;
      attribute redefines signalName = "GND";
    }
  }

  port def MotorPinout :> ConnectorPort {
    attribute redefines series = "Molex Micro-Fit 3.0";
    port c1 : ContactPort {
      attribute redefines contactId  = "1";
      attribute redefines kind       = ContactKind::power;
      attribute redefines signalName = "MOT+";
    }
    port c2 : ContactPort {
      attribute redefines contactId  = "2";
      attribute redefines kind       = ContactKind::power;
      attribute redefines signalName = "MOT-";
    }
  }

  port def SensorPinout :> ConnectorPort {
    attribute redefines series = "JST-XH";
    port c1 : ContactPort {
      attribute redefines contactId  = "1";
      attribute redefines kind       = ContactKind::power;
      attribute redefines signalName = "3V3";
    }
    port c2 : ContactPort {
      attribute redefines contactId  = "2";
      attribute redefines kind       = ContactKind::signal;
      attribute redefines signalName = "TEMP";
    }
    port c3 : ContactPort {
      attribute redefines contactId  = "3";
      attribute redefines kind       = ContactKind::ground;
      attribute redefines signalName = "GND";
    }
  }

  // =================================================================
  // Units
  // =================================================================

  part def BatteryPack :> PowerSource {
    attribute redefines nominalVoltage = 12 [V];
    port X1 : BatteryPinout {
      attribute redefines designator = "X1";
      attribute redefines gender     = ConnectorGender::female;
      attribute redefines kind       = ConnectorKind::wire_to_wire;
    }
  }

  part def ControllerBoard :> PCBAUnit {
    port J1 : PowerInPinout {
      attribute redefines designator = "J1";
      attribute redefines gender     = ConnectorGender::male;
      attribute redefines kind       = ConnectorKind::wire_to_board;
    }
    port J2 : MotorPinout {
      attribute redefines designator = "J2";
      attribute redefines gender     = ConnectorGender::male;
      attribute redefines kind       = ConnectorKind::wire_to_board;
    }
    port J3 : SensorPinout {
      attribute redefines designator = "J3";
      attribute redefines gender     = ConnectorGender::male;
      attribute redefines kind       = ConnectorKind::wire_to_board;
    }
  }

  part def PowerLamp :> UserInterfaceDevice {
    port X1 : LampPinout {
      attribute redefines designator = "X1";
      attribute redefines gender     = ConnectorGender::male;
    }
  }

  part def PumpMotor :> Motor {
    attribute redefines motorKind    = MotorKind::brushed_dc;
    attribute redefines ratedVoltage = 12 [V];
    // Pigtail leads terminated in a Micro-Fit plug.
    port X1 : MotorPinout {
      attribute redefines designator = "X1";
      attribute redefines gender     = ConnectorGender::male;
      attribute redefines kind       = ConnectorKind::wire_to_wire;
    }
  }

  part def TempSensor :> Sensor {
    attribute redefines measurand = Measurand::temperature;
    port J1 : SensorPinout {
      attribute redefines designator = "J1";
      attribute redefines gender     = ConnectorGender::male;
    }
  }

  // =================================================================
  // Cables
  // =================================================================

  part def PowerHarness :> Cable {
    doc /* Y-harness: battery -> controller, tapped to the power lamp. */
    attribute redefines length = 600 [mm];

    port P1 : BatteryPinout {
      attribute redefines designator = "P1";
      attribute redefines gender     = ConnectorGender::male;
    }
    port P2 : PowerInPinout {
      attribute redefines designator = "P2";
      attribute redefines gender     = ConnectorGender::female;
    }
    port P3 : LampPinout {
      attribute redefines designator = "P3";
      attribute redefines gender     = ConnectorGender::female;
    }

    part SP1 : Splice {
      attribute redefines designator = "SP1";
      port redefines node { attribute redefines kind = ContactKind::power;  attribute redefines signalName = "VBAT"; }
    }
    part SP2 : Splice {
      attribute redefines designator = "SP2";
      port redefines node { attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
    }

    connection w1 : Wire connect P1.c1 to SP1.node { attribute redefines gauge = "18AWG"; attribute redefines color = "red"; }
    connection w2 : Wire connect SP1.node to P2.c1 { attribute redefines gauge = "18AWG"; attribute redefines color = "red"; }
    connection w3 : Wire connect SP1.node to P3.c1 { attribute redefines gauge = "24AWG"; attribute redefines color = "red"; }
    connection w4 : Wire connect P1.c2 to SP2.node { attribute redefines gauge = "18AWG"; attribute redefines color = "black"; }
    connection w5 : Wire connect SP2.node to P2.c2 { attribute redefines gauge = "18AWG"; attribute redefines color = "black"; }
    connection w6 : Wire connect SP2.node to P3.c2 { attribute redefines gauge = "24AWG"; attribute redefines color = "black"; }
  }

  part def MotorCable :> Cable {
    attribute redefines length = 400 [mm];
    port P1 : MotorPinout {
      attribute redefines designator = "P1";
      attribute redefines gender     = ConnectorGender::female;
    }
    port P2 : MotorPinout {
      attribute redefines designator = "P2";
      attribute redefines gender     = ConnectorGender::female;
    }
    connection w1 : Wire connect P1.c1 to P2.c1 { attribute redefines gauge = "18AWG"; attribute redefines twistGroup = "TP1"; }
    connection w2 : Wire connect P1.c2 to P2.c2 { attribute redefines gauge = "18AWG"; attribute redefines twistGroup = "TP1"; }
  }

  part def SensorCable :> Cable {
    attribute redefines length   = 1200 [mm];
    attribute redefines shielded = false;
    port P1 : SensorPinout {
      attribute redefines designator = "P1";
      attribute redefines gender     = ConnectorGender::female;
    }
    port P2 : SensorPinout {
      attribute redefines designator = "P2";
      attribute redefines gender     = ConnectorGender::female;
    }
    connection w1 : Wire connect P1.c1 to P2.c1 { attribute redefines gauge = "26AWG"; }
    connection w2 : Wire connect P1.c2 to P2.c2 { attribute redefines gauge = "26AWG"; }
    connection w3 : Wire connect P1.c3 to P2.c3 { attribute redefines gauge = "26AWG"; }
  }

  // =================================================================
  // The system
  // =================================================================

  part pumpSystem : InterconnectSystem {
    attribute redefines partNumber = "SYS-PUMP-01";
    attribute redefines revision   = "A";

    part G1 : BatteryPack     { attribute redefines designator = "G1"; }
    part A1 : ControllerBoard {
      attribute redefines designator = "A1";
      ref part redefines pcba = controllerPcba;
    }
    part H1 : PowerLamp       { attribute redefines designator = "H1"; }
    part B1 : TempSensor      { attribute redefines designator = "B1"; }

    part A2 : Assembly {
      doc /* Pump assembly: motor shipped with its cable attached. */
      attribute redefines designator = "A2";
      part M1 : PumpMotor  { attribute redefines designator = "M1"; }
      part W2 : MotorCable { attribute redefines designator = "W2"; }
      connection : Mate connect W2.P2 to M1.X1;
    }

    part W1 : PowerHarness { attribute redefines designator = "W1"; }
    part W3 : SensorCable  { attribute redefines designator = "W3"; }

    // ---- physical mates ----
    connection : Mate connect G1.X1 to W1.P1;
    connection : Mate connect W1.P2 to A1.J1;
    connection : Mate connect W1.P3 to H1.X1;
    connection : Mate connect A1.J2 to A2.W2.P1;
    connection : Mate connect A1.J3 to W3.P1;
    connection : Mate connect W3.P2 to B1.J1;

    // ---- logical overlay ----
    connection pwr  : InterfaceLink connect G1.X1 to A1.J1 {
      attribute redefines protocol = InterfaceProtocol::power;
    }
    connection drv  : InterfaceLink connect A1.J2 to A2.M1.X1 {
      attribute redefines protocol = InterfaceProtocol::motor_phase;
    }
    connection temp : InterfaceLink connect A1.J3 to B1.J1 {
      attribute redefines protocol = InterfaceProtocol::analog;
    }
  }
}
