/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.mappers;

import verigate.adapter.sars.domain.constants.DomainConstants;
import verigate.adapter.sars.domain.enums.TaxClearanceType;
import verigate.adapter.sars.domain.models.TaxComplianceRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to tax compliance verification requests.
 */
public interface TaxComplianceMapper {

  /**
   * Maps a VerifyPartyCommand to a TaxComplianceRequest.
   *
   * @param command the verification command
   * @return the mapped tax compliance request
   */
  TaxComplianceRequest mapToTaxComplianceRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   *
   * @param command the verification command
   * @return the mapped tax compliance request
   */
  static TaxComplianceRequest mapToTaxComplianceRequestDefault(VerifyPartyCommand command) {

    String idNumber = extractIdNumber(command);
    String taxReferenceNumber = extractTaxReferenceNumber(command);

    if ((idNumber == null || idNumber.trim().isEmpty())
        && (taxReferenceNumber == null || taxReferenceNumber.trim().isEmpty())) {
      throw new IllegalArgumentException(
          "Either ID number or tax reference number is required for tax compliance verification");
    }

    String companyRegistrationNumber = extractCompanyRegistrationNumber(command);
    TaxClearanceType clearanceType = extractClearanceType(command);

    return TaxComplianceRequest.builder()
        .idNumber(idNumber != null ? idNumber.trim() : null)
        .taxReferenceNumber(taxReferenceNumber != null ? taxReferenceNumber.trim() : null)
        .companyRegistrationNumber(
            companyRegistrationNumber != null ? companyRegistrationNumber.trim() : null)
        .clearanceType(clearanceType)
        .build();
  }

  /**
   * Extracts the ID number from the command metadata.
   *
   * @param command the verification command
   * @return the ID number, or null if not present
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
   * Extracts the tax reference number from the command metadata.
   *
   * @param command the verification command
   * @return the tax reference number, or null if not present
   */
  static String extractTaxReferenceNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_TAX_REFERENCE_NUMBER);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_KEY_TAX_REFERENCE_NUMBER_ALT);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("taxRefNumber");
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("tax_ref_number");
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the company registration number from the command metadata.
   *
   * @param command the verification command
   * @return the company registration number, or null if not present
   */
  static String extractCompanyRegistrationNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value =
        command.getMetadata().get(DomainConstants.METADATA_KEY_COMPANY_REGISTRATION_NUMBER);
    if (value != null) {
      return value.toString();
    }

    value =
        command.getMetadata().get(DomainConstants.METADATA_KEY_COMPANY_REGISTRATION_NUMBER_ALT);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("enterpriseNumber");
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get("enterprise_number");
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the clearance type from the command metadata.
   *
   * @param command the verification command
   * @return the clearance type, defaulting to GOOD_STANDING if not specified
   */
  static TaxClearanceType extractClearanceType(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return TaxClearanceType.GOOD_STANDING;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_KEY_CLEARANCE_TYPE);
    if (value != null) {
      return TaxClearanceType.fromDescription(value.toString());
    }

    value = command.getMetadata().get(DomainConstants.METADATA_KEY_CLEARANCE_TYPE_ALT);
    if (value != null) {
      return TaxClearanceType.fromDescription(value.toString());
    }

    value = command.getMetadata().get("certificateType");
    if (value != null) {
      return TaxClearanceType.fromDescription(value.toString());
    }

    value = command.getMetadata().get("certificate_type");
    if (value != null) {
      return TaxClearanceType.fromDescription(value.toString());
    }

    return TaxClearanceType.GOOD_STANDING;
  }
}
