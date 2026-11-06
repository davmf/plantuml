package Rule1_NoDanglingPorts_Test {
  doc /* Structural test fixture for PCBAProfile.
     *
     * Two boards in this file:
     *   GoodBoard - every port participates in a connection or NoConnect.
     *   BadBoard  - U2.pinA is intentionally left dangling.
     *
     * Rule 1 (NoDanglingPorts) is currently a doc-only constraint def,
     * so neither board produces a Rule 1 diagnostic when validated by
     * the OMG pilot today. Both boards must, however, parse and type-
     * check cleanly under PCBAProfile - that is what this file proves.
     *
     * When the external rule pack lands, the same fixture will be
     * driven through it: the rule pack must accept GoodBoard and
     * reject BadBoard, citing U2.pinA. */

  private import PCBAProfile::*;

  // -----------------------------------------------------------------
  // A trivial 2-pin IC for the test
  // -----------------------------------------------------------------

  part def TinyIC :> IC {
    port pinA : SinglePinPort {
      attribute redefines direction = SinglePinDirection::output;
      attribute redefines role      = SinglePinRole::data;
    }
    port pinB : SinglePinPort {
      attribute redefines direction = SinglePinDirection::input;
      attribute redefines role      = SinglePinRole::data;
    }
  }

  // -----------------------------------------------------------------
  // Good board - every port is accounted for.
  //   U1.pinA -> U2.pinB via Net N1
  //   U1.pinB -> NoConnect NC1
  //   U2.pinA -> NoConnect NC2
  // -----------------------------------------------------------------

  part GoodBoard : PCBA {
    attribute redefines partNumber = "PCBA-TEST-GOOD";
    attribute redefines revision   = "A";

    part U1 : TinyIC {
      attribute redefines designator = "U1";
    }
    part U2 : TinyIC {
      attribute redefines designator = "U2";
    }

    connection N1 : Net connect U1.pinA to U2.pinB;
    connection NC1 : NoConnect connect U1.pinB to U1.pinB;
    connection NC2 : NoConnect connect U2.pinA to U2.pinA;
  }

  // -----------------------------------------------------------------
  // Bad board - U2.pinA is intentionally dangling.
  //   U1.pinA -> U2.pinB via Net N1
  //   U1.pinB -> NoConnect NC1
  //   U2.pinA -> (nothing)
  //
  // Note: structurally valid under PCBAProfile. The "dangling pin"
  // status only becomes a diagnostic once the rule pack runs.
  // -----------------------------------------------------------------

  part BadBoard : PCBA {
    attribute redefines partNumber = "PCBA-TEST-BAD";
    attribute redefines revision   = "A";

    part U1 : TinyIC {
      attribute redefines designator = "U1";
    }
    part U2 : TinyIC {
      attribute redefines designator = "U2";
    }

    connection N1 : Net connect U1.pinA to U2.pinB;
    connection NC1 : NoConnect connect U1.pinB to U1.pinB;
    // U2.pinA intentionally has no connection and no NoConnect.
  }
}
