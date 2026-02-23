/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.cipc.domain.models.Address;
import verigate.adapter.cipc.domain.models.Auditor;
import verigate.adapter.cipc.domain.models.Capital;
import verigate.adapter.cipc.domain.models.ChangeHistory;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.CompanyStatus;
import verigate.adapter.cipc.domain.models.Director;
import verigate.adapter.cipc.domain.models.DirectorStatus;
import verigate.adapter.cipc.domain.models.DirectorType;
import verigate.adapter.cipc.domain.models.Secretary;
import verigate.adapter.cipc.infrastructure.http.dto.CipcAddressDto;
import verigate.adapter.cipc.infrastructure.http.dto.CipcAuditorDto;
import verigate.adapter.cipc.infrastructure.http.dto.CipcCapitalDto;
import verigate.adapter.cipc.infrastructure.http.dto.CipcCompanyDto;
import verigate.adapter.cipc.infrastructure.http.dto.CipcDirectorDto;
import verigate.adapter.cipc.infrastructure.http.dto.CipcHistoryDto;
import verigate.adapter.cipc.infrastructure.http.dto.CipcSecretaryDto;

/**
 * Mapper for converting CIPC DTOs to domain models.
 */
public class CipcDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(CipcDtoMapper.class);

  // Common date formats used by CIPC
  private static final DateTimeFormatter[] DATE_FORMATTERS = {
    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    DateTimeFormatter.ofPattern("dd-MM-yyyy"),
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ofPattern("yyyy/MM/dd")
  };

  /**
   * Maps CIPC company DTO to domain model.
   */
  public CompanyProfile mapToCompanyProfile(CipcCompanyDto dto) {
    if (dto == null) {
      return null;
    }

    return CompanyProfile.builder()
        .enterpriseNumber(dto.enterpriseNumber())
        .enterpriseName(dto.enterpriseName())
        .enterpriseShortName(dto.enterpriseShortName())
        .tradingName(dto.tradingName())
        .translatedName(dto.translatedName())
        .enterpriseTypeDescription(dto.enterpriseTypeDescription())
        .businessActivity(dto.businessActivity())
        .registrationDate(parseDate(dto.registrationDate()))
        .businessStartDate(parseDate(dto.businessStartDate()))
        .enterpriseStatus(CompanyStatus.fromDescription(dto.enterpriseStatusDescription()))
        .financialYearEnd(dto.financialYearEnd())
        .financialYearEffectiveDate(parseDate(dto.financialYearEffectiveDate()))
        .taxNumber(dto.taxNumber())
        .officeAddress(mapToAddress(dto.officeAddress()))
        .postalAddress(mapToAddress(dto.postalAddress()))
        .directors(mapToDirectors(dto.directors()))
        .secretaries(mapToSecretaries(dto.secretaries()))
        .auditors(mapToAuditors(dto.auditors()))
        .capital(mapToCapital(dto.capital()))
        .history(mapToHistory(dto.history()))
        .build();
  }

  /**
   * Maps address DTOs to domain address (takes first address from list).
   */
  private Address mapToAddress(List<CipcAddressDto> addressDtos) {
    if (addressDtos == null || addressDtos.isEmpty()) {
      return null;
    }

    CipcAddressDto dto = addressDtos.get(0); // Take first address

    return Address.builder()
        .addressLine1(dto.addressLine1())
        .addressLine2(dto.addressLine2())
        .addressLine3(null) // Not available in office/postal address
        .addressLine4(null) // Not available in office/postal address
        .city(dto.city())
        .region(dto.region())
        .postalCode(dto.postalCode())
        .country(dto.country())
        .build();
  }

  /**
   * Maps director DTOs to domain directors.
   */
  private List<Director> mapToDirectors(List<CipcDirectorDto> directorDtos) {
    if (directorDtos == null) {
      return List.of();
    }

    return directorDtos.stream()
        .map(this::mapToDirector)
        .collect(Collectors.toList());
  }

  /**
   * Maps individual director DTO to domain director.
   */
  private Director mapToDirector(CipcDirectorDto dto) {
    if (dto == null) {
      return null;
    }

    Address residentialAddress = Address.builder()
        .addressLine1(dto.residentialAddress1())
        .addressLine2(dto.residentialAddress2())
        .addressLine3(dto.residentialAddress3())
        .addressLine4(dto.residentialAddress4())
        .postalCode(dto.residentialPostalCode())
        .country(dto.country())
        .build();

    return Director.builder()
        .firstNames(dto.firstNames())
        .surname(dto.surname())
        .initials(dto.initials())
        .identityNumber(null) // Not in this DTO structure
        .dateOfBirth(parseDate(dto.dateOfBirth()))
        .directorStatus(DirectorStatus.fromDescription(dto.directorStatus()))
        .directorType(DirectorType.fromDescription(dto.directorType()))
        .designation(dto.designation())
        .appointmentDate(parseDate(dto.appointmentDate()))
        .resignationDate(parseDate(dto.resignationDate()))
        .memberSizeInterest(dto.memberSizeInterest())
        .memberContribution(dto.memberContribution())
        .residentialAddress(residentialAddress)
        .countryCode(dto.country())
        .build();
  }

  /**
   * Maps secretary DTOs to domain secretaries.
   */
  private List<Secretary> mapToSecretaries(List<CipcSecretaryDto> secretaryDtos) {
    if (secretaryDtos == null) {
      return List.of();
    }

    return secretaryDtos.stream()
        .map(this::mapToSecretary)
        .collect(Collectors.toList());
  }

  /**
   * Maps individual secretary DTO to domain secretary.
   */
  private Secretary mapToSecretary(CipcSecretaryDto dto) {
    if (dto == null) {
      return null;
    }

    Address residentialAddress = Address.builder()
        .addressLine1(dto.residentialAddress1())
        .addressLine2(dto.residentialAddress2())
        .addressLine3(dto.residentialAddress3())
        .addressLine4(dto.residentialAddress4())
        .postalCode(dto.residentialPostalCode())
        .country(dto.country())
        .build();

    return Secretary.builder()
        .firstNames(dto.firstNames())
        .surname(dto.surname())
        .initials(dto.initials())
        .dateOfBirth(parseDate(dto.dateOfBirth()))
        .status(dto.status())
        .type(dto.type())
        .designation(dto.designation())
        .appointmentDate(parseDate(dto.appointmentDate()))
        .resignationDate(parseDate(dto.resignationDate()))
        .residentialAddress(residentialAddress)
        .countryCode(dto.country())
        .build();
  }

  /**
   * Maps auditor DTOs to domain auditors.
   */
  private List<Auditor> mapToAuditors(List<CipcAuditorDto> auditorDtos) {
    if (auditorDtos == null) {
      return List.of();
    }

    return auditorDtos.stream()
        .map(this::mapToAuditor)
        .collect(Collectors.toList());
  }

  /**
   * Maps individual auditor DTO to domain auditor.
   */
  private Auditor mapToAuditor(CipcAuditorDto dto) {
    if (dto == null) {
      return null;
    }

    return Auditor.builder()
        .auditorName(dto.auditorName())
        .auditorTypeDescription(dto.auditorTypeDescription())
        .auditorStatusDescription(dto.auditorStatusDescription())
        .professionDescription(dto.professionDescription())
        .activityStartDate(parseDate(dto.activityStartDate()))
        .activityEndDate(parseDate(dto.activityEndDate()))
        .build();
  }

  /**
   * Maps capital DTOs to domain capital.
   */
  private List<Capital> mapToCapital(List<CipcCapitalDto> capitalDtos) {
    if (capitalDtos == null) {
      return List.of();
    }

    return capitalDtos.stream()
        .map(this::mapToCapital)
        .collect(Collectors.toList());
  }

  /**
   * Maps individual capital DTO to domain capital.
   */
  private Capital mapToCapital(CipcCapitalDto dto) {
    if (dto == null) {
      return null;
    }

    return Capital.builder()
        .capitalTypeDescription(dto.capitalTypeDescription())
        .numberOfShares(dto.numberOfShares())
        .parValue(dto.parValue() != null ? BigDecimal.valueOf(dto.parValue()) : null)
        .shareAmount(dto.shareAmount() != null ? BigDecimal.valueOf(dto.shareAmount()) : null)
        .premium(dto.premium() != null ? BigDecimal.valueOf(dto.premium()) : null)
        .build();
  }

  /**
   * Maps history DTOs to domain history.
   */
  private List<ChangeHistory> mapToHistory(List<CipcHistoryDto> historyDtos) {
    if (historyDtos == null) {
      return List.of();
    }

    return historyDtos.stream()
        .map(this::mapToChangeHistory)
        .collect(Collectors.toList());
  }

  /**
   * Maps individual history DTO to domain change history.
   */
  private ChangeHistory mapToChangeHistory(CipcHistoryDto dto) {
    if (dto == null) {
      return null;
    }

    return ChangeHistory.builder()
        .changeDate(parseDate(dto.changeDate()))
        .changeDescription(dto.changeDescription())
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
