/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

import java.time.LocalDateTime;

/**
 * Immutable representation of an API key record stored in DynamoDB.
 * <p>
 * The raw API key value is never persisted. Two hashes are stored:
 * <ul>
 *   <li>{@code lookupHash}: unsalted SHA-256 hash used as partition key for efficient lookup</li>
 *   <li>{@code verificationHash}: salted SHA-256 hash used for secure verification with constant-time comparison</li>
 * </ul>
 * The {@code keyPrefix} (first 8 characters of the raw key) is kept solely
 * for human-readable identification in dashboards and logs.
 *
 * @param lookupHash       unsalted SHA-256 hex digest used as partition key
 * @param verificationHash salted SHA-256 hex digest used for secure verification
 * @param salt             base64-encoded salt used for verification hash
 * @param partnerId        the partner this key belongs to
 * @param status           lifecycle status: ACTIVE, REVOKED, or EXPIRED
 * @param keyPrefix        first 8 characters of the raw key for display purposes
 * @param createdAt        when the key was created
 * @param expiresAt        optional expiry timestamp; {@code null} means no expiry
 * @param createdBy        identifier of the user/system that created this key
 */
public record ApiKeyRecord(
    String lookupHash,
    String verificationHash,
    String salt,
    String partnerId,
    String status,
    String keyPrefix,
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    String createdBy
) {

  /** Status value indicating the key is active and can be used for authentication. */
  public static final String STATUS_ACTIVE = "ACTIVE";

  /** Status value indicating the key has been explicitly revoked. */
  public static final String STATUS_REVOKED = "REVOKED";

  /** Status value indicating the key has passed its expiry timestamp. */
  public static final String STATUS_EXPIRED = "EXPIRED";

  /**
   * Returns {@code true} if this key is currently usable for authentication.
   */
  public boolean isActive() {
    return STATUS_ACTIVE.equals(status);
  }
}
