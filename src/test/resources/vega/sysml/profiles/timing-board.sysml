package PowerUpBoard_PCBA {
  doc /* Board-level (PCBAProfile) model of a small MCU board, just
     * enough for test-timing-1.sysml to reference its nets:
     *
     *   VIN --> U1 (3V3 LDO) --+3V3--> U2 (1V8 LDO) --+1V8--> U4 core
     *                          +-----> U3 supervisor, U4 I/O, Y1
     *   U1.pg --PG_3V3--> U2.en      (1V8 enabled once 3V3 is good)
     *   U3.nrst --NRESET--> U4.nrst  (supervisor holds the MCU in reset)
     *   Y1.clkOut --HSE_CLK--> U4.xin   (24 MHz oscillator)
     *
     * Every rail and signal a power-up timing diagram shows is a
     * PowerRail or Net connection here. */

  private import PCBAProfile::*;

  part def Ldo :> PowerComponent {
    port vin : PowerPort {
      attribute redefines direction = PowerDirection::sink;
      attribute redefines role      = PowerRole::primary;
    }
    port vout : PowerPort {
      attribute redefines direction = PowerDirection::source;
      attribute redefines role      = PowerRole::rail;
    }
    port en : SinglePinPort {
      attribute redefines direction = SinglePinDirection::input;
      attribute redefines role      = SinglePinRole::enable;
    }
    port pg : SinglePinPort {
      attribute redefines direction = SinglePinDirection::oc;
      attribute redefines role      = SinglePinRole::status;
    }
  }

  part def Supervisor :> IC {
    port vdd : PowerPort {
      attribute redefines direction = PowerDirection::sink;
      attribute redefines role      = PowerRole::rail;
    }
    port nrst : SinglePinPort {
      attribute redefines direction = SinglePinDirection::oc;
      attribute redefines role      = SinglePinRole::reset;
    }
  }

  part def Mcu :> IC {
    port vdd : PowerPort {
      attribute redefines direction = PowerDirection::sink;
      attribute redefines role      = PowerRole::rail;
    }
    port vddCore : PowerPort {
      attribute redefines direction = PowerDirection::sink;
      attribute redefines role      = PowerRole::rail;
    }
    port nrst : SinglePinPort {
      attribute redefines direction = SinglePinDirection::input;
      attribute redefines role      = SinglePinRole::reset;
    }
    port xin : SinglePinPort {
      attribute redefines direction = SinglePinDirection::input;
      attribute redefines role      = SinglePinRole::clock;
    }
  }

  part def Oscillator :> IC {
    port vdd : PowerPort {
      attribute redefines direction = PowerDirection::sink;
      attribute redefines role      = PowerRole::rail;
    }
    port clkOut : SinglePinPort {
      attribute redefines direction = SinglePinDirection::output;
      attribute redefines role      = SinglePinRole::clock;
    }
  }

  part board : PCBA {
    attribute redefines partNumber = "PCBA-MCU-PWR";
    attribute redefines revision   = "A";

    part U1 : Ldo        { attribute redefines designator = "U1"; }
    part U2 : Ldo        { attribute redefines designator = "U2"; }
    part U3 : Supervisor { attribute redefines designator = "U3"; }
    part U4 : Mcu        { attribute redefines designator = "U4"; }
    part Y1 : Oscillator { attribute redefines designator = "Y1"; }

    connection P3V3 : PowerRail connect U1.vout to U4.vdd {
      attribute redefines name = "+3V3";
    }
    connection P3V3_U2 : PowerRail connect U1.vout to U2.vin {
      attribute redefines name = "+3V3";
    }
    connection P3V3_U3 : PowerRail connect U1.vout to U3.vdd {
      attribute redefines name = "+3V3";
    }
    connection P3V3_Y1 : PowerRail connect U1.vout to Y1.vdd {
      attribute redefines name = "+3V3";
    }
    connection P1V8 : PowerRail connect U2.vout to U4.vddCore {
      attribute redefines name = "+1V8";
    }
    connection PG_3V3 : Net connect U1.pg to U2.en {
      attribute redefines name = "PG_3V3";
    }
    connection NRESET : Net connect U3.nrst to U4.nrst {
      attribute redefines name = "NRESET";
    }
    connection HSE_CLK : Net connect Y1.clkOut to U4.xin {
      attribute redefines name = "HSE_CLK";
    }
    connection NC_U1_EN : NoConnect connect U1.en to U1.en;
    connection NC_U2_PG : NoConnect connect U2.pg to U2.pg;
  }
}
