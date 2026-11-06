package AuthoriseDriverStateMachine {
  doc /* AuthoriseDriver - the sub-machine referenced by the 'Authorising'
     * SubmachineState in TestStateMachine6 (charge-post). Exposes two
     * named entry points (full, cachedToken) and two named exit points
     * (authorised, refused) on its top Region's boundary, matched by
     * name against the SubmachineState's own entryPoints/exitPoints per
     * SubmachineEntryExitCorrespondence.
     *
     * Mirrors charge-post-state-machine.puml, the 'authorise-driver'
     * diagram / AUTHORISE block (lines 44-59, 719-772).
     */

  private import StateMachineProfile::*;

  part authoriseDriverSm : StateMachine {
    :>> name = "AuthoriseDriver";

    item :>> events {
      item readerReady              : Event { :>> name = "readerReady"; }
      item readerFault               : Event { :>> name = "readerFault"; :>> payload = "3 tries"; }
      item credentialSeen            : Event { :>> name = "credentialSeen"; }
      item credentialCaptured        : Event { :>> name = "credentialCaptured"; }
      item idleTimeout                : Event { :>> name = "idleTimeout"; :>> payload = "60 s"; }
      item tokenAccepted             : Event { :>> name = "tokenAccepted"; }
      item backOfficeUnreachable     : Event { :>> name = "backOfficeUnreachable"; :>> payload = "5 s"; }
    }

    action :>> actions {
      action powerReader        : Action { :>> name = "powerReader()"; }
      action handshakeReader     : Action { :>> name = "handshakeReader()"; }
      action clearReaderBuffer   : Action { :>> name = "clearReaderBuffer()"; }
      action postFaultF12        : Action { :>> name = "postFault(F12)"; }
      action awaitTapOrApp        : Action { :>> name = "awaitTapOrApp()"; }
      action beep2                : Action { :>> name = "beep(2)"; }
      action hashCredential       : Action { :>> name = "hash(credential)"; }
      action showTimeout          : Action { :>> name = "showTimeout()"; }
      action queryBackOffice      : Action { :>> name = "queryBackOffice()"; }
      action spinner               : Action { :>> name = "spinner()"; }
      action issueSessionId       : Action { :>> name = "issueSessionId()"; }
      action flagDeferredBilling : Action { :>> name = "flagDeferredBilling()"; }
      action showTryLater         : Action { :>> name = "showTryLater()"; }
    }

    constraint :>> guards {
      constraint notWellFormed    : Guard { :>> name = "notWellFormed";    :>> expression = "!wellFormed"; }
      constraint tokenOnWhitelist : Guard { :>> name = "tokenOnWhitelist"; :>> expression = "tokenOnWhitelist"; }
      constraint elseGuard         : Guard { :>> name = "else";             :>> expression = "else"; }
    }

    part :>> top : Region {
      :>> name = "AuthoriseDriver";

      // Boundary pins - matched by name against the Authorising
      // SubmachineState's entryPoints/exitPoints in TestStateMachine6.
      part epFull   : Pseudostate { :>> name = "full";        :>> kind = PseudostateKind::entryPoint; }
      part epCached : Pseudostate { :>> name = "cachedToken"; :>> kind = PseudostateKind::entryPoint; }
      part xpOk     : Pseudostate { :>> name = "authorised";  :>> kind = PseudostateKind::exitPoint; }
      part xpNo     : Pseudostate { :>> name = "refused";     :>> kind = PseudostateKind::exitPoint; }

      part driverStart : Pseudostate {
        :>> name = "AuthoriseDriverStart";
        :>> kind = PseudostateKind::initial;
      }

      part initialisingReader : State {
        :>> name         = "InitialisingReader";
        :>> entryActions = (authoriseDriverSm.actions.powerReader);
        :>> doActions    = (authoriseDriverSm.actions.handshakeReader);
        :>> exitActions  = (authoriseDriverSm.actions.clearReaderBuffer);
      }

      part awaitingCredential : State {
        :>> name      = "AwaitingCredential";
        :>> doActions = (authoriseDriverSm.actions.awaitTapOrApp);
      }

      part validatingToken : State {
        :>> name         = "ValidatingToken";
        :>> entryActions = (authoriseDriverSm.actions.queryBackOffice);
        :>> doActions    = (authoriseDriverSm.actions.spinner);
      }

      part offlineWhitelist : Pseudostate {
        :>> name = "OfflineWhitelist";
        :>> kind = PseudostateKind::junction;
      }

      connection startToInitReader : Transition connect driverStart.outgoing to initialisingReader.incoming {
        :>> name = "AuthoriseDriverStart -> InitialisingReader";
        :>> kind = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }

      connection fullToInitReader : Transition connect epFull.outgoing to initialisingReader.incoming {
        :>> name = "full -> InitialisingReader";
        :>> kind = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }

      connection initReaderToAwaiting : Transition connect initialisingReader.outgoing to awaitingCredential.incoming {
        :>> name           = "InitialisingReader -> AwaitingCredential";
        :>> trigger        = authoriseDriverSm.events.readerReady;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }

      connection initReaderToRefused : Transition connect initialisingReader.outgoing to xpNo.incoming {
        :>> name           = "InitialisingReader -> refused";
        :>> trigger        = authoriseDriverSm.events.readerFault;
        :>> actions        = (authoriseDriverSm.actions.postFaultF12);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }

      connection cachedToValidating : Transition connect epCached.outgoing to validatingToken.incoming {
        :>> name           = "cachedToken -> ValidatingToken";
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::secondary;
      }

      connection awaitingSelfLoop : Transition connect awaitingCredential.outgoing to awaitingCredential.incoming {
        :>> name           = "AwaitingCredential -> AwaitingCredential (credentialSeen, unreadable)";
        :>> trigger        = authoriseDriverSm.events.credentialSeen;
        :>> guards         = (authoriseDriverSm.guards.notWellFormed);
        :>> actions        = (authoriseDriverSm.actions.beep2);
        :>> kind           = TransitionKind::internal;
        :>> classification = TransitionClassification::secondary;
      }

      connection awaitingToValidating : Transition connect awaitingCredential.outgoing to validatingToken.incoming {
        :>> name           = "AwaitingCredential -> ValidatingToken";
        :>> trigger        = authoriseDriverSm.events.credentialCaptured;
        :>> actions        = (authoriseDriverSm.actions.hashCredential);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }

      connection awaitingToRefused : Transition connect awaitingCredential.outgoing to xpNo.incoming {
        :>> name           = "AwaitingCredential -> refused";
        :>> trigger        = authoriseDriverSm.events.idleTimeout;
        :>> actions        = (authoriseDriverSm.actions.showTimeout);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }

      connection validatingToAccepted : Transition connect validatingToken.outgoing to xpOk.incoming {
        :>> name           = "ValidatingToken -> authorised";
        :>> trigger        = authoriseDriverSm.events.tokenAccepted;
        :>> actions        = (authoriseDriverSm.actions.issueSessionId);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::primary;
      }

      connection validatingToOffline : Transition connect validatingToken.outgoing to offlineWhitelist.incoming {
        :>> name           = "ValidatingToken -> OfflineWhitelist";
        :>> trigger        = authoriseDriverSm.events.backOfficeUnreachable;
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::secondary;
      }

      connection offlineToAccepted : Transition connect offlineWhitelist.outgoing to xpOk.incoming {
        :>> name           = "OfflineWhitelist -> authorised [tokenOnWhitelist]";
        :>> guards         = (authoriseDriverSm.guards.tokenOnWhitelist);
        :>> actions        = (authoriseDriverSm.actions.flagDeferredBilling);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::secondary;
      }

      connection offlineToRefused : Transition connect offlineWhitelist.outgoing to xpNo.incoming {
        :>> name           = "OfflineWhitelist -> refused [else]";
        :>> guards         = (authoriseDriverSm.guards.elseGuard);
        :>> actions        = (authoriseDriverSm.actions.showTryLater);
        :>> kind           = TransitionKind::external;
        :>> classification = TransitionClassification::exception;
      }
    }
  }
}
