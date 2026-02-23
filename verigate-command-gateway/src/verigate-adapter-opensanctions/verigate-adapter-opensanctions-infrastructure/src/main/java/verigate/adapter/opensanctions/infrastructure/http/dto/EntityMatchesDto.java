/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for OpenSanctions entity matches.
 */
public class EntityMatchesDto {

  @JsonProperty("status")
  private Integer status;

  @JsonProperty("results")
  private List<ScoredEntityDto> results;

  @JsonProperty("total")
  private TotalSpecDto total;

  @JsonProperty("query")
  private EntityExampleDto query;

  // Getters and setters
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public List<ScoredEntityDto> getResults() {
    return results;
  }

  public void setResults(List<ScoredEntityDto> results) {
    this.results = results;
  }

  public TotalSpecDto getTotal() {
    return total;
  }

  public void setTotal(TotalSpecDto total) {
    this.total = total;
  }

  public EntityExampleDto getQuery() {
    return query;
  }

  public void setQuery(EntityExampleDto query) {
    this.query = query;
  }
}
