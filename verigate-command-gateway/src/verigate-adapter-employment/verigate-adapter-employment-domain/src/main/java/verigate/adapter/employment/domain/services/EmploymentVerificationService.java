/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.domain.services;

import verigate.adapter.employment.domain.models.EmploymentVerificationRequest;
import verigate.adapter.employment.domain.models.EmploymentVerificationResponse;

/**
 * Service interface for employment verification operations.
 * Provides methods to verify employment details via an external employment verification API.
 */
public interface EmploymentVerificationService {

  /**
   * Verifies employment details for the given request.
   *
   * @param request the employment verification request containing ID number and employer details
   * @return the employment verification response with status and employment details
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  EmploymentVerificationResponse verifyEmployment(EmploymentVerificationRequest request);
}
