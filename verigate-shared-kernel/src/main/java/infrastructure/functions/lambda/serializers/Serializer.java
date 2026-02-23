/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.serializers;

import domain.exceptions.DeserializeException;
import domain.exceptions.SerializeException;
import java.nio.ByteBuffer;
import java.util.List;

/** Represents a serializer that serializes data into objects of the specified class. */
public interface Serializer {
  /**
   * Serializes the given data into an object of the specified class.
   *
   * @param data the data to be deserialized
   * @param clazz the class of the object to be deserialized into
   * @param <T> the type of the object to be deserialized into
   * @return the deserialized object
   */
  <T> T deserialize(String data, Class<T> clazz) throws DeserializeException;

  /**
   * Deserializes the data contained in the ByteBuffer into an object of the specified class.
   *
   * @param byteBuffer the ByteBuffer containing the data to be deserialized
   * @param clazz the class of the object to be deserialized into
   * @param <T> the type of the object to be deserialized into
   * @return the deserialized object, or null if the data in the ByteBuffer cannot be deserialized
   *     into the specified class
   */
  <T> T deserialize(ByteBuffer byteBuffer, Class<T> clazz) throws DeserializeException;

  /**
   * Deserializes the given string data into an object of the specified type.
   *
   * @param <T> the type of the object to be deserialized
   * @param data the string data to be deserialized
   * @return the deserialized object of type T
   */
  <T> T deserialize(String data) throws DeserializeException;

  /**
   * Deserializes the given ByteBuffer into an object of the specified type.
   *
   * @param <T> the type of the object to be deserialized
   * @param byteBuffer the ByteBuffer containing the data to be deserialized
   * @return the deserialized object of type T
   */
  <T> T deserialize(ByteBuffer byteBuffer) throws DeserializeException;

  /**
   * Deserializes the given string data into a list of objects of the specified type.
   *
   * @param <T> the type of the objects to be deserialized
   * @param data the string data to be deserialized
   * @param clazz the class of the objects to be deserialized into
   * @return the deserialized list of objects of type T
   */
  <T> List<T> deserializeList(String data, Class<T> clazz) throws DeserializeException;

  /**
   * Serializes the given object into a string representation.
   *
   * @param object the object to be serialized
   * @param <T> the type of the object to be serialized
   * @return the string representation of the serialized object
   */
  <T> String serialize(T object) throws SerializeException;

  /**
   * Serializes the given object into a byte buffer.
   */
  <T> ByteBuffer serializeToBytes(T object) throws SerializeException;
}
