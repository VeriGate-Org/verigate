/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.domain.models;

import java.util.List;

/**
 * Entity matches result for a specific query.
 */
public class EntityMatches {

  private final Integer status;
  private final List<ScoredEntity> results;
  private final TotalSpec total;
  private final EntityExample query;

  /**
   * Constructor.
   */
  public EntityMatches(
      Integer status, List<ScoredEntity> results, TotalSpec total, EntityExample query) {
    this.status = status;
    this.results = results;
    this.total = total;
    this.query = query;
  }

  // Getters
  public Integer getStatus() {
    return status;
  }

  public List<ScoredEntity> getResults() {
    return results;
  }

  public TotalSpec getTotal() {
    return total;
  }

  public EntityExample getQuery() {
    return query;
  }
}
