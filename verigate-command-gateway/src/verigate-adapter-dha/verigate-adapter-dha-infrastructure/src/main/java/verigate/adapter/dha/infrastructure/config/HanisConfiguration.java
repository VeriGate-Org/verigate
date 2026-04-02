/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.config;

import crosscutting.environment.Environment;
import verigate.adapter.dha.infrastructure.constants.EnvironmentConstants;

/**
 * Configuration for the HANIS SOAP integration.
 * Reads values from environment variables.
 */
public class HanisConfiguration {

  private static final String DEFAULT_PRIMARY_URL =
      "https://hanisrvs1.hanis.gov.za/PersonData_4/HANISNPRRequest.asmx";
  private static final int DEFAULT_TIMEOUT_SECONDS = 30;

  private final Environment environment;

  public HanisConfiguration(Environment environment) {
    this.environment = environment;
  }

  public boolean isEnabled() {
    return Boolean.parseBoolean(getOrDefault(EnvironmentConstants.HANIS_INTEGRATION_ENABLED, "false"));
  }

  public String getSiteId() {
    return getOrDefault(EnvironmentConstants.HANIS_SITE_ID, "");
  }

  public String getWorkstationId() {
    return getOrDefault(EnvironmentConstants.HANIS_WORKSTATION_ID, "");
  }

  public String getPrimaryUrl() {
    return getOrDefault(EnvironmentConstants.HANIS_PRIMARY_URL, DEFAULT_PRIMARY_URL);
  }

  public String getFailoverUrl() {
    return getOrDefault(EnvironmentConstants.HANIS_FAILOVER_URL, "");
  }

  public int getTimeoutSeconds() {
    try {
      return Integer.parseInt(
          getOrDefault(EnvironmentConstants.HANIS_TIMEOUT_SECONDS,
              String.valueOf(DEFAULT_TIMEOUT_SECONDS)));
    } catch (NumberFormatException e) {
      return DEFAULT_TIMEOUT_SECONDS;
    }
  }

  /**
   * Validates that required configuration is present when HANIS integration is enabled.
   *
   * @throws IllegalStateException if required configuration is missing
   */
  public void validate() {
    if (!isEnabled()) {
      return;
    }

    if (getSiteId().isBlank()) {
      throw new IllegalStateException(
          "HANIS_SITE_ID must be set when HANIS integration is enabled");
    }
    if (getWorkstationId().isBlank()) {
      throw new IllegalStateException(
          "HANIS_WORKSTATION_ID must be set when HANIS integration is enabled");
    }
  }

  private String getOrDefault(String key, String defaultValue) {
    String value = environment.get(key);
    return (value != null && !value.trim().isEmpty()) ? value.trim() : defaultValue;
  }
}
