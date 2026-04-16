/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * DTO for DeedsWeb entity match response.
 */
public class EntityMatchResponseDto {

  @JsonProperty("responses")
  private Map<String, EntityMatchesDto> responses;

  @JsonProperty("matcher")
  private Map<String, FeatureDocDto> matcher;

  @JsonProperty("limit")
  private Integer limit;

  // Getters and setters
  public Map<String, EntityMatchesDto> getResponses() {
    return responses;
  }

  public void setResponses(Map<String, EntityMatchesDto> responses) {
    this.responses = responses;
  }

  public Map<String, FeatureDocDto> getMatcher() {
    return matcher;
  }

  public void setMatcher(Map<String, FeatureDocDto> matcher) {
    this.matcher = matcher;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }
}
