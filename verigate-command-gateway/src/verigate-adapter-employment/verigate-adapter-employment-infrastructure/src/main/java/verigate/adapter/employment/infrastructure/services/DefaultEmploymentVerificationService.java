/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.employment.domain.models.EmploymentVerificationRequest;
import verigate.adapter.employment.domain.models.EmploymentVerificationResponse;
import verigate.adapter.employment.domain.services.EmploymentVerificationService;
import verigate.adapter.employment.infrastructure.http.EmploymentApiAdapter;
import verigate.adapter.employment.infrastructure.http.dto.EmploymentVerificationRequestDto;
import verigate.adapter.employment.infrastructure.http.dto.EmploymentVerificationResponseDto;
import verigate.adapter.employment.infrastructure.mappers.EmploymentDtoMapper;

/**
 * Default implementation of the {@link EmploymentVerificationService} using the infrastructure
 * HTTP adapter.
 */
public class DefaultEmploymentVerificationService implements EmploymentVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultEmploymentVerificationService.class);

  private final EmploymentApiAdapter apiAdapter;
  private final EmploymentDtoMapper dtoMapper;

  public DefaultEmploymentVerificationService(
      EmploymentApiAdapter apiAdapter, EmploymentDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public EmploymentVerificationResponse verifyEmployment(
      EmploymentVerificationRequest request) {

    logger.info(
        "Verifying employment for ID: ****{}",
        maskIdNumber(request.idNumber()));

    try {
      EmploymentVerificationRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      EmploymentVerificationResponseDto responseDto = apiAdapter.verifyEmployment(requestDto);

      EmploymentVerificationResponse response = dtoMapper.mapToResponse(responseDto);

      logger.info(
          "Successfully verified employment for ID: ****{} - status: {}",
          maskIdNumber(request.idNumber()),
          response.status());
      return response;

    } catch (PermanentException e) {
      if (e.getMessage() != null && e.getMessage().contains("not found")) {
        logger.info(
            "Employment record not found for ID: ****{}",
            maskIdNumber(request.idNumber()));
        return EmploymentVerificationResponse.notFound();
      }

      logger.error(
          "Permanent error verifying employment for ID ****{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      return EmploymentVerificationResponse.error(
          "Failed to verify employment: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error verifying employment for ID ****{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error verifying employment for ID ****{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage(),
          e);
      return EmploymentVerificationResponse.error("Unexpected error: " + e.getMessage());
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
