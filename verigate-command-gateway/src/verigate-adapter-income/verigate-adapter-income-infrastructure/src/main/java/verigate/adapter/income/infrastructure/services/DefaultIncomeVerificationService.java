/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.income.domain.models.IncomeVerificationRequest;
import verigate.adapter.income.domain.models.IncomeVerificationResponse;
import verigate.adapter.income.domain.services.IncomeVerificationService;
import verigate.adapter.income.infrastructure.dtos.IncomeVerificationRequestDto;
import verigate.adapter.income.infrastructure.dtos.IncomeVerificationResponseDto;
import verigate.adapter.income.infrastructure.http.IncomeVerificationApiAdapter;
import verigate.adapter.income.infrastructure.mappers.IncomeDtoMapper;

/**
 * Default implementation of the {@link IncomeVerificationService} using the infrastructure
 * HTTP adapter.
 *
 * <p>This service orchestrates income verification by:
 * <ol>
 *   <li>Mapping domain requests to infrastructure DTOs</li>
 *   <li>Calling the Income Verification API via the HTTP adapter</li>
 *   <li>Mapping API responses back to domain models</li>
 * </ol>
 *
 * <p>The external income verification API cross-references payslip data,
 * bank statement analysis, and employer confirmation. It may also reference
 * the Document Verification service (P2.4) for OCR-based document parsing.
 */
public class DefaultIncomeVerificationService implements IncomeVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultIncomeVerificationService.class);

  private final IncomeVerificationApiAdapter apiAdapter;
  private final IncomeDtoMapper dtoMapper;

  public DefaultIncomeVerificationService(
      IncomeVerificationApiAdapter apiAdapter, IncomeDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public IncomeVerificationResponse verifyIncome(IncomeVerificationRequest request) {

    logger.info(
        "Verifying income for ID: ****{}, declared income: {}, source: {}",
        maskIdNumber(request.idNumber()),
        request.declaredMonthlyIncome(),
        request.sourceType());

    try {
      IncomeVerificationRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      IncomeVerificationResponseDto responseDto = apiAdapter.verifyIncome(requestDto);

      IncomeVerificationResponse response = dtoMapper.mapToResponse(responseDto);

      logger.info(
          "Successfully verified income for ID: ****{} - status: {}, confidence: {}",
          maskIdNumber(request.idNumber()),
          response.status(),
          response.assessment() != null ? response.assessment().confidence() : "N/A");
      return response;

    } catch (PermanentException e) {
      if (e.getMessage() != null && e.getMessage().contains("not found")) {
        logger.info(
            "Income record not found for ID: ****{}",
            maskIdNumber(request.idNumber()));
        return IncomeVerificationResponse.unverifiable(
            request.declaredMonthlyIncome(),
            "No income record found for the provided details");
      }

      logger.error(
          "Permanent error verifying income for ID ****{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      return IncomeVerificationResponse.error(
          request.declaredMonthlyIncome(),
          "Failed to verify income: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error verifying income for ID ****{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error verifying income for ID ****{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage(),
          e);
      return IncomeVerificationResponse.error(
          request.declaredMonthlyIncome(),
          "Unexpected error: " + e.getMessage());
    }
  }

  /**
   * Masks an ID number for safe logging, showing only the last 4 digits.
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() <= 4) {
      return "****";
    }
    return idNumber.substring(idNumber.length() - 4);
  }
}
