/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.documents;

import static org.junit.Assert.assertEquals;

import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import org.junit.jupiter.api.Test;

/**
 * This class is responsible for testing the parsing of credentials.
 */
public class CredentialsParsingTest {

  @Test
  public void testParseCredentials() {
    var serializer = new DefaultInternalTransportJsonSerializer();

    var serializedCredentials = "{\"clientId\":\"clientId\",\"clientSecret\":\"clientSecret\"}";

    var credentials =
        serializer.deserialize(serializedCredentials, AdobePdfGeneratorCredentials.class);

    assertEquals(credentials.clientId(), "clientId");
    assertEquals(credentials.clientSecret(), "clientSecret");
  }
}
