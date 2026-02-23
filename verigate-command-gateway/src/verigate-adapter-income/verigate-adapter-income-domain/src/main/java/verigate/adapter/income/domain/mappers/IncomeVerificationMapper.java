/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.mappers;

import java.math.BigDecimal;
import verigate.adapter.income.domain.constants.DomainConstants;
import verigate.adapter.income.domain.enums.IncomeSourceType;
import verigate.adapter.income.domain.models.IncomeVerificationRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to income verification requests.
 */
public interface IncomeVerificationMapper {

  /**
   * Maps a VerifyPartyCommand to an IncomeVerificationRequest.
   *
   * @param command the verification command
   * @return the mapped income verification request
   */
  IncomeVerificationRequest mapToIncomeVerificationRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   *
   * @param command the verification command
   * @return the mapped income verification request
   */
  static IncomeVerificationRequest mapToIncomeVerificationRequestDefault(
      VerifyPartyCommand command) {

    String idNumber = extractIdNumber(command);

    if (idNumber == null || idNumber.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "ID number is required for income verification");
    }

    String employerName = extractEmployerName(command);
    BigDecimal declaredMonthlyIncome = extractDeclaredMonthlyIncome(command);
    IncomeSourceType sourceType = extractIncomeSourceType(command);
    String bankAccountNumber = extractBankAccountNumber(command);

    return IncomeVerificationRequest.builder()
        .idNumber(idNumber.trim())
        .employerName(employerName != null ? employerName.trim() : null)
        .declaredMonthlyIncome(declaredMonthlyIncome)
        .sourceType(sourceType)
        .bankAccountNumber(bankAccountNumber != null ? bankAccountNumber.trim() : null)
        .build();
  }

  /**
   * Extracts the ID number from the command metadata.
   */
  static String extractIdNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_ID_NUMBER);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_KEY_ID_NUMBER_ALT);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("identityNumber");
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("identity_number");
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the employer name from the command metadata.
   */
  static String extractEmployerName(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_EMPLOYER_NAME);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_KEY_EMPLOYER_NAME_ALT);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("companyName");
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("company_name");
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the declared monthly income from the command metadata.
   */
  static BigDecimal extractDeclaredMonthlyIncome(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_DECLARED_MONTHLY_INCOME);
    if (value == null) {
      value = command.getMetadata()
          .get(DomainConstants.METADATA_KEY_DECLARED_MONTHLY_INCOME_ALT);
    }
    if (value == null) {
      value = command.getMetadata().get("monthlyIncome");
    }
    if (value == null) {
      value = command.getMetadata().get("monthly_income");
    }

    if (value == null) {
      return null;
    }

    try {
      return new BigDecimal(value.toString().trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Invalid declared monthly income value: " + value);
    }
  }

  /**
   * Extracts the income source type from the command metadata.
   */
  static IncomeSourceType extractIncomeSourceType(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return IncomeSourceType.OTHER;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_INCOME_SOURCE_TYPE);
    if (value == null) {
      value = command.getMetadata().get(DomainConstants.METADATA_KEY_INCOME_SOURCE_TYPE_ALT);
    }
    if (value == null) {
      value = command.getMetadata().get("sourceType");
    }
    if (value == null) {
      value = command.getMetadata().get("source_type");
    }

    if (value == null) {
      return IncomeSourceType.OTHER;
    }

    return IncomeSourceType.fromDescription(value.toString());
  }

  /**
   * Extracts the bank account number from the command metadata.
   */
  static String extractBankAccountNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_BANK_ACCOUNT_NUMBER);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_KEY_BANK_ACCOUNT_NUMBER_ALT);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("accountNumber");
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("account_number");
    return value != null ? value.toString() : null;
  }
}
