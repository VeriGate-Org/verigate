/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.saqa.domain.handlers.VerifyQualificationCommandHandler;
import verigate.adapter.saqa.domain.mappers.QualificationVerificationMapper;
import verigate.adapter.saqa.domain.models.QualificationRecord;
import verigate.adapter.saqa.domain.models.QualificationVerificationRequest;
import verigate.adapter.saqa.domain.models.QualificationVerificationResponse;
import verigate.adapter.saqa.domain.models.QualificationVerificationStatus;
import verigate.adapter.saqa.domain.services.QualificationVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing qualification verification commands using the SAQA
 * registry.
 */
public class DefaultVerifyQualificationCommandHandler
    implements VerifyQualificationCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyQualificationCommandHandler.class);

  private final QualificationVerificationService qualificationVerificationService;

  /**
   * Constructor for default qualification verification handler.
   *
   * @param qualificationVerificationService the SAQA qualification verification service
   */
  public DefaultVerifyQualificationCommandHandler(
      QualificationVerificationService qualificationVerificationService) {
    this.qualificationVerificationService = qualificationVerificationService;
  }

  /**
   * Handles qualification verification using the SAQA registry.
   *
   * @param command the command to handle
   * @return map containing verification results (for compatibility with existing interface)
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting qualification verification for command: {}", command.getId());

    try {
      QualificationVerificationRequest verificationRequest =
          QualificationVerificationMapper.mapToQualificationVerificationRequestDefault(command);

      logger.info(
          "Mapped command to qualification verification request for ID: {}",
          maskIdNumber(verificationRequest.idNumber()));

      QualificationVerificationResponse verificationResponse =
          qualificationVerificationService.verifyQualification(verificationRequest);

      logger.info(
          "Qualification verification completed with status: {}",
          verificationResponse.status());

      VerificationOutcome outcome = mapStatusToOutcome(verificationResponse.status());

      logger.info(
          "Qualification verification outcome for command {}: {}",
          command.getId(),
          outcome);

      QualificationRecord matched = verificationResponse.matchedQualification();

      return Map.of(
          "outcome",
          outcome.toString(),
          "status",
          verificationResponse.status().toString(),
          "qualificationTitle",
          matched != null && matched.qualificationTitle() != null
              ? matched.qualificationTitle() : "",
          "institution",
          matched != null && matched.institution() != null
              ? matched.institution() : "",
          "nqfLevel",
          matched != null ? String.valueOf(matched.nqfLevel()) : "0",
          "matchConfidence",
          String.valueOf(verificationResponse.matchConfidence()));

    } catch (TransientException | PermanentException e) {
      logger.error(
          "Qualification verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid qualification verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during qualification verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected qualification verification error", e);
    }
  }

  /**
   * Maps a QualificationVerificationStatus to a VerificationOutcome.
   *
   * @param status the qualification verification status
   * @return the corresponding verification outcome
   */
  private VerificationOutcome mapStatusToOutcome(QualificationVerificationStatus status) {
    return switch (status) {
      case VERIFIED -> VerificationOutcome.SUCCEEDED;
      case NOT_FOUND -> VerificationOutcome.HARD_FAIL;
      case REVOKED -> VerificationOutcome.HARD_FAIL;
      case EXPIRED -> VerificationOutcome.SOFT_FAIL;
      case MISMATCH -> VerificationOutcome.SOFT_FAIL;
      case PENDING_VERIFICATION -> VerificationOutcome.SOFT_FAIL;
      case ERROR -> VerificationOutcome.SYSTEM_OUTAGE;
    };
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
            String details = result.get("status");
            return new VerificationResult(outcome, details);
          } catch (Exception e) {
            logger.error(
                "Async qualification verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Qualification verification failed: " + e.getMessage());
          }
        });
  }

  /**
   * Masks an ID number for safe logging.
   *
   * @param idNumber the ID number to mask
   * @return the masked ID number
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 6) {
      return "***";
    }
    return idNumber.substring(0, 3) + "***" + idNumber.substring(idNumber.length() - 3);
  }
}
