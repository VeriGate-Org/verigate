/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

/**
 * Resolves a partner ID from a raw API key.
 * Implementations should look up the API key in the partner configuration store
 * and perform secure, constant-time verification.
 */
public interface ApiKeyResolver {

  /**
   * Resolves the partner ID associated with the given raw API key.
   * Uses constant-time comparison to prevent timing attacks.
   *
   * @param rawApiKey the raw API key from the X-API-Key header
   * @return partnerId if the key is valid, null otherwise
   */
  String resolvePartnerId(String rawApiKey);
}
