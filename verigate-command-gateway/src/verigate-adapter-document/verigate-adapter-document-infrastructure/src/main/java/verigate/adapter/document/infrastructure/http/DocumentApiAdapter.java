/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.document.domain.constants.DomainConstants;
import verigate.adapter.document.infrastructure.http.dto.DocumentVerificationRequestDto;
import verigate.adapter.document.infrastructure.http.dto.DocumentVerificationResponseDto;

/**
 * API adapter for calling the Document Verification endpoints.
 */
public class DocumentApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(DocumentApiAdapter.class);

  private final DocumentHttpAdapter httpAdapter;

  public DocumentApiAdapter(DocumentHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Submits a document for verification against the supplied subject information.
   */
  public DocumentVerificationResponseDto verifyDocument(
      DocumentVerificationRequestDto request)
      throws TransientException, PermanentException {

    validateRequest(request);

    logger.debug(
        "Requesting document verification for document type: {}",
        request.documentType());

    try {
      DocumentVerificationResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_VERIFY_DOCUMENT,
              request,
              DocumentVerificationResponseDto.class);

      logger.debug(
          "Successfully received document verification response with status: {}",
          response.status());
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to verify document: {}",
          e.getMessage());
      throw e;
    }
  }

  private void validateRequest(DocumentVerificationRequestDto request) {
    if (request == null) {
      throw new IllegalArgumentException("Document verification request cannot be null");
    }

    if (request.documentReference() == null || request.documentReference().trim().isEmpty()) {
      throw new IllegalArgumentException("Document reference cannot be null or empty");
    }
  }
}
