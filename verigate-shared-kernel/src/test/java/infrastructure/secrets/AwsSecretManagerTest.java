/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.secrets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.exceptions.PermanentException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretVersionStageRequest;

class AwsSecretManagerTest {
  private static final String AWS_CURRENT = "AWSCURRENT";
  private static final String TEST_STAGE = "TEST_STAGE";
  private static final String TEST_VERSION_ID = UUID.randomUUID().toString();
  private static final String EXISTING_SECRET_NAME = "secretName";
  private static final String EXISTING_SECRET_VALUE = "secretValue";

  private SecretsManagerClient mockSecretsManagerClient;
  private AwsSecretManager awsSecretManager;

  @BeforeEach
  public void setup() {
    this.mockSecretsManagerClient = mock(SecretsManagerClient.class);
    this.awsSecretManager = new AwsSecretManager(mockSecretsManagerClient);
  }

  @Test
  public void getSecretByNameExists() {
    when(mockSecretsManagerClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(
        GetSecretValueResponse.builder().secretString(EXISTING_SECRET_VALUE).build());

    final String actualSecret = awsSecretManager.getSecret(EXISTING_SECRET_NAME);

    final ArgumentCaptor<GetSecretValueRequest> captor = ArgumentCaptor.forClass(GetSecretValueRequest.class);
    verify(mockSecretsManagerClient).getSecretValue(captor.capture());
    final GetSecretValueRequest getSecretValueRequest = captor.getValue();
    assertEquals(EXISTING_SECRET_NAME, getSecretValueRequest.secretId());
    assertNull(getSecretValueRequest.versionId());
    assertEquals(AWS_CURRENT, getSecretValueRequest.versionStage());
    assertEquals(EXISTING_SECRET_VALUE, actualSecret);
  }

  @Test
  public void getSecretByNameDoesNotExist() {
    when(mockSecretsManagerClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(
        GetSecretValueResponse.builder().secretString(null).build());

    assertThrows(PermanentException.class, () -> awsSecretManager.getSecret("non-existent-secret"));
  }

  @Test
  public void getSecretByNameAndStage() {
    when(mockSecretsManagerClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(
        GetSecretValueResponse.builder().secretString(EXISTING_SECRET_VALUE).build());

    final String actualSecret = awsSecretManager.getSecret(EXISTING_SECRET_NAME, TEST_STAGE);
    final ArgumentCaptor<GetSecretValueRequest> captor = ArgumentCaptor.forClass(GetSecretValueRequest.class);
    verify(mockSecretsManagerClient).getSecretValue(captor.capture());
    final GetSecretValueRequest getSecretValueRequest = captor.getValue();
    assertEquals(EXISTING_SECRET_NAME, getSecretValueRequest.secretId());
    assertNull(getSecretValueRequest.versionId());
    assertEquals(TEST_STAGE, getSecretValueRequest.versionStage());
    assertEquals(EXISTING_SECRET_VALUE, actualSecret);
  }

  @Test
  public void getSecretByNameIdAndStage() {
    when(mockSecretsManagerClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(
        GetSecretValueResponse.builder().secretString(EXISTING_SECRET_VALUE).build());

    final String actualSecret = awsSecretManager.getSecret(EXISTING_SECRET_NAME, TEST_VERSION_ID, TEST_STAGE);
    final ArgumentCaptor<GetSecretValueRequest> captor = ArgumentCaptor.forClass(GetSecretValueRequest.class);
    verify(mockSecretsManagerClient).getSecretValue(captor.capture());
    final GetSecretValueRequest getSecretValueRequest = captor.getValue();
    assertEquals(EXISTING_SECRET_NAME, getSecretValueRequest.secretId());
    assertEquals(TEST_VERSION_ID, getSecretValueRequest.versionId());
    assertEquals(TEST_STAGE, getSecretValueRequest.versionStage());
    assertEquals(EXISTING_SECRET_VALUE, actualSecret);
  }

  @Test
  public void setSecretByName() {
    awsSecretManager.setSecret(EXISTING_SECRET_NAME, EXISTING_SECRET_VALUE);
    final ArgumentCaptor<PutSecretValueRequest> captor = ArgumentCaptor.forClass(PutSecretValueRequest.class);
    verify(mockSecretsManagerClient).putSecretValue(captor.capture());
    final PutSecretValueRequest putSecretValueRequest = captor.getValue();
    assertEquals(EXISTING_SECRET_NAME, putSecretValueRequest.secretId());
    assertEquals(EXISTING_SECRET_VALUE, putSecretValueRequest.secretString());
    assertNull(putSecretValueRequest.clientRequestToken());
    assertTrue(putSecretValueRequest.versionStages().isEmpty());
  }

  @Test
  public void setSecretByNameIdAndStage() {
    awsSecretManager.setSecret(EXISTING_SECRET_NAME, EXISTING_SECRET_VALUE, TEST_VERSION_ID, Set.of(TEST_STAGE));
    final ArgumentCaptor<PutSecretValueRequest> captor = ArgumentCaptor.forClass(PutSecretValueRequest.class);
    verify(mockSecretsManagerClient).putSecretValue(captor.capture());
    final PutSecretValueRequest putSecretValueRequest = captor.getValue();
    assertEquals(EXISTING_SECRET_NAME, putSecretValueRequest.secretId());
    assertEquals(EXISTING_SECRET_VALUE, putSecretValueRequest.secretString());
    assertEquals(TEST_VERSION_ID, putSecretValueRequest.clientRequestToken());
    assertEquals(List.of(TEST_STAGE), putSecretValueRequest.versionStages());
  }

  @Test
  public void promoteNonCurrentSecretVersionStage() {
    final String currentVersionId = "current-version-id";
    final String pendingVersionId = "pending-version-id";

    when(mockSecretsManagerClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(
        GetSecretValueResponse.builder().secretString(EXISTING_SECRET_VALUE).versionId(currentVersionId).build());

    awsSecretManager.promoteSecretVersionStage(EXISTING_SECRET_NAME, pendingVersionId);
    final ArgumentCaptor<UpdateSecretVersionStageRequest> captor = ArgumentCaptor.forClass(UpdateSecretVersionStageRequest.class);
    verify(mockSecretsManagerClient).updateSecretVersionStage(captor.capture());
    final UpdateSecretVersionStageRequest updateSecretVersionStageRequest = captor.getValue();
    assertEquals(EXISTING_SECRET_NAME, updateSecretVersionStageRequest.secretId());
    assertEquals(pendingVersionId, updateSecretVersionStageRequest.moveToVersionId());
    assertEquals(currentVersionId, updateSecretVersionStageRequest.removeFromVersionId());
    assertEquals(AWS_CURRENT, updateSecretVersionStageRequest.versionStage());
  }

  @Test
  public void promoteAlreadyCurrentSecretVersionStage() {
    final String currentVersionId = "current-version-id";

    when(mockSecretsManagerClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(
        GetSecretValueResponse.builder().secretString(EXISTING_SECRET_VALUE).versionId(currentVersionId).build());

    awsSecretManager.promoteSecretVersionStage(EXISTING_SECRET_NAME, currentVersionId);

    verify(mockSecretsManagerClient, times(0)).updateSecretVersionStage(any(UpdateSecretVersionStageRequest.class));
  }

}