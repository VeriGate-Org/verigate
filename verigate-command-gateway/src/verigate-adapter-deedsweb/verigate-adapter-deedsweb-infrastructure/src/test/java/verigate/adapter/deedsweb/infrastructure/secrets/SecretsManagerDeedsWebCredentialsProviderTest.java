/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.secrets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.secrets.SecretManager;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import verigate.adapter.deedsweb.domain.models.DeedsWebCredentials;
import verigate.adapter.deedsweb.infrastructure.config.DeedsWebApiConfiguration;

class SecretsManagerDeedsWebCredentialsProviderTest {

  private SecretManager secretManager;
  private DeedsWebApiConfiguration configuration;
  private SecretsManagerDeedsWebCredentialsProvider provider;

  @BeforeEach
  void setUp() {
    secretManager = mock(SecretManager.class);
    // Real config so the secret name isn't blank.
    Properties props = new Properties();
    props.setProperty("deedsweb.credentials.secret.name", "verigate/deedsweb/credentials");
    configuration = new DeedsWebApiConfiguration(props);
    provider =
        new SecretsManagerDeedsWebCredentialsProvider(
            secretManager, new ObjectMapper(), configuration);
  }

  @Test
  void get_returnsParsedCredentials() throws Exception {
    when(secretManager.getSecret(eq("verigate/deedsweb/credentials")))
        .thenReturn("{\"username\":\"alice\",\"password\":\"s3cret\"}");

    DeedsWebCredentials creds = provider.get();

    assertEquals("alice", creds.username());
    assertEquals("s3cret", creds.password());
  }

  @Test
  void get_secretsManagerFailure_throwsTransient() {
    when(secretManager.getSecret(eq("verigate/deedsweb/credentials")))
        .thenThrow(SecretsManagerException.builder().message("AWS unavailable").build());

    TransientException ex = assertThrows(TransientException.class, () -> provider.get());
    assertTrue(ex.getMessage().contains("Failed to fetch DeedsWeb credentials"));
  }

  @Test
  void get_emptySecret_throwsPermanent() {
    when(secretManager.getSecret(eq("verigate/deedsweb/credentials"))).thenReturn("");

    PermanentException ex = assertThrows(PermanentException.class, () -> provider.get());
    assertTrue(ex.getMessage().contains("is empty"));
  }

  @Test
  void get_invalidJson_throwsPermanent() {
    when(secretManager.getSecret(eq("verigate/deedsweb/credentials")))
        .thenReturn("not-valid-json");

    PermanentException ex = assertThrows(PermanentException.class, () -> provider.get());
    assertTrue(ex.getMessage().contains("not valid JSON"));
  }

  @Test
  void get_missingUsername_throwsPermanent() {
    when(secretManager.getSecret(eq("verigate/deedsweb/credentials")))
        .thenReturn("{\"password\":\"s3cret\"}");

    PermanentException ex = assertThrows(PermanentException.class, () -> provider.get());
    assertTrue(ex.getMessage().contains("missing the 'username' field"));
  }

  @Test
  void get_missingPassword_throwsPermanent() {
    when(secretManager.getSecret(eq("verigate/deedsweb/credentials")))
        .thenReturn("{\"username\":\"alice\"}");

    PermanentException ex = assertThrows(PermanentException.class, () -> provider.get());
    assertTrue(ex.getMessage().contains("missing the 'password' field"));
  }

  @Test
  void get_blankConfiguredSecretName_throwsPermanent() {
    DeedsWebApiConfiguration blankConfig = new DeedsWebApiConfiguration(new Properties()) {
      @Override
      public String getCredentialsSecretName() {
        return "";
      }
    };
    SecretsManagerDeedsWebCredentialsProvider blankProvider =
        new SecretsManagerDeedsWebCredentialsProvider(
            secretManager, new ObjectMapper(), blankConfig);

    PermanentException ex = assertThrows(PermanentException.class, blankProvider::get);
    assertTrue(ex.getMessage().contains("not configured"));
  }
}
