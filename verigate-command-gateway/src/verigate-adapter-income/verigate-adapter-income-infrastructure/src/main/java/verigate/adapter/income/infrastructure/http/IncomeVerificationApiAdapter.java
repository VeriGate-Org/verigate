/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.income.domain.constants.DomainConstants;
import verigate.adapter.income.infrastructure.dtos.IncomeVerificationRequestDto;
import verigate.adapter.income.infrastructure.dtos.IncomeVerificationResponseDto;

/**
 * API adapter for calling the Income Verification endpoints.
 *
 * <p>This adapter handles the communication with the external income verification API,
 * including payslip cross-referencing, bank statement analysis, and employer confirmation
 * endpoints. It may reference the Document Verification service (P2.4) for OCR-based
 * document parsing.
 */
public class IncomeVerificationApiAdapter {

  private static final Logger logger =
      LoggerFactory.getLogger(IncomeVerificationApiAdapter.class);

  private final IncomeHttpAdapter httpAdapter;

  public IncomeVerificationApiAdapter(IncomeHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Verifies income details for the supplied request parameters.
   *
   * @param request the income verification request DTO
   * @return the income verification response DTO
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  public IncomeVerificationResponseDto verifyIncome(IncomeVerificationRequestDto request)
      throws TransientException, PermanentException {

    validateRequest(request);

    logger.debug(
        "Requesting income verification for ID: ****{}, declared income: {}",
        maskIdNumber(request.idNumber()),
        request.declaredMonthlyIncome());

    try {
      IncomeVerificationResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_VERIFY_INCOME,
              request,
              IncomeVerificationResponseDto.class);

      logger.debug(
          "Successfully retrieved income verification for ID: ****{} - status: {}",
          maskIdNumber(request.idNumber()),
          response.status());
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to verify income for ID ****{}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;
    }
  }

  private void validateRequest(IncomeVerificationRequestDto request) {
    if (request == null) {
      throw new IllegalArgumentException("Income verification request cannot be null");
    }

    if (request.idNumber() == null || request.idNumber().trim().isEmpty()) {
      throw new IllegalArgumentException("ID number cannot be null or empty");
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
