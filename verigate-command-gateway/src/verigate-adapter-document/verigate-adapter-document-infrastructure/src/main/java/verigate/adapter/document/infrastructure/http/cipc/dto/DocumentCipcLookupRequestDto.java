/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http.cipc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for the CIPC {@code /companyprofile} lookup, used to cross-validate an uploaded
 * CIPC registration document. Intentionally a separate, minimal DTO from
 * {@code verigate-adapter-cipc}'s equivalent — see story 2.1 design notes.
 */
public record DocumentCipcLookupRequestDto(
    @JsonProperty("enterprise_number") String enterpriseNumber
) {
}
