/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for {@code POST /v1/cipc/company-search}. Per Datanamix's docs, exactly
 * one of {@code BusinessRegistrationNumber}, {@code BusinessName}, {@code VatNumber}, or
 * {@code SolePropIDNumber} must be populated — the endpoint rejects combinations. Story
 * 2.2 only needs the first two, so {@code VatNumber}/{@code SolePropIDNumber} are always
 * null; the static factories enforce that only one field is ever set.
 */
public record CompanySearchRequestDto(
    @JsonProperty("EnvironmentType") String environmentType,
    @JsonProperty("ClientReference") String clientReference,
    @JsonProperty("BusinessRegistrationNumber") String businessRegistrationNumber,
    @JsonProperty("BusinessName") String businessName,
    @JsonProperty("VatNumber") String vatNumber,
    @JsonProperty("SolePropIDNumber") String solePropIdNumber
) {

  /**
   * Builds a request that searches by business name.
   */
  public static CompanySearchRequestDto byBusinessName(
      String environmentType, String clientReference, String businessName) {
    return new CompanySearchRequestDto(
        environmentType, clientReference, null, businessName, null, null);
  }

  /**
   * Builds a request that searches by exact CIPC registration number.
   */
  public static CompanySearchRequestDto byRegistrationNumber(
      String environmentType, String clientReference, String registrationNumber) {
    return new CompanySearchRequestDto(
        environmentType, clientReference, registrationNumber, null, null, null);
  }
}
