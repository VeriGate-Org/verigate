/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.services;

import verigate.adapter.cipc.domain.models.CompanyProfileRequest;
import verigate.adapter.cipc.domain.models.CompanyProfileResponse;

/**
 * Service interface for CIPC company operations.
 * Provides methods to retrieve company information from the CIPC public data API.
 */
public interface CipcCompanyService {

  /**
   * Retrieves the complete company profile including directors, secretaries, auditors, and capital.
   *
   * @param request the company profile request containing the enterprise number
   * @return the company profile response with complete company details
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  CompanyProfileResponse getCompanyProfile(CompanyProfileRequest request);

  /**
   * Retrieves basic company information without related entities.
   *
   * @param request the company profile request containing the enterprise number
   * @return the company profile response with basic company details
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  CompanyProfileResponse getCompanyInformation(CompanyProfileRequest request);
}
