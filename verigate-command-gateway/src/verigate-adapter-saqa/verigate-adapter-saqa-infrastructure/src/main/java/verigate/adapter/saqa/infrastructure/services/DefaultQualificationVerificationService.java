/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.saqa.domain.models.QualificationVerificationRequest;
import verigate.adapter.saqa.domain.models.QualificationVerificationResponse;
import verigate.adapter.saqa.domain.services.QualificationVerificationService;
import verigate.adapter.saqa.infrastructure.http.SaqaApiAdapter;
import verigate.adapter.saqa.infrastructure.http.dto.QualificationVerificationRequestDto;
import verigate.adapter.saqa.infrastructure.http.dto.QualificationVerificationResponseDto;
import verigate.adapter.saqa.infrastructure.mappers.SaqaDtoMapper;

/**
 * Default implementation of the {@link QualificationVerificationService} using the infrastructure
 * HTTP adapter.
 */
public class DefaultQualificationVerificationService
    implements QualificationVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultQualificationVerificationService.class);

  private final SaqaApiAdapter apiAdapter;
  private final SaqaDtoMapper dtoMapper;

  public DefaultQualificationVerificationService(
      SaqaApiAdapter apiAdapter, SaqaDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public QualificationVerificationResponse verifyQualification(
      QualificationVerificationRequest request) {

    logger.info(
        "Verifying qualification for ID: {}",
        maskIdNumber(request.idNumber()));

    try {
      QualificationVerificationRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      QualificationVerificationResponseDto responseDto =
          apiAdapter.verifyQualification(requestDto);

      QualificationVerificationResponse response = dtoMapper.mapToResponse(responseDto);

      logger.info(
          "Successfully verified qualification for ID: {} - status: {}",
          maskIdNumber(request.idNumber()),
          response.status());
      return response;

    } catch (PermanentException e) {
      if (e.getMessage() != null && e.getMessage().contains("not found")) {
        logger.info(
            "Qualification not found for ID: {}",
            maskIdNumber(request.idNumber()));
        return QualificationVerificationResponse.notFound(
            "No qualification records found for the provided identity");
      }

      logger.error(
          "Permanent error verifying qualification for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      return QualificationVerificationResponse.error(
          "Failed to verify qualification: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error verifying qualification for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error verifying qualification for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage(),
          e);
      return QualificationVerificationResponse.error(
          "Unexpected error: " + e.getMessage());
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
