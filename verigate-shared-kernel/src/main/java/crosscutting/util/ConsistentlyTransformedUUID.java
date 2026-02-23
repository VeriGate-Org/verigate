/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * This class provides a utility method to generate a UUID by consistently but not cryptographically
 * transforming an input UUID with an additional byte of data. It utilizes a Murmur 3 hash function
 * to ensure the transformation is secure and produces a unique output UUID based on the input UUID
 * and the additional byte. Consistent hashing is useful in scenarios where we not not have the
 * requirement of cryptographic hashing and is much faster.
 */
public class ConsistentlyTransformedUUID {

  /**
   * Generates a new UUID derived from hashing the input UUID concatenated with an additional byte.
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
    // Concatenate the input UUID's string representation with the additional byte to form the
    // input string for hashing.
    String inputForHash = inputUUID.toString() + clazz.getCanonicalName() + additionalInput;

    HashFunction hashFunction = Hashing.murmur3_128();
    byte[] hashBytes = hashFunction.hashBytes(inputForHash.getBytes()).asBytes();

    // Wrap the first 16 bytes of the hash in a ByteBuffer. The UUID requires 128 bits (16 bytes),
    // so we take the first part of the hash to form the UUID, ensuring it is sufficiently unique.
    ByteBuffer byteBuffer = ByteBuffer.wrap(hashBytes, 0, 16);

    // Extract the high and low 64 bits from the ByteBuffer to form the two long values needed for
    // a UUID.
    long highBits = byteBuffer.getLong();
    long lowBits = byteBuffer.getLong();

    // Construct and return the new UUID from the high and low bits derived from the hash.
    return new UUID(highBits, lowBits);
  }
}
