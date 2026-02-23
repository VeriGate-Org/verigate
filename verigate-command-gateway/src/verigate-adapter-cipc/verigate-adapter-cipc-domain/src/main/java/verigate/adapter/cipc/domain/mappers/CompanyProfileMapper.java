/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.mappers;

import verigate.adapter.cipc.domain.models.CompanyProfileRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to CIPC company profile requests.
 */
public interface CompanyProfileMapper {

  /**
   * Maps a VerifyPartyCommand to a CompanyProfileRequest.
   *
   * @param command the verification command
   * @return the mapped company profile request
   */
  CompanyProfileRequest mapToCompanyProfileRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   */
  static CompanyProfileRequest mapToCompanyProfileRequestDefault(VerifyPartyCommand command) {

    // Extract enterprise number from command metadata
    String enterpriseNumber = extractEnterpriseNumber(command);

    if (enterpriseNumber == null || enterpriseNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Enterprise number is required for CIPC verification");
    }

    return CompanyProfileRequest.builder().enterpriseNumber(enterpriseNumber.trim()).build();
  }

  /**
   * Extracts the enterprise number from the command metadata.
   */
  static String extractEnterpriseNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    // Try various possible field names for enterprise number
    Object enterpriseNumberObj = command.getMetadata().get("enterpriseNumber");
    if (enterpriseNumberObj != null) {
      return enterpriseNumberObj.toString();
    }

    enterpriseNumberObj = command.getMetadata().get("enterprise_number");
    if (enterpriseNumberObj != null) {
      return enterpriseNumberObj.toString();
    }

    enterpriseNumberObj = command.getMetadata().get("companyNumber");
    if (enterpriseNumberObj != null) {
      return enterpriseNumberObj.toString();
    }

    enterpriseNumberObj = command.getMetadata().get("company_number");
    if (enterpriseNumberObj != null) {
      return enterpriseNumberObj.toString();
    }

    enterpriseNumberObj = command.getMetadata().get("registrationNumber");
    if (enterpriseNumberObj != null) {
      return enterpriseNumberObj.toString();
    }

    enterpriseNumberObj = command.getMetadata().get("registration_number");
    return enterpriseNumberObj != null ? enterpriseNumberObj.toString() : null;
  }

  /**
   * Validates enterprise number format.
   */
  static boolean isValidEnterpriseNumber(String enterpriseNumber) {
    if (enterpriseNumber == null || enterpriseNumber.trim().isEmpty()) {
      return false;
    }

    // CIPC enterprise numbers follow format: YYYY/NNNNNN/NN
    return enterpriseNumber.matches("\\d{4}/\\d{6}/\\d{2}");
  }
}
