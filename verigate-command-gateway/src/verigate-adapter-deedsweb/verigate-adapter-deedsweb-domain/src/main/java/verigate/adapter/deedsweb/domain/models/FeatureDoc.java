/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Feature documentation for matching algorithm.
 */
public class FeatureDoc {

  private final String description;
  private final Double coefficient;
  private final String url;

  /**
   * Constructor.
   */
  public FeatureDoc(String description, Double coefficient, String url) {
    this.description = description;
    this.coefficient = coefficient;
    this.url = url;
  }

  // Getters
  public String getDescription() {
    return description;
  }

  public Double getCoefficient() {
    return coefficient;
  }

  public String getUrl() {
    return url;
  }
}
