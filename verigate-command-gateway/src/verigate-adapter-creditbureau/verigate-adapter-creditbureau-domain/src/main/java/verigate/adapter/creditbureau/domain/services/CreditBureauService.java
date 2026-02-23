/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.domain.services;

import verigate.adapter.creditbureau.domain.models.CreditCheckRequest;
import verigate.adapter.creditbureau.domain.models.CreditCheckResponse;

/**
 * Service interface for credit bureau operations.
 * Provides methods to perform credit checks against a credit bureau provider.
 */
public interface CreditBureauService {

  /**
   * Performs a credit check for the given request.
   *
   * @param request the credit check request containing party identification details
   * @return the credit check response with assessment results
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  CreditCheckResponse performCreditCheck(CreditCheckRequest request);
}
