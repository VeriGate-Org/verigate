/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.fraudwatchlist.domain.constants.DomainConstants;
import verigate.adapter.fraudwatchlist.domain.handlers.ScreenFraudWatchlistCommandHandler;
import verigate.adapter.fraudwatchlist.domain.mappers.FraudWatchlistMapper;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckRequest;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckResponse;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckStatus;
import verigate.adapter.fraudwatchlist.domain.models.FraudSource;
import verigate.adapter.fraudwatchlist.domain.services.FraudWatchlistScreeningService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing fraud watchlist screening commands.
 */
public class DefaultScreenFraudWatchlistCommandHandler
    implements ScreenFraudWatchlistCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultScreenFraudWatchlistCommandHandler.class);

  private final FraudWatchlistScreeningService screeningService;

  /**
   * Constructor for default fraud watchlist screening handler.
   *
   * @param screeningService the fraud watchlist screening service
   */
  public DefaultScreenFraudWatchlistCommandHandler(
      FraudWatchlistScreeningService screeningService) {
    this.screeningService = screeningService;
  }

  /**
   * Handles fraud watchlist screening using the fraud watchlist API.
   *
   * @param command the command to handle
   * @return map containing screening results
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting fraud watchlist screening for command: {}", command.getId());

    try {
      FraudCheckRequest checkRequest =
          FraudWatchlistMapper.mapToFraudCheckRequestDefault(command);

      logger.info(
          "Mapped command to fraud check request for ID: {}",
          maskPii(checkRequest.idNumber()));

      FraudCheckResponse checkResponse = screeningService.checkFraudWatchlist(checkRequest);

      logger.info(
          "Fraud watchlist screening completed with status: {} for command: {}",
          checkResponse.status(),
          command.getId());

      VerificationOutcome outcome = determineOutcome(checkResponse);
      String details = buildDetails(checkResponse, outcome);

      logger.info(
          "Fraud watchlist screening outcome: {} for command: {}",
          outcome,
          command.getId());

      String matchedSourcesStr = checkResponse.matchedSources() != null
          ? checkResponse.matchedSources().stream()
              .map(FraudSource::name)
              .collect(Collectors.joining(","))
          : "";

      return Map.of(
          "outcome", outcome.toString(),
          "details", details,
          "fraudStatus", checkResponse.status().toString(),
          "riskScore", String.valueOf(checkResponse.overallRiskScore()),
          "alertCount", String.valueOf(
              checkResponse.alerts() != null ? checkResponse.alerts().size() : 0),
          "matchedSources", matchedSourcesStr,
          "summary", checkResponse.screeningSummary() != null
              ? checkResponse.screeningSummary() : "");

    } catch (TransientException | PermanentException e) {
      logger.error(
          "Fraud watchlist screening failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid fraud watchlist screening request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during fraud watchlist screening for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected fraud watchlist screening error", e);
    }
  }

  /**
   * Determines the verification outcome based on the fraud check response.
   *
   * @param response the fraud check response
   * @return the verification outcome
   */
  private VerificationOutcome determineOutcome(FraudCheckResponse response) {
    FraudCheckStatus status = response.status();
    double riskScore = response.overallRiskScore();

    return switch (status) {
      case CLEAR -> VerificationOutcome.SUCCEEDED;
      case CONFIRMED_FRAUD -> VerificationOutcome.HARD_FAIL;
      case SUSPECTED_FRAUD -> VerificationOutcome.SOFT_FAIL;
      case FLAGGED -> determineFlaggedOutcome(riskScore);
      case NOT_FOUND -> VerificationOutcome.SUCCEEDED;
      case ERROR -> VerificationOutcome.SYSTEM_OUTAGE;
    };
  }

  /**
   * Determines the outcome for a FLAGGED status based on the risk score.
   */
  private VerificationOutcome determineFlaggedOutcome(double riskScore) {
    if (riskScore >= DomainConstants.RISK_THRESHOLD_HIGH) {
      return VerificationOutcome.HARD_FAIL;
    } else if (riskScore >= 0.5) {
      return VerificationOutcome.SOFT_FAIL;
    } else {
      return VerificationOutcome.SUCCEEDED;
    }
  }

  /**
   * Builds the details string for the verification result.
   */
  private String buildDetails(FraudCheckResponse response, VerificationOutcome outcome) {
    FraudCheckStatus status = response.status();

    return switch (status) {
      case CLEAR -> "Fraud watchlist screening clear. No alerts found.";
      case CONFIRMED_FRAUD -> String.format(
          "Confirmed fraud detected. Risk score: %.2f. Alerts: %d.",
          response.overallRiskScore(),
          response.alerts() != null ? response.alerts().size() : 0);
      case SUSPECTED_FRAUD -> String.format(
          "Suspected fraud detected. Risk score: %.2f. Alerts: %d.",
          response.overallRiskScore(),
          response.alerts() != null ? response.alerts().size() : 0);
      case FLAGGED -> buildFlaggedDetails(response, outcome);
      case NOT_FOUND -> "No records found on fraud watchlists.";
      case ERROR -> "System error during fraud watchlist screening: "
          + (response.screeningSummary() != null ? response.screeningSummary() : "Unknown error");
    };
  }

  /**
   * Builds details for FLAGGED status with risk score context.
   */
  private String buildFlaggedDetails(FraudCheckResponse response, VerificationOutcome outcome) {
    String baseDetail = String.format(
        "Flagged on fraud watchlist. Risk score: %.2f. Alerts: %d.",
        response.overallRiskScore(),
        response.alerts() != null ? response.alerts().size() : 0);

    if (outcome == VerificationOutcome.SUCCEEDED) {
      return baseDetail + " Low risk - proceeding with warnings.";
    }

    return baseDetail;
  }

  /**
   * Alternative async method for non-blocking screening.
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
                "Async fraud watchlist screening failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Fraud watchlist screening failed: " + e.getMessage());
          }
        });
  }

  /**
   * Masks personally identifiable information for safe logging.
   *
   * @param value the PII value to mask
   * @return the masked value
   */
  private String maskPii(String value) {
    if (value == null || value.trim().isEmpty()) {
      return "***";
    }
    String trimmed = value.trim();
    if (trimmed.length() < 6) {
      return "***";
    }
    return trimmed.substring(0, 3) + "***" + trimmed.substring(trimmed.length() - 2);
  }
}
