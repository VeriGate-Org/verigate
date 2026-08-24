/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http.cipc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Minimal CIPC director fields needed for document cross-validation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentCipcDirectorDto(
    @JsonProperty("first_names") String firstNames,
    @JsonProperty("surname") String surname
) {

  /**
   * Returns the director's full name, or an empty string if both parts are absent.
   */
  public String fullName() {
    StringBuilder sb = new StringBuilder();
    if (firstNames != null && !firstNames.trim().isEmpty()) {
      sb.append(firstNames.trim());
    }
    if (surname != null && !surname.trim().isEmpty()) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(surname.trim());
    }
    return sb.toString();
  }
}
