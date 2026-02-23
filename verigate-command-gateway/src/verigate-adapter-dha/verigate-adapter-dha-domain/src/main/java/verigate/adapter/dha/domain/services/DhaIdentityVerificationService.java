/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.services;

import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;

/**
 * Service interface for DHA identity verification operations.
 * Provides methods to verify individual identity against DHA records.
 */
public interface DhaIdentityVerificationService {

  /**
   * Verifies an individual's identity against the DHA population register.
   *
   * @param request the identity verification request containing the individual's details
   * @return the identity verification response with verification outcome and details
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  IdentityVerificationResponse verifyIdentity(IdentityVerificationRequest request);
}
