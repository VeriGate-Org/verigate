/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.infrastructure.mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.saqa.domain.models.QualificationRecord;
import verigate.adapter.saqa.domain.models.QualificationType;
import verigate.adapter.saqa.domain.models.QualificationVerificationRequest;
import verigate.adapter.saqa.domain.models.QualificationVerificationResponse;
import verigate.adapter.saqa.domain.models.QualificationVerificationStatus;
import verigate.adapter.saqa.infrastructure.http.dto.QualificationVerificationRequestDto;
import verigate.adapter.saqa.infrastructure.http.dto.QualificationVerificationResponseDto;
import verigate.adapter.saqa.infrastructure.http.dto.QualificationVerificationResponseDto.QualificationRecordDto;

/**
 * Mapper for converting between SAQA DTOs and domain models.
 */
public class SaqaDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(SaqaDtoMapper.class);

  // Common date formats used by SAQA
  private static final DateTimeFormatter[] DATE_FORMATTERS = {
      DateTimeFormatter.ofPattern("yyyy-MM-dd"),
      DateTimeFormatter.ofPattern("dd-MM-yyyy"),
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("yyyy/MM/dd")
  };

  /**
   * Maps a domain request to a DTO for the SAQA API.
   */
  public QualificationVerificationRequestDto mapToRequestDto(
      QualificationVerificationRequest request) {
    if (request == null) {
      return null;
    }

    return new QualificationVerificationRequestDto(
        request.idNumber(),
        request.firstName(),
        request.lastName(),
        request.qualificationTitle(),
        request.institution(),
        request.yearCompleted());
  }

  /**
   * Maps a SAQA API response DTO to the domain response model.
   */
  public QualificationVerificationResponse mapToResponse(
      QualificationVerificationResponseDto dto) {
    if (dto == null) {
      return QualificationVerificationResponse.error("Null response from SAQA API");
    }

    QualificationVerificationStatus status =
        QualificationVerificationStatus.fromDescription(dto.status());

    List<QualificationRecord> qualifications = mapToQualificationRecords(dto.qualifications());

    QualificationRecord matchedQualification =
        mapToQualificationRecord(dto.matchedQualification());

    return new QualificationVerificationResponse(
        status,
        qualifications,
        matchedQualification,
        dto.matchConfidence(),
        dto.verificationNotes());
  }

  /**
   * Maps a list of qualification record DTOs to domain models.
   */
  private List<QualificationRecord> mapToQualificationRecords(
      List<QualificationRecordDto> dtos) {
    if (dtos == null) {
      return List.of();
    }

    return dtos.stream()
        .map(this::mapToQualificationRecord)
        .collect(Collectors.toList());
  }

  /**
   * Maps a single qualification record DTO to a domain model.
   */
  private QualificationRecord mapToQualificationRecord(QualificationRecordDto dto) {
    if (dto == null) {
      return null;
    }

    return QualificationRecord.builder()
        .qualificationTitle(dto.qualificationTitle())
        .qualificationType(QualificationType.fromDescription(dto.qualificationType()))
        .institution(dto.institution())
        .nqfLevel(dto.nqfLevel())
        .dateConferred(parseDate(dto.dateConferred()))
        .saqaId(dto.saqaId())
        .status(dto.status())
        .build();
  }

  /**
   * Parses date string using multiple format attempts.
   */
  private LocalDate parseDate(String dateString) {
    if (dateString == null || dateString.trim().isEmpty()) {
      return null;
    }

    String trimmed = dateString.trim();

    for (DateTimeFormatter formatter : DATE_FORMATTERS) {
      try {
        return LocalDate.parse(trimmed, formatter);
      } catch (DateTimeParseException e) {
        // Try next formatter
      }
    }

    logger.warn("Failed to parse date: {}", dateString);
    return null;
  }
}
