/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.config;

import crosscutting.environment.Environment;

/**
 * Configuration for the Datanamix API client (story 2.3).
 *
 * <p>{@code EnvironmentType} defaults to {@code SANDBOX} deliberately — a production
 * client ID/secret has not been provisioned/approved yet (see story 2.1's IaC pattern
 * for how the secret should eventually be wired: Terraform sensitive variable → SSM
 * parameter → env var, never hand-entered). Sandbox requests need no credentials at
 * all, so this configuration works out of the box for sandbox testing.
 */
public class DatanamixApiConfiguration {

  public static final String DEFAULT_BASE_URL = "https://api.datanamix.com";
  public static final String DEFAULT_ENVIRONMENT_TYPE = "SANDBOX";

  private static final String ENV_BASE_URL = "DATANAMIX_BASE_URL";
  private static final String ENV_ENVIRONMENT_TYPE = "DATANAMIX_ENVIRONMENT_TYPE";
  private static final String ENV_CLIENT_ID = "DATANAMIX_CLIENT_ID";
  private static final String ENV_CLIENT_SECRET = "DATANAMIX_CLIENT_SECRET";

  private final Environment environment;

  public DatanamixApiConfiguration(Environment environment) {
    this.environment = environment;
  }

  /**
   * Returns the Datanamix API base URL.
   */
  public String getBaseUrl() {
    String value = environment.get(ENV_BASE_URL);
    return value != null && !value.trim().isEmpty() ? value.trim() : DEFAULT_BASE_URL;
  }

  /**
   * Returns the configured environment type ({@code SANDBOX} or {@code LIVE}).
   */
  public String getEnvironmentType() {
    String value = environment.get(ENV_ENVIRONMENT_TYPE);
    return value != null && !value.trim().isEmpty()
        ? value.trim().toUpperCase()
        : DEFAULT_ENVIRONMENT_TYPE;
  }

  /**
   * Returns true when configured for the live environment, requiring OAuth.
   */
  public boolean isLive() {
    return "LIVE".equals(getEnvironmentType());
  }

  /**
   * Returns the OAuth2 client ID. Only required when {@link #isLive()}.
   */
  public String getClientId() {
    return environment.get(ENV_CLIENT_ID);
  }

  /**
   * Returns the OAuth2 client secret. Only required when {@link #isLive()}.
   */
  public String getClientSecret() {
    return environment.get(ENV_CLIENT_SECRET);
  }
}
