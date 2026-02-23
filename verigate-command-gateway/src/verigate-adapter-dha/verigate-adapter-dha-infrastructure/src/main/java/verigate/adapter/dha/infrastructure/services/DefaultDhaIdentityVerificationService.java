/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.services.DhaIdentityVerificationService;
import verigate.adapter.dha.infrastructure.http.DhaIdentityApiAdapter;
import verigate.adapter.dha.infrastructure.http.dto.DhaIdentityRequestDto;
import verigate.adapter.dha.infrastructure.http.dto.DhaIdentityResponseDto;
import verigate.adapter.dha.infrastructure.mappers.DhaDtoMapper;

/**
 * Default implementation of the {@link DhaIdentityVerificationService} using the infrastructure
 * HTTP adapter.
 */
public class DefaultDhaIdentityVerificationService implements DhaIdentityVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultDhaIdentityVerificationService.class);

  private final DhaIdentityApiAdapter apiAdapter;
  private final DhaDtoMapper dtoMapper;

  public DefaultDhaIdentityVerificationService(
      DhaIdentityApiAdapter apiAdapter, DhaDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public IdentityVerificationResponse verifyIdentity(IdentityVerificationRequest request) {
    String maskedId = maskIdNumber(request.idNumber());
    logger.info("Verifying identity for ID number ending: ...{}", maskedId);

    try {
      DhaIdentityRequestDto requestDto = dtoMapper.mapToRequestDto(request);
      DhaIdentityResponseDto responseDto = apiAdapter.verifyIdentity(requestDto);

      if (responseDto == null) {
        logger.info("No verification result returned for ID number ending: ...{}", maskedId);
        return IdentityVerificationResponse.notFound();
      }

      IdentityVerificationResponse verificationResponse =
          dtoMapper.mapToIdentityVerificationResponse(responseDto);

      logger.info(
          "Successfully verified identity for ID ending ...{} - status: {}",
          maskedId,
          verificationResponse.status());
      return verificationResponse;

    } catch (PermanentException e) {
      if (e.getMessage() != null && e.getMessage().contains("not found")) {
        logger.info("Identity not found for ID number ending: ...{}", maskedId);
        return IdentityVerificationResponse.notFound();
      }

      logger.error(
          "Permanent error verifying identity for ID ending ...{}: {}",
          maskedId,
          e.getMessage());
      return IdentityVerificationResponse.error(
          verigate.adapter.dha.domain.models.IdVerificationStatus.NOT_FOUND,
          "Failed to verify identity: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error verifying identity for ID ending ...{}: {}",
          maskedId,
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error verifying identity for ID ending ...{}: {}",
          maskedId,
          e.getMessage(),
          e);
      return IdentityVerificationResponse.error(
          verigate.adapter.dha.domain.models.IdVerificationStatus.NOT_FOUND,
          "Unexpected error: " + e.getMessage());
    }
  }

  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 4) {
      return "***";
    }
    return idNumber.substring(idNumber.length() - 4);
  }
}
