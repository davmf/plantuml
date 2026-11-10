package ClimateDefinitions {
	private import ClimateLibrary::*;

	// The system the use case and the requirement are about
	part def SmartHome {
		attribute currentTemp : ThermodynamicTemperatureValue;
	}

	part def HomeOwner;

	requirement def TemperatureRequirement {
		doc /* The room temperature shall stay below 25 degrees Celsius (298.15 K). */
		subject home : SmartHome;
		require constraint { home.currentTemp <= 298.15[K] }
	}

	use case def ManageClimate {
		subject home : SmartHome;
		actor owner : HomeOwner;
		objective climateGoal {
			doc /* Goal: Maintain safe and comfortable indoor conditions. */
			requirement tempLimit : TemperatureRequirement;
		}
	}
}
