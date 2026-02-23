/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.serializers.http;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import crosscutting.serialization.MoneyDeserializer;
import crosscutting.serialization.MoneySerializer;
import domain.exceptions.DeserializeException;
import domain.exceptions.SerializeException;
import domain.exceptions.StringExceptionBuilder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A serializer that converts JSON data to objects of a specified class. */
public class DefaultJsonSerializer implements JsonSerializer {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJsonSerializer.class);
  private final ObjectMapper objectMapper;

  /**
   * Default constructor for JsonSerializer. Configures the ObjectMapper with common serializers,
   * deserializers, and visibility settings.
   */
  public DefaultJsonSerializer() {
    // register common serializers and deserializers
    objectMapper =
        JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(
                new SimpleModule()
                    .addSerializer(Money.class, new MoneySerializer())
                    .addDeserializer(Money.class, new MoneyDeserializer()))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
  }

  /**
   * Factory method to create an instance of JsonSerializer with type information.
   *
   * @return a JsonSerializer instance configured to include type information.
   */
  public static DefaultJsonSerializer withTyping() {
    var serializer = new DefaultJsonSerializer();

    // TypeResolverBuilder to include type information
    TypeResolverBuilder<?> typeBuilder =
        new StdTypeResolverBuilder()
            .init(JsonTypeInfo.Id.NAME, null)
            .inclusion(JsonTypeInfo.As.PROPERTY)
            .typeProperty("__artifact_type__");

    serializer.objectMapper.setDefaultTyping(typeBuilder);
    return serializer;
  }

  /**
   * Registers a class type with the specified type name.
   *
   * @param type the name of the type to be registered
   * @param clazz the class to be registered
   */
  public void registerClassType(String type, Class<?> clazz) {
    objectMapper.registerSubtypes(new NamedType(clazz, type));
  }

  /**
   * Serializes the given JSON data into an object of the specified class.
   *
   * @param data the JSON data to be deserialized
   * @param clazz the class of the object to be deserialized
   * @return the deserialized object
   * @throws DeserializeException if an error occurs while mapping or processing the JSON data
   */
  public <S> S deserialize(String data, Class<S> clazz) {

    if (data == null) {
      throw new DeserializeException(null, "content is null");
    }
    try {
      return objectMapper.readValue(data, clazz);
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred while mapping json", e);
      throw new DeserializeException(data,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (JsonProcessingException e) {
      LOGGER.error("An error occurred while processing json", e);
      throw new DeserializeException(data,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    }
  }

  /**
   * Deserializes the data contained in the ByteBuffer into an object of the specified class.
   *
   * @param byteBuffer the ByteBuffer containing the data to be deserialized
   * @param clazz the class of the object to be deserialized into
   * @param <T> the type of the object to be deserialized into
   * @return the deserialized object
   * @throws DeserializeException if an error occurs during the deserialization process
   */
  @Override
  public <T> T deserialize(ByteBuffer byteBuffer, Class<T> clazz) {

    byte[] bytes = byteBuffer.array();

    if (bytes.length == 0) {
      throw new DeserializeException(null, "content is null");
    }
    try {
      return objectMapper.readValue(bytes, clazz);
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred while mapping json from ByteBuffer", e);
      throw new DeserializeException(bytes,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (IOException e) {
      LOGGER.error("An error occurred while processing json from ByteBuffer", e);
      throw new DeserializeException(bytes,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    }
  }

  /**
   * Deserializes the given JSON data into an object of the specified type.
   *
   * @param <T> the type of the deserialized object
   * @param data the JSON data as a String
   * @return the deserialized object
   * @throws DeserializeException if there is a problem with the deserialization process
   */
  @Override
  public <T> T deserialize(String data) {

    if (data == null) {
      throw new DeserializeException(null, "content is null");
    }
    try {
      return objectMapper.readValue(data, new TypeReference<>() {});
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred while mapping json from ByteBuffer", e);
      throw new DeserializeException(data,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (IOException e) {
      LOGGER.error("An error occurred while processing json from ByteBuffer", e);
      throw new DeserializeException(data,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    }
  }

  /**
   * Deserializes the data contained in the ByteBuffer into an object of the specified type.
   *
   * @param byteBuffer the ByteBuffer containing the data to be deserialized
   * @param <T> the type of the object to be deserialized
   * @return the deserialized object
   * @throws DeserializeException if an error occurs while mapping or processing the object
   */
  @Override
  public <T> T deserialize(ByteBuffer byteBuffer) {

    byte[] bytes = byteBuffer.array();

    if (bytes.length == 0) {
      throw new DeserializeException(null, "content is null");
    }

    try {
      return objectMapper.readValue(bytes, new TypeReference<>() {});
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred while mapping json from ByteBuffer", e);
      throw new DeserializeException(bytes,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (IOException e) {
      LOGGER.error("An error occurred while processing json from ByteBuffer", e);
      throw new DeserializeException(bytes,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    }
  }

  /**
   * Deserializes the given JSON data into a list of objects of the specified type.
   *
   * @param data the JSON data to be deserialized
   * @param clazz the class of the objects to be deserialized
   * @param <T> the type of the objects to be deserialized
   * @return the deserialized list of objects
   * @throws DeserializeException if an error occurs while mapping or processing the object
   */
  @Override
  public <T> List<T> deserializeList(String data, Class<T> clazz) {

    if (data == null) {
      throw new DeserializeException(null, "content is null");
    }
    try {
      return objectMapper.readValue(
          data, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred while mapping json from ByteBuffer", e);
      throw new DeserializeException(data,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (IOException e) {
      LOGGER.error("An error occurred while processing json from ByteBuffer", e);
      throw new DeserializeException(data,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    }
  }

  /**
   * Serializes the given object into a JSON string representation.
   *
   * @param object the object to be serialized
   * @param <T> the type of the object to be serialized
   * @return the string representation of the serialized object
   * @throws SerializeException if an error occurs while mapping or processing the object
   */
  @Override
  public <T> String serialize(T object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred while mapping json", e);
      throw new SerializeException(object, e.getMessage());
    } catch (JsonProcessingException e) {
      LOGGER.error("An error occurred while processing json", e);
      throw new SerializeException(object, e.getMessage());
    }
  }

  /**
   * Serializes the given object into a JSON byte array representation.
   *
   * @param object the object to be serialized
   * @param <T> the type of the object to be serialized
   * @return the byte array representation of the serialized object
   * @throws SerializeException if an error occurs while mapping or processing the object
   */
  @Override
  public <T> ByteBuffer serializeToBytes(T object) {
    try {
      byte[] byteArray = objectMapper.writeValueAsBytes(object);
      return ByteBuffer.wrap(byteArray);
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred while mapping json", e);
      throw new SerializeException(object, e.getMessage());
    } catch (JsonProcessingException e) {
      LOGGER.error("An error occurred while processing json", e);
      throw new SerializeException(object, e.getMessage());
    }
  }
}
