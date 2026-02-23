package infrastructure.messaging.headers;

import domain.exceptions.DeserializeException;
import java.util.UUID;

/**
 * Factory for constructing message headers.
 */
public interface HeaderFactory {

  Header newHeader(MessageType messageType, UUID schemaId, EncodingType encodingType,
      CompressionType compressionType, UUID sourceId, int payloadSize);

  Header parseHeader(byte[] headerBytes) throws DeserializeException;

}
