/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.mappers;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.document.domain.models.DocumentType;
import verigate.adapter.document.domain.models.DocumentVerificationRequest;
import verigate.adapter.document.domain.models.DocumentVerificationResponse;
import verigate.adapter.document.domain.models.DocumentVerificationStatus;
import verigate.adapter.document.infrastructure.http.dto.DocumentVerificationRequestDto;
import verigate.adapter.document.infrastructure.http.dto.DocumentVerificationResponseDto;

/**
 * Mapper for converting between Document Verification DTOs and domain models.
 */
public class DocumentDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(DocumentDtoMapper.class);

  /**
   * Maps a domain request to an infrastructure DTO for the API call.
   */
  public DocumentVerificationRequestDto mapToRequestDto(DocumentVerificationRequest request) {
    if (request == null) {
      return null;
    }

    return new DocumentVerificationRequestDto(
        request.documentReference(),
        request.documentType() != null ? request.documentType().toString() : null,
        request.subjectIdNumber(),
        request.subjectName(),
        request.s3BucketName(),
        request.s3ObjectKey()
    );
  }

  /**
   * Maps an infrastructure response DTO to a domain response.
   */
  public DocumentVerificationResponse mapToResponse(DocumentVerificationResponseDto dto) {
    if (dto == null) {
      logger.warn("Received null response DTO from Document Verification API");
      return DocumentVerificationResponse.error("Received empty response from verification API");
    }

    DocumentVerificationStatus status =
        DocumentVerificationStatus.fromDescription(dto.status());

    DocumentType documentType = dto.documentType() != null
        ? DocumentType.fromDescription(dto.documentType())
        : null;

    Map<String, String> extractedData = dto.extractedData() != null
        ? dto.extractedData()
        : Map.of();

    return new DocumentVerificationResponse(
        status,
        documentType,
        extractedData,
        dto.confidenceScore(),
        dto.matchDetails(),
        dto.verificationNotes()
    );
  }
}
