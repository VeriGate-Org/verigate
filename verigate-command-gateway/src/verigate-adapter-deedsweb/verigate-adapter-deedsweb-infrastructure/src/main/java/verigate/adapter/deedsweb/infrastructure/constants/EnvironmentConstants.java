/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.constants;

/** Environment variable and property-key constants for the DeedsWeb adapter. */
public class EnvironmentConstants {

  // DeedsWeb environment variables
  public static final String DEEDSWEB_CREDENTIALS_SECRET_NAME = "DEEDSWEB_CREDENTIALS_SECRET_NAME";
  public static final String DEEDSWEB_BASE_URL = "DEEDSWEB_BASE_URL";
  public static final String DEEDSWEB_CONNECTION_TIMEOUT_MS = "DEEDSWEB_CONNECTION_TIMEOUT_MS";
  public static final String DEEDSWEB_READ_TIMEOUT_MS = "DEEDSWEB_READ_TIMEOUT_MS";
  public static final String DEEDSWEB_RETRY_ATTEMPTS = "DEEDSWEB_RETRY_ATTEMPTS";
  public static final String DEEDSWEB_RETRY_DELAY_MS = "DEEDSWEB_RETRY_DELAY_MS";

  // Property keys
  public static final String PROPERTY_CREDENTIALS_SECRET_NAME = "deedsweb.credentials.secret.name";
  public static final String PROPERTY_BASE_URL = "deedsweb.base.url";
  public static final String PROPERTY_CONNECTION_TIMEOUT_MS = "deedsweb.connection.timeout.ms";
  public static final String PROPERTY_READ_TIMEOUT_MS = "deedsweb.read.timeout.ms";
  public static final String PROPERTY_RETRY_ATTEMPTS = "deedsweb.retry.attempts";
  public static final String PROPERTY_RETRY_DELAY_MS = "deedsweb.retry.delay.ms";

  // Default values
  public static final String DEFAULT_BASE_URL =
      "https://deedssoap.deeds.gov.za:443/deeds-registration-soap/";
  public static final String DEFAULT_CREDENTIALS_SECRET_NAME = "verigate/deedsweb/credentials";
  public static final String DEFAULT_CONNECTION_TIMEOUT_MS = "30000";
  public static final String DEFAULT_READ_TIMEOUT_MS = "60000";
  public static final String DEFAULT_RETRY_ATTEMPTS = "3";
  public static final String DEFAULT_RETRY_DELAY_MS = "1000";

  private EnvironmentConstants() {
    // Utility class
  }
}
