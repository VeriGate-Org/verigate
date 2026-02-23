/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for CIPC secretary data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CipcSecretaryDto(
    @JsonProperty("first_names") String firstNames,
    @JsonProperty("surname") String surname,
    @JsonProperty("initials") String initials,
    @JsonProperty("date_of_birth") String dateOfBirth,
    @JsonProperty("status") String status,
    @JsonProperty("type") String type,
    @JsonProperty("designation") String designation,
    @JsonProperty("appointment_date") String appointmentDate,
    @JsonProperty("resignation_date") String resignationDate,
    @JsonProperty("residential_address_1") String residentialAddress1,
    @JsonProperty("residential_address_2") String residentialAddress2,
    @JsonProperty("residential_address_3") String residentialAddress3,
    @JsonProperty("residential_address_4") String residentialAddress4,
    @JsonProperty("residential_postal_code") String residentialPostalCode,
    @JsonProperty("country") String country
) {
}