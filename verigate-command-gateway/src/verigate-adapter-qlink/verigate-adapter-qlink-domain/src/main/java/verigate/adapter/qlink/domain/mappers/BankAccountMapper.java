/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.domain.mappers;

import verigate.adapter.qlink.domain.constants.DomainConstants;
import verigate.adapter.qlink.domain.models.BankVerificationRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to QLink bank verification requests.
 */
public interface BankAccountMapper {

  /**
   * Maps a VerifyPartyCommand to a BankVerificationRequest.
   *
   * @param command the verification command
   * @return the mapped bank verification request
   */
  BankVerificationRequest mapToBankVerificationRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   */
  static BankVerificationRequest mapToBankVerificationRequestDefault(VerifyPartyCommand command) {

    // Extract bank details from command metadata
    String accountNumber = extractMetadataValue(command, DomainConstants.METADATA_ACCOUNT_NUMBER);
    String branchCode = extractMetadataValue(command, DomainConstants.METADATA_BRANCH_CODE);
    String accountHolderName = extractMetadataValue(command,
        DomainConstants.METADATA_ACCOUNT_HOLDER_NAME);
    String idNumber = extractMetadataValue(command, DomainConstants.METADATA_ID_NUMBER);

    if (accountNumber == null || accountNumber.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Account number is required for QLink bank verification");
    }

    if (branchCode == null || branchCode.trim().isEmpty()) {
      throw new IllegalArgumentException("Branch code is required for QLink bank verification");
    }

    return BankVerificationRequest.builder()
        .accountNumber(accountNumber.trim())
        .branchCode(branchCode.trim())
        .accountHolderName(accountHolderName != null ? accountHolderName.trim() : null)
        .idNumber(idNumber != null ? idNumber.trim() : null)
        .build();
  }

  /**
   * Extracts a metadata value from the command by key.
   */
  static String extractMetadataValue(VerifyPartyCommand command, String key) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(key);
    return value != null ? value.toString() : null;
  }

  /**
   * Validates that the account number is in a valid format.
   */
  static boolean isValidAccountNumber(String accountNumber) {
    if (accountNumber == null || accountNumber.trim().isEmpty()) {
      return false;
    }

    // Bank account numbers are typically numeric and between 7 and 16 digits
    return accountNumber.trim().matches("\\d{7,16}");
  }

  /**
   * Validates that the branch code is in a valid format.
   */
  static boolean isValidBranchCode(String branchCode) {
    if (branchCode == null || branchCode.trim().isEmpty()) {
      return false;
    }

    // South African branch codes are typically 6 digits
    return branchCode.trim().matches("\\d{5,6}");
  }
}
