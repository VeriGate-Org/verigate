/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.application.handlers;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import verigate.adapter.deedsweb.domain.handlers.PropertyVerificationCommandHandler;
import verigate.adapter.deedsweb.domain.mappers.VerificationResultMapper;
import verigate.adapter.deedsweb.domain.mappers.VerifyPartyCommandMapper;
import verigate.adapter.deedsweb.domain.models.EntityMatchRequest;
import verigate.adapter.deedsweb.domain.models.EntityMatchResponse;
import verigate.adapter.deedsweb.domain.services.DeedsWebMatchingService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.events.VerificationEventPublisher;
import verigate.verification.cg.domain.factories.EventFactory;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation of sanctions screening command handler using OpenSanctions API.
 */
public class DefaultPropertyVerificationCommandHandler
    implements PropertyVerificationCommandHandler {

  private static final Logger LOGGER =
      Logger.getLogger(DefaultPropertyVerificationCommandHandler.class.getName());

  private final DeedsWebMatchingService openSanctionsService;
  private final VerificationEventPublisher eventPublisher;
  private final EventFactory eventFactory;

  /**
   * Constructor.
   *
   * @param openSanctionsService the OpenSanctions matching service
   * @param eventPublisher      the event publisher for verification results
   * @param eventFactory        the factory to create verification events
   */
  public DefaultPropertyVerificationCommandHandler(
      DeedsWebMatchingService openSanctionsService,
      VerificationEventPublisher eventPublisher,
      EventFactory eventFactory) {
    this.openSanctionsService = openSanctionsService;
    this.eventPublisher = eventPublisher;
    this.eventFactory = eventFactory;
  }

  @Override
  public Map<String, String> handle(VerifyPartyCommand command) {
    LOGGER.info("Processing sanctions screening command for party: " + maskSensitiveData(command));

    try {
      VerificationResult result = performSanctionsScreening(command);
      publishVerificationEvent(command, result);

      LOGGER.info("Sanctions screening completed with outcome: " + result.outcome());
      // Return basic result information as a map
      Map<String, String> resultMap = new HashMap<>();
      resultMap.put("outcome", result.outcome().toString());
      if (result.failureReason() != null) {
        resultMap.put("failureReason", result.failureReason());
      }
      resultMap.put("provider", "OpenSanctions");
      return resultMap;

    } catch (TransientException e) {
      LOGGER.log(Level.WARNING, "Transient error during sanctions screening", e);
      publishTransientFailureEvent(command, e);
      throw e;
    } catch (PermanentException e) {
      LOGGER.log(Level.SEVERE, "Permanent error during sanctions screening", e);
      publishPermanentFailureEvent(command, e);
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error during sanctions screening", e);
      PermanentException permanentException =
          new PermanentException("Unexpected error during sanctions screening", e);
      publishPermanentFailureEvent(command, permanentException);
      throw permanentException;
    }
  }

  @Override
  public CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            VerificationResult result = performSanctionsScreening(command);
            publishVerificationEvent(command, result);
            return result;
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  private VerificationResult performSanctionsScreening(VerifyPartyCommand command)
      throws TransientException, PermanentException {

    // Map command to OpenSanctions request
    EntityMatchRequest matchRequest = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

    // Call OpenSanctions API
    EntityMatchResponse matchResponse = openSanctionsService.matchEntities(matchRequest);

    // Map response to verification result
    String requestId = command.getId().toString();
    return VerificationResultMapper.mapToVerificationResult(matchResponse, requestId);
  }

  private void publishVerificationEvent(VerifyPartyCommand command, VerificationResult result) {
    try {
      eventPublisher.publish(List.of(eventFactory.createEvent(result.outcome(), command, result)));
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to publish verification event", e);
      // Don't fail the verification due to event publishing issues
    }
  }

  private void publishTransientFailureEvent(
      VerifyPartyCommand command, TransientException exception) {
    try {
      VerificationResult result =
          new VerificationResult(
              VerificationOutcome.SYSTEM_OUTAGE,
              "OpenSanctions service temporarily unavailable: " + exception.getMessage());
      eventPublisher.publish(List.of(eventFactory.createEvent(result.outcome(), command, result)));
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to publish transient failure event", e);
    }
  }

  private void publishPermanentFailureEvent(
      VerifyPartyCommand command, PermanentException exception) {
    try {
      VerificationResult result =
          new VerificationResult(
              VerificationOutcome.HARD_FAIL,
              "OpenSanctions verification failed permanently: " + exception.getMessage());
      eventPublisher.publish(List.of(eventFactory.createEvent(result.outcome(), command, result)));
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to publish permanent failure event", e);
    }
  }

  private String maskSensitiveData(VerifyPartyCommand command) {
    // Mask sensitive information in logs
    return "VerificationRequest[id="
        + command.getId()
        + ", type="
        + command.getVerificationType()
        + "]";
  }
}
