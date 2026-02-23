/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.handlers.VerifyIdentityCommandHandler;
import verigate.adapter.dha.domain.mappers.IdentityVerificationMapper;
import verigate.adapter.dha.domain.models.IdVerificationStatus;
import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.services.DhaIdentityVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing identity verification commands using the DHA
 * identity verification API.
 */
public class DefaultVerifyIdentityCommandHandler
    implements VerifyIdentityCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyIdentityCommandHandler.class);

  private final DhaIdentityVerificationService identityVerificationService;

  /**
   * Constructor for default identity verification handler.
   *
   * @param identityVerificationService the DHA identity verification service
   */
  public DefaultVerifyIdentityCommandHandler(
      DhaIdentityVerificationService identityVerificationService) {
    this.identityVerificationService = identityVerificationService;
  }

  /**
   * Handles identity verification using the DHA identity verification API.
   *
   * @param command the command to handle
   * @return map containing verification results (for compatibility with existing interface)
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting identity verification for command: {}", command.getId());

    try {
      IdentityVerificationRequest verificationRequest =
          IdentityVerificationMapper.mapToIdentityVerificationRequestDefault(command);

      logger.info(
          "Mapped command to identity verification request for ID number ending: ...{}",
          maskIdNumber(verificationRequest.idNumber()));

      IdentityVerificationResponse verificationResponse =
          identityVerificationService.verifyIdentity(verificationRequest);

      logger.info(
          "Identity verification completed with status: {}",
          verificationResponse.status());

      VerificationResult verificationResult =
          analyzeVerificationResponse(verificationResponse, command);

      logger.info(
          "Identity verification completed with outcome: {}",
          verificationResult.outcome());

      return Map.of(
          "outcome",
          verificationResult.outcome().toString(),
          "details",
          verificationResult.failureReason() != null ? verificationResult.failureReason() : "",
          "idNumber",
          maskIdNumber(verificationRequest.idNumber()),
          "verificationStatus",
          verificationResponse.status().toString(),
          "citizenshipStatus",
          verificationResponse.citizenshipStatus().toString(),
          "vitalStatus",
          verificationResponse.vitalStatus().toString(),
          "matchDetails",
          verificationResponse.matchDetails() != null ? verificationResponse.matchDetails() : "");
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Identity verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid identity verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during identity verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected identity verification error", e);
    }
  }

  /**
   * Analyzes the identity verification response to determine verification outcome.
   *
   * <p>Analysis logic:
   * <ul>
   *   <li>VERIFIED -> SUCCEEDED</li>
   *   <li>DECEASED -> HARD_FAIL</li>
   *   <li>NOT_FOUND -> SOFT_FAIL</li>
   *   <li>MISMATCH -> SOFT_FAIL</li>
   *   <li>EXPIRED_ID -> SOFT_FAIL</li>
   *   <li>BLOCKED -> HARD_FAIL</li>
   * </ul>
   *
   * @param response the identity verification response
   * @param command the original command
   * @return the verification result
   */
  private VerificationResult analyzeVerificationResponse(
      IdentityVerificationResponse response, VerifyPartyCommand command) {

    IdVerificationStatus status = response.status();

    return switch (status) {
      case VERIFIED -> {
        String successMessage = String.format(
            "Identity verified successfully. Citizenship: %s, Vital Status: %s",
            response.citizenshipStatus(),
            response.vitalStatus());
        logger.info("Identity verification succeeded for command: {}", command.getId());
        yield new VerificationResult(VerificationOutcome.SUCCEEDED, successMessage);
      }
      case DECEASED -> {
        String reason = String.format(
            "Individual is recorded as deceased in DHA records. Details: %s",
            response.matchDetails() != null ? response.matchDetails() : "N/A");
        logger.info(
            "Identity verification hard fail - deceased: command {}",
            command.getId());
        yield new VerificationResult(VerificationOutcome.HARD_FAIL, reason);
      }
      case BLOCKED -> {
        String reason = String.format(
            "Identity is blocked in DHA records. Details: %s",
            response.matchDetails() != null ? response.matchDetails() : "N/A");
        logger.info(
            "Identity verification hard fail - blocked: command {}",
            command.getId());
        yield new VerificationResult(VerificationOutcome.HARD_FAIL, reason);
      }
      case NOT_FOUND -> {
        String reason = "Identity not found in DHA population register";
        logger.info(
            "Identity verification soft fail - not found: command {}",
            command.getId());
        yield new VerificationResult(VerificationOutcome.SOFT_FAIL, reason);
      }
      case MISMATCH -> {
        String reason = String.format(
            "Identity details do not match DHA records. Details: %s",
            response.matchDetails() != null ? response.matchDetails() : "N/A");
        logger.info(
            "Identity verification soft fail - mismatch: command {}",
            command.getId());
        yield new VerificationResult(VerificationOutcome.SOFT_FAIL, reason);
      }
      case EXPIRED_ID -> {
        String reason = String.format(
            "ID document has expired. Details: %s",
            response.matchDetails() != null ? response.matchDetails() : "N/A");
        logger.info(
            "Identity verification soft fail - expired ID: command {}",
            command.getId());
        yield new VerificationResult(VerificationOutcome.SOFT_FAIL, reason);
      }
    };
  }

  /**
   * Masks the ID number for safe logging.
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 4) {
      return "***";
    }
    return "***" + idNumber.substring(idNumber.length() - 4);
  }

  /**
   * Alternative async method for non-blocking verification. This method can be used for future
   * async support.
   */
  public CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            Map<String, String> result = handle(command);
            VerificationOutcome outcome = VerificationOutcome.valueOf(result.get("outcome"));
            String details = result.get("details");
            return new VerificationResult(outcome, details);
          } catch (Exception e) {
            logger.error(
                "Async identity verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Identity verification failed: " + e.getMessage());
          }
        });
  }
}
