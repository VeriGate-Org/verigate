/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.document.domain.models.DocumentVerificationRequest;
import verigate.adapter.document.domain.models.DocumentVerificationResponse;
import verigate.adapter.document.domain.services.DocumentVerificationService;
import verigate.adapter.document.infrastructure.http.DocumentApiAdapter;
import verigate.adapter.document.infrastructure.http.dto.DocumentVerificationRequestDto;
import verigate.adapter.document.infrastructure.http.dto.DocumentVerificationResponseDto;
import verigate.adapter.document.infrastructure.mappers.DocumentDtoMapper;

/**
 * Default implementation of the {@link DocumentVerificationService} using the infrastructure HTTP
 * adapter.
 */
public class DefaultDocumentVerificationService implements DocumentVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultDocumentVerificationService.class);

  private final DocumentApiAdapter apiAdapter;
  private final DocumentDtoMapper dtoMapper;

  public DefaultDocumentVerificationService(
      DocumentApiAdapter apiAdapter, DocumentDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public DocumentVerificationResponse verifyDocument(DocumentVerificationRequest request) {
    logger.info(
        "Verifying document of type: {} for reference: [MASKED]",
        request.documentType());

    try {
      DocumentVerificationRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      DocumentVerificationResponseDto responseDto = apiAdapter.verifyDocument(requestDto);

      DocumentVerificationResponse response = dtoMapper.mapToResponse(responseDto);

      logger.info(
          "Document verification completed with status: {}, confidence: {}",
          response.status(),
          response.confidenceScore());
      return response;

    } catch (PermanentException e) {
      logger.error(
          "Permanent error during document verification: {}",
          e.getMessage());
      return DocumentVerificationResponse.error(
          "Document verification failed: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error during document verification: {}",
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error during document verification: {}",
          e.getMessage(),
          e);
      return DocumentVerificationResponse.error("Unexpected error: " + e.getMessage());
    }
  }
}
