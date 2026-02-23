/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.qlink.domain.constants.DomainConstants;
import verigate.adapter.qlink.infrastructure.http.dto.QLinkBankVerificationRequestDto;
import verigate.adapter.qlink.infrastructure.http.dto.QLinkBankVerificationResponseDto;

/**
 * API adapter for calling the QLink bank verification endpoints.
 */
public class QLinkBankApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(QLinkBankApiAdapter.class);

  private final QLinkHttpAdapter httpAdapter;

  public QLinkBankApiAdapter(QLinkHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Verifies bank account details using the QLink bank verification endpoint.
   */
  public QLinkBankVerificationResponseDto verifyBankAccount(
      String accountNumber,
      String branchCode,
      String accountHolderName,
      String idNumber)
      throws TransientException, PermanentException {

    validateAccountNumber(accountNumber);
    validateBranchCode(branchCode);

    String maskedAccount = maskAccountNumber(accountNumber);
    logger.debug(
        "Requesting bank account verification for account ending: {}",
        maskedAccount);

    QLinkBankVerificationRequestDto request =
        new QLinkBankVerificationRequestDto(
            accountNumber.trim(),
            branchCode.trim(),
            accountHolderName != null ? accountHolderName.trim() : null,
            idNumber != null ? idNumber.trim() : null);

    try {
      QLinkBankVerificationResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_VERIFY_BANK_ACCOUNT,
              request,
              QLinkBankVerificationResponseDto.class);

      logger.debug(
          "Successfully completed bank verification for account ending: {}",
          maskedAccount);
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to verify bank account ending {}: {}",
          maskedAccount,
          e.getMessage());
      throw e;
    }
  }

  private void validateAccountNumber(String accountNumber) {
    if (accountNumber == null || accountNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Account number cannot be null or empty");
    }
  }

  private void validateBranchCode(String branchCode) {
    if (branchCode == null || branchCode.trim().isEmpty()) {
      throw new IllegalArgumentException("Branch code cannot be null or empty");
    }
  }

  private String maskAccountNumber(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 4) {
      return "****";
    }
    return "****" + accountNumber.substring(accountNumber.length() - 4);
  }
}
