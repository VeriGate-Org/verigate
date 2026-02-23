/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConsistentlyTransformedUUIDTest {

  @Test
  void transformedUUIDShouldBeDeterministic() {
    var uuid = UUID.randomUUID();
    var uuid1 = ConsistentlyTransformedUUID.generateUUIDFromHash(uuid, Object.class, (byte) 0);
    var uuid2 = ConsistentlyTransformedUUID.generateUUIDFromHash(uuid, Object.class, (byte) 0);

    assertEquals(uuid1, uuid2);
  }

  @Test
  void algorithmShouldNotChange() {
    var uuid = UUID.fromString("c28c659a-e544-b061-ed52-97cebc524cf8");
    var uuid1 = ConsistentlyTransformedUUID.generateUUIDFromHash(uuid, Object.class, (byte) 0);

    assertEquals("538a79b2-8e39-a3b5-49e6-f7954ea670aa", uuid1.toString());
  }

  @Test
  void differentTransformedUUIDsShouldNotMatch() {
    var uuid1 =
        ConsistentlyTransformedUUID.generateUUIDFromHash(
            UUID.randomUUID(), Object.class, (byte) 0);
    var uuid2 =
        ConsistentlyTransformedUUID.generateUUIDFromHash(
            UUID.randomUUID(), Object.class, (byte) 0);

    assertNotEquals(uuid1, uuid2);
  }

  @Test
  void bulkDifferentTransformedUUIDsShouldNotMatch() {
    var uuid1 =
        ConsistentlyTransformedUUID.generateUUIDFromHash(
            UUID.randomUUID(), Object.class, (byte) 0);

    for (int i = 0; i < 10000000; i++) {
      var uuid2 =
          ConsistentlyTransformedUUID.generateUUIDFromHash(
              UUID.randomUUID(), Object.class, (byte) 0);

      assertNotEquals(uuid1, uuid2);
    }
  }
}
