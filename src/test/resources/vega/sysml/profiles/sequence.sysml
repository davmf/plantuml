package TestSequence3 {
  doc /* Login handshake with the full feature set:
     *   - A loopFragment for retry on bad credentials (up to 3
     *     attempts).
     *   - An async audit log signal (asynchronous Message) emitted
     *     once login succeeds.
     *   - A Session object created on success (createMessage) and
     *     destroyed on logout (destroyMessage).
     *   - A Gate at the Interaction boundary receiving an external
     *     'sso-handoff' message into the System.
     *   - A TimeObservation bounding the auth-service round trip
     *     to at most 500ms.
     */

  private import SequenceProfile::*;

  part interaction : Interaction {
    :>> name = "Login - Full Features";

    part :>> participantClasses {
      part userClass    : ParticipantClass { :>> name = "User";    }
      part systemClass  : ParticipantClass { :>> name = "System";  }
      part authClass    : ParticipantClass { :>> name = "AuthService"; }
      part auditClass   : ParticipantClass { :>> name = "AuditLog";    }
      part sessionClass : ParticipantClass { :>> name = "Session";     }
    }

    part :>> lifelines {
      part user : Lifeline {
        :>> name = "u";
        :>> kind = InteractionParticipantKind::actorParticipant;
        :>> represents = interaction.participantClasses.userClass;
      }
      part system : Lifeline {
        :>> name = "sys";
        :>> kind = InteractionParticipantKind::lifeline;
        :>> represents = interaction.participantClasses.systemClass;
      }
      part auth : Lifeline {
        :>> name = "auth";
        :>> kind = InteractionParticipantKind::lifeline;
        :>> represents = interaction.participantClasses.authClass;
      }
      part audit : Lifeline {
        :>> name = "audit";
        :>> kind = InteractionParticipantKind::lifeline;
        :>> represents = interaction.participantClasses.auditClass;
      }
      part session : Lifeline {
        :>> name = "session";
        :>> kind = InteractionParticipantKind::lifeline;
        :>> represents = interaction.participantClasses.sessionClass;
      }
    }

    part :>> gates {
      part ssoGate : Gate {
        :>> name = "ssoHandoffGate";
      }
    }

    part :>> timeObservations {
      part authRtt : TimeObservation {
        :>> name    = "authRoundTrip";
        :>> kind    = TimeConstraintKind::atMost;
        :>> upperNs = 500000000;   // 500 ms in nanoseconds
      }
    }

    part :>> messages {
      // Retry loop body: user -> system -> auth (sync), reply back.
      part loginAttempt : Message {
        :>> name      = "loginAttempt()";
        :>> kind      = MessageKind::synchronous;
        :>> signature = "login(u, p)";
        :>> order     = 1;
        connect source to interaction.lifelines.user.outgoing;
        connect target to interaction.lifelines.system.incoming;
      }

      part authCheck : Message {
        :>> name      = "authCheck()";
        :>> kind      = MessageKind::synchronous;
        :>> signature = "verify(u, p)";
        :>> order     = 2;
        :>> timing    = interaction.timeObservations.authRtt;
        connect source to interaction.lifelines.system.outgoing;
        connect target to interaction.lifelines.auth.incoming;
      }

      part authReply : Message {
        :>> name      = "authReply";
        :>> kind      = MessageKind::reply;
        :>> signature = "verify(u, p)";
        :>> arguments = ("valid");
        :>> order     = 3;
        connect source to interaction.lifelines.auth.outgoing;
        connect target to interaction.lifelines.system.incoming;
      }

      part loginReply : Message {
        :>> name      = "loginReply";
        :>> kind      = MessageKind::reply;
        :>> signature = "login(u, p)";
        :>> arguments = ("sessionToken");
        :>> order     = 4;
        connect source to interaction.lifelines.system.outgoing;
        connect target to interaction.lifelines.user.incoming;
      }

      // After the loop: create session.
      part createSession : Message {
        :>> name      = "createSession";
        :>> kind      = MessageKind::createMessage;
        :>> signature = "Session(u)";
        :>> order     = 5;
        connect source to interaction.lifelines.system.outgoing;
        connect target to interaction.lifelines.session.incoming;
      }

      // Async audit log signal.
      part auditSignal : Message {
        :>> name      = "auditLoginSuccess";
        :>> kind      = MessageKind::asynchronous;
        :>> signature = "logEvent(loginSuccess, u)";
        :>> order     = 6;
        connect source to interaction.lifelines.system.outgoing;
        connect target to interaction.lifelines.audit.incoming;
      }

      // Later: user logs out, session destroyed.
      part logoutCall : Message {
        :>> name      = "logout()";
        :>> kind      = MessageKind::synchronous;
        :>> signature = "logout(sessionToken)";
        :>> order     = 7;
        connect source to interaction.lifelines.user.outgoing;
        connect target to interaction.lifelines.system.incoming;
      }

      part destroySession : Message {
        :>> name      = "destroySession";
        :>> kind      = MessageKind::destroyMessage;
        :>> order     = 8;
        connect source to interaction.lifelines.system.outgoing;
        connect target to interaction.lifelines.session.incoming;
      }
    }

    // Gate-originated boundary-crossing message.
    connection ssoHandoff : GateMessage {
      :>> name      = "ssoHandoff";
      :>> kind      = MessageKind::asynchronous;
      :>> signature = "ssoToken(externalUser)";
      :>> order     = 0;     // arrives before any internal message
      connect source to interaction.gates.ssoGate.boundary;
      connect target to interaction.lifelines.system.incoming;
    }

    part :>> fragments {
      part retryLoop : CombinedFragment {
        :>> name = "retryUpTo3Times";
        :>> op   = FragmentOperator::loopFragment;

        part :>> operands {
          part retryBody : InteractionOperand {
            :>> name     = "attempt";
            :>> guard    = "attempts < 3";
            :>> order    = 1;
            :>> messages = (
              interaction.messages.loginAttempt,
              interaction.messages.authCheck,
              interaction.messages.authReply,
              interaction.messages.loginReply
            );
          }
        }
      }
    }
  }
}
