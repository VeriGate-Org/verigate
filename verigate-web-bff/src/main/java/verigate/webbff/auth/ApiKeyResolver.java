/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

/**
 * Resolves a partner ID from a hashed API key.
 * Implementations should look up the API key hash in the partner configuration store.
 */
public interface ApiKeyResolver {

  /**
   * Resolves the partner ID associated with the given API key hash.
   *
   * @param apiKeyHash SHA-256 hash of the API key
   * @return partnerId if the key is valid, null otherwise
   */
  String resolvePartnerId(String apiKeyHash);
}
