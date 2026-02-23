/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.document.domain.constants.DomainConstants;
import verigate.adapter.document.domain.handlers.VerifyDocumentCommandHandler;
import verigate.adapter.document.domain.mappers.DocumentVerificationMapper;
import verigate.adapter.document.domain.models.DocumentVerificationRequest;
import verigate.adapter.document.domain.models.DocumentVerificationResponse;
import verigate.adapter.document.domain.models.DocumentVerificationStatus;
import verigate.adapter.document.domain.services.DocumentVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing document verification commands.
 */
public class DefaultVerifyDocumentCommandHandler
    implements VerifyDocumentCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyDocumentCommandHandler.class);

  private final DocumentVerificationService documentVerificationService;

  /**
   * Constructor for default document verification handler.
   *
   * @param documentVerificationService the document verification service
   */
  public DefaultVerifyDocumentCommandHandler(
      DocumentVerificationService documentVerificationService) {
    this.documentVerificationService = documentVerificationService;
  }

  /**
   * Handles document verification using the document verification API.
   *
   * @param command the command to handle
   * @return map containing verification results
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting document verification for command: {}", command.getId());

    try {
      DocumentVerificationRequest verificationRequest =
          DocumentVerificationMapper.mapToDocumentVerificationRequestDefault(command);

      logger.info(
          "Mapped command to document verification request for reference: [MASKED]");

      DocumentVerificationResponse verificationResponse =
          documentVerificationService.verifyDocument(verificationRequest);

      VerificationOutcome outcome = mapStatusToOutcome(verificationResponse.status());

      logger.info(
          "Document verification completed with outcome: {} for status: {}",
          outcome,
          verificationResponse.status());

      String extractedFieldsSummary = verificationResponse.extractedData() != null
          ? String.valueOf(verificationResponse.extractedData().size()) + " fields"
          : "0 fields";

      return Map.of(
          DomainConstants.RESULT_OUTCOME,
          outcome.toString(),
          DomainConstants.RESULT_STATUS,
          verificationResponse.status().toString(),
          DomainConstants.RESULT_DOCUMENT_TYPE,
          verificationResponse.documentType() != null
              ? verificationResponse.documentType().toString()
              : "",
          DomainConstants.RESULT_CONFIDENCE_SCORE,
          String.valueOf(verificationResponse.confidenceScore()),
          DomainConstants.RESULT_MATCH_DETAILS,
          verificationResponse.matchDetails() != null
              ? verificationResponse.matchDetails()
              : "",
          DomainConstants.RESULT_EXTRACTED_FIELDS,
          extractedFieldsSummary);

    } catch (TransientException | PermanentException e) {
      logger.error(
          "Document verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid document verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during document verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected document verification error", e);
    }
  }

  /**
   * Maps a document verification status to a verification outcome.
   *
   * @param status the document verification status
   * @return the corresponding verification outcome
   */
  private VerificationOutcome mapStatusToOutcome(DocumentVerificationStatus status) {
    return switch (status) {
      case VERIFIED -> VerificationOutcome.SUCCEEDED;
      case MISMATCH -> VerificationOutcome.SOFT_FAIL;
      case SUSPECTED_FRAUD -> VerificationOutcome.HARD_FAIL;
      case UNREADABLE -> VerificationOutcome.SOFT_FAIL;
      case EXPIRED -> VerificationOutcome.SOFT_FAIL;
      case NOT_FOUND -> VerificationOutcome.HARD_FAIL;
      case PENDING -> VerificationOutcome.SOFT_FAIL;
      case ERROR -> VerificationOutcome.SYSTEM_OUTAGE;
    };
  }

  /**
   * Alternative async method for non-blocking verification. This method can be used for future
   * async support.
   */
  @Override
  public CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            Map<String, String> result = handle(command);
            VerificationOutcome outcome = VerificationOutcome.valueOf(
                result.get(DomainConstants.RESULT_OUTCOME));
            String details = result.get(DomainConstants.RESULT_MATCH_DETAILS);
            return new VerificationResult(outcome, details);
          } catch (Exception e) {
            logger.error(
                "Async document verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Document verification failed: " + e.getMessage());
          }
        });
  }
}
