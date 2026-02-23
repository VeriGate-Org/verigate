/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for CIPC address data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CipcAddressDto(
    @JsonProperty("address_line_1") String addressLine1,
    @JsonProperty("address_line_2") String addressLine2,
    @JsonProperty("city") String city,
    @JsonProperty("region") String region,
    @JsonProperty("country") String country,
    @JsonProperty("postal_code") String postalCode
) {
}