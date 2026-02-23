/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.constants.DomainConstants;
import verigate.adapter.dha.infrastructure.http.dto.DhaIdentityRequestDto;
import verigate.adapter.dha.infrastructure.http.dto.DhaIdentityResponseDto;

/**
 * API adapter for calling the DHA identity verification endpoints.
 */
public class DhaIdentityApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(DhaIdentityApiAdapter.class);

  private final DhaHttpAdapter httpAdapter;

  public DhaIdentityApiAdapter(DhaHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Verifies the identity of an individual against the DHA population register.
   */
  public DhaIdentityResponseDto verifyIdentity(DhaIdentityRequestDto request)
      throws TransientException, PermanentException {
    validateIdNumber(request.idNumber());

    logger.debug(
        "Requesting identity verification for ID number ending: ...{}",
        maskIdNumber(request.idNumber()));

    try {
      DhaIdentityResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_VERIFY_IDENTITY,
              request,
              DhaIdentityResponseDto.class);

      logger.debug(
          "Successfully received identity verification response for ID number ending: ...{}",
          maskIdNumber(request.idNumber()));
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to verify identity for ID number ending ...{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;
    }
  }

  private void validateIdNumber(String idNumber) {
    if (idNumber == null || idNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("ID number cannot be null or empty");
    }

    String trimmed = idNumber.trim();
    if (!trimmed.matches(DomainConstants.SA_ID_NUMBER_PATTERN)) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid South African ID number format: %s. Expected 13 digits (e.g., %s)",
              maskIdNumber(trimmed),
              DomainConstants.SA_ID_NUMBER_EXAMPLE));
    }
  }

  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 4) {
      return "***";
    }
    return "***" + idNumber.substring(idNumber.length() - 4);
  }
}
