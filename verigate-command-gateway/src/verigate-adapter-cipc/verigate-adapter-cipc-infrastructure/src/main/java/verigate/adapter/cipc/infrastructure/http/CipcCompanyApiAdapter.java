/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.cipc.domain.constants.DomainConstants;
import verigate.adapter.cipc.infrastructure.http.dto.CipcCompanyProfileResponseDto;
import verigate.adapter.cipc.infrastructure.http.dto.CipcEnterpriseNumberRequestDto;

/**
 * API adapter for calling the CIPC company endpoints.
 */
public class CipcCompanyApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(CipcCompanyApiAdapter.class);

  private final CipcHttpAdapter httpAdapter;

  public CipcCompanyApiAdapter(CipcHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Retrieves the complete company profile for the supplied enterprise number.
   */
  public CipcCompanyProfileResponseDto getCompanyProfile(String enterpriseNumber)
      throws TransientException, PermanentException {
    return fetchCompanyData(
        enterpriseNumber,
        DomainConstants.ENDPOINT_COMPANY_PROFILE,
        "company profile");
  }

  /**
   * Retrieves basic company information (without related entities) for the supplied enterprise
   * number.
   */
  public CipcCompanyProfileResponseDto getCompanyInformation(String enterpriseNumber)
      throws TransientException, PermanentException {
    return fetchCompanyData(
        enterpriseNumber,
        DomainConstants.ENDPOINT_COMPANY_INFORMATION,
        "basic company information");
  }

  private CipcCompanyProfileResponseDto fetchCompanyData(
      String enterpriseNumber, String endpoint, String description)
      throws TransientException, PermanentException {
    String trimmedEnterpriseNumber = validateEnterpriseNumber(enterpriseNumber);

    logger.debug(
        "Requesting {} for enterprise number {}",
        description,
        trimmedEnterpriseNumber);

    CipcEnterpriseNumberRequestDto request =
        new CipcEnterpriseNumberRequestDto(trimmedEnterpriseNumber);

    try {
      CipcCompanyProfileResponseDto response =
          httpAdapter.post(endpoint, request, CipcCompanyProfileResponseDto.class);

      logger.debug(
          "Successfully retrieved {} for enterprise number {}",
          description,
          trimmedEnterpriseNumber);
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to retrieve {} for enterprise number {}: {}",
          description,
          trimmedEnterpriseNumber,
          e.getMessage());
      throw e;
    }
  }

  private String validateEnterpriseNumber(String enterpriseNumber) {
    if (enterpriseNumber == null || enterpriseNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Enterprise number cannot be null or empty");
    }

    String trimmed = enterpriseNumber.trim();
    if (!trimmed.matches(DomainConstants.ENTERPRISE_NUMBER_PATTERN)) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid enterprise number format: %s. Expected format: %s",
              trimmed,
              DomainConstants.ENTERPRISE_NUMBER_EXAMPLE));
    }
    return trimmed;
  }
}
