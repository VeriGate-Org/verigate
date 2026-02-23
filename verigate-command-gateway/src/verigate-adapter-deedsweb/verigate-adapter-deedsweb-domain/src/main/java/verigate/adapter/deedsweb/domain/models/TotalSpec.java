/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Total specification for paginated results.
 */
public class TotalSpec {

  private final Integer value;
  private final String relation;

  public TotalSpec(Integer value, String relation) {
    this.value = value;
    this.relation = relation;
  }

  // Getters
  public Integer getValue() {
    return value;
  }

  public String getRelation() {
    return relation;
  }
}
