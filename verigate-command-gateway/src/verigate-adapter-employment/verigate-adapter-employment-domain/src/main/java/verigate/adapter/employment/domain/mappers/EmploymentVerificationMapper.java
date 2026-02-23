/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.domain.mappers;

import verigate.adapter.employment.domain.constants.DomainConstants;
import verigate.adapter.employment.domain.models.EmploymentVerificationRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to employment verification requests.
 */
public interface EmploymentVerificationMapper {

  /**
   * Maps a VerifyPartyCommand to an EmploymentVerificationRequest.
   *
   * @param command the verification command
   * @return the mapped employment verification request
   */
  EmploymentVerificationRequest mapToEmploymentVerificationRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   */
  static EmploymentVerificationRequest mapToEmploymentVerificationRequestDefault(
      VerifyPartyCommand command) {

    String idNumber = extractIdNumber(command);

    if (idNumber == null || idNumber.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "ID number is required for employment verification");
    }

    String employerName = extractEmployerName(command);
    String employeeNumber = extractEmployeeNumber(command);
    String startDate = extractStartDate(command);

    return EmploymentVerificationRequest.builder()
        .idNumber(idNumber.trim())
        .employerName(employerName != null ? employerName.trim() : null)
        .employeeNumber(employeeNumber != null ? employeeNumber.trim() : null)
        .startDate(startDate != null ? startDate.trim() : null)
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
   * Extracts the employee number from the command metadata.
   */
  static String extractEmployeeNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_EMPLOYEE_NUMBER);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_KEY_EMPLOYEE_NUMBER_ALT);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("staffNumber");
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("staff_number");
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the start date from the command metadata.
   */
  static String extractStartDate(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_START_DATE);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_KEY_START_DATE_ALT);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("employmentStartDate");
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("employment_start_date");
    return value != null ? value.toString() : null;
  }
}
