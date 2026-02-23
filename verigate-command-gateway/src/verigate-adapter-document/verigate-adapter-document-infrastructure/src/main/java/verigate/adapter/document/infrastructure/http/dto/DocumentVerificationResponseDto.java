/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * DTO for Document Verification API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentVerificationResponseDto(
    @JsonProperty("status") String status,
    @JsonProperty("document_type") String documentType,
    @JsonProperty("extracted_data") Map<String, String> extractedData,
    @JsonProperty("confidence_score") double confidenceScore,
    @JsonProperty("match_details") String matchDetails,
    @JsonProperty("verification_notes") String verificationNotes
) {
}
