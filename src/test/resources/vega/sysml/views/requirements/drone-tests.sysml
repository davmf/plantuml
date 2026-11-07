// Synthetic verifications of the survey drone: test cases and test runs.
package DroneTests {
	verification def <'TC-001'> 'Hover endurance test' {
		subject dut : 'CONF-1';
		objective {
			verify 'SYS-001';
		}
	}
	verification <'TC-002'> 'Battery alarm test' {
		subject dut : 'CONF-1';
		objective {
			verify 'SW-001';
			verify requirement 'SYS-404';
		}
	}
	verification 'Run 2026-01-12' :> 'TC-001';
	verification 'Run 2026-02-03' :> 'TC-001';
	// A test rig part with the same name as a part of the drone
	part controller;
	analysis <'AN-001'> MassBudget {
		objective {
			verify 'SYS-001.2';
		}
	}
}
