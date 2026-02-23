/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for CIPC company data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CipcCompanyDto(
    @JsonProperty("enterprise_number") String enterpriseNumber,
    @JsonProperty("enterprise_name") String enterpriseName,
    @JsonProperty("enterprise_short_name") String enterpriseShortName,
    @JsonProperty("trading_name") String tradingName,
    @JsonProperty("translated_name") String translatedName,
    @JsonProperty("enterprise_type_description") String enterpriseTypeDescription,
    @JsonProperty("business_activity") String businessActivity,
    @JsonProperty("registration_date") String registrationDate,
    @JsonProperty("business_start_date") String businessStartDate,
    @JsonProperty("enterprise_status_description") String enterpriseStatusDescription,
    @JsonProperty("financial_year_end") String financialYearEnd,
    @JsonProperty("financial_year_effective_date") String financialYearEffectiveDate,
    @JsonProperty("tax_number") String taxNumber,
    @JsonProperty("office_address") List<CipcAddressDto> officeAddress,
    @JsonProperty("postal_address") List<CipcAddressDto> postalAddress,
    @JsonProperty("directors") List<CipcDirectorDto> directors,
    @JsonProperty("secretaries") List<CipcSecretaryDto> secretaries,
    @JsonProperty("auditors") List<CipcAuditorDto> auditors,
    @JsonProperty("captial") List<CipcCapitalDto> capital, // Note: typo in CIPC API
    @JsonProperty("history") List<CipcHistoryDto> history
) {
}