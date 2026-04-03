/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.logging.Logger;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.infrastructure.config.OpenSanctionsApiConfiguration;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityMatchRequestDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityMatchResponseDto;
import verigate.adapter.opensanctions.infrastructure.mappers.OpenSanctionsDtoMapper;

/**
 * API adapter for OpenSanctions matching endpoints.
 */
public class OpenSanctionsApiAdapter extends OpenSanctionsHttpAdapter {

  private static final Logger LOGGER = Logger.getLogger(OpenSanctionsApiAdapter.class.getName());

  public OpenSanctionsApiAdapter(OpenSanctionsApiConfiguration config) {
    super(config);
  }

  /**
   * Performs entity matching against the OpenSanctions API.
   *
   * @param request the entity match request
   * @return the entity match response
   * @throws TransientException for temporary failures
   * @throws PermanentException for permanent failures
   */
  public EntityMatchResponse matchEntities(EntityMatchRequest request)
      throws TransientException, PermanentException {

    LOGGER.info("Performing entity matching with dataset: " + request.getDataset());

    // Build the endpoint URL with query parameters
    String endpoint = buildMatchEndpoint(request);

    // Map domain request to DTO
    EntityMatchRequestDto requestDto = OpenSanctionsDtoMapper.mapToDto(request);

    // Make the API call
    EntityMatchResponseDto responseDto = post(endpoint, requestDto, EntityMatchResponseDto.class);

    // Map DTO response back to domain model
    EntityMatchResponse response = OpenSanctionsDtoMapper.mapToDomain(responseDto);

    LOGGER.info("Entity matching completed successfully");
    return response;
  }

  /**
   * Performs simple text search against the OpenSanctions API.
   *
   * @param dataset the dataset to search
   * @param query the search query text
   * @param limit maximum results to return
   * @return the search response
   * @throws TransientException for temporary failures
   * @throws PermanentException for permanent failures
   */
  public EntityMatchResponse searchEntities(String dataset, String query, Integer limit)
      throws TransientException, PermanentException {

    LOGGER.info(
        "Performing text search in dataset: " + dataset + " with query: " + maskQuery(query));

    // Build the search endpoint URL with query parameters
    String endpoint = buildSearchEndpoint(dataset, query, limit);

    // Make the API call (search uses GET)
    EntityMatchResponseDto responseDto = get(endpoint, EntityMatchResponseDto.class);

    // Map DTO response back to domain model
    EntityMatchResponse response = OpenSanctionsDtoMapper.mapToDomain(responseDto);

    LOGGER.info("Text search completed successfully");
    return response;
  }

  /**
   * Checks if the OpenSanctions service is healthy.
   *
   * @return true if service is available
   * @throws TransientException for connectivity issues
   */
  /**
   * Retrieves a specific entity by its ID.
   *
   * @param entityId the entity identifier
   * @return the scored entity
   * @throws TransientException for temporary failures
   * @throws PermanentException for permanent failures
   */
  public verigate.adapter.opensanctions.domain.models.ScoredEntity getEntity(String entityId)
      throws TransientException, PermanentException {

    LOGGER.info("Retrieving entity: " + entityId);

    verigate.adapter.opensanctions.infrastructure.http.dto.ScoredEntityDto dto =
        get("/entities/" + entityId,
            verigate.adapter.opensanctions.infrastructure.http.dto.ScoredEntityDto.class);

    return OpenSanctionsDtoMapper.mapToDomain(dto);
  }

  /**
   * Retrieves adjacent (related) entities for a given entity.
   *
   * @param entityId the entity identifier
   * @return the match response containing related entities
   * @throws TransientException for temporary failures
   * @throws PermanentException for permanent failures
   */
  public EntityMatchResponse getAdjacentEntities(String entityId)
      throws TransientException, PermanentException {

    LOGGER.info("Retrieving adjacent entities for: " + entityId);

    EntityMatchResponseDto responseDto =
        get("/entities/" + entityId + "/adjacent", EntityMatchResponseDto.class);

    return OpenSanctionsDtoMapper.mapToDomain(responseDto);
  }

  /**
   * Checks if the OpenSanctions service is healthy.
   *
   * @return true if service is available
   * @throws TransientException for connectivity issues
   */
  public boolean checkServiceHealth() throws TransientException {
    try {
      LOGGER.fine("Checking OpenSanctions service health");

      // Use the healthz endpoint
      get("/healthz", String.class);

      LOGGER.fine("Service health check passed");
      return true;

    } catch (PermanentException e) {
      // Permanent exceptions are still considered "healthy" from a connectivity standpoint
      LOGGER.fine("Service health check passed (permanent error expected)");
      return true;
    } catch (TransientException e) {
      LOGGER.warning("Service health check failed: " + e.getMessage());
      throw e;
    }
  }

  private String buildMatchEndpoint(EntityMatchRequest request) {
    StringBuilder endpoint = new StringBuilder("/match/");
    endpoint.append(request.getDataset());

    boolean hasParams = false;

    if (request.getLimit() != null) {
      endpoint.append(hasParams ? "&" : "?").append("limit=").append(request.getLimit());
      hasParams = true;
    }

    if (request.getThreshold() != null) {
      endpoint.append(hasParams ? "&" : "?").append("threshold=").append(request.getThreshold());
      hasParams = true;
    }

    if (request.getCutoff() != null) {
      endpoint.append(hasParams ? "&" : "?").append("cutoff=").append(request.getCutoff());
      hasParams = true;
    }

    if (request.getAlgorithm() != null) {
      endpoint.append(hasParams ? "&" : "?").append("algorithm=").append(request.getAlgorithm());
      hasParams = true;
    }

    if (request.getTopics() != null && !request.getTopics().isEmpty()) {
      for (String topic : request.getTopics()) {
        endpoint.append(hasParams ? "&" : "?").append("topics=").append(topic);
        hasParams = true;
      }
    }

    if (request.getIncludeDatasets() != null && !request.getIncludeDatasets().isEmpty()) {
      for (String dataset : request.getIncludeDatasets()) {
        endpoint.append(hasParams ? "&" : "?").append("include_dataset=").append(dataset);
        hasParams = true;
      }
    }

    if (request.getExcludeDatasets() != null && !request.getExcludeDatasets().isEmpty()) {
      for (String dataset : request.getExcludeDatasets()) {
        endpoint.append(hasParams ? "&" : "?").append("exclude_dataset=").append(dataset);
        hasParams = true;
      }
    }

    if (request.getExcludeSchemas() != null && !request.getExcludeSchemas().isEmpty()) {
      for (String schema : request.getExcludeSchemas()) {
        endpoint.append(hasParams ? "&" : "?").append("exclude_schema=").append(schema);
        hasParams = true;
      }
    }

    return endpoint.toString();
  }

  private String buildSearchEndpoint(String dataset, String query, Integer limit) {
    StringBuilder endpoint = new StringBuilder("/search/");
    endpoint.append(dataset);

    boolean hasParams = false;

    if (query != null && !query.trim().isEmpty()) {
      endpoint
          .append("?q=")
          .append(java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8));
      hasParams = true;
    }

    if (limit != null) {
      endpoint.append(hasParams ? "&" : "?").append("limit=").append(limit);
    }

    return endpoint.toString();
  }

  private String maskQuery(String query) {
    // Mask sensitive information in logs
    if (query == null || query.length() <= 3) {
      return "***";
    }
    return query.substring(0, 2) + "***";
  }
}
