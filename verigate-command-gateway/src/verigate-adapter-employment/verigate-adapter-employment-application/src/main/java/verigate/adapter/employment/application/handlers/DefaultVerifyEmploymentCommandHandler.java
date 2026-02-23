/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.employment.domain.constants.DomainConstants;
import verigate.adapter.employment.domain.handlers.VerifyEmploymentCommandHandler;
import verigate.adapter.employment.domain.mappers.EmploymentVerificationMapper;
import verigate.adapter.employment.domain.models.EmploymentStatus;
import verigate.adapter.employment.domain.models.EmploymentVerificationRequest;
import verigate.adapter.employment.domain.models.EmploymentVerificationResponse;
import verigate.adapter.employment.domain.services.EmploymentVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing employment verification commands.
 */
public class DefaultVerifyEmploymentCommandHandler
    implements VerifyEmploymentCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyEmploymentCommandHandler.class);

  private final EmploymentVerificationService employmentVerificationService;

  /**
   * Constructor for default employment verification handler.
   *
   * @param employmentVerificationService the employment verification service
   */
  public DefaultVerifyEmploymentCommandHandler(
      EmploymentVerificationService employmentVerificationService) {
    this.employmentVerificationService = employmentVerificationService;
  }

  /**
   * Handles employment verification using the employment verification API.
   *
   * @param command the command to handle
   * @return map containing verification results (for compatibility with existing interface)
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting employment verification for command: {}", command.getId());

    try {
      EmploymentVerificationRequest verificationRequest =
          EmploymentVerificationMapper.mapToEmploymentVerificationRequestDefault(command);

      logger.info(
          "Mapped command to employment verification request for ID: {}",
          maskIdNumber(verificationRequest.idNumber()));

      EmploymentVerificationResponse verificationResponse =
          employmentVerificationService.verifyEmployment(verificationRequest);

      logger.info(
          "Employment verification completed with status: {} for ID: {}",
          verificationResponse.status(),
          maskIdNumber(verificationRequest.idNumber()));

      return buildResultMap(verificationResponse, verificationRequest);

    } catch (TransientException | PermanentException e) {
      logger.error(
          "Employment verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid employment verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during employment verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected employment verification error", e);
    }
  }

  /**
   * Builds the result map based on the verification response and analysis logic.
   *
   * @param response the employment verification response
   * @param request the original request
   * @return the result map with outcome and details
   */
  private Map<String, String> buildResultMap(
      EmploymentVerificationResponse response,
      EmploymentVerificationRequest request) {

    VerificationOutcome outcome = analyzeEmploymentStatus(response.status());
    String details = buildDetailsMessage(response);

    Map<String, String> result = new HashMap<>();
    result.put(DomainConstants.RESULT_KEY_OUTCOME, outcome.toString());
    result.put(DomainConstants.RESULT_KEY_DETAILS, details);
    result.put(DomainConstants.RESULT_KEY_EMPLOYMENT_STATUS, response.status().toString());
    result.put(DomainConstants.RESULT_KEY_EMPLOYER_NAME,
        response.employerName() != null ? response.employerName() : "");
    result.put(DomainConstants.RESULT_KEY_JOB_TITLE,
        response.jobTitle() != null ? response.jobTitle() : "");
    result.put(DomainConstants.RESULT_KEY_EMPLOYMENT_TYPE,
        response.employmentType() != null ? response.employmentType().toString() : "");
    result.put(DomainConstants.RESULT_KEY_START_DATE,
        response.startDate() != null ? response.startDate() : "");
    result.put(DomainConstants.RESULT_KEY_END_DATE,
        response.endDate() != null ? response.endDate() : "");
    result.put(DomainConstants.RESULT_KEY_DEPARTMENT,
        response.department() != null ? response.department() : "");

    logger.info(
        "Employment verification result - outcome: {}, status: {}, employer: {}",
        outcome,
        response.status(),
        response.employerName());

    return result;
  }

  /**
   * Analyzes the employment status to determine the verification outcome.
   *
   * <p>Analysis logic:
   * <ul>
   *   <li>EMPLOYED -> SUCCEEDED</li>
   *   <li>TERMINATED -> SOFT_FAIL</li>
   *   <li>ON_LEAVE -> SUCCEEDED (with note)</li>
   *   <li>SUSPENDED -> SOFT_FAIL</li>
   *   <li>NOT_FOUND -> HARD_FAIL</li>
   *   <li>UNVERIFIABLE -> SOFT_FAIL</li>
   * </ul>
   *
   * @param status the employment status
   * @return the corresponding verification outcome
   */
  private VerificationOutcome analyzeEmploymentStatus(EmploymentStatus status) {
    return switch (status) {
      case EMPLOYED -> VerificationOutcome.SUCCEEDED;
      case TERMINATED -> VerificationOutcome.SOFT_FAIL;
      case ON_LEAVE -> VerificationOutcome.SUCCEEDED;
      case SUSPENDED -> VerificationOutcome.SOFT_FAIL;
      case NOT_FOUND -> VerificationOutcome.HARD_FAIL;
      case UNVERIFIABLE -> VerificationOutcome.SOFT_FAIL;
    };
  }

  /**
   * Builds a human-readable details message based on the verification response.
   */
  private String buildDetailsMessage(EmploymentVerificationResponse response) {
    return switch (response.status()) {
      case EMPLOYED -> String.format(
          "Employment verified successfully. Employer: %s, Title: %s, Type: %s",
          response.employerName() != null ? response.employerName() : "N/A",
          response.jobTitle() != null ? response.jobTitle() : "N/A",
          response.employmentType() != null ? response.employmentType() : "N/A");
      case TERMINATED -> String.format(
          "Employment terminated. Employer: %s, End Date: %s",
          response.employerName() != null ? response.employerName() : "N/A",
          response.endDate() != null ? response.endDate() : "N/A");
      case ON_LEAVE -> String.format(
          "Employee currently on leave. Employer: %s, Title: %s",
          response.employerName() != null ? response.employerName() : "N/A",
          response.jobTitle() != null ? response.jobTitle() : "N/A");
      case SUSPENDED -> String.format(
          "Employee currently suspended. Employer: %s",
          response.employerName() != null ? response.employerName() : "N/A");
      case NOT_FOUND -> "No employment record found for the provided details";
      case UNVERIFIABLE -> "Employment could not be verified at this time";
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
            VerificationOutcome outcome = VerificationOutcome.valueOf(result.get("outcome"));
            String details = result.get("details");
            return new VerificationResult(outcome, details);
          } catch (Exception e) {
            logger.error(
                "Async employment verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Employment verification failed: " + e.getMessage());
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
