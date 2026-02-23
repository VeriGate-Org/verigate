/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.services;

import verigate.adapter.sars.domain.models.TaxComplianceRequest;
import verigate.adapter.sars.domain.models.TaxComplianceResponse;

/**
 * Service interface for SARS tax compliance verification operations.
 * Provides methods to verify tax compliance status and Tax Clearance Certificates
 * via the SARS eFiling API.
 */
public interface SarsTaxComplianceService {

  /**
   * Verifies tax compliance status for the given request.
   *
   * @param request the tax compliance request containing identification and clearance details
   * @return the tax compliance response with status and certificate details
   * @throws domain.exceptions.TransientException if a temporary error occurs that may be retried
   * @throws domain.exceptions.PermanentException if a permanent error occurs that should not
   *     be retried
   */
  TaxComplianceResponse verifyTaxCompliance(TaxComplianceRequest request);
}
