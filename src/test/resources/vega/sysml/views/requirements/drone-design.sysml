// Synthetic design of the survey drone: what satisfies the requirements.
package DroneDesign {
	part def SurveyDrone;
	part def Battery;

	part <'CONF-1'> prototype : SurveyDrone {
		part battery : Battery;
		part controller;
		satisfy 'SYS-001.1' by battery;
	}
	satisfy 'SYS-001' by prototype;
	satisfy 'SYS-002' by prototype.controller.alarmPort;
	satisfy 'SW-002' by missingUnit;

	action landing;
	allocate landing to prototype.controller;
}
