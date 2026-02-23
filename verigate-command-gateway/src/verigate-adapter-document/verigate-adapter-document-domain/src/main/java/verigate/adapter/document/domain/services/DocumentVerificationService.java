/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.services;

import verigate.adapter.document.domain.models.DocumentVerificationRequest;
import verigate.adapter.document.domain.models.DocumentVerificationResponse;

/**
 * Service interface for document verification operations.
 * Provides methods to verify documents against subject information.
 */
public interface DocumentVerificationService {

  /**
   * Verifies a document by comparing its content against the subject information provided.
   *
   * @param request the document verification request containing document and subject details
   * @return the document verification response with status, extracted data, and confidence score
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  DocumentVerificationResponse verifyDocument(DocumentVerificationRequest request);
}
