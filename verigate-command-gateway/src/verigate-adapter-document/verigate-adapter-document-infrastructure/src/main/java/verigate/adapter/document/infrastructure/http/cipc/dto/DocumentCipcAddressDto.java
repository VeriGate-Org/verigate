/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http.cipc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Minimal CIPC address fields needed for document cross-validation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentCipcAddressDto(
    @JsonProperty("address_line_1") String addressLine1,
    @JsonProperty("address_line_2") String addressLine2,
    @JsonProperty("city") String city,
    @JsonProperty("region") String region,
    @JsonProperty("postal_code") String postalCode
) {

  /**
   * Formats the address as a single display/comparison line.
   */
  public String asSingleLine() {
    StringBuilder sb = new StringBuilder();
    appendPart(sb, addressLine1);
    appendPart(sb, addressLine2);
    appendPart(sb, city);
    appendPart(sb, region);
    appendPart(sb, postalCode);
    return sb.toString();
  }

  private void appendPart(StringBuilder sb, String part) {
    if (part != null && !part.trim().isEmpty()) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(part.trim());
    }
  }
}
