/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.domain.services;

import verigate.adapter.saqa.domain.models.QualificationVerificationRequest;
import verigate.adapter.saqa.domain.models.QualificationVerificationResponse;

/**
 * Service interface for SAQA qualification verification operations.
 * Provides methods to verify qualifications against the SAQA registry.
 */
public interface QualificationVerificationService {

  /**
   * Verifies a qualification against the SAQA registry.
   *
   * @param request the qualification verification request containing the details to verify
   * @return the qualification verification response with full verification details
   * @throws domain.exceptions.TransientException if a temporary error occurs that may be retried
   * @throws domain.exceptions.PermanentException if a permanent error occurs that should not be
   *     retried
   */
  QualificationVerificationResponse verifyQualification(
      QualificationVerificationRequest request);
}
