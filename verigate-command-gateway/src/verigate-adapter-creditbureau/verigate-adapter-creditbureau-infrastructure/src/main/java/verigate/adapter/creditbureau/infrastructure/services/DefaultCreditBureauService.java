/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.creditbureau.domain.models.CreditCheckRequest;
import verigate.adapter.creditbureau.domain.models.CreditCheckResponse;
import verigate.adapter.creditbureau.domain.services.CreditBureauService;
import verigate.adapter.creditbureau.infrastructure.http.CreditBureauApiAdapter;
import verigate.adapter.creditbureau.infrastructure.http.dto.CreditCheckRequestDto;
import verigate.adapter.creditbureau.infrastructure.http.dto.CreditCheckResponseDto;
import verigate.adapter.creditbureau.infrastructure.mappers.CreditBureauDtoMapper;

/**
 * Default implementation of the {@link CreditBureauService} using the infrastructure HTTP adapter.
 */
public class DefaultCreditBureauService implements CreditBureauService {

  private static final Logger logger = LoggerFactory.getLogger(DefaultCreditBureauService.class);

  private final CreditBureauApiAdapter apiAdapter;
  private final CreditBureauDtoMapper dtoMapper;

  public DefaultCreditBureauService(
      CreditBureauApiAdapter apiAdapter, CreditBureauDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public CreditCheckResponse performCreditCheck(CreditCheckRequest request) {
    logger.info(
        "Performing credit check for ID: {}",
        maskIdNumber(request.idNumber()));

    try {
      CreditCheckRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      CreditCheckResponseDto responseDto = apiAdapter.performCreditCheck(requestDto);

      CreditCheckResponse response = dtoMapper.mapToResponse(responseDto);

      logger.info(
          "Credit check completed with status: {} for ID: {}",
          response.status(),
          maskIdNumber(request.idNumber()));
      return response;

    } catch (PermanentException e) {
      logger.error(
          "Permanent error during credit check for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      return CreditCheckResponse.error("Credit check failed: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error during credit check for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error during credit check for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage(),
          e);
      return CreditCheckResponse.error("Unexpected error: " + e.getMessage());
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
