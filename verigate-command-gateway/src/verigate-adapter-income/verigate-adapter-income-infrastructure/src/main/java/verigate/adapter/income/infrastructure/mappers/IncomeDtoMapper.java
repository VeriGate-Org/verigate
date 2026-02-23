/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.infrastructure.mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.income.domain.enums.ConfidenceLevel;
import verigate.adapter.income.domain.enums.IncomeVerificationStatus;
import verigate.adapter.income.domain.models.IncomeAssessment;
import verigate.adapter.income.domain.models.IncomeVerificationRequest;
import verigate.adapter.income.domain.models.IncomeVerificationResponse;
import verigate.adapter.income.infrastructure.dtos.IncomeVerificationRequestDto;
import verigate.adapter.income.infrastructure.dtos.IncomeVerificationResponseDto;

/**
 * Mapper for converting Income Verification DTOs to domain models and vice versa.
 */
public class IncomeDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(IncomeDtoMapper.class);

  /**
   * Maps a domain request to an infrastructure DTO for the API call.
   *
   * @param domainRequest the domain income verification request
   * @return the infrastructure DTO for the API call, or null if the input is null
   */
  public IncomeVerificationRequestDto mapToRequestDto(
      IncomeVerificationRequest domainRequest) {
    if (domainRequest == null) {
      return null;
    }

    return new IncomeVerificationRequestDto(
        domainRequest.idNumber(),
        domainRequest.employerName(),
        domainRequest.declaredMonthlyIncome(),
        domainRequest.sourceType() != null ? domainRequest.sourceType().name() : null,
        domainRequest.bankAccountNumber());
  }

  /**
   * Maps an infrastructure response DTO to a domain response model.
   *
   * @param dto the infrastructure response DTO from the API
   * @return the domain income verification response
   */
  public IncomeVerificationResponse mapToResponse(IncomeVerificationResponseDto dto) {
    if (dto == null) {
      logger.warn("Received null income verification response DTO");
      return IncomeVerificationResponse.error(null, "No response received from income API");
    }

    IncomeVerificationStatus status =
        IncomeVerificationStatus.fromDescription(dto.status());
    ConfidenceLevel confidence =
        ConfidenceLevel.fromDescription(dto.confidenceLevel());

    BigDecimal verifiedMonthlyIncome =
        dto.verifiedMonthlyIncome() != null ? dto.verifiedMonthlyIncome() : BigDecimal.ZERO;
    BigDecimal declaredMonthlyIncome =
        dto.declaredMonthlyIncome() != null ? dto.declaredMonthlyIncome() : BigDecimal.ZERO;
    BigDecimal variance = dto.variance() != null
        ? dto.variance()
        : IncomeAssessment.calculateVariance(declaredMonthlyIncome, verifiedMonthlyIncome);

    List<String> evidenceSources =
        dto.evidenceSources() != null ? dto.evidenceSources() : List.of();

    IncomeAssessment assessment = new IncomeAssessment(
        verifiedMonthlyIncome,
        declaredMonthlyIncome,
        variance,
        confidence,
        evidenceSources,
        dto.affordabilityConfirmed());

    LocalDateTime verifiedAt = parseVerifiedAt(dto.verifiedAt());

    return new IncomeVerificationResponse(
        status,
        assessment,
        dto.reason(),
        verifiedAt);
  }

  /**
   * Parses the verified-at timestamp from the API response.
   *
   * @param verifiedAt the timestamp string from the API
   * @return the parsed LocalDateTime, or the current time if parsing fails
   */
  private LocalDateTime parseVerifiedAt(String verifiedAt) {
    if (verifiedAt == null || verifiedAt.trim().isEmpty()) {
      return LocalDateTime.now();
    }

    try {
      return LocalDateTime.parse(verifiedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } catch (DateTimeParseException e) {
      logger.warn("Failed to parse verifiedAt timestamp '{}', using current time", verifiedAt);
      return LocalDateTime.now();
    }
  }
}
