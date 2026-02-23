/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CryptographicallyTransformedUUIDTest {

  @Test
  void transformedUUIDShouldBeDeterministic() {
    var uuid = UUID.randomUUID();
    var uuid1 = CryptographicallyTransformedUUID.generateUUIDFromHash(uuid, Object.class, (byte) 0);
    var uuid2 = CryptographicallyTransformedUUID.generateUUIDFromHash(uuid, Object.class, (byte) 0);

    assertEquals(uuid1, uuid2);
  }

  @Test
  void algorithmShouldNotChange() {
    var uuid = UUID.fromString("c28c659a-e544-b061-ed52-97cebc524cf8");
    var uuid1 = CryptographicallyTransformedUUID.generateUUIDFromHash(uuid, Object.class, (byte) 0);

    assertEquals("4088fc7a-0af7-fa9e-ec8f-6bf0fb3b439b", uuid1.toString());
  }

  @Test
  void differentTransformedUUIDsShouldNotMatch() {
    var uuid1 =
        CryptographicallyTransformedUUID.generateUUIDFromHash(
            UUID.randomUUID(), Object.class, (byte) 0);
    var uuid2 =
        CryptographicallyTransformedUUID.generateUUIDFromHash(
            UUID.randomUUID(), Object.class, (byte) 0);

    assertNotEquals(uuid1, uuid2);
  }

  @Test
  void bulkDifferentTransformedUUIDsShouldNotMatch() {
    var uuid1 =
        CryptographicallyTransformedUUID.generateUUIDFromHash(
            UUID.randomUUID(), Object.class, (byte) 0);

    for (int i = 0; i < 10000000; i++) {
      var uuid2 =
          CryptographicallyTransformedUUID.generateUUIDFromHash(
              UUID.randomUUID(), Object.class, (byte) 0);

      assertNotEquals(uuid1, uuid2);
    }
  }
}
