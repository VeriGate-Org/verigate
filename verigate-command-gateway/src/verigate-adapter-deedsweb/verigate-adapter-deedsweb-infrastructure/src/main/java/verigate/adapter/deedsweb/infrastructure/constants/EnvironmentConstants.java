/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.constants;

/**
 * Environment variable constants for OpenSanctions adapter.
 */
public class EnvironmentConstants {

  // OpenSanctions API configuration
  public static final String OPENSANCTIONS_API_KEY = "OPENSANCTIONS_API_KEY";
  public static final String OPENSANCTIONS_BASE_URL = "OPENSANCTIONS_BASE_URL";

  // HTTP client configuration
  public static final String OPENSANCTIONS_CONNECTION_TIMEOUT_MS =
      "OPENSANCTIONS_CONNECTION_TIMEOUT_MS";
  public static final String OPENSANCTIONS_READ_TIMEOUT_MS = "OPENSANCTIONS_READ_TIMEOUT_MS";
  public static final String OPENSANCTIONS_RETRY_ATTEMPTS = "OPENSANCTIONS_RETRY_ATTEMPTS";
  public static final String OPENSANCTIONS_RETRY_DELAY_MS = "OPENSANCTIONS_RETRY_DELAY_MS";

  // Default values
  public static final String DEFAULT_BASE_URL = "https://api.opensanctions.org";
  public static final String DEFAULT_CONNECTION_TIMEOUT_MS = "30000";
  public static final String DEFAULT_READ_TIMEOUT_MS = "60000";
  public static final String DEFAULT_RETRY_ATTEMPTS = "3";
  public static final String DEFAULT_RETRY_DELAY_MS = "1000";

  private EnvironmentConstants() {
    // Utility class
  }
}
