package infrastructure.messaging.headers.v1;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.exceptions.DeserializeException;
import infrastructure.messaging.headers.CompressionType;
import infrastructure.messaging.headers.EncodingType;
import infrastructure.messaging.headers.Header;
import infrastructure.messaging.headers.MessageType;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BinaryMessageHeaderTest {

  @Test
  public void invalidHeaderNullMessageType() {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> new BinaryMessageHeader(null, UUID.randomUUID(), EncodingType.AVRO,
            CompressionType.GZIP, UUID.randomUUID(), 55));
    assertEquals("MessageType must not be null", illegalArgumentException.getMessage());
  }

  @Test
  public void invalidHeaderNullSchemaId() {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> new BinaryMessageHeader(MessageType.COMMAND, null, EncodingType.AVRO,
            CompressionType.GZIP, UUID.randomUUID(), 55));
    assertEquals("SchemaId must not be null", illegalArgumentException.getMessage());
  }

  @Test
  public void invalidHeaderNullEncodingType() {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> new BinaryMessageHeader(MessageType.COMMAND, UUID.randomUUID(), null,
            CompressionType.GZIP, UUID.randomUUID(), 55));
    assertEquals("EncodingType must not be null", illegalArgumentException.getMessage());
  }

  @Test
  public void invalidHeaderNullCompressionType() {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> new BinaryMessageHeader(MessageType.COMMAND, UUID.randomUUID(), EncodingType.AVRO,
            null, UUID.randomUUID(), 55));
    assertEquals("CompressionType must not be null", illegalArgumentException.getMessage());
  }

  @Test
  public void invalidHeaderNullSourceId() {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> new BinaryMessageHeader(MessageType.COMMAND, UUID.randomUUID(), EncodingType.AVRO,
            CompressionType.GZIP, null, 55));
    assertEquals("SourceId must not be null", illegalArgumentException.getMessage());
  }

  @Test
  public void invalidHeaderZeroPayloadSize() {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> new BinaryMessageHeader(MessageType.COMMAND, UUID.randomUUID(), EncodingType.AVRO,
            CompressionType.GZIP, UUID.randomUUID(), 0));
    assertEquals("Payload size must be greater than 0", illegalArgumentException.getMessage());
  }

  @Test
  public void invalidHeaderNegativePayloadSize() {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> new BinaryMessageHeader(MessageType.COMMAND, UUID.randomUUID(), EncodingType.AVRO,
            CompressionType.GZIP, UUID.randomUUID(), -1));
    assertEquals("Payload size must be greater than 0", illegalArgumentException.getMessage());
  }

  @Test
  public void validHeaderSerialization() {
    final MessageType messageType = MessageType.COMMAND;
    final UUID schemaId = UUID.randomUUID();
    final EncodingType encodingType = EncodingType.AVRO;
    final CompressionType compressionType = CompressionType.NONE;
    final UUID sourceId = UUID.randomUUID();
    final int payloadSize = 33;

    final BinaryMessageHeader actualBinaryMessageHeader = new BinaryMessageHeader(messageType, schemaId,
        encodingType, compressionType, sourceId, payloadSize);
    final byte[] actualByteArray = actualBinaryMessageHeader.toByteArray();
    final byte[] expectedByteArray = createHeaderByteArray(messageType, schemaId, encodingType,
        compressionType, sourceId, payloadSize);
    assertArrayEquals(expectedByteArray, actualByteArray);
  }

  @Test
  public void validHeaderDeserialization() {
    final MessageType messageType = MessageType.COMMAND;
    final UUID schemaId = UUID.randomUUID();
    final EncodingType encodingType = EncodingType.AVRO;
    final CompressionType compressionType = CompressionType.NONE;
    final UUID sourceId = UUID.randomUUID();
    final int payloadSize = 33;

    final byte[] actualByteArray = createHeaderByteArray(messageType, schemaId, encodingType,
        compressionType, sourceId, payloadSize);
    final Header actualHeader = BinaryMessageHeader.fromByteArray(actualByteArray);
    final Header expectedHeader = new BinaryMessageHeader(
        messageType,
        schemaId,
        encodingType,
        compressionType,
        sourceId,
        payloadSize
    );
    assertEquals(expectedHeader, actualHeader);
  }

  @Test
  public void validHeaderSerializeDeserialize() {
    final MessageType messageType = MessageType.COMMAND;
    final UUID schemaId = UUID.randomUUID();
    final EncodingType encodingType = EncodingType.AVRO;
    final CompressionType compressionType = CompressionType.NONE;
    final UUID sourceId = UUID.randomUUID();
    final int payloadSize = 33;

    final Header headerOne = new BinaryMessageHeader(
        messageType,
        schemaId,
        encodingType,
        compressionType,
        sourceId,
        payloadSize
    );

    final byte[] headerOneByteArray = headerOne.toByteArray();
    final Header headerTwo = BinaryMessageHeader.fromByteArray(headerOneByteArray);
    assertEquals(headerOne, headerTwo);
  }

  @Test
  public void nullBytesDeserialization() {
    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(null));
    assertEquals("Null byte array", deserializeException.getMessage());
  }

  @Test
  public void emptyBytesDeserialization() {
    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(new byte[0]));
    assertEquals("Empty byte array", deserializeException.getMessage());
  }

  @Test
  public void insufficientBytesDeserialization() {
    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(new byte[1]));
    assertEquals("Invalid header: Insufficient header bytes", deserializeException.getMessage());
  }

  @Test
  public void invalidMagicNumberDeserialization() {
    byte[] headerBytes = createRandomValidHeaderByteArray();
    headerBytes[0] = 0x01;

    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(headerBytes));
    assertEquals("Invalid header: Expected magic number not found", deserializeException.getMessage());
  }

  @Test
  public void invalidVersionNumberDeserialization() {
    byte[] headerBytes = createRandomValidHeaderByteArray();
    headerBytes[4] = 0x09;

    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(headerBytes));
    assertEquals("Invalid header: Received version: 9, supporting: 1", deserializeException.getMessage());
  }

  @Test
  public void invalidMessageTypeDeserialization() {
    byte[] headerBytes = createRandomValidHeaderByteArray();
    headerBytes[5] = 127;

    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(headerBytes));
    assertEquals("Invalid header: Unsupported message type: 127", deserializeException.getMessage());
  }

  @Test
  public void invalidEncodingTypeDeserialization() {
    byte[] headerBytes = createRandomValidHeaderByteArray();
    headerBytes[22] = 127;

    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(headerBytes));
    assertEquals("Invalid header: Unsupported encoding type: 127", deserializeException.getMessage());
  }

  @Test
  public void invalidCompressionTypeDeserialization() {
    byte[] headerBytes = createRandomValidHeaderByteArray();
    headerBytes[23] = 127;

    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(headerBytes));
    assertEquals("Invalid header: Unsupported compression type: 127", deserializeException.getMessage());
  }

  @Test
  public void invalidPayloadSizeDeserialization() {
    byte[] headerBytes = createRandomValidHeaderByteArray();
    headerBytes[40] = 0;
    headerBytes[41] = 0;
    headerBytes[42] = 0;
    headerBytes[43] = 0;

    DeserializeException deserializeException = assertThrows(DeserializeException.class,
        () -> BinaryMessageHeader.fromByteArray(headerBytes));
    assertEquals("Invalid header: invalid payload size", deserializeException.getMessage());
  }

  private byte[] createHeaderByteArray(MessageType messageType, UUID schemaId,
      EncodingType encodingType, CompressionType compressionType, UUID sourceId, int payloadSize) {
    final ByteBuffer expectedBuffer = ByteBuffer.wrap(new byte[44]);
    expectedBuffer.putInt(0xA5A5A5A5);
    expectedBuffer.put((byte) 1);
    expectedBuffer.put(messageType.byteIndicator());
    expectedBuffer.putLong(schemaId.getMostSignificantBits());
    expectedBuffer.putLong(schemaId.getLeastSignificantBits());
    expectedBuffer.put(encodingType.byteIndicator());
    expectedBuffer.put(compressionType.byteIndicator());
    expectedBuffer.putLong(sourceId.getMostSignificantBits());
    expectedBuffer.putLong(sourceId.getLeastSignificantBits());
    expectedBuffer.putInt(payloadSize);
    return expectedBuffer.array();
  }

  private byte[] createRandomValidHeaderByteArray() {
    return createHeaderByteArray(MessageType.COMMAND, UUID.randomUUID(), EncodingType.JSON,
        CompressionType.GZIP, UUID.randomUUID(), 123);
  }

}