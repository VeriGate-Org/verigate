package verigate.verification.cg.domain.factories;

import com.google.inject.Inject;
import domain.events.EventIdFactory;
import java.time.Instant;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.events.VerificationEvent;
import verigate.verification.cg.domain.events.VerificationHardFailEvent;
import verigate.verification.cg.domain.events.VerificationSoftFailEvent;
import verigate.verification.cg.domain.events.VerificationSucceededEvent;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Factory class for creating verification events based on the outcome of a verification process.
 */
public class VerificationEventFactory implements EventFactory {

  private final EventIdFactory eventIdFactory;

  /**
   * Constructs a new VerificationEventFactory with the specified EventIdFactory.
   *
   * @param eventIdFactory the factory to create event IDs
   */
  @Inject
  public VerificationEventFactory(EventIdFactory eventIdFactory) {
    this.eventIdFactory = eventIdFactory;
  }

  /**
   * Creates a verification event based on the outcome of a verification process.
   *
   * @param outcome the outcome of the verification
   * @param command the command that triggered the verification
   * @param verificationResult the result of the verification
   * @return a VerificationEvent corresponding to the outcome
   */
  public VerificationEvent createEvent(
      VerificationOutcome outcome,
      VerifyPartyCommand command,
      VerificationResult verificationResult) {

    var currentDateTime = Instant.now();
    var eventId = eventIdFactory.createFromCommand(command.getId(), command.getClass());

    // TODO: populate logicalclockid and origination
    switch (outcome) {
      case SUCCEEDED:
        return new VerificationSucceededEvent(
            eventId, currentDateTime, currentDateTime, null, command.getVerificationType(), null);
      case SOFT_FAIL:
        return new VerificationSoftFailEvent(
            eventId, currentDateTime, currentDateTime, null, command.getVerificationType(), null);
      case HARD_FAIL:
        return new VerificationHardFailEvent(
            eventId, currentDateTime, currentDateTime, null, command.getVerificationType(), null);
      default:
        throw new IllegalArgumentException("Unknown outcome: " + outcome);
    }
  }
}
