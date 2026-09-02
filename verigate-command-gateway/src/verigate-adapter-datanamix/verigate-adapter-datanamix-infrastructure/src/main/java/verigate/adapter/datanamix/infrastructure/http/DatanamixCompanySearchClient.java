/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.datanamix.domain.models.CompanyDirector;
import verigate.adapter.datanamix.domain.models.CompanyProfile;
import verigate.adapter.datanamix.domain.models.CompanySearchMatch;
import verigate.adapter.datanamix.domain.services.CompanySearchService;
import verigate.adapter.datanamix.infrastructure.config.DatanamixApiConfiguration;
import verigate.adapter.datanamix.infrastructure.http.dto.CipcResultDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CommercialBusinessInformationDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CommercialDirectorInformationDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CompanyMatchDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CompanyResultRequestDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CompanyResultResponseDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CompanySearchRequestDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CompanySearchResponseDto;

/**
 * Implements {@link CompanySearchService} by orchestrating Datanamix's two-step CIPC
 * Search API: {@code /v1/cipc/company-search} to resolve identity matches for a name or
 * registration number, then {@code /v1/cipc/company-result} to retrieve the full
 * business/director record for a chosen match.
 */
public class DatanamixCompanySearchClient implements CompanySearchService {

  private static final Logger logger = LoggerFactory.getLogger(DatanamixCompanySearchClient.class);

  private static final String ENDPOINT_COMPANY_SEARCH = "/v1/cipc/company-search";
  private static final String ENDPOINT_COMPANY_RESULT = "/v1/cipc/company-result";
  private static final String OUTPUT_FORMAT_JSON = "JSON";

  // Payload-level Datanamix ResponseCode values, documented identically for both the
  // company-search and company-result endpoints' "List of Result Messages" tables.
  private static final int RESPONSE_CODE_SUCCESS = 0;
  private static final int RESPONSE_CODE_NOT_FOUND = 4;
  private static final int RESPONSE_CODE_SERVICE_UNAVAILABLE = 5;
  private static final int RESPONSE_CODE_VALIDATION_ERROR = 6;
  private static final int RESPONSE_CODE_INTERNAL_ERROR = 7;

  private final DatanamixHttpAdapter httpAdapter;
  private final DatanamixApiConfiguration configuration;

  public DatanamixCompanySearchClient(
      DatanamixHttpAdapter httpAdapter, DatanamixApiConfiguration configuration) {
    this.httpAdapter = httpAdapter;
    this.configuration = configuration;
  }

  @Override
  public List<CompanySearchMatch> searchByName(String businessName)
      throws TransientException, PermanentException {

    if (businessName == null || businessName.trim().isEmpty()) {
      throw new PermanentException("Business name is required for company search");
    }

    CompanySearchResponseDto response = httpAdapter.post(
        ENDPOINT_COMPANY_SEARCH,
        CompanySearchRequestDto.byBusinessName(
            configuration.getEnvironmentType(), newClientReference(), businessName.trim()),
        CompanySearchResponseDto.class);

    checkResponseCode(ENDPOINT_COMPANY_SEARCH, response.responseCode(), response.messages());

    if (!response.success() || response.commercialDetails().isEmpty()) {
      logger.info("No Datanamix company match for name search: messages={}", response.messages());
      return List.of();
    }

    List<CompanySearchMatch> matches = new ArrayList<>();
    for (CompanyMatchDto match : response.commercialDetails()) {
      if (match.enquiryId() == null || match.enquiryResultId() == null) {
        logger.warn("Skipping Datanamix company-search match with no enquiry reference: {}",
            match.businessName());
        continue;
      }
      matches.add(new CompanySearchMatch(
          match.businessName(), match.registrationNumber(),
          match.enquiryId(), match.enquiryResultId()));
    }
    return matches;
  }

  @Override
  public CompanyProfile searchByRegistrationNumber(String registrationNumber)
      throws TransientException, PermanentException {

    if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
      throw new PermanentException("Registration number is required for company search");
    }
    String trimmed = registrationNumber.trim();

    CompanySearchResponseDto response = httpAdapter.post(
        ENDPOINT_COMPANY_SEARCH,
        CompanySearchRequestDto.byRegistrationNumber(
            configuration.getEnvironmentType(), newClientReference(), trimmed),
        CompanySearchResponseDto.class);

    checkResponseCode(ENDPOINT_COMPANY_SEARCH, response.responseCode(), response.messages());

