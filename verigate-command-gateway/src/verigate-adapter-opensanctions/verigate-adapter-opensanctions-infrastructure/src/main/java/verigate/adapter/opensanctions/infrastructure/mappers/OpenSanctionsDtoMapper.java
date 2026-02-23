/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.mappers;

import java.util.Map;
import java.util.stream.Collectors;
import verigate.adapter.opensanctions.domain.models.EntityExample;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.models.EntityMatches;
import verigate.adapter.opensanctions.domain.models.FeatureDoc;
import verigate.adapter.opensanctions.domain.models.ScoredEntity;
import verigate.adapter.opensanctions.domain.models.TotalSpec;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityExampleDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityMatchRequestDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityMatchResponseDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityMatchesDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.FeatureDocDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.ScoredEntityDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.TotalSpecDto;

/**
 * Maps between domain models and DTOs for OpenSanctions API.
 */
public class OpenSanctionsDtoMapper {

  /**
   * Maps domain EntityMatchRequest to DTO.
   */
  public static EntityMatchRequestDto mapToDto(EntityMatchRequest request) {
    EntityMatchRequestDto dto = new EntityMatchRequestDto();

    if (request.getQueries() != null) {
      Map<String, EntityExampleDto> queryDtos =
          request.getQueries().entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> mapToDto(entry.getValue())));
      dto.setQueries(queryDtos);
    }

    return dto;
  }

  /**
   * Maps domain EntityExample to DTO.
   */
  public static EntityExampleDto mapToDto(EntityExample example) {
    EntityExampleDto dto = new EntityExampleDto();
    dto.setId(example.getId());
    dto.setSchema(example.getSchema());
    dto.setProperties(example.getProperties());
    return dto;
  }

  /**
   * Maps DTO EntityMatchResponseDto to domain model.
   */
  public static EntityMatchResponse mapToDomain(EntityMatchResponseDto dto) {
    Map<String, EntityMatches> responses = null;
    if (dto.getResponses() != null) {
      responses =
          dto.getResponses().entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> mapToDomain(entry.getValue())));
    }

    Map<String, FeatureDoc> matcher = null;
    if (dto.getMatcher() != null) {
      matcher =
          dto.getMatcher().entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> mapToDomain(entry.getValue())));
    }

    return new EntityMatchResponse(responses, matcher, dto.getLimit());
  }

  /**
   * Maps DTO EntityMatchesDto to domain model.
   */
  public static EntityMatches mapToDomain(EntityMatchesDto dto) {
    var results =
        dto.getResults() != null
            ? dto.getResults().stream()
                .map(OpenSanctionsDtoMapper::mapToDomain)
                .collect(Collectors.toList())
            : null;

    var total = dto.getTotal() != null ? mapToDomain(dto.getTotal()) : null;
    var query = dto.getQuery() != null ? mapToDomain(dto.getQuery()) : null;

    return new EntityMatches(dto.getStatus(), results, total, query);
  }

  /**
   * Maps DTO ScoredEntityDto to domain model.
   */
  public static ScoredEntity mapToDomain(ScoredEntityDto dto) {
    return new ScoredEntity.Builder()
        .id(dto.getId())
        .caption(dto.getCaption())
        .schema(dto.getSchema())
        .properties(dto.getProperties())
        .datasets(dto.getDatasets())
        .referents(dto.getReferents())
        .target(dto.getTarget())
        .firstSeen(dto.getFirstSeen())
        .lastSeen(dto.getLastSeen())
        .lastChange(dto.getLastChange())
        .score(dto.getScore())
        .features(dto.getFeatures())
        .match(dto.getMatch())
        .token(dto.getToken())
        .build();
  }

  /**
   * Maps DTO EntityExampleDto to domain model.
   */
  public static EntityExample mapToDomain(EntityExampleDto dto) {
    return new EntityExample.Builder()
        .id(dto.getId())
        .schema(dto.getSchema())
        .properties(dto.getProperties())
        .build();
  }

  /**
   * Maps DTO TotalSpecDto to domain model.
   */
  public static TotalSpec mapToDomain(TotalSpecDto dto) {
    return new TotalSpec(dto.getValue(), dto.getRelation());
  }

  /**
   * Maps DTO FeatureDocDto to domain model.
   */
  public static FeatureDoc mapToDomain(FeatureDocDto dto) {
    return new FeatureDoc(dto.getDescription(), dto.getCoefficient(), dto.getUrl());
  }
}
