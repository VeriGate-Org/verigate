/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.models.ScoredEntity;
import verigate.adapter.opensanctions.domain.services.OpenSanctionsMatchingService;
import verigate.adapter.opensanctions.infrastructure.http.OpenSanctionsApiAdapter;

/**
 * Default implementation of OpenSanctions matching service.
 */
public class DefaultOpenSanctionsMatchingService implements OpenSanctionsMatchingService {

  private static final Logger LOGGER =
      Logger.getLogger(DefaultOpenSanctionsMatchingService.class.getName());

  private final OpenSanctionsApiAdapter apiAdapter;

  public DefaultOpenSanctionsMatchingService(OpenSanctionsApiAdapter apiAdapter) {
    this.apiAdapter = apiAdapter;
  }

  @Override
  public EntityMatchResponse matchEntities(EntityMatchRequest request)
      throws TransientException, PermanentException {

    LOGGER.info("Starting entity matching request");

    try {
      validateRequest(request);

      EntityMatchResponse response = apiAdapter.matchEntities(request);

      LOGGER.info("Entity matching completed successfully");
      return response;

    } catch (TransientException e) {
      LOGGER.log(Level.WARNING, "Transient error during entity matching", e);
      throw e;
    } catch (PermanentException e) {
      LOGGER.log(Level.SEVERE, "Permanent error during entity matching", e);
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error during entity matching", e);
      throw new PermanentException("Unexpected error during entity matching", e);
    }
  }

  @Override
  public EntityMatchResponse searchEntities(String dataset, String query, Integer limit)
      throws TransientException, PermanentException {

    LOGGER.info("Starting text search in dataset: " + dataset);

    try {
      validateSearchParameters(dataset, query, limit);

      EntityMatchResponse response = apiAdapter.searchEntities(dataset, query, limit);

      LOGGER.info("Text search completed successfully");
      return response;

    } catch (TransientException e) {
      LOGGER.log(Level.WARNING, "Transient error during text search", e);
      throw e;
    } catch (PermanentException e) {
      LOGGER.log(Level.SEVERE, "Permanent error during text search", e);
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error during text search", e);
      throw new PermanentException("Unexpected error during text search", e);
    }
  }

  @Override
  public ScoredEntity getEntity(String entityId) throws TransientException, PermanentException {
    LOGGER.info("Retrieving entity: " + entityId);

    try {
      if (entityId == null || entityId.trim().isEmpty()) {
        throw new PermanentException("Entity ID cannot be null or empty");
      }
      return apiAdapter.getEntity(entityId);
    } catch (TransientException | PermanentException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error retrieving entity", e);
      throw new PermanentException("Unexpected error retrieving entity", e);
    }
  }

  @Override
  public List<ScoredEntity> getAdjacentEntities(String entityId)
      throws TransientException, PermanentException {
    LOGGER.info("Retrieving adjacent entities for: " + entityId);

    try {
      if (entityId == null || entityId.trim().isEmpty()) {
        throw new PermanentException("Entity ID cannot be null or empty");
      }
      EntityMatchResponse response = apiAdapter.getAdjacentEntities(entityId);
      if (response.getResponses() == null || response.getResponses().isEmpty()) {
        return Collections.emptyList();
      }
      return response.getResponses().values().stream()
          .filter(em -> em.getResults() != null)
          .flatMap(em -> em.getResults().stream())
          .collect(Collectors.toList());
    } catch (TransientException | PermanentException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error retrieving adjacent entities", e);
      throw new PermanentException("Unexpected error retrieving adjacent entities", e);
    }
  }

  @Override
  public boolean isServiceHealthy() throws TransientException {
    LOGGER.fine("Checking OpenSanctions service health");

    try {
      return apiAdapter.checkServiceHealth();
    } catch (TransientException e) {
      LOGGER.log(Level.WARNING, "Service health check failed", e);
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Unexpected error during health check", e);
      throw new TransientException("Health check failed", e);
    }
  }

  private void validateRequest(EntityMatchRequest request) throws PermanentException {
    if (request == null) {
      throw new PermanentException("Entity match request cannot be null");
    }

    if (request.getDataset() == null || request.getDataset().trim().isEmpty()) {
      throw new PermanentException("Dataset cannot be null or empty");
    }

    if (request.getQueries() == null || request.getQueries().isEmpty()) {
      throw new PermanentException("Queries cannot be null or empty");
    }

    // Validate each query
    for (var entry : request.getQueries().entrySet()) {
      if (entry.getKey() == null || entry.getKey().trim().isEmpty()) {
        throw new PermanentException("Query key cannot be null or empty");
      }

      if (entry.getValue() == null) {
        throw new PermanentException("Query entity example cannot be null");
      }

      if (entry.getValue().getSchema() == null || entry.getValue().getSchema().trim().isEmpty()) {
        throw new PermanentException("Entity schema cannot be null or empty");
      }

      if (entry.getValue().getProperties() == null || entry.getValue().getProperties().isEmpty()) {
        throw new PermanentException("Entity properties cannot be null or empty");
      }
    }

    // Validate optional parameters
    if (request.getLimit() != null && request.getLimit() <= 0) {
      throw new PermanentException("Limit must be positive");
    }

    if (request.getThreshold() != null
        && (request.getThreshold() < 0 || request.getThreshold() > 1)) {
      throw new PermanentException("Threshold must be between 0 and 1");
    }

    if (request.getCutoff() != null && (request.getCutoff() < 0 || request.getCutoff() > 1)) {
      throw new PermanentException("Cutoff must be between 0 and 1");
    }
  }

  private void validateSearchParameters(String dataset, String query, Integer limit)
      throws PermanentException {

    if (dataset == null || dataset.trim().isEmpty()) {
      throw new PermanentException("Dataset cannot be null or empty");
    }

    if (query == null || query.trim().isEmpty()) {
      throw new PermanentException("Search query cannot be null or empty");
    }

    if (limit != null && limit <= 0) {
      throw new PermanentException("Limit must be positive");
    }
  }
}
