/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for OpenSanctions feature documentation.
 */
public class FeatureDocDto {

  @JsonProperty("description")
  private String description;

  @JsonProperty("coefficient")
  private Double coefficient;

  @JsonProperty("url")
  private String url;

  // Getters and setters
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Double getCoefficient() {
    return coefficient;
  }

  public void setCoefficient(Double coefficient) {
    this.coefficient = coefficient;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
