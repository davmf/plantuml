package Home {
    part def SmartHome;
    part def HomeOwner;
    part def Technician;
    part def Garden;
    requirement def <'R-1'> ComfortRequirement {
        doc /* The home shall be comfortable. */
        subject home : SmartHome;
    }
    requirement def <'R-2'> TemperatureRequirement :> ComfortRequirement {
        doc /* The room temperature shall stay below 25 degrees Celsius. */
        subject home : SmartHome;
        attribute limit = 298.15;
        assume constraint { home.occupied == true }
        require constraint { home.currentTemp <= limit and max(a, b) > (c + 1) * 2 }
    }
    requirement <'R-3'> standalone { doc /* Not linked to any use case. */ }
    #derivation dependency from 'R-3' to 'R-1';

    use case def ManageClimate {
        subject home : SmartHome;
        actor owner : HomeOwner;
        actor tech : Technician [0..1];
        objective climateGoal {
            doc /* Maintain safe and comfortable indoor conditions. */
            requirement tempLimit : TemperatureRequirement;
        }
        include use case measure : MeasureTemperature;
        include use case alert : NotifyOwner;
        include Missing;
    }
    use case def MeasureTemperature {
        subject home : SmartHome;
    }
    use case def NotifyOwner {
        subject home : SmartHome;
        actor recipient : HomeOwner;
    }
    use case def WaterPlants {
        subject garden : Garden;
        actor owner : HomeOwner;
        actor weather;
    }
    use case def Service :> ManageClimate {
        actor tech : Technician;
    }
    use case eveningCooling : ManageClimate;
    use case loose { objective { doc /* Just a goal. */ } }
}
