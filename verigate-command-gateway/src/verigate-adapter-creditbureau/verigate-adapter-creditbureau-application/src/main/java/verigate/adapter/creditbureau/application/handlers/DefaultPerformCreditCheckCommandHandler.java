/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.creditbureau.domain.handlers.PerformCreditCheckCommandHandler;
import verigate.adapter.creditbureau.domain.mappers.CreditCheckMapper;
import verigate.adapter.creditbureau.domain.models.CreditAssessment;
import verigate.adapter.creditbureau.domain.models.CreditCheckRequest;
import verigate.adapter.creditbureau.domain.models.CreditCheckResponse;
import verigate.adapter.creditbureau.domain.models.CreditCheckStatus;
import verigate.adapter.creditbureau.domain.services.CreditBureauService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing credit check commands using a credit bureau provider.
 */
public class DefaultPerformCreditCheckCommandHandler
    implements PerformCreditCheckCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultPerformCreditCheckCommandHandler.class);

  private final CreditBureauService creditBureauService;

  /**
   * Constructor for default credit check handler.
   *
   * @param creditBureauService the credit bureau service
   */
  public DefaultPerformCreditCheckCommandHandler(CreditBureauService creditBureauService) {
    this.creditBureauService = creditBureauService;
  }

  /**
   * Handles credit check verification using a credit bureau provider.
   *
   * @param command the command to handle
   * @return map containing verification results
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting credit check for command: {}", command.getId());

    try {
      CreditCheckRequest creditCheckRequest =
          CreditCheckMapper.mapToCreditCheckRequestDefault(command);

      logger.info(
          "Mapped command to credit check request for ID: {}",
          maskIdNumber(creditCheckRequest.idNumber()));

      CreditCheckResponse creditCheckResponse =
          creditBureauService.performCreditCheck(creditCheckRequest);

      logger.info(
          "Credit check completed with status: {} for command: {}",
          creditCheckResponse.status(),
          command.getId());

      return analyzeAndBuildResult(creditCheckResponse);

    } catch (TransientException | PermanentException e) {
      logger.error(
          "Credit check failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid credit check request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during credit check for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected credit check error", e);
    }
  }

  /**
   * Analyzes the credit check response and builds the result map with the appropriate
   * verification outcome.
   */
  private Map<String, String> analyzeAndBuildResult(CreditCheckResponse response) {
    VerificationOutcome outcome = determineOutcome(response);
    Map<String, String> result = new HashMap<>();

    result.put("outcome", outcome.toString());
    result.put("status", response.status().toString());
    result.put("riskLevel", response.riskLevel() != null ? response.riskLevel() : "");
    result.put("summary", response.summary() != null ? response.summary() : "");
    result.put("affordability", String.valueOf(response.affordabilityConfirmed()));

    if (response.assessment() != null) {
      CreditAssessment assessment = response.assessment();
      result.put("creditScore", String.valueOf(assessment.creditScore()));
      result.put("scoreBand", assessment.scoreBand().toString());
      result.put("debtToIncomeRatio", String.valueOf(assessment.debtToIncomeRatio()));
      result.put("hasJudgments", String.valueOf(assessment.hasJudgments()));
      result.put("hasDefaults", String.valueOf(assessment.hasDefaults()));
    }

    logger.info("Credit check analysis outcome: {}", outcome);
    return result;
  }

  /**
   * Determines the verification outcome based on the credit check response status.
   */
  private VerificationOutcome determineOutcome(CreditCheckResponse response) {
    CreditCheckStatus status = response.status();

    return switch (status) {
      case PASSED -> {
        logger.info("Credit check passed");
        yield VerificationOutcome.SUCCEEDED;
      }
      case FAILED -> {
        logger.info("Credit check failed - hard fail");
        yield VerificationOutcome.HARD_FAIL;
      }
      case REVIEW_REQUIRED -> {
        if (response.assessment() != null
            && (response.assessment().hasJudgments() || response.assessment().hasDefaults())) {
          logger.info("Credit check review required with judgments or defaults - soft fail");
          yield VerificationOutcome.SOFT_FAIL;
        }
        logger.info("Credit check review required without adverse findings - succeeded with note");
        yield VerificationOutcome.SUCCEEDED;
      }
      case INSUFFICIENT_DATA -> {
        logger.info("Credit check insufficient data - soft fail");
        yield VerificationOutcome.SOFT_FAIL;
      }
      case BUREAU_UNAVAILABLE -> {
        logger.warn("Credit bureau unavailable - system outage");
        yield VerificationOutcome.SYSTEM_OUTAGE;
      }
      case ERROR -> {
        logger.error("Credit check error - system outage");
        yield VerificationOutcome.SYSTEM_OUTAGE;
      }
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
            String details = result.get("summary");
            return new VerificationResult(outcome, details);
          } catch (Exception e) {
            logger.error(
                "Async credit check failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Credit check failed: " + e.getMessage());
          }
        });
  }

  /**
   * Masks an ID number for safe logging by showing only the first 3 and last 2 characters.
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 6) {
      return "***";
    }
    return idNumber.substring(0, 3) + "***" + idNumber.substring(idNumber.length() - 2);
  }
}
