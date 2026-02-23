/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.creditbureau.domain.constants.DomainConstants;
import verigate.adapter.creditbureau.infrastructure.http.dto.CreditCheckRequestDto;
import verigate.adapter.creditbureau.infrastructure.http.dto.CreditCheckResponseDto;

/**
 * API adapter for calling the Credit Bureau credit check endpoints.
 */
public class CreditBureauApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(CreditBureauApiAdapter.class);

  private final CreditBureauHttpAdapter httpAdapter;

  public CreditBureauApiAdapter(CreditBureauHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Performs a credit check for the supplied request details.
   */
  public CreditCheckResponseDto performCreditCheck(CreditCheckRequestDto request)
      throws TransientException, PermanentException {

    validateRequest(request);

    logger.debug(
        "Requesting credit check for ID: {}",
        maskIdNumber(request.idNumber()));

    try {
      CreditCheckResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_CREDIT_CHECK,
              request,
              CreditCheckResponseDto.class);

      logger.debug(
          "Successfully completed credit check for ID: {}",
          maskIdNumber(request.idNumber()));
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to perform credit check for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;
    }
  }

  private void validateRequest(CreditCheckRequestDto request) {
    if (request == null) {
      throw new IllegalArgumentException("Credit check request cannot be null");
    }

    if (request.idNumber() == null || request.idNumber().trim().isEmpty()) {
      throw new IllegalArgumentException("ID number cannot be null or empty");
    }
  }

  /**
   * Masks an ID number for safe logging.
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 6) {
      return "***";
    }
    return idNumber.substring(0, 3) + "***" + idNumber.substring(idNumber.length() - 2);
  }
}
