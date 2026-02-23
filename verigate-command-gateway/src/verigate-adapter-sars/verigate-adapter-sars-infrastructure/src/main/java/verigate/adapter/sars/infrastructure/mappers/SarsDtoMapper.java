/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.mappers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.sars.domain.enums.TaxClearanceType;
import verigate.adapter.sars.domain.enums.TaxComplianceStatus;
import verigate.adapter.sars.domain.models.TaxClearanceCertificate;
import verigate.adapter.sars.domain.models.TaxComplianceRequest;
import verigate.adapter.sars.domain.models.TaxComplianceResponse;
import verigate.adapter.sars.infrastructure.dtos.SarsTaxComplianceRequestDto;
import verigate.adapter.sars.infrastructure.dtos.SarsTaxComplianceResponseDto;

/**
 * Mapper for converting SARS Tax Compliance DTOs to domain models and vice versa.
 */
public class SarsDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(SarsDtoMapper.class);

  /**
   * Maps a domain request to an infrastructure DTO for the API call.
   *
   * @param domainRequest the domain tax compliance request
   * @return the infrastructure request DTO
   */
  public SarsTaxComplianceRequestDto mapToRequestDto(TaxComplianceRequest domainRequest) {
    if (domainRequest == null) {
      return null;
    }

    return new SarsTaxComplianceRequestDto(
        domainRequest.idNumber(),
        domainRequest.taxReferenceNumber(),
        domainRequest.companyRegistrationNumber(),
        domainRequest.clearanceType() != null
            ? domainRequest.clearanceType().name()
            : TaxClearanceType.GOOD_STANDING.name());
  }

  /**
   * Maps an infrastructure response DTO to a domain response model.
   *
   * @param dto the infrastructure response DTO
   * @return the domain tax compliance response
   */
  public TaxComplianceResponse mapToResponse(SarsTaxComplianceResponseDto dto) {
    if (dto == null) {
      logger.warn("Received null SARS tax compliance response DTO");
      return TaxComplianceResponse.notFound();
    }

    TaxComplianceStatus status = TaxComplianceStatus.fromDescription(dto.status());
    TaxClearanceCertificate certificate = mapToCertificate(dto);

    return new TaxComplianceResponse(
        status,
        certificate,
        dto.reason(),
        java.time.LocalDateTime.now());
  }

  /**
   * Maps the response DTO to a TaxClearanceCertificate domain model.
   *
   * @param dto the response DTO containing certificate details
   * @return the TaxClearanceCertificate, or null if no certificate data is present
   */
  private TaxClearanceCertificate mapToCertificate(SarsTaxComplianceResponseDto dto) {
    if (dto.certificateNumber() == null || dto.certificateNumber().trim().isEmpty()) {
      return null;
    }

    LocalDate issueDate = parseDate(dto.issueDate(), "issue date");
    LocalDate expiryDate = parseDate(dto.expiryDate(), "expiry date");
    TaxClearanceType clearanceType = dto.clearanceType() != null
        ? TaxClearanceType.fromDescription(dto.clearanceType())
        : TaxClearanceType.GOOD_STANDING;
    boolean valid = dto.certificateValid() != null ? dto.certificateValid() : false;

    return new TaxClearanceCertificate(
        dto.certificateNumber(),
        issueDate,
        expiryDate,
        clearanceType,
        valid);
  }

  /**
   * Safely parses a date string to a LocalDate.
   *
   * @param dateString the date string to parse
   * @param fieldName the name of the field for logging purposes
   * @return the parsed LocalDate, or null if parsing fails
   */
  private LocalDate parseDate(String dateString, String fieldName) {
    if (dateString == null || dateString.trim().isEmpty()) {
      return null;
    }

    try {
      return LocalDate.parse(dateString.trim());
    } catch (DateTimeParseException e) {
      logger.warn(
          "Failed to parse {} from SARS response: {} - value: {}",
          fieldName,
          e.getMessage(),
          dateString);
      return null;
    }
  }
}
