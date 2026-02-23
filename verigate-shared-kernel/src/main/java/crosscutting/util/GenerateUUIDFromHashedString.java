/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import domain.exceptions.PermanentException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * This class provides a utility method to generate a UUID by hashing an input string and truncating
 * the hash to fit into a UUID. It utilizes a SHA-1 hash function to ensure the transformation is
 * secure and produces a unique output UUID based on the input string.
 */
public class GenerateUUIDFromHashedString {

  /**
   * Courtesy of ChatGPT...
   * Generates a new UUID derived from hashing the input string. This method employs SHA-1 hashing
   * to ensure a secure cryptographic transformation. The resultant hash is then used to construct
   * a new UUID.
   *
   * @param input The original string to be transformed. It serves as the base for the cryptographic
   *     operation.
   * @return A new UUID generated from the SHA-1 hash of the input string.
   * @throws RuntimeException if the SHA-1 hashing algorithm is not supported on the platform. This
   *     is a wrapper around the NoSuchAlgorithmException to simplify error handling for the caller.
   */
  public static UUID create(String input) {
    try {
      // Create a SHA-1 hash from the input string
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

      // Truncate the hash to the first 16 bytes (128 bits) to fit into a UUID
      byte[] truncatedHash = new byte[16];
      System.arraycopy(hash, 0, truncatedHash, 0, truncatedHash.length);

      // Set the version to 5 (name-based, SHA-1) and variant to IETF RFC 4122
      truncatedHash[6] &= 0x0F; // clear version
      truncatedHash[6] |= 0x50; // set to version 5
      truncatedHash[8] &= 0x3F; // clear variant
      truncatedHash[8] |= 0x80; // set to IETF variant

      // Create the UUID from the truncated hash
      return UUID.nameUUIDFromBytes(truncatedHash);
    } catch (Exception e) {
      throw new PermanentException("Failed to generate UUID from hashed string", e);
    }
  }
}
