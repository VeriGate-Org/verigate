/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.qlink.domain.handlers.VerifyBankAccountDetailsCommandHandler;
import verigate.adapter.qlink.domain.mappers.BankAccountMapper;
import verigate.adapter.qlink.domain.models.BankAccountStatus;
import verigate.adapter.qlink.domain.models.BankVerificationRequest;
import verigate.adapter.qlink.domain.models.BankVerificationResponse;
import verigate.adapter.qlink.domain.services.QLinkBankVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing bank account verification commands using the QLink
 * bank verification API.
 */
public class DefaultVerifyBankAccountDetailsCommandHandler
    implements VerifyBankAccountDetailsCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyBankAccountDetailsCommandHandler.class);

  private final QLinkBankVerificationService bankVerificationService;

  /**
   * Constructor for default bank account verification handler.
   *
   * @param bankVerificationService the QLink bank verification service
   */
  public DefaultVerifyBankAccountDetailsCommandHandler(
      QLinkBankVerificationService bankVerificationService) {
    this.bankVerificationService = bankVerificationService;
  }

  /**
   * Handles bank account verification using the QLink bank verification API.
   *
   * @param command the command to handle
   * @return map containing verification results (for compatibility with existing interface)
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting bank account verification for command: {}", command.getId());

    try {
      BankVerificationRequest verificationRequest =
          BankAccountMapper.mapToBankVerificationRequestDefault(command);

      logger.info(
          "Mapped command to bank verification request for account ending: {}",
          maskAccountNumber(verificationRequest.accountNumber()));

      BankVerificationResponse verificationResponse =
          bankVerificationService.verifyBankAccount(verificationRequest);

      logger.info(
          "Bank verification completed with status: {}",
          verificationResponse.status());

      VerificationResult verificationResult =
          analyzeBankAccountStatus(verificationResponse, command);

      logger.info(
          "Bank account verification completed with outcome: {}",
          verificationResult.outcome());

      return Map.of(
          "outcome",
          verificationResult.outcome().toString(),
          "details",
          verificationResult.failureReason() != null ? verificationResult.failureReason() : "",
          "bankAccountStatus",
          verificationResponse.status().toString(),
          "accountHolderName",
          verificationResponse.accountHolderName() != null
              ? verificationResponse.accountHolderName()
              : "",
          "branchName",
          verificationResponse.branchName() != null
              ? verificationResponse.branchName()
              : "",
          "accountType",
          verificationResponse.accountType() != null
              ? verificationResponse.accountType().toString()
              : "",
          "matchScore",
          String.valueOf(verificationResponse.matchScore()));
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Bank account verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid bank account verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during bank account verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected bank account verification error", e);
    }
  }

  /**
   * Analyzes the bank account verification response to determine verification outcome.
   *
   * <p>Analysis logic:
   * <ul>
   *   <li>VALID -> SUCCEEDED</li>
   *   <li>DORMANT -> SOFT_FAIL</li>
   *   <li>INVALID, CLOSED, NOT_FOUND -> HARD_FAIL</li>
   * </ul>
   *
   * @param response the bank verification response
   * @param command the original command
   * @return the verification result
   */
  private VerificationResult analyzeBankAccountStatus(
      BankVerificationResponse response, VerifyPartyCommand command) {

    BankAccountStatus status = response.status();

    switch (status) {
      case VALID:
        String successMessage =
            String.format(
                "Bank account verified successfully. Status: %s, Match Score: %.2f",
                status,
                response.matchScore());
        logger.info("Bank account verification succeeded for command: {}", command.getId());
        return new VerificationResult(VerificationOutcome.SUCCEEDED, successMessage);

      case DORMANT:
        String dormantReason =
            String.format("Bank account is dormant. Status: %s", status);
        logger.info(
            "Bank account verification soft fail - dormant account for command: {}",
            command.getId());
        return new VerificationResult(VerificationOutcome.SOFT_FAIL, dormantReason);

      case INVALID:
        String invalidReason =
            String.format("Bank account is invalid. Status: %s", status);
        logger.info(
            "Bank account verification hard fail - invalid account for command: {}",
            command.getId());
        return new VerificationResult(VerificationOutcome.HARD_FAIL, invalidReason);

      case CLOSED:
        String closedReason =
            String.format("Bank account is closed. Status: %s", status);
        logger.info(
            "Bank account verification hard fail - closed account for command: {}",
            command.getId());
        return new VerificationResult(VerificationOutcome.HARD_FAIL, closedReason);

      case NOT_FOUND:
        String notFoundReason =
            String.format("Bank account not found. Status: %s", status);
        logger.info(
            "Bank account verification hard fail - account not found for command: {}",
            command.getId());
        return new VerificationResult(VerificationOutcome.HARD_FAIL, notFoundReason);

      default:
        String unknownReason =
            String.format("Unknown bank account status: %s", status);
        logger.warn(
            "Bank account verification unknown status for command: {}", command.getId());
        return new VerificationResult(VerificationOutcome.HARD_FAIL, unknownReason);
    }
  }

  /**
   * Masks account number for safe logging, showing only the last 4 digits.
   */
  private String maskAccountNumber(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 4) {
      return "****";
    }
    return "****" + accountNumber.substring(accountNumber.length() - 4);
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
                "Async bank account verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Bank account verification failed: " + e.getMessage());
          }
        });
  }
}
