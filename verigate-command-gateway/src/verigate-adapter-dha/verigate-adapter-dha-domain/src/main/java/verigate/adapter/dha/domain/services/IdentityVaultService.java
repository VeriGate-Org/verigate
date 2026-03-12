/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.services;

import java.util.Optional;
import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.models.VerifiedIdentity;

/**
 * Service interface for the identity vault — caches DHA verification results
 * to avoid duplicate R10/lookup calls for the same individual.
 */
public interface IdentityVaultService {

  /**
   * Looks up a cached verification result by ID number and partner.
   *
   * @param idNumber  the SA ID number
   * @param partnerId the partner performing the verification
   * @return cached result if found and still fresh
   */
  Optional<VerifiedIdentity> findVerifiedIdentity(String idNumber, String partnerId);

  /**
   * Stores a verified identity result in the vault.
   *
   * @param request        the original verification request
   * @param response       the DHA verification response
   * @param partnerId      the partner who performed the verification
   * @param verificationId the unique verification ID
   */
  void storeVerifiedIdentity(
      IdentityVerificationRequest request,
      IdentityVerificationResponse response,
      String partnerId,
      String verificationId);
}
