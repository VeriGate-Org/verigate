package verigate.verification.cg.domain.factories;

import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.events.VerificationEvent;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Factory interface for creating verification events.
 * This interface defines a method to create a verification event based on the outcome,
 * command ID, response, and verification result.
 */
public interface EventFactory {
  VerificationEvent createEvent(
      VerificationOutcome outcome,
      VerifyPartyCommand command,
      VerificationResult verificationResult);
}
