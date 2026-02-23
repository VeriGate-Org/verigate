/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for AWS Cognito JWT validation.
 * <p>
 * When {@code enabled} is {@code false} (the default), the Cognito JWT
 * authentication filter is a no-op and all authentication falls through
 * to the API key filter.
 */
@Configuration
@ConfigurationProperties(prefix = "verigate.auth.cognito")
public class CognitoJwtConfig {

  private String userPoolId;
  private String region = "af-south-1";
  private String appClientId;
  private boolean enabled = false;

  public String getUserPoolId() {
    return userPoolId;
  }

  public void setUserPoolId(String userPoolId) {
    this.userPoolId = userPoolId;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getAppClientId() {
    return appClientId;
  }

  public void setAppClientId(String appClientId) {
    this.appClientId = appClientId;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Constructs the JWKS URL for the configured Cognito User Pool.
   *
   * @return the JWKS URL, e.g.
   *   {@code https://cognito-idp.af-south-1.amazonaws.com/af-south-1_abc123/.well-known/jwks.json}
   */
  public String jwksUrl() {
    return String.format(
        "https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json",
        region, userPoolId);
  }

  /**
   * Returns the expected issuer claim ({@code iss}) value for tokens
   * issued by the configured Cognito User Pool.
   *
   * @return the issuer URL
   */
  public String issuerUrl() {
    return String.format(
        "https://cognito-idp.%s.amazonaws.com/%s",
        region, userPoolId);
  }
}
