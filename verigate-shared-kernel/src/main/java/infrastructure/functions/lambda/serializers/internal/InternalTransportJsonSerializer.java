/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.serializers.internal;

import domain.exceptions.DeserializeException;
import infrastructure.functions.lambda.serializers.Serializer;
import java.util.List;

/**
 * `InternalTransportJsonSerializer` is the interface that represents
 * the serializer for the internal transport. This serializer is
 * responsible for converting domain objects into a format suitable
 * for internal communication between microservices. It ensures that
 * data is efficiently serialized and deserialized while maintaining
 * compatibility and consistency across different services.
 *
 * <p>This interface abstracts the underlying serialization mechanism,
 * which in our case will eventually be standardized to use Avro as per
 * Suraj's RFC. By defining this interface, we allow for flexibility
 * in changing the serialization mechanism in the future without
 * impacting the service implementations that rely on it.
 *
 * <p>Key responsibilities of the `InternalTransportJsonSerializer`:
 * - Serialize domain objects to a byte array for transport.
 * - Deserialize byte arrays back into domain objects.
 * - Ensure schema compatibility and handle schema evolution.
 * - Provide efficient and performant serialization/deserialization.
 *
 * <p>Implementations of this interface should handle the specifics
 * of the chosen serialization format and any necessary schema
 * management.
 */
public interface InternalTransportJsonSerializer extends Serializer {

  /**
   * Deserializes a string representation of a list into a List of objects of the specified class.
   *
   * @param data The string representation of the list to be deserialized.
   * @param clazz The class of the objects in the list.
   * @param <T> The type of the objects in the list.
   * @return The deserialized List of objects.
   */
  <T> List<T> deserializeList(String data, Class<T> clazz) throws DeserializeException;
}
