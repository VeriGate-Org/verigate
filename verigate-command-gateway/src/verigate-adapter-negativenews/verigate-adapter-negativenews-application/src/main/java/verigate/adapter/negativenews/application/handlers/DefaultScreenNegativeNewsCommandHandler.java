/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.negativenews.domain.constants.DomainConstants;
import verigate.adapter.negativenews.domain.handlers.ScreenNegativeNewsCommandHandler;
import verigate.adapter.negativenews.domain.mappers.NegativeNewsScreeningMapper;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningRequest;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningResponse;
import verigate.adapter.negativenews.domain.models.ScreeningOutcome;
import verigate.adapter.negativenews.domain.services.NegativeNewsScreeningService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing negative news screening commands.
 */
public class DefaultScreenNegativeNewsCommandHandler
    implements ScreenNegativeNewsCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultScreenNegativeNewsCommandHandler.class);

  private final NegativeNewsScreeningService screeningService;

  /**
   * Constructor for default negative news screening handler.
   *
   * @param screeningService the negative news screening service
   */
  public DefaultScreenNegativeNewsCommandHandler(
      NegativeNewsScreeningService screeningService) {
    this.screeningService = screeningService;
  }

  /**
   * Handles negative news screening using the configured screening API.
   *
   * @param command the command to handle
   * @return map containing screening results
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting negative news screening for command: {}", command.getId());

    try {
      NegativeNewsScreeningRequest screeningRequest =
          NegativeNewsScreeningMapper.mapToScreeningRequestDefault(command);

      logger.info(
          "Mapped command to negative news screening request for subject: [MASKED]");

      NegativeNewsScreeningResponse screeningResponse =
          screeningService.screenForNegativeNews(screeningRequest);

      logger.info(
          "Negative news screening completed with outcome: {} for command: {}",
          screeningResponse.outcome(),
          command.getId());

      VerificationOutcome verificationOutcome =
          determineVerificationOutcome(screeningResponse);

      String details = buildDetails(screeningResponse, verificationOutcome);

      logger.info(
          "Negative news screening resolved to verification outcome: {} for command: {}",
          verificationOutcome,
          command.getId());

      return Map.of(
          "outcome", verificationOutcome.toString(),
          "details", details,
          "screeningOutcome", screeningResponse.outcome().toString(),
          "totalArticles", String.valueOf(screeningResponse.totalArticlesFound()),
          "adverseCount", String.valueOf(screeningResponse.adverseArticlesCount()),
          "severity", String.valueOf(screeningResponse.highestSeverityScore()),
          "summary", screeningResponse.screeningSummary() != null
              ? screeningResponse.screeningSummary() : "");

    } catch (TransientException | PermanentException e) {
      logger.error(
          "Negative news screening failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid negative news screening request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during negative news screening for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected negative news screening error", e);
    }
  }

  /**
   * Determines the verification outcome based on the screening response.
   *
   * <p>Analysis logic:
   * <ul>
   *   <li>CLEAR -> SUCCEEDED</li>
   *   <li>ADVERSE_FOUND with highestSeverityScore >= 0.9 -> HARD_FAIL</li>
   *   <li>ADVERSE_FOUND with highestSeverityScore >= 0.7 -> SOFT_FAIL</li>
   *   <li>ADVERSE_FOUND with lower score -> SUCCEEDED (with warnings)</li>
   *   <li>INCONCLUSIVE -> SOFT_FAIL</li>
   *   <li>ERROR -> SYSTEM_OUTAGE</li>
   * </ul>
   *
   * @param response the screening response
   * @return the verification outcome
   */
  private VerificationOutcome determineVerificationOutcome(
      NegativeNewsScreeningResponse response) {

    ScreeningOutcome screeningOutcome = response.outcome();

    return switch (screeningOutcome) {
      case CLEAR -> VerificationOutcome.SUCCEEDED;
      case ADVERSE_FOUND -> {
        double severity = response.highestSeverityScore();
        if (severity >= DomainConstants.SENTIMENT_THRESHOLD_HIGHLY_NEGATIVE) {
          yield VerificationOutcome.HARD_FAIL;
        } else if (severity >= DomainConstants.SENTIMENT_THRESHOLD_NEGATIVE) {
          yield VerificationOutcome.SOFT_FAIL;
        } else {
          yield VerificationOutcome.SUCCEEDED;
        }
      }
      case INCONCLUSIVE -> VerificationOutcome.SOFT_FAIL;
      case ERROR -> VerificationOutcome.SYSTEM_OUTAGE;
    };
  }

  /**
   * Builds a details string based on the screening response and outcome.
   */
  private String buildDetails(
      NegativeNewsScreeningResponse response, VerificationOutcome outcome) {

    return switch (response.outcome()) {
      case CLEAR -> String.format(
          "No adverse news found. Total articles scanned: %d",
          response.totalArticlesFound());
      case ADVERSE_FOUND -> {
        if (outcome == VerificationOutcome.SUCCEEDED) {
          yield String.format(
              "Adverse news found with low severity (score: %.2f). "
                  + "Adverse articles: %d of %d total. Proceeding with warnings.",
              response.highestSeverityScore(),
              response.adverseArticlesCount(),
              response.totalArticlesFound());
        } else {
          yield String.format(
              "Adverse news found. Severity score: %.2f. "
                  + "Adverse articles: %d of %d total. %s",
              response.highestSeverityScore(),
              response.adverseArticlesCount(),
              response.totalArticlesFound(),
              response.screeningSummary() != null ? response.screeningSummary() : "");
        }
      }
      case INCONCLUSIVE -> String.format(
          "Negative news screening was inconclusive. %s",
          response.screeningSummary() != null ? response.screeningSummary() : "");
      case ERROR -> String.format(
          "Negative news screening encountered an error. %s",
          response.screeningSummary() != null ? response.screeningSummary() : "");
    };
  }

  /**
   * Alternative async method for non-blocking screening. This method can be used for future
   * async support.
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
                "Async negative news screening failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Negative news screening failed: " + e.getMessage());
          }
        });
  }
}
