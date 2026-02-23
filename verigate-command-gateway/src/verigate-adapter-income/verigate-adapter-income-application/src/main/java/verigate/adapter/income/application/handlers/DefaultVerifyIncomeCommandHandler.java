/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.income.domain.constants.DomainConstants;
import verigate.adapter.income.domain.enums.IncomeVerificationStatus;
import verigate.adapter.income.domain.handlers.VerifyIncomeCommandHandler;
import verigate.adapter.income.domain.mappers.IncomeVerificationMapper;
import verigate.adapter.income.domain.models.IncomeAssessment;
import verigate.adapter.income.domain.models.IncomeVerificationRequest;
import verigate.adapter.income.domain.models.IncomeVerificationResponse;
import verigate.adapter.income.domain.services.IncomeVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing income verification commands.
 *
 * <p>This handler orchestrates income verification by:
 * <ol>
 *   <li>Mapping the incoming command to an income verification request</li>
 *   <li>Delegating to the income verification service</li>
 *   <li>Mapping the response to a standardized result map</li>
 * </ol>
 */
public class DefaultVerifyIncomeCommandHandler
    implements VerifyIncomeCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyIncomeCommandHandler.class);

  private final IncomeVerificationService incomeVerificationService;

  /**
   * Constructor for default income verification handler.
   *
   * @param incomeVerificationService the income verification service
   */
  public DefaultVerifyIncomeCommandHandler(
      IncomeVerificationService incomeVerificationService) {
    this.incomeVerificationService = incomeVerificationService;
  }

  /**
   * Handles income verification using the income verification API.
   *
   * @param command the command to handle
   * @return map containing verification results (for compatibility with existing interface)
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting income verification for command: {}", command.getId());

    try {
      IncomeVerificationRequest verificationRequest =
          IncomeVerificationMapper.mapToIncomeVerificationRequestDefault(command);

      logger.info(
          "Mapped command to income verification request for ID: {}, declared income: {}",
          maskIdNumber(verificationRequest.idNumber()),
          verificationRequest.declaredMonthlyIncome());

      IncomeVerificationResponse verificationResponse =
          incomeVerificationService.verifyIncome(verificationRequest);

      logger.info(
          "Income verification completed with status: {} for ID: {}",
          verificationResponse.status(),
          maskIdNumber(verificationRequest.idNumber()));

      return buildResultMap(verificationResponse, verificationRequest);

    } catch (TransientException | PermanentException e) {
      logger.error(
          "Income verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid income verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during income verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected income verification error", e);
    }
  }

  /**
   * Builds the result map based on the verification response and analysis logic.
   *
   * @param response the income verification response
   * @param request the original request
   * @return the result map with outcome and details
   */
  private Map<String, String> buildResultMap(
      IncomeVerificationResponse response,
      IncomeVerificationRequest request) {

    VerificationOutcome outcome = analyzeIncomeVerificationStatus(response.status());
    String details = buildDetailsMessage(response);
    IncomeAssessment assessment = response.assessment();

    Map<String, String> result = new HashMap<>();
    result.put(DomainConstants.RESULT_KEY_OUTCOME, outcome.toString());
    result.put(DomainConstants.RESULT_KEY_DETAILS, details);
    result.put(DomainConstants.RESULT_KEY_VERIFICATION_STATUS, response.status().toString());
    result.put(DomainConstants.RESULT_KEY_REASON,
        response.reason() != null ? response.reason() : "");

    if (assessment != null) {
      result.put(DomainConstants.RESULT_KEY_VERIFIED_MONTHLY_INCOME,
          assessment.verifiedMonthlyIncome() != null
              ? assessment.verifiedMonthlyIncome().toPlainString() : "0");
      result.put(DomainConstants.RESULT_KEY_DECLARED_MONTHLY_INCOME,
          assessment.declaredMonthlyIncome() != null
              ? assessment.declaredMonthlyIncome().toPlainString() : "0");
      result.put(DomainConstants.RESULT_KEY_VARIANCE,
          assessment.variance() != null
              ? assessment.variance().toPlainString() : "0");
      result.put(DomainConstants.RESULT_KEY_CONFIDENCE_LEVEL,
          assessment.confidence() != null ? assessment.confidence().toString() : "NONE");
      result.put(DomainConstants.RESULT_KEY_EVIDENCE_SOURCES,
          assessment.evidenceSources() != null
              ? String.join(",", assessment.evidenceSources()) : "");
      result.put(DomainConstants.RESULT_KEY_AFFORDABILITY_CONFIRMED,
          String.valueOf(assessment.affordabilityConfirmed()));
    }

    logger.info(
        "Income verification result - outcome: {}, status: {}, confidence: {}",
        outcome,
        response.status(),
        assessment != null ? assessment.confidence() : "N/A");

    return result;
  }

  /**
   * Analyzes the income verification status to determine the verification outcome.
   *
   * <p>Analysis logic:
   * <ul>
   *   <li>VERIFIED -> SUCCEEDED</li>
   *   <li>MISMATCH -> HARD_FAIL (declared income doesn't match)</li>
   *   <li>UNVERIFIABLE -> HARD_FAIL (cannot verify income)</li>
   *   <li>INSUFFICIENT_EVIDENCE -> SOFT_FAIL (need more data)</li>
   *   <li>PENDING -> SOFT_FAIL (awaiting additional information)</li>
   *   <li>ERROR -> SYSTEM_OUTAGE</li>
   * </ul>
   *
   * @param status the income verification status
   * @return the corresponding verification outcome
   */
  private VerificationOutcome analyzeIncomeVerificationStatus(
      IncomeVerificationStatus status) {
    return switch (status) {
      case VERIFIED -> VerificationOutcome.SUCCEEDED;
      case MISMATCH -> VerificationOutcome.HARD_FAIL;
      case UNVERIFIABLE -> VerificationOutcome.HARD_FAIL;
      case INSUFFICIENT_EVIDENCE -> VerificationOutcome.SOFT_FAIL;
      case PENDING -> VerificationOutcome.SOFT_FAIL;
      case ERROR -> VerificationOutcome.SYSTEM_OUTAGE;
    };
  }

  /**
   * Builds a human-readable details message based on the verification response.
   */
  private String buildDetailsMessage(IncomeVerificationResponse response) {
    IncomeAssessment assessment = response.assessment();
    return switch (response.status()) {
      case VERIFIED -> String.format(
          "Income verified successfully. Verified monthly income: %s, "
              + "Declared monthly income: %s, Variance: %s%%, Confidence: %s",
          assessment != null && assessment.verifiedMonthlyIncome() != null
              ? assessment.verifiedMonthlyIncome().toPlainString() : "N/A",
          assessment != null && assessment.declaredMonthlyIncome() != null
              ? assessment.declaredMonthlyIncome().toPlainString() : "N/A",
          assessment != null && assessment.variance() != null
              ? assessment.variance().toPlainString() : "N/A",
          assessment != null && assessment.confidence() != null
              ? assessment.confidence() : "N/A");
      case MISMATCH -> String.format(
          "Income mismatch detected. Declared: %s, Verified: %s, Variance: %s%%",
          assessment != null && assessment.declaredMonthlyIncome() != null
              ? assessment.declaredMonthlyIncome().toPlainString() : "N/A",
          assessment != null && assessment.verifiedMonthlyIncome() != null
              ? assessment.verifiedMonthlyIncome().toPlainString() : "N/A",
          assessment != null && assessment.variance() != null
              ? assessment.variance().toPlainString() : "N/A");
      case INSUFFICIENT_EVIDENCE -> String.format(
          "Insufficient evidence for income verification. Reason: %s",
          response.reason() != null ? response.reason() : "N/A");
      case UNVERIFIABLE -> String.format(
          "Income could not be verified. Reason: %s",
          response.reason() != null ? response.reason() : "N/A");
      case PENDING -> String.format(
          "Income verification is pending. Reason: %s",
          response.reason() != null ? response.reason() : "Awaiting additional data");
      case ERROR -> String.format(
          "Income verification encountered an error. Reason: %s",
          response.reason() != null ? response.reason() : "System error");
    };
  }

  /**
   * Alternative async method for non-blocking verification.
   */
  @Override
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
                "Async income verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.SYSTEM_OUTAGE,
                "Income verification failed: " + e.getMessage());
          }
        });
  }

  /**
   * Masks an ID number for safe logging, showing only the last 4 digits.
   *
   * @param idNumber the ID number to mask
   * @return the masked ID number
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() <= 4) {
      return "****";
    }
    return "****" + idNumber.substring(idNumber.length() - 4);
  }
}
