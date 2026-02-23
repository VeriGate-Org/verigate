/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import verigate.adapter.deedsweb.domain.models.EntityMatchRequest;
import verigate.adapter.deedsweb.domain.models.EntityMatchResponse;

/**
 * Service interface for DeedsWeb entity matching operations.
 * Provides property deed verification capabilities using the DeedsWeb API.
 */
public interface DeedsWebMatchingService {

  /**
   * Performs entity matching against the DeedsWeb database.
   *
   * @param request the entity matching request with query details
   * @return the matching response with scored entities
   * @throws TransientException for temporary failures that can be retried
   * @throws PermanentException for permanent failures
   */
  EntityMatchResponse matchEntities(EntityMatchRequest request)
      throws TransientException, PermanentException;

  /**
   * Performs a simple text-based search against DeedsWeb.
   *
   * @param dataset the dataset to search (e.g., "deeds", "default")
   * @param query the search query text
   * @param limit maximum number of results to return
   * @return the search response with matching entities
   * @throws TransientException for temporary failures that can be retried
   * @throws PermanentException for permanent failures
   */
  EntityMatchResponse searchEntities(String dataset, String query, Integer limit)
      throws TransientException, PermanentException;

  /**
   * Checks if the DeedsWeb service is available and responsive.
   *
   * @return true if the service is healthy, false otherwise
   * @throws TransientException for temporary connectivity issues
   */
  boolean isServiceHealthy() throws TransientException;
}
