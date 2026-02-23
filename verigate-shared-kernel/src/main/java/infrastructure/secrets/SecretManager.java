/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.secrets;

import domain.exceptions.PermanentException;
import java.util.Set;

/**
 * Interface for a secret manager. This is used to retrieve secrets from a secret store.
 * Implementations of this interface should be able to retrieve secrets from a secret store.
 * The secret store could be a file, a key vault, or any other secret store.
 */
public interface SecretManager {
  /**
   * Retrieve a secret from the secret store.
   *
   * @param secretName The name of the secret to retrieve.
   * @return The value of the secret.
   */
  String getSecret(String secretName) throws PermanentException;

  /**
   * Retrieve a secret from the secret store.
   *
   * @param secretName The name of the secret to retrieve.
   * @param versionStage The version stage of the secret to retrieve.
   * @return The value of the secret.
   */
  String getSecret(String secretName, String versionStage) throws PermanentException;

  /**
   * Retrieve a secret from the secret store.
   *
   * @param secretName The name of the secret to retrieve.
   * @param versionId The version of the secret to retrieve.
   * @param versionStage The version stage of the secret to retrieve.
   * @return The value of the secret.
   */
  String getSecret(String secretName, String versionId, String versionStage)
      throws PermanentException;

  /**
   * Set a secret in the secret store.
   *
   * @param secretName The name of the secret to set.
   * @param secretValue The value of the secret to set.
   */
  void setSecret(String secretName, String secretValue) throws PermanentException;

  /**
   * Set a secret in the secret store.
   *
   * @param secretName The name of the secret to set.
   * @param secretValue The value of the secret to set.
   * @param versionId The version of the secret to set.
   * @param versionStage The version stage of the secret to set.
   */
  void setSecret(String secretName, String secretValue, String versionId, Set<String> versionStage)
      throws PermanentException;

  /**
   * Promote a secret version to a new stage.
   *
   * @param secretName The name of the secret to promote.
   * @param pendingSecretVersion The version of the secret to promote.
   * @throws PermanentException If an error occurs while promoting the secret version.
   *
   */
  void promoteSecretVersionStage(String secretName, String pendingSecretVersion)
      throws PermanentException;
}
