/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * This class provides a utility method to generate a UUID by hashing an input string and truncating
 * the hash to fit into a UUID. It utilizes a SHA-1 hash function to ensure the transformation is
 * secure and produces a unique output UUID based on the input string.
 */
public class GenerateUUIDFromHashedStringTest {

  @Test
  public void testCreate() {
    String input = "input";
    String input2 = "0301017089188";

    var result = GenerateUUIDFromHashedString.create(input);
    var result2 = GenerateUUIDFromHashedString.create(input2);

    assertEquals(UUID.fromString("0f27cf7f-6e1d-3bf9-bda5-51e06700ac99"), result);
    assertEquals(UUID.fromString("80673474-2145-307d-820a-61b1c8d357c1"), result2);
  }
}
