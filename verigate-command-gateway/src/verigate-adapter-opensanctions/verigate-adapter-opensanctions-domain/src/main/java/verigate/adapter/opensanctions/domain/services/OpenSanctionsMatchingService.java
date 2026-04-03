/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.List;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.models.ScoredEntity;

/**
 * Service interface for OpenSanctions entity matching operations.
 * Provides sanctions screening capabilities using the OpenSanctions API.
 */
public interface OpenSanctionsMatchingService {

  /**
   * Performs entity matching against the OpenSanctions database.
   *
   * @param request the entity matching request with query details
   * @return the matching response with scored entities
   * @throws TransientException for temporary failures that can be retried
   * @throws PermanentException for permanent failures
   */
  EntityMatchResponse matchEntities(EntityMatchRequest request)
      throws TransientException, PermanentException;

  /**
   * Performs a simple text-based search against OpenSanctions.
   *
   * @param dataset the dataset to search (e.g., "sanctions", "default")
   * @param query the search query text
   * @param limit maximum number of results to return
   * @return the search response with matching entities
   * @throws TransientException for temporary failures that can be retried
   * @throws PermanentException for permanent failures
   */
  EntityMatchResponse searchEntities(String dataset, String query, Integer limit)
      throws TransientException, PermanentException;

  /**
   * Retrieves a specific entity by its ID.
   *
   * @param entityId the entity identifier
   * @return the scored entity details
   * @throws TransientException for temporary failures
   * @throws PermanentException for permanent failures
   */
  ScoredEntity getEntity(String entityId) throws TransientException, PermanentException;

  /**
   * Retrieves adjacent (related) entities for a given entity.
   *
   * @param entityId the entity identifier
   * @return list of related scored entities
   * @throws TransientException for temporary failures
   * @throws PermanentException for permanent failures
   */
  List<ScoredEntity> getAdjacentEntities(String entityId)
      throws TransientException, PermanentException;

  /**
   * Checks if the OpenSanctions service is available and responsive.
   *
   * @return true if the service is healthy, false otherwise
   * @throws TransientException for temporary connectivity issues
   */
  boolean isServiceHealthy() throws TransientException;
}
