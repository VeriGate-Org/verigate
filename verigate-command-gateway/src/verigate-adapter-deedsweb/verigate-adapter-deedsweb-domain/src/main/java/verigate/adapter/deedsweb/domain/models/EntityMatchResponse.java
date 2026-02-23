/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

import java.util.Map;

/**
 * Response model for OpenSanctions entity matching.
 */
public class EntityMatchResponse {

  private final Map<String, EntityMatches> responses;
  private final Map<String, FeatureDoc> matcher;
  private final Integer limit;

  /**
   * Constructor.
   */
  public EntityMatchResponse(
      Map<String, EntityMatches> responses, Map<String, FeatureDoc> matcher, Integer limit) {
    this.responses = responses;
    this.matcher = matcher;
    this.limit = limit;
  }

  // Getters
  public Map<String, EntityMatches> getResponses() {
    return responses;
  }

  public Map<String, FeatureDoc> getMatcher() {
    return matcher;
  }

  public Integer getLimit() {
    return limit;
  }
}
