/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http.cipc;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.document.domain.models.CipcCompanyLookupResult;
import verigate.adapter.document.domain.services.CipcLookupService;
import verigate.adapter.document.infrastructure.http.cipc.dto.DocumentCipcAddressDto;
import verigate.adapter.document.infrastructure.http.cipc.dto.DocumentCipcCompanyDto;
import verigate.adapter.document.infrastructure.http.cipc.dto.DocumentCipcDirectorDto;
import verigate.adapter.document.infrastructure.http.cipc.dto.DocumentCipcLookupRequestDto;
import verigate.adapter.document.infrastructure.http.cipc.dto.DocumentCipcLookupResponseDto;

/**
 * Implements the {@link CipcLookupService} port using the document adapter's own CIPC HTTP
 * client.
 */
public class DocumentCipcCompanyClient implements CipcLookupService {

  private static final Logger logger = LoggerFactory.getLogger(DocumentCipcCompanyClient.class);
  private static final String ENDPOINT_COMPANY_PROFILE = "/companyprofile";

  private final DocumentCipcHttpAdapter httpAdapter;

  public DocumentCipcCompanyClient(DocumentCipcHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  @Override
  public CipcCompanyLookupResult lookupCompany(String registrationNumber)
      throws TransientException, PermanentException {

    if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
      logger.debug("No registration number to look up");
      return CipcCompanyLookupResult.notFound(registrationNumber);
    }

    String trimmed = registrationNumber.trim();
    logger.debug("Looking up CIPC company for cross-validation: {}", trimmed);

    DocumentCipcLookupRequestDto request = new DocumentCipcLookupRequestDto(trimmed);

    DocumentCipcLookupResponseDto response;
    try {
      response = httpAdapter.post(
          ENDPOINT_COMPANY_PROFILE, request, DocumentCipcLookupResponseDto.class);
    } catch (PermanentException e) {
      // CIPC returns 404 (mapped to PermanentException) for an unknown enterprise number —
      // that is a legitimate "not found" outcome for document cross-validation, not a
      // system error, so it should not fail the whole document verification.
      logger.info("CIPC lookup found no company for {}: {}", trimmed, e.getMessage());
      return CipcCompanyLookupResult.notFound(trimmed);
    }

    if (response == null || response.company() == null || response.company().isEmpty()) {
      return CipcCompanyLookupResult.notFound(trimmed);
    }

    DocumentCipcCompanyDto company = response.company().get(0);
    return toLookupResult(company);
  }

  private CipcCompanyLookupResult toLookupResult(DocumentCipcCompanyDto company) {
    String address = company.officeAddress() != null && !company.officeAddress().isEmpty()
        ? company.officeAddress().get(0).asSingleLine()
        : null;

    List<String> directorNames = company.directors() == null
        ? List.of()
        : company.directors().stream()
            .map(DocumentCipcDirectorDto::fullName)
            .filter(name -> !name.isEmpty())
            .toList();

    return new CipcCompanyLookupResult(
        true,
        company.enterpriseNumber(),
        company.enterpriseName(),
        company.enterpriseStatusDescription(),
        company.enterpriseTypeDescription(),
        address,
        directorNames);
  }
}
