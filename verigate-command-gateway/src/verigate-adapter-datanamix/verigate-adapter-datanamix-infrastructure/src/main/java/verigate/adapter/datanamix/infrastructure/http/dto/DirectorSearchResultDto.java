/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single identity match from {@code POST /v1/cipc/director-search}'s
 * {@code DirectorSearchResults} array. Only the fields story 2.3 needs are mapped;
 * {@code PassportNumber}/{@code FirstInitial}/{@code BirthDate}/{@code Gender} are
 * present in the real response but not modeled here.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectorSearchResultDto(
    @JsonProperty("IDNumber") String idNumber,
    @JsonProperty("FirstName") String firstName,
    @JsonProperty("Surname") String surname,
    @JsonProperty("EnquiryID") Long enquiryId,
    @JsonProperty("EnquiryResultID") Long enquiryResultId
) {
}
