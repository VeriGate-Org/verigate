/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * This class provides a utility method to generate a UUID by cryptographically transforming an
 * input UUID with an additional byte of data. It utilizes a SHA-256 hash function to ensure the
 * transformation is secure and produces a unique output UUID based on the input UUID and the
 * additional byte.
 */
public class CryptographicallyTransformedUUID {

  /**
   * Generates a new UUID derived from hashing the input UUID concatenated with an additional byte.
   * This method employs SHA-256 hashing to ensure a secure cryptographic transformation. The
   * resultant hash is then used to construct a new UUID.
   *
   * @param inputUUID The original UUID to be transformed. It serves as the base for the
   *     cryptographic operation.
   * @param additionalInput A single byte of additional data to be concatenated with the input UUID
   *     before hashing. This provides an extra layer of input variation and enhances security.
   * @return A new UUID generated from the SHA-256 hash of the input UUID and the additional byte.
   * @throws RuntimeException if the SHA-256 hashing algorithm is not supported on the platform.
   *     This is a wrapper around the NoSuchAlgorithmException to simplify error handling for the
   *     caller.
   */
  public static UUID generateUUIDFromHash(UUID inputUUID, Class<?> clazz, byte additionalInput) {
    try {
      // Concatenate the input UUID's string representation with the additional byte to form the
      // input string for hashing.
      String inputForHash = inputUUID.toString() + clazz.getCanonicalName() + additionalInput;

      // Acquire a MessageDigest instance for SHA-256, ensuring a secure hash function is used for
      // the transformation.
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      // Compute the SHA-256 hash of the input data. This cryptographic hash function ensures the
      // output is unique
      // and non-reversible, providing a strong foundation for generating a secure UUID.
      byte[] hashBytes = digest.digest(inputForHash.getBytes());

      // Wrap the first 16 bytes of the hash in a ByteBuffer. The UUID requires 128 bits (16 bytes),
      // so we take the first part of the hash to form the UUID, ensuring it is sufficiently unique.
      ByteBuffer byteBuffer = ByteBuffer.wrap(hashBytes, 0, 16);

      // Extract the high and low 64 bits from the ByteBuffer to form the two long values needed for
      // a UUID.
      long highBits = byteBuffer.getLong();
      long lowBits = byteBuffer.getLong();

      // Construct and return the new UUID from the high and low bits derived from the hash.
      return new UUID(highBits, lowBits);
    } catch (NoSuchAlgorithmException e) {
      // Wrap and throw a RuntimeException if SHA-256 is not available, simplifying error handling
      // for the caller.
      throw new RuntimeException("SHA-256 not supported", e);
    }
  }
}