    if (!response.success() || response.commercialDetails().isEmpty()) {
      logger.info(
          "No Datanamix company match for registration number search: messages={}",
          response.messages());
      return CompanyProfile.notFound(trimmed);
    }

    // An exact registration-number search is expected to resolve to a single company;
    // if Datanamix ever returns more than one, the first is used and the rest logged
    // rather than silently discarded.
    CompanyMatchDto match = response.commercialDetails().get(0);
    if (response.commercialDetails().size() > 1) {
      logger.warn(
          "Datanamix returned {} matches for an exact registration number search; using the first",
          response.commercialDetails().size());
    }
    if (match.enquiryId() == null || match.enquiryResultId() == null) {
      logger.warn(
          "Datanamix company-search match had no enquiry reference for registration number search");
      return CompanyProfile.notFound(trimmed);
    }

    return getProfile(match.enquiryId(), match.enquiryResultId());
  }

  @Override
  public CompanyProfile getProfile(long enquiryId, long enquiryResultId)
      throws TransientException, PermanentException {

    CompanyResultResponseDto response = httpAdapter.post(
        ENDPOINT_COMPANY_RESULT,
        new CompanyResultRequestDto(
            configuration.getEnvironmentType(), newClientReference(), OUTPUT_FORMAT_JSON,
            enquiryId, enquiryResultId),
        CompanyResultResponseDto.class);

    CipcResultDto result = response.cipcResult();
    if (result == null) {
      logger.warn(
          "Datanamix company-result returned no CIPCResult for EnquiryID={}, EnquiryResultID={}",
          enquiryId, enquiryResultId);
      return CompanyProfile.notFound(null);
    }

    checkResponseCode(ENDPOINT_COMPANY_RESULT, result.responseCode(), result.messages());

    if (!result.success() || result.commercialBusinessInformation() == null) {
      logger.info(
          "Datanamix company-result found no business data for EnquiryID={}, EnquiryResultID={}: "
              + "messages={}",
          enquiryId, enquiryResultId, result.messages());
      return CompanyProfile.notFound(null);
    }

    CommercialBusinessInformationDto business = result.commercialBusinessInformation();
    List<CompanyDirector> directors = new ArrayList<>();
    for (CommercialDirectorInformationDto directorDto : result.commercialDirectorInformation()) {
      directors.add(new CompanyDirector(
          directorDto.idNumber(),
          directorDto.fullName(),
          directorDto.directorStatusCode(),
          parseDateOrNull(directorDto.appointmentDate())));
    }

    return new CompanyProfile(
        true,
        business.businessName(),
        business.tradeName(),
        business.registrationNumber(),
        business.businessStatus(),
        business.businessType(),
        parseDateOrNull(business.registrationDate()),
        directors);
  }

  /**
   * Applies Datanamix's payload-level {@code ResponseCode} semantics. {@code 0}
   * (success) and {@code 4} (no record found) both fall through to the caller's
   * existing {@code success()}/empty-result checks — {@code 4} is a normal "not found"
   * outcome, not an error. {@code 5}/{@code 7} are retriable service problems;
   * {@code 6} is a client-side validation error and never worth retrying.
   */
  private void checkResponseCode(String endpoint, int responseCode, List<String> messages)
      throws TransientException, PermanentException {
    switch (responseCode) {
      case RESPONSE_CODE_SUCCESS, RESPONSE_CODE_NOT_FOUND -> {
        // No action needed -- handled by the caller's success()/empty-result checks.
      }
      case RESPONSE_CODE_SERVICE_UNAVAILABLE ->
          throw new TransientException(
              "Datanamix " + endpoint + " temporarily unavailable: " + messages);
      case RESPONSE_CODE_VALIDATION_ERROR ->
          throw new PermanentException(
              "Datanamix " + endpoint + " rejected the request: " + messages);
      case RESPONSE_CODE_INTERNAL_ERROR ->
          throw new TransientException(
              "Datanamix " + endpoint + " internal error, retry may succeed: " + messages);
      default ->
          logger.warn(
              "Unrecognized Datanamix ResponseCode {} from {}: {}",
              responseCode, endpoint, messages);
    }
  }

  private LocalDate parseDateOrNull(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return LocalDate.parse(value.trim());
    } catch (DateTimeParseException e) {
      logger.warn("Could not parse Datanamix date '{}': {}", value, e.getMessage());
      return null;
    }
  }

  private String newClientReference() {
    return "VERIGATE-" + UUID.randomUUID();
  }
}
