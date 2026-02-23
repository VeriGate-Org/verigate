/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.constants;

/**
 * Environment variable constants for CIPC adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String CIPC_API_KEY = "CIPC_API_KEY";
  public static final String CIPC_BASE_URL = "CIPC_BASE_URL";

  // HTTP Configuration
  public static final String CIPC_HTTP_TIMEOUT_SECONDS = "CIPC_HTTP_TIMEOUT_SECONDS";
  public static final String CIPC_HTTP_RETRY_ATTEMPTS = "CIPC_HTTP_RETRY_ATTEMPTS";
  public static final String CIPC_HTTP_RETRY_DELAY_MS = "CIPC_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String CIPC_RATE_LIMIT_RPS = "CIPC_RATE_LIMIT_RPS";
  public static final String CIPC_RATE_LIMIT_BURST = "CIPC_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String CIPC_ENABLE_REQUEST_LOGGING = "CIPC_ENABLE_REQUEST_LOGGING";
  public static final String CIPC_ENABLE_RESPONSE_LOGGING = "CIPC_ENABLE_RESPONSE_LOGGING";
  public static final String CIPC_LOG_LEVEL = "CIPC_LOG_LEVEL";

  // Default values (fallback URLs)
  public static final String DEFAULT_CIPC_BASE_URL = "https://cipc-apm-rs-dev.azure-api.net/enterprise/v1";

  private EnvironmentConstants() {
  }
}
