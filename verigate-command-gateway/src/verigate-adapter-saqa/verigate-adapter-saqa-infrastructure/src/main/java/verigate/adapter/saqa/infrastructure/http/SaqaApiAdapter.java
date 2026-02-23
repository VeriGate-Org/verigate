/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.saqa.domain.constants.DomainConstants;
import verigate.adapter.saqa.infrastructure.http.dto.QualificationVerificationRequestDto;
import verigate.adapter.saqa.infrastructure.http.dto.QualificationVerificationResponseDto;

/**
 * API adapter for calling the SAQA qualification verification endpoints.
 */
public class SaqaApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(SaqaApiAdapter.class);

  private final SaqaHttpAdapter httpAdapter;

  public SaqaApiAdapter(SaqaHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Verifies a qualification against the SAQA registry.
   *
   * @param request the qualification verification request DTO
   * @return the qualification verification response DTO
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  public QualificationVerificationResponseDto verifyQualification(
      QualificationVerificationRequestDto request)
      throws TransientException, PermanentException {

    logger.debug(
        "Requesting qualification verification for ID: {}",
        maskIdNumber(request.idNumber()));

    try {
      QualificationVerificationResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_QUALIFICATIONS_VERIFY,
              request,
              QualificationVerificationResponseDto.class);

      logger.debug(
          "Successfully received qualification verification response for ID: {}",
          maskIdNumber(request.idNumber()));
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to verify qualification for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;
    }
  }

  /**
   * Masks an ID number for safe logging.
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 6) {
      return "***";
    }
    return idNumber.substring(0, 3) + "***" + idNumber.substring(idNumber.length() - 3);
  }
}
