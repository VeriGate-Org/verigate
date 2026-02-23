/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Document Verification API request.
 */
public record DocumentVerificationRequestDto(
    @JsonProperty("document_reference") String documentReference,
    @JsonProperty("document_type") String documentType,
    @JsonProperty("subject_id_number") String subjectIdNumber,
    @JsonProperty("subject_name") String subjectName,
    @JsonProperty("s3_bucket_name") String s3BucketName,
    @JsonProperty("s3_object_key") String s3ObjectKey
) {
}
