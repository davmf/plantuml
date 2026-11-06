package VehicleModel {
  doc /* A small synthetic vehicle model for the definitions view. */

  private import ScalarValues::*;

  package Interfaces {
    port def FuelPort {
      doc /* Where the fuel {liquid} comes in. */
      in item fuel : Fuel;
      attribute flowRate : Real;
      part cap : Cap;
    }
    part def Cap;
    item def Fuel {
      attribute octane : Integer = 95;
    }
    interface def FuelLine {
      end source : FuelPort;
      end target : FuelPort;
      attribute length : ISQ::LengthValue;
    }
  }

  enum def Colour {
    enum red;
    enum green;
    blue;
    enum 'metallic (premium)';
  }

  calc def Range {
    in fuelVolume : ISQ::VolumeValue;
    in consumption : Real;
    return distance : ISQ::LengthValue = fuelVolume / max(consumption, 1);
  }

  attribute def Dimensions {
    attribute length : ISQ::LengthValue;
    attribute width : ISQ::LengthValue[0..1];
  }

  abstract part def Vehicle {
    doc /* Anything with wheels.
       * Abstract: see Car and Truck. */
    attribute mass : ISQ::MassValue;
    attribute paint : Colour = Colour::red;
    attribute size : Dimensions;
    part engine : Engine;
    part wheels : Wheel[4..*];
    port fuelIn : FuelPort;
    ref part driver : Person[0..1];
    // Cap here is VehicleModel::Cap, not Interfaces::Cap
    part spareCap : Cap[0..1];
    part radio : Accessories::Radio[0..1];
  }

  part def Car :> Vehicle {
    attribute doors : Integer[1] default 4;
    part trailer : Trailer[0..1];
    part def Trailer {
      attribute axles : Integer;
    }
  }

  part def Truck :> Vehicle, Hauler {
    attribute :>> mass = 7500;
  }

  part def Engine {
    attribute cylinders : Integer;
    action def Start {
      in item key : Key;
      out attribute ok : Boolean;
    }
  }

  part def Wheel {
    @Annotations::Reviewed { by = "QA"; }
  }
  part def Person;
  part def Cap;
  item def Key;

  requirement def MassLimit {
    doc /* The vehicle mass shall not exceed the limit. */
    subject vehicle : Vehicle;
    attribute limit : ISQ::MassValue;
  }

  library package Parts {
    part def Bolt {
      attribute size : String;
    }
  }
}
