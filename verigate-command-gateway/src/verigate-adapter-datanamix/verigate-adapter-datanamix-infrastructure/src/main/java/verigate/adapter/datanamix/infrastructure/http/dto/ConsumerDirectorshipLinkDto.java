/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single directorship record from {@code Result.DirectorResults.ConsumerDirectorshipLink[]}
 * in the {@code POST /v1/cipc/director-result} response — this is the array that
 * actually answers story 2.3's "every company this person directs" requirement.
 *
 * <p>Only the fields the domain model needs are mapped; {@code TelephoneNumber},
 * {@code SICDescription}, {@code PhysicalAddress}, and {@code PostalAddress} are
 * present in the real response but not modeled here.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConsumerDirectorshipLinkDto(
    @JsonProperty("DirectorDesignationDescription") String directorDesignationDescription,
    @JsonProperty("AppointmentDate") String appointmentDate,
    @JsonProperty("CommercialName") String commercialName,
    @JsonProperty("CommercialStatus") String commercialStatus,
    @JsonProperty("RegistrationNumber") String registrationNumber,
    @JsonProperty("DirectorStatus") String directorStatus
) {
}
