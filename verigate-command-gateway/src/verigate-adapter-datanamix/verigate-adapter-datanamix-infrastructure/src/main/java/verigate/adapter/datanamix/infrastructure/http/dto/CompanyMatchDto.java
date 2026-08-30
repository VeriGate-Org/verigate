/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single entry in {@code CommercialDetails[]} from {@code POST /v1/cipc/company-search}.
 *
 * <p>Datanamix's own docs are internally inconsistent on this array's field names: the
 * formal API reference (with a live curl example) uses {@code RegistrationNumber} /
 * {@code BusinessName}, while the older narrative "Sandbox" pages show
 * {@code RegistrationNo} / {@code Businessname} (plus a {@code CommercialID} field not
 * used here). The reference schema's live example is treated as authoritative;
 * {@code @JsonAlias} on the two ambiguous fields is a cheap hedge against sandbox
 * actually returning the other shape — confirmed against a real response by
 * {@code DatanamixLiveIntegrationTest} rather than left as a guess.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyMatchDto(
    @JsonProperty("RegistrationNumber") @JsonAlias("RegistrationNo") String registrationNumber,
    @JsonProperty("BusinessName") @JsonAlias("Businessname") String businessName,
    @JsonProperty("EnquiryID") Long enquiryId,
    @JsonProperty("EnquiryResultID") Long enquiryResultId
) {
}
