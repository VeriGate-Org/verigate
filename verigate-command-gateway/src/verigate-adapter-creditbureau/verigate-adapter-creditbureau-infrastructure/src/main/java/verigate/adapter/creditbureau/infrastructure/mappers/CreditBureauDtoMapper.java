/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.infrastructure.mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.creditbureau.domain.models.CreditAssessment;
import verigate.adapter.creditbureau.domain.models.CreditBureauProvider;
import verigate.adapter.creditbureau.domain.models.CreditCheckRequest;
import verigate.adapter.creditbureau.domain.models.CreditCheckResponse;
import verigate.adapter.creditbureau.domain.models.CreditCheckStatus;
import verigate.adapter.creditbureau.domain.models.CreditScoreBand;
import verigate.adapter.creditbureau.infrastructure.http.dto.CreditCheckRequestDto;
import verigate.adapter.creditbureau.infrastructure.http.dto.CreditCheckResponseDto;

/**
 * Mapper for converting Credit Bureau DTOs to domain models and vice versa.
 */
public class CreditBureauDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(CreditBureauDtoMapper.class);

  // Common date formats used by credit bureau providers
  private static final DateTimeFormatter[] DATE_FORMATTERS = {
    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    DateTimeFormatter.ofPattern("dd-MM-yyyy"),
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ofPattern("yyyy/MM/dd")
  };

  /**
   * Maps a domain CreditCheckRequest to a CreditCheckRequestDto.
   */
  public CreditCheckRequestDto mapToRequestDto(CreditCheckRequest request) {
    if (request == null) {
      return null;
    }

    return new CreditCheckRequestDto(
        request.idNumber(),
        request.firstName(),
        request.lastName(),
        request.dateOfBirth(),
        request.consentReference());
  }

  /**
   * Maps a CreditCheckResponseDto to a domain CreditCheckResponse.
   */
  public CreditCheckResponse mapToResponse(CreditCheckResponseDto dto) {
    if (dto == null) {
      return CreditCheckResponse.error("Null response received from credit bureau");
    }

    CreditCheckStatus status = CreditCheckStatus.fromDescription(dto.status());

    if (status == CreditCheckStatus.BUREAU_UNAVAILABLE) {
      return new CreditCheckResponse(
          CreditCheckStatus.BUREAU_UNAVAILABLE, null, false, "UNKNOWN",
          dto.summary() != null ? dto.summary() : "Credit bureau service is unavailable");
    }

    if (status == CreditCheckStatus.ERROR) {
      return CreditCheckResponse.error(
          dto.summary() != null ? dto.summary() : "Credit check error occurred");
    }

    if (status == CreditCheckStatus.INSUFFICIENT_DATA) {
      return CreditCheckResponse.insufficientData(
          dto.summary() != null ? dto.summary() : "Insufficient data for credit assessment");
    }

    CreditAssessment assessment = mapToAssessment(dto);

    boolean affordabilityConfirmed =
        dto.affordabilityConfirmed() != null && dto.affordabilityConfirmed();
    String riskLevel = dto.riskLevel() != null ? dto.riskLevel() : "UNKNOWN";
    String summary = dto.summary() != null ? dto.summary() : "";

    return new CreditCheckResponse(status, assessment, affordabilityConfirmed, riskLevel, summary);
  }

  /**
   * Maps the response DTO fields into a CreditAssessment domain model.
   */
  private CreditAssessment mapToAssessment(CreditCheckResponseDto dto) {
    int creditScore = dto.creditScore() != null ? dto.creditScore() : 0;
    CreditScoreBand scoreBand = CreditScoreBand.fromScore(creditScore);
    CreditBureauProvider provider = parseProvider(dto.provider());
    LocalDate assessmentDate = parseDate(dto.assessmentDate());
    double totalDebt = dto.totalDebt() != null ? dto.totalDebt() : 0.0;
    double monthlyIncome = dto.monthlyIncome() != null ? dto.monthlyIncome() : 0.0;
    double debtToIncomeRatio = dto.debtToIncomeRatio() != null ? dto.debtToIncomeRatio() : 0.0;
    boolean hasJudgments = dto.hasJudgments() != null && dto.hasJudgments();
    boolean hasDefaults = dto.hasDefaults() != null && dto.hasDefaults();
    List<String> fraudIndicators =
        dto.fraudIndicators() != null ? dto.fraudIndicators() : List.of();

    return new CreditAssessment(
        creditScore, scoreBand, provider, assessmentDate,
        totalDebt, monthlyIncome, debtToIncomeRatio,
        hasJudgments, hasDefaults, fraudIndicators);
  }

  /**
   * Parses the credit bureau provider string into the enum.
   */
  private CreditBureauProvider parseProvider(String provider) {
    if (provider == null || provider.trim().isEmpty()) {
      return CreditBureauProvider.EXPERIAN; // Default provider
    }

    try {
      return CreditBureauProvider.valueOf(provider.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      logger.warn("Unknown credit bureau provider: {}, defaulting to EXPERIAN", provider);
      return CreditBureauProvider.EXPERIAN;
    }
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
