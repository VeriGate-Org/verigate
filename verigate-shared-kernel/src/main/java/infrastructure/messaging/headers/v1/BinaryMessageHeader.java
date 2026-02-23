package infrastructure.messaging.headers.v1;

import crosscutting.util.ParameterValidation;
import domain.exceptions.DeserializeException;
import infrastructure.messaging.headers.CompressionType;
import infrastructure.messaging.headers.EncodingType;
import infrastructure.messaging.headers.Header;
import infrastructure.messaging.headers.MessageType;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * V1 of the binary message header.
 *
 * <p>
 * The implementation conforms to:
 * <a href="https://verigate.atlassian.net/wiki/spaces/">
 * </a>
 * </p>
 */
public record BinaryMessageHeader(MessageType messageType,
                                  UUID schemaId,
                                  EncodingType encodingType,
                                  CompressionType compressionType,
                                  UUID sourceId,
                                  int payloadSize) implements Header {

  /**
   * Fixed constant to identify the message.
   */
  private static final int MAGIC_NUMBER = 0xA5A5A5A5;

  /**
   * This implementation currently supports version 1 of the encoding scheme.
   */
  private static final byte VERSION_1 = 0x01;

  private static final int MAGIC_NUMBER_BYTE_COUNT = 4;
  private static final int VERSION_BYTE_COUNT = 1;
  private static final int MESSAGE_TYPE_BYTE_COUNT = 1;
  private static final int SCHEMA_IDENTIFIER_BYTE_COUNT = 16;
  private static final int ENCODING_TYPE_BYTE_COUNT = 1;
  private static final int COMPRESSION_TYPE_BYTE_COUNT = 1;
  private static final int SOURCE_IDENTIFIER_BYTE_COUNT = 16;
  private static final int PAYLOAD_SIZE_BYTE_COUNT = 4;
  private static final int HEADER_SIZE =
      MAGIC_NUMBER_BYTE_COUNT
          + VERSION_BYTE_COUNT
          + MESSAGE_TYPE_BYTE_COUNT
          + SCHEMA_IDENTIFIER_BYTE_COUNT
          + ENCODING_TYPE_BYTE_COUNT
          + COMPRESSION_TYPE_BYTE_COUNT
          + SOURCE_IDENTIFIER_BYTE_COUNT
          + PAYLOAD_SIZE_BYTE_COUNT;

  /**
   * Parameter validation on construction.
   */
  public BinaryMessageHeader {
    ParameterValidation.requireNonNull(messageType, "MessageType");
    ParameterValidation.requireNonNull(schemaId, "SchemaId");
    ParameterValidation.requireNonNull(encodingType, "EncodingType");
    ParameterValidation.requireNonNull(compressionType, "CompressionType");
    ParameterValidation.requireNonNull(sourceId, "SourceId");
    if (payloadSize <= 0) {
      throw new IllegalArgumentException("Payload size must be greater than 0");
    }
  }

  @Override
  public int headerSize() {
    return HEADER_SIZE;
  }

  @Override
  public byte[] toByteArray() {
    final ByteBuffer headerBuffer = ByteBuffer.wrap(new byte[HEADER_SIZE]);
    headerBuffer.putInt(MAGIC_NUMBER);
    headerBuffer.put(VERSION_1);
    headerBuffer.put(messageType.byteIndicator());
    headerBuffer.putLong(schemaId.getMostSignificantBits()); // 8 bytes
    headerBuffer.putLong(schemaId.getLeastSignificantBits()); // 8 bytes
    headerBuffer.put(encodingType.byteIndicator());
    headerBuffer.put(compressionType.byteIndicator());
    headerBuffer.putLong(sourceId.getMostSignificantBits()); // 8 bytes
    headerBuffer.putLong(sourceId.getLeastSignificantBits()); // 8 bytes
    headerBuffer.putInt(payloadSize);
    return headerBuffer.array();
  }

  /**
   * Parse a header from the supplied byte array.
   *
   * @param headerBytes Raw header bytes.
   * @return The deserialized header.
   * @throws DeserializeException If deserialization failed.
   */
  public static Header fromByteArray(byte[] headerBytes) throws DeserializeException {
    validateHasBytes(headerBytes);
    final ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
    parseMagicNumber(headerBuffer);
    parseVersion(headerBuffer);
    final MessageType messageType = parseMessageType(headerBuffer);
    final UUID schemaId = parseSchemaId(headerBuffer);
    final EncodingType encodingType = parseEncodingType(headerBuffer);
    final CompressionType compressionType = parseCompressionType(headerBuffer);
    final UUID sourceId = parseSourceId(headerBuffer);
    final int payloadSize = parsePayloadSize(headerBuffer);
    return new BinaryMessageHeader(messageType, schemaId, encodingType, compressionType, sourceId,
        payloadSize);
  }

  /**
   * Checks that the byte array is not null, empty, and has at least enough bytes needed for the
   * headers.
   *
   * @throws DeserializeException if the byte array is null, empty, or does not have enough bytes
   */
  private static void validateHasBytes(byte[] bytes) throws DeserializeException {
    if (bytes == null) {
      throw new DeserializeException(null, "Null byte array");
    }
    if (bytes.length == 0) {
      throw new DeserializeException(bytes, "Empty byte array");
    }
    if (bytes.length < HEADER_SIZE) {
      throw new DeserializeException(bytes, "Invalid header: Insufficient header bytes");
    }
  }

  private static void parseMagicNumber(ByteBuffer headerBuffer) {
    final int magicNumber = headerBuffer.getInt();
    if (magicNumber != MAGIC_NUMBER) {
      throw new DeserializeException(headerBuffer.array(),
          "Invalid header: Expected magic number not found");
    }
  }

  private static void parseVersion(ByteBuffer headerBuffer) {
    final byte version = headerBuffer.get();
    if (version != VERSION_1) {
      throw new DeserializeException(headerBuffer.array(),
          "Invalid header: Received version: %s, supporting: %s".formatted(version, VERSION_1));
    }
  }

  private static MessageType parseMessageType(ByteBuffer headerBuffer) {
    final byte messageTypeIndicator = headerBuffer.get();
    return MessageType.fromByteIndicator(messageTypeIndicator).orElseThrow(
        () -> new DeserializeException(headerBuffer.array(),
            "Invalid header: Unsupported message type: %s".formatted(messageTypeIndicator)));
  }

  private static UUID parseSchemaId(ByteBuffer headerBuffer) {
    return new UUID(headerBuffer.getLong(), headerBuffer.getLong());
  }

  private static EncodingType parseEncodingType(ByteBuffer headerBuffer) {
    final byte encodingTypeIndicator = headerBuffer.get();
    return EncodingType.fromByteIndicator(encodingTypeIndicator).orElseThrow(
        () -> new DeserializeException(headerBuffer.array(),
            "Invalid header: Unsupported encoding type: %s".formatted(encodingTypeIndicator)));
  }

  private static CompressionType parseCompressionType(ByteBuffer headerBuffer) {
    final byte compressionTypeIndicator = headerBuffer.get();
    return CompressionType.fromByteIndicator(compressionTypeIndicator).orElseThrow(
        () -> new DeserializeException(headerBuffer.array(),
            "Invalid header: Unsupported compression type: %s".formatted(
                compressionTypeIndicator)));
  }

  private static UUID parseSourceId(ByteBuffer headerBuffer) {
    return new UUID(headerBuffer.getLong(), headerBuffer.getLong());
  }

  private static int parsePayloadSize(ByteBuffer headerBuffer) {
    final int payloadSize = headerBuffer.getInt();
    if (payloadSize <= 0) {
      throw new DeserializeException(headerBuffer.array(), "Invalid header: invalid payload size");
    }
    return payloadSize;
  }

}
