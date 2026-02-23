/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.employment.domain.constants.DomainConstants;
import verigate.adapter.employment.infrastructure.http.dto.EmploymentVerificationRequestDto;
import verigate.adapter.employment.infrastructure.http.dto.EmploymentVerificationResponseDto;

/**
 * API adapter for calling the Employment Verification endpoints.
 */
public class EmploymentApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(EmploymentApiAdapter.class);

  private final EmploymentHttpAdapter httpAdapter;

  public EmploymentApiAdapter(EmploymentHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Verifies employment details for the supplied request parameters.
   */
  public EmploymentVerificationResponseDto verifyEmployment(
      EmploymentVerificationRequestDto request)
      throws TransientException, PermanentException {

    validateRequest(request);

    logger.debug(
        "Requesting employment verification for ID: ****{}",
        maskIdNumber(request.idNumber()));

    try {
      EmploymentVerificationResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_VERIFY_EMPLOYMENT,
              request,
              EmploymentVerificationResponseDto.class);

      logger.debug(
          "Successfully retrieved employment verification for ID: ****{}",
          maskIdNumber(request.idNumber()));
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to verify employment for ID ****{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;
    }
  }

  private void validateRequest(EmploymentVerificationRequestDto request) {
    if (request == null) {
      throw new IllegalArgumentException("Employment verification request cannot be null");
    }

    if (request.idNumber() == null || request.idNumber().trim().isEmpty()) {
      throw new IllegalArgumentException("ID number cannot be null or empty");
    }
  }

  /**
   * Masks an ID number for safe logging, showing only the last 4 digits.
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() <= 4) {
      return "****";
    }
    return idNumber.substring(idNumber.length() - 4);
  }
}
