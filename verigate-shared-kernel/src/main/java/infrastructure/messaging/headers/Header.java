package infrastructure.messaging.headers;

import java.util.UUID;

/**
 * Base interface for message headers.
 */
public interface Header {

  int headerSize();

  EncodingType encodingType();

  CompressionType compressionType();

  UUID schemaId();

  int payloadSize();

  byte[] toByteArray();
}
