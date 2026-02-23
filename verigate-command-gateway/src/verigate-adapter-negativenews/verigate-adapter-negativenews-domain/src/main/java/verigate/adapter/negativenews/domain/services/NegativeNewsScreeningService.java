/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.domain.services;

import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningRequest;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningResponse;

/**
 * Service interface for negative news screening operations.
 * Provides methods to screen subjects against negative news sources.
 */
public interface NegativeNewsScreeningService {

  /**
   * Screens a subject for negative news articles based on the provided request criteria.
   *
   * @param request the screening request containing subject details and search parameters
   * @return the screening response with results and analysis
   * @throws domain.exceptions.TransientException if a temporary error occurs that may be retried
   * @throws domain.exceptions.PermanentException if a permanent error occurs that should not be
   *     retried
   */
  NegativeNewsScreeningResponse screenForNegativeNews(NegativeNewsScreeningRequest request);
}
