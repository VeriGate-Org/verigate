/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.secrets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.secrets.SecretManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import verigate.adapter.deedsweb.domain.models.DeedsWebCredentials;
import verigate.adapter.deedsweb.domain.services.DeedsWebCredentialsProvider;
import verigate.adapter.deedsweb.infrastructure.config.DeedsWebApiConfiguration;

/**
 * {@link DeedsWebCredentialsProvider} backed by AWS Secrets Manager. Fetches the configured
 * secret on every call (no caching) and parses its JSON payload of the form
 * {@code {"username":"...","password":"..."}}.
 */
public class SecretsManagerDeedsWebCredentialsProvider implements DeedsWebCredentialsProvider {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SecretsManagerDeedsWebCredentialsProvider.class);

  private static final String USERNAME_FIELD = "username";
  private static final String PASSWORD_FIELD = "password";

  private final SecretManager secretManager;
  private final ObjectMapper objectMapper;
  private final DeedsWebApiConfiguration configuration;

  public SecretsManagerDeedsWebCredentialsProvider(
      SecretManager secretManager,
      ObjectMapper objectMapper,
      DeedsWebApiConfiguration configuration) {
    this.secretManager = secretManager;
    this.objectMapper = objectMapper;
    this.configuration = configuration;
  }

  @Override
  public DeedsWebCredentials get() throws TransientException, PermanentException {
    String secretName = configuration.getCredentialsSecretName();
    if (secretName == null || secretName.isBlank()) {
      throw new PermanentException(
          "DeedsWeb credentials secret name is not configured");
    }

    String secretJson;
    try {
      secretJson = secretManager.getSecret(secretName);
    } catch (SecretsManagerException e) {
      LOGGER.warn("Transient failure while fetching DeedsWeb credentials secret '{}'", secretName);
      throw new TransientException(
          "Failed to fetch DeedsWeb credentials from Secrets Manager: " + e.getMessage(), e);
    }

    if (secretJson == null || secretJson.isBlank()) {
      throw new PermanentException(
          "DeedsWeb credentials secret '" + secretName + "' is empty");
    }

    JsonNode node;
    try {
      node = objectMapper.readTree(secretJson);
    } catch (Exception e) {
      throw new PermanentException(
          "DeedsWeb credentials secret '" + secretName + "' is not valid JSON", e);
    }

    String username = textOrNull(node, USERNAME_FIELD);
    String password = textOrNull(node, PASSWORD_FIELD);

    if (username == null || username.isBlank()) {
      throw new PermanentException(
          "DeedsWeb credentials secret '" + secretName + "' is missing the 'username' field");
    }
    if (password == null || password.isBlank()) {
      throw new PermanentException(
          "DeedsWeb credentials secret '" + secretName + "' is missing the 'password' field");
    }

    return new DeedsWebCredentials(username, password);
  }

  private static String textOrNull(JsonNode root, String field) {
    JsonNode child = root.get(field);
    return (child == null || child.isNull()) ? null : child.asText();
  }
}
