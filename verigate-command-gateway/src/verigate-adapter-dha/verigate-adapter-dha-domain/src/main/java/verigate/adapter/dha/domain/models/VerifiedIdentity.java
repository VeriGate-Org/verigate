/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents a cached identity verification result in the identity vault.
 *
 * @param identityHash   SHA-256 hash of the ID number (partition key)
 * @param partnerId      partner who performed the verification (sort key)
 * @param verificationStatus the DHA verification status
 * @param citizenshipStatus  citizenship status from DHA
 * @param vitalStatus        vital status from DHA
 * @param matchDetails       match details from DHA
 * @param verifiedAt         when the verification was performed
 * @param expiresAt          TTL epoch seconds for DynamoDB auto-cleanup
 */
public record VerifiedIdentity(
    String identityHash,
    String partnerId,
    String verificationStatus,
    String citizenshipStatus,
    String vitalStatus,
    String matchDetails,
    Instant verifiedAt,
    long expiresAt) {

  /**
   * Returns true if this cached result is still fresh enough for re-use.
   */
  public boolean isFreshEnough(Duration maxAge) {
    return verifiedAt != null && Instant.now().minus(maxAge).isBefore(verifiedAt);
  }
}
