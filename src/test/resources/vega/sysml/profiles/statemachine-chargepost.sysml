package ChargePostStateMachine {
  doc /* Public DC Fast-Charge Post - Controller State Machine. A
     * real-world example exercising every notation the profile
     * supports: simple/composite states, all Pseudostate kinds
     * (initial, final, shallow/deep history, choice, junction,
     * fork/join, terminate, entry/exit point), internal transitions,
     * completion transitions, and a SubmachineState reference into
     * TestStateMachine7 (AuthoriseDriver).
     *
     * Mirrors charge-post-state-machine.puml, the LIFETIME /
     * INSERVICE_BODY / CHARGESESSION_BODY / FINALISING_BODY /
     * POWERDELIVERY_BODY / CABLECOOLING_BODY / CONSTANTCURRENT_BODY /
     * COOLANTRUNNING_BODY blocks (lines 287-717).
     */

  private import StateMachineProfile::*;
  private import AuthoriseDriverStateMachine::*;

  part chargePostSm : StateMachine {
    :>> name = "charge-post";

    item :>> events {
      item mainsApplied           : Event { :>> name = "mains applied"; }
      item selfTestFailed          : Event { :>> name = "selfTestFailed"; }
      item selfTestPassed          : Event { :>> name = "selfTestPassed"; }
      item mainsSagEvt             : Event { :>> name = "mainsSag"; }
      item serviceDoorOpened       : Event { :>> name = "serviceDoorOpened"; }
      item serviceDoorClosed       : Event { :>> name = "serviceDoorClosed"; }
      item technicianLocksOut     : Event { :>> name = "technicianLocksOut"; }
      item technicianAuthenticated : Event { :>> name = "technicianAuthenticated"; }
      item decommissionCommand     : Event { :>> name = "decommissionCommand"; }
      item emergencyStopPressed    : Event { :>> name = "emergencyStopPressed"; }
      item connectorInserted       : Event { :>> name = "connectorInserted"; }
      item cardPresented            : Event { :>> name = "cardPresented"; }
      item screenIdle               : Event { :>> name = "screenIdle"; :>> payload = "30 s"; }
      item gridBrownout             : Event { :>> name = "gridBrownout"; }
      item gridRestored             : Event { :>> name = "gridRestored"; }
      item holdTimeout15min        : Event { :>> name = "holdTimeout"; :>> payload = "15 min"; }
      item holdExpired              : Event { :>> name = "holdExpired"; }
      item cableStowed              : Event { :>> name = "cableStowed"; }
      item captureOk                : Event { :>> name = "captureOk"; }
      item cdrAcknowledged          : Event { :>> name = "cdrAcknowledged"; }
      item currentStable            : Event { :>> name = "currentStable"; }
      item deliveryComplete         : Event { :>> name = "deliveryComplete"; }
    }

    action :>> actions {
      action runSelfTest         : Action { :>> name = "runSelfTest()"; }
      action blinkAmber           : Action { :>> name = "blinkAmber(1 Hz)"; }
      action holdSelfTest         : Action { :>> name = "holdSelfTest()"; }
      action logSelfTest          : Action { :>> name = "logSelfTest(result)"; }
      action attemptIncrement     : Action { :>> name = "attempt++"; }
      action rearmRelays          : Action { :>> name = "rearmRelays()"; }
      action postFaultF01         : Action { :>> name = "postFault(F01)"; }
      action announceAvailable   : Action { :>> name = "announceAvailable()"; }
      action logAccess            : Action { :>> name = "logAccess(technician)"; }
      action inhibitPower         : Action { :>> name = "inhibitPower()"; }
      action streamDiagnostics   : Action { :>> name = "streamDiagnostics()"; }
      action postFaultF99         : Action { :>> name = "postFault(F99)"; }
      action showOutOfService    : Action { :>> name = "showOutOfService()"; }
      action reportFaultToCpo    : Action { :>> name = "reportFaultToCpo(every 5 min)"; }
      action wipeCredentials      : Action { :>> name = "wipeCredentials()"; }
      action openContactors       : Action { :>> name = "openContactors()"; }
      action ventCoolant          : Action { :>> name = "ventCoolant()"; }
      action showHoldMessage      : Action { :>> name = "showHoldMessage()"; }
      action monitorGrid          : Action { :>> name = "monitorGrid()"; }
      action closeContactors      : Action { :>> name = "closeContactors()"; }
      action endSessionEarly      : Action { :>> name = "endSessionEarly()"; }
      action showWelcome          : Action { :>> name = "showWelcome()"; }
      action pollRfidReader       : Action { :>> name = "pollRfidReader()"; }
      action rampCurrent          : Action { :>> name = "rampCurrent(20 A/s)"; }
      action dimDisplay           : Action { :>> name = "dimDisplay()"; }
      action clearDisplay         : Action { :>> name = "clearDisplay()"; }
      action beep2                 : Action { :>> name = "beep(2)"; }
      action lockConnector        : Action { :>> name = "lockConnector()"; }
      action reuseToken            : Action { :>> name = "reuseToken()"; }
      action setLimit500A         : Action { :>> name = "setLimit(500 A)"; }
      action setLimit125A         : Action { :>> name = "setLimit(125 A)"; }
      action warnDerated           : Action { :>> name = "warnDerated()"; }
      action setLimitRequest      : Action { :>> name = "setLimit(request.kW)"; }
      action unlockConnector      : Action { :>> name = "unlockConnector()"; }
      action showRetry             : Action { :>> name = "showRetry()"; }
      action captureAudit         : Action { :>> name = "captureAudit()"; }
      action holdCard60s          : Action { :>> name = "holdCard(60 s)"; }
      action printReceipt         : Action { :>> name = "printReceipt()"; }
      action pumpZero              : Action { :>> name = "pump(0 %)"; }
      action purgeLoop             : Action { :>> name = "purgeLoop()"; }
      action logKnee                : Action { :>> name = "logKnee()"; }
      action holdVoltage           : Action { :>> name = "holdVoltage(pack.vMax)"; }
      action holdCurrentLimit      : Action { :>> name = "holdCurrent(limit)"; }
      action postFaultF31          : Action { :>> name = "postFault(F31)"; }
      action holdCurrentDerated   : Action { :>> name = "holdCurrent(limit * 0.6)"; }
      action clearDerateBanner    : Action { :>> name = "clearDerateBanner()"; }
      action requestPackCooling  : Action { :>> name = "requestPackCooling()"; }
      action holdCurrentThrottled : Action { :>> name = "holdCurrent(30 A)"; }
      action pumpFifty              : Action { :>> name = "pump(50 %)"; }
      action logCableTemp1Hz      : Action { :>> name = "logCableTemp(1 Hz)"; }
      action pumpHundred           : Action { :>> name = "pump(100 %)"; }
      action fansMax                : Action { :>> name = "fansMax()"; }
      action logCableTemp4Hz      : Action { :>> name = "logCableTemp(4 Hz)"; }
      action fansAuto               : Action { :>> name = "fansAuto()"; }
      action captureFunds          : Action { :>> name = "captureFunds(kWh * tariff)"; }
    }

    constraint :>> guards {
      constraint attemptLt3         : Guard { :>> name = "attemptLt3";         :>> expression = "attempt < 3"; }
      constraint attemptGe3         : Guard { :>> name = "attemptGe3";         :>> expression = "attempt >= 3"; }
      constraint notReadable         : Guard { :>> name = "notReadable";        :>> expression = "!isReadable"; }
      constraint connectorLocksOk   : Guard { :>> name = "connectorLocksOk";  :>> expression = "connectorLocks()"; }
      constraint tokenCachedReadable : Guard { :>> name = "tokenCachedReadable"; :>> expression = "tokenCached && isReadable"; }
      constraint kwHigh150DegLow35  : Guard { :>> name = "kwHigh150DegLow35";  :>> expression = "request.kW > 150 && pack.degC < 35"; }
      constraint kwHigh150DegHigh35 : Guard { :>> name = "kwHigh150DegHigh35"; :>> expression = "request.kW > 150 && pack.degC >= 35"; }
      constraint elseProfile         : Guard { :>> name = "elseProfile";        :>> expression = "else"; }
      constraint insulationOk        : Guard { :>> name = "insulationOk";       :>> expression = "insulationOk"; }
      constraint strikesLt2          : Guard { :>> name = "strikesLt2";         :>> expression = "strikes < 2"; }
      constraint strikesGe2          : Guard { :>> name = "strikesGe2";         :>> expression = "strikes >= 2"; }
      constraint socGe80             : Guard { :>> name = "socGe80";            :>> expression = "soc >= 80 %"; }
      constraint currentLt8          : Guard { :>> name = "currentLt8";         :>> expression = "current < 8 A"; }
      constraint socTargetOrStop    : Guard { :>> name = "socTargetOrStop";    :>> expression = "soc >= target || driverStop"; }
      constraint packHot45           : Guard { :>> name = "packHot45";          :>> expression = "pack.degC > 45"; }
      constraint packCool38          : Guard { :>> name = "packCool38";         :>> expression = "pack.degC < 38"; }
      constraint packHot52           : Guard { :>> name = "packHot52";          :>> expression = "pack.degC > 52"; }
      constraint packCool48          : Guard { :>> name = "packCool48";         :>> expression = "pack.degC < 48"; }
      constraint cableHot45          : Guard { :>> name = "cableHot45";         :>> expression = "cable.degC > 45"; }
      constraint cableCool40         : Guard { :>> name = "cableCool40";        :>> expression = "cable.degC < 40"; }
      constraint cableHot65          : Guard { :>> name = "cableHot65";         :>> expression = "cable.degC > 65"; }
      constraint cableCool60         : Guard { :>> name = "cableCool60";        :>> expression = "cable.degC < 60"; }
    }

    // ==================================================================
    // Top region - PoweringUp / InService / ServiceMode / OutOfService /
    // HardStop / Decommissioned. Mirrors LIFETIME.
    // ==================================================================
    part :>> top : Region {
      :>> name = "top";

      part lifetimeStart : Pseudostate {
        :>> name = "LifetimeStart";
        :>> kind = PseudostateKind::initial;
      }

      part hardStop : Pseudostate {
        :>> name = "HardStop";
        :>> kind = PseudostateKind::terminateNode;
      }

      part decommissioned : State {
        :>> name = "Decommissioned";
      }

      part poweringUp : State {
        :>> name         = "PoweringUp";
        :>> entryActions = (chargePostSm.actions.runSelfTest);
        :>> doActions    = (chargePostSm.actions.blinkAmber);
        :>> exitActions  = (chargePostSm.actions.logSelfTest);
      }

      part serviceMode : State {
        :>> name         = "ServiceMode";
        :>> entryActions = (chargePostSm.actions.inhibitPower);
        :>> doActions    = (chargePostSm.actions.streamDiagnostics);
      }

      part outOfService : State {
        :>> name         = "OutOfService";
        :>> entryActions = (chargePostSm.actions.showOutOfService);
        :>> doActions    = (chargePostSm.actions.reportFaultToCpo);
      }

      // ---- InService: composite state, own body in the nested part below ----
      part inService : CompositeState {
        :>> name = "InService";

        part :>> regions {
          part inServiceRegion : Region {
            :>> name = "InService";

            part inServiceStart : Pseudostate {
              :>> name = "InServiceStart";
              :>> kind = PseudostateKind::initial;
            }

            part serviceHistory : Pseudostate {
              :>> name = "[H]";
              :>> kind = PseudostateKind::shallowHistory;
            }

            part waitingForDriver : State {
              :>> name         = "WaitingForDriver";
              :>> entryActions = (chargePostSm.actions.showWelcome);
              :>> doActions    = (chargePostSm.actions.pollRfidReader);
              :>> exitActions  = (chargePostSm.actions.clearDisplay);
            }

            // Submachine reference - contents live in TestStateMachine7.
            part authorising : SubmachineState {
              :>> name       = "Authorising";
              :>> submachine = authoriseDriverSm;

              part :>> entryPoints {
                part authFull   : Pseudostate { :>> name = "full";        :>> kind = PseudostateKind::entryPoint; }
                part authCached : Pseudostate { :>> name = "cachedToken"; :>> kind = PseudostateKind::entryPoint; }
              }
              part :>> exitPoints {
                part authOk : Pseudostate { :>> name = "authorised"; :>> kind = PseudostateKind::exitPoint; }
                part authNo : Pseudostate { :>> name = "refused";    :>> kind = PseudostateKind::exitPoint; }
              }
            }

            part pickProfile : Pseudostate {
              :>> name = "PickProfile";
              :>> kind = PseudostateKind::choice;
            }

            part retryOrBlock : Pseudostate {
              :>> name = "RetryOrBlock";
              :>> kind = PseudostateKind::choice;
            }

            part cardBlocked : State {
              :>> name         = "CardBlocked";
              :>> entryActions = (chargePostSm.actions.holdCard60s);
            }

            part suspended : State {
              :>> name         = "Suspended";
              :>> entryActions = (chargePostSm.actions.openContactors, chargePostSm.actions.showHoldMessage);
              :>> doActions    = (chargePostSm.actions.monitorGrid);
            }

            // ---- Charge Session: composite state, own body nested below ----
            part chargeSession : CompositeState {
              :>> name = "Charge Session";

              part :>> regions {
                part chargeSessionRegion : Region {
                  :>> name = "Charge Session";

                  part deliveringEnergyFork : Pseudostate {
                    :>> name = "DeliveringEnergyFork";
                    :>> kind = PseudostateKind::forkNode;
                  }
                  part chargeSessionJoin : Pseudostate {
                    :>> name = "ChargeSessionJoin";
                    :>> kind = PseudostateKind::joinNode;
                  }
                  part chargeSessionStart : Pseudostate {
                    :>> name = "ChargeSessionStart";
                    :>> kind = PseudostateKind::initial;
                  }
                  part chargeSessionHistory : Pseudostate {
                    :>> name = "[H*]";
                    :>> kind = PseudostateKind::deepHistory;
                  }
                  part chargeSessionEnd : Pseudostate {
                    :>> name = "ChargeSessionEnd";
                    :>> kind = PseudostateKind::final;
                  }

                  // ---- Power Delivery: composite, own body nested below ----
                  part powerDelivery : CompositeState {
                    :>> name = "Power Delivery";

                    part :>> regions {
                      part powerDeliveryRegion : Region {
                        :>> name = "Power Delivery";

                        part powerDeliveryStart : Pseudostate {
                          :>> name = "PowerDeliveryStart";
                          :>> kind = PseudostateKind::initial;
                        }
                        part powerDeliveryEnd : Pseudostate {
                          :>> name = "PowerDeliveryEnd";
                          :>> kind = PseudostateKind::final;
                        }

                        part rampingUp : State {
                          :>> name         = "RampingUp";
                          :>> entryActions = (chargePostSm.actions.closeContactors);
                          :>> doActions    = (chargePostSm.actions.rampCurrent);
                        }

                        part constantVoltage : State {
                          :>> name      = "ConstantVoltage";
                          :>> doActions = (chargePostSm.actions.holdVoltage);
                        }

                        part toppingOff : State {
                          :>> name = "ToppingOff";
                        }

                        // ---- Constant Current: composite, own body below ----
                        part constantCurrent : CompositeState {
                          :>> name = "Constant Current";

                          part :>> regions {
                            part constantCurrentRegion : Region {
                              :>> name = "Constant Current";

                              part constantCurrentStart : Pseudostate {
                                :>> name = "ConstantCurrentStart";
                                :>> kind = PseudostateKind::initial;
                              }

                              part fullRate : State {
                                :>> name      = "FullRate";
                                :>> doActions = (chargePostSm.actions.holdCurrentLimit);
                              }

                              part derated : State {
                                :>> name         = "Derated";
                                :>> doActions    = (chargePostSm.actions.holdCurrentDerated);
                                :>> exitActions  = (chargePostSm.actions.clearDerateBanner);
                              }

                              part throttled : State {
                                :>> name         = "Throttled";
                                :>> entryActions = (chargePostSm.actions.requestPackCooling);
                                :>> doActions    = (chargePostSm.actions.holdCurrentThrottled);
                              }

                              connection ccStartToFullRate : Transition connect constantCurrentStart.outgoing to fullRate.incoming {
                                :>> name = "ConstantCurrentStart -> FullRate";
                                :>> kind = TransitionKind::external;
                                :>> classification = TransitionClassification::primary;
                              }
                              connection fullRateToDerated : Transition connect fullRate.outgoing to derated.incoming {
                                :>> name           = "FullRate -> Derated";
                                :>> guards         = (chargePostSm.guards.packHot45);
                                :>> actions        = (chargePostSm.actions.warnDerated);
                                :>> kind           = TransitionKind::external;
                                :>> classification = TransitionClassification::secondary;
                              }
                              connection deratedToFullRate : Transition connect derated.outgoing to fullRate.incoming {
                                :>> name           = "Derated -> FullRate";
                                :>> guards         = (chargePostSm.guards.packCool38);
                                :>> kind           = TransitionKind::external;
                                :>> classification = TransitionClassification::secondary;
                              }
                              connection deratedToThrottled : Transition connect derated.outgoing to throttled.incoming {
                                :>> name           = "Derated -> Throttled";
                                :>> guards         = (chargePostSm.guards.packHot52);
                                :>> actions        = (chargePostSm.actions.postFaultF31);
                                :>> kind           = TransitionKind::external;
                                :>> classification = TransitionClassification::secondary;
                              }
                              connection throttledToDerated : Transition connect throttled.outgoing to derated.incoming {
                                :>> name           = "Throttled -> Derated";
                                :>> guards         = (chargePostSm.guards.packCool48);
                                :>> kind           = TransitionKind::external;
                                :>> classification = TransitionClassification::secondary;
                              }
                            }
                          }
                        }

                        connection pdStartToRampingUp : Transition connect powerDeliveryStart.outgoing to rampingUp.incoming {
                          :>> name = "PowerDeliveryStart -> RampingUp";
                          :>> kind = TransitionKind::external;
                          :>> classification = TransitionClassification::primary;
                        }
                        connection rampingUpToConstantCurrent : Transition connect rampingUp.outgoing to constantCurrent.incoming {
                          :>> name           = "RampingUp -> Constant Current";
                          :>> trigger        = chargePostSm.events.currentStable;
                          :>> guards         = (chargePostSm.guards.insulationOk);
                          :>> kind           = TransitionKind::external;
                          :>> classification = TransitionClassification::primary;
                        }
                        connection constantCurrentToConstantVoltage : Transition connect constantCurrent.outgoing to constantVoltage.incoming {
                          :>> name           = "Constant Current -> ConstantVoltage";
                          :>> guards         = (chargePostSm.guards.socGe80);
                          :>> actions        = (chargePostSm.actions.logKnee);
                          :>> kind           = TransitionKind::external;
                          :>> classification = TransitionClassification::primary;
                        }
                        connection constantVoltageToToppingOff : Transition connect constantVoltage.outgoing to toppingOff.incoming {
                          :>> name           = "ConstantVoltage -> ToppingOff";
                          :>> guards         = (chargePostSm.guards.currentLt8);
                          :>> kind           = TransitionKind::external;
                          :>> classification = TransitionClassification::primary;
                        }
                        connection toppingOffToEnd : Transition connect toppingOff.outgoing to powerDeliveryEnd.incoming {
                          :>> name           = "ToppingOff -> PowerDeliveryEnd";
                          :>> guards         = (chargePostSm.guards.socTargetOrStop);
                          :>> kind           = TransitionKind::external;
                          :>> classification = TransitionClassification::primary;
                        }
                      }
                    }
                  }

                  // ---- Cable Cooling: composite, own body nested below ----
                  part cableCooling : CompositeState {
                    :>> name = "Cable Cooling";

                    part :>> regions {
                      part cableCoolingRegion : Region {
                        :>> name = "Cable Cooling";

                        part cableCoolingStart : Pseudostate {
                          :>> name = "CableCoolingStart";
                          :>> kind = PseudostateKind::initial;
                        }
                        part cableCoolingEnd : Pseudostate {
                          :>> name = "CableCoolingEnd";
                          :>> kind = PseudostateKind::final;
                        }

                        part coolantIdle : State {
                          :>> name = "CoolantIdle";
                        }

                        // ---- Coolant Running: composite, own body below ----
                        part coolantRunning : CompositeState {
                          :>> name = "Coolant Running";

                          part :>> regions {
                            part coolantRunningRegion : Region {
                              :>> name = "Coolant Running";

                              part coolantRunningStart : Pseudostate {
                                :>> name = "CoolantRunningStart";
                                :>> kind = PseudostateKind::initial;
                              }

                              part pumpModerate : State {
                                :>> name         = "PumpModerate";
                                :>> entryActions = (chargePostSm.actions.pumpFifty);
                                :>> doActions    = (chargePostSm.actions.logCableTemp1Hz);
                              }

                              part pumpBoost : State {
                                :>> name      = "PumpBoost";
                                :>> doActions = (chargePostSm.actions.logCableTemp4Hz);
                              }

                              connection crStartToPumpModerate : Transition connect coolantRunningStart.outgoing to pumpModerate.incoming {
                                :>> name = "CoolantRunningStart -> PumpModerate";
                                :>> kind = TransitionKind::external;
                                :>> classification = TransitionClassification::secondary;
                              }
                              connection pumpModerateToBoost : Transition connect pumpModerate.outgoing to pumpBoost.incoming {
                                :>> name           = "PumpModerate -> PumpBoost";
                                :>> guards         = (chargePostSm.guards.cableHot65);
                                :>> actions        = (chargePostSm.actions.pumpHundred, chargePostSm.actions.fansMax);
                                :>> kind           = TransitionKind::external;
                                :>> classification = TransitionClassification::secondary;
                              }
                              connection pumpBoostToModerate : Transition connect pumpBoost.outgoing to pumpModerate.incoming {
                                :>> name           = "PumpBoost -> PumpModerate";
                                :>> guards         = (chargePostSm.guards.cableCool60);
                                :>> actions        = (chargePostSm.actions.pumpFifty, chargePostSm.actions.fansAuto);
                                :>> kind           = TransitionKind::external;
                                :>> classification = TransitionClassification::secondary;
                              }
                            }
                          }
                        }

                        connection ccStartToCoolantIdle : Transition connect cableCoolingStart.outgoing to coolantIdle.incoming {
                          :>> name = "CableCoolingStart -> CoolantIdle";
                          :>> kind = TransitionKind::external;
                          :>> classification = TransitionClassification::primary;
                        }
                        connection coolantIdleToRunning : Transition connect coolantIdle.outgoing to coolantRunning.incoming {
                          :>> name           = "CoolantIdle -> Coolant Running";
                          :>> guards         = (chargePostSm.guards.cableHot45);
                          :>> kind           = TransitionKind::external;
                          :>> classification = TransitionClassification::secondary;
                        }
                        connection coolantRunningToIdle : Transition connect coolantRunning.outgoing to coolantIdle.incoming {
                          :>> name           = "Coolant Running -> CoolantIdle";
                          :>> guards         = (chargePostSm.guards.cableCool40);
                          :>> actions        = (chargePostSm.actions.pumpZero);
                          :>> kind           = TransitionKind::external;
                          :>> classification = TransitionClassification::secondary;
                        }
                        connection coolantIdleToEnd : Transition connect coolantIdle.outgoing to cableCoolingEnd.incoming {
                          :>> name           = "CoolantIdle -> CableCoolingEnd";
                          :>> trigger        = chargePostSm.events.deliveryComplete;
                          :>> kind           = TransitionKind::external;
                          :>> classification = TransitionClassification::primary;
                        }
                        connection coolantRunningToEnd : Transition connect coolantRunning.outgoing to cableCoolingEnd.incoming {
                          :>> name           = "Coolant Running -> CableCoolingEnd";
                          :>> trigger        = chargePostSm.events.deliveryComplete;
                          :>> actions        = (chargePostSm.actions.purgeLoop);
                          :>> kind           = TransitionKind::external;
                          :>> classification = TransitionClassification::secondary;
                        }
                      }
                    }
                  }

                  connection csStartToHistory : Transition connect chargeSessionStart.outgoing to chargeSessionHistory.incoming {
                    :>> name = "ChargeSessionStart -> [H*]";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection historyToFork : Transition connect chargeSessionHistory.outgoing to deliveringEnergyFork.incoming {
                    :>> name = "[H*] -> DeliveringEnergyFork";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection forkToPowerDelivery : Transition connect deliveringEnergyFork.outgoing to powerDelivery.incoming {
                    :>> name = "DeliveringEnergyFork -> Power Delivery";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection forkToCableCooling : Transition connect deliveringEnergyFork.outgoing to cableCooling.incoming {
                    :>> name = "DeliveringEnergyFork -> Cable Cooling";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection powerDeliveryToJoin : Transition connect powerDelivery.outgoing to chargeSessionJoin.incoming {
                    :>> name = "Power Delivery -> ChargeSessionJoin";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection cableCoolingToJoin : Transition connect cableCooling.outgoing to chargeSessionJoin.incoming {
                    :>> name = "Cable Cooling -> ChargeSessionJoin";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection joinToEnd : Transition connect chargeSessionJoin.outgoing to chargeSessionEnd.incoming {
                    :>> name = "ChargeSessionJoin -> ChargeSessionEnd";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                }
              }
            }

            // ---- Finalising Session: composite, own body nested below ----
            part finalising : CompositeState {
              :>> name = "Finalising Session";

              part :>> regions {
                part finalisingRegion : Region {
                  :>> name = "Finalising Session";

                  part releaseFork : Pseudostate {
                    :>> name = "ReleaseFork";
                    :>> kind = PseudostateKind::forkNode;
                  }
                  part releaseJoin : Pseudostate {
                    :>> name = "ReleaseJoin";
                    :>> kind = PseudostateKind::joinNode;
                  }
                  part finalisingStart : Pseudostate {
                    :>> name = "FinalisingStart";
                    :>> kind = PseudostateKind::initial;
                  }
                  part finalisingEnd : Pseudostate {
                    :>> name = "FinalisingEnd";
                    :>> kind = PseudostateKind::final;
                  }

                  part unlockingConnector : State {
                    :>> name         = "UnlockingConnector";
                    :>> entryActions = (chargePostSm.actions.unlockConnector);
                  }
                  part settlingPayment : State {
                    :>> name         = "SettlingPayment";
                    :>> entryActions = (chargePostSm.actions.captureFunds);
                  }
                  part uploadingRecord : State {
                    :>> name = "UploadingRecord";
                  }

                  connection finalisingStartToFork : Transition connect finalisingStart.outgoing to releaseFork.incoming {
                    :>> name = "FinalisingStart -> ReleaseFork";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection forkToUnlocking : Transition connect releaseFork.outgoing to unlockingConnector.incoming {
                    :>> name = "ReleaseFork -> UnlockingConnector";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection unlockingToJoin : Transition connect unlockingConnector.outgoing to releaseJoin.incoming {
                    :>> name           = "UnlockingConnector -> ReleaseJoin";
                    :>> trigger        = chargePostSm.events.cableStowed;
                    :>> kind           = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection forkToSettling : Transition connect releaseFork.outgoing to settlingPayment.incoming {
                    :>> name = "ReleaseFork -> SettlingPayment";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection settlingToUploading : Transition connect settlingPayment.outgoing to uploadingRecord.incoming {
                    :>> name           = "SettlingPayment -> UploadingRecord";
                    :>> trigger        = chargePostSm.events.captureOk;
                    :>> kind           = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection uploadingToJoin : Transition connect uploadingRecord.outgoing to releaseJoin.incoming {
                    :>> name           = "UploadingRecord -> ReleaseJoin";
                    :>> trigger        = chargePostSm.events.cdrAcknowledged;
                    :>> kind           = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                  connection joinToFinalisingEnd : Transition connect releaseJoin.outgoing to finalisingEnd.incoming {
                    :>> name = "ReleaseJoin -> FinalisingEnd";
                    :>> kind = TransitionKind::external;
                    :>> classification = TransitionClassification::primary;
                  }
                }
              }
            }

            // ---- InService-level transitions ----
            connection inServiceStartToHistory : Transition connect inServiceStart.outgoing to serviceHistory.incoming {
              :>> name = "InServiceStart -> [H]";
              :>> kind = TransitionKind::external;
              :>> classification = TransitionClassification::primary;
            }
            connection historyToWaiting : Transition connect serviceHistory.outgoing to waitingForDriver.incoming {
              :>> name = "[H] -> WaitingForDriver";
              :>> kind = TransitionKind::external;
              :>> classification = TransitionClassification::primary;
            }
            connection waitingSelfLoop : Transition connect waitingForDriver.outgoing to waitingForDriver.incoming {
              :>> name           = "WaitingForDriver -> WaitingForDriver (cardPresented, unreadable)";
              :>> trigger        = chargePostSm.events.cardPresented;
              :>> guards         = (chargePostSm.guards.notReadable);
              :>> actions        = (chargePostSm.actions.beep2);
              :>> kind           = TransitionKind::internal;
              :>> classification = TransitionClassification::secondary;
            }
            connection waitingScreenIdle : Transition connect waitingForDriver.outgoing to waitingForDriver.incoming {
              :>> name           = "WaitingForDriver -> WaitingForDriver (screenIdle, internal)";
              :>> trigger        = chargePostSm.events.screenIdle;
              :>> actions        = (chargePostSm.actions.dimDisplay);
              :>> kind           = TransitionKind::internal;
              :>> classification = TransitionClassification::secondary;
            }
            connection waitingToAuthFull : Transition connect waitingForDriver.outgoing to authorising.entryPoints.authFull.incoming {
              :>> name           = "WaitingForDriver -> Authorising.full";
              :>> trigger        = chargePostSm.events.connectorInserted;
              :>> guards         = (chargePostSm.guards.connectorLocksOk);
              :>> actions        = (chargePostSm.actions.lockConnector);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::primary;
            }
            connection waitingToAuthCached : Transition connect waitingForDriver.outgoing to authorising.entryPoints.authCached.incoming {
              :>> name           = "WaitingForDriver -> Authorising.cachedToken";
              :>> trigger        = chargePostSm.events.cardPresented;
              :>> guards         = (chargePostSm.guards.tokenCachedReadable);
              :>> actions        = (chargePostSm.actions.reuseToken);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::secondary;
            }
            connection authOkToPickProfile : Transition connect authorising.exitPoints.authOk.outgoing to pickProfile.incoming {
              :>> name = "Authorising.authorised -> PickProfile";
              :>> kind = TransitionKind::external;
              :>> classification = TransitionClassification::primary;
            }
            connection pickProfileHighKwCool : Transition connect pickProfile.outgoing to chargeSession.incoming {
              :>> name           = "PickProfile -> Charge Session (high kW, cool pack)";
              :>> guards         = (chargePostSm.guards.kwHigh150DegLow35);
              :>> actions        = (chargePostSm.actions.setLimit500A);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::primary;
            }
            connection pickProfileHighKwHot : Transition connect pickProfile.outgoing to chargeSession.incoming {
              :>> name           = "PickProfile -> Charge Session (high kW, hot pack)";
              :>> guards         = (chargePostSm.guards.kwHigh150DegHigh35);
              :>> actions        = (chargePostSm.actions.setLimit125A, chargePostSm.actions.warnDerated);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::secondary;
            }
            connection pickProfileElse : Transition connect pickProfile.outgoing to chargeSession.incoming {
              :>> name           = "PickProfile -> Charge Session (else)";
              :>> guards         = (chargePostSm.guards.elseProfile);
              :>> actions        = (chargePostSm.actions.setLimitRequest);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::secondary;
            }
            connection authNoToRetryOrBlock : Transition connect authorising.exitPoints.authNo.outgoing to retryOrBlock.incoming {
              :>> name = "Authorising.refused -> RetryOrBlock";
              :>> kind = TransitionKind::external;
              :>> classification = TransitionClassification::exception;
            }
            connection retryOrBlockRetry : Transition connect retryOrBlock.outgoing to waitingForDriver.incoming {
              :>> name           = "RetryOrBlock -> WaitingForDriver";
              :>> guards         = (chargePostSm.guards.strikesLt2);
              :>> actions        = (chargePostSm.actions.unlockConnector, chargePostSm.actions.showRetry);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::exception;
            }
            connection retryOrBlockBlock : Transition connect retryOrBlock.outgoing to cardBlocked.incoming {
              :>> name           = "RetryOrBlock -> CardBlocked";
              :>> guards         = (chargePostSm.guards.strikesGe2);
              :>> actions        = (chargePostSm.actions.captureAudit);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::exception;
            }
            connection cardBlockedToWaiting : Transition connect cardBlocked.outgoing to waitingForDriver.incoming {
              :>> name           = "CardBlocked -> WaitingForDriver";
              :>> trigger        = chargePostSm.events.holdExpired;
              :>> actions        = (chargePostSm.actions.unlockConnector);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::exception;
            }
            connection chargeSessionToSuspended : Transition connect chargeSession.outgoing to suspended.incoming {
              :>> name           = "Charge Session -> Suspended";
              :>> trigger        = chargePostSm.events.gridBrownout;
              :>> actions        = (chargePostSm.actions.openContactors);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::secondary;
            }
            connection suspendedToChargeSession : Transition connect suspended.outgoing to chargeSession.incoming {
              :>> name           = "Suspended -> Charge Session";
              :>> trigger        = chargePostSm.events.gridRestored;
              :>> guards         = (chargePostSm.guards.insulationOk);
              :>> actions        = (chargePostSm.actions.closeContactors);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::secondary;
            }
            connection suspendedToRetryOrBlock : Transition connect suspended.outgoing to retryOrBlock.incoming {
              :>> name           = "Suspended -> RetryOrBlock";
              :>> trigger        = chargePostSm.events.holdTimeout15min;
              :>> actions        = (chargePostSm.actions.endSessionEarly);
              :>> kind           = TransitionKind::external;
              :>> classification = TransitionClassification::exception;
            }
            connection chargeSessionToFinalising : Transition connect chargeSession.outgoing to finalising.incoming {
              :>> name = "Charge Session -> Finalising Session";
              :>> kind = TransitionKind::external;
              :>> classification = TransitionClassification::primary;
            }
            connection finalisingToWaiting : Transition connect finalising.outgoing to waitingForDriver.incoming {
              :>> name    = "Finalising Session -> WaitingForDriver";
              :>> actions = (chargePostSm.actions.printReceipt);
              :>> kind    = TransitionKind::external;
              :>> classification = TransitionClassification::primary;
            }
          }
        }
      }

      // ---- Top-level transitions ----
      connection lifetimeStartToPoweringUp : Transition connect lifetimeStart.outgoing to poweringUp.incoming {
        :>> name           = "LifetimeStart -> PoweringUp";
        :>> trigger        = chargePostSm.events.mainsApplied;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection poweringUpRetry : Transition connect poweringUp.outgoing to poweringUp.incoming {
        :>> name           = "PoweringUp -> PoweringUp (selfTestFailed, retry)";
        :>> trigger        = chargePostSm.events.selfTestFailed;
        :>> guards         = (chargePostSm.guards.attemptLt3);
        :>> actions        = (chargePostSm.actions.attemptIncrement, chargePostSm.actions.rearmRelays);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection poweringUpToOutOfService : Transition connect poweringUp.outgoing to outOfService.incoming {
        :>> name           = "PoweringUp -> OutOfService";
        :>> trigger        = chargePostSm.events.selfTestFailed;
        :>> guards         = (chargePostSm.guards.attemptGe3);
        :>> actions        = (chargePostSm.actions.postFaultF01);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
      connection poweringUpToInService : Transition connect poweringUp.outgoing to inService.incoming {
        :>> name           = "PoweringUp -> InService";
        :>> trigger        = chargePostSm.events.selfTestPassed;
        :>> actions        = (chargePostSm.actions.announceAvailable);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection poweringUpMainsSag : Transition connect poweringUp.outgoing to poweringUp.incoming {
        :>> name           = "PoweringUp -> PoweringUp (mainsSag, internal)";
        :>> trigger        = chargePostSm.events.mainsSagEvt;
        :>> actions        = (chargePostSm.actions.holdSelfTest);
        :>> kind           = TransitionKind::internal;
        :>> classification = TransitionClassification::secondary;
      }
      connection inServiceToServiceMode : Transition connect inService.outgoing to serviceMode.incoming {
        :>> name           = "InService -> ServiceMode";
        :>> trigger        = chargePostSm.events.serviceDoorOpened;
        :>> actions        = (chargePostSm.actions.logAccess);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection serviceModeToInService : Transition connect serviceMode.outgoing to inService.incoming {
        :>> name           = "ServiceMode -> InService";
        :>> trigger        = chargePostSm.events.serviceDoorClosed;
        :>> actions        = (chargePostSm.actions.rearmRelays);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }
      connection serviceModeToOutOfService : Transition connect serviceMode.outgoing to outOfService.incoming {
        :>> name           = "ServiceMode -> OutOfService";
        :>> trigger        = chargePostSm.events.technicianLocksOut;
        :>> actions        = (chargePostSm.actions.postFaultF99);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
      connection outOfServiceToServiceMode : Transition connect outOfService.outgoing to serviceMode.incoming {
        :>> name           = "OutOfService -> ServiceMode";
        :>> trigger        = chargePostSm.events.technicianAuthenticated;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
      connection outOfServiceToDecommissioned : Transition connect outOfService.outgoing to decommissioned.incoming {
        :>> name           = "OutOfService -> Decommissioned";
        :>> trigger        = chargePostSm.events.decommissionCommand;
        :>> actions        = (chargePostSm.actions.wipeCredentials);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
      connection inServiceToHardStop : Transition connect inService.outgoing to hardStop.incoming {
        :>> name           = "InService -> HardStop";
        :>> trigger        = chargePostSm.events.emergencyStopPressed;
        :>> actions        = (chargePostSm.actions.openContactors, chargePostSm.actions.ventCoolant);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
      connection serviceModeToHardStop : Transition connect serviceMode.outgoing to hardStop.incoming {
        :>> name           = "ServiceMode -> HardStop";
        :>> trigger        = chargePostSm.events.emergencyStopPressed;
        :>> actions        = (chargePostSm.actions.openContactors);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
    }
  }
}
