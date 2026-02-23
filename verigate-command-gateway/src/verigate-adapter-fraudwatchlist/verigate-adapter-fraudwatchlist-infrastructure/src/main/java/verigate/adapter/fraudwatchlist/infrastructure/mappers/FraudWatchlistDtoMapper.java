/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.infrastructure.mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.fraudwatchlist.domain.models.FraudAlert;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckRequest;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckResponse;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckStatus;
import verigate.adapter.fraudwatchlist.domain.models.FraudSource;
import verigate.adapter.fraudwatchlist.infrastructure.http.dto.FraudCheckRequestDto;
import verigate.adapter.fraudwatchlist.infrastructure.http.dto.FraudCheckResponseDto;

/**
 * Mapper for converting Fraud Watchlist DTOs to domain models and vice versa.
 */
public class FraudWatchlistDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(FraudWatchlistDtoMapper.class);

  // Common date formats used by fraud data sources
  private static final DateTimeFormatter[] DATE_FORMATTERS = {
      DateTimeFormatter.ofPattern("yyyy-MM-dd"),
      DateTimeFormatter.ofPattern("dd-MM-yyyy"),
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("yyyy/MM/dd")
  };

  /**
   * Maps a domain FraudCheckRequest to a DTO for the API call.
   */
  public FraudCheckRequestDto mapToRequestDto(FraudCheckRequest request) {
    if (request == null) {
      return null;
    }

    return new FraudCheckRequestDto(
        request.idNumber(),
        request.firstName(),
        request.lastName(),
        request.dateOfBirth(),
        request.phoneNumber());
  }

  /**
   * Maps a response DTO to a domain FraudCheckResponse.
   */
  public FraudCheckResponse mapToFraudCheckResponse(FraudCheckResponseDto dto) {
    if (dto == null) {
      return null;
    }

    FraudCheckStatus status = FraudCheckStatus.fromDescription(dto.status());
    List<FraudAlert> alerts = mapToFraudAlerts(dto.alerts());
    List<FraudSource> matchedSources = mapToFraudSources(dto.matchedSources());

    return new FraudCheckResponse(
        status,
        alerts,
        dto.overallRiskScore(),
        matchedSources,
        dto.screeningSummary());
  }

  /**
   * Maps alert DTOs to domain fraud alerts.
   */
  private List<FraudAlert> mapToFraudAlerts(List<FraudCheckResponseDto.FraudAlertDto> alertDtos) {
    if (alertDtos == null) {
      return List.of();
    }

    return alertDtos.stream()
        .map(this::mapToFraudAlert)
        .collect(Collectors.toList());
  }

  /**
   * Maps an individual alert DTO to a domain fraud alert.
   */
  private FraudAlert mapToFraudAlert(FraudCheckResponseDto.FraudAlertDto dto) {
    if (dto == null) {
      return null;
    }

    return new FraudAlert(
        FraudSource.fromDescription(dto.source()),
        dto.alertType(),
        parseDate(dto.alertDate()),
        dto.severity(),
        dto.description(),
        dto.referenceNumber());
  }

  /**
   * Maps source description strings to domain FraudSource enums.
   */
  private List<FraudSource> mapToFraudSources(List<String> sourceDescriptions) {
    if (sourceDescriptions == null) {
      return List.of();
    }

    return sourceDescriptions.stream()
        .map(FraudSource::fromDescription)
        .filter(source -> source != null)
        .collect(Collectors.toList());
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
