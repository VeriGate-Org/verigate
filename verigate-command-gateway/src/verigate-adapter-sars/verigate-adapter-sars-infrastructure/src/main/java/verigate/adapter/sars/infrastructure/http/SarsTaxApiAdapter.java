/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.sars.domain.constants.DomainConstants;
import verigate.adapter.sars.infrastructure.dtos.SarsTaxComplianceRequestDto;
import verigate.adapter.sars.infrastructure.dtos.SarsTaxComplianceResponseDto;

/**
 * API adapter for calling the SARS Tax Compliance verification endpoints.
 */
public class SarsTaxApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(SarsTaxApiAdapter.class);

  private final SarsHttpAdapter httpAdapter;

  /**
   * Creates a new SARS Tax API adapter.
   *
   * @param httpAdapter the base HTTP adapter for making API calls
   */
  public SarsTaxApiAdapter(SarsHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Verifies tax compliance status for the supplied request parameters.
   *
   * @param request the tax compliance request DTO
   * @return the tax compliance response DTO
   * @throws TransientException if a retriable error occurs
   * @throws PermanentException if a non-retriable error occurs
   */
  public SarsTaxComplianceResponseDto verifyTaxCompliance(
      SarsTaxComplianceRequestDto request)
      throws TransientException, PermanentException {

    validateRequest(request);

    logger.debug(
        "Requesting SARS tax compliance verification for tax ref: ****{}",
        maskTaxReference(request.taxReferenceNumber()));

    try {
      SarsTaxComplianceResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_VERIFY_TAX_COMPLIANCE,
              request,
              SarsTaxComplianceResponseDto.class);

      logger.debug(
          "Successfully retrieved SARS tax compliance verification for tax ref: ****{}",
          maskTaxReference(request.taxReferenceNumber()));
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to verify SARS tax compliance for tax ref ****{}: {}",
          maskTaxReference(request.taxReferenceNumber()),
          e.getMessage());
      throw e;
    }
  }

  private void validateRequest(SarsTaxComplianceRequestDto request) {
    if (request == null) {
      throw new IllegalArgumentException("SARS tax compliance request cannot be null");
    }

    boolean hasIdNumber = request.idNumber() != null && !request.idNumber().trim().isEmpty();
    boolean hasTaxRef = request.taxReferenceNumber() != null
        && !request.taxReferenceNumber().trim().isEmpty();

    if (!hasIdNumber && !hasTaxRef) {
      throw new IllegalArgumentException(
          "Either ID number or tax reference number is required");
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
