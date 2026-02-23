/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.secrets;

import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretVersionStageRequest;

/**
 * This class is used to retrieve secrets from AWS Secret Manager. It implements the SecretManager
 * interface.
 */
public class AwsSecretManager implements SecretManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsSecretManager.class);
  /**
   * The default version stage used in AWS Secrets manager for the latest ("current") secret
   * version.
   */
  private static final String VERSION_STAGE_AWS_CURRENT = "AWSCURRENT";

  private final SecretsManagerClient secretsManagerClient;

  /** Constructor for the AwsSecretManager class. */
  public AwsSecretManager(SecretsManagerClient secretsManagerClient) {
    this.secretsManagerClient = secretsManagerClient;
  }

  @Override
  public String getSecret(String secretName) throws PermanentException {
    return getSecret(secretName, null);
  }

  @Override
  public String getSecret(String secretName, String versionStage) throws PermanentException {
    return getSecret(secretName, null, versionStage);
  }

  @Override
  public String getSecret(String secretName, String versionId, String versionStage)
      throws PermanentException {
    LOGGER.debug("Retrieving secret from AWS Secret Manager.");

    // Default to current if version stage is not supplied
    final String resolvedVersionStage =
        (versionStage != null) ? versionStage : VERSION_STAGE_AWS_CURRENT;

    var getSecretValueRequest =
        GetSecretValueRequest.builder()
            .secretId(secretName)
            .versionId(versionId)
            .versionStage(resolvedVersionStage)
            .build();

    var getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);

    if (getSecretValueResponse.secretString() == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail("Secret not found").build());
    }

    return getSecretValueResponse.secretString();
  }

  @Override
  public void setSecret(String secretName, String secretValue) throws PermanentException {
    LOGGER.debug("Setting secret in AWS Secret Manager.");

    var putSecretValueRequest =
        PutSecretValueRequest.builder().secretId(secretName).secretString(secretValue).build();

    secretsManagerClient.putSecretValue(putSecretValueRequest);
  }

  @Override
  public void setSecret(
      String secretName, String secretValue, String versionId, Set<String> versionStages)
      throws PermanentException {
    LOGGER.debug("Setting secret in AWS Secret Manager associated to version stages: {}",
        versionStages);

    var putSecretValueRequest =
        PutSecretValueRequest.builder()
            .secretId(secretName)
            .clientRequestToken(versionId)
            .versionStages(versionStages)
            .secretString(secretValue)
            .build();

    secretsManagerClient.putSecretValue(putSecretValueRequest);
  }

  @Override
  public void promoteSecretVersionStage(String secretName, String pendingSecretVersion)
      throws PermanentException {
    LOGGER.debug("Setting secret version stage in AWS Secret Manager.");

    // Get the current secret version
    var currentSecretVersion =
        secretsManagerClient
            .getSecretValue(
                GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .versionStage(VERSION_STAGE_AWS_CURRENT)
                    .build())
            .versionId();

    if (pendingSecretVersion.equals(currentSecretVersion)) {
      LOGGER.info("The current secret version is already marked as {}.", VERSION_STAGE_AWS_CURRENT);
      return;
    }

    var updateSecretVersionStageRequest =
        UpdateSecretVersionStageRequest.builder()
            .secretId(secretName)
            .moveToVersionId(pendingSecretVersion)
            .removeFromVersionId(currentSecretVersion)
            .versionStage(VERSION_STAGE_AWS_CURRENT)
            .build();

    secretsManagerClient.updateSecretVersionStage(updateSecretVersionStageRequest);
  }
}
