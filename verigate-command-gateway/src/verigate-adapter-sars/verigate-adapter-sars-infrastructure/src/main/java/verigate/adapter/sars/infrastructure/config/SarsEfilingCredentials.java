/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import infrastructure.secrets.SecretManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads SARS eFiling credentials from AWS Secrets Manager.
 *
 * <p>The secret is expected to be a JSON object with keys {@code login_name}
 * and {@code password}. Credentials are lazily loaded and cached.</p>
 */
public class SarsEfilingCredentials {

  private static final Logger logger = LoggerFactory.getLogger(SarsEfilingCredentials.class);

  private final SecretManager secretManager;
  private final String secretName;

  private volatile String loginName;
  private volatile String password;
  private volatile boolean loaded;

  public SarsEfilingCredentials(SecretManager secretManager, String secretName) {
    this.secretManager = secretManager;
    this.secretName = secretName;
  }

  public String getLoginName() {
    ensureLoaded();
    return loginName;
  }

  public String getPassword() {
    ensureLoaded();
    return password;
  }

  /**
   * Returns a masked version of the login name for safe logging.
   */
  public String getMaskedLoginName() {
    String name = getLoginName();
    if (name == null || name.length() <= 3) {
      return "***";
    }
    return name.substring(0, 3) + "***";
  }

  private synchronized void ensureLoaded() {
    if (loaded) {
      return;
    }
    try {
      logger.info("Loading SARS eFiling credentials from secret: {}", secretName);
      String secretJson = secretManager.getSecret(secretName);

      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(secretJson);

      this.loginName = node.has("login_name") ? node.get("login_name").asText() : null;
      this.password = node.has("password") ? node.get("password").asText() : null;

      if (this.loginName == null || this.password == null) {
        throw new IllegalStateException(
            "SARS eFiling secret missing required fields: login_name, password");
      }

      this.loaded = true;
      logger.info("SARS eFiling credentials loaded for user: {}", getMaskedLoginName());

    } catch (IllegalStateException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to load SARS eFiling credentials from secret: " + secretName, e);
    }
  }
}
