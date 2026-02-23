/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.serializers.internal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import crosscutting.serialization.MoneyDeserializer;
import crosscutting.serialization.MoneySerializer;
import domain.exceptions.DeserializeException;
import domain.exceptions.SerializeException;
import domain.exceptions.StringExceptionBuilder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A serializer that converts JSON data to objects of a specified class. */
public class DefaultInternalTransportJsonSerializer implements InternalTransportJsonSerializer {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(DefaultInternalTransportJsonSerializer.class);
  private final ObjectMapper objectMapper;
  private final ArrayList<String> registeredSubtypes = new ArrayList<>();

  /**
   * Default constructor for DefaultInternalTransportJsonSerializer. Configures the ObjectMapper
   * with common serializers, deserializers, and visibility settings.
   */
  public DefaultInternalTransportJsonSerializer() {
    // register common serializers and deserializers
    objectMapper =
        JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new Jdk8Module())
            .addModule(
                new SimpleModule()
                    .addSerializer(Money.class, new MoneySerializer())
                    .addDeserializer(Money.class, new MoneyDeserializer()))
            .visibility(
                VisibilityChecker.Std.defaultInstance()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
            .build();
  }

  /**
   * A constructor for DefaultInternalTransportJsonSerializer to register additional custom
   * modules over and above the ones from the default constructor.
   *
   * @param additionalModules Additional modules to register.
   */
  public DefaultInternalTransportJsonSerializer(Module... additionalModules) {
    this();
    objectMapper.registerModules(additionalModules);
  }

  /**
   * Factory method to create an instance of DefaultInternalTransportJsonSerializer with type
   * information.
   *
   * @return a DefaultInternalTransportJsonSerializer instance configured to include type
   *         information.
   */
  public static DefaultInternalTransportJsonSerializer withTyping() {
    var serializer = new DefaultInternalTransportJsonSerializer();

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
   * Associates Jackson metadata with a target class.
   * Useful if it's not possible or desirable to apply annotations directly on
   * the target class (for example if it's a third-party class or a domain object).
   * <p/>Example of a mixin class:
   * <pre>
   *   &#64;JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "__artifact_type__")
   *   public abstract class CollectionBaseTypeMixIn {}
   * </pre>
   *
   * <p/>Example usage:
   * <pre>
   *  // needed for serialization and deserialization
   *  serializer.addMixin(CollectionEvent.class, CollectionBaseTypeMixIn.class);
   *
   *  // needed for deserialization only
   *  serializer.registerClassType("CollectionSucceededEvent", CollectionSucceededEvent.class);
   *  serializer.registerClassType("CollectionFailedEvent", CollectionFailedEvent.class);
   *  serializer.registerClassType("CollectionPartiallySucceededEvent",
   *      CollectionPartiallySucceededEvent.class);
   * </pre>
   */
  public void addMixin(Class targetClazz, Class mixinClazz) {
    objectMapper.addMixIn(targetClazz, mixinClazz);
  }

  /**
   * Registers a class type with the specified type name for deserialization.
   *
   * @param type the name of the type to be registered
   * @param clazz the class to be registered
   */
  public void registerClassType(String type, Class<?> clazz) {
    NamedType namedType = new NamedType(clazz, type);
    objectMapper.registerSubtypes(namedType);
    this.registeredSubtypes.add(namedType.getName());
  }

  @Override
  public <T> T deserialize(String data, Class<T> clazz) {
    // TODO: add this back in once we have refactored everywhere
    // to set types on our serializers
    // checkTypeRegistration();
    return handleDeserialization(data, () -> objectMapper.readValue(data, clazz));
  }

  @Override
  public <T> T deserialize(ByteBuffer byteBuffer, Class<T> clazz) {
    // TODO: add this back in once we have refactored everywhere
    // to set types on our serializers
    // checkTypeRegistration();
    byte[] bytes = byteBuffer.array();
    return handleDeserialization(bytes, () -> objectMapper.readValue(bytes, clazz));
  }

  @Override
  public <T> T deserialize(String data) {
    return handleDeserialization(data,
        () -> objectMapper.readValue(data, new TypeReference<T>() {}));
  }

  @Override
  public <T> T deserialize(ByteBuffer byteBuffer) {
    byte[] bytes = byteBuffer.array();
    return handleDeserialization(bytes,
        () -> objectMapper.readValue(bytes, new TypeReference<>() {}));
  }

  public <T> T deserialize(String data, TypeReference<T> typeReference) {
    return handleDeserialization(data, () -> objectMapper.readValue(data, typeReference));
  }

  @Override
  public <T> List<T> deserializeList(String data, Class<T> clazz) {
    return handleDeserialization(data, () -> objectMapper.readerForListOf(clazz).readValue(data));
  }

  @Override
  public <T> String serialize(T object) {
    return handleSerialization(object, () -> objectMapper.writeValueAsString(object));
  }

  @Override
  public <T> ByteBuffer serializeToBytes(T object) {
    return handleSerialization(object,
        () -> {
          byte[] byteArray = objectMapper.writeValueAsBytes(object);
          return ByteBuffer.wrap(byteArray);
        });
  }

  /**
   * Handles deserialization and exception handling.
   *
   * @param deserializationTask The deserialization task to execute.
   * @param <T> The type of the deserialized object.
   * @return The deserialized object.
   * @throws DeserializeException If an error occurs while mapping or processing the JSON data.
   */
  private <T> T handleDeserialization(Object content, DeserializationTask<T> deserializationTask) {

    if (content == null) {
      throw new DeserializeException(null, "content is null");
    }
    try {
      return deserializationTask.execute();
    } catch (InvalidTypeIdException e) {
      if (e.getTypeId() != null) {
        return null; // this type is of no interest as no classes registered against this type
      }
      throw new DeserializeException(content,
          "No type id was located in JSON data. This is required if classes have been registered"
              + " against type IDs",
          e);
    } catch (JsonMappingException e) {
      LOGGER.error("An error occurred while mapping JSON", e);
      throw new DeserializeException(content,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (JsonProcessingException e) {
      LOGGER.error("An error occurred while processing JSON", e);
      throw new DeserializeException(content,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (IOException e) {
      LOGGER.error("An error occurred while processing json from ByteBuffer", e);
      throw new DeserializeException(content,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (ClassCastException e) {
      LOGGER.error("An error occurred while casting the deserialized object", e);
      throw new DeserializeException(content,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    }
  }

  /**
   * Handles serialization and exception handling.
   *
   * @param serializationTask The serialization task to execute.
   * @param <T> The type of the serialized object.
   * @return The serialized JSON data.
   * @throws SerializeException If an error occurs while processing the JSON data.
   */
  private <T> T handleSerialization(Object data, SerializationTask<T> serializationTask) {
    try {
      return serializationTask.execute();
    } catch (JsonProcessingException e) {
      LOGGER.error("An error occurred while processing JSON", e);
      throw new SerializeException(data,
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    }
  }

  /**
   * Functional interface for deserialization tasks.
   *
   * @param <T> The type of the deserialized object.
   */
  @FunctionalInterface
  private interface DeserializationTask<T> {
    T execute() throws IOException, ClassCastException;
  }

  /**
   * Functional interface for serialization tasks.
   *
   * @param <T> The type of the serialized object.
   */
  @FunctionalInterface
  private interface SerializationTask<T> {
    T execute() throws JsonProcessingException;
  }
}
