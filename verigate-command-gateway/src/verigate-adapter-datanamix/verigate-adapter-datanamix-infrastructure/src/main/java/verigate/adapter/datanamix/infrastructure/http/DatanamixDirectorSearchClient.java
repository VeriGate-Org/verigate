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
import verigate.adapter.datanamix.domain.models.DirectorPortfolio;
import verigate.adapter.datanamix.domain.models.DirectorshipEntry;
import verigate.adapter.datanamix.domain.services.DirectorPortfolioService;
import verigate.adapter.datanamix.infrastructure.config.DatanamixApiConfiguration;
import verigate.adapter.datanamix.infrastructure.http.dto.ConsumerDirectorshipLinkDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorResultRequestDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorResultResponseDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorSearchRequestDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorSearchResponseDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorSearchResultDto;

/**
 * Implements {@link DirectorPortfolioService} by orchestrating Datanamix's two-step
 * Director Verification API: {@code CIPCDirectorSearch} to resolve identity matches for
 * an ID number, then {@code CIPCDirectorResult} for each match to retrieve the actual
 * directorship list ({@code ConsumerDirectorshipLink[]}). Results from all identity
 * matches are flattened into a single {@link DirectorPortfolio}.
 *
 * <p>If a result call for one identity match fails, the whole search fails rather than
 * silently returning a partial portfolio — for a compliance/KYC-relevant check, a loud
 * failure is safer than an incomplete result that looks complete.
 */
public class DatanamixDirectorSearchClient implements DirectorPortfolioService {

  private static final Logger logger = LoggerFactory.getLogger(DatanamixDirectorSearchClient.class);

  private static final String ENDPOINT_DIRECTOR_SEARCH = "/v1/cipc/director-search";
  private static final String ENDPOINT_DIRECTOR_RESULT = "/v1/cipc/director-result";
  private static final String OUTPUT_FORMAT_JSON = "JSON";

  private final DatanamixHttpAdapter httpAdapter;
  private final DatanamixApiConfiguration configuration;

  public DatanamixDirectorSearchClient(
      DatanamixHttpAdapter httpAdapter, DatanamixApiConfiguration configuration) {
    this.httpAdapter = httpAdapter;
    this.configuration = configuration;
  }

  @Override
  public DirectorPortfolio searchByIdNumber(String idNumber)
      throws TransientException, PermanentException {

    if (idNumber == null || idNumber.trim().isEmpty()) {
      throw new PermanentException("ID number is required for director portfolio search");
    }
    String trimmedId = idNumber.trim();

    DirectorSearchResponseDto searchResponse = httpAdapter.post(
        ENDPOINT_DIRECTOR_SEARCH,
        new DirectorSearchRequestDto(
            configuration.getEnvironmentType(), newClientReference(), trimmedId),
        DirectorSearchResponseDto.class);

    if (!searchResponse.success() || searchResponse.directorSearchResults().isEmpty()) {
      logger.info(
          "No Datanamix director identity match for ID [MASKED]: messages={}",
          searchResponse.messages());
      return DirectorPortfolio.notFound(trimmedId);
    }

    List<DirectorshipEntry> allDirectorships = new ArrayList<>();
    DirectorSearchResultDto firstMatch = searchResponse.directorSearchResults().get(0);

    for (DirectorSearchResultDto match : searchResponse.directorSearchResults()) {
      allDirectorships.addAll(fetchDirectorships(match));
    }

    return new DirectorPortfolio(
        true, trimmedId, firstMatch.firstName(), firstMatch.surname(), allDirectorships);
  }

  private List<DirectorshipEntry> fetchDirectorships(DirectorSearchResultDto match)
      throws TransientException, PermanentException {

    DirectorResultResponseDto resultResponse = httpAdapter.post(
        ENDPOINT_DIRECTOR_RESULT,
        new DirectorResultRequestDto(
            configuration.getEnvironmentType(), newClientReference(), OUTPUT_FORMAT_JSON,
            match.enquiryId(), match.enquiryResultId()),
        DirectorResultResponseDto.class);

    if (!resultResponse.success() || resultResponse.result() == null
        || resultResponse.result().directorResults() == null) {
      logger.warn(
          "Datanamix director-result call succeeded at the HTTP level but returned no data "
              + "for EnquiryID={}, EnquiryResultID={}: messages={}",
          match.enquiryId(), match.enquiryResultId(), resultResponse.messages());
      return List.of();
    }

    List<ConsumerDirectorshipLinkDto> links =
        resultResponse.result().directorResults().consumerDirectorshipLink();

    List<DirectorshipEntry> entries = new ArrayList<>();
    for (ConsumerDirectorshipLinkDto link : links) {
      entries.add(new DirectorshipEntry(
          link.commercialName(),
          link.registrationNumber(),
          link.commercialStatus(),
          link.directorStatus(),
          link.directorDesignationDescription(),
          parseDateOrNull(link.appointmentDate())));
    }
    return entries;
  }

  private LocalDate parseDateOrNull(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return LocalDate.parse(value.trim());
    } catch (DateTimeParseException e) {
      logger.warn("Could not parse Datanamix AppointmentDate '{}': {}", value, e.getMessage());
      return null;
    }
  }

  private String newClientReference() {
    return "VERIGATE-" + UUID.randomUUID();
  }
}
