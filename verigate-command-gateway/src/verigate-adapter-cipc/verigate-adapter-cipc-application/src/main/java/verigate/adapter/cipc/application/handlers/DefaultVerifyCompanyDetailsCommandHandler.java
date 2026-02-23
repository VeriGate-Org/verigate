/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.cipc.domain.handlers.VerifyCompanyDetailsCommandHandler;
import verigate.adapter.cipc.domain.mappers.CompanyProfileMapper;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.CompanyProfileRequest;
import verigate.adapter.cipc.domain.models.CompanyProfileResponse;
import verigate.adapter.cipc.domain.services.CipcCompanyService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing company verification commands using the CIPC public data
 * API.
 */
public class DefaultVerifyCompanyDetailsCommandHandler
    implements VerifyCompanyDetailsCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyCompanyDetailsCommandHandler.class);

  private final CipcCompanyService companyService;

  /**
   * Constructor for default company verification handler.
   *
   * @param companyService the CIPC company service
   */
  public DefaultVerifyCompanyDetailsCommandHandler(CipcCompanyService companyService) {
    this.companyService = companyService;
  }

  /**
   * Handles company verification using the CIPC public data API.
   *
   * @param command the command to handle
   * @return map containing verification results (for compatibility with existing interface)
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting company verification for command: {}", command.getId());

    try {
      CompanyProfileRequest profileRequest =
          CompanyProfileMapper.mapToCompanyProfileRequestDefault(command);

      logger.info(
          "Mapped command to company profile request for enterprise: {}",
          profileRequest.enterpriseNumber());

      CompanyProfileResponse profileResponse = companyService.getCompanyProfile(profileRequest);

      if (!profileResponse.isSuccess()) {
        if (!profileResponse.found() && profileResponse.errorMessage() != null) {
          if (profileResponse.errorMessage().contains("not found")) {
            logger.info("Company not found: {}", profileRequest.enterpriseNumber());
            return Map.of(
                "outcome",
                VerificationOutcome.SOFT_FAIL.toString(),
                "details",
                "Company not found in CIPC registry",
                "enterpriseNumber",
                profileRequest.enterpriseNumber(),
                "found",
                "false");
          }
        }

        logger.error(
            "Company profile retrieval failed: {}",
            profileResponse.errorMessage());
        throw new PermanentException(
            "Failed to retrieve CIPC company profile: " + profileResponse.errorMessage());
      }

      logger.info(
          "Company profile retrieved successfully for: {}",
          profileResponse.company().enterpriseNumber());

      VerificationResult verificationResult =
          analyzeCompanyProfile(profileResponse.company(), command);

      logger.info(
          "Company verification completed with outcome: {}",
          verificationResult.outcome());

      return Map.of(
          "outcome",
          verificationResult.outcome().toString(),
          "details",
          verificationResult.failureReason() != null ? verificationResult.failureReason() : "",
          "enterpriseNumber",
          profileResponse.company().enterpriseNumber(),
          "enterpriseName",
          profileResponse.company().enterpriseName() != null
              ? profileResponse.company().enterpriseName()
              : "",
          "enterpriseStatus",
          profileResponse.company().enterpriseStatus().toString(),
          "isActive",
          String.valueOf(profileResponse.company().isActive()),
          "activeDirectorsCount",
          String.valueOf(profileResponse.company().getActiveDirectors().size()),
          "totalDirectorsCount",
          String.valueOf(profileResponse.company().directors().size()));
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Company verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid company verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during company verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected company verification error", e);
    }
  }

  /**
   * Analyzes the company profile to determine verification outcome.
   *
   * @param company the company profile
   * @param command the original command
   * @return the verification result
   */
  private VerificationResult analyzeCompanyProfile(
      CompanyProfile company, VerifyPartyCommand command) {

    if (!company.isActive()) {
      String reason =
          String.format("Company is not active. Status: %s", company.enterpriseStatus());
      logger.info(
          "Company verification failed - inactive company: {}",
          company.enterpriseNumber());
      return new VerificationResult(VerificationOutcome.SOFT_FAIL, reason);
    }

    if (company.enterpriseName() == null || company.enterpriseName().trim().isEmpty()) {
      String reason = "Company name is missing or empty";
      logger.warn(
          "Company verification failed - missing company name: {}",
          company.enterpriseNumber());
      return new VerificationResult(VerificationOutcome.SOFT_FAIL, reason);
    }

    if (company.getActiveDirectors().isEmpty()) {
      String reason = "Company has no active directors";
      logger.info(
          "Company verification soft fail - no active directors: {}",
          company.enterpriseNumber());
      return new VerificationResult(VerificationOutcome.SOFT_FAIL, reason);
    }

    String successMessage =
        String.format(
            "Company verified successfully. Name: %s, Status: %s, Active Directors: %d",
            company.getDisplayName(),
            company.enterpriseStatus(),
            company.getActiveDirectors().size());

    logger.info("Company verification succeeded: {}", company.enterpriseNumber());
    return new VerificationResult(VerificationOutcome.SUCCEEDED, successMessage);
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
                "Async company verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Company verification failed: " + e.getMessage());
          }
        });
  }
}
