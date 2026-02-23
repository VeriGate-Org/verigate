/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * DTO for OpenSanctions entity match request.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityMatchRequestDto {

  @JsonProperty("queries")
  private Map<String, EntityExampleDto> queries;

  @JsonProperty("weights")
  private Map<String, Double> weights;

  // Getters and setters
  public Map<String, EntityExampleDto> getQueries() {
    return queries;
  }

  public void setQueries(Map<String, EntityExampleDto> queries) {
    this.queries = queries;
  }

  public Map<String, Double> getWeights() {
    return weights;
  }

  public void setWeights(Map<String, Double> weights) {
    this.weights = weights;
  }
}
