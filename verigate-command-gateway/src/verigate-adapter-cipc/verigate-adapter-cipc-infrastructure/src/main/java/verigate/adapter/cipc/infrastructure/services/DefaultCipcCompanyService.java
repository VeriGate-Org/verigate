/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.CompanyProfileRequest;
import verigate.adapter.cipc.domain.models.CompanyProfileResponse;
import verigate.adapter.cipc.domain.services.CipcCompanyService;
import verigate.adapter.cipc.infrastructure.http.CipcCompanyApiAdapter;
import verigate.adapter.cipc.infrastructure.http.dto.CipcCompanyProfileResponseDto;
import verigate.adapter.cipc.infrastructure.mappers.CipcDtoMapper;

/**
 * Default implementation of the {@link CipcCompanyService} using the infrastructure HTTP adapter.
 */
public class DefaultCipcCompanyService implements CipcCompanyService {

  private static final Logger logger = LoggerFactory.getLogger(DefaultCipcCompanyService.class);

  private final CipcCompanyApiAdapter apiAdapter;
  private final CipcDtoMapper dtoMapper;

  public DefaultCipcCompanyService(CipcCompanyApiAdapter apiAdapter, CipcDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public CompanyProfileResponse getCompanyProfile(CompanyProfileRequest request) {
    return retrieveCompanyData(
        request,
        "company profile",
        apiAdapter::getCompanyProfile,
        "Failed to retrieve company profile");
  }

  @Override
  public CompanyProfileResponse getCompanyInformation(CompanyProfileRequest request) {
    return retrieveCompanyData(
        request,
        "basic company information",
        apiAdapter::getCompanyInformation,
        "Failed to retrieve company information");
  }

  private CompanyProfileResponse retrieveCompanyData(
      CompanyProfileRequest request,
      String description,
      Function<String, CipcCompanyProfileResponseDto> apiCall,
      String errorPrefix) {
    String enterpriseNumber = request.enterpriseNumber();
    logger.info("Retrieving {} for enterprise number: {}", description, enterpriseNumber);

    try {
      CipcCompanyProfileResponseDto responseDto = apiCall.apply(enterpriseNumber);

      if (responseDto.company() == null || responseDto.company().isEmpty()) {
        logger.info("No company found for enterprise number: {}", enterpriseNumber);
        return CompanyProfileResponse.notFound();
      }

      CompanyProfile companyProfile = dtoMapper.mapToCompanyProfile(responseDto.company().get(0));

      logger.info(
          "Successfully retrieved {} for {} - {}",
          description,
          enterpriseNumber,
          companyProfile.enterpriseName());
      return CompanyProfileResponse.success(companyProfile);

    } catch (PermanentException e) {
      if (e.getMessage() != null && e.getMessage().contains("not found")) {
        logger.info("Company not found for enterprise number: {}", enterpriseNumber);
        return CompanyProfileResponse.notFound();
      }

      logger.error(
          "Permanent error retrieving {} for {}: {}",
          description,
          enterpriseNumber,
          e.getMessage());
      return CompanyProfileResponse.error(errorPrefix + ": " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error retrieving {} for {}: {}",
          description,
          enterpriseNumber,
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error retrieving {} for {}: {}",
          description,
          enterpriseNumber,
          e.getMessage(),
          e);
      return CompanyProfileResponse.error("Unexpected error: " + e.getMessage());
    }
  }
}
