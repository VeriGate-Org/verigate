/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for SAQA qualification verification API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record QualificationVerificationResponseDto(
    @JsonProperty("status") String status,
    @JsonProperty("qualifications") List<QualificationRecordDto> qualifications,
    @JsonProperty("matched_qualification") QualificationRecordDto matchedQualification,
    @JsonProperty("match_confidence") double matchConfidence,
    @JsonProperty("verification_notes") String verificationNotes
) {

  /**
   * DTO for an individual qualification record in the SAQA response.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record QualificationRecordDto(
      @JsonProperty("qualification_title") String qualificationTitle,
      @JsonProperty("qualification_type") String qualificationType,
      @JsonProperty("institution") String institution,
      @JsonProperty("nqf_level") int nqfLevel,
      @JsonProperty("date_conferred") String dateConferred,
      @JsonProperty("saqa_id") String saqaId,
      @JsonProperty("status") String status
  ) {
  }
}
