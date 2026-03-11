package verigate.verification.cg.domain.factories;

import com.google.inject.Inject;
import crosscutting.metrics.Meter;
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
  private final Meter meter;

  /**
   * Constructs a new VerificationEventFactory with the specified EventIdFactory.
   *
   * @param eventIdFactory the factory to create event IDs
   * @param meter the metrics publisher
   */
  @Inject
  public VerificationEventFactory(EventIdFactory eventIdFactory, Meter meter) {
    this.eventIdFactory = eventIdFactory;
    this.meter = meter;
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

    String typeTag = command.getVerificationType() != null
        ? "verification_type:" + command.getVerificationType().name()
        : "verification_type:unknown";

    // TODO: populate logicalclockid and origination
    switch (outcome) {
      case SUCCEEDED:
        meter.incrementCounter("verification.completed", typeTag, "outcome:succeeded");
        return new VerificationSucceededEvent(
            eventId, currentDateTime, currentDateTime, null, command.getVerificationType(), null);
      case SOFT_FAIL:
        meter.incrementCounter("verification.failed", typeTag, "outcome:soft_fail");
        return new VerificationSoftFailEvent(
            eventId, currentDateTime, currentDateTime, null, command.getVerificationType(), null);
      case HARD_FAIL:
        meter.incrementCounter("verification.failed", typeTag, "outcome:hard_fail");
        return new VerificationHardFailEvent(
            eventId, currentDateTime, currentDateTime, null, command.getVerificationType(), null);
      default:
        throw new IllegalArgumentException("Unknown outcome: " + outcome);
    }
  }
}
