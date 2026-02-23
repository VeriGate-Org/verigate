/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for fraud watchlist check request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FraudCheckRequestDto(
    @JsonProperty("id_number") String idNumber,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("date_of_birth") String dateOfBirth,
    @JsonProperty("phone_number") String phoneNumber
) {
}
