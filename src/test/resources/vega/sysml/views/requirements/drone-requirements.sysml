// Synthetic requirements of a small survey drone, for the requirements view tests.
package DroneRequirements {
	doc /* Requirements of the SkyCount survey drone. */
	private import ScalarValues::*;

	requirement def <'RD-1'> SafetyRequirement {
		doc /* A requirement whose violation can hurt people. */
	}

	package System {
		requirement <'SYS-001'> 'Flight time' {
			doc /* The drone shall hover for at least 25 minutes with the survey camera
			 * fitted, at 20 degrees Celsius and sea level, with a fully charged battery
			 * and no wind, measured from take-off to the low battery landing. */
			attribute minutes : Real = 25;
			subject drone : SurveyDrone;

			requirement <'SYS-001.1'> 'Battery capacity' {
				doc /* The battery shall store at least 90 Wh. */
			}
			requirement <'SYS-001.2'> 'Take-off mass' {
				doc /* The take-off mass shall not exceed 2.5 kg. */
			}
		}
		requirement <'SYS-002'> 'Low battery landing' {
			doc /* The drone shall land when the battery charge falls below 15 %. */
		}
		requirement <'SAF-001'> Geofence : SafetyRequirement {
			doc /* The drone shall not leave the surveyed area. */
		}
	}

	package Software {
		requirement <'SW-001'> BatteryMonitor {
			doc /* The flight software shall estimate the battery charge every second. */
		}
		requirement <'SW-002'> LandingController {
			doc /* The flight software shall start a landing within 2 s of a low battery alarm. */
		}
		requirement <'SW-003'> GeofenceCheck;
		requirement Logging;
	}

	#derivation dependency from 'SW-001' to 'SYS-002';
	#derivation dependency from 'SW-002' to 'SYS-002', 'SYS-003';
	#refinement dependency from 'SW-003' to 'SAF-001';
	dependency from Logging to 'SW-001';
	#derivation connection {
		end #original ::> 'SYS-001';
		end #derive ::> 'SW-001';
	}
}
