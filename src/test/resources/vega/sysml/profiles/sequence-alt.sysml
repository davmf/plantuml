package TestSequence2 {
  doc /* Login handshake with success / failure alternatives, modelled
     * as an alt CombinedFragment containing two InteractionOperands
     * guarded by 'credentialsValid' and '!credentialsValid'.
     * Demonstrates CombinedFragment, InteractionOperand and guards.
     */

  private import SequenceProfile::*;

  part interaction : Interaction {
    :>> name = "Login - With Alt";

    part :>> participantClasses {
      part userClass    : ParticipantClass { :>> name = "User";    }
      part systemClass  : ParticipantClass { :>> name = "System";  }
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
    }

    part :>> messages {
      part loginCall : Message {
        :>> name      = "login()";
        :>> kind      = MessageKind::synchronous;
        :>> signature = "login(u, p)";
        :>> order     = 1;
        connect source to interaction.lifelines.user.outgoing;
        connect target to interaction.lifelines.system.incoming;
      }

      part successReply : Message {
        :>> name      = "loginReply(success)";
        :>> kind      = MessageKind::reply;
        :>> signature = "login(u, p)";
        :>> arguments = ("sessionToken");
        :>> order     = 2;
        connect source to interaction.lifelines.system.outgoing;
        connect target to interaction.lifelines.user.incoming;
      }

      part failureReply : Message {
        :>> name      = "loginReply(failure)";
        :>> kind      = MessageKind::reply;
        :>> signature = "login(u, p)";
        :>> arguments = ("AuthenticationError");
        :>> order     = 3;
        connect source to interaction.lifelines.system.outgoing;
        connect target to interaction.lifelines.user.incoming;
      }
    }

    part :>> fragments {
      part loginAlt : CombinedFragment {
        :>> name = "loginOutcome";
        :>> op   = FragmentOperator::altFragment;

        part :>> operands {
          part successBranch : InteractionOperand {
            :>> name     = "credentialsValid";
            :>> guard    = "credentialsValid";
            :>> order    = 1;
            :>> messages = (interaction.messages.successReply);
          }

          part failureBranch : InteractionOperand {
            :>> name     = "credentialsInvalid";
            :>> guard    = "!credentialsValid";
            :>> order    = 2;
            :>> messages = (interaction.messages.failureReply);
          }
        }
      }
    }
  }
}
