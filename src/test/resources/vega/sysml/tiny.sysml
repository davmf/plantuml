package Tiny {
  doc /* Edge cases for !include_sysml: a pinout reused with a usage-level
     * redefinition, an untyped connect, an unmated and a NoMate connector,
     * a UnitLink, and a second system. */
  private import InterconnectProfile::*;

  port def TwoPin :> ConnectorPort {
    attribute redefines series = "JST-PH 2P";
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power; attribute redefines signalName = "5V"; }
    port c2 : ContactPort { attribute redefines contactId = "2"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "GND"; }
  }

  part def Board :> PCBAUnit {
    attribute redefines partNumber = "PCBA-1";
    port J1 : TwoPin { attribute redefines designator = "J1"; attribute redefines gender = ConnectorGender::male; }
    port J2 : TwoPin { attribute redefines designator = "J2"; attribute redefines gender = ConnectorGender::male; }
    port J3 : TwoPin { :>> designator = "J3"; }
  }

  part def Lamp :> UserInterfaceDevice {
    port X1 : TwoPin {
      attribute redefines designator = "X1";
      attribute redefines gender = ConnectorGender::female;
      port redefines c2 { attribute redefines kind = ContactKind::nc; }
    }
  }

  part main : InterconnectSystem {
    attribute redefines partNumber = "SYS-TINY";
    attribute redefines revision = "B";
    part A1 : Board { attribute redefines designator = "A1"; }
    part H1 : Lamp  { attribute redefines designator = "H1"; }
    connect A1.J1 to H1.X1;                         // untyped: a Mate
    connection : NoMate connect A1.J3 to A1.J3;     // J2 is left unmated
    connection : UnitLink connect A1 to H1 { attribute redefines protocol = InterfaceProtocol::power; }
    connection : Mate connect A1.J9 to H1.X1;       // no such connector: a warning
  }

  part other : InterconnectSystem {
    part B1 : Board;
  }
}
