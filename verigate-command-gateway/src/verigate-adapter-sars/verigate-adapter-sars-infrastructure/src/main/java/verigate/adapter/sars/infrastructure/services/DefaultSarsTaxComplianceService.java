/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.sars.domain.models.TaxComplianceRequest;
import verigate.adapter.sars.domain.models.TaxComplianceResponse;
import verigate.adapter.sars.domain.services.SarsTaxComplianceService;
import verigate.adapter.sars.infrastructure.dtos.SarsTaxComplianceRequestDto;
import verigate.adapter.sars.infrastructure.dtos.SarsTaxComplianceResponseDto;
import verigate.adapter.sars.infrastructure.http.SarsTaxApiAdapter;
import verigate.adapter.sars.infrastructure.mappers.SarsDtoMapper;

/**
 * Default implementation of the {@link SarsTaxComplianceService} using the infrastructure
 * HTTP adapter to communicate with the SARS eFiling API.
 */
public class DefaultSarsTaxComplianceService implements SarsTaxComplianceService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultSarsTaxComplianceService.class);

  private final SarsTaxApiAdapter apiAdapter;
  private final SarsDtoMapper dtoMapper;

  /**
   * Creates a new default SARS tax compliance service.
   *
   * @param apiAdapter the SARS Tax API adapter for making HTTP calls
   * @param dtoMapper the DTO mapper for converting between domain and infrastructure models
   */
  public DefaultSarsTaxComplianceService(
      SarsTaxApiAdapter apiAdapter, SarsDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public TaxComplianceResponse verifyTaxCompliance(TaxComplianceRequest request) {

    logger.info(
        "Verifying SARS tax compliance for tax ref: ****{}",
        maskTaxReference(request.taxReferenceNumber()));

    try {
      SarsTaxComplianceRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      SarsTaxComplianceResponseDto responseDto = apiAdapter.verifyTaxCompliance(requestDto);

      TaxComplianceResponse response = dtoMapper.mapToResponse(responseDto);

      logger.info(
          "Successfully verified SARS tax compliance for tax ref: ****{} - status: {}",
          maskTaxReference(request.taxReferenceNumber()),
          response.status());
      return response;

    } catch (PermanentException e) {
      if (e.getMessage() != null && e.getMessage().contains("not found")) {
        logger.info(
            "Taxpayer record not found for tax ref: ****{}",
            maskTaxReference(request.taxReferenceNumber()));
        return TaxComplianceResponse.notFound();
      }

      logger.error(
          "Permanent error verifying SARS tax compliance for tax ref ****{}: {}",
          maskTaxReference(request.taxReferenceNumber()),
          e.getMessage());
      return TaxComplianceResponse.error(
          "Failed to verify tax compliance: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error verifying SARS tax compliance for tax ref ****{}: {}",
          maskTaxReference(request.taxReferenceNumber()),
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error verifying SARS tax compliance for tax ref ****{}: {}",
          maskTaxReference(request.taxReferenceNumber()),
          e.getMessage(),
          e);
      return TaxComplianceResponse.error("Unexpected error: " + e.getMessage());
    }
  }

  /**
   * Masks a tax reference number for safe logging, showing only the last 4 digits.
   *
   * @param taxReference the tax reference number to mask
   * @return the masked tax reference number
   */
  private String maskTaxReference(String taxReference) {
    if (taxReference == null || taxReference.length() <= 4) {
      return "****";
    }
    return taxReference.substring(taxReference.length() - 4);
  }
}
