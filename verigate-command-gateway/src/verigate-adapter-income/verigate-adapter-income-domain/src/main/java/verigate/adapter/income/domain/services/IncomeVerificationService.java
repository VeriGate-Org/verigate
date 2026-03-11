/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.services;

import verigate.adapter.income.domain.models.IncomeVerificationRequest;
import verigate.adapter.income.domain.models.IncomeVerificationResponse;

/**
 * Service interface for income verification operations.
 * Provides methods to verify income details by cross-referencing payslip data,
 * bank statement analysis, and employer confirmation.
 */
public interface IncomeVerificationService {

  /**
   * Verifies income details for the given request.
   *
   * <p>The verification process cross-references multiple data sources:
   * <ul>
   *   <li>Payslip data (via OCR-based document parsing from
   *       Document Verification service P2.4)</li>
   *   <li>Bank statement analysis (transaction pattern matching)</li>
   *   <li>Employer confirmation (direct employer verification)</li>
   * </ul>
   *
   * @param request the income verification request containing identity, employer,
   *                declared income, and bank account details
   * @return the income verification response with status, assessment, and confidence level
   * @throws domain.exceptions.TransientException if a temporary error occurs that may be retried
   * @throws domain.exceptions.PermanentException if a permanent error occurs that should not
   *                                               be retried
   */
  IncomeVerificationResponse verifyIncome(IncomeVerificationRequest request);
}
