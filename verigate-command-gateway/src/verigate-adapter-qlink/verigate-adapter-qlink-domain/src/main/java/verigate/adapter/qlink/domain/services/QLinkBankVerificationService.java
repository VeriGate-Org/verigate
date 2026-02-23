/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.domain.services;

import verigate.adapter.qlink.domain.models.BankVerificationRequest;
import verigate.adapter.qlink.domain.models.BankVerificationResponse;

/**
 * Service interface for QLink bank account verification operations.
 * Provides methods to verify bank account details via the QLink API.
 */
public interface QLinkBankVerificationService {

  /**
   * Verifies bank account details using the QLink bank verification API.
   *
   * @param request the bank verification request containing account details
   * @return the bank verification response with verification results
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  BankVerificationResponse verifyBankAccount(BankVerificationRequest request);
}
