package crosscutting.resiliency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.exceptions.PermanentException;
import infrastructure.event.handler.model.ExtendedMockEventA;
import infrastructure.event.handler.model.ExtendedMockEventB;
import infrastructure.event.handler.model.MockEvent;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class DefaultInternalTransportJsonSerializerTest {
  // Tests for T deserialize(String data)
  @Test
  void deserializesStringToExpectedType() {
    var serializer = new DefaultInternalTransportJsonSerializer();
    var data = serializer.serialize(new ExtendedMockEventA("a"));
    var deserialized = serializer.deserialize(data);

    assertNotNull(deserialized);
  }

  @Test
  void permanentExceptionThrownByDeserializingInvalidJsonStringWithoutType() {
    var serializer = new DefaultInternalTransportJsonSerializer();

    // JSON with incompatible type for nestedObject
    String invalidJson = "{\"obj\": \"blah!\"";

    assertThrows(
        PermanentException.class,
        () -> {
          serializer.deserialize(invalidJson);
        });
  }

  // Tests for T deserialize(ByteBuffer byteBuffer)
  @Test
  void deserializesByteBufferToExpectedType() {
    var serializer = new DefaultInternalTransportJsonSerializer();
    var data = serializer.serializeToBytes(new ExtendedMockEventA("a"));
    var deserialized = serializer.deserialize(data);

    assertNotNull(deserialized);
  }

  @Test
  void permanentExceptionThrownByDeserializingInvalidJsonByteBufferWithoutType() {
    var serializer = new DefaultInternalTransportJsonSerializer();

    // JSON with incompatible type for nestedObject
    String invalidJson = "{\"obj\": \"blah!\"";
    byte[] byteArray = invalidJson.getBytes(StandardCharsets.UTF_8);
    ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);

    assertThrows(
        PermanentException.class,
        () -> {
          serializer.deserialize(byteBuffer);
        });
  }

  // Tests for T deserialize(ByteBuffer byteBuffer, Class<T> clazz)
  @Test
  void registeredTypeDeserializesByteBufferToExpectedType() {
    var serializer = DefaultInternalTransportJsonSerializer.withTyping();
    serializer.registerClassType(
        ExtendedMockEventA.class.getSimpleName(), ExtendedMockEventA.class);

    var data = serializer.serializeToBytes(new ExtendedMockEventA("a"));
    var deserialized = serializer.deserialize(data, MockEvent.class);

    assertNotNull(deserialized);

    assertEquals(ExtendedMockEventA.class, deserialized.getClass());
  }

  @Test
  void unregisteredTypeDeserializesByteBufferNull() {
    var serializerA = DefaultInternalTransportJsonSerializer.withTyping();
    serializerA.registerClassType(
        ExtendedMockEventA.class.getSimpleName(), ExtendedMockEventA.class);
    var serializerB = DefaultInternalTransportJsonSerializer.withTyping();
    serializerB.registerClassType(
        ExtendedMockEventB.class.getSimpleName(), ExtendedMockEventB.class);

    var dataB = serializerB.serializeToBytes(new ExtendedMockEventB("b"));
    var deserializedB = serializerA.deserialize(dataB, MockEvent.class);

    assertEquals(null, deserializedB);
  }

  @Test
  void permanentExceptionThrownByDeserializingInvalidJsonByteBuffer() {
    var serializer = DefaultInternalTransportJsonSerializer.withTyping();
    serializer.registerClassType(MockEvent.class.getSimpleName(), MockEvent.class);

    // JSON with incompatible type for nestedObject
    String invalidJson = "{\"obj\": \"blah!\"";
    byte[] byteArray = invalidJson.getBytes(StandardCharsets.UTF_8);
    ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);

    assertThrows(
        PermanentException.class,
        () -> {
          serializer.deserialize(byteBuffer, MockEvent.class);
        });
  }

  // Tests for S deserialize(String data, Class<S> clazz)
  @Test
  void registeredTypeDeserializesStringToExpectedType() {
    var serializerA = DefaultInternalTransportJsonSerializer.withTyping();
    serializerA.registerClassType(
        ExtendedMockEventA.class.getSimpleName(), ExtendedMockEventA.class);

    var dataA = serializerA.serialize(new ExtendedMockEventA("a"));
    var deserializedA = serializerA.deserialize(dataA, MockEvent.class);

    assertNotNull(deserializedA);

    assertEquals(ExtendedMockEventA.class, deserializedA.getClass());
  }

  @Test
  void unregisteredTypeDeserializesStringNull() {
    var serializerA = DefaultInternalTransportJsonSerializer.withTyping();
    serializerA.registerClassType(
        ExtendedMockEventA.class.getSimpleName(), ExtendedMockEventA.class);
    var serializerB = DefaultInternalTransportJsonSerializer.withTyping();
    serializerB.registerClassType(
        ExtendedMockEventB.class.getSimpleName(), ExtendedMockEventB.class);

    var dataB = serializerB.serialize(new ExtendedMockEventB("b"));
    var deserializedB = serializerA.deserialize(dataB, MockEvent.class);

    assertEquals(null, deserializedB);
  }

  @Test
  void permanentExceptionThrownByDeserializingInvalidJsonString() {
    var serializer = DefaultInternalTransportJsonSerializer.withTyping();
    serializer.registerClassType(MockEvent.class.getSimpleName(), MockEvent.class);

    // JSON with incompatible type for nestedObject
    String invalidJson = "{\"obj\": \"blah!\"";

    assertThrows(
        PermanentException.class,
        () -> {
          serializer.deserialize(invalidJson, MockEvent.class);
        });
  }
}
