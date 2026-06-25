/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.application.handlers;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import verigate.adapter.opensanctions.domain.handlers.SanctionsScreeningCommandHandler;
import verigate.adapter.opensanctions.domain.mappers.VerificationResultMapper;
import verigate.adapter.opensanctions.domain.mappers.VerifyPartyCommandMapper;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.services.OpenSanctionsMatchingService;
import verigate.adapter.opensanctions.domain.services.SanctionsReportService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.events.VerificationEventPublisher;
import verigate.verification.cg.domain.factories.EventFactory;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation of sanctions screening command handler using OpenSanctions API.
 */
public class DefaultSanctionsScreeningCommandHandler implements SanctionsScreeningCommandHandler {

  private static final Logger LOGGER =
      Logger.getLogger(DefaultSanctionsScreeningCommandHandler.class.getName());

  private final OpenSanctionsMatchingService openSanctionsService;
  private final VerificationEventPublisher eventPublisher;
  private final EventFactory eventFactory;
  private final SanctionsReportService reportService;

  public DefaultSanctionsScreeningCommandHandler(
      OpenSanctionsMatchingService openSanctionsService,
      VerificationEventPublisher eventPublisher,
      EventFactory eventFactory,
      SanctionsReportService reportService) {
    this.openSanctionsService = openSanctionsService;
    this.eventPublisher = eventPublisher;
    this.eventFactory = eventFactory;
    this.reportService = reportService;
  }

  @Override
  public Map<String, String> handle(VerifyPartyCommand command) {
    LOGGER.info("Processing sanctions screening command for party: " + maskSensitiveData(command));

    try {
      // Map command to OpenSanctions request
      EntityMatchRequest matchRequest = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

      // Call OpenSanctions API
      EntityMatchResponse matchResponse = openSanctionsService.matchEntities(matchRequest);

      // Map response to verification result
      String requestId = command.getId().toString();
      VerificationResult result =
          VerificationResultMapper.mapToVerificationResult(matchResponse, requestId);
      publishVerificationEvent(command, result);

      LOGGER.info("Sanctions screening completed with outcome: " + result.outcome());

      // Build result map with outcome and match details
      Map<String, String> resultMap = new HashMap<>();
      resultMap.put("outcome", result.outcome().toString());
      if (result.failureReason() != null) {
        resultMap.put("failureReason", result.failureReason());
      }
      resultMap.put("provider", "OpenSanctions");

      // Add subject context so the report has a human-readable subject line
      String firstName = extractMetadata(command, "firstName");
      String lastName = extractMetadata(command, "lastName");
      String entityType = extractMetadata(command, "entityType");
      String subjectName = buildSubjectName(firstName, lastName, entityType);
      resultMap.put("subject_name", subjectName);
      resultMap.put("entity_type", entityType.isEmpty() ? "Person" : entityType);

      // Merge detailed match information
      Map<String, String> matchDetails =
          VerificationResultMapper.createResultDetails(matchResponse);
      resultMap.putAll(matchDetails);

      // Generate and store the screening report (best-effort — never fail the verification)
      try {
        String partnerId = command.getPartnerId() != null ? command.getPartnerId() : "";
        String reportKey = reportService.generateReport(requestId, partnerId, resultMap);
        if (reportKey != null) {
          resultMap.put("reportDocumentId", reportKey);
        }
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Report generation failed for commandId " + requestId
            + ", continuing without report", e);
      }

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
            EntityMatchRequest matchRequest =
                VerifyPartyCommandMapper.mapToEntityMatchRequest(command);
            EntityMatchResponse matchResponse =
                openSanctionsService.matchEntities(matchRequest);
            String requestId = command.getId().toString();
            VerificationResult result =
                VerificationResultMapper.mapToVerificationResult(matchResponse, requestId);
            publishVerificationEvent(command, result);
            return result;
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
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
    return "VerificationRequest[id="
        + command.getId()
        + ", type="
        + command.getVerificationType()
        + "]";
  }

  private static String extractMetadata(VerifyPartyCommand command, String key) {
    Object value = command.getMetadata().get(key);
    return value != null ? value.toString() : "";
  }

  private static String buildSubjectName(String firstName, String lastName, String entityType) {
    if (!firstName.isEmpty() && !lastName.isEmpty()) {
      return firstName + " " + lastName;
    }
    if (!firstName.isEmpty()) {
      return firstName;
    }
    if (!lastName.isEmpty()) {
      return lastName;
    }
    return entityType.isEmpty() ? "Unknown" : entityType + " entity";
  }
}
