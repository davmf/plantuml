package NcPair {
  doc /* A Mate between two pinouts whose pin 2 is nc on both sides: with
     * detail=contact the harness joins pin 1 and pin 3 only. */
  private import InterconnectProfile::*;

  port def ThreePin :> ConnectorPort {
    attribute redefines series = "M8 3P";
    port c1 : ContactPort { attribute redefines contactId = "1"; attribute redefines kind = ContactKind::power;  attribute redefines signalName = "24V"; }
    port c2 : ContactPort { attribute redefines contactId = "2"; attribute redefines kind = ContactKind::nc; }
    port c3 : ContactPort { attribute redefines contactId = "3"; attribute redefines kind = ContactKind::ground; attribute redefines signalName = "0V"; }
  }

  part def Board :> PCBAUnit {
    port J1 : ThreePin { attribute redefines designator = "J1"; attribute redefines gender = ConnectorGender::male; }
  }

  part def Sensor1 :> Sensor {
    port X1 : ThreePin { attribute redefines designator = "X1"; attribute redefines gender = ConnectorGender::female; }
  }

  part ncSystem : InterconnectSystem {
    part A1 : Board   { attribute redefines designator = "A1"; }
    part B1 : Sensor1 { attribute redefines designator = "B1"; }
    connection : Mate connect A1.J1 to B1.X1;
  }
}
