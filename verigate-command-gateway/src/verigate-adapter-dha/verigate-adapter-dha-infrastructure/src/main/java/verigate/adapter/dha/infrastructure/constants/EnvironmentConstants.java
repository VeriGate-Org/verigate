/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.constants;

/**
 * Environment variable constants for DHA adapter configuration.
 */
public class EnvironmentConstants {

  // API Authentication
  public static final String DHA_API_KEY = "DHA_API_KEY";
  public static final String DHA_BASE_URL = "DHA_BASE_URL";

  // HTTP Configuration
  public static final String DHA_HTTP_TIMEOUT_SECONDS = "DHA_HTTP_TIMEOUT_SECONDS";
  public static final String DHA_HTTP_RETRY_ATTEMPTS = "DHA_HTTP_RETRY_ATTEMPTS";
  public static final String DHA_HTTP_RETRY_DELAY_MS = "DHA_HTTP_RETRY_DELAY_MS";

  // Rate Limiting
  public static final String DHA_RATE_LIMIT_RPS = "DHA_RATE_LIMIT_RPS";
  public static final String DHA_RATE_LIMIT_BURST = "DHA_RATE_LIMIT_BURST";

  // Monitoring and Logging
  public static final String DHA_ENABLE_REQUEST_LOGGING = "DHA_ENABLE_REQUEST_LOGGING";
  public static final String DHA_ENABLE_RESPONSE_LOGGING = "DHA_ENABLE_RESPONSE_LOGGING";
  public static final String DHA_LOG_LEVEL = "DHA_LOG_LEVEL";

  // HANIS SOAP Integration
  public static final String HANIS_INTEGRATION_ENABLED = "HANIS_INTEGRATION_ENABLED";
  public static final String HANIS_SITE_ID = "HANIS_SITE_ID";
  public static final String HANIS_WORKSTATION_ID = "HANIS_WORKSTATION_ID";
  public static final String HANIS_PRIMARY_URL = "HANIS_PRIMARY_URL";
  public static final String HANIS_FAILOVER_URL = "HANIS_FAILOVER_URL";
  public static final String HANIS_TIMEOUT_SECONDS = "HANIS_TIMEOUT_SECONDS";

  // Default values (fallback URLs)
  public static final String DEFAULT_DHA_BASE_URL =
      "https://dha-api-dev.services.gov.za/identity/v1";

  private EnvironmentConstants() {
  }
}
