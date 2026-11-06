package TestInterconnect3 {
  doc /* Worked example for InterconnectProfile: a solar-powered remote
     * weather station.
     *
     *   OUTSIDE                         | A5 ENCLOSURE
     *                                   |
     *   G2 solar panel X1(+) X2(-)      |
     *        \      /                   |
     *         W1 (2x MC4 -> terminal) --|--> A2 solar charger J1
     *                                   |       J2 <-- W2 --> G1 battery (Anderson)
     *                                   |       J3 --- W6 --> A1 data logger J1
     *                                   |                     J3 == A3 LTE modem (card edge)
     *                                   |                     J4 debug: NoMate
     *   E1 antenna X1 ================= |== W5 SMA bulkhead <-- coax -- A3 J2 (U.FL)
     *                                   |
     *   B1 wind  --+                    |
     *   B2 T/RH  --+-- W4 RS-485 T-harness (SP1..SP4)
     *   B3 solar --+        P1 ======== |== W3 M12 bulkhead <-- pigtail -- A1 J2
     *
     * Features exercised:
     *   - three PCBA / Module units, incl. a card-edge module (A3)
     *   - an RS-485 multi-drop bus to three sensors, built from one
     *     T-harness with four Splices (one per conductor)
     *   - panel-mount bulkhead connectors modelled as short pigtail
     *     Cables (W3, W5) that cross the enclosure wall
     *   - coaxial RF path: centre conductor as signal, outer as shield
     *   - hermaphroditic connector (Anderson PP45) on the battery
     *   - single-pole connectors (MC4) - one ConnectorPort per pole -
     *     merged into one terminal plug by cable W1
     *   - two PowerSources, one feeding another via the charger
     *   - a UnitLink for the solar input (no single connector carries
     *     both poles)
     *   - an Assembly (A5) whose bulkhead pigtails are mated from
     *     outside at system level
     *
     * Requires PCBAProfile and InterconnectProfile to be loaded first.
     * Expected to pass every InterconnectProfile rule. */

  private import ISQ::*;
  private import SI::*;
  private import InterconnectProfile::*;

  // =================================================================
  // Pinouts
  // =================================================================

  port def Mc4PlusPinout :> ConnectorPort {
    attribute redefines series = "MC4";
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "PV+"; }
  }

  port def Mc4MinusPinout :> ConnectorPort {
    attribute redefines series = "MC4";
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "PV-"; }
  }

  port def PvTerminalPinout :> ConnectorPort {
    attribute redefines series = "Phoenix MSTB 5.08 2P";
    attribute redefines kind   = ConnectorKind::terminal;
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "PV+"; }
    port c2 : ContactPort { attribute redefines contactId = "2"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "PV-"; }
  }

  port def AndersonPinout :> ConnectorPort {
    attribute redefines series = "Anderson Powerpole PP45";
    port red   : ContactPort { attribute redefines contactId = "R"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "VBAT"; }
    port black : ContactPort { attribute redefines contactId = "B"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
  }

  port def BatteryTerminalPinout :> ConnectorPort {
    attribute redefines series = "Phoenix MSTB 5.08 2P";
    attribute redefines kind   = ConnectorKind::terminal;
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "VBAT"; }
    port c2 : ContactPort { attribute redefines contactId = "2"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
  }

  port def Load12Pinout :> ConnectorPort {
    attribute redefines series = "Molex Micro-Fit 3.0 2P";
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "12V_LOAD"; }
    port c2 : ContactPort { attribute redefines contactId = "2"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
  }

  port def Rs485TerminalPinout :> ConnectorPort {
    attribute redefines series = "Phoenix MC 1.5 4P";
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "12V_BUS"; }
    port c2 : ContactPort { attribute redefines contactId = "2"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "RS485_A"; }
    port c3 : ContactPort { attribute redefines contactId = "3"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
    port c4 : ContactPort { attribute redefines contactId = "4"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "RS485_B"; }
  }

  port def M12BusPinout :> ConnectorPort {
    doc /* Same signal assignment as Rs485TerminalPinout, on M12. */
    attribute redefines series = "M12 A-coded 4P";
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "12V_BUS"; }
    port c2 : ContactPort { attribute redefines contactId = "2"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "RS485_A"; }
    port c3 : ContactPort { attribute redefines contactId = "3"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
    port c4 : ContactPort { attribute redefines contactId = "4"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "RS485_B"; }
  }

  port def MiniPcieSlotPinout :> ConnectorPort {
    doc /* Abbreviated: only the contacts this system uses. */
    attribute redefines series = "Mini PCIe";
    attribute redefines kind   = ConnectorKind::board_to_board;
    port c2  : ContactPort { attribute redefines contactId = "2";  attribute redefines kind = ContactKind::power;  attribute redefines signalName = "3V3_AUX"; }
    port c4  : ContactPort { attribute redefines contactId = "4";  attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
    port c36 : ContactPort { attribute redefines contactId = "36"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "USB_D-"; }
    port c38 : ContactPort { attribute redefines contactId = "38"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "USB_D+"; }
  }

  port def CoaxPinout :> ConnectorPort {
    port centre : ContactPort { attribute redefines contactId = "C"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "RF_MAIN"; }
    port outer  : ContactPort { attribute redefines contactId = "S"; attribute redefines kind = ContactKind::shield; }
  }

  port def UflPinout :> CoaxPinout { attribute redefines series = "Hirose U.FL"; }
  port def SmaPinout :> CoaxPinout { attribute redefines series = "SMA"; }

  port def SwdPinout :> ConnectorPort {
    attribute redefines series = "Tag-Connect TC2030";
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "3V3"; }
    port c2 : ContactPort { attribute redefines contactId = "2"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "SWDIO"; }
    port c3 : ContactPort { attribute redefines contactId = "3"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "NRST"; }
    port c4 : ContactPort { attribute redefines contactId = "4"; attribute redefines kind = ContactKind::signal; attribute redefines signalName = "SWCLK"; }
    port c5 : ContactPort { attribute redefines contactId = "5"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
  }

  // =================================================================
  // Units
  // =================================================================

  part def SolarPanel :> PowerSource {
    attribute redefines nominalVoltage = 18 [V];
    attribute redefines maxCurrent     = 3 [A];
    port X1 : Mc4PlusPinout  { attribute redefines designator = "X1"; attribute redefines gender = ConnectorGender::male;   attribute redefines kind = ConnectorKind::wire_to_wire; }
    port X2 : Mc4MinusPinout { attribute redefines designator = "X2"; attribute redefines gender = ConnectorGender::female; attribute redefines kind = ConnectorKind::wire_to_wire; }
  }

  part def LiFePO4Battery :> PowerSource {
    attribute redefines nominalVoltage = 12.8 [V];
    port X1 : AndersonPinout {
      attribute redefines designator = "X1";
      attribute redefines gender     = ConnectorGender::hermaphroditic;
      attribute redefines kind       = ConnectorKind::wire_to_wire;
    }
  }

  part def SolarCharger :> PCBAUnit {
    attribute redefines partNumber = "PCBA-WX-CHG";
    port J1 : PvTerminalPinout      { attribute redefines designator = "J1"; attribute redefines gender = ConnectorGender::male; }
    port J2 : BatteryTerminalPinout { attribute redefines designator = "J2"; attribute redefines gender = ConnectorGender::male; }
    port J3 : Load12Pinout          { attribute redefines designator = "J3"; attribute redefines gender = ConnectorGender::male; }
  }

  part def DataLogger :> PCBAUnit {
    attribute redefines partNumber = "PCBA-WX-LOG";
    port J1 : Load12Pinout        { attribute redefines designator = "J1"; attribute redefines gender = ConnectorGender::male; }
    port J2 : Rs485TerminalPinout { attribute redefines designator = "J2"; attribute redefines gender = ConnectorGender::male; }
    port J3 : MiniPcieSlotPinout  { attribute redefines designator = "J3"; attribute redefines gender = ConnectorGender::female; }
    port J4 : SwdPinout           { attribute redefines designator = "J4"; attribute redefines gender = ConnectorGender::genderless; }  // pogo pads
  }

  part def LteModem :> Module {
    attribute redefines partNumber = "Quectel EC25 Mini PCIe";
    port J1 : MiniPcieSlotPinout { attribute redefines designator = "J1"; attribute redefines gender = ConnectorGender::male; }  // gold fingers
    port J2 : UflPinout          { attribute redefines designator = "J2"; attribute redefines gender = ConnectorGender::male; }
  }

  part def LteAntenna :> Device {
    port X1 : SmaPinout { attribute redefines designator = "X1"; attribute redefines gender = ConnectorGender::male; }
  }

  part def Anemometer :> Sensor {
    attribute redefines measurand = Measurand::speed;
    port X1 : M12BusPinout { attribute redefines designator = "X1"; attribute redefines gender = ConnectorGender::male; }
  }

  part def TempHumiditySensor :> Sensor {
    attribute redefines measurand = Measurand::humidity;
    port X1 : M12BusPinout { attribute redefines designator = "X1"; attribute redefines gender = ConnectorGender::male; }
  }

  part def Pyranometer :> Sensor {
    attribute redefines measurand = Measurand::light;
    port X1 : M12BusPinout { attribute redefines designator = "X1"; attribute redefines gender = ConnectorGender::male; }
  }

  // =================================================================
  // Cables
  // =================================================================

  part def SolarCable :> Cable {
    doc /* Two single-pole MC4 ends merged into one 2-way terminal plug. */
    attribute redefines length = 5000 [mm];
    port P1 : Mc4PlusPinout    { attribute redefines designator = "P1"; attribute redefines gender = ConnectorGender::female; }
    port P2 : Mc4MinusPinout   { attribute redefines designator = "P2"; attribute redefines gender = ConnectorGender::male; }
    port P3 : PvTerminalPinout { attribute redefines designator = "P3"; attribute redefines gender = ConnectorGender::female; }
    connection w1 : Wire connect P1.c1 to P3.c1 { attribute redefines gauge = "4mm2"; attribute redefines color = "red"; }
    connection w2 : Wire connect P2.c1 to P3.c2 { attribute redefines gauge = "4mm2"; attribute redefines color = "black"; }
  }

  part def BatteryCable :> Cable {
    attribute redefines length = 400 [mm];
    port P1 : AndersonPinout        { attribute redefines designator = "P1"; attribute redefines gender = ConnectorGender::hermaphroditic; }
    port P2 : BatteryTerminalPinout { attribute redefines designator = "P2"; attribute redefines gender = ConnectorGender::female; }
    connection w1 : Wire connect P1.red   to P2.c1 { attribute redefines gauge = "12AWG"; attribute redefines color = "red"; }
    connection w2 : Wire connect P1.black to P2.c2 { attribute redefines gauge = "12AWG"; attribute redefines color = "black"; }
  }

  part def LoadCable :> Cable {
    attribute redefines length = 250 [mm];
    port P1 : Load12Pinout { attribute redefines designator = "P1"; attribute redefines gender = ConnectorGender::female; }
    port P2 : Load12Pinout { attribute redefines designator = "P2"; attribute redefines gender = ConnectorGender::female; }
    connection w1 : Wire connect P1.c1 to P2.c1 { attribute redefines gauge = "20AWG"; }
    connection w2 : Wire connect P1.c2 to P2.c2 { attribute redefines gauge = "20AWG"; }
  }

  part def BusBulkheadPigtail :> Cable {
    doc /* M12 panel jack in the enclosure wall, pigtailed to a pluggable
       * terminal for the logger. P2 is the bulkhead. */
    attribute redefines length = 200 [mm];
    port P1 : Rs485TerminalPinout { attribute redefines designator = "P1"; attribute redefines gender = ConnectorGender::female; }
    port P2 : M12BusPinout {
      attribute redefines designator = "P2";
      attribute redefines gender     = ConnectorGender::female;
      attribute redefines kind       = ConnectorKind::panel_mount;
    }
    connection w1 : Wire connect P1.c1 to P2.c1;
    connection w2 : Wire connect P1.c2 to P2.c2 { attribute redefines twistGroup = "485"; }
    connection w3 : Wire connect P1.c3 to P2.c3;
    connection w4 : Wire connect P1.c4 to P2.c4 { attribute redefines twistGroup = "485"; }
  }

  part def SensorBusHarness :> Cable {
    doc /* RS-485 T-harness: one M12 male trunk end (P1) and three M12
       * female drops (P2..P4). Each conductor meets at its own splice,
       * so every splice joins exactly four wires. */
    attribute redefines length   = 8000 [mm];
    attribute redefines shielded = false;

    port P1 : M12BusPinout { attribute redefines designator = "P1"; attribute redefines gender = ConnectorGender::male; }
    port P2 : M12BusPinout { attribute redefines designator = "P2"; attribute redefines gender = ConnectorGender::female; }
    port P3 : M12BusPinout { attribute redefines designator = "P3"; attribute redefines gender = ConnectorGender::female; }
    port P4 : M12BusPinout { attribute redefines designator = "P4"; attribute redefines gender = ConnectorGender::female; }

    part SP1 : Splice { attribute redefines designator = "SP1"; port redefines node { attribute redefines kind = ContactKind::power;  attribute redefines signalName = "12V_BUS"; } }
    part SP2 : Splice { attribute redefines designator = "SP2"; port redefines node { attribute redefines kind = ContactKind::signal; attribute redefines signalName = "RS485_A"; } }
    part SP3 : Splice { attribute redefines designator = "SP3"; port redefines node { attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; } }
    part SP4 : Splice { attribute redefines designator = "SP4"; port redefines node { attribute redefines kind = ContactKind::signal; attribute redefines signalName = "RS485_B"; } }

    // 12V_BUS
    connection w11 : Wire connect P1.c1 to SP1.node;
    connection w12 : Wire connect SP1.node to P2.c1;
    connection w13 : Wire connect SP1.node to P3.c1;
    connection w14 : Wire connect SP1.node to P4.c1;
    // RS485_A
    connection w21 : Wire connect P1.c2 to SP2.node;
    connection w22 : Wire connect SP2.node to P2.c2;
    connection w23 : Wire connect SP2.node to P3.c2;
    connection w24 : Wire connect SP2.node to P4.c2;
    // GND
    connection w31 : Wire connect P1.c3 to SP3.node;
    connection w32 : Wire connect SP3.node to P2.c3;
    connection w33 : Wire connect SP3.node to P3.c3;
    connection w34 : Wire connect SP3.node to P4.c3;
    // RS485_B
    connection w41 : Wire connect P1.c4 to SP4.node;
    connection w42 : Wire connect SP4.node to P2.c4;
    connection w43 : Wire connect SP4.node to P3.c4;
    connection w44 : Wire connect SP4.node to P4.c4;
  }

  part def RfBulkheadPigtail :> Cable {
    doc /* U.FL to SMA bulkhead coax pigtail (1.13 mm micro-coax). */
    attribute redefines length   = 150 [mm];
    attribute redefines shielded = true;
    port P1 : UflPinout { attribute redefines designator = "P1"; attribute redefines gender = ConnectorGender::female; }
    port P2 : SmaPinout {
      attribute redefines designator = "P2";
      attribute redefines gender     = ConnectorGender::female;
      attribute redefines kind       = ConnectorKind::panel_mount;
    }
    connection centre : Wire connect P1.centre to P2.centre;
    connection outer  : Wire connect P1.outer  to P2.outer;
  }

  // =================================================================
  // The system
  // =================================================================

  part weatherStation : InterconnectSystem {
    attribute redefines partNumber = "SYS-WX-10";
    attribute redefines revision   = "A";

    // ---- outside the enclosure ----
    part G2 : SolarPanel         { attribute redefines designator = "G2"; }
    part E1 : LteAntenna         { attribute redefines designator = "E1"; }
    part B1 : Anemometer         { attribute redefines designator = "B1"; }
    part B2 : TempHumiditySensor { attribute redefines designator = "B2"; }
    part B3 : Pyranometer        { attribute redefines designator = "B3"; }
    part W1 : SolarCable         { attribute redefines designator = "W1"; }
    part W4 : SensorBusHarness   { attribute redefines designator = "W4"; }

    // ---- the enclosure ----
    part A5 : Assembly {
      doc /* IP66 enclosure with all electronics and the battery.
         * W3 and W5 are its bulkhead pass-throughs. */
      attribute redefines designator = "A5";
      part A1 : DataLogger         { attribute redefines designator = "A1"; }
      part A2 : SolarCharger       { attribute redefines designator = "A2"; }
      part A3 : LteModem           { attribute redefines designator = "A3"; }
      part G1 : LiFePO4Battery     { attribute redefines designator = "G1"; }
      part W2 : BatteryCable       { attribute redefines designator = "W2"; }
      part W3 : BusBulkheadPigtail { attribute redefines designator = "W3"; }
      part W5 : RfBulkheadPigtail  { attribute redefines designator = "W5"; }
      part W6 : LoadCable          { attribute redefines designator = "W6"; }

      connection : Mate connect G1.X1 to W2.P1;   // hermaphroditic pair
      connection : Mate connect W2.P2 to A2.J2;
      connection : Mate connect A2.J3 to W6.P1;
      connection : Mate connect W6.P2 to A1.J1;
      connection : Mate connect A1.J3 to A3.J1;   // card edge
      connection : Mate connect A3.J2 to W5.P1;
      connection : Mate connect A1.J2 to W3.P1;

      connection nmSwd : NoMate connect A1.J4 to A1.J4;  // programming pads, production only
    }

    // ---- through the enclosure wall ----
    connection : Mate connect G2.X1 to W1.P1;
    connection : Mate connect G2.X2 to W1.P2;
    connection : Mate connect W1.P3 to A5.A2.J1;
    connection : Mate connect E1.X1 to A5.W5.P2;
    connection : Mate connect W4.P1 to A5.W3.P2;
    connection : Mate connect W4.P2 to B1.X1;
    connection : Mate connect W4.P3 to B2.X1;
    connection : Mate connect W4.P4 to B3.X1;

    // ---- logical overlay ----
    connection batt  : InterfaceLink connect A5.G1.X1 to A5.A2.J2 { attribute redefines protocol = InterfaceProtocol::power; }
    connection load  : InterfaceLink connect A5.A2.J3 to A5.A1.J1 { attribute redefines protocol = InterfaceProtocol::power; }
    connection usb   : InterfaceLink connect A5.A1.J3 to A5.A3.J1 { attribute redefines protocol = InterfaceProtocol::usb; }
    connection rf    : InterfaceLink connect A5.A3.J2 to E1.X1    { attribute redefines protocol = InterfaceProtocol::rf; }
    connection bus1  : InterfaceLink connect A5.A1.J2 to B1.X1    { attribute redefines protocol = InterfaceProtocol::rs485; }
    connection bus2  : InterfaceLink connect A5.A1.J2 to B2.X1    { attribute redefines protocol = InterfaceProtocol::rs485; }
    connection bus3  : InterfaceLink connect A5.A1.J2 to B3.X1    { attribute redefines protocol = InterfaceProtocol::rs485; }
    // G2's poles are separate single-pole connectors, so no one
    // ConnectorPort carries both power and ground: link the Units instead.
    connection solar : UnitLink connect G2 to A5.A2 { attribute redefines protocol = InterfaceProtocol::power; }
  }
}
